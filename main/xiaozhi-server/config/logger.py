import os
import sys
from loguru import logger
from config.config_loader import load_config
from config.settings import check_config_file

SERVER_VERSION = "0.5.2"
_logger_initialized = False
_global_logger_instance = None


def get_module_abbreviation(module_name, module_dict):
    """获取模块名称的缩写，如果为空则返回00
    如果名称中包含下划线，则返回下划线后面的前两个字符
    """
    module_value = module_dict.get(module_name, "")
    if not module_value:
        return "00"
    if "_" in module_value:
        parts = module_value.split("_")
        return parts[-1][:2] if parts[-1] else "00"
    return module_value[:2]


def build_module_string(selected_module):
    """构建模块字符串"""
    return (
            get_module_abbreviation("VAD", selected_module)
            + get_module_abbreviation("ASR", selected_module)
            + get_module_abbreviation("LLM", selected_module)
            + get_module_abbreviation("TTS", selected_module)
            + get_module_abbreviation("Memory", selected_module)
            + get_module_abbreviation("Intent", selected_module)
    )


def formatter(record):
    """为没有 tag 的日志添加默认值"""
    record["extra"].setdefault("tag", record["name"])
    return record["message"]


async def async_setup_logging():
    global _logger_initialized, _global_logger_instance

    if _logger_initialized:
        return

    await check_config_file()
    """从配置文件中读取日志配置，并设置日志输出格式和级别"""
    config = await load_config()
    log_config = config["log"]

    logger.configure(
        extra={
            "selected_module": log_config.get("selected_module", "00000000000000")
        }
    )  # 新增配置
    log_format = log_config.get(
        "log_format",
        "<green>{time:YYMMDD HH:mm:ss}</green>[{version}_{extra[selected_module]}][<light-blue>{extra[tag]}</light-blue>]-<level>{level}</level>-<light-green>{message}</light-green>",
    )
    log_format_file = log_config.get(
        "log_format_file",
        "{time:YYYY-MM-DD HH:mm:ss} - {version_{extra[selected_module]}} - {name} - {level} - {extra[tag]} - {message}",
    )
    selected_module_str = logger._core.extra["selected_module"]

    log_format = log_format.replace("{version}", SERVER_VERSION)
    log_format = log_format.replace("{selected_module}", selected_module_str)
    log_format_file = log_format_file.replace("{version}", SERVER_VERSION)
    log_format_file = log_format_file.replace(
        "{selected_module}", selected_module_str
    )

    log_level = log_config.get("log_level", "INFO")
    log_dir = log_config.get("log_dir", "tmp")
    log_file = log_config.get("log_file", "server.log")
    data_dir = log_config.get("data_dir", "data")

    os.makedirs(log_dir, exist_ok=True)
    os.makedirs(data_dir, exist_ok=True)

    # 配置日志输出
    logger.remove()

    # 输出到控制台
    logger.add(sys.stdout, format=log_format, level=log_level, filter=formatter)

    # 输出到文件
    logger.add(
        os.path.join(log_dir, log_file),
        format=log_format_file,
        level=log_level,
        filter=formatter,
    )
    _global_logger_instance = logger
    _logger_initialized = True

def setup_logging():
    """
    获取已配置的全局 logger 实例。
    **重要：** 必须在应用程序启动时，通过 `await _configure_global_logger_async()` 先进行异步初始化。
    """
    if _global_logger_instance is None:
        raise RuntimeError(
            "Logger has not been initialized. "
            "Ensure `await _configure_global_logger_async()` is called before accessing the logger."
        )
    return _global_logger_instance


async def update_module_string(selected_module_str):
    """更新模块字符串并重新配置日志处理器"""
    logger.debug(f"更新日志配置组件")
    current_module = logger._core.extra["selected_module"]

    if current_module == selected_module_str:
        return

    try:
        logger.configure(extra={"selected_module": selected_module_str})

        config = await load_config()
        log_config = config["log"]

        log_format = log_config.get(
            "log_format",
            "<green>{time:YYMMDD HH:mm:ss}</green>[{version}_{extra[selected_module]}][<light-blue>{extra[tag]}</light-blue>]-<level>{level}</level>-<light-green>{message}</light-green>",
        )
        log_format_file = log_config.get(
            "log_format_file",
            "{time:YYYY-MM-DD HH:mm:ss} - {version_{extra[selected_module]}} - {name} - {level} - {extra[tag]} - {message}",
        )

        log_format = log_format.replace("{version}", SERVER_VERSION)
        log_format = log_format.replace("{selected_module}", selected_module_str)
        log_format_file = log_format_file.replace("{version}", SERVER_VERSION)
        log_format_file = log_format_file.replace(
            "{selected_module}", selected_module_str
        )

        logger.remove()
        logger.add(
            sys.stdout,
            format=log_format,
            level=log_config.get("log_level", "INFO"),
            filter=formatter,
        )
        logger.add(
            os.path.join(
                log_config.get("log_dir", "tmp"),
                log_config.get("log_file", "server.log"),
            ),
            format=log_format_file,
            level=log_config.get("log_level", "INFO"),
            filter=formatter,
        )

    except Exception as e:
        logger.error(f"日志配置更新失败: {str(e)}")
        raise
