package com.example.basecommon.enums;

import lombok.Getter;

@Getter
public enum MessageCode {
    SUCCESS("i.xx.fw.200"),
    NOT_FOUND("i.xx.fw.404"),
    GROUP_NOT_FOUND("i.xx.fw.405"),
    USER_NOT_FOUND("i.xx.fw.406"),


    FORBIDDEN("w.xx.fw.8000"),
    INVALID_ARGUMENT("w.xx.fw.8001"),
    CODE_2FA_INVALID("w.xx.fw.8002"),
    CODE_2FA_EXPIRED("w.xx.fw.8003"),
    ACCOUNT_INCORRECT("w.xx.fw.8004"),
    ACCOUNT_LOCKED("w.xx.fw.8005"),
    CODE_2FA_WRONG_MANY_TIMES("w.xx.fw.8006"),
    SAME_OLD_PASSWORD("w.xx.fw.8007"),
    CONFIRM_PASSWORD_NOT_MATCH("w.xx.fw.8008"),
    RECOVERY_PASSWORD_CODE_INVALID("w.xx.fw.8009"),
    RECOVERY_PASSWORD_CODE_WRONG_TIMES("w.xx.fw.8010"),
    RECOVERY_PASSWORD_CODE_EXPIRED("w.xx.fw.8011"),
    BAD_REQUEST("w.xx.fw.8012"),
    ACCOUNT_DISABLED("w.xx.fw.8013"),
    ACCESS_DENIED("w.xx.fw.8014"),
    ACCESS_NOT_STARTED("w.xx.fw.8015"),





    SYSTEM_ERROR("e.xx.fw.9001"),
    INVALID_TOKEN("e.xx.fw.9002"),




    INVALID_DATE_FORMAT("e.xx.fw.8014"),
    END_DATE_BEFORE_START_DATE("e.xx.fw.8015");


    private String code;


    MessageCode(String code) {
        this.code = code;
    }
}

