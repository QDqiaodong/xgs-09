package com.journal.inspiration.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("journal_work")
public class JournalWork {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String coverImage;

    private String content;

    private String layoutIdea;

    private String colorScheme;

    private String inspiration;

    private Integer viewCount;

    private Integer likeCount;

    private Integer favoriteCount;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
