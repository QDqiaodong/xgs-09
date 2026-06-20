package com.journal.inspiration.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("color_palette")
public class ColorPalette {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String styleDescription;

    private String colorScheme;

    private String coverColor;

    private String categoryIds;

    private Integer useCount;

    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
