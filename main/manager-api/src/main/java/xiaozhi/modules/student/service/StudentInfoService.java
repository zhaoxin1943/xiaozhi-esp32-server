package xiaozhi.modules.student.service;

import xiaozhi.common.service.BaseService;
import xiaozhi.modules.student.entity.StudentInfoEntity;

public interface StudentInfoService extends BaseService<StudentInfoEntity> {
    StudentInfoEntity getStudentByDeviceId(String deviceId);

    void insertStudent(String deviceId);

    /**
     * 解绑时，同时删除对应的学生信息
     * @param deviceId
     */
    void deleteStudentByDeviceId(String deviceId);
}
