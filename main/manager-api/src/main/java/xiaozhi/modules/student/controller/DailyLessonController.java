package xiaozhi.modules.student.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import xiaozhi.common.utils.ConvertUtils;
import xiaozhi.common.utils.Result;
import xiaozhi.modules.agent.entity.AgentEntity;
import xiaozhi.modules.student.dto.DailyLessonSaveDTO;
import xiaozhi.modules.student.entity.DailyLessonEntity;
import xiaozhi.modules.student.service.DailyLessonService;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Tag(name = "课程管理")
@AllArgsConstructor
@RestController
@RequestMapping("/daily-lesson")
public class DailyLessonController {
    private final DailyLessonService dailyLessonService;

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

}
