package xiaozhi.modules.student.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import xiaozhi.common.utils.Result;
import xiaozhi.modules.student.entity.StudentInfoEntity;
import xiaozhi.modules.student.service.StudentInfoService;

@Tag(name = "学生管理")
@AllArgsConstructor
@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentInfoService studentInfoService;

    @GetMapping("/{deviceId}")
    @Operation(summary = "获取该设备绑定的学生信息")
    public Result<StudentInfoEntity> getStudentInfoByDeviceId(@PathVariable String deviceId) {
        StudentInfoEntity student_info = studentInfoService.getStudentByDeviceId(deviceId);
        return new Result<StudentInfoEntity>().ok(student_info);
    }

    @PostMapping("/updateStudentInfo")
    @Operation(summary = "更新学生信息")
    public Result<Boolean> updateStudentInfo(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("fieldName") String fieldName,
            @RequestParam("fieldValue") String fieldValue
    ) {
        studentInfoService.updateStudentInfoByDeviceId(deviceId, fieldName, fieldValue);
        return new Result<Boolean>().ok(true);
    }
}
