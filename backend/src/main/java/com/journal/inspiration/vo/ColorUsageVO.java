package com.journal.inspiration.vo;

import lombok.Data;
import java.util.List;

@Data
public class ColorUsageVO {

    private List<ColorFamilyStatsVO> familyStats;

    private List<ColorCombinationVO> topCombinations;

    private List<SingleColorStatsVO> topColors;

    private Integer totalColorSchemes;
}
