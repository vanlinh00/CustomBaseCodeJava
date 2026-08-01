package com.example.basecommon.util;

import com.example.basecommon.enums.MessageCode;
import com.example.basecommon.exception.BasicException;
import org.apache.commons.lang3.StringUtils;


import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.example.basecommon.util.StringUtil.sanitizeString;


public class NumberUtil {




    /**
     * @param num
     * @return num or 0
     */
    public static int checkNull(Integer num) {
        if (num == null) {
            return 0;
        }
        return num;
    }


    /**
     * @param num
     * @return
     */
    public static long checkNull(Long num) {
        if (num == null) {
            return 0;
        }
        return num;
    }


    public static Long parseLong(String num) {
        if (StringUtils.isEmpty(num)) {
            return null;


        }


        try {
            long res = Long.parseLong(num);
            return res;
        } catch (Exception e) {
            throw new BasicException(MessageCode.INVALID_ARGUMENT);
        }
    }


    public static Integer toInteger(Object data) {
        if (data == null) {
            return null;
        } else {
            Integer result;
            try {
                if (data instanceof String) {
                    result = Integer.valueOf(((String) data).trim());
                } else if (data instanceof BigDecimal) {
                    result = ((BigDecimal) data).intValue();
                } else if (data instanceof Double) {
                    Double db = (Double) data;
                    result = db.intValue();
                } else {
                    result = Integer.valueOf(data.toString());
                }
            } catch (Exception ex) {
                return null;
            }
            return result;
        }
    }


    public static int toIntegerZero(Object data) {
        return checkNull(toInteger(data));
    }


    public static Long toLong(Object data) {
        if (data == null) {
            return null;
        } else {
            Long result;
            try {
                if (data instanceof String) {
                    result = Long.valueOf(((String) data).trim());
                } else if (data instanceof Integer) {
                    result = ((Integer) data).longValue();
                } else if (data instanceof BigDecimal) {
                    result = ((BigDecimal) data).longValue();
                } else if (data instanceof Double) {
                    Double db = (Double) data;
                    result = db.longValue();
                } else {
                    result = Long.valueOf(data.toString());
                }
            } catch (Exception ex) {
                return null;
            }
            return result;
        }
    }


    public static long toLongZero(Object data) {
        return checkNull(toLong(data));
    }


    public static Long parseStringToLongOrThrow(String input) {
        String sanitized = sanitizeString(input);
        if (sanitized == null) {
            return null;
        }
        try {
            return Long.valueOf(sanitized);
        } catch (NumberFormatException e) {
            throw new BasicException(MessageCode.INVALID_ARGUMENT);
        }
    }


    public static Float parseStringToFloatOrThrow(String input) {
        String sanitized = sanitizeString(input);
        if (sanitized == null) {
            return null;
        }
        try {
            return Float.valueOf(sanitized);
        } catch (NumberFormatException e) {
            throw new BasicException(MessageCode.INVALID_ARGUMENT);
        }
    }
    public static Integer roundToInt(Float value) {
        if (value == null) {
            return 0;
        }
        return Math.round(value);
    }
    public static Float roundTo2Decimals(Float value) {
        if (value == null) {
            return 0f;
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
}

