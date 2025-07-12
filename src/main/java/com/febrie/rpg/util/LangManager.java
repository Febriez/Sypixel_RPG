package com.febrie.rpg.util;

import com.febrie.rpg.RPGMain;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 향상된 언어 관리 시스템
 * Adventure API와 색상 플레이스홀더 완벽 지원
 * 분할된 언어 파일 지원
 *
 * @author Febrie, CoffeeTory
 */
public class LangManager {

    private final RPGMain plugin;
    private final Gson gson = new Gson();
    private final Map<String, JsonObject> languageConfigs = new HashMap<>();
    private String defaultLanguage;

    // 캐시 시스템 (성능 최적화)
    private final Map<String, Map<String, String>> messageCache = new ConcurrentHashMap<>();
    private final Map<String, Map<String, List<String>>> listCache = new ConcurrentHashMap<>();

    // 캐시 크기 제한
    private static final int MAX_CACHE_SIZE = 1000;

    // 색상 플레이스홀더 패턴
    private static final Pattern COLOR_PATTERN = Pattern.compile("%COLOR_([A-Z_]+)%");

    // 언어 파일 섹션
    private static final String[] LANGUAGE_SECTIONS = {
            "general", "gui", "items", "job", "talent", "stat", "messages", "commands", "status", "currency"
    };

    public LangManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.defaultLanguage = "ko_KR";
        loadLanguages();
    }

    /**
     * 언어 파일 로드
     */
    private void loadLanguages() {
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        // 기본 언어 파일 복사 및 로드
        loadLanguageFiles("ko_KR");
        loadLanguageFiles("en_US");

        // 캐시 초기화
        initializeCache();

        LogUtil.info("Language system initialized with " + languageConfigs.size() + " languages: " + languageConfigs.keySet());
    }

    /**
     * 특정 언어의 모든 섹션 파일 로드
     */
    private void loadLanguageFiles(@NotNull String langCode) {
        JsonObject combinedLang = new JsonObject();

        for (String section : LANGUAGE_SECTIONS) {
            String fileName = langCode + "/" + section + ".json";
            File langFile = new File(plugin.getDataFolder(), "lang/" + fileName);

            // 파일이 없으면 복사
            if (!langFile.exists()) {
                langFile.getParentFile().mkdirs(); // 언어 디렉토리 생성

                try (InputStream inputStream = plugin.getResource("lang/" + fileName)) {
                    if (inputStream != null) {
                        Files.copy(inputStream, langFile.toPath());
                        LogUtil.info("Created language file: " + fileName);
                    } else {
                        LogUtil.warning("Resource not found: lang/" + fileName);
                        continue;
                    }
                } catch (IOException e) {
                    LogUtil.error("Failed to copy language file " + fileName, e);
                    continue;
                }
            }

            // 파일 로드
            try (InputStreamReader reader = new InputStreamReader(
                    Files.newInputStream(langFile.toPath()), StandardCharsets.UTF_8)) {

                JsonObject sectionObject = gson.fromJson(reader, JsonObject.class);

                // 섹션을 최상위 키로 추가 (중첩 구조 유지)
                combinedLang.add(section, sectionObject);

                LogUtil.info("Loaded language section: " + fileName);

            } catch (IOException e) {
                LogUtil.error("Failed to load language file " + fileName, e);
            }
        }

        // 통합된 언어 데이터 저장
        languageConfigs.put(langCode, combinedLang);
    }

    /**
     * 캐시 초기화
     */
    private void initializeCache() {
        for (String lang : languageConfigs.keySet()) {
            messageCache.put(lang, new ConcurrentHashMap<>());
            listCache.put(lang, new ConcurrentHashMap<>());
        }
    }

    /**
     * 메시지 가져오기 (캐시 활용)
     */
    @NotNull
    public String getMessage(@NotNull Player player, @NotNull String key, @NotNull String... placeholders) {
        String lang = getPlayerLanguage(player);
        return getMessage(lang, key, placeholders);
    }

    /**
     * 메시지 가져오기 (캐시 활용)
     */
    @NotNull
    public String getMessage(@NotNull String language, @NotNull String key, @NotNull String... placeholders) {
        // 캐시 확인
        Map<String, String> langCache = messageCache.get(language);
        String cachedMessage = langCache != null ? langCache.get(key) : null;

        String message;
        if (cachedMessage != null) {
            message = cachedMessage;
        } else {
            message = getValueFromJson(language, key);
            if (message == null) {
                LogUtil.warning("Missing translation key: " + key + " (language: " + language + ")");
                return key;
            }

            // 캐시에 저장
            if (langCache != null && langCache.size() < MAX_CACHE_SIZE) {
                langCache.put(key, message);
            }
        }

        // 플레이스홀더 교체
        String result = message;
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                result = result.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }

        return result;
    }

    /**
     * Component 메시지 가져오기
     */
    @NotNull
    public Component getComponent(@NotNull Player player, @NotNull String key, @NotNull String... placeholders) {
        String message = getMessage(player, key, placeholders);
        return parseColorPlaceholders(message);
    }

    /**
     * Component 메시지 가져오기
     */
    @NotNull
    public Component getComponent(@NotNull String language, @NotNull String key, @NotNull String... placeholders) {
        String message = getMessage(language, key, placeholders);
        return parseColorPlaceholders(message);
    }

    /**
     * Component 리스트 가져오기 (캐시 활용)
     */
    @NotNull
    public List<Component> getComponentList(@NotNull Player player, @NotNull String key, @NotNull String... placeholders) {
        String lang = getPlayerLanguage(player);
        return getComponentList(lang, key, placeholders);
    }

    /**
     * Component 리스트 가져오기 (캐시 활용)
     */
    @NotNull
    public List<Component> getComponentList(@NotNull String language, @NotNull String key, @NotNull String... placeholders) {
        // 리스트 캐시 확인
        Map<String, List<String>> langListCache = listCache.get(language);
        List<String> cachedList = langListCache != null ? langListCache.get(key) : null;

        if (cachedList == null) {
            JsonElement element = getElementFromJson(language, key);

            if (element == null || !element.isJsonArray()) {
                LogUtil.warning("Missing or invalid translation key list: " + key + " (language: " + language + ")");
                return List.of(Component.text(key));
            }

            JsonArray jsonArray = element.getAsJsonArray();
            cachedList = new ArrayList<>();

            for (JsonElement arrayElement : jsonArray) {
                if (arrayElement.isJsonPrimitive()) {
                    cachedList.add(arrayElement.getAsString());
                }
            }

            // 캐시에 저장
            if (langListCache != null && langListCache.size() < MAX_CACHE_SIZE) {
                langListCache.put(key, cachedList);
            }
        }

        // 캐시된 리스트를 Component로 변환
        List<Component> components = new ArrayList<>();
        for (String message : cachedList) {
            String result = message;
            for (int i = 0; i < placeholders.length; i += 2) {
                if (i + 1 < placeholders.length) {
                    result = result.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
                }
            }
            components.add(parseColorPlaceholders(result));
        }

        return components;
    }

    /**
     * JSON에서 값 가져오기
     */
    @Nullable
    private String getValueFromJson(@NotNull String language, @NotNull String key) {
        JsonElement element = getElementFromJson(language, key);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : null;
    }

    /**
     * JSON에서 요소 가져오기 (폴백 로직 포함)
     */
    @Nullable
    private JsonElement getElementFromJson(@NotNull String language, @NotNull String key) {
        JsonElement element = navigateJsonPath(languageConfigs.get(language), key);

        // 기본 언어로 폴백
        if (element == null && !language.equals(defaultLanguage)) {
            element = navigateJsonPath(languageConfigs.get(defaultLanguage), key);
        }

        // 영어로 최종 폴백
        if (element == null && !language.equals("en_US")) {
            element = navigateJsonPath(languageConfigs.get("en_US"), key);
        }

        return element;
    }

    /**
     * JSON 경로 탐색 (점 표기법 사용)
     */
    @Nullable
    private JsonElement navigateJsonPath(@Nullable JsonObject jsonObject, @NotNull String path) {
        if (jsonObject == null) {
            return null;
        }

        String[] parts = path.split("\\.");
        JsonElement current = jsonObject;

        for (String part : parts) {
            if (current == null || !current.isJsonObject()) {
                return null;
            }
            current = current.getAsJsonObject().get(part);
        }

        return current;
    }

    /**
     * 색상 플레이스홀더 파싱 (Adventure API 사용)
     */
    @NotNull
    public Component parseColorPlaceholders(@NotNull String text) {
        if (text.isEmpty()) {
            return Component.empty();
        }

        // 플레이스홀더가 없으면 빠르게 반환
        if (!text.contains("%COLOR_")) {
            return Component.text(text);
        }

        TextComponent.Builder result = Component.text();
        Matcher matcher = COLOR_PATTERN.matcher(text);
        int lastEnd = 0;
        TextColor currentColor = NamedTextColor.WHITE;

        while (matcher.find()) {
            // 색상 태그 이전의 텍스트 추가
            if (matcher.start() > lastEnd) {
                String beforeText = text.substring(lastEnd, matcher.start());
                if (!beforeText.isEmpty()) {
                    result.append(Component.text(beforeText, currentColor));
                }
            }

            // 색상 이름 추출 및 적용
            String colorName = matcher.group(1);
            TextColor newColor = ColorUtil.fromName(colorName);
            if (newColor != null) {
                currentColor = newColor;
            } else {
                LogUtil.warning("Unknown color name in placeholder: " + colorName);
            }

            lastEnd = matcher.end();
        }

        // 마지막 남은 텍스트 추가
        if (lastEnd < text.length()) {
            String remainingText = text.substring(lastEnd);
            if (!remainingText.isEmpty()) {
                result.append(Component.text(remainingText, currentColor));
            }
        }

        return result.build();
    }

    /**
     * 플레이어 언어 가져오기 (player.locale() 사용)
     */
    @NotNull
    public String getPlayerLanguage(@NotNull Player player) {
        // 클라이언트 locale 실시간 감지
        String clientLocale = player.locale().toString();

        // 언어 코드 추출 (예: ko_KR, en_US 등)
        if (clientLocale.contains("_")) {
            String langCode = clientLocale.substring(0, 2);
            String countryCode = clientLocale.substring(3, 5).toUpperCase();
            String fullCode = langCode + "_" + countryCode;

            // 지원하는 언어인지 확인
            if (languageConfigs.containsKey(fullCode)) {
                return fullCode;
            }
        }

        // 언어 코드만으로 매칭 시도
        String langCode = clientLocale.substring(0, 2).toLowerCase();

        // 한국어 확인
        if (langCode.equals("ko")) {
            return "ko_KR";
        }

        // 영어 또는 기타 언어는 영어로
        return "en_US";
    }

    /**
     * 언어 파일 리로드
     */
    public void reload() {
        languageConfigs.clear();
        messageCache.clear();
        listCache.clear();
        loadLanguages();
        LogUtil.info("Language system reloaded");
    }

    /**
     * 사용 가능한 언어 목록
     */
    @NotNull
    public Set<String> getAvailableLanguages() {
        return Collections.unmodifiableSet(languageConfigs.keySet());
    }

    /**
     * 언어 사용 가능 여부 확인
     */
    public boolean isLanguageAvailable(@NotNull String language) {
        return languageConfigs.containsKey(language);
    }

    /**
     * 기본 언어 가져오기
     */
    @NotNull
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * 기본 언어 설정
     */
    public void setDefaultLanguage(@NotNull String language) {
        if (languageConfigs.containsKey(language)) {
            this.defaultLanguage = language;
        }
    }

    /**
     * 플레이어에게 메시지 전송
     */
    public void sendMessage(@NotNull Player player, @NotNull String key, @NotNull String... placeholders) {
        Component message = getComponent(player, key, placeholders);
        player.sendMessage(message);
    }

    /**
     * 모든 플레이어에게 브로드캐스트
     */
    public void broadcast(@NotNull String key, @NotNull String... placeholders) {
        plugin.getServer().getOnlinePlayers().forEach(player ->
                sendMessage(player, key, placeholders));
    }

    /**
     * 캐시 통계 가져오기 (디버깅용)
     */
    public Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new HashMap<>();
        messageCache.forEach((lang, cache) ->
                stats.put(lang + "_messages", cache.size()));
        listCache.forEach((lang, cache) ->
                stats.put(lang + "_lists", cache.size()));
        return stats;
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        messageCache.values().forEach(Map::clear);
        listCache.values().forEach(Map::clear);
    }
}