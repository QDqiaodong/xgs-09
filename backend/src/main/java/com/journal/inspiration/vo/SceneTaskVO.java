package com.journal.inspiration.vo;

import lombok.Data;

@Data
public class SceneTaskVO {

    private Long id;

    private Long sceneCategoryId;

    private String title;

    private String description;

    private String promptTips;

    private String layoutSuggestion;

    private String icon;

    private Integer sort;
}
