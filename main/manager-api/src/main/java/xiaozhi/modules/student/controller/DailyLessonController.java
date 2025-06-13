package xiaozhi.modules.student.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import xiaozhi.common.exception.RenException;
import xiaozhi.common.utils.ConvertUtils;
import xiaozhi.common.utils.Result;
import xiaozhi.modules.agent.entity.AgentEntity;
import xiaozhi.modules.student.dto.DailyLessonSaveDTO;
import xiaozhi.modules.student.dto.LessonCompletionRequestDto;
import xiaozhi.modules.student.entity.DailyLessonEntity;
import xiaozhi.modules.student.entity.StudentInfoEntity;
import xiaozhi.modules.student.service.DailyLessonService;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.Date;

import org.springframework.validation.annotation.Validated;
import xiaozhi.modules.student.service.StudentInfoService;
import xiaozhi.modules.student.service.StudyLogService;

@Tag(name = "课程管理")
@AllArgsConstructor
@RestController
@RequestMapping("/daily-lesson")
public class DailyLessonController {
    private final DailyLessonService dailyLessonService;
    private final StudentInfoService studentInfoService;
    private final StudyLogService studyLogService;

    @GetMapping("/{date}")
    @Operation(summary = "获取指定日期的课程")
    public Result<DailyLessonEntity> getDailyLessonByDate(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        DailyLessonEntity dailyLessonEntity = dailyLessonService.getDailyLessonByDate(date);
        return new Result<DailyLessonEntity>().ok(dailyLessonEntity);
    }

    @PostMapping("/add-daily-lesson")
    @Operation(summary = "新增每日课程")
    @RequiresPermissions("sys:role:normal")
    public Result<String> addDailyLesson(@Valid @RequestBody DailyLessonSaveDTO dto) {
        DailyLessonEntity entity = ConvertUtils.sourceToTarget(dto, DailyLessonEntity.class);
        BeanUtils.copyProperties(dto, entity);
        dailyLessonService.addDailyLesson(entity);
        return new Result<String>().ok(entity.getId());
    }

    /**
     * 实现记录课程完成的接口
     *
     * @param lessonId   课程ID，从路径中获取
     * @param requestDto 请求体，包含设备ID
     * @return 操作结果
     */
    @PostMapping("/{lessonId}/complete")
    @Operation(summary = "记录一堂课已完成")
    public Result<Void> recordLessonCompletion(
            @Parameter(description = "要标记为完成的课程ID") @PathVariable String lessonId,
            @Validated @RequestBody LessonCompletionRequestDto requestDto) {

        StudentInfoEntity student = studentInfoService.getStudentByDeviceId(requestDto.getDeviceId());

        if (student == null) {
            throw new RenException("未找到该设备绑定的学生，无法记录课程。");
        }

        studyLogService.recordLessonCompletion(student.getId(), lessonId);

        return new Result<Void>().ok(null);
    }

}
