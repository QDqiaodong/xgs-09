package com.journal.inspiration.vo;

import lombok.Data;

@Data
public class WorkElementVO {

    private Long id;

    private Integer category;

    private String categoryDesc;

    private String categoryIcon;

    private String name;

    private String description;

    private String imageUrl;

    private Integer quantity;

    private Integer sort;
}
