package com.journal.inspiration.vo;

import lombok.Data;

@Data
public class WorkElementVO {

    private Long id;

    private Long workId;

    private Integer category;

    private String categoryDesc;

    private String categoryIcon;

    private String name;

    private String description;

    private Integer quantity;

    private String position;

    private Integer sort;
}
