package com.journal.inspiration.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkElementDTO {

    @NotNull(message = "元素分类不能为空")
    private Integer category;

    private String name;

    private String description;

    private String imageUrl;

    private Integer quantity;

    private Integer sort;
}
