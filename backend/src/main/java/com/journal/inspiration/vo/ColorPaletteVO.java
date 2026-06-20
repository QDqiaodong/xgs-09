package com.journal.inspiration.vo;

import lombok.Data;
import java.util.List;

@Data
public class ColorPaletteVO {

    private Long id;

    private String name;

    private String styleDescription;

    private String colorScheme;

    private String coverColor;

    private List<Long> categoryIdList;

    private Integer useCount;

    private Integer sort;
}
