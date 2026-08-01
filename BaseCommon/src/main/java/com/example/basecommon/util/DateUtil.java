package com.example.basecommon.util;

import com.example.basecommon.constants.DateConst;
import com.example.basecommon.enums.MessageCode;
import com.example.basecommon.exception.BasicException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * @author Linhnv03
 */
@Slf4j
@Component
public class DateUtil {


    @Autowired
    private HttpServletRequest httpServletRequest;


    private static DateUtil dateUtil;


    @PostConstruct
    public void init() {
        dateUtil = this;
        dateUtil.httpServletRequest = this.httpServletRequest;
    }


    /**
     * Parse string to LocalDate. Throws BasicException if null/empty or invalid format.
     *
     * @param dateStr input date string in YYYY-MM-DD
     * @return parsed LocalDate
     */
    public static LocalDate parseDateOrThrow(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new BasicException(MessageCode.INVALID_ARGUMENT);
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new BasicException(MessageCode.INVALID_DATE_FORMAT);
        }
    }




    public static Date parseDateToUtilDateOrThrow(String dateStr) {


        dateStr = DateUtil.convertCompactFormatToDateStr(dateStr);


        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new BasicException(MessageCode.INVALID_ARGUMENT);
        }
        try {
            LocalDate localDate = LocalDate.parse(dateStr);
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            throw new BasicException(MessageCode.INVALID_DATE_FORMAT);
        }
    }


    /**
     * Validate start date <= end date, throw error if not
     */
    public static void validateStartEndDate(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new BasicException(MessageCode.END_DATE_BEFORE_START_DATE);
        }
    }


    /**
     * Calculate the number of days between 2 dates (including both ends)
     */
    public static long countDaysInclusive(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }


    /**
     * Specify a time range by number of days:
     * <=7 days: "week"
     * <=31 days: "month"
     * >31 days: "year"
     */
    public static String calculateRange(long days) {
        if (days <= 7) {
            return "week";
        } else if (days <= 31) {
            return "month";
        } else {
            return "year";
        }
    }


    /**
     * List of weekdays abbreviated to three letters, following international standards,
     * starting from Monday to Sunday.
     */
    public static final List<String> WEEKDAYS = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");


    public static List<String> getRollingWeekdayLabels(LocalDate dateStart) {
        return IntStream.range(0, 7)
                .mapToObj(i -> dateStart.plusDays(i)
                        .getDayOfWeek()
                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                .collect(Collectors.toList());
    }


    /**
     * List of months abbreviated to three letters.
     */
    public static final List<String> MONTHS = Arrays.asList(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    );


    /**
     * Generate a list of days in a month formatted as two-digit strings
     * such as "01", "02", ... up to the given maxDay.
     *
     * @return a list of formatted day strings, with length equal to maxDay
     */
    public static List<String> getDaysOfMonth(LocalDate date) {
        return IntStream.rangeClosed(1, date.lengthOfMonth())
                .mapToObj(i -> String.format("%02d", i))
                .collect(Collectors.toList());
    }




    public static int calculatePercent(int count, long totalDays) {
        return totalDays > 0 ? (int) Math.round(count * 100.0 / totalDays) : 0;
    }


    public static String dateToStringTimeZone(Date date, String format) {
        Date dateTimezone = convertToSiteTimezone(date, getTimeZoneId(dateUtil.httpServletRequest));
        return dateToString(dateTimezone, format);
    }


    public static Date convertToSiteTimezone(Date utcSource, String timeZoneId) {
        if (utcSource != null) {
            DateFormat formatterUTC = new SimpleDateFormat(DateConst.YYYY_MM_DD_HH_MM_SS);
            formatterUTC.setTimeZone(TimeZone.getTimeZone(DateConst.UTC));
            try {
                Date date = formatterUTC.parse(dateToString(utcSource, DateConst.YYYY_MM_DD_HH_MM_SS));


                DateFormat formatterIST = new SimpleDateFormat(DateConst.YYYY_MM_DD_HH_MM_SS);
                formatterIST.setTimeZone(TimeZone.getTimeZone(timeZoneId));
                return stringToDate(formatterIST.format(date), DateConst.YYYY_MM_DD_HH_MM_SS);
            } catch (ParseException ignored) {
            }
        }
        return null;
    }


    public static String dateToString(Date date, String format) {
        if (date == null) {
            return null;
        }


        if (StringUtils.isEmpty(format)) {
            format = DateConst.YYYY_MM_DD;
        }
        return getSimpleDateFormat(date, format);
    }


    public static String getTimeZoneId(HttpServletRequest request) {
        try {
            String timeZone = request.getHeader(DateConst.TIMEZONE_HEADER);
            if (StringUtils.isNotBlank(timeZone)) {
                return ZoneId.of(timeZone).getId();
            }
        } catch (Exception ignored) {
        }


        return ZoneId.of(DateConst.JST).getId();
    }


    public static Date stringToDate(String date, String format) {
        if (date == null) {
            return null;
        }
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.parse(date);
        } catch (Exception ignored) {
        }
        return null;
    }


    private static String getSimpleDateFormat(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }


    public static String convertDateStrToCompactFormat(String dateStr) {
        if (StringUtils.isBlank(dateStr)) return null;
        if (dateStr.matches("\\d{8}")) return dateStr;
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }


    public static String convertCompactFormatToDateStr(String dateStr) {
        if (StringUtils.isBlank(dateStr)) return null;
        if ("00000000".equals(dateStr)) return "0000-00-00";
        if ("99999999".equals(dateStr)) return "9999-12-31";
        if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) return dateStr;
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }


    public static String convertCompactFormatToDate(String dateStr) {
        if (StringUtils.isBlank(dateStr)) return null;
        if ("00000000".equals(dateStr)) return "0000-00-00";
        if ("99999999".equals(dateStr)) return "9999-12-31";
        if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) return dateStr;
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DateConst.YYYYMMDD))
                .format(DateTimeFormatter.ofPattern(DateConst.YYYY_MM_DD_SLASH));
    }


    public static String convertCompactFormatToDateStrNoValidate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        if ("00000000".equals(dateStr)) return "0000-00-00";
        if ("99999999".equals(dateStr)) return "9999-12-31";
        if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) return dateStr;
        return dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6, 8);
    }


    public static Date calculateStartDate(int daysAgo) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -daysAgo);
        return cal.getTime();
    }




    public static String getDateFormattedFromDateTime(String dateTimeStr) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConst.YYYY_MM_DD_HH_MM_SS);
        LocalDateTime dt = LocalDateTime.parse(dateTimeStr, formatter);
        return dt.toLocalDate().toString();
    }




    public static Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    public static Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }


    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);


        return cal1.getTime().equals(cal2.getTime());
    }


    public static LocalDate convertToLocalDate(Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }


    public static String formatDateTime(Date date, LocalTime time, Locale locale) {
        if (date == null || time == null) {
            return "";
        }
        LocalDate localDate = convertToLocalDate(date);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, time);


        String pattern;
        if (Locale.JAPAN.equals(locale)) {
            pattern = DateConst.YYYYMMDD_HHMM;
        } else {
            pattern = DateConst.YYYY_MM_DD_HH_MM;
        }


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
        return localDateTime.format(formatter);
    }


    public static boolean isValidDateStr(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    public static LocalDate parseToLocalDate(String dateStr) {
        if (StringUtils.isBlank(dateStr)) return null;


        if (dateStr.matches("\\d{8}")) {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        }


        if (dateStr.contains("/")) {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        }


        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }


    public static Date today() {
        Calendar cal = Calendar.getInstance();
        resetTime(cal);
        return cal.getTime();
    }


    public static Date daysAgo(int days) {
        Calendar cal = Calendar.getInstance();
        resetTime(cal);
        cal.add(Calendar.DAY_OF_MONTH, -days);
        return cal.getTime();
    }


    public static Date monthsAgo(int months) {
        Calendar cal = Calendar.getInstance();
        resetTime(cal);
        cal.add(Calendar.MONTH, -months);
        return cal.getTime();
    }


    private static void resetTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }


}

