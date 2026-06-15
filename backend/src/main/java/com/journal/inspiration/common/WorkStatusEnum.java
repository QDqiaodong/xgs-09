package com.journal.inspiration.common;

import lombok.Getter;

@Getter
public enum WorkStatusEnum {

    PUBLIC(1, "可展示"),
    PRIVATE(2, "仅本人可见"),
    ARCHIVED(3, "已归档");

    private final Integer code;
    private final String desc;

    WorkStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static WorkStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (WorkStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static boolean isPublic(Integer code) {
        return PUBLIC.getCode().equals(code);
    }

    public static boolean isPrivate(Integer code) {
        return PRIVATE.getCode().equals(code);
    }

    public static boolean isArchived(Integer code) {
        return ARCHIVED.getCode().equals(code);
    }

    public static boolean isVisibleToPublic(Integer code) {
        return PUBLIC.getCode().equals(code);
    }
}
