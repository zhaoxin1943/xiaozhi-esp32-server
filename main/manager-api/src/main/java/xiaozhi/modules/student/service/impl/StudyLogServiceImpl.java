package xiaozhi.modules.student.service.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import xiaozhi.common.service.impl.BaseServiceImpl;
import xiaozhi.modules.student.dao.DailyLessonDao;
import xiaozhi.modules.student.dao.StudyLogDao;
import xiaozhi.modules.student.entity.DailyLessonEntity;
import xiaozhi.modules.student.entity.StudyLogEntity;
import xiaozhi.modules.student.service.StudyLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.Date;
import java.util.List;
import org.springframework.util.CollectionUtils;
import java.util.Collections;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class StudyLogServiceImpl extends BaseServiceImpl<StudyLogDao, StudyLogEntity> implements StudyLogService {

    private final DailyLessonDao dailyLessonDao;
    private final StudyLogDao studyLogDao;
    private static final Logger logger = LoggerFactory.getLogger(StudyLogServiceImpl.class);

    @Override
    public List<DailyLessonEntity> getCompletedLessons(String studentId, Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date startOfDay = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<StudyLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.ge("completion_date", startOfDay);
        queryWrapper.lt("completion_date", endOfDay);

        List<StudyLogEntity> studyLogs = studyLogDao.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(studyLogs)) {
            return Collections.emptyList();
        }

        List<String> completedLessonIds = studyLogs.stream()
                .map(StudyLogEntity::getLessonId)
                .distinct()
                .collect(Collectors.toList());

        return dailyLessonDao.selectBatchIds(completedLessonIds);
    }

    @Override
    public List<DailyLessonEntity> getUncompletedLessons(String studentId, Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date startOfDay = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<StudyLogEntity> completedQuery = new QueryWrapper<>();
        completedQuery.select("lesson_id")
                .eq("student_id", studentId);
        completedQuery.ge("completion_date", startOfDay);
        completedQuery.lt("completion_date", endOfDay);

        List<String> completedLessonIds = studyLogDao.selectList(completedQuery).stream()
                .map(StudyLogEntity::getLessonId)
                .distinct()
                .collect(Collectors.toList());

        QueryWrapper<DailyLessonEntity> uncompletedQuery = new QueryWrapper<>();
        uncompletedQuery.ge("publish_date", startOfDay);
        uncompletedQuery.lt("publish_date", endOfDay);

        if (!CollectionUtils.isEmpty(completedLessonIds)) {
            uncompletedQuery.notIn("id", completedLessonIds);
        }

        return dailyLessonDao.selectList(uncompletedQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordLessonCompletion(String studentId, String lessonId) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        Date startOfToday = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfTomorrow = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<StudyLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId)
                .eq("lesson_id", lessonId);
        queryWrapper.ge("completion_date", startOfToday);
        queryWrapper.lt("completion_date", endOfTomorrow);


        Long count = studyLogDao.selectCount(queryWrapper);
        if (count > 0) {
            logger.warn("学生(ID:{}) 今天已完成过课程(ID:{})，无需重复记录。", studentId, lessonId);
            return;
        }

        StudyLogEntity studyLog = new StudyLogEntity();
        studyLog.setStudentId(studentId);
        studyLog.setLessonId(lessonId);
        studyLog.setCompletionDate(new Date());

        this.insert(studyLog);
    }
}
