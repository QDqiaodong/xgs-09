package com.journal.inspiration.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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

    private Integer quantity;

    private String position;

    private Integer sort;

    private LocalDateTime createTime;
}
