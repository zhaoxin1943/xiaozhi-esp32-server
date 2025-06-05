import os
import time
import base64
from typing import Optional, Dict
import asyncio
import httpx

TAG = __name__


class DeviceNotFoundException(Exception):
    pass


class DeviceBindException(Exception):
    def __init__(self, bind_code):
        self.bind_code = bind_code
        super().__init__(f"设备绑定异常，绑定码: {bind_code}")


class ManageApiClient:
    _instance = None
    _client = None  # 存储 httpx.AsyncClient 实例
    _secret = None
    _init_task = None # 用于确保 _init_client 只被执行一次的 Task

    def __new__(cls):
        """单例模式确保全局唯一实例，并支持传入配置参数"""
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            # 在 __new__ 中启动一个异步初始化任务，但它不能直接是 await
        return cls._instance

    @classmethod
    async def initialize(cls, config):
        if cls._instance is None:
            cls._instance = cls.__new__(cls)

        if cls._init_task is None:
            cls._init_task = asyncio.create_task(cls._init_client(config))
        await cls._init_task

    @classmethod
    async def _init_client(cls, config):
        """异步初始化持久化连接池"""
        cls.config = config.get("manager-api")

        if not cls.config:
            raise Exception("manager-api配置错误")

        if not cls.config.get("url") or not cls.config.get("secret"):
            raise Exception("manager-api的url或secret配置错误")

        if "你" in cls.config.get("secret"):
            raise Exception("请先配置manager-api的secret")

        cls._secret = cls.config.get("secret")
        cls.max_retries = cls.config.get("max_retries", 6)  # 最大重试次数
        cls.retry_delay = cls.config.get("retry_delay", 10)  # 初始重试延迟(秒)
        # NOTE(goody): 2025/4/16 http相关资源统一管理，后续可以增加线程池或者超时
        # 后续也可以统一配置apiToken之类的走通用的Auth
        # 核心修改：使用 httpx.AsyncClient
        cls._client = httpx.AsyncClient( # 修改为 httpx.AsyncClient
            base_url=cls.config.get("url"),
            headers={
                "User-Agent": f"PythonClient/2.0 (PID:{os.getpid()})",
                "Accept": "application/json",
                "Authorization": "Bearer " + cls._secret,
            },
            timeout=cls.config.get("timeout", 30),  # 默认超时时间30秒
        )

    @classmethod
    async def _request(cls, method: str, endpoint: str, **kwargs) -> Dict:
        """发送单次HTTP请求并处理响应"""
        endpoint = endpoint.lstrip("/")
        # 核心修改：使用 await 调用异步方法
        response = await cls._client.request(method, endpoint, **kwargs)
        response.raise_for_status()

        result = response.json()

        # 处理API返回的业务错误
        if result.get("code") == 10041:
            raise DeviceNotFoundException(result.get("msg"))
        elif result.get("code") == 10042:
            raise DeviceBindException(result.get("msg"))
        elif result.get("code") != 0:
            raise Exception(f"API返回错误: {result.get('msg', '未知错误')}")

        # 返回成功数据
        return result.get("data") if result.get("code") == 0 else None

    @classmethod
    def _should_retry(cls, exception: Exception) -> bool:
        """判断异常是否应该重试"""
        # 网络连接相关错误
        if isinstance(
            exception, (httpx.ConnectError, httpx.TimeoutException, httpx.NetworkError)
        ):
            return True

        # HTTP状态码错误
        if isinstance(exception, httpx.HTTPStatusError):
            status_code = exception.response.status_code
            return status_code in [408, 429, 500, 502, 503, 504]

        return False

    @classmethod
    async def _execute_request(cls, method: str, endpoint: str, **kwargs) -> Dict:
        """带重试机制的请求执行器"""
        retry_count = 0

        while retry_count <= cls.max_retries:
            try:
                # 核心修改：使用 await 调用异步方法
                return await cls._request(method, endpoint, **kwargs)
            except Exception as e:
                # 判断是否应该重试
                if retry_count < cls.max_retries and cls._should_retry(e):
                    retry_count += 1
                    print(
                        f"{method} {endpoint} 请求失败，将在 {cls.retry_delay:.1f} 秒后进行第 {retry_count} 次重试"
                    )
                    # 核心修改：使用 await asyncio.sleep 代替 time.sleep
                    await asyncio.sleep(cls.retry_delay)
                    continue
                else:
                    # 不重试，直接抛出异常
                    raise

    @classmethod
    async def safe_close(cls):
        """安全关闭连接池"""
        if cls._client:
            await cls._client.aclose()
            cls._instance = None
            cls._init_task = None

async def get_server_config() -> Optional[Dict]:
    """获取服务器基础配置"""
    return await ManageApiClient._instance._execute_request("POST", "/config/server-base")


async def get_agent_models(
    mac_address: str, client_id: str, selected_module: Dict
) -> Optional[Dict]:
    """获取代理模型配置"""
    return await ManageApiClient._instance._execute_request(
        "POST",
        "/config/agent-models",
        json={
            "macAddress": mac_address,
            "clientId": client_id,
            "selectedModule": selected_module,
        },
    )


async def save_mem_local_short(mac_address: str, short_momery: str) -> Optional[Dict]:
    try:
        return await ManageApiClient._instance._execute_request(
            "PUT",
            f"/agent/saveMemory/" + mac_address,
            json={
                "summaryMemory": short_momery,
            },
        )
    except Exception as e:
        print(f"存储短期记忆到服务器失败: {e}")
        return None


async def report(
    mac_address: str, session_id: str, chat_type: int, content: str, audio, report_time
) -> Optional[Dict]:
    """带熔断的业务方法示例"""
    if not content or not ManageApiClient._instance:
        return None
    try:
        return await ManageApiClient._instance._execute_request(
            "POST",
            f"/agent/chat-history/report",
            json={
                "macAddress": mac_address,
                "sessionId": session_id,
                "chatType": chat_type,
                "content": content,
                "reportTime": report_time,
                "audioBase64": (
                    base64.b64encode(audio).decode("utf-8") if audio else None
                ),
            },
        )
    except Exception as e:
        print(f"TTS上报失败: {e}")
        return None


async def init_service(config):
    await ManageApiClient.initialize(config)


async def manage_api_http_safe_close():
    await ManageApiClient.safe_close()
