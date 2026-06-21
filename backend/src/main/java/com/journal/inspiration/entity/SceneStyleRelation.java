package com.journal.inspiration.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("scene_style_relation")
public class SceneStyleRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sceneCategoryId;

    private Long styleCategoryId;

    private Integer matchScore;

    private Integer isPrimary;

    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
