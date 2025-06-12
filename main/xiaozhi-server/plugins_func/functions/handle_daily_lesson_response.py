from config.logger import setup_logging
from plugins_func.register import register_function, ActionResponse, Action, ToolType

handle_daily_lesson_response_tool = {
    "type": "function",
    "function": {
        "name": "handle_daily_lesson_response",
        "description": "当用户对“每日节目”的收听邀请做出明确的“同意”或“拒绝”的回应后，必须调用此函数来通知后端系统。",
        "parameters": {
            "type": "object",
            "properties": {
                "is_agree": {
                    "type": "boolean",
                    "description": "用于表示用户的明确意图。如果用户同意收听，则设置为true；如果用户拒绝收听，则设置为false。"
                }
            },
            "required": ["is_agree"]
        }
    }
}


@register_function(name="handle_daily_lesson_response", desc=handle_daily_lesson_response_tool,
                   type=ToolType.WAIT_WITH_CONN)
def handle_daily_lesson_response(conn, is_agree: bool):
    logger = setup_logging()
    logger.info(f"handle_daily_lesson_response({conn.uncompleted_lessons[0]}),is_agree={is_agree}")
