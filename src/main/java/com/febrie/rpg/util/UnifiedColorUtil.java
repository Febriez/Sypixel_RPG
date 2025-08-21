package com.febrie.rpg.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 통합 색상 처리 유틸리티
 * 모든 색상 처리를 LangManager 기반으로 통일
 *
 * @author Febrie, CoffeeTory
 */
public class UnifiedColorUtil {

    private UnifiedColorUtil() {
        throw new UnsupportedOperationException("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    // 표준 색상 정의 (ColorUtil 호환성을 위해 추가)
    public static final TextColor PRIMARY = TextColor.color(0x00AA00);      // 녹색
    public static final TextColor SECONDARY = TextColor.color(0x5555FF);    // 파란색
    public static final TextColor SUCCESS = TextColor.color(0x55FF55);     // 밝은 녹색
    public static final TextColor WARNING = TextColor.color(0xFFAA00);     // 주황색
    public static final TextColor ERROR = TextColor.color(0xFF5555);       // 빨간색
    public static final TextColor INFO = TextColor.color(0xAAAAAA);        // 회색
    public static final TextColor ACCENT = TextColor.color(0xFFFF55);      // 노란색
    public static final TextColor MUTED = TextColor.color(0x555555);       // 어두운 회색

    // ColorUtil 호환성을 위한 추가 색상
    public static final TextColor RED = NamedTextColor.RED;
    public static final TextColor GREEN = NamedTextColor.GREEN;
    public static final TextColor BLUE = NamedTextColor.BLUE;
    public static final TextColor YELLOW = NamedTextColor.YELLOW;
    public static final TextColor GOLD = NamedTextColor.GOLD;
    public static final TextColor GRAY = NamedTextColor.GRAY;
    public static final TextColor WHITE = NamedTextColor.WHITE;
    public static final TextColor AQUA = NamedTextColor.AQUA;
    public static final TextColor LIGHT_PURPLE = NamedTextColor.LIGHT_PURPLE;
    public static final TextColor DARK_GREEN = NamedTextColor.DARK_GREEN;
    public static final TextColor DARK_PURPLE = NamedTextColor.DARK_PURPLE;
    public static final TextColor DARK_RED = NamedTextColor.DARK_RED;
    public static final TextColor DARK_BLUE = NamedTextColor.DARK_BLUE;
    public static final TextColor DARK_GRAY = NamedTextColor.DARK_GRAY;
    public static final TextColor DARK_AQUA = NamedTextColor.DARK_AQUA;
    public static final TextColor BLACK = NamedTextColor.BLACK;
    public static final TextColor LEGENDARY = NamedTextColor.GOLD;

    // 특수 색상들
    public static final TextColor COPPER = TextColor.color(184, 115, 51); // Copper color for talents
    public static final TextColor HEALTH = TextColor.color(255, 85, 85); // Red for health
    public static final TextColor MANA = TextColor.color(51, 153, 255); // Mana blue
    public static final TextColor EXPERIENCE = TextColor.color(255, 215, 0); // Gold for experience
    public static final TextColor EPIC = NamedTextColor.DARK_PURPLE;
    public static final TextColor RARE = NamedTextColor.BLUE;
    public static final TextColor UNCOMMON = NamedTextColor.GREEN;
    public static final TextColor COMMON = NamedTextColor.GRAY;
    public static final TextColor MYTHIC = TextColor.color(255, 0, 255); // Magenta for mythic
    public static final TextColor ORANGE = TextColor.color(255, 170, 0); // Orange
    public static final TextColor NETHERITE = TextColor.color(47, 41, 45); // Dark gray for netherite
    public static final TextColor DIAMOND = TextColor.color(185, 242, 255); // Light cyan for diamond
    public static final TextColor EMERALD = TextColor.color(85, 255, 85); // Green for emerald
    public static final TextColor IRON = TextColor.color(192, 192, 192); // Silver/gray for iron
    public static final TextColor GUI_TITLE = TextColor.color(255, 255, 255); // White for GUI titles

    /**
     * 레거시 색상 코드를 Component로 변환 (권장)
     */
    @NotNull
    public static Component parse(@NotNull String text) {
        // HEX 색상 변환
        text = convertHexColors(text);

        // Legacy 색상 코드 변환
        return LEGACY.deserialize(text).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * MiniMessage 형식 파싱 (고급 기능)
     */
    @NotNull
    public static Component parseMini(@NotNull String text) {
        return MINI_MESSAGE.deserialize(text).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * 컴포넌트 생성 헬퍼 메서드들
     */
    @NotNull
    public static Component text(@NotNull String text) {
        return Component.text(text).decoration(TextDecoration.ITALIC, false);
    }

    @NotNull
    public static Component text(@NotNull String text, @NotNull TextColor color) {
        return Component.text(text).color(color).decoration(TextDecoration.ITALIC, false);
    }

    @NotNull
    public static Component primary(@NotNull String text) {
        return text(text, PRIMARY);
    }

    @NotNull
    public static Component secondary(@NotNull String text) {
        return text(text, SECONDARY);
    }

    @NotNull
    public static Component success(@NotNull String text) {
        return text(text, SUCCESS);
    }

    @NotNull
    public static Component warning(@NotNull String text) {
        return text(text, WARNING);
    }

    @NotNull
    public static Component error(@NotNull String text) {
        return text(text, ERROR);
    }

    @NotNull
    public static Component info(@NotNull String text) {
        return text(text, INFO);
    }

    @NotNull
    public static Component accent(@NotNull String text) {
        return text(text, ACCENT);
    }

    @NotNull
    public static Component muted(@NotNull String text) {
        return text(text, MUTED);
    }

    /**
     * 장식 추가 헬퍼
     */
    @NotNull
    public static Component bold(@NotNull Component component) {
        return component.decoration(TextDecoration.BOLD, true);
    }

    @NotNull
    public static Component italic(@NotNull Component component) {
        return component.decoration(TextDecoration.ITALIC, true);
    }

    @NotNull
    public static Component underline(@NotNull Component component) {
        return component.decoration(TextDecoration.UNDERLINED, true);
    }

    @NotNull
    public static Component strikethrough(@NotNull Component component) {
        return component.decoration(TextDecoration.STRIKETHROUGH, true);
    }

    /**
     * HEX 색상 코드 변환
     */
    @NotNull
    private static String convertHexColors(@NotNull String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder stringBuilder = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append("§").append(c);
            }
            matcher.appendReplacement(stringBuilder, replacement.toString());
        }

        matcher.appendTail(stringBuilder);
        return stringBuilder.toString();
    }

    /**
     * 모든 GUI에서 사용할 표준 메서드
     */
    @NotNull
    public static Component parseComponent(@NotNull String text) {
        return parse(text);
    }

    /**
     * HEX 색상 코드를 TextColor로 파싱
     */
    @NotNull
    public static TextColor parseHexColor(@NotNull String hex) {
        // Remove # if present
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        // Parse RGB values
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return TextColor.color(r, g, b);
    }

    /**
     * 색상 이름으로 TextColor 가져오기
     */
    @NotNull
    public static TextColor fromName(@NotNull String name) {
        return switch (name.toUpperCase()) {
            case "BLACK" -> BLACK;
            case "DARK_BLUE" -> DARK_BLUE;
            case "DARK_GREEN" -> DARK_GREEN;
            case "DARK_AQUA" -> DARK_AQUA;
            case "DARK_RED" -> DARK_RED;
            case "DARK_PURPLE" -> DARK_PURPLE;
            case "GOLD" -> GOLD;
            case "GRAY" -> GRAY;
            case "DARK_GRAY" -> DARK_GRAY;
            case "BLUE" -> BLUE;
            case "GREEN" -> GREEN;
            case "AQUA" -> AQUA;
            case "RED" -> RED;
            case "LIGHT_PURPLE" -> LIGHT_PURPLE;
            case "YELLOW" -> YELLOW;
            case "WHITE" -> WHITE;
            case "LEGENDARY" -> LEGENDARY;
            case "EPIC" -> EPIC;
            case "RARE" -> RARE;
            case "UNCOMMON" -> UNCOMMON;
            case "COMMON" -> COMMON;
            case "MYTHIC" -> MYTHIC;
            case "ERROR" -> ERROR;
            case "SUCCESS" -> SUCCESS;
            case "WARNING" -> WARNING;
            case "INFO" -> INFO;
            default -> WHITE;
        };
    }
}