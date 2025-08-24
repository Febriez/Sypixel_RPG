package com.febrie.rpg.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 통합 색상 처리 유틸리티
 * 모든 색상 파싱 로직을 담당
 * 
 * 지원하는 색상 형식:
 * - %COLOR_BLACK%, %COLOR_WHITE% 등 (명명된 색상)
 * - %COLOR_#RRGGBB% (HEX 색상)
 * - &코드 (레거시 색상 코드)
 * - &#RRGGBB (레거시 HEX 색상)
 *
 * @author Febrie, CoffeeTory
 */
public class UnifiedColorUtil {

    private UnifiedColorUtil() {
        throw new UnsupportedOperationException("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }

    // 색상 파싱 패턴
    private static final Pattern COLOR_PLACEHOLDER_PATTERN = Pattern.compile("%COLOR_([A-Z_]+|#[0-9A-Fa-f]{6})%");
    private static final Pattern LEGACY_HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    
    // 명명된 색상 매핑
    private static final Map<String, TextColor> NAMED_COLORS = new HashMap<>();
    
    static {
        // Minecraft 기본 색상
        NAMED_COLORS.put("BLACK", NamedTextColor.BLACK);
        NAMED_COLORS.put("DARK_BLUE", NamedTextColor.DARK_BLUE);
        NAMED_COLORS.put("DARK_GREEN", NamedTextColor.DARK_GREEN);
        NAMED_COLORS.put("DARK_AQUA", NamedTextColor.DARK_AQUA);
        NAMED_COLORS.put("DARK_RED", NamedTextColor.DARK_RED);
        NAMED_COLORS.put("DARK_PURPLE", NamedTextColor.DARK_PURPLE);
        NAMED_COLORS.put("GOLD", NamedTextColor.GOLD);
        NAMED_COLORS.put("GRAY", NamedTextColor.GRAY);
        NAMED_COLORS.put("DARK_GRAY", NamedTextColor.DARK_GRAY);
        NAMED_COLORS.put("BLUE", NamedTextColor.BLUE);
        NAMED_COLORS.put("GREEN", NamedTextColor.GREEN);
        NAMED_COLORS.put("AQUA", NamedTextColor.AQUA);
        NAMED_COLORS.put("RED", NamedTextColor.RED);
        NAMED_COLORS.put("LIGHT_PURPLE", NamedTextColor.LIGHT_PURPLE);
        NAMED_COLORS.put("YELLOW", NamedTextColor.YELLOW);
        NAMED_COLORS.put("WHITE", NamedTextColor.WHITE);
        
        // 커스텀 색상
        NAMED_COLORS.put("PRIMARY", TextColor.color(0x00AA00));      // 녹색
        NAMED_COLORS.put("SECONDARY", TextColor.color(0x5555FF));    // 파란색
        NAMED_COLORS.put("SUCCESS", TextColor.color(0x55FF55));      // 밝은 녹색
        NAMED_COLORS.put("WARNING", TextColor.color(0xFFAA00));      // 주황색
        NAMED_COLORS.put("ERROR", TextColor.color(0xFF5555));        // 빨간색
        NAMED_COLORS.put("INFO", TextColor.color(0xAAAAAA));         // 회색
        NAMED_COLORS.put("ACCENT", TextColor.color(0xFFFF55));       // 노란색
        NAMED_COLORS.put("MUTED", TextColor.color(0x555555));        // 어두운 회색
        
        // RPG 특수 색상
        NAMED_COLORS.put("LEGENDARY", NamedTextColor.GOLD);
        NAMED_COLORS.put("EPIC", NamedTextColor.DARK_PURPLE);
        NAMED_COLORS.put("RARE", NamedTextColor.BLUE);
        NAMED_COLORS.put("UNCOMMON", NamedTextColor.GREEN);
        NAMED_COLORS.put("COMMON", NamedTextColor.GRAY);
        NAMED_COLORS.put("MYTHIC", TextColor.color(255, 0, 255));    // 마젠타
        NAMED_COLORS.put("COPPER", TextColor.color(184, 115, 51));    // 구리색
        NAMED_COLORS.put("HEALTH", TextColor.color(255, 85, 85));     // 체력 빨강
        NAMED_COLORS.put("MANA", TextColor.color(51, 153, 255));      // 마나 파랑
        NAMED_COLORS.put("EXPERIENCE", TextColor.color(255, 215, 0)); // 경험치 금색
        NAMED_COLORS.put("NETHERITE", TextColor.color(47, 41, 45));   // 네더라이트 어두운 회색
        NAMED_COLORS.put("DIAMOND", TextColor.color(185, 242, 255));  // 다이아몬드 하늘색
        NAMED_COLORS.put("EMERALD", TextColor.color(85, 255, 85));    // 에메랄드 녹색
        NAMED_COLORS.put("IRON", TextColor.color(192, 192, 192));     // 철 은색
        NAMED_COLORS.put("GUI_TITLE", TextColor.color(255, 255, 255));// GUI 제목 흰색
        NAMED_COLORS.put("ORANGE", TextColor.color(255, 170, 0));     // 주황색
    }
    
    // 공개 색상 상수 (기존 코드와의 호환성을 위해)
    public static final TextColor BLACK = NamedTextColor.BLACK;
    public static final TextColor DARK_BLUE = NamedTextColor.DARK_BLUE;
    public static final TextColor DARK_GREEN = NamedTextColor.DARK_GREEN;
    public static final TextColor DARK_AQUA = NamedTextColor.DARK_AQUA;
    public static final TextColor DARK_RED = NamedTextColor.DARK_RED;
    public static final TextColor DARK_PURPLE = NamedTextColor.DARK_PURPLE;
    public static final TextColor GOLD = NamedTextColor.GOLD;
    public static final TextColor GRAY = NamedTextColor.GRAY;
    public static final TextColor DARK_GRAY = NamedTextColor.DARK_GRAY;
    public static final TextColor BLUE = NamedTextColor.BLUE;
    public static final TextColor GREEN = NamedTextColor.GREEN;
    public static final TextColor AQUA = NamedTextColor.AQUA;
    public static final TextColor RED = NamedTextColor.RED;
    public static final TextColor LIGHT_PURPLE = NamedTextColor.LIGHT_PURPLE;
    public static final TextColor YELLOW = NamedTextColor.YELLOW;
    public static final TextColor WHITE = NamedTextColor.WHITE;
    
    // 커스텀 색상 상수
    public static final TextColor PRIMARY = TextColor.color(0x00AA00);
    public static final TextColor SECONDARY = TextColor.color(0x5555FF);
    public static final TextColor SUCCESS = TextColor.color(0x55FF55);
    public static final TextColor WARNING = TextColor.color(0xFFAA00);
    public static final TextColor ERROR = TextColor.color(0xFF5555);
    public static final TextColor INFO = TextColor.color(0xAAAAAA);
    public static final TextColor ACCENT = TextColor.color(0xFFFF55);
    public static final TextColor MUTED = TextColor.color(0x555555);
    
    // RPG 특수 색상 상수
    public static final TextColor LEGENDARY = NamedTextColor.GOLD;
    public static final TextColor EPIC = NamedTextColor.DARK_PURPLE;
    public static final TextColor RARE = NamedTextColor.BLUE;
    public static final TextColor UNCOMMON = NamedTextColor.GREEN;
    public static final TextColor COMMON = NamedTextColor.GRAY;
    public static final TextColor MYTHIC = TextColor.color(255, 0, 255);
    public static final TextColor COPPER = TextColor.color(184, 115, 51);
    public static final TextColor HEALTH = TextColor.color(255, 85, 85);
    public static final TextColor MANA = TextColor.color(51, 153, 255);
    public static final TextColor EXPERIENCE = TextColor.color(255, 215, 0);
    public static final TextColor NETHERITE = TextColor.color(47, 41, 45);
    public static final TextColor DIAMOND = TextColor.color(185, 242, 255);
    public static final TextColor EMERALD = TextColor.color(85, 255, 85);
    public static final TextColor IRON = TextColor.color(192, 192, 192);
    public static final TextColor GUI_TITLE = TextColor.color(255, 255, 255);
    public static final TextColor ORANGE = TextColor.color(255, 170, 0);

    /**
     * 메인 색상 파싱 메서드
     * 모든 색상 형식을 처리하고 Component로 변환
     * 
     * @param text 파싱할 텍스트
     * @return 색상이 적용된 Component
     */
    @NotNull
    public static Component parseComponent(@NotNull String text) {
        // 1. %COLOR_XXX% 형식 처리
        text = processColorPlaceholders(text);
        
        // 2. &#RRGGBB 형식 처리
        text = processLegacyHex(text);
        
        // 3. &코드 처리 및 Component 변환
        return LEGACY_SERIALIZER.deserialize(text).decoration(TextDecoration.ITALIC, false);
    }
    
    /**
     * 레거시 색상 파싱 메서드 (호환성 유지)
     * parseComponent와 동일한 기능
     * 
     * @param text 파싱할 텍스트
     * @return 색상이 적용된 Component
     */
    @NotNull
    public static Component parse(@NotNull String text) {
        return parseComponent(text);
    }
    
    /**
     * %COLOR_XXX% 형식의 색상 플레이스홀더 처리
     * 
     * @param text 처리할 텍스트
     * @return 색상 코드로 변환된 텍스트
     */
    @NotNull
    private static String processColorPlaceholders(@NotNull String text) {
        Matcher matcher = COLOR_PLACEHOLDER_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String colorSpec = matcher.group(1);
            String replacement;
            
            if (colorSpec.startsWith("#")) {
                // HEX 색상 처리
                replacement = convertHexToLegacy(colorSpec.substring(1));
            } else {
                // 명명된 색상 처리
                TextColor color = NAMED_COLORS.get(colorSpec);
                if (color != null) {
                    replacement = convertTextColorToLegacy(color);
                } else {
                    // 알 수 없는 색상은 그대로 둠
                    replacement = matcher.group();
                }
            }
            
            matcher.appendReplacement(result, replacement);
        }
        
        matcher.appendTail(result);
        return result.toString();
    }
    
    /**
     * &#RRGGBB 형식의 레거시 HEX 색상 처리
     * 
     * @param text 처리할 텍스트
     * @return 변환된 텍스트
     */
    @NotNull
    private static String processLegacyHex(@NotNull String text) {
        Matcher matcher = LEGACY_HEX_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String hex = matcher.group(1);
            String replacement = convertHexToLegacy(hex);
            matcher.appendReplacement(result, replacement);
        }
        
        matcher.appendTail(result);
        return result.toString();
    }
    
    /**
     * HEX 색상을 레거시 형식으로 변환
     * 
     * @param hex 6자리 HEX 코드
     * @return 레거시 색상 코드
     */
    @NotNull
    private static String convertHexToLegacy(@NotNull String hex) {
        StringBuilder result = new StringBuilder("§x");
        for (char c : hex.toLowerCase().toCharArray()) {
            result.append("§").append(c);
        }
        return result.toString();
    }
    
    /**
     * TextColor를 가장 가까운 레거시 색상 코드로 변환
     * 
     * @param color TextColor
     * @return 레거시 색상 코드
     */
    @NotNull
    private static String convertTextColorToLegacy(@NotNull TextColor color) {
        // NamedTextColor인 경우 직접 매핑
        if (color instanceof NamedTextColor named) {
            return switch (named) {
                case BLACK -> "&0";
                case DARK_BLUE -> "&1";
                case DARK_GREEN -> "&2";
                case DARK_AQUA -> "&3";
                case DARK_RED -> "&4";
                case DARK_PURPLE -> "&5";
                case GOLD -> "&6";
                case GRAY -> "&7";
                case DARK_GRAY -> "&8";
                case BLUE -> "&9";
                case GREEN -> "&a";
                case AQUA -> "&b";
                case RED -> "&c";
                case LIGHT_PURPLE -> "&d";
                case YELLOW -> "&e";
                case WHITE -> "&f";
                default -> "&f";
            };
        }
        
        // RGB 색상인 경우 HEX로 변환
        int rgb = color.value();
        String hex = String.format("%06x", rgb);
        return convertHexToLegacy(hex);
    }
    
    /**
     * 간편 메서드: 텍스트와 색상으로 Component 생성
     * 
     * @param text 텍스트
     * @param color 색상
     * @return Component
     */
    @NotNull
    public static Component text(@NotNull String text, @NotNull TextColor color) {
        return Component.text(text).color(color).decoration(TextDecoration.ITALIC, false);
    }
    
    /**
     * 간편 메서드: 텍스트로 Component 생성
     * 
     * @param text 텍스트
     * @return Component
     */
    @NotNull
    public static Component text(@NotNull String text) {
        return Component.text(text).decoration(TextDecoration.ITALIC, false);
    }
    
    /**
     * HEX 문자열을 TextColor로 파싱
     * 
     * @param hex HEX 색상 코드 (#포함 또는 미포함)
     * @return TextColor
     */
    @NotNull
    public static TextColor parseHexColor(@NotNull String hex) {
        // # 제거
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        
        // RGB 값 파싱
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        
        return TextColor.color(r, g, b);
    }
    
    /**
     * 색상 이름으로 TextColor 가져오기
     * 
     * @param name 색상 이름
     * @return TextColor (없으면 WHITE 반환)
     */
    @NotNull
    public static TextColor fromName(@NotNull String name) {
        return NAMED_COLORS.getOrDefault(name.toUpperCase(), NamedTextColor.WHITE);
    }
}