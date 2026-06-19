package com.journal.inspiration.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("work_element")
public class WorkElement {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long workId;

    private Integer category;

    private String name;

    private String description;

    private String imageUrl;

    private Integer quantity;

    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
