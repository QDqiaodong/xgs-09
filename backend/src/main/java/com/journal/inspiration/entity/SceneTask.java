package com.journal.inspiration.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("scene_task")
public class SceneTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sceneCategoryId;

    private String title;

    private String description;

    private String promptTips;

    private String layoutSuggestion;

    private String icon;

    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
