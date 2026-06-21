package com.journal.inspiration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class WorkPublishDTO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "封面不能为空")
    private String coverImage;

    private Integer coverType;

    private String content;

    private String layoutIdea;

    private String layoutConfig;

    private Long layoutTemplateId;

    private String colorScheme;

    private String inspiration;

    private List<Long> categoryIds;

    private List<WorkElementDTO> elements;

    private Long sceneCategoryId;

    private List<Long> checkedSceneTaskIds;
}
