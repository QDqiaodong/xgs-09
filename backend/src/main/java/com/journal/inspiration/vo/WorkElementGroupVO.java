package com.journal.inspiration.vo;

import lombok.Data;

import java.util.List;

@Data
public class WorkElementGroupVO {

    private Integer category;

    private String categoryDesc;

    private String categoryIcon;

    private Integer totalCount;

    private List<WorkElementVO> elements;
}
