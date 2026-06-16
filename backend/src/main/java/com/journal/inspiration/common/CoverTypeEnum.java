package com.journal.inspiration.common;

import lombok.Getter;

@Getter
public enum CoverTypeEnum {

    HORIZONTAL_COLLAGE(1, "横向拼贴", 3, 2, "适合展示多图拼贴风格手账"),
    VERTICAL_FULL(2, "竖向整页", 3, 4, "适合展示竖向整页手账内容"),
    CLOSEUP(3, "局部特写", 1, 1, "适合展示细节特写");

    private final Integer code;
    private final String desc;
    private final Integer widthRatio;
    private final Integer heightRatio;
    private final String tip;

    CoverTypeEnum(Integer code, String desc, Integer widthRatio, Integer heightRatio, String tip) {
        this.code = code;
        this.desc = desc;
        this.widthRatio = widthRatio;
        this.heightRatio = heightRatio;
        this.tip = tip;
    }

    public static CoverTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CoverTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    public double getAspectRatio() {
        return (double) widthRatio / heightRatio;
    }

    public static CoverTypeEnum getDefault() {
        return HORIZONTAL_COLLAGE;
    }
}
