package com.febrie.rpg.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO 변환을 위한 유틸리티 클래스
 * Map 변환 등의 중복 코드를 제거하기 위한 공통 메소드 제공
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
        if (value instanceof Long) {
            return (Long) value;
        }
        return null;
    }

    /**
     * Object를 Integer로 안전하게 변환
     */
    @Nullable
    public static Integer toInteger(@Nullable Object value) {
        if (value instanceof Long) {
            return ((Long) value).intValue();
        } else if (value instanceof Integer) {
            return (Integer) value;
        }
        return null;
    }

    /**
     * Object를 Double로 안전하게 변환
     */
    @Nullable
    public static Double toDouble(@Nullable Object value) {
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        }
        return null;
    }

    /**
     * Object를 String으로 안전하게 변환
     */
    @Nullable
    public static String toString(@Nullable Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

    /**
     * Map<String, Object>를 Map<String, Integer>로 변환
     * DTO의 스탯, 특성 레벨 등에 사용
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
     * DTO의 시간 관련 데이터에 사용
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
     * null이 아닌 경우에만 값을 설정하는 헬퍼 메소드
     */
    public static void setIfNotNull(@NotNull Map<String, Object> map, @NotNull String key, @Nullable Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    /**
     * Map에서 값을 가져와서 Long으로 변환 후 설정
     */
    public static void setLongFromMap(@NotNull Map<String, Object> map, @NotNull String key,
                                      @NotNull java.util.function.Consumer<Long> setter) {
        Long value = toLong(map.get(key));
        if (value != null) {
            setter.accept(value);
        }
    }

    /**
     * Map에서 값을 가져와서 Integer로 변환 후 설정
     */
    public static void setIntFromMap(@NotNull Map<String, Object> map, @NotNull String key,
                                     @NotNull java.util.function.Consumer<Integer> setter) {
        Integer value = toInteger(map.get(key));
        if (value != null) {
            setter.accept(value);
        }
    }
}