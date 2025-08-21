package com.febrie.rpg.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;

/**
 * Adventure API 기반 다국어 시스템
 * Translator 인터페이스 직접 구현으로 deprecated API 제거
 *
 * @author Febrie, CoffeeTory
 */
public class LangManager implements Translator {

    private final JavaPlugin plugin;
    private final Gson gson = new Gson();
    private final Key translatorKey;
    private final Map<Locale, Map<String, MessageFormat>> translations = new HashMap<>();
    private static LangManager instance;

    /**
     * LangManager 생성자
     *
     * @param plugin 플러그인 인스턴스
     */
    private LangManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.translatorKey = Key.key(plugin.getName().toLowerCase(), "translations");
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
    private void initialize() {
        loadAllTranslations();
        registerToGlobalTranslator();
    }

    /**
     * GlobalTranslator에 등록
     */
    private void registerToGlobalTranslator() {
        GlobalTranslator.translator().addSource(this);
        plugin.getLogger().info("번역 시스템이 GlobalTranslator에 등록되었습니다.");
    }

    /**
     * Translator 인터페이스 구현 - name
     */
    @Override
    public @NotNull Key name() {
        return translatorKey;
    }

    /**
     * Translator 인터페이스 구현 - translate
     * Component.translatable()이 호출될 때 이 메서드가 호출됨
     */
    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        // 정확한 로케일 매칭
        Map<String, MessageFormat> localeTranslations = translations.get(locale);
        if (localeTranslations != null) {
            MessageFormat format = localeTranslations.get(key);
            if (format != null) {
                return format;
            }
        }

        // 언어만 맞는 로케일 찾기 (예: en_GB -> en)
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
            MessageFormat format = englishTranslations.get(key);
            if (format != null) {
                return format;
            }
        }

        // US 영어도 시도
        Map<String, MessageFormat> usTranslations = translations.get(Locale.US);
        if (usTranslations != null) {
            return usTranslations.get(key);
        }

        return null;
    }

    /**
     * 모든 번역 파일 로드
     */
    private void loadAllTranslations() {
        // 지원하는 언어 코드
        String[] languageCodes = {"en_us", "ko_kr"};

        // 각 언어별 JSON 파일들
        String[] jsonFiles = {"colors.json", "commands.json", "currency.json", "dialog.json", "general.json", "gui.json", "island.json", "items.json", "job.json", "messages.json", "quest.json", "stat.json", "status.json", "talent.json"};

        for (String langCode : languageCodes) {
            Locale locale = createLocale(langCode);
            Map<String, MessageFormat> localeTranslations = translations.computeIfAbsent(locale, k -> new HashMap<>());
            int loadedFiles = 0;

            for (String jsonFile : jsonFiles) {
                String resourcePath = "/" + langCode + "/" + jsonFile;
                try (InputStream stream = plugin.getClass().getResourceAsStream(resourcePath)) {
                    if (stream != null) {
                        processJsonContent(stream, locale, localeTranslations);
                        loadedFiles++;
                    } else {
                        plugin.getLogger().warning("리소스를 찾을 수 없음: " + resourcePath);
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
     */
    private void processJsonContent(InputStream stream, Locale locale, Map<String, MessageFormat> localeTranslations) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            Map<String, String> translations = flattenJson(jsonObject);
            registerTranslations(translations, locale, localeTranslations);
        }
    }

    /**
     * JSON 평면화
     */
    private @NotNull Map<String, String> flattenJson(JsonObject jsonObject) {
        Map<String, String> result = new HashMap<>();
        flattenJsonRecursive(jsonObject, "", result);
        return result;
    }

    /**
     * JSON 재귀적 평면화
     */
    private void flattenJsonRecursive(@NotNull JsonElement element, String prefix, Map<String, String> result) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            obj.entrySet().forEach(entry -> {
                String key = buildKey(prefix, entry.getKey());
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
     * 키 생성
     */
    @Contract(pure = true)
    private String buildKey(@NotNull String prefix, String key) {
        return prefix.isEmpty() ? key : prefix + "." + key;
    }

    /**
     * 번역 등록
     */
    private void registerTranslations(@NotNull Map<String, String> translations, Locale locale, Map<String, MessageFormat> localeTranslations) {
        translations.forEach((key, value) -> {
            try {
                // 색상 코드를 모두 제거한 순수한 텍스트로 저장
                String cleanValue = removeColorCodes(value);
                MessageFormat messageFormat = new MessageFormat(cleanValue, locale);
                localeTranslations.put(key, messageFormat);
            } catch (Exception e) {
                plugin.getLogger().warning("번역 등록 실패 [" + locale + "] " + key + ": " + e.getMessage());
            }
        });
        
        // 디버깅: 등록된 키 개수 출력
        if (!localeTranslations.isEmpty()) {
            plugin.getLogger().info("등록된 번역 키 (" + locale + "): " + localeTranslations.size() + "개");
        }
    }
    
    /**
     * 색상 코드 제거
     */
    private String removeColorCodes(@NotNull String text) {
        // %COLOR_XXX% 형식 제거
        String cleaned = text.replaceAll("%COLOR_[A-Z_]+%", "");
        
        // & 색상 코드 제거 (&a, &1, &k 등)
        cleaned = cleaned.replaceAll("&[0-9a-fk-or]", "");
        
        // # HEX 색상 코드 제거 (&#FFFFFF 형식)
        cleaned = cleaned.replaceAll("&#[0-9A-Fa-f]{6}", "");
        
        // 섹션 기호 색상 코드 제거
        cleaned = cleaned.replaceAll("§[0-9a-fk-or]", "");
        
        // 연속된 공백을 하나로 정리
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        
        return cleaned;
    }

    /**
     * Locale 생성
     */
    private Locale createLocale(@NotNull String langCode) {
        // xx_xx 형식을 xx-xx로 변환하여 Locale 생성
        return Locale.forLanguageTag(langCode.replace("_", "-"));
    }

    /**
     * Component.translatable 생성 헬퍼 메서드
     */
    @NotNull
    public Component translate(@NotNull String key, @NotNull Component... args) {
        return Component.translatable(key, args);
    }

    /**
     * 리스트 번역 헬퍼 메서드
     */
    @NotNull
    public List<Component> translateList(@NotNull String baseKey, int maxLines) {
        List<Component> result = new ArrayList<>();
        for (int i = 0; i < maxLines; i++) {
            String lineKey = baseKey + "." + i;
            result.add(Component.translatable(lineKey));
        }
        return result;
    }

    /**
     * 리로드 (인스턴스 메서드)
     */
    private void reloadInstance() {
        // GlobalTranslator에서 기존 인스턴스 제거
        GlobalTranslator.translator().removeSource(this);

        // 번역 데이터 초기화
        translations.clear();

        // 재초기화
        initialize();
        plugin.getLogger().info("번역 시스템이 리로드되었습니다.");
    }

    /**
     * 종료 시 정리 (인스턴스 메서드)
     */
    private void shutdownInstance() {
        GlobalTranslator.translator().removeSource(this);
        plugin.getLogger().info("번역 시스템이 종료되었습니다.");
    }

    /**
     * 특정 키가 존재하는지 확인 (정적 메서드)
     */
    public static boolean hasKey(@NotNull String key, @NotNull Locale locale) {
        if (instance == null) return false;
        Map<String, MessageFormat> localeTranslations = instance.translations.get(locale);
        return localeTranslations != null && localeTranslations.containsKey(key);
    }

    /**
     * 색상 키로부터 색상 코드 가져오기 (호환성 유지)
     */
    @NotNull
    public static String getColorCode(@NotNull String colorKey, @NotNull Locale locale) {
        if (instance == null) return "&f";

        String normalizedKey = colorKey;

        // COLOR_ 접두사 제거
        if (normalizedKey.startsWith("COLOR_")) {
            normalizedKey = normalizedKey.substring(6).toLowerCase();
        }

        // colors. 접두사 확인 및 추가
        if (!normalizedKey.startsWith("colors.")) {
            normalizedKey = "colors." + normalizedKey;
        }

        // translate 메서드를 통해 색상 코드 가져오기
        MessageFormat format = instance.translate(normalizedKey, locale);
        if (format != null) {
            return format.format(new Object[0]);
        }

        // 기본값 반환
        return "&f"; // 흰색
    }

    /**
     * 번역된 텍스트를 UnifiedColorUtil로 파싱하여 색상 코드 적용 (호환성 유지)
     */
    @NotNull
    public static Component parseTranslation(@NotNull String translatedText, @NotNull Locale locale) {
        String processedText = translatedText;

        // 색상 플레이스홀더 처리
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{(COLOR_[^}]+|colors\\.[^}]+)}");
        java.util.regex.Matcher matcher = pattern.matcher(processedText);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String colorKey = matcher.group(1);
            String colorCode = getColorCode(colorKey, locale);
            matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(colorCode));
        }
        matcher.appendTail(sb);
        processedText = sb.toString();

        // 레거시 %COLOR_KEY% 형식도 지원
        java.util.regex.Pattern legacyPattern = java.util.regex.Pattern.compile("%COLOR_([^%]+)%");
        java.util.regex.Matcher legacyMatcher = legacyPattern.matcher(processedText);
        StringBuilder legacySb = new StringBuilder();

        while (legacyMatcher.find()) {
            String colorKey = legacyMatcher.group(1).toLowerCase();
            String colorCode = getColorCode(colorKey, locale);
            legacyMatcher.appendReplacement(legacySb, java.util.regex.Matcher.quoteReplacement(colorCode));
        }
        legacyMatcher.appendTail(legacySb);
        processedText = legacySb.toString();

        // UnifiedColorUtil을 사용하여 색상 코드 파싱
        return UnifiedColorUtil.parse(processedText);
    }

    /**
     * 번역 키로부터 색상이 적용된 Component 가져오기 (호환성 유지)
     */
    @NotNull
    public static Component getComponent(@NotNull String key, @NotNull Locale locale, @NotNull Object... args) {
        // Component.translatable 생성
        Component translatable;
        if (args.length == 0) {
            translatable = Component.translatable(key);
        } else {
            // Object[]를 Component[]로 변환
            Component[] componentArgs = new Component[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Component) {
                    componentArgs[i] = (Component) args[i];
                } else {
                    componentArgs[i] = Component.text(String.valueOf(args[i]));
                }
            }
            translatable = Component.translatable(key, componentArgs);
        }

        // GlobalTranslator를 통해 렌더링
        Component rendered = GlobalTranslator.renderer().render(translatable, locale);

        // 렌더링된 텍스트에서 색상 코드 처리
        String plainText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(rendered);
        if (plainText.contains("%COLOR_") || plainText.contains("{COLOR_") || plainText.contains("{colors.")) {
            return parseTranslation(plainText, locale);
        }

        return rendered;
    }

    /**
     * 기본 로케일로 번역된 Component 가져오기 (호환성 유지)
     */
    @NotNull
    public static Component getComponent(@NotNull String key, @NotNull Object... args) {
        return getComponent(key, Locale.ENGLISH, args);
    }

    /**
     * 정적 초기화 메서드 (기존 코드 호환성)
     */
    public static void initialize(@NotNull JavaPlugin plugin) {
        LangManager manager = new LangManager(plugin);
        LangManager.instance = manager;
        manager.initialize();
    }

    /**
     * 정적 리로드 메서드 (기존 코드 호환성)
     */
    public static void reload() {
        if (instance != null) {
            instance.reloadInstance();
        }
    }

    /**
     * 정적 shutdown 메서드 (기존 코드 호환성)
     */
    public static void shutdown() {
        if (instance != null) {
            instance.shutdownInstance();
        }
    }

    /**
     * get 메서드 (기존 코드 호환성)
     * 플레이어의 로케일을 사용하여 번역된 Component 반환
     */
    @NotNull
    public static Component get(@NotNull String key, @NotNull org.bukkit.entity.Player player) {
        return GlobalTranslator.renderer().render(Component.translatable(key), player.locale());
    }

    /**
     * get 메서드 (기존 코드 호환성)
     * 플레이어의 로케일을 사용하여 번역된 Component 반환 (인수 포함)
     */
    @NotNull
    public static Component get(@NotNull String key, @NotNull org.bukkit.entity.Player player, @NotNull Object... args) {
        Component[] componentArgs = new Component[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Component) {
                componentArgs[i] = (Component) args[i];
            } else {
                componentArgs[i] = Component.text(String.valueOf(args[i]));
            }
        }
        return GlobalTranslator.renderer().render(Component.translatable(key, componentArgs), player.locale());
    }

    /**
     * getList 메서드 (기존 코드 호환성)
     * 플레이어의 로케일을 사용하여 번역된 Component 리스트 반환
     */
    @NotNull
    public static List<Component> getList(@NotNull String baseKey, @NotNull org.bukkit.entity.Player player) {
        List<Component> result = new ArrayList<>();
        int i = 0;
        while (i < 100) { // 최대 100줄까지 시도
            String lineKey = baseKey + "." + i;
            // 키가 존재하는지 확인
            Component translatable = Component.translatable(lineKey);
            Component rendered = GlobalTranslator.renderer().render(translatable, player.locale());

            // 렌더링된 텍스트가 키와 동일하면 번역이 없는 것으로 간주
            String plainText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                    .serialize(rendered);
            if (plainText.equals(lineKey)) {
                break; // 더 이상 번역이 없음
            }

            result.add(rendered);
            i++;
        }
        return result;
    }

    /**
     * getList 메서드 (기존 코드 호환성)
     * 최대 줄 수를 지정하여 번역된 Component 리스트 반환
     */
    @NotNull
    public static List<Component> getList(@NotNull String baseKey, @NotNull org.bukkit.entity.Player player, int maxLines) {
        List<Component> result = new ArrayList<>();
        for (int i = 0; i < maxLines; i++) {
            String lineKey = baseKey + "." + i;
            result.add(GlobalTranslator.renderer().render(Component.translatable(lineKey), player.locale()));
        }
        return result;
    }

    /**
     * getAllKeys 메서드 (기존 코드 호환성)
     * 모든 등록된 키 반환
     */
    @NotNull
    public static Set<String> getAllKeys(@NotNull Locale locale) {
        if (instance == null) return new HashSet<>();
        Map<String, MessageFormat> localeTranslations = instance.translations.get(locale);
        return localeTranslations != null ? new HashSet<>(localeTranslations.keySet()) : new HashSet<>();
    }

    /**
     * toggleDebugMode 메서드 (기존 코드 호환성)
     * 디버그 모드 토글 (새 구현에서는 미지원)
     */
    public static boolean toggleDebugMode() {
        // 새 구현에서는 디버그 모드를 지원하지 않음
        return false;
    }

    /**
     * getString 메서드 추가 (GUI 클래스들이 String을 기대하는 경우를 위해)
     * Component를 plain text로 변환
     */
    @NotNull
    public static String getString(@NotNull String key, @NotNull org.bukkit.entity.Player player) {
        Component component = get(key, player);
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(component);
    }

    /**
     * getString 메서드 추가 (인수 포함)
     */
    @NotNull
    public static String getString(@NotNull String key, @NotNull org.bukkit.entity.Player player, @NotNull Object... args) {
        Component component = get(key, player, args);
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(component);
    }
}