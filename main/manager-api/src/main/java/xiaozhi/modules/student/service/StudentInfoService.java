package xiaozhi.modules.student.service;

import xiaozhi.common.service.BaseService;
import xiaozhi.modules.student.entity.StudentInfoEntity;

public interface StudentInfoService extends BaseService<StudentInfoEntity> {
    StudentInfoEntity getStudentByDeviceId(String deviceId);
}
