package xiaozhi.modules.student.service;

import xiaozhi.common.service.BaseService;
import xiaozhi.modules.student.entity.DailyLessonEntity;
import xiaozhi.modules.student.entity.StudyLogEntity;

import java.util.Date;
import java.util.List;


public interface StudyLogService extends BaseService<StudyLogEntity> {
    /**
     * 查询学生在某一天已学习的课程列表
     *
     * @param studentId 学生ID
     * @param date      查询日期
     * @return 已学习的课程列表
     */
    List<DailyLessonEntity> getCompletedLessons(String studentId, Date date);

    /**
     * 查询学生在某一天未学习的课程列表
     *
     * @param studentId 学生ID
     * @param date      查询日期
     * @return 未学习的课程列表
     */
    List<DailyLessonEntity> getUncompletedLessons(String studentId, Date date);

    /**
     * 记录一堂课已完成
     *
     * @param studentId 学生ID
     * @param lessonId  课程ID
     */
    void recordLessonCompletion(String studentId, String lessonId);
}
