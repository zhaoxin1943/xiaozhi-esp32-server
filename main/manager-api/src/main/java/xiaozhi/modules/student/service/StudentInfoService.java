package xiaozhi.modules.student.service;

import xiaozhi.common.service.BaseService;
import xiaozhi.modules.student.entity.StudentInfoEntity;

public interface StudentInfoService extends BaseService<StudentInfoEntity> {
    /**
     * 根据设备id查询学生信息
     * @param deviceId
     * @return
     */
    StudentInfoEntity getStudentByDeviceId(String deviceId);

    /**
     * 插入一条学生信息
     * @param deviceId 一台设备对应一个学生
     */
    void insertStudent(String deviceId);

    /**
     * 解绑时，同时删除对应的学生信息
     * @param deviceId
     */
    void deleteStudentByDeviceId(String deviceId,String tag);

    /**
     * 更新学生信息
     * @param deviceId
     */
    void updateStudentInfoByDeviceId(String deviceId,String filedName,String filedValue);
}
