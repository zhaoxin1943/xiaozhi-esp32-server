from typing import Optional, Dict

from config.manage_api_client import ManageApiClient


async def get_student_info(device_id) -> Optional[Dict]:
    """获取学生信息"""
    return await ManageApiClient._instance._execute_request("GET", f"/student/{device_id}")

async def get_student_info_with_lessons(device_id) -> Optional[Dict]:
    """获取学生信息以及今日未收听的节目"""
    return await ManageApiClient._instance._execute_request("GET", f"/student/{device_id}/with-lessons")


async def get_device_chat_history(device_id: str) -> Optional[Dict]:
    """获取设备维度的聊天记录"""
    return await ManageApiClient._instance._execute_request("GET", f"/agent/chat-history-by-device/{device_id}")


async def update_student_info_by_deviceId(device_id: str, field_name: str, field_value: str):
    # 1. 定义接口的固定路径
    endpoint = "/student/updateStudentInfo"

    # 2. 将要发送的参数打包成一个字典
    payload = {
        "deviceId": device_id,
        "fieldName": field_name,
        "fieldValue": field_value
    }

    # 3. 通过 data 参数将 payload 作为请求体发送
    return await ManageApiClient._instance._execute_request("POST",
                                                            endpoint,
                                                            data=payload)


async def get_enter_student_info_llm():
    return await ManageApiClient._instance._execute_request("GET", f"/agent/llm/CollectingInfo")
