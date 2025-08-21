package com.febrie.rpg.util;

import org.jetbrains.annotations.NotNull;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 통합 시간 처리 유틸리티
 * 모든 시간 처리를 Instant 기반으로 표준화
 *
 * @author Febrie, CoffeeTory
 */
public class UnifiedTimeUtil {

    private UnifiedTimeUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Seoul");

    // 표준 포맷터
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter KOREAN_FORMAT = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
    private static final DateTimeFormatter SHORT_FORMAT = DateTimeFormatter.ofPattern("MM/dd HH:mm");

    /**
     * 현재 시간 (Instant)
     */
    @NotNull
    public static Instant now() {
        return Instant.now();
    }

    /**
     * 현재 시간 (밀리초)
     */
    public static long nowMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 밀리초를 Instant로 변환
     */
    @NotNull
    public static Instant fromMillis(long millis) {
        return Instant.ofEpochMilli(millis);
    }

    /**
     * Instant를 밀리초로 변환
     */
    public static long toMillis(@NotNull Instant instant) {
        return instant.toEpochMilli();
    }

    /**
     * LocalDateTime을 Instant로 변환
     */
    @NotNull
    public static Instant toInstant(@NotNull LocalDateTime dateTime) {
        return dateTime.atZone(DEFAULT_ZONE).toInstant();
    }

    /**
     * Instant를 LocalDateTime으로 변환
     */
    @NotNull
    public static LocalDateTime toLocalDateTime(@NotNull Instant instant) {
        return LocalDateTime.ofInstant(instant, DEFAULT_ZONE);
    }

    /**
     * 날짜만 포맷
     */
    @NotNull
    public static String formatDate(@NotNull Instant instant) {
        return DATE_FORMAT.format(toLocalDateTime(instant));
    }

    /**
     * 시간만 포맷
     */
    @NotNull
    public static String formatTime(@NotNull Instant instant) {
        return TIME_FORMAT.format(toLocalDateTime(instant));
    }

    /**
     * 날짜와 시간 포맷
     */
    @NotNull
    public static String formatDateTime(@NotNull Instant instant) {
        return DATETIME_FORMAT.format(toLocalDateTime(instant));
    }

    /**
     * 한국어 포맷
     */
    @NotNull
    public static String formatKorean(@NotNull Instant instant) {
        return KOREAN_FORMAT.format(toLocalDateTime(instant));
    }

    /**
     * 짧은 포맷
     */
    @NotNull
    public static String formatShort(@NotNull Instant instant) {
        return SHORT_FORMAT.format(toLocalDateTime(instant));
    }

    /**
     * 상대 시간 표시 (예: "3분 전", "2시간 전")
     */
    @NotNull
    public static String formatRelative(@NotNull Instant instant) {
        Instant now = Instant.now();
        long seconds = ChronoUnit.SECONDS.between(instant, now);

        if (seconds < 0) {
            return formatFuture(-seconds);
        }

        if (seconds < 60) {
            return seconds + "초 전";
        } else if (seconds < 3600) {
            return (seconds / 60) + "분 전";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "시간 전";
        } else if (seconds < 2592000) {
            return (seconds / 86400) + "일 전";
        } else if (seconds < 31536000) {
            return (seconds / 2592000) + "개월 전";
        } else {
            return (seconds / 31536000) + "년 전";
        }
    }

    /**
     * 미래 시간 표시
     */
    @NotNull
    private static String formatFuture(long seconds) {
        if (seconds < 60) {
            return seconds + "초 후";
        } else if (seconds < 3600) {
            return (seconds / 60) + "분 후";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "시간 후";
        } else if (seconds < 2592000) {
            return (seconds / 86400) + "일 후";
        } else if (seconds < 31536000) {
            return (seconds / 2592000) + "개월 후";
        } else {
            return (seconds / 31536000) + "년 후";
        }
    }

    /**
     * 지속 시간 포맷 (예: "1시간 23분 45초")
     */
    @NotNull
    public static String formatDuration(long millis) {
        if (millis < 0) {
            return "0초";
        }

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(days).append("일 ");
        }
        if (hours % 24 > 0) {
            result.append(hours % 24).append("시간 ");
        }
        if (minutes % 60 > 0) {
            result.append(minutes % 60).append("분 ");
        }
        if (seconds % 60 > 0 || result.isEmpty()) {
            result.append(seconds % 60).append("초");
        }

        return result.toString().trim();
    }

    /**
     * 두 시간 사이의 차이 (밀리초)
     */
    public static long diff(@NotNull Instant from, @NotNull Instant to) {
        return ChronoUnit.MILLIS.between(from, to);
    }

    /**
     * 시간 추가
     */
    @NotNull
    public static Instant plusDays(@NotNull Instant instant, long days) {
        return instant.plus(days, ChronoUnit.DAYS);
    }

    @NotNull
    public static Instant plusHours(@NotNull Instant instant, long hours) {
        return instant.plus(hours, ChronoUnit.HOURS);
    }

    @NotNull
    public static Instant plusMinutes(@NotNull Instant instant, long minutes) {
        return instant.plus(minutes, ChronoUnit.MINUTES);
    }

    /**
     * 오늘 시작 시간
     */
    @NotNull
    public static Instant startOfDay() {
        return LocalDate.now(DEFAULT_ZONE).atStartOfDay(DEFAULT_ZONE).toInstant();
    }

    /**
     * 오늘 종료 시간
     */
    @NotNull
    public static Instant endOfDay() {
        return LocalDate.now(DEFAULT_ZONE).atTime(23, 59, 59).atZone(DEFAULT_ZONE).toInstant();
    }

    /**
     * 이번 주 시작 시간
     */
    @NotNull
    public static Instant startOfWeek() {
        return LocalDate.now(DEFAULT_ZONE).with(DayOfWeek.MONDAY).atStartOfDay(DEFAULT_ZONE).toInstant();
    }

    /**
     * 이번 달 시작 시간
     */
    @NotNull
    public static Instant startOfMonth() {
        return LocalDate.now(DEFAULT_ZONE).withDayOfMonth(1).atStartOfDay(DEFAULT_ZONE).toInstant();
    }
}