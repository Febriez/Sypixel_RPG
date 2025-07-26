package com.febrie.rpg.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Firestore JSON 형식 변환 유틸리티
 * 
 * @author Febrie, CoffeeTory
 */
public final class JsonUtil {
    
    private JsonUtil() {
        // 유틸리티 클래스
    }
    
    // ===== 값 생성 메소드 =====
    
    @NotNull
    public static JsonObject createStringValue(@NotNull String value) {
        JsonObject obj = new JsonObject();
        obj.addProperty("stringValue", value);
        return obj;
    }
    
    @NotNull
    public static JsonObject createIntegerValue(long value) {
        JsonObject obj = new JsonObject();
        obj.addProperty("integerValue", value);
        return obj;
    }
    
    @NotNull
    public static JsonObject createBooleanValue(boolean value) {
        JsonObject obj = new JsonObject();
        obj.addProperty("booleanValue", value);
        return obj;
    }
    
    @NotNull
    public static JsonObject createDoubleValue(double value) {
        JsonObject obj = new JsonObject();
        obj.addProperty("doubleValue", value);
        return obj;
    }
    
    @NotNull
    public static JsonObject createMapValue(@NotNull JsonObject fields) {
        JsonObject mapValue = new JsonObject();
        JsonObject map = new JsonObject();
        map.add("fields", fields);
        mapValue.add("mapValue", map);
        return mapValue;
    }
    
    @NotNull
    public static <T> JsonObject createArrayValue(@NotNull List<T> items, @NotNull Function<T, JsonObject> converter) {
        JsonObject arrayValue = new JsonObject();
        JsonObject array = new JsonObject();
        JsonArray values = new JsonArray();
        
        for (T item : items) {
            JsonObject itemValue = new JsonObject();
            itemValue.add("mapValue", converter.apply(item));
            values.add(itemValue);
        }
        
        array.add("values", values);
        arrayValue.add("arrayValue", array);
        return arrayValue;
    }
    
    @NotNull
    public static JsonObject createStringArrayValue(@NotNull List<String> strings) {
        JsonObject arrayValue = new JsonObject();
        JsonObject array = new JsonObject();
        JsonArray values = new JsonArray();
        
        for (String str : strings) {
            values.add(createStringValue(str));
        }
        
        array.add("values", values);
        arrayValue.add("arrayValue", array);
        return arrayValue;
    }
    
    @NotNull
    public static <K, V> JsonObject createMapField(@NotNull Map<K, V> map, @NotNull Function<V, JsonObject> valueConverter) {
        JsonObject mapValue = new JsonObject();
        JsonObject mapObj = new JsonObject();
        JsonObject fields = new JsonObject();
        
        map.forEach((key, value) -> {
            fields.add(key.toString(), valueConverter.apply(value));
        });
        
        mapObj.add("fields", fields);
        mapValue.add("mapValue", mapObj);
        return mapValue;
    }
    
    // ===== 값 추출 메소드 =====
    
    @Nullable
    public static String getStringValue(@NotNull JsonObject fields, @NotNull String fieldName) {
        return getStringValue(fields, fieldName, null);
    }
    
    @NotNull
    public static String getStringValue(@NotNull JsonObject fields, @NotNull String fieldName, @Nullable String defaultValue) {
        if (!fields.has(fieldName)) return defaultValue == null ? "" : defaultValue;
        JsonObject field = fields.getAsJsonObject(fieldName);
        if (!field.has("stringValue")) return defaultValue == null ? "" : defaultValue;
        return field.get("stringValue").getAsString();
    }
    
    public static int getIntegerValue(@NotNull JsonObject fields, @NotNull String fieldName) {
        return getIntegerValue(fields, fieldName, 0);
    }
    
    public static int getIntegerValue(@NotNull JsonObject fields, @NotNull String fieldName, int defaultValue) {
        if (!fields.has(fieldName)) return defaultValue;
        JsonObject field = fields.getAsJsonObject(fieldName);
        if (!field.has("integerValue")) return defaultValue;
        return field.get("integerValue").getAsInt();
    }
    
    public static long getLongValue(@NotNull JsonObject fields, @NotNull String fieldName) {
        return getLongValue(fields, fieldName, 0L);
    }
    
    public static long getLongValue(@NotNull JsonObject fields, @NotNull String fieldName, long defaultValue) {
        if (!fields.has(fieldName)) return defaultValue;
        JsonObject field = fields.getAsJsonObject(fieldName);
        if (!field.has("integerValue")) return defaultValue;
        return field.get("integerValue").getAsLong();
    }
    
    public static boolean getBooleanValue(@NotNull JsonObject fields, @NotNull String fieldName) {
        return getBooleanValue(fields, fieldName, false);
    }
    
    public static boolean getBooleanValue(@NotNull JsonObject fields, @NotNull String fieldName, boolean defaultValue) {
        if (!fields.has(fieldName)) return defaultValue;
        JsonObject field = fields.getAsJsonObject(fieldName);
        if (!field.has("booleanValue")) return defaultValue;
        return field.get("booleanValue").getAsBoolean();
    }
    
    public static double getDoubleValue(@NotNull JsonObject fields, @NotNull String fieldName) {
        return getDoubleValue(fields, fieldName, 0.0);
    }
    
    public static double getDoubleValue(@NotNull JsonObject fields, @NotNull String fieldName, double defaultValue) {
        if (!fields.has(fieldName)) return defaultValue;
        JsonObject field = fields.getAsJsonObject(fieldName);
        if (!field.has("doubleValue")) return defaultValue;
        return field.get("doubleValue").getAsDouble();
    }
    
    @NotNull
    public static <T> List<T> getArrayValue(@NotNull JsonObject fields, @NotNull String fieldName, @NotNull Function<JsonObject, T> converter) {
        if (!fields.has(fieldName)) return List.of();
        
        JsonObject fieldObj = fields.getAsJsonObject(fieldName);
        if (!fieldObj.has("arrayValue")) return List.of();
        
        JsonObject arrayValue = fieldObj.getAsJsonObject("arrayValue");
        if (!arrayValue.has("values")) return List.of();
        
        JsonArray values = arrayValue.getAsJsonArray("values");
        return StreamSupport.stream(values.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .filter(obj -> obj.has("mapValue"))
                .map(obj -> obj.getAsJsonObject("mapValue"))
                .map(converter)
                .collect(Collectors.toList());
    }
    
    @NotNull
    public static List<String> getStringArrayValue(@NotNull JsonObject fields, @NotNull String fieldName) {
        if (!fields.has(fieldName)) return List.of();
        
        JsonObject fieldObj = fields.getAsJsonObject(fieldName);
        if (!fieldObj.has("arrayValue")) return List.of();
        
        JsonObject arrayValue = fieldObj.getAsJsonObject("arrayValue");
        if (!arrayValue.has("values")) return List.of();
        
        JsonArray values = arrayValue.getAsJsonArray("values");
        return StreamSupport.stream(values.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .filter(obj -> obj.has("stringValue"))
                .map(obj -> obj.get("stringValue").getAsString())
                .collect(Collectors.toList());
    }
    
    @NotNull
    public static JsonObject getMapValue(@NotNull JsonObject fields, @NotNull String fieldName) {
        if (!fields.has(fieldName)) return new JsonObject();
        
        JsonObject fieldObj = fields.getAsJsonObject(fieldName);
        if (!fieldObj.has("mapValue")) return new JsonObject();
        
        JsonObject mapValue = fieldObj.getAsJsonObject("mapValue");
        if (!mapValue.has("fields")) return new JsonObject();
        
        return mapValue.getAsJsonObject("fields");
    }
    
    @NotNull
    public static <K, V> Map<K, V> getMapField(@NotNull JsonObject fields, @NotNull String fieldName, 
                                                @NotNull Function<String, K> keyConverter,
                                                @NotNull Function<JsonObject, V> valueConverter) {
        JsonObject mapFields = getMapValue(fields, fieldName);
        return mapFields.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> keyConverter.apply(entry.getKey()),
                        entry -> valueConverter.apply(entry.getValue().getAsJsonObject())
                ));
    }
    
    /**
     * DTO를 Firestore 형식의 JsonObject로 변환하는 헬퍼 메소드
     */
    @NotNull
    public static JsonObject wrapInDocument(@NotNull JsonObject fields) {
        JsonObject document = new JsonObject();
        document.add("fields", fields);
        return document;
    }
    
    /**
     * Firestore 문서에서 fields 추출
     */
    @NotNull
    public static JsonObject unwrapDocument(@NotNull JsonObject document) {
        if (!document.has("fields")) {
            throw new IllegalArgumentException("Invalid Firestore document: missing fields");
        }
        return document.getAsJsonObject("fields");
    }
}