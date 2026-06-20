package com.journal.inspiration.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class WorkUpdateDTO {

    @NotNull(message = "作品ID不能为空")
    private Long id;

    @NotNull(message = "用户ID不能为空")
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

    private List<WorkElementDTO> elements;
}
