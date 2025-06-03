package xiaozhi.modules.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xiaozhi.modules.student.enums.LearningStyle;
import xiaozhi.modules.student.enums.StudentGender;

import java.util.Date;

/**
 * 学生信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("student_info")
@Schema(description = "学生信息")
public class StudentInfoEntity extends BaseEntity {

    /**
     * 每个学生对应一台设备
     */
    @Schema(description = "设备ID")
    private String deviceId;

    @Schema(description = "学生昵称")
    private String nickName;

    @Schema(description = "学生真实名字")
    private String realName;

    @Schema(description = "出生年月")
    private Date birthDate;

    @Schema(description = "学生性别")
    private StudentGender gender;

    /**
     * 年级保留
     */
    @Schema(description = "就读年级")
    private Integer schoolGrade;

    @Schema(description = "口语能力自评，1-10分")
    private Integer selfAssessment;

    @Schema(description = "最后活跃时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date lastActive;

    @Schema(description = "学习风格")
    private LearningStyle learningStyle;

    @Schema(description = "家长是否完成验证，COPPA合规")
    private Boolean isParentVerified;

}
