from typing import Optional, Dict

from config.manage_api_client import ManageApiClient


async def get_student_info(device_id) -> Optional[Dict]:
    """获取服务器基础配置"""
    return await ManageApiClient._instance._execute_request("GET", f"/student/{device_id}")


async def get_device_chat_history(device_id:str) -> Optional[Dict]:
    """获取设备维度的聊天记录"""
    return await ManageApiClient._instance._execute_request("GET", f"/agent/chat-history-by-device/{device_id}")
