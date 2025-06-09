from custom.update_student_info import update_student_info_to_db
from plugins_func.register import register_function, ActionResponse, Action, ToolType
import re

pattern = r'(.+?)__([^_\n]+)($|__)'

update_student_info_tool = {
    "type": "function",
    "function": {
        "name": "update_student_info",
        "description": "Update the student's information in the database with the provided field name and value.",
        "parameters": {
            "type": "object",
            "properties": {
                "field_name": {
                    "type": "string",
                    "enum": ["nick_name", "gender", "birth_date"],
                    "description": "The field to update: 'nick_name', 'gender', or 'birth_date'."
                },
                "field_value": {
                    "type": "string",
                    "description": "The value for the specified field. For 'gender', must be 'boy' or 'girl'. For 'birth_date', must be in 'YYYY-MM' format (e.g., '2003-05'). For 'nick_name', must be a non-empty string."
                }
            },
            "required": ["field_name", "field_value"],
            "additionalProperties": False
        }
    }
}


@register_function(name="update_student_info", desc=update_student_info_tool, type=ToolType.WAIT)
def update_student_info(field_name, field_value):
    match = re.search(pattern, field_value)
    if match:
        real_field_value = match.group(1)
        device_id = match.group(2)
        update_student_info_to_db(device_id, field_name, real_field_value)
        return ActionResponse(Action.REQLLM, f"已更新{field_name}", None)
    else:
        return ActionResponse(Action.REQLLM, f"未成功更新{field_name}，请继续提问", None)
