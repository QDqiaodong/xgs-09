package com.journal.inspiration.vo;

import lombok.Data;

@Data
public class ColorFamilyStatsVO {

    private String family;

    private String familyName;

    private String representativeColor;

    private Integer count;

    private Double percentage;
}
