from typing import Optional, Dict

from config.manage_api_client import ManageApiClient


async def get_student_info(device_id) -> Optional[Dict]:
    """获取服务器基础配置"""
    return await ManageApiClient._instance._execute_request("GET", f"/student/{device_id}")