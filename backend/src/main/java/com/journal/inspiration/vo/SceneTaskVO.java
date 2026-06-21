package com.journal.inspiration.vo;

import lombok.Data;
import java.util.List;

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

    private List<SceneStyleRelationVO> recommendedStyles;
}
