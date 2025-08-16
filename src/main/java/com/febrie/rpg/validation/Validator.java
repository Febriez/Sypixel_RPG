package com.febrie.rpg.validation;

import com.febrie.rpg.exception.RPGException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * 통합 검증 유틸리티
 * 모든 검증 로직을 중앙화
 *
 * @author Febrie, CoffeeTory
 */
public class Validator {
    
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /**
     * 널 체크
     */
    public static <T> T requireNonNull(@Nullable T obj, @NotNull String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
        return obj;
    }
    
    /**
     * 빈 문자열 체크
     */
    @NotNull
    public static String requireNonEmpty(@Nullable String str, @NotNull String name) {
        requireNonNull(str, name);
        if (str.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " cannot be empty");
        }
        return str;
    }
    
    /**
     * 숫자 범위 체크
     */
    public static int requireInRange(int value, int min, int max, @NotNull String name) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                String.format("%s must be between %d and %d, but was %d", name, min, max, value)
            );
        }
        return value;
    }
    
    /**
     * 양수 체크
     */
    public static int requirePositive(int value, @NotNull String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive, but was " + value);
        }
        return value;
    }
    
    /**
     * 음수가 아닌 수 체크
     */
    public static int requireNonNegative(int value, @NotNull String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative, but was " + value);
        }
        return value;
    }
    
    /**
     * UUID 형식 검증
     */
    public static boolean isValidUUID(@Nullable String uuid) {
        return uuid != null && UUID_PATTERN.matcher(uuid).matches();
    }
    
    /**
     * 플레이어 이름 검증
     */
    public static boolean isValidPlayerName(@Nullable String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }
    
    /**
     * 이메일 검증
     */
    public static boolean isValidEmail(@Nullable String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * 컬렉션 크기 검증
     */
    public static <T> List<T> requireSize(@NotNull List<T> list, int minSize, int maxSize, @NotNull String name) {
        requireNonNull(list, name);
        int size = list.size();
        if (size < minSize || size > maxSize) {
            throw new IllegalArgumentException(
                String.format("%s size must be between %d and %d, but was %d", name, minSize, maxSize, size)
            );
        }
        return list;
    }
    
    /**
     * 조건 검증
     */
    public static void require(boolean condition, @NotNull String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * 빌더 패턴 검증
     */
    public static class ValidationBuilder<T> {
        private final T value;
        private final String name;
        private final List<String> errors = new ArrayList<>();
        
        public ValidationBuilder(@Nullable T value, @NotNull String name) {
            this.value = value;
            this.name = name;
        }
        
        public ValidationBuilder<T> notNull() {
            if (value == null) {
                errors.add(name + " cannot be null");
            }
            return this;
        }
        
        public ValidationBuilder<T> test(@NotNull Predicate<T> predicate, @NotNull String errorMessage) {
            if (value != null && !predicate.test(value)) {
                errors.add(name + ": " + errorMessage);
            }
            return this;
        }
        
        public ValidationBuilder<T> equalsValue(@NotNull T expected) {
            if (!Objects.equals(value, expected)) {
                errors.add(name + " must be " + expected + " but was " + value);
            }
            return this;
        }
        
        public T validate() throws ValidationException {
            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }
            return value;
        }
        
        public boolean isValid() {
            return errors.isEmpty();
        }
        
        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }
    }
    
    /**
     * 검증 예외
     */
    public static class ValidationException extends Exception {
        private final List<String> errors;
        
        public ValidationException(@NotNull List<String> errors) {
            super("Validation failed: " + String.join(", ", errors));
            this.errors = new ArrayList<>(errors);
        }
        
        public List<String> getErrors() {
            return errors;
        }
    }
    
    /**
     * 빌더 생성 헬퍼
     */
    public static <T> ValidationBuilder<T> validate(@Nullable T value, @NotNull String name) {
        return new ValidationBuilder<>(value, name);
    }
}