package com.febrie.rpg.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;

/**
 * 언어 관리 시스템
 * 다국어 지원을 위한 간소화된 매니저
 *
 * 주요 기능:
 * - 언어 파일 로드
 * - 번역 키-값 관리
 * - 색상 코드 지원 (%COLOR_XXX% 형식)
 * - 플레이스홀더 지원
 *
 * @author Febrie, CoffeeTory
 */
public class LangManager {

    private final JavaPlugin plugin;
    private final Gson gson = new Gson();
    private final Map<Locale, Map<String, String>> translations = new HashMap<>();

    private static LangManager instance;

    /**
     * LangManager 생성자
     *
     * @param plugin 플러그인 인스턴스
     */
    private LangManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 싱글톤 인스턴스 반환
     */
    public static LangManager getInstance() {
        return instance;
    }

    /**
     * 초기화
     */
    private void init() {
        loadAllTranslations();
        plugin.getLogger().info("언어 시스템이 초기화되었습니다.");
    }

    /**
     * 모든 번역 파일 로드
     */
    private void loadAllTranslations() {
        // 지원하는 언어 코드
        String[] languageCodes = {"en_us", "ko_kr"};

        // 각 언어별 JSON 파일들
        String[] jsonFiles = {"commands.json", "currency.json", "dialog.json", "general.json",
                              "gui.json", "island.json", "items.json", "job.json",
                              "messages.json", "quest.json", "stat.json", "status.json",
                              "talent.json", "biome.json"};

        for (String langCode : languageCodes) {
            Locale locale = createLocale(langCode);
            Map<String, String> localeTranslations = translations.computeIfAbsent(locale, k -> new HashMap<>());
            int loadedFiles = 0;

            for (String jsonFile : jsonFiles) {
                String resourcePath = "/" + langCode + "/" + jsonFile;
                try (InputStream stream = plugin.getClass().getResourceAsStream(resourcePath)) {
                    if (stream != null) {
                        // 파일 이름에서 .json 제거하여 접두사로 사용
                        String filePrefix = jsonFile.replace(".json", "");
                        int beforeSize = localeTranslations.size();
                        processJsonContent(stream, localeTranslations, filePrefix);
                        int afterSize = localeTranslations.size();
                        int addedKeys = afterSize - beforeSize;
                        if (addedKeys > 0) {
                            plugin.getLogger().info("[" + langCode + "] " + jsonFile + ": " + addedKeys + "개 키 로드 (총 " + afterSize + "개)");
                        }
                        loadedFiles++;
                    }
                } catch (Exception e) {
                    plugin.getLogger().severe("JSON 로드 실패: " + resourcePath + " - " + e.getMessage());
                }
            }

            if (loadedFiles > 0) {
                plugin.getLogger().info(langCode + " 언어 로드 완료 (" + loadedFiles + " 파일)");
            } else {
                plugin.getLogger().warning(langCode + " 언어 파일을 찾을 수 없습니다.");
            }
        }
    }

    /**
     * JSON 내용 처리
     * @param stream 입력 스트림
     * @param localeTranslations 번역 맵
     * @param filePrefix 파일 이름 접두사
     */
    private void processJsonContent(InputStream stream, Map<String, String> localeTranslations, String filePrefix) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            Map<String, String> flattened = flattenJson(jsonObject, filePrefix);
            localeTranslations.putAll(flattened);
        }
    }

    /**
     * JSON 평면화
     * @param jsonObject JSON 객체
     * @param filePrefix 파일 이름 접두사
     * @return 평면화된 맵
     */
    private @NotNull Map<String, String> flattenJson(JsonObject jsonObject, String filePrefix) {
        Map<String, String> result = new HashMap<>();
        flattenJsonRecursive(jsonObject, filePrefix, result);
        return result;
    }

    /**
     * JSON 재귀적 평면화
     */
    private void flattenJsonRecursive(@NotNull JsonElement element, String prefix, Map<String, String> result) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            obj.entrySet().forEach(entry -> {
                String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
                flattenJsonRecursive(entry.getValue(), key, result);
            });
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                JsonElement arrayElement = array.get(i);
                if (arrayElement.isJsonPrimitive()) {
                    result.put(prefix + "." + i, arrayElement.getAsString());
                } else {
                    String key = prefix + "." + i;
                    flattenJsonRecursive(arrayElement, key, result);
                }
            }
        } else if (element.isJsonPrimitive()) {
            result.put(prefix, element.getAsString());
        }
    }

    /**
     * Locale 생성
     */
    private Locale createLocale(@NotNull String langCode) {
        return Locale.forLanguageTag(langCode.replace("_", "-"));
    }

    /**
     * 원시 번역 텍스트 가져오기
     */
    @Nullable
    private String getRawTranslation(@NotNull String key, @NotNull Locale locale) {
        // 정확한 로케일 매칭
        Map<String, String> localeTranslations = translations.get(locale);
        if (localeTranslations != null) {
            String text = localeTranslations.get(key);
            if (text != null) return text;
        }

        // 언어만 맞는 로케일 찾기
        for (Map.Entry<Locale, Map<String, String>> entry : translations.entrySet()) {
            if (entry.getKey().getLanguage().equals(locale.getLanguage())) {
                String text = entry.getValue().get(key);
                if (text != null) return text;
            }
        }

        // 영어로 fallback
        Map<String, String> englishTranslations = translations.get(Locale.US);
        if (englishTranslations == null) {
            englishTranslations = translations.get(Locale.ENGLISH);
        }

        if (englishTranslations != null) {
            return englishTranslations.get(key);
        }

        return null;
    }

    // ============================================
    // 공개 정적 메서드 (API)
    // ============================================

    /**
     * 메인 API - 색상이 적용된 Component 반환 (플레이스홀더 없음)
     *
     * @param key 번역 키
     * @param locale 로케일
     * @return 색상이 적용된 Component
     */
    @NotNull
    public static Component getComponent(@NotNull String key, @NotNull Locale locale) {
        if (instance == null) {
            return Component.text(key);
        }

        String text = instance.getRawTranslation(key, locale);
        if (text == null) {
            instance.plugin.getLogger().warning("Missing translation key: " + key + " for locale: " + locale);
            return Component.text(key);
        }

        // 색상 파싱은 UnifiedColorUtil에 위임
        return UnifiedColorUtil.parseComponent(text);
    }

    /**
     * 메인 API - 플레이스홀더가 있는 Component 반환
     *
     * Component와 String을 모두 지원하는 가변인자
     *
     * @param key 번역 키
     * @param locale 로케일
     * @param args 플레이스홀더 값들 (Component 또는 기타 객체)
     * @return 색상과 플레이스홀더가 적용된 Component
     */
    @NotNull
    public static Component getComponent(@NotNull String key, @NotNull Locale locale, @NotNull Object... args) {
        if (instance == null) {
            return Component.text(key);
        }

        String text = instance.getRawTranslation(key, locale);
        if (text == null) {
            instance.plugin.getLogger().warning("Missing translation key: " + key + " for locale: " + locale);
            return Component.text(key);
        }

        // 플레이스홀더 처리
        if (args.length > 0) {
            try {
                MessageFormat format = new MessageFormat(text, locale);
                Object[] stringArgs = new Object[args.length];

                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Component component) {
                        // Component를 일반 텍스트로 변환
                        stringArgs[i] = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                                .serialize(component);
                    } else {
                        stringArgs[i] = String.valueOf(args[i]);
                    }
                }

                text = format.format(stringArgs);
            } catch (Exception e) {
                instance.plugin.getLogger().warning("Failed to format translation for key: " + key + " - " + e.getMessage());
            }
        }

        // 색상 파싱은 UnifiedColorUtil에 위임
        return UnifiedColorUtil.parseComponent(text);
    }

    /**
     * 정적 초기화 메서드
     *
     * @param plugin 플러그인 인스턴스
     */
    public static void initialize(@NotNull JavaPlugin plugin) {
        if (instance != null) {
            instance.plugin.getLogger().warning("LangManager가 이미 초기화되어 있습니다.");
            return;
        }

        instance = new LangManager(plugin);
        instance.init();
    }

    /**
     * 정적 리로드 메서드
     */
    public static void reload() {
        if (instance == null) {
            throw new IllegalStateException("LangManager가 초기화되지 않았습니다.");
        }

        instance.translations.clear();
        instance.init();
        instance.plugin.getLogger().info("언어 시스템이 리로드되었습니다.");
    }

    /**
     * 정적 종료 메서드
     */
    public static void shutdown() {
        if (instance != null) {
            instance.translations.clear();
            instance.plugin.getLogger().info("언어 시스템이 종료되었습니다.");
            instance = null;
        }
    }

    /**
     * 특정 키가 존재하는지 확인
     *
     * @param key    번역 키
     * @param locale 언어
     * @return 키 존재 여부
     */
    public static boolean hasKey(@NotNull String key, @NotNull Locale locale) {
        if (instance == null) {
            throw new IllegalStateException("LangManager가 초기화되지 않았습니다.");
        }
        
        Map<String, String> localeTranslations = instance.translations.get(locale);
        return localeTranslations != null && localeTranslations.containsKey(key);
    }

    /**
     * 특정 언어의 모든 키를 반환
     *
     * @param locale 언어
     * @return 모든 키의 집합
     */
    public static Set<String> getAllKeys(@NotNull Locale locale) {
        if (instance == null) {
            throw new IllegalStateException("LangManager가 초기화되지 않았습니다.");
        }
        
        Map<String, String> localeTranslations = instance.translations.get(locale);
        return localeTranslations != null ? localeTranslations.keySet() : new HashSet<>();
    }

    /**
     * 번역된 문자열을 반환 (Component가 아닌 String)
     *
     * @param key    번역 키
     * @param locale 언어
     * @return 번역된 문자열
     */
    public static String getString(@NotNull String key, @NotNull Locale locale) {
        if (instance == null) {
            throw new IllegalStateException("LangManager가 초기화되지 않았습니다.");
        }
        
        Map<String, String> localeTranslations = instance.translations.get(locale);
        if (localeTranslations == null) {
            return key; // 번역을 찾지 못하면 키를 반환
        }
        
        return localeTranslations.getOrDefault(key, key);
    }

    // 디버그 모드 변수 추가
    private static boolean debugMode = false;

    /**
     * 디버그 모드 토글
     *
     * @return 변경된 디버그 모드 상태
     */
    public static boolean toggleDebugMode() {
        debugMode = !debugMode;
        if (instance != null) {
            instance.plugin.getLogger().info("디버그 모드: " + (debugMode ? "활성화" : "비활성화"));
        }
        return debugMode;
    }

    /**
     * 디버그 모드 상태 확인
     *
     * @return 디버그 모드 활성화 여부
     */
    public static boolean isDebugMode() {
        return debugMode;
    }

    /**
     * 플레이어의 locale을 사용하여 Component를 반환 (편의 메서드)
     *
     * @param key    번역 키
     * @param player 플레이어 (locale을 가져오기 위해)
     * @return 번역된 Component
     */
    public static Component get(@NotNull String key, @NotNull org.bukkit.entity.Player player) {
        return getComponent(key, player.locale());
    }

    /**
     * 플레이어의 locale을 사용하여 인수가 있는 Component를 반환 (편의 메서드)
     *
     * @param key    번역 키
     * @param player 플레이어 (locale을 가져오기 위해)
     * @param args   번역 인수들
     * @return 번역된 Component
     */
    public static Component get(@NotNull String key, @NotNull org.bukkit.entity.Player player, @NotNull Object... args) {
        return getComponent(key, player.locale(), args);
    }

    /**
     * 플레이어의 locale을 사용하여 Component 리스트를 반환 (편의 메서드)
     *
     * @param key    번역 키
     * @param player 플레이어 (locale을 가져오기 위해)
     * @return 번역된 Component 리스트
     */
    public static java.util.List<Component> getList(@NotNull String key, @NotNull org.bukkit.entity.Player player) {
        // For now, return a simple implementation - could be enhanced to support actual list translations
        String translated = getString(key, player.locale());
        return java.util.Arrays.stream(translated.split("\\n"))
                .map(Component::text)
                .collect(java.util.stream.Collectors.toList());
    }
}