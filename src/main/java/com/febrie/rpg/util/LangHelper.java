package com.febrie.rpg.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 언어 헬퍼 - 플레이스홀더 처리
 * LangManager와 분리하여 플레이스홀더 교체 기능 제공
 */
public class LangHelper {

    /**
     * 텍스트를 가져와서 플레이스홀더를 교체 (enum 버전)
     */
    @NotNull
    public static Component text(@NotNull LangKey key, Object... args) {
        return text(key, Locale.US, args);
    }

    @NotNull
    public static Component text(@NotNull LangKey key, @NotNull Locale locale, Object... args) {
        Component base = LangManager.text(key, locale);
        return replacePlaceholders(base, args);
    }

    @NotNull
    public static Component text(@NotNull LangKey key, @NotNull org.bukkit.entity.Player player, Object... args) {
        return text(key, player.locale(), args);
    }

    /**
     * 리스트를 가져와서 플레이스홀더를 교체 (enum 버전)
     */
    @NotNull
    public static List<Component> list(@NotNull LangKey key, Object... args) {
        return list(key, Locale.US, args);
    }

    @NotNull
    public static List<Component> list(@NotNull LangKey key, @NotNull Locale locale, Object... args) {
        List<Component> base = LangManager.list(key, locale);
        return base.stream().map(comp -> replacePlaceholders(comp, args)).collect(Collectors.toList());
    }

    @NotNull
    public static List<Component> list(@NotNull LangKey key, @NotNull org.bukkit.entity.Player player, Object... args) {
        return list(key, player.locale(), args);
    }

    /**
     * Component의 플레이스홀더를 교체
     * {0}, {1}, {2} 형식의 플레이스홀더를 지원
     */
    @NotNull
    public static Component replacePlaceholders(@NotNull Component component, Object... args) {
        Component result = component;

        for (int i = 0; i < args.length; i++) {
            String placeholder = "{" + i + "}";
            String replacement = String.valueOf(args[i]);

            TextReplacementConfig config = TextReplacementConfig.builder().matchLiteral(placeholder)
                    .replacement(Component.text(replacement)).build();

            result = result.replaceText(config);
        }

        return result;
    }

    /**
     * 명명된 플레이스홀더 교체
     * %name%, %value% 형식의 플레이스홀더를 지원
     */
    @NotNull
    public static Component replace(@NotNull Component component, @NotNull String placeholder, @NotNull String value) {
        TextReplacementConfig config = TextReplacementConfig.builder().matchLiteral(placeholder)
                .replacement(Component.text(value)).build();

        return component.replaceText(config);
    }

    /**
     * 여러 명명된 플레이스홀더를 한번에 교체
     */
    @NotNull
    public static Component replaceAll(@NotNull Component component, @NotNull String... replacements) {
        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Replacements must be in pairs (placeholder, value)");
        }

        Component result = component;
        for (int i = 0; i < replacements.length; i += 2) {
            result = replace(result, replacements[i], replacements[i + 1]);
        }

        return result;
    }

}