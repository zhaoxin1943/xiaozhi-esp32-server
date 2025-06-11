package xiaozhi.modules.student.dao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import xiaozhi.modules.student.entity.DailyLessonEntity;
import xiaozhi.modules.student.entity.StudentInfoEntity;

import java.util.List;

@Data
@Schema(description = "包含未学习课程的学生详细信息传输对象")
public class StudentWithLessonsDto {

    @Schema(description = "学生基本信息")
    private StudentInfoEntity studentInfo;

    @Schema(description = "当天未学习的课程列表")
    private List<DailyLessonEntity> uncompletedLessons;
}