package com.example.basecommon.constants;

public class DateConst {


    public static final String TIMEZONE_HEADER = "timezone";


    // yyyy pattern date
    public static final String YYYY = "yyyy";
    // MM pattern date
    public static final String MM = "MM";
    // dd pattern date
    public static final String DD = "dd";
    public static final String HH = "HH";
    public static final String MMi = "mm";
    // dd-MM-yyyy pattern date
    public static final String DD_MM_YYYY = "dd/MM/yyyy";
    // yyyy-MM-dd pattern date
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    // yyyy/MM/dd pattern date
    public static final String YYYY_MM_DD_SLASH = "yyyy/MM/dd";
    public static final String YY_MM_DD_SLASH = "yy/MM/dd";
    // yyyyMMdd pattern date
    public static final String YYYYMMDD = "yyyyMMdd";
    // yyyy-MM-dd HH:mm:ss pattern date
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    // yyyy/MM/dd HH:mm:ss pattern date
    public static final String YYYY_MM_DD_HH_MM_SS_SLASH = "yyyy/MM/dd HH:mm:ss";
    // ddMMyy pattern date
    public static final String DDMMYY = "ddMMyy";
    // yyMMdd pattern date
    public static final String YYMMDD = "yyMMdd";
    // yyyyMMddHHmmss pattern date
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    // HH:mm pattern date
    public static final String HH_MM = "HH:mm";
    // HHmm pattern date
    public static final String HHMM = "HHmm";
    // Timezone UTC
    public static final String UTC = "UTC";
    // Timezone JST
    public static final String JST = "Asia/Tokyo";
    // yyyy年MM月dd日(E) HH:mm pattern date
    public static final String YYYYMMDDE_HHMM = "yyyy年MM月dd日(E) HH:mm";
    // yyyy年MM月dd日(E) pattern date
    public static final String YYYYMMDDE = "yyyy年MM月dd日";
    public static final String MMDDE = "MM月dd日";


    // yyyy年MM月dd日 HH:mm pattern date
    public static final String YYYYMMDD_HHMM = "yyyy年MM月dd日 HH:mm";


    public static final String YEAR_TWO_DIGIT = "yy[yy]";
    public static final String YEAR_TWO_DIGIT2 = "yy[yy]MMddHHmm";


    // HHmmss pattern date
    public static final String HHMMSS = "HHmmss";


    // regex pattern
    // matching yyyyMMdd : 20221222
    public static final String YYYYMMDD_REGEX = "^(([0-9][0-9][0-9][0-9])((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01])|(0[469]|11)(0[1-9]|[12][0-9]|30)|(02)(0[1-9]|1[0-9]|2[0-8]))|([0-9][0-9])([02468][048]|[13579][26])(02)(29))$";
    // matching yyyy-MM-dd : 2022-12-22
    public static final String YYYY_MM_DD_REGEX = "^(((\\d{4}-((0[13578]-|1[02]\\-)(0[1-9]|[12]\\d|3[01])|(0[13456789]-|1[012]-)(0[1-9]|[12]\\d|30)|02\\-(0[1-9]|1\\d|2[0-8])))|((([02468][048]|[13579][26])00|\\d{2}([13579][26]|0[48]|[2468][048])))-02-29)){0,10}$";
    // matching yyyy-MM-dd HH:mm:ss : 2022-12-22 20:00:00
    public static final String YYYY_MM_DD_HH_MM_SS_REGEX = "(^(((\\d\\d)(([02468][048])|([13579][26]))-02-29)|(((\\d\\d)(\\d\\d)))-((((0\\d)|(1[0-2]))-((0\\d)|(1\\d)|(2[0-8])))|((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30)))))\\s(([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d))$)";
    private DateConst() {
        throw new IllegalStateException();
    }


    public static final String DEFAULT_DATE_OF_BIRTH = null;


}

