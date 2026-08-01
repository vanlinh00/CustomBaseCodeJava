package com.example.basecommon.enums;


import com.example.basecommon.exception.BasicException;

public enum EGender {
    M("male"),
    F("female"),
    U("other");


    private final String label;


    EGender(String label) {
        this.label = label;
    }


    public String getLabel() {
        return label;
    }


    public static EGender convertStringToEGender(String genderStr) {
        if (genderStr == null) return null;
        switch (genderStr.toLowerCase()) {
            case "male": return EGender.M;
            case "female": return EGender.F;
            case "other": return EGender.U;
            default: throw new BasicException(MessageCode.INVALID_ARGUMENT);
        }
    }


    public static String convertEGenderToString(EGender gender) {
        if (gender == null) return "other";
        return gender.getLabel();
    }


    public static EGender convertStringToEGenderOrNull(String genderStr) {
        if (genderStr == null) return null;
        switch (genderStr.toLowerCase()) {
            case "male": return EGender.M;
            case "female": return EGender.F;
            case "other": return EGender.U;
            default: return null;
        }
    }
}



