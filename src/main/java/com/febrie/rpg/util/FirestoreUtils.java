package com.febrie.rpg.util;

import com.google.cloud.firestore.DocumentSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Firestore 문서에서 값을 안전하게 가져오기 위한 유틸리티 클래스
 * null 체크를 통해 NullPointerException을 방지하고 기본값을 제공합니다.
 */
public final class FirestoreUtils {
    
    private FirestoreUtils() {
        // 유틸리티 클래스는 인스턴스화 방지
    }
    
    /**
     * DocumentSnapshot에서 int 값을 안전하게 가져옵니다.
     * @param doc DocumentSnapshot
     * @param field 필드 이름
     * @return 값이 없으면 0
     */
    public static int getInt(@NotNull DocumentSnapshot doc, @NotNull String field) {
        Long value = doc.getLong(field);
        return value != null ? value.intValue() : 0;
    }
    
    /**
     * DocumentSnapshot에서 int 값을 안전하게 가져옵니다.
     * @param doc DocumentSnapshot
     * @param field 필드 이름
     * @param defaultValue 기본값
     * @return 값이 없으면 defaultValue
     */
    public static int getInt(@NotNull DocumentSnapshot doc, @NotNull String field, int defaultValue) {
        Long value = doc.getLong(field);
        return value != null ? value.intValue() : defaultValue;
    }
    
    /**
     * DocumentSnapshot에서 long 값을 안전하게 가져옵니다.
     * @param doc DocumentSnapshot
     * @param field 필드 이름
     * @return 값이 없으면 0L
     */
    public static long getLong(@NotNull DocumentSnapshot doc, @NotNull String field) {
        Long value = doc.getLong(field);
        return value != null ? value : 0L;
    }
    
    /**
     * DocumentSnapshot에서 long 값을 안전하게 가져옵니다.
     * @param doc DocumentSnapshot
     * @param field 필드 이름
     * @param defaultValue 기본값
     * @return 값이 없으면 defaultValue
     */
    public static long getLong(@NotNull DocumentSnapshot doc, @NotNull String field, long defaultValue) {
        Long value = doc.getLong(field);
        return value != null ? value : defaultValue;
    }
    
    /**
     * DocumentSnapshot에서 double 값을 안전하게 가져옵니다.
     * @param doc DocumentSnapshot
     * @param field 필드 이름
     * @return 값이 없으면 0.0
     */
    public static double getDouble(@NotNull DocumentSnapshot doc, @NotNull String field) {
        Double value = doc.getDouble(field);
        return value != null ? value : 0.0;
    }
    
    /**
     * DocumentSnapshot에서 double 값을 안전하게 가져옵니다.
     * @param doc DocumentSnapshot
     * @param field 필드 이름
     * @param defaultValue 기본값
     * @return 값이 없으면 defaultValue
     */
    public static double getDouble(@NotNull DocumentSnapshot doc, @NotNull String field, double defaultValue) {
        Double value = doc.getDouble(field);
        return value != null ? value : defaultValue;
    }
    
    /**
     * DocumentSnapshot에서 float 값을 안전하게 가져옵니다.
     * @param doc DocumentSnapshot
     * @param field 필드 이름
     * @return 값이 없으면 0.0f
     */
    public static float getFloat(@NotNull DocumentSnapshot doc, @NotNull String field) {
        Double value = doc.getDouble(field);
        return value != null ? value.floatValue() : 0.0f;
    }
    
    /**
     * DocumentSnapshot에서 float 값을 안전하게 가져옵니다.
     * @param doc DocumentSnapshot
     * @param field 필드 이름
     * @param defaultValue 기본값
     * @return 값이 없으면 defaultValue
     */
    public static float getFloat(@NotNull DocumentSnapshot doc, @NotNull String field, float defaultValue) {
        Double value = doc.getDouble(field);
        return value != null ? value.floatValue() : defaultValue;
    }
    
    /**
     * DocumentSnapshot에서 boolean 값을 안전하게 가져옵니다.
     * @param doc DocumentSnapshot
     * @param field 필드 이름
     * @return 값이 없으면 false
     */
    public static boolean getBoolean(@NotNull DocumentSnapshot doc, @NotNull String field) {
        Boolean value = doc.getBoolean(field);
        return value != null ? value : false;
    }
    
    /**
     * DocumentSnapshot에서 boolean 값을 안전하게 가져옵니다.
     * @param doc DocumentSnapshot
     * @param field 필드 이름
     * @param defaultValue 기본값
     * @return 값이 없으면 defaultValue
     */
    public static boolean getBoolean(@NotNull DocumentSnapshot doc, @NotNull String field, boolean defaultValue) {
        Boolean value = doc.getBoolean(field);
        return value != null ? value : defaultValue;
    }
    
    /**
     * DocumentSnapshot에서 String 값을 안전하게 가져옵니다.
     * @param doc DocumentSnapshot
     * @param field 필드 이름
     * @return 값이 없으면 빈 문자열
     */
    @NotNull
    public static String getString(@NotNull DocumentSnapshot doc, @NotNull String field) {
        String value = doc.getString(field);
        return value != null ? value : "";
    }
    
    /**
     * DocumentSnapshot에서 String 값을 안전하게 가져옵니다.
     * @param doc DocumentSnapshot
     * @param field 필드 이름
     * @param defaultValue 기본값
     * @return 값이 없으면 defaultValue
     */
    @NotNull
    public static String getString(@NotNull DocumentSnapshot doc, @NotNull String field, @NotNull String defaultValue) {
        String value = doc.getString(field);
        return value != null ? value : defaultValue;
    }
    
    // Map에서 값을 가져오는 메소드들
    
    /**
     * Map에서 int 값을 안전하게 가져옵니다.
     * @param map Map
     * @param key 키
     * @return 값이 없으면 0
     */
    public static int getInt(@NotNull Map<String, Object> map, @NotNull String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }
    
    /**
     * Map에서 int 값을 안전하게 가져옵니다.
     * @param map Map
     * @param key 키
     * @param defaultValue 기본값
     * @return 값이 없으면 defaultValue
     */
    public static int getInt(@NotNull Map<String, Object> map, @NotNull String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
    
    /**
     * Map에서 long 값을 안전하게 가져옵니다.
     * @param map Map
     * @param key 키
     * @return 값이 없으면 0L
     */
    public static long getLong(@NotNull Map<String, Object> map, @NotNull String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }
    
    /**
     * Map에서 long 값을 안전하게 가져옵니다.
     * @param map Map
     * @param key 키
     * @param defaultValue 기본값
     * @return 값이 없으면 defaultValue
     */
    public static long getLong(@NotNull Map<String, Object> map, @NotNull String key, long defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return defaultValue;
    }
    
    /**
     * Map에서 double 값을 안전하게 가져옵니다.
     * @param map Map
     * @param key 키
     * @return 값이 없으면 0.0
     */
    public static double getDouble(@NotNull Map<String, Object> map, @NotNull String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
    
    /**
     * Map에서 double 값을 안전하게 가져옵니다.
     * @param map Map
     * @param key 키
     * @param defaultValue 기본값
     * @return 값이 없으면 defaultValue
     */
    public static double getDouble(@NotNull Map<String, Object> map, @NotNull String key, double defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }
    
    /**
     * Map에서 float 값을 안전하게 가져옵니다.
     * @param map Map
     * @param key 키
     * @return 값이 없으면 0.0f
     */
    public static float getFloat(@NotNull Map<String, Object> map, @NotNull String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return 0.0f;
    }
    
    /**
     * Map에서 float 값을 안전하게 가져옵니다.
     * @param map Map
     * @param key 키
     * @param defaultValue 기본값
     * @return 값이 없으면 defaultValue
     */
    public static float getFloat(@NotNull Map<String, Object> map, @NotNull String key, float defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return defaultValue;
    }
    
    /**
     * Map에서 boolean 값을 안전하게 가져옵니다.
     * @param map Map
     * @param key 키
     * @return 값이 없으면 false
     */
    public static boolean getBoolean(@NotNull Map<String, Object> map, @NotNull String key) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return false;
    }
    
    /**
     * Map에서 boolean 값을 안전하게 가져옵니다.
     * @param map Map
     * @param key 키
     * @param defaultValue 기본값
     * @return 값이 없으면 defaultValue
     */
    public static boolean getBoolean(@NotNull Map<String, Object> map, @NotNull String key, boolean defaultValue) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }
    
    /**
     * Map에서 String 값을 안전하게 가져옵니다.
     * @param map Map
     * @param key 키
     * @return 값이 없으면 빈 문자열
     */
    @NotNull
    public static String getString(@NotNull Map<String, Object> map, @NotNull String key) {
        Object value = map.get(key);
        if (value instanceof String) {
            return (String) value;
        }
        return "";
    }
    
    /**
     * Map에서 String 값을 안전하게 가져옵니다 (null 가능).
     * @param map Map
     * @param key 키
     * @param defaultValue 기본값 (null 가능)
     * @return 값이 없으면 defaultValue
     */
    @Nullable
    public static String getString(@NotNull Map<String, Object> map, @NotNull String key, @Nullable String defaultValue) {
        Object value = map.get(key);
        if (value instanceof String) {
            return (String) value;
        }
        return defaultValue;
    }
    
    /**
     * Map에서 Map 값을 안전하게 가져옵니다.
     * @param map Map
     * @param key 키
     * @param defaultValue 기본값 (null 가능)
     * @return 값이 없으면 defaultValue
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static Map<String, Object> getMap(@NotNull Map<String, Object> map, @NotNull String key, @Nullable Map<String, Object> defaultValue) {
        Object value = map.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return defaultValue;
    }
    
    /**
     * Map에서 List 값을 안전하게 가져옵니다.
     * @param map Map
     * @param key 키
     * @param defaultValue 기본값
     * @return 값이 없으면 defaultValue
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> List<T> getList(@NotNull Map<String, Object> map, @NotNull String key, @NotNull List<T> defaultValue) {
        Object value = map.get(key);
        if (value instanceof List) {
            return (List<T>) value;
        }
        return defaultValue;
    }
    
    /**
     * Map에서 Long 값을 안전하게 가져옵니다 (null 가능).
     * @param map Map
     * @param key 키
     * @param defaultValue 기본값 (null 가능)
     * @return 값이 없으면 defaultValue
     */
    @Nullable
    public static Long getLong(@NotNull Map<String, Object> map, @NotNull String key, @Nullable Long defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return defaultValue;
    }
    
    /**
     * Map에서 nullable Long 값을 안전하게 가져옵니다.
     * @param map Map
     * @param key 키
     * @return 값이 없거나 null이면 null
     */
    @Nullable
    public static Long getLongOrNull(@NotNull Map<String, Object> map, @NotNull String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }
}