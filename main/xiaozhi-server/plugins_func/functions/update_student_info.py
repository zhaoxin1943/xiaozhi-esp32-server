from custom.update_student_info import update_student_info_to_db
from plugins_func.register import register_function, ActionResponse, Action, ToolType

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
                    "enum": ["nickname", "gender", "birth_date"],
                    "description": "The field to update: 'nickname', 'gender', or 'birth_date'."
                },
                "field_value": {
                    "type": "string",
                    "description": "The value for the specified field. For 'gender', must be 'boy' or 'girl'. For 'birth_date', must be in 'YYYY-MM' format (e.g., '2003-05'). For 'nickname', must be a non-empty string."
                }
            },
            "required": ["field_name", "field_value"],
            "additionalProperties": False
        }
    }
}


@register_function(name="update_student_info", desc=update_student_info_tool, type=ToolType.WAIT)
def update_student_info(field_name, field_value):
    update_student_info_to_db(field_name, field_value)
    return ActionResponse(Action.REQLLM, '已更新', None)
