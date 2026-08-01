package com.example.basecommon.util;


import com.example.basecommon.enums.MessageCode;
import com.example.basecommon.exception.BasicException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;




public class StringUtil {


    public static String defaultString(String str) {
        return str == null ? "" : str;
    }


    public static String sanitizeString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        return input.trim();
    }


    static List<String> parseCsvToList(String csv) {
        if (csv == null || csv.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }


    public static List<String> parseCsvToListOrThrow(String csv, MessageCode error) {
        List<String> list = parseCsvToList(csv);
        if (list.isEmpty()) {
            throw new BasicException(error);
        }
        return list;
    }






}

