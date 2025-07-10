package com.febrie.rpg.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for time-related operations
 *
 * @author Febrie, CoffeeTory
 */
public final class TimeUtil {

    private TimeUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Formats milliseconds into a readable string (e.g., "1h 30m 45s")
     *
     * @param millis The milliseconds to format
     * @return Formatted time string
     */
    public static @NotNull String formatTime(long millis) {
        return formatTime(millis, true);
    }

    /**
     * Formats milliseconds into a readable string with optional short format
     *
     * @param millis      The milliseconds to format
     * @param shortFormat Whether to use short format (h,m,s) or long format (hours, minutes, seconds)
     * @return Formatted time string
     */
    public static @NotNull String formatTime(long millis, boolean shortFormat) {
        if (millis < 1000) {
            return shortFormat ? "0s" : "0 seconds";
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(days).append(shortFormat ? "d " : (days == 1 ? " day " : " days "));
        }
        if (hours > 0) {
            result.append(hours).append(shortFormat ? "h " : (hours == 1 ? " hour " : " hours "));
        }
        if (minutes > 0) {
            result.append(minutes).append(shortFormat ? "m " : (minutes == 1 ? " minute " : " minutes "));
        }
        if (seconds > 0) {
            result.append(seconds).append(shortFormat ? "s" : (seconds == 1 ? " second" : " seconds"));
        }

        return result.toString().trim();
    }

    /**
     * Formats a duration into a Component with color coding
     *
     * @param duration The duration to format
     * @return Colored component
     */
    public static @NotNull Component formatDurationColored(@NotNull Duration duration) {
        long totalSeconds = duration.getSeconds();

        TextColor color;
        if (totalSeconds < 10) {
            color = NamedTextColor.RED;
        } else if (totalSeconds < 30) {
            color = NamedTextColor.GOLD;
        } else if (totalSeconds < 60) {
            color = NamedTextColor.YELLOW;
        } else {
            color = NamedTextColor.GREEN;
        }

        return Component.text(formatTime(duration.toMillis()), color);
    }

    /**
     * Parses a time string (e.g., "1h30m", "2d", "45s") into milliseconds
     *
     * @param timeString The time string to parse
     * @return Milliseconds
     * @throws IllegalArgumentException if the format is invalid
     */
    public static long parseTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            throw new IllegalArgumentException("Time string cannot be null or empty");
        }

        timeString = timeString.toLowerCase().replaceAll("\\s+", "");
        long totalMillis = 0;

        // Pattern matches: number followed by unit (d/h/m/s)
        String pattern = "(\\d+)([dhms])";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(timeString);

        while (m.find()) {
            long value = Long.parseLong(m.group(1));
            String unit = m.group(2);

            switch (unit) {
                case "d" -> totalMillis += TimeUnit.DAYS.toMillis(value);
                case "h" -> totalMillis += TimeUnit.HOURS.toMillis(value);
                case "m" -> totalMillis += TimeUnit.MINUTES.toMillis(value);
                case "s" -> totalMillis += TimeUnit.SECONDS.toMillis(value);
            }
        }

        if (totalMillis == 0) {
            throw new IllegalArgumentException("Invalid time format: " + timeString);
        }

        return totalMillis;
    }

    /**
     * Gets the time remaining until a specific timestamp
     *
     * @param targetMillis The target timestamp in milliseconds
     * @return Duration remaining
     */
    public static Duration getTimeUntil(long targetMillis) {
        long now = System.currentTimeMillis();
        long diff = targetMillis - now;
        return Duration.ofMillis(Math.max(0, diff));
    }

    /**
     * Gets the time elapsed since a specific timestamp
     *
     * @param startMillis The start timestamp in milliseconds
     * @return Duration elapsed
     */
    public static Duration getTimeSince(long startMillis) {
        long now = System.currentTimeMillis();
        long diff = now - startMillis;
        return Duration.ofMillis(Math.max(0, diff));
    }

    /**
     * Formats a timestamp into a readable date string
     *
     * @param millis  The timestamp in milliseconds
     * @param pattern The date pattern (e.g., "yyyy-MM-dd HH:mm:ss")
     * @return Formatted date string
     */
    public static @NotNull String formatDate(long millis, String pattern) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(millis),
                ZoneId.systemDefault()
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * Formats a timestamp into a standard date string
     *
     * @param millis The timestamp in milliseconds
     * @return Formatted date string in "yyyy-MM-dd HH:mm:ss" format
     */
    public static @NotNull String formatDate(long millis) {
        return formatDate(millis, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Creates a countdown component that shows remaining time
     *
     * @param endTime The end time in milliseconds
     * @return Component showing remaining time
     */
    public static Component createCountdown(long endTime) {
        Duration remaining = getTimeUntil(endTime);
        if (remaining.isZero() || remaining.isNegative()) {
            return Component.text("Expired", NamedTextColor.RED);
        }
        return formatDurationColored(remaining);
    }

    /**
     * Checks if a cooldown has expired
     *
     * @param lastUsed       The last used timestamp
     * @param cooldownMillis The cooldown duration in milliseconds
     * @return true if cooldown has expired
     */
    public static boolean isCooldownExpired(long lastUsed, long cooldownMillis) {
        return System.currentTimeMillis() - lastUsed >= cooldownMillis;
    }

    /**
     * Gets the remaining cooldown time
     *
     * @param lastUsed       The last used timestamp
     * @param cooldownMillis The cooldown duration in milliseconds
     * @return Remaining cooldown in milliseconds, or 0 if expired
     */
    public static long getRemainingCooldown(long lastUsed, long cooldownMillis) {
        long elapsed = System.currentTimeMillis() - lastUsed;
        return Math.max(0, cooldownMillis - elapsed);
    }

    /**
     * Converts ticks to milliseconds (20 ticks = 1 second)
     *
     * @param ticks The number of ticks
     * @return Milliseconds
     */
    public static long ticksToMillis(long ticks) {
        return ticks * 50L;
    }

    /**
     * Converts milliseconds to ticks (20 ticks = 1 second)
     *
     * @param millis The milliseconds
     * @return Number of ticks
     */
    public static long millisToTicks(long millis) {
        return millis / 50L;
    }

    /**
     * Gets a human-readable "time ago" string
     *
     * @param millis The timestamp
     * @return String like "5 minutes ago", "2 hours ago"
     */
    public static @NotNull String getTimeAgo(long millis) {
        long diff = System.currentTimeMillis() - millis;

        if (diff < 1000) return "just now";
        if (diff < 60_000) return (diff / 1000) + " seconds ago";
        if (diff < 3_600_000) return (diff / 60_000) + " minutes ago";
        if (diff < 86_400_000) return (diff / 3_600_000) + " hours ago";
        if (diff < 2_592_000_000L) return (diff / 86_400_000) + " days ago";
        if (diff < 31_536_000_000L) return (diff / 2_592_000_000L) + " months ago";
        return (diff / 31_536_000_000L) + " years ago";
    }

    /**
     * Gets current timestamp in milliseconds
     *
     * @return Current timestamp
     */
    public static long now() {
        return System.currentTimeMillis();
    }

    /**
     * Creates a Component showing formatted time with a prefix
     *
     * @param prefix The prefix text
     * @param millis The time in milliseconds
     * @param color  The color to use
     * @return Formatted component
     */
    public static @NotNull Component formatTimeComponent(String prefix, long millis, TextColor color) {
        return Component.text(prefix, color)
                .append(Component.text(formatTime(millis), NamedTextColor.WHITE));
    }
}