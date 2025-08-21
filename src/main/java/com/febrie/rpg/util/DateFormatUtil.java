package com.febrie.rpg.util;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 날짜 포맷팅 유틸리티 클래스
 * 프로젝트 전체에서 일관된 날짜 형식을 사용하기 위한 중앙화된 포맷터
 *
 * @author Febrie, CoffeeTory
 */
public final class DateFormatUtil {

    // Common formatters
    public static final DateTimeFormatter FULL_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter SHORT_DATE_TIME = DateTimeFormatter.ofPattern("MM-dd HH:mm");
    public static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_ONLY = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter SLASH_DATE_TIME = DateTimeFormatter.ofPattern("MM/dd HH:mm");
    public static final DateTimeFormatter FULL_DATE_TIME_SEC = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Private constructor to prevent instantiation
    private DateFormatUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 전체 날짜/시간 포맷팅 (yyyy-MM-dd HH:mm)
     */
    @NotNull
    public static String formatFullDateTime(@NotNull LocalDateTime dateTime) {
        return dateTime.format(FULL_DATE_TIME);
    }

    /**
     * 짧은 날짜/시간 포맷팅 (MM-dd HH:mm)
     */
    @NotNull
    public static String formatShortDateTime(@NotNull LocalDateTime dateTime) {
        return dateTime.format(SHORT_DATE_TIME);
    }

    /**
     * 날짜만 포맷팅 (yyyy-MM-dd)
     */
    @NotNull
    public static String formatDateOnly(@NotNull LocalDateTime dateTime) {
        return dateTime.format(DATE_ONLY);
    }

    /**
     * 시간만 포맷팅 (HH:mm)
     */
    @NotNull
    public static String formatTimeOnly(@NotNull LocalDateTime dateTime) {
        return dateTime.format(TIME_ONLY);
    }

    /**
     * 밀리초를 지정된 포맷터로 변환
     */
    @NotNull
    public static String formatFromMillis(long millis, @NotNull DateTimeFormatter formatter) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()).format(formatter);
    }

    /**
     * 밀리초를 전체 날짜/시간으로 포맷팅
     */
    @NotNull
    public static String formatFullDateTimeFromMillis(long millis) {
        return formatFromMillis(millis, FULL_DATE_TIME);
    }

    /**
     * 밀리초를 짧은 날짜/시간으로 포맷팅
     */
    @NotNull
    public static String formatShortDateTimeFromMillis(long millis) {
        return formatFromMillis(millis, SHORT_DATE_TIME);
    }

    /**
     * 밀리초를 날짜만으로 포맷팅
     */
    @NotNull
    public static String formatDateOnlyFromMillis(long millis) {
        return formatFromMillis(millis, DATE_ONLY);
    }

    /**
     * 밀리초를 시간만으로 포맷팅
     */
    @NotNull
    public static String formatTimeOnlyFromMillis(long millis) {
        return formatFromMillis(millis, TIME_ONLY);
    }

    /**
     * 밀리초를 슬래시 형식 날짜/시간으로 포맷팅
     */
    @NotNull
    public static String formatSlashDateTimeFromMillis(long millis) {
        return formatFromMillis(millis, SLASH_DATE_TIME);
    }

    /**
     * 밀리초를 LocalDateTime으로 변환
     */
    @NotNull
    public static LocalDateTime toLocalDateTime(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }
}