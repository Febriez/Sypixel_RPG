package com.febrie.rpg.util;

import com.febrie.rpg.RPGMain;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;

/**
 * Adventure API Translator 구현체
 * JSON 번역 파일을 로드하여 Component.translatable()을 지원
 */
public class LangManager implements Translator {

    private static RPGMain plugin;
    private static LangManager instance;
    private static final Gson gson = new Gson();
    private static final String[] SECTIONS = {"general", "gui", "items", "job", "talent", "stat", "messages", "commands", "status", "currency", "quest", "island"};

    // 번역 데이터 저장 (로케일 -> 키 -> MessageFormat)
    private final Map<Locale, Map<String, MessageFormat>> translations = new HashMap<>();
    
    // TabComplete 자동완성을 위한 로케일별 제안사항 저장
    private static final Map<Locale, List<String>> tabCompleteSuggestions = new HashMap<>();

    /**
     * 다국어 시스템 초기화
     */
    public static void initialize(@NotNull RPGMain pluginInstance) {
        plugin = pluginInstance;
        
        // LangManager 인스턴스 생성 및 GlobalTranslator에 등록
        instance = new LangManager();
        instance.loadTranslations();
        GlobalTranslator.translator().addSource(instance);

        // TabComplete 제안사항 로드
        loadTabCompleteSuggestions();

        plugin.getLogger().info("번역 시스템이 초기화되었습니다.");
    }

    /**
     * 번역 데이터 로드
     */
    private void loadTranslations() {
        // 지원 언어별 번역 로드
        loadLanguageTranslations(Locale.KOREAN, "ko_KR");
        loadLanguageTranslations(Locale.ENGLISH, "en_US");
    }

    /**
     * 특정 언어의 번역 파일들을 로드
     */
    private void loadLanguageTranslations(Locale locale, String langCode) {
        Map<String, MessageFormat> localeTranslations = translations.computeIfAbsent(locale, k -> new HashMap<>());
        
        for (String section : SECTIONS) {
            try (InputStream is = plugin.getResource("lang/" + langCode + "/" + section + ".json")) {
                if (is != null) {
                    JsonObject obj = gson.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), JsonObject.class);
                    registerTranslations(obj, section, locale, localeTranslations);
                }
            } catch (Exception e) {
                plugin.getLogger()
                        .warning("언어 파일 로드 실패: lang/" + langCode + "/" + section + ".json - " + e.getMessage());
            }
        }
    }

    /**
     * JSON 객체를 재귀적으로 파싱하여 번역 등록
     */
    private void registerTranslations(JsonObject source, String prefix, Locale locale, Map<String, MessageFormat> localeTranslations) {
        for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
            String key = prefix + "." + entry.getKey();
            JsonElement value = entry.getValue();
            
            if (value.isJsonObject()) {
                // 중첩된 객체는 재귀적으로 처리
                registerTranslations(value.getAsJsonObject(), key, locale, localeTranslations);
            } else if (value.isJsonArray()) {
                // 배열의 경우 인덱스별로 등록
                for (int i = 0; i < value.getAsJsonArray().size(); i++) {
                    String indexKey = key + "[" + i + "]";
                    String text = value.getAsJsonArray().get(i).getAsString();
                    localeTranslations.put(indexKey, new MessageFormat(cleanColorCodes(text), locale));
                }
            } else {
                // 단일 문자열 등록
                String text = value.getAsString();
                localeTranslations.put(key, new MessageFormat(cleanColorCodes(text), locale));
            }
        }
    }

    /**
     * Translator 인터페이스 구현 - 이름
     */
    @Override
    public @NotNull Key name() {
        return Key.key("sypixelrpg", "translations");
    }

    /**
     * Translator 인터페이스 구현 - 번역
     */
    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        Map<String, MessageFormat> localeTranslations = translations.get(locale);
        if (localeTranslations != null) {
            MessageFormat format = localeTranslations.get(key);
            if (format != null) {
                return format;
            }
        }
        
        // 정확한 로케일이 없으면 언어만 맞는 로케일 찾기
        for (Map.Entry<Locale, Map<String, MessageFormat>> entry : translations.entrySet()) {
            if (entry.getKey().getLanguage().equals(locale.getLanguage())) {
                MessageFormat format = entry.getValue().get(key);
                if (format != null) {
                    return format;
                }
            }
        }
        
        // 기본 언어(영어)로 fallback
        Map<String, MessageFormat> englishTranslations = translations.get(Locale.ENGLISH);
        if (englishTranslations != null) {
            return englishTranslations.get(key);
        }
        
        return null;
    }

    /**
     * 색상 코드 제거 (번역에서는 텍스트만, 색상은 코드에서 처리)
     */
    private static String cleanColorCodes(String text) {
        return text.replaceAll("%COLOR_[^%]+%", "");
    }

    /**
     * TabComplete 자동완성 제안사항 로드
     */
    private static void loadTabCompleteSuggestions() {
        // 한국어 자동완성 제안
        tabCompleteSuggestions.put(Locale.KOREAN, Arrays.asList("안녕하세요!", "ㅎㅇ", "뭐해?", "같이할래?", "넵", "ㅇㅋ", "ㄳ", "ㅅㄱ", "잠깐", "도와주세요"));

        // 영어 자동완성 제안
        tabCompleteSuggestions.put(Locale.ENGLISH, Arrays.asList("Hello!", "Hi", "What's up?", "Want to join?", "Yes", "OK", "Thanks", "Wait", "Help me", "Sure"));
    }

    /**
     * 로케일별 TabComplete 자동완성 제안사항 반환
     */
    public static List<String> getTabCompleteSuggestions(@NotNull Player player) {
        Locale locale = player.locale().getLanguage().equals("ko") ? Locale.KOREAN : Locale.ENGLISH;
        return tabCompleteSuggestions.getOrDefault(locale, tabCompleteSuggestions.get(Locale.ENGLISH));
    }

    /**
     * Component.translatable을 일반 문자열로 변환 (AnvilGUI 등에서 사용)
     */
    public static String getPlainText(@NotNull String key, @NotNull Player player, Component... args) {
        Component translatable = Component.translatable(key, args);
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(GlobalTranslator.render(translatable, player.locale()));
    }


    /**
     * 리로드 - 번역 데이터 재로드
     */
    public static void reload() {
        if (instance != null) {
            // 기존 번역 데이터 정리
            instance.translations.clear();
            tabCompleteSuggestions.clear();
            
            // 번역 데이터 재로드
            instance.loadTranslations();
            loadTabCompleteSuggestions();
            
            plugin.getLogger().info("번역 시스템이 리로드되었습니다.");
        }
    }

    /**
     * 종료 시 GlobalTranslator에서 제거
     */
    public static void shutdown() {
        if (instance != null) {
            GlobalTranslator.translator().removeSource(instance);
            plugin.getLogger().info("번역 시스템이 종료되었습니다.");
        }
    }
}