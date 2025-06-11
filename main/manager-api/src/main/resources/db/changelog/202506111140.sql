CREATE TABLE `daily_lesson`
(
    `id`              VARCHAR(36) NOT NULL COMMENT 'ID',
    `create_date`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更改时间',
    `deleted`         TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    `lesson_name`     VARCHAR(255)         DEFAULT NULL COMMENT '课程名称',
    `lesson_duration` INT                  DEFAULT NULL COMMENT '课程时长',
    `lesson_index`    INT                  DEFAULT NULL COMMENT '课程索引',
    `publish_date`    DATETIME             DEFAULT NULL COMMENT '课程发布时间',
    `audio_url`       VARCHAR(255)         DEFAULT NULL COMMENT '课程音频url',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日课程';