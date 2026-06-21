package com.journal.inspiration.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("layout_template")
public class LayoutTemplate {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String templateCode;

    private String templateName;

    private String templateType;

    private String description;

    private String previewImage;

    private String layoutConfig;

    private String styleTags;

    private String sceneTags;

    private Integer useCount;

    private Integer isPreset;

    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
