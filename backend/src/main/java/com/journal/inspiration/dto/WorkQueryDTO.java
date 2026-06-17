package com.journal.inspiration.dto;

import lombok.Data;

@Data
public class WorkQueryDTO {

    private Long categoryId;

    private String categoryType;

    private Long styleCategoryId;

    private Long sceneCategoryId;

    private String keyword;

    private Long userId;

    private Integer status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
