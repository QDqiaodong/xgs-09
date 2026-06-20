package com.journal.inspiration.vo;

import lombok.Data;

@Data
public class SceneTaskCheckVO {

    private Long id;

    private Long sceneCategoryId;

    private String title;

    private String description;

    private String promptTips;

    private String layoutSuggestion;

    private String icon;

    private Integer sort;

    private Integer checked;
}
