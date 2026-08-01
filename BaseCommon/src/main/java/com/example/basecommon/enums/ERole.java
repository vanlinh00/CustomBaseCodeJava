package com.example.basecommon.enums;


import com.example.basecommon.exception.BasicException;

public enum ERole {
    ROLE_ADMIN, // System Admin
    ROLE_GROUP, // Provincial Admin (provincial management)
    ROLE_INSTRUCTOR, // Provincial staff (officers, employees working in the province)
    ROLE_OUTSOURCED_ADMIN_GROUP,
    ROLE_OUTSOURCED_INSTRUCTOR;


    public static ERole fromString(String roleStr) {
        try {
            return ERole.valueOf(roleStr.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BasicException(MessageCode.INVALID_ARGUMENT);
        }
    }
}

