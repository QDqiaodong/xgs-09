package com.journal.inspiration.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("work_scene_task_check")
public class WorkSceneTaskCheck {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long workId;

    private Long sceneTaskId;

    private Long sceneCategoryId;

    private Long userId;

    private Integer checked;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
