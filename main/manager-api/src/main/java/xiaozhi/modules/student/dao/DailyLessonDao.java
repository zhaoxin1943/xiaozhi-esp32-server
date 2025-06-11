package xiaozhi.modules.student.dao;

import org.apache.ibatis.annotations.Mapper;
import xiaozhi.common.dao.BaseDao;
import xiaozhi.modules.student.entity.DailyLessonEntity;

@Mapper
public interface DailyLessonDao extends BaseDao<DailyLessonEntity> {
}
