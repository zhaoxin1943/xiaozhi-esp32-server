package xiaozhi.modules.student.service.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xiaozhi.common.exception.RenException;
import xiaozhi.common.service.impl.BaseServiceImpl;
import xiaozhi.modules.student.dao.DailyLessonDao;
import xiaozhi.modules.student.entity.DailyLessonEntity;
import xiaozhi.modules.student.service.DailyLessonService;

import java.util.List;
import java.util.Objects;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Service
@AllArgsConstructor
public class DailyLessonServiceImpl extends BaseServiceImpl<DailyLessonDao, DailyLessonEntity> implements DailyLessonService {

    private final DailyLessonDao dailyLessonDao;
    private static final Logger logger = LoggerFactory.getLogger(DailyLessonServiceImpl.class);

    @Override
    public DailyLessonEntity getDailyLessonByDate(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date startOfDay = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<DailyLessonEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.ge("publish_date", startOfDay);
        queryWrapper.lt("publish_date", endOfDay);

        queryWrapper.orderByDesc("create_date");

        queryWrapper.last("LIMIT 1");

        return dailyLessonDao.selectOne(queryWrapper);
    }

    @Override
    public void addDailyLesson(DailyLessonEntity dailyLesson) {
        if (Objects.isNull(dailyLesson) || Objects.isNull(dailyLesson.getPublishDate())) {
            logger.warn("课程或其发布日期为空，无法添加。");
            throw new RenException("课程或其发布日期为空，无法添加。");
        }

        // 1. 计算当天的时间范围
        LocalDate localDate = dailyLesson.getPublishDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date startOfDay = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // 2. 查询当天已存在的最大 lessonIndex
        QueryWrapper<DailyLessonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("MAX(lesson_index)");
        queryWrapper.ge("publish_date", startOfDay);
        queryWrapper.lt("publish_date", endOfDay);

        List<Object> resultList = dailyLessonDao.selectObjs(queryWrapper);

        int currentIndex = 0;
        if (!CollectionUtils.isEmpty(resultList) && resultList.getFirst() != null) {
            Object maxIndexObj = resultList.getFirst();
            currentIndex = ((Number) maxIndexObj).intValue();
        }
        // 3. 设置新的 lessonIndex
        dailyLesson.setLessonIndex(currentIndex + 1);
        // 4. 插入数据
        dailyLessonDao.insert(dailyLesson);
    }
}
