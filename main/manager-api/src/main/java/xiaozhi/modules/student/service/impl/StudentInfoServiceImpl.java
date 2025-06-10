package xiaozhi.modules.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;

import xiaozhi.common.service.impl.BaseServiceImpl;
import xiaozhi.modules.student.dao.StudentDao;
import xiaozhi.modules.student.entity.StudentInfoEntity;
import xiaozhi.modules.student.enums.StudentGender;
import xiaozhi.modules.student.service.StudentInfoService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public class StudentInfoServiceImpl extends BaseServiceImpl<StudentDao, StudentInfoEntity> implements StudentInfoService {

    private final StudentDao studentDao;
    private static final Logger logger = LoggerFactory.getLogger(StudentInfoServiceImpl.class);

    @Override
    public StudentInfoEntity getStudentByDeviceId(String deviceId) {
        QueryWrapper<StudentInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_id", deviceId);
        queryWrapper.eq("deleted", 0);
        return studentDao.selectOne(queryWrapper);
    }

    @Override
    public void insertStudent(String deviceId) {
        StudentInfoEntity studentInfoEntity = new StudentInfoEntity();
        studentInfoEntity.setDeviceId(deviceId);
        studentDao.insert(studentInfoEntity);
    }

    @Override
    public void deleteStudentByDeviceId(String deviceId, String tag) {
        QueryWrapper<StudentInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_id", deviceId);
        queryWrapper.eq("deleted", 0);
        StudentInfoEntity studentInfoEntity = studentDao.selectOne(queryWrapper);
        if (studentInfoEntity != null) {
            studentInfoEntity.setDeleted(true);
            studentInfoEntity.setDeviceId(studentInfoEntity.getDeviceId() + tag);
            studentDao.updateById(studentInfoEntity);
        }
    }

    @Override
    public void updateStudentInfoByDeviceId(String deviceId, String fieldName, String fieldValue) {
        // 日志记录
        logger.info("Updating student info: deviceId={}, fieldName={}, fieldValue={}", deviceId, fieldName, fieldValue);

        // 参数校验
        if (deviceId == null || fieldName == null || fieldValue == null) {
            throw new IllegalArgumentException("deviceId, fieldName, and fieldValue cannot be null");
        }

        // 查询现有记录
        QueryWrapper<StudentInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_id", deviceId);
        queryWrapper.eq("deleted", 0);
        StudentInfoEntity studentInfoEntity = studentDao.selectOne(queryWrapper);
        if (studentInfoEntity == null) {
            return;
        }

        // 根据 fieldName 更新字段
        try {
            switch (fieldName.toLowerCase()) {
                case "nick_name":
                    studentInfoEntity.setNickName(fieldValue);
                    logger.info("Updated nickname to {}", fieldValue);
                    break;
                case "birth_date":
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                    sdf.setLenient(false); // 严格校验格式
                    Date birthDate = sdf.parse(fieldValue + "-01"); // 默认日期为 1
                    studentInfoEntity.setBirthDate(birthDate);
                    logger.info("Updated birth_date to {}", fieldValue);
                    break;
                case "gender":
                    // 转换为 StudentGender 枚举
                    String genderValue = fieldValue.toLowerCase();
                    if ("boy".equals(genderValue)) {
                        studentInfoEntity.setGender(StudentGender.BOY);
                        logger.info("Updated gender to BOY");
                    } else if ("girl".equals(genderValue)) {
                        studentInfoEntity.setGender(StudentGender.GIRL);
                        logger.info("Updated gender to GIRL");
                    } else {
                        logger.warn("Invalid gender value: {}", fieldValue);
                        throw new IllegalArgumentException("Invalid gender value: " + fieldValue + ". Must be 'boy' or 'girl'.");
                    }
                    break;
                default:
                    logger.warn("Invalid fieldName: {}", fieldName);
                    throw new IllegalArgumentException("Invalid fieldName: " + fieldName);
            }
        } catch (ParseException e) {
            logger.error("Invalid birth_date format: {}", fieldValue, e);
            throw new IllegalArgumentException("Invalid birth_date format: " + fieldValue + ". Must be YYYY-MM, like 2003-05.");
        }

        // 更新最后活跃时间
        studentInfoEntity.setLastActive(new Date());
        studentDao.updateById(studentInfoEntity); // 更新现有记录
        logger.info("Updated student record for deviceId={}", deviceId);
    }
}
