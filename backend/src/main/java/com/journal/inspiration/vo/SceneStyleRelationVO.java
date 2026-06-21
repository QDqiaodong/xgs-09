package com.journal.inspiration.vo;

import lombok.Data;

@Data
public class SceneStyleRelationVO {

    private Long id;

    private Long sceneCategoryId;

    private String sceneCategoryName;

    private Long styleCategoryId;

    private String styleCategoryName;

    private String styleCategoryIcon;

    private Integer matchScore;

    private Integer isPrimary;

    private Integer sort;
}
