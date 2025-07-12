package com.febrie.rpg.util;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 확장된 색상 유틸리티 클래스
 * NamedTextColor에 없는 커스텀 색상 제공 및 fromName 메소드 추가
 *
 * @author Febrie, CoffeeTory
 */
public final class ColorUtil {

    private ColorUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // === 기본 확장 색상 ===
    public static final TextColor ORANGE = TextColor.color(255, 165, 0);
    public static final TextColor PINK = TextColor.color(255, 192, 203);
    public static final TextColor BROWN = TextColor.color(139, 69, 19);
    public static final TextColor LIME = TextColor.color(50, 205, 50);
    public static final TextColor CYAN = TextColor.color(0, 255, 255);

    // === RPG 테마 색상 ===
    public static final TextColor LEGENDARY = TextColor.color(255, 215, 0);
    public static final TextColor EPIC = TextColor.color(163, 53, 238);
    public static final TextColor RARE = TextColor.color(0, 112, 221);
    public static final TextColor UNCOMMON = TextColor.color(30, 255, 0);
    public static final TextColor COMMON = TextColor.color(157, 157, 157);

    // === 상태 색상 ===
    public static final TextColor SUCCESS = TextColor.color(34, 139, 34);
    public static final TextColor WARNING = TextColor.color(255, 140, 0);
    public static final TextColor ERROR = TextColor.color(220, 20, 60);
    public static final TextColor INFO = TextColor.color(30, 144, 255);

    // === UI 색상 ===
    public static final TextColor MANA = TextColor.color(52, 152, 219);
    public static final TextColor HEALTH = TextColor.color(231, 76, 60);
    public static final TextColor EXPERIENCE = TextColor.color(241, 196, 15);
    public static final TextColor COINS = TextColor.color(255, 215, 0);

    // === 추가 색상 ===
    public static final TextColor GRAY = TextColor.color(128, 128, 128);
    public static final TextColor DARK_GRAY = TextColor.color(64, 64, 64);
    public static final TextColor LIGHT_GRAY = TextColor.color(192, 192, 192);
    public static final TextColor WHITE = TextColor.color(255, 255, 255);
    public static final TextColor BLACK = TextColor.color(0, 0, 0);

    // === 재료 색상 ===
    public static final TextColor DIAMOND = TextColor.color(185, 242, 255); // #B9F2FF
    public static final TextColor GOLD = TextColor.color(255, 215, 0); // #FFD700
    public static final TextColor NETHERITE = TextColor.color(68, 58, 69); // #443A45
    public static final TextColor COPPER = TextColor.color(184, 115, 51); // #B87333
    public static final TextColor EMERALD = TextColor.color(80, 200, 120); // #50C878

    // 색상 캐시 (성능 최적화)
    private static final Map<String, TextColor> COLOR_CACHE = new HashMap<>();

    static {
        // ColorUtil 색상 캐싱
        Field[] fields = ColorUtil.class.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) &&
                    Modifier.isFinal(field.getModifiers()) &&
                    field.getType().equals(TextColor.class)) {
                try {
                    TextColor color = (TextColor) field.get(null);
                    COLOR_CACHE.put(field.getName().toUpperCase(), color);
                } catch (IllegalAccessException e) {
                    // 무시
                }
            }
        }

        // NamedTextColor 색상 캐싱
        for (NamedTextColor namedColor : NamedTextColor.NAMES.values()) {
            COLOR_CACHE.put(namedColor.toString().toUpperCase(), namedColor);
        }
    }

    /**
     * 이름으로 색상 가져오기 (LangManager에서 사용)
     *
     * @param colorName 색상 이름 (대소문자 구분 없음)
     * @return TextColor 또는 null
     */
    @Nullable
    public static TextColor fromName(@NotNull String colorName) {
        return COLOR_CACHE.get(colorName.toUpperCase().replace(" ", "_"));
    }

    /**
     * HEX 코드로 색상 생성
     *
     * @param hex HEX 색상 코드 (# 제외)
     * @return TextColor
     */
    @NotNull
    public static TextColor fromHex(@NotNull String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        if (hex.length() != 6) {
            throw new IllegalArgumentException("Hex color must be 6 characters long");
        }

        try {
            int color = Integer.parseInt(hex, 16);
            return TextColor.color(color);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex color: " + hex, e);
        }
    }

    /**
     * 두 색상 간의 그라디언트 색상 생성
     *
     * @param start 시작 색상
     * @param end   끝 색상
     * @param ratio 비율 (0.0 = 시작, 1.0 = 끝)
     * @return 보간된 색상
     */
    @NotNull
    public static TextColor gradient(@NotNull TextColor start, @NotNull TextColor end, double ratio) {
        ratio = Math.max(0.0, Math.min(1.0, ratio));

        int startRed = start.red();
        int startGreen = start.green();
        int startBlue = start.blue();

        int endRed = end.red();
        int endGreen = end.green();
        int endBlue = end.blue();

        int red = (int) (startRed + (endRed - startRed) * ratio);
        int green = (int) (startGreen + (endGreen - startGreen) * ratio);
        int blue = (int) (startBlue + (endBlue - startBlue) * ratio);

        return TextColor.color(red, green, blue);
    }

    /**
     * 레어도에 따른 색상 가져오기
     *
     * @param rarity 레어도 레벨 (0 = 일반, 4 = 전설)
     * @return 적절한 레어도 색상
     */
    @NotNull
    public static TextColor rarityColor(int rarity) {
        return switch (rarity) {
            case 1 -> UNCOMMON;
            case 2 -> RARE;
            case 3 -> EPIC;
            case 4 -> LEGENDARY;
            default -> COMMON;
        };
    }

    /**
     * 체력 비율에 따른 색상 가져오기
     *
     * @param healthPercent 체력 비율 (0.0 - 1.0)
     * @return 빨간색(낮음)에서 초록색(높음)까지의 색상
     */
    @NotNull
    public static TextColor healthColor(double healthPercent) {
        healthPercent = Math.max(0.0, Math.min(1.0, healthPercent));

        if (healthPercent > 0.6) {
            // 초록색에서 노란색으로 전환
            return gradient(TextColor.color(255, 255, 0), TextColor.color(0, 255, 0),
                    (healthPercent - 0.6) / 0.4);
        } else if (healthPercent > 0.3) {
            // 노란색에서 주황색으로 전환
            return gradient(TextColor.color(255, 165, 0), TextColor.color(255, 255, 0),
                    (healthPercent - 0.3) / 0.3);
        } else {
            // 빨간색에서 주황색으로 전환
            return gradient(TextColor.color(255, 0, 0), TextColor.color(255, 165, 0),
                    healthPercent / 0.3);
        }
    }

    /**
     * 사용 가능한 모든 색상 이름 가져오기
     *
     * @return 색상 이름 집합
     */
    @NotNull
    public static Set<String> getAvailableColorNames() {
        return COLOR_CACHE.keySet();
    }

    /**
     * RGB 값으로 색상 생성
     *
     * @param red   빨간색 (0-255)
     * @param green 초록색 (0-255)
     * @param blue  파란색 (0-255)
     * @return TextColor
     */
    @NotNull
    public static TextColor rgb(int red, int green, int blue) {
        return TextColor.color(
                Math.max(0, Math.min(255, red)),
                Math.max(0, Math.min(255, green)),
                Math.max(0, Math.min(255, blue))
        );
    }

    /**
     * 색상을 밝게 만들기
     *
     * @param color  원본 색상
     * @param factor 밝기 계수 (1.0 이상)
     * @return 밝아진 색상
     */
    @NotNull
    public static TextColor brighten(@NotNull TextColor color, float factor) {
        factor = Math.max(1.0f, factor);

        int red = (int) Math.min(255, color.red() * factor);
        int green = (int) Math.min(255, color.green() * factor);
        int blue = (int) Math.min(255, color.blue() * factor);

        return TextColor.color(red, green, blue);
    }

    /**
     * 색상을 어둡게 만들기
     *
     * @param color  원본 색상
     * @param factor 어둡기 계수 (0.0 - 1.0)
     * @return 어두워진 색상
     */
    @NotNull
    public static TextColor darken(@NotNull TextColor color, float factor) {
        factor = Math.max(0.0f, Math.min(1.0f, factor));

        int red = (int) (color.red() * factor);
        int green = (int) (color.green() * factor);
        int blue = (int) (color.blue() * factor);

        return TextColor.color(red, green, blue);
    }

    /**
     * 색상의 보색 가져오기
     *
     * @param color 원본 색상
     * @return 보색
     */
    @NotNull
    public static TextColor complement(@NotNull TextColor color) {
        return TextColor.color(
                255 - color.red(),
                255 - color.green(),
                255 - color.blue()
        );
    }
}