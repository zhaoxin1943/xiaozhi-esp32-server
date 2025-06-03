package xiaozhi.modules.student.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xiaozhi.common.utils.Result;
import xiaozhi.modules.student.service.StudentInfoService;

@Tag(name = "学生管理")
@AllArgsConstructor
@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentInfoService studentInfoService;

    @GetMapping("/{deviceId}")
    @Operation(summary = "查询是否录入过学生信息")
    @RequiresPermissions("sys:role:normal")
    public Result<Boolean> checkStudentExistsByDeviceId(@PathVariable String deviceId) {
        boolean has_student = studentInfoService.hasStudentByDeviceId(deviceId);
        return new Result<Boolean>().ok(has_student);
    }
}
