package com.touclick.android.utils;

/**
 * Created by fanjl on 2017/3/30.
 */
public enum CaptchaCTypeEnum {
    CLICK(13, "图文点击型"),
    BLOCK(14, "图标选择型");


    private int id;
    private String name;

    CaptchaCTypeEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public static CaptchaCTypeEnum fromValue(int id) {
        for (CaptchaCTypeEnum status : CaptchaCTypeEnum.values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        return null;
    }

    public static CaptchaCTypeEnum fromString(String str) {
        for (CaptchaCTypeEnum role : CaptchaCTypeEnum.values()) {
            if (role.name().equals(str)) {
                return role;
            }
        }
        throw new IllegalArgumentException(str);
    }
}
