package com.journal.inspiration.vo;

import lombok.Data;
import java.util.List;

@Data
public class ScenePreferenceVO {

    private Long categoryId;

    private String name;

    private String type;

    private Integer count;

    private Integer totalWorks;

    private Double percentage;

    private String icon;

    private String description;

    private String bannerColor;

    private List<WorkCoverVO> representativeWorks;
}
