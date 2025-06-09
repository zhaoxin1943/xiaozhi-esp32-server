import asyncio

from config.logger import setup_logging
from custom.request import update_student_info_by_deviceId


def update_student_info_to_db(device_id: str, field_name: str, field_value: str):
    logger = setup_logging()
    logger.info(f"----update_student_info: {field_name}={field_value} for {device_id}----")
    asyncio.run(update_student_info_by_deviceId(device_id, field_name, field_value))
