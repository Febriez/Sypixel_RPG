package com.febrie.rpg.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 중앙 집중식 로깅 유틸리티
 * Bukkit.getLogger()의 @ApiStatus.Internal 경고를 방지하고
 * 통일된 로깅 인터페이스를 제공
 *
 * @author Febrie, CoffeeTory
 */
public final class LogUtil {

    private static Logger logger;
    private static String prefix = "[SypixelRPG]";
    private static boolean debugMode = false;

    private LogUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 로거 초기화
     */
    public static void initialize(@NotNull Plugin plugin) {
        logger = plugin.getLogger();
        prefix = "[" + plugin.getName() + "]";
    }

    /**
     * 디버그 모드 설정
     */
    public static void setDebugMode(boolean debug) {
        debugMode = debug;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    /**
     * 정보 로그
     */
    public static void info(@NotNull String message) {
        if (logger != null) {
            logger.info(message);
        } else {
            Bukkit.getConsoleSender().sendMessage(prefix + " [INFO] " + message);
        }
    }

    /**
     * 경고 로그
     */
    public static void warning(@NotNull String message) {
        if (logger != null) {
            logger.warning(message);
        } else {
            Bukkit.getConsoleSender().sendMessage(prefix + " §e[WARNING] " + message);
        }
    }

    /**
     * 에러 로그
     */
    public static void error(@NotNull String message) {
        if (logger != null) {
            logger.severe(message);
        } else {
            Bukkit.getConsoleSender().sendMessage(prefix + " §c[ERROR] " + message);
        }
    }

    /**
     * 에러 로그 (예외 포함)
     */
    public static void error(@NotNull String message, @NotNull Throwable throwable) {
        error(message + ": " + throwable.getMessage());
        if (logger != null) {
            logger.log(Level.SEVERE, message, throwable);
        } else {
            throwable.printStackTrace();
        }
    }

    /**
     * 디버그 로그 (디버그 모드일 때만 출력)
     */
    public static void debug(@NotNull String message) {
        if (debugMode) {
            if (logger != null) {
                logger.info("[DEBUG] " + message);
            } else {
                Bukkit.getConsoleSender().sendMessage(prefix + " §7[DEBUG] " + message);
            }
        }
    }

    /**
     * 포맷된 정보 로그
     */
    public static void info(@NotNull String format, @NotNull Object... args) {
        info(String.format(format, args));
    }

    /**
     * 포맷된 경고 로그
     */
    public static void warning(@NotNull String format, @NotNull Object... args) {
        warning(String.format(format, args));
    }

    /**
     * 포맷된 에러 로그
     */
    public static void error(@NotNull String format, @NotNull Object... args) {
        error(String.format(format, args));
    }

    /**
     * 포맷된 디버그 로그
     */
    public static void debug(@NotNull String format, @NotNull Object... args) {
        debug(String.format(format, args));
    }

    /**
     * warn 메소드 (warning과 동일)
     */
    public static void warn(@NotNull String message) {
        warning(message);
    }

    /**
     * 성능 측정용 로그
     */
    public static void logPerformance(@NotNull String operation, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        debug("Performance: " + operation + " took " + duration + "ms");
    }
}