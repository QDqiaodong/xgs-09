package com.journal.inspiration.dto;

import lombok.Data;
import java.util.List;

@Data
public class WorkPublishDTO {

    private Long userId;

    private String title;

    private String coverImage;

    private Integer coverType;

    private String content;

    private String layoutIdea;

    private String layoutConfig;

    private String colorScheme;

    private String inspiration;

    private List<Long> categoryIds;
}
