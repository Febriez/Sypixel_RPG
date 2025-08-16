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
    public static final TextColor LEGENDARY = NamedTextColor.GOLD;
    
    /**
     * 레거시 색상 코드를 Component로 변환 (권장)
     */
    @NotNull
    public static Component parse(@NotNull String text) {
        // HEX 색상 변환
        text = convertHexColors(text);
        
        // Legacy 색상 코드 변환
        return LEGACY.deserialize(text)
            .decoration(TextDecoration.ITALIC, false);
    }
    
    /**
     * MiniMessage 형식 파싱 (고급 기능)
     */
    @NotNull
    public static Component parseMini(@NotNull String text) {
        return MINI_MESSAGE.deserialize(text)
            .decoration(TextDecoration.ITALIC, false);
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
        return Component.text(text)
            .color(color)
            .decoration(TextDecoration.ITALIC, false);
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
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append("§").append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        
        matcher.appendTail(buffer);
        return buffer.toString();
    }
    
    /**
     * 레거시 지원용 (deprecated)
     */
    @Deprecated
    @NotNull
    public static String colorize(@NotNull String text) {
        return LegacyComponentSerializer.legacySection()
            .serialize(parse(text));
    }
    
    /**
     * 모든 GUI에서 사용할 표준 메서드
     */
    @NotNull
    public static Component parseComponent(@NotNull String text) {
        return parse(text);
    }
}