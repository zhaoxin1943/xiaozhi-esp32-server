from config.logger import setup_logging


def update_student_info_to_db(field_name, field_value):
    logger = setup_logging()
    logger.info(f"----update_student_info: {field_name}={field_value}")