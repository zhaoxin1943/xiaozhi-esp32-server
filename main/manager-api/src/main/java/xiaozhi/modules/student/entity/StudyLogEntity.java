package xiaozhi.modules.student.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 学生学习记录实体类
 * <p>
 * 该表为关联表，用于连接学生（student_info）和每日课程（daily_lesson），
 * 记录学生的课程完成情况。
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承了BaseEntity，建议callSuper = true
@Accessors(chain = true)
@TableName("study_log")
@Schema(description = "学生学习记录")
public class StudyLogEntity extends BaseEntity {

    @Schema(description = "学生ID (外键, 关联 student_info.id)")
    private String studentId;

    @Schema(description = "每日课程ID (外键, 关联 daily_lesson.id)")
    private String lessonId;

    @Schema(description = "课程完成日期")
    private Date completionDate;

}