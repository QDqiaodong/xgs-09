package com.journal.inspiration.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LayoutTemplateVO {

    private Long id;

    private String templateCode;

    private String templateName;

    private String templateType;

    private String templateTypeName;

    private String description;

    private String previewImage;

    private String layoutConfig;

    private String styleTags;

    private String sceneTags;

    private List<String> styleTagList;

    private List<String> sceneTagList;

    private Integer useCount;

    private Integer isPreset;

    private Integer sort;

    private Boolean isRecommended;

    private LocalDateTime createTime;
}
