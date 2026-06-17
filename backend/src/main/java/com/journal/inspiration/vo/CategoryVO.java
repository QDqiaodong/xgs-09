package com.journal.inspiration.vo;

import lombok.Data;

@Data
public class CategoryVO {

    private Long id;

    private String name;

    private String type;

    private String icon;

    private String description;

    private String bannerColor;

    private Integer workCount;

    private Integer taskCount;
}
