package com.febrie.rpg.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Firestore 문서 파싱 유틸리티
 * 
 * @author Febrie, CoffeeTory
 */
public final class FirestoreUtil {
    
    private static final Gson gson = new Gson();
    
    private FirestoreUtil() {
        // 유틸리티 클래스
    }
    
    /**
     * Firestore 문서를 지정된 타입으로 파싱
     *
     * @param document Firestore 문서 Map
     * @param clazz    대상 클래스
     * @param <T>      대상 타입
     * @return 파싱된 객체 또는 null
     */
    @Nullable
    public static <T> T parseFirestoreDocument(@NotNull Map<String, Object> document, @NotNull Class<T> clazz) {
        if (document.isEmpty()) {
            return null;
        }
        
        try {
            String json = gson.toJson(document);
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            System.err.println("Failed to parse Firestore document to " + clazz.getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 객체를 Firestore 저장용 Map으로 변환
     *
     * @param obj 변환할 객체
     * @return 변환된 Map
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static Map<String, Object> convertToMap(@NotNull Object obj) {
        String json = gson.toJson(obj);
        return gson.fromJson(json, Map.class);
    }
    
    /**
     * 안전한 타입 캐스팅
     *
     * @param obj   캐스팅할 객체
     * @param clazz 대상 클래스
     * @param <T>   대상 타입
     * @return 캐스팅된 객체 또는 null
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T safeCast(@Nullable Object obj, @NotNull Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        
        if (clazz.isInstance(obj)) {
            return (T) obj;
        }
        
        return null;
    }
    
    /**
     * Firestore 문서에서 필드 추출
     *
     * @param document Firestore 문서
     * @param field    필드명
     * @param clazz    대상 클래스
     * @param <T>      대상 타입
     * @return 추출된 값 또는 null
     */
    @Nullable
    public static <T> T getField(@NotNull Map<String, Object> document, @NotNull String field, @NotNull Class<T> clazz) {
        Object value = document.get(field);
        return safeCast(value, clazz);
    }
    
    /**
     * Firestore 문서에서 필드 추출 (기본값 지원)
     *
     * @param document     Firestore 문서
     * @param field        필드명
     * @param clazz        대상 클래스
     * @param defaultValue 기본값
     * @param <T>          대상 타입
     * @return 추출된 값 또는 기본값
     */
    @NotNull
    public static <T> T getField(@NotNull Map<String, Object> document, @NotNull String field, 
                                 @NotNull Class<T> clazz, @NotNull T defaultValue) {
        T value = getField(document, field, clazz);
        return value != null ? value : defaultValue;
    }
    
    /**
     * 중첩된 필드 추출
     *
     * @param document Firestore 문서
     * @param path     필드 경로 (점으로 구분)
     * @param clazz    대상 클래스
     * @param <T>      대상 타입
     * @return 추출된 값 또는 null
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getNestedField(@NotNull Map<String, Object> document, @NotNull String path, @NotNull Class<T> clazz) {
        String[] parts = path.split("\\.");
        Object current = document;
        
        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(part);
            } else {
                return null;
            }
            
            if (current == null) {
                return null;
            }
        }
        
        return safeCast(current, clazz);
    }
}