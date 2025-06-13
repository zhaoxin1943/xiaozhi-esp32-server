package xiaozhi.modules.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank; // 推荐使用 jakarta.validation 标准
import lombok.Data;

@Data
@Schema(description = "记录课程完成情况的请求对象")
public class LessonCompletionRequestDto {

    @NotBlank(message = "设备ID不能为空")
    @Schema(description = "学生的设备ID", required = true, example = "device-abc-123")
    private String deviceId;
}