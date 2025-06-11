package xiaozhi.modules.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank; // 推荐使用 jakarta.validation 标准
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

@Data
@Schema(description = "每日课程保存或更新的数据传输对象")
public class DailyLessonSaveDTO {

    @Schema(description = "课程ID (更新时必须提供)")
    private String id;

    @NotBlank(message = "课程名称不能为空")
    @Schema(description = "课程名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lessonName;

    @NotNull(message = "课程时长不能为空")
    @PositiveOrZero(message = "课程时长必须为非负数")
    @Schema(description = "课程时长 (秒)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer lessonDuration;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull(message = "课程发布时间不能为空")
    @Schema(description = "课程发布时间，格式 yyyy-MM-dd", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date publishDate;

    @NotBlank(message = "音频链接不能为空") // 修改为 @NotBlank
    @URL(message = "必须是有效的URL格式")
    @Schema(description = "课程音频url", requiredMode = Schema.RequiredMode.REQUIRED)
    private String audioUrl;
}