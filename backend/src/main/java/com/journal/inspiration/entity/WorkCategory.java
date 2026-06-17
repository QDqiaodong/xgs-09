package com.journal.inspiration.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("work_category")
public class WorkCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long workId;

    private Long categoryId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
