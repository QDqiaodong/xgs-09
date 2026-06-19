package com.journal.inspiration.common;

import lombok.Getter;

@Getter
public enum ElementCategoryEnum {

    STICKER(1, "贴纸", "✨"),
    WASHI_TAPE(2, "胶带", "🎀"),
    PHOTO(3, "照片", "📷"),
    HANDWRITING(4, "手写字", "✍️"),
    BORDER(5, "边框", "🖼️");

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
        for (ElementCategoryEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
