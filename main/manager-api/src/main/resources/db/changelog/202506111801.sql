CREATE TABLE `study_log`
(
    `id`              VARCHAR(36) NOT NULL COMMENT '主键ID',
    `student_id`      VARCHAR(36) NOT NULL COMMENT '学生ID (外键, 关联 student_info.id)',
    `lesson_id`       VARCHAR(36) NOT NULL COMMENT '每日课程ID (外键, 关联 daily_lesson.id)',
    `completion_date` DATE        NOT NULL COMMENT '课程完成日期',
    `create_date`     DATETIME    NOT NULL COMMENT '创建时间',
    `update_date`     DATETIME    NOT NULL COMMENT '更改时间',
    `deleted`         TINYINT(1) UNSIGNED NOT NULL DEFAULT '0' COMMENT '是否删除 (0:未删除, 1:已删除)',
    PRIMARY KEY (`id`),
    INDEX             `idx_student_completion` (`student_id`, `completion_date`) USING BTREE COMMENT '学生完成日期索引，用于快速查询某学生某天的学习记录',
    INDEX             `idx_lesson_id` (`lesson_id`) USING BTREE COMMENT '课程ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生学习记录表';