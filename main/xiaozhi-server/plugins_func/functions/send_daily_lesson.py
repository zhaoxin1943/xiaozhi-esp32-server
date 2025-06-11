from plugins_func.register import register_function, ActionResponse, Action, ToolType

send_daily_lesson_tool = {
    "type": "function",
    "function": {
        "name": "send_daily_lesson",
        "description": "当用户明确同意收听当天的英语节目时，调用此函数。后端将处理音频的下发。",
        "parameters": {
            "type": "object",
            "properties": {},
            "required": []
        }
    }
}


@register_function(name="send_daily_lesson", desc=send_daily_lesson_tool, type=ToolType.WAIT)
def send_daily_lesson():
    print('-------call send_daily_lesson--------')
