package com.example.basecommon.enums;

public enum VerifyStatusUser {
    NOT_VERIFIED(0),
    VERIFIED(1),
    DELETED(2),
    PENDING(3);
    private final int value;


    VerifyStatusUser(int value) {
        this.value = value;
    }


    public int getValue() {
        return value;
    }




    public static VerifyStatusUser fromValueOrDefault(Integer value) {
        if (value == null) return NOT_VERIFIED;
        for (VerifyStatusUser s : values()) {
            if (s.value == value) return s;
        }
        return NOT_VERIFIED;
    }
}



