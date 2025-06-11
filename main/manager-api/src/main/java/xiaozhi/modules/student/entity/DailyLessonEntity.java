package xiaozhi.modules.student.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("daily_lesson")
@Schema(description = "每日课程")
public class DailyLessonEntity extends BaseEntity {
    @Schema(description = "课程名称")
    private String lessonName;

    @Schema(description = "课程时长")
    private Integer lessonDuration;

    @Schema(description = "课程索引")
    private Integer lessonIndex;

    @Schema(description = "课程发布时间")
    private Date publishDate;

    @Schema(description = "课程音频url")
    private String audioUrl;
}
