package xiaozhi.modules.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import xiaozhi.common.service.impl.BaseServiceImpl;
import xiaozhi.modules.student.dao.StudentDao;
import xiaozhi.modules.student.entity.StudentInfoEntity;
import xiaozhi.modules.student.service.StudentInfoService;

@Service
@AllArgsConstructor
public class StudentInfoServiceImpl extends BaseServiceImpl<StudentDao, StudentInfoEntity> implements StudentInfoService {

    private final StudentDao studentDao;

    @Override
    public StudentInfoEntity getStudentByDeviceId(String deviceId) {
        QueryWrapper<StudentInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_id", deviceId);
        return studentDao.selectOne(queryWrapper);
    }
}
