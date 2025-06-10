from zoneinfo import ZoneInfo
from datetime import datetime, timezone


def replace_registered_date(prompt):
    """
    替换提示词中的 [registered_date] 为当前日期 (YYYY-MM-DD) 使用 CST (UTC+8)。

    Args:
        prompt (str): 原始提示词，包含 [registered_date] 占位符

    Returns:
        str: 替换 [registered_date] 后的提示词
    """
    cst_tz = ZoneInfo("Asia/Shanghai")
    current_date = datetime.now(tz=cst_tz).strftime("%Y-%m-%d")
    updated_prompt = prompt.replace("[registered_date]", current_date)
    return updated_prompt
