package xiaozhi.modules.student.service;

import xiaozhi.common.service.BaseService;
import xiaozhi.modules.student.entity.DailyLessonEntity;

import java.util.Date;

public interface DailyLessonService extends BaseService<DailyLessonEntity> {
    /**
     * 查询date所指当天的lesson，如果有多条，则返回最新的那一条
     *
     * @param date lesson发布日志
     * @return lesson
     */
    DailyLessonEntity getDailyLessonByDate(Date date);

    /**
     * 添加每日课程
     *
     * @param dailyLesson 课程
     */
    void addDailyLesson(DailyLessonEntity dailyLesson);
}
