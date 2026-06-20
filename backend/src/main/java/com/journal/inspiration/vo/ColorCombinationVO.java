package com.journal.inspiration.vo;

import lombok.Data;
import java.util.List;

@Data
public class ColorCombinationVO {

    private List<String> colors;

    private Integer count;

    private String styleTag;
}
