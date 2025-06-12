import asyncio
import aiohttp
from config.logger import setup_logging
from plugins_func.register import register_function, ActionResponse, Action, ToolType
import os
from urllib.parse import urlparse
from core.utils.dialogue import Message
from core.providers.tts.dto.dto import TTSMessageDTO, SentenceType, ContentType

TAG = __name__

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
    logger.bind(tag=TAG).info(f"handle_daily_lesson_response,is_agree={is_agree}")
    if is_agree:
        if not conn.loop.is_running():
            logger.bind(tag=TAG).error("事件循环未运行，无法提交任务")
            return ActionResponse(
                action=Action.RESPONSE, result="系统繁忙", response="请稍后再试"
            )
        audio_url = conn.uncompleted_lessons[0]['audioUrl']
        lesson_name = conn.uncompleted_lessons[0]['lessonName']
        future = asyncio.run_coroutine_threadsafe(
            play_remote_music(conn, audio_url, lesson_name), conn.loop
        )

        def handle_done(f):
            try:
                audio_path = f.result()
                conn.logger.bind(tag=TAG).info("播放课程完成")
                # if audio_path and os.path.exists(audio_path):
                #     try:
                #         os.remove(audio_path)
                #         conn.logger.bind(tag=TAG).info(f"已删除音频文件: {audio_path}")
                #     except Exception as e:
                #         conn.logger.bind(tag=TAG).error(f"删除音频文件失败: {e}")
            except Exception as e:
                conn.logger.bind(tag=TAG).error(f"播放课程失败: {e}")

        future.add_done_callback(handle_done)
        return ActionResponse(
            action=Action.NONE, result="指令已接收", response=None
        )
    else:
        return ActionResponse(action=Action.REQLLM, result="指令已接收", response=None)


async def download_lesson_audio(audio_url, save_dir='.lessons'):
    async with aiohttp.ClientSession() as session:
        try:
            filename = os.path.basename(urlparse(audio_url).path)
            filepath = os.path.join(save_dir, filename)
            os.makedirs(save_dir, exist_ok=True)
            async with session.get(audio_url) as response:
                if response.status == 200:
                    with open(filepath, 'wb') as f:
                        while True:
                            chunk = await response.content.read(1024 * 1024)  # 1MB 缓冲
                            if not chunk:
                                break
                            f.write(chunk)
                    return filepath
                else:
                    raise Exception(f"下载失败，状态码: {response.status}")
        except Exception as e:
            raise Exception(f"下载 {audio_url} 时出错: {str(e)}")


async def play_remote_music(conn, audio_url: str, lesson_name: str):
    logger = setup_logging()
    try:
        audio_path = await download_lesson_audio(audio_url)
        start_text = f"Great! Let's listen to today's program {lesson_name}. "
        conn.dialogue.put(Message(role="assistant", content=start_text))
        end_text = "And that's the program for today. So, what are you thinking about now?"
        conn.dialogue.put(Message(role="assistant", content=end_text))

        conn.tts.tts_text_queue.put(
            TTSMessageDTO(
                sentence_id=conn.sentence_id,
                sentence_type=SentenceType.FIRST,
                content_type=ContentType.ACTION,
            )
        )

        conn.tts.tts_text_queue.put(
            TTSMessageDTO(
                sentence_id=conn.sentence_id,
                sentence_type=SentenceType.MIDDLE,
                content_type=ContentType.TEXT,
                content_detail=start_text,
            )
        )

        conn.tts.tts_text_queue.put(
            TTSMessageDTO(
                sentence_id=conn.sentence_id,
                sentence_type=SentenceType.MIDDLE,
                content_type=ContentType.FILE,
                content_file=audio_path,
            )
        )

        conn.tts.tts_text_queue.put(
            TTSMessageDTO(
                sentence_id=conn.sentence_id,
                sentence_type=SentenceType.MIDDLE,
                content_type=ContentType.TEXT,
                content_detail=end_text,
            )
        )

        conn.tts.tts_text_queue.put(
            TTSMessageDTO(
                sentence_id=conn.sentence_id,
                sentence_type=SentenceType.LAST,
                content_type=ContentType.ACTION,
            )
        )

        return audio_path

    except Exception as e:
        logger.bind(tag=TAG).error(f"play_remote_music error: {str(e)}")
        return None
