package com.febrie.rpg.dto;

import com.febrie.rpg.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * DTO 변환을 위한 향상된 유틸리티 클래스
 * 타입 안전성과 성능을 고려한 변환 메소드 제공
 *
 * @author Febrie, CoffeeTory
 */
public final class DTOUtil {

    private DTOUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Object를 Long으로 안전하게 변환
     */
    @Nullable
    public static Long toLong(@Nullable Object value) {
        switch (value) {
            case null -> {
                return null;
            }
            case Long l -> {
                return l;
            }
            case Integer i -> {
                return i.longValue();
            }
            case String s -> {
                try {
                    return Long.parseLong(s);
                } catch (NumberFormatException e) {
                    LogUtil.debug("Long 변환 실패: " + value);
                }
            }
            default -> {
            }
        }

        return null;
    }

    /**
     * Object를 Integer로 안전하게 변환
     */
    @Nullable
    public static Integer toInteger(@Nullable Object value) {
        switch (value) {
            case null -> {
                return null;
            }
            case int i -> {
                return i;
            }
            case long longValue -> {
                if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                    return (int) longValue;
                } else {
                    LogUtil.warning("Integer 범위 초과: " + longValue);
                }
            }
            case String s -> {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    LogUtil.debug("Integer 변환 실패: " + value);
                }
            }
            default -> {
            }
        }

        return null;
    }

    /**
     * Object를 Double로 안전하게 변환
     */
    @Nullable
    public static Double toDouble(@Nullable Object value) {
        switch (value) {
            case null -> {
                return null;
            }
            case Double v -> {
                return v;
            }
            case Float v -> {
                return v.doubleValue();
            }
            case Long l -> {
                return l.doubleValue();
            }
            case Integer i -> {
                return i.doubleValue();
            }
            case String s -> {
                try {
                    return Double.parseDouble(s);
                } catch (NumberFormatException e) {
                    LogUtil.debug("Double 변환 실패: " + value);
                }
            }
            default -> {
            }
        }

        return null;
    }

    /**
     * Object를 String으로 안전하게 변환
     */
    @Nullable
    public static String toString(@Nullable Object value) {
        if (value == null) return null;

        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    /**
     * Object를 Boolean으로 안전하게 변환
     */
    @Nullable
    public static Boolean toBoolean(@Nullable Object value) {
        switch (value) {
            case null -> {
                return null;
            }
            case Boolean b -> {
                return b;
            }
            case String s -> {
                String strValue = s.toLowerCase();
                if ("true".equals(strValue) || "1".equals(strValue)) {
                    return true;
                } else if ("false".equals(strValue) || "0".equals(strValue)) {
                    return false;
                }
            }
            case Number number -> {
                return number.intValue() != 0;
            }
            default -> {
            }
        }

        return null;
    }

    /**
     * Map<String, Object>를 Map<String, Integer>로 변환
     */
    @NotNull
    public static Map<String, Integer> toIntegerMap(@Nullable Object mapObject) {
        Map<String, Integer> result = new HashMap<>();

        if (!(mapObject instanceof Map)) {
            return result;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) mapObject;

        map.forEach((key, value) -> {
            Integer intValue = toInteger(value);
            if (intValue != null) {
                result.put(key, intValue);
            }
        });

        return result;
    }

    /**
     * Map<String, Object>를 Map<String, Long>으로 변환
     */
    @NotNull
    public static Map<String, Long> toLongMap(@Nullable Object mapObject) {
        Map<String, Long> result = new HashMap<>();

        if (!(mapObject instanceof Map)) {
            return result;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) mapObject;

        map.forEach((key, value) -> {
            Long longValue = toLong(value);
            if (longValue != null) {
                result.put(key, longValue);
            }
        });

        return result;
    }

    /**
     * Map<String, Object>를 Map<String, String>으로 변환
     */
    @NotNull
    public static Map<String, String> toStringMap(@Nullable Object mapObject) {
        Map<String, String> result = new HashMap<>();

        if (!(mapObject instanceof Map)) {
            return result;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) mapObject;

        map.forEach((key, value) -> {
            String strValue = toString(value);
            if (strValue != null) {
                result.put(key, strValue);
            }
        });

        return result;
    }

    /**
     * null이 아닌 경우에만 값을 설정
     */
    public static void setIfNotNull(@NotNull Map<String, Object> map, @NotNull String key, @Nullable Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    /**
     * Map에서 Long 값을 가져와 설정
     */
    public static void setLongFromMap(@NotNull Map<String, Object> map, @NotNull String key,
                                      @NotNull Consumer<Long> setter) {
        Long value = toLong(map.get(key));
        if (value != null) {
            setter.accept(value);
        }
    }

    /**
     * Map에서 Integer 값을 가져와 설정
     */
    public static void setIntFromMap(@NotNull Map<String, Object> map, @NotNull String key,
                                     @NotNull Consumer<Integer> setter) {
        Integer value = toInteger(map.get(key));
        if (value != null) {
            setter.accept(value);
        }
    }

    /**
     * Map에서 Double 값을 가져와 설정
     */
    public static void setDoubleFromMap(@NotNull Map<String, Object> map, @NotNull String key,
                                        @NotNull Consumer<Double> setter) {
        Double value = toDouble(map.get(key));
        if (value != null) {
            setter.accept(value);
        }
    }

    /**
     * Map에서 String 값을 가져와 설정
     */
    public static void setStringFromMap(@NotNull Map<String, Object> map, @NotNull String key,
                                        @NotNull Consumer<String> setter) {
        String value = toString(map.get(key));
        if (value != null) {
            setter.accept(value);
        }
    }

    /**
     * Map에서 Boolean 값을 가져와 설정
     */
    public static void setBooleanFromMap(@NotNull Map<String, Object> map, @NotNull String key,
                                         @NotNull Consumer<Boolean> setter) {
        Boolean value = toBoolean(map.get(key));
        if (value != null) {
            setter.accept(value);
        }
    }

    /**
     * Map 병합 (기존 값 유지)
     */
    public static void mergeMap(@NotNull Map<String, Object> target, @NotNull Map<String, Object> source) {
        source.forEach(target::putIfAbsent);
    }

    /**
     * Map 병합 (기존 값 덮어쓰기)
     */
    public static void overwriteMap(@NotNull Map<String, Object> target, @NotNull Map<String, Object> source) {
        target.putAll(source);
    }

    /**
     * 안전한 Map 생성 (null 체크)
     */
    @NotNull
    public static Map<String, Object> safeMap(@Nullable Map<String, Object> map) {
        return map != null ? map : new HashMap<>();
    }

    /**
     * 기본값과 함께 Long 가져오기
     */
    public static long getLongOrDefault(@NotNull Map<String, Object> map, @NotNull String key, long defaultValue) {
        Long value = toLong(map.get(key));
        return value != null ? value : defaultValue;
    }

    /**
     * 기본값과 함께 Integer 가져오기
     */
    public static int getIntOrDefault(@NotNull Map<String, Object> map, @NotNull String key, int defaultValue) {
        Integer value = toInteger(map.get(key));
        return value != null ? value : defaultValue;
    }

    /**
     * 기본값과 함께 String 가져오기
     */
    @NotNull
    public static String getStringOrDefault(@NotNull Map<String, Object> map, @NotNull String key, @NotNull String defaultValue) {
        String value = toString(map.get(key));
        return value != null ? value : defaultValue;
    }

    /**
     * 기본값과 함께 Boolean 가져오기
     */
    public static boolean getBooleanOrDefault(@NotNull Map<String, Object> map, @NotNull String key, boolean defaultValue) {
        Boolean value = toBoolean(map.get(key));
        return value != null ? value : defaultValue;
    }
}