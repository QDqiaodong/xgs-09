package com.journal.inspiration.common;

import lombok.Getter;

@Getter
public enum ElementCategoryEnum {
    IMAGE(1, "图片", "🖼️"),
    TEXT(2, "文字", "✍️"),
    STICKER(3, "贴纸", "🏷️"),
    TAPE(4, "胶带", "🎀"),
    STAMP(5, "印章", "📌"),
    HANDWRITING(6, "手写", "📝");

    private final Integer code;
    private final String desc;
    private final String icon;

    ElementCategoryEnum(Integer code, String desc, String icon) {
        this.code = code;
        this.desc = desc;
        this.icon = icon;
    }

    public static ElementCategoryEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ElementCategoryEnum item : values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
