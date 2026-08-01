package com.example.basecommon.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
public class GsonParserUtils {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(byte[].class, new ByteArrayToStringAdapter())


            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, type, ctx) ->
                            src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString()))


            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (src, type, ctx) ->
                            src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString()))


            .registerTypeAdapter(LocalTime.class,
                    (JsonSerializer<LocalTime>) (src, type, ctx) ->
                            src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString()))


            .create();


    public static String parseObjectToString(Object object) {
        if (object == null) return "null";


        try {
            return GSON.toJson(object);
        } catch (Exception e) {
            log.warn("Error while parsing object: {}", object.getClass().getName(), e);
            return "[SERIALIZE_ERROR]";
        }
    }


    public static <T> T parseStringToObject(String json, Class<T> classObject) {
        try {
            return GSON.fromJson(json, classObject);
        } catch (Exception e) {
            log.warn("Error parsing json to object: {}", classObject.getName(), e);
            return null;
        }
    }


    public static <T> List<T> convertJsonToList(String json, Class<T> classType) {
        try {
            Type listType = TypeToken.getParameterized(List.class, classType).getType();
            return GSON.fromJson(json, listType);
        } catch (Exception e) {
            log.warn("Error parsing json to list: {}", classType.getName(), e);
            return new ArrayList<>();
        }
    }


    public static <T> List<T> convertJsonToList(String json, Class<T> classType, List<T> defaultValue) {
        List<T> list = convertJsonToList(json, classType);
        return list != null ? list : defaultValue;
    }


    public static String parseObjectToStringDisableHtmlEscaping(Object object) {
        try {
            return new GsonBuilder()
                    .registerTypeAdapter(byte[].class, new ByteArrayToStringAdapter())
                    .disableHtmlEscaping()


                    // nhớ add lại adapter java.time
                    .registerTypeAdapter(LocalDateTime.class,
                            (JsonSerializer<LocalDateTime>) (src, type, ctx) ->
                                    src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString()))
                    .registerTypeAdapter(LocalDate.class,
                            (JsonSerializer<LocalDate>) (src, type, ctx) ->
                                    src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString()))
                    .registerTypeAdapter(LocalTime.class,
                            (JsonSerializer<LocalTime>) (src, type, ctx) ->
                                    src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString()))


                    .create()
                    .toJson(object);
        } catch (Exception e) {
            log.warn("Error while parsing object (disableHtmlEscaping)", e);
            return "[SERIALIZE_ERROR]";
        }
    }


    private static class ByteArrayToStringAdapter implements JsonSerializer<byte[]> {
        @Override
        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("byte-array");
        }
    }
}

