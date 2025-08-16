package com.febrie.rpg.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

/**
 * 색상 유틸리티 클래스 (레거시 호환성)
 * 모든 기능은 UnifiedColorUtil로 리다이렉트됩니다.
 * 
 * @deprecated Use {@link UnifiedColorUtil} instead
 * @author Febrie, CoffeeTory
 */
@Deprecated
public final class ColorUtil {
    
    private ColorUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // === 색상 상수 (UnifiedColorUtil로 리다이렉트) ===
    public static final TextColor AQUA = UnifiedColorUtil.AQUA;
    public static final TextColor BLACK = NamedTextColor.BLACK;
    public static final TextColor GOLD = UnifiedColorUtil.GOLD;
    public static final TextColor BLUE = UnifiedColorUtil.BLUE;
    public static final TextColor DARK_BLUE = NamedTextColor.DARK_BLUE;
    public static final TextColor DARK_GRAY = NamedTextColor.DARK_GRAY;
    public static final TextColor DARK_GREEN = UnifiedColorUtil.DARK_GREEN;
    public static final TextColor DARK_PURPLE = UnifiedColorUtil.DARK_PURPLE;
    public static final TextColor DARK_RED = NamedTextColor.DARK_RED;
    public static final TextColor GRAY = UnifiedColorUtil.GRAY;
    public static final TextColor GREEN = UnifiedColorUtil.GREEN;
    public static final TextColor LIGHT_PURPLE = UnifiedColorUtil.LIGHT_PURPLE;
    public static final TextColor RED = UnifiedColorUtil.RED;
    public static final TextColor WHITE = UnifiedColorUtil.WHITE;
    public static final TextColor YELLOW = UnifiedColorUtil.YELLOW;
    
    // 특수 색상
    public static final TextColor LEGENDARY = UnifiedColorUtil.LEGENDARY;
    public static final TextColor EPIC = NamedTextColor.DARK_PURPLE;
    public static final TextColor RARE = NamedTextColor.BLUE;
    public static final TextColor UNCOMMON = NamedTextColor.GREEN;
    public static final TextColor COMMON = NamedTextColor.GRAY;
    
    /**
     * 레거시 색상 코드를 Component로 변환
     */
    @NotNull
    public static Component parseComponent(@NotNull String text) {
        return UnifiedColorUtil.parseComponent(text);
    }
    
    /**
     * 색상 코드를 문자열로 변환
     */
    @NotNull
    public static String colorize(@NotNull String text) {
        return UnifiedColorUtil.colorize(text);
    }
    
    /**
     * Component 생성
     */
    @NotNull
    public static Component parse(@NotNull String text) {
        return UnifiedColorUtil.parse(text);
    }
    
    /**
     * 텍스트 컴포넌트 생성
     */
    @NotNull
    public static Component text(@NotNull String text) {
        return UnifiedColorUtil.text(text);
    }
    
    /**
     * 색상이 적용된 텍스트 컴포넌트 생성
     */
    @NotNull
    public static Component text(@NotNull String text, @NotNull TextColor color) {
        return UnifiedColorUtil.text(text, color);
    }
}