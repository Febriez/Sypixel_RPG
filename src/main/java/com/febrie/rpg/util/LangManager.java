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
    
    // 아이템 번역 전용 캐시 - 사전 로드된 번역 텍스트 저장
    private final Map<Locale, Map<String, String>> itemTranslations = new HashMap<>();
    
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
        loadItemTranslations();
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
                        // 파일 이름에서 .json 제거하여 접두사로 사용
                        String filePrefix = jsonFile.replace(".json", "");
                        int beforeSize = localeTranslations.size();
                        processJsonContent(stream, locale, localeTranslations, filePrefix);
                        int afterSize = localeTranslations.size();
                        int addedKeys = afterSize - beforeSize;
                        if (addedKeys > 0) {
                            plugin.getLogger().info("[" + langCode + "] " + jsonFile + ": " + addedKeys + "개 키 로드 (총 " + afterSize + "개)");
                        }
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
     * @param filePrefix 파일 이름 접두사 (ex: "gui", "messages")
     */
    private void processJsonContent(InputStream stream, Locale locale, Map<String, MessageFormat> localeTranslations, String filePrefix) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            Map<String, String> translations = flattenJson(jsonObject, filePrefix);
            registerTranslations(translations, locale, localeTranslations);
        }
    }

    /**
     * JSON 평면화
     * @param filePrefix 파일 이름 접두사
     */
    private @NotNull Map<String, String> flattenJson(JsonObject jsonObject, String filePrefix) {
        Map<String, String> result = new HashMap<>();
        // 파일 이름을 최상위 접두사로 사용
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
        itemTranslations.clear();

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
     * GUI 텍스트를 번역하여 Component로 반환
     * Component.translatable() 대신 사용
     * 
     * @param key 번역 키
     * @param locale 로케일
     * @param args 플레이스홀더 인수들
     * @return 번역되고 색상 코드가 적용된 Component
     */
    @NotNull
    public static Component getGuiText(@NotNull String key, @NotNull Locale locale, @NotNull Object... args) {
        if (instance == null) return Component.text(key);
        
        // 번역 가져오기
        MessageFormat format = instance.translate(key, locale);
        if (format == null) {
            // 번역이 없으면 키 반환
            instance.plugin.getLogger().warning("Missing translation key: " + key + " for locale: " + locale);
            return Component.text(key);
        }
        
        // 인수 포맷팅
        String translatedText;
        try {
            if (args.length > 0) {
                // Object를 String으로 변환
                Object[] stringArgs = new Object[args.length];
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Component) {
                        stringArgs[i] = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                                .serialize((Component) args[i]);
                    } else {
                        stringArgs[i] = String.valueOf(args[i]);
                    }
                }
                translatedText = format.format(stringArgs);
            } else {
                translatedText = format.format(new Object[0]);
            }
        } catch (Exception e) {
            instance.plugin.getLogger().warning("Failed to format translation for key: " + key + " - " + e.getMessage());
            return Component.text(key);
        }
        
        // 색상 코드 처리
        return UnifiedColorUtil.parse(translatedText);
    }
    
    /**
     * GUI 텍스트를 번역하여 Component로 반환 (인수 없음)
     * 
     * @param key 번역 키
     * @param locale 로케일
     * @return 번역되고 색상 코드가 적용된 Component
     */
    @NotNull
    public static Component getGuiText(@NotNull String key, @NotNull Locale locale) {
        return getGuiText(key, locale, new Object[0]);
    }

    /**
     * 정적 초기화 메서드
     */
    public static void initialize(@NotNull JavaPlugin plugin) {
        LangManager manager = new LangManager(plugin);
        LangManager.instance = manager;
        manager.initialize();
    }

    /**
     * 정적 리로드 메서드
     */
    public static void reload() {
        if (instance != null) {
            instance.reloadInstance();
        }
    }

    /**
     * 정적 shutdown 메서드
     */
    public static void shutdown() {
        if (instance != null) {
            instance.shutdownInstance();
        }
    }
    
    /**
     * 아이템 번역 파일을 별도로 로드하여 메모리에 캐시
     * items.json 파일의 모든 번역을 사전 로드
     */
    private void loadItemTranslations() {
        String[] languageCodes = {"en_us", "ko_kr"};
        
        for (String langCode : languageCodes) {
            Locale locale = createLocale(langCode);
            Map<String, String> localeItemTranslations = itemTranslations.computeIfAbsent(locale, k -> new HashMap<>());
            
            String resourcePath = "/" + langCode + "/items.json";
            try (InputStream stream = plugin.getClass().getResourceAsStream(resourcePath)) {
                if (stream != null) {
                    try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                        // items.json의 구조를 평면화하여 저장 - "items" 프리픽스 추가
                        flattenItemJson(jsonObject, "items", localeItemTranslations);
                        plugin.getLogger().info("[" + langCode + "] items.json: " + localeItemTranslations.size() + "개 아이템 번역 로드");
                    }
                } else {
                    plugin.getLogger().warning("아이템 번역 파일을 찾을 수 없음: " + resourcePath);
                }
            } catch (Exception e) {
                plugin.getLogger().severe("아이템 번역 로드 실패: " + resourcePath + " - " + e.getMessage());
            }
        }
    }
    
    /**
     * 아이템 JSON을 평면화하여 저장
     * name과 lore를 구분하여 저장
     */
    private void flattenItemJson(JsonElement element, String prefix, Map<String, String> result) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            obj.entrySet().forEach(entry -> {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                
                if (key.equals("name") && value.isJsonPrimitive()) {
                    // name 키는 직접 저장
                    String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
                    result.put(fullKey, value.getAsString());
                } else if (key.equals("lore") && value.isJsonArray()) {
                    // lore 배열은 각 줄을 별도로 저장
                    JsonArray loreArray = value.getAsJsonArray();
                    String lorePrefix = prefix.isEmpty() ? "lore" : prefix + ".lore";
                    for (int i = 0; i < loreArray.size(); i++) {
                        result.put(lorePrefix + "." + i, loreArray.get(i).getAsString());
                    }
                    // lore 전체 개수도 저장
                    result.put(lorePrefix + ".size", String.valueOf(loreArray.size()));
                } else {
                    // 다른 키는 재귀적으로 처리
                    String newPrefix = prefix.isEmpty() ? key : prefix + "." + key;
                    flattenItemJson(value, newPrefix, result);
                }
            });
        }
    }
    
    /**
     * 아이템 이름 번역 가져오기
     * @param key 번역 키 (예: "items.mainmenu.profile-button.name")
     * @param locale 로케일
     * @return 번역된 텍스트 또는 키 자체 (번역이 없는 경우)
     */
    @NotNull
    public static String getItemName(@NotNull String key, @NotNull Locale locale) {
        if (instance == null) return key;
        
        Map<String, String> localeItems = instance.itemTranslations.get(locale);
        if (localeItems != null && localeItems.containsKey(key)) {
            return localeItems.get(key);
        }
        
        // 언어만 맞는 로케일 찾기 (예: en_GB -> en_US)
        for (Map.Entry<Locale, Map<String, String>> entry : instance.itemTranslations.entrySet()) {
            if (entry.getKey().getLanguage().equals(locale.getLanguage())) {
                String translation = entry.getValue().get(key);
                if (translation != null) {
                    return translation;
                }
            }
        }
        
        // 기본 영어로 fallback
        Map<String, String> englishItems = instance.itemTranslations.get(Locale.US);
        if (englishItems != null && englishItems.containsKey(key)) {
            return englishItems.get(key);
        }
        
        return key; // 번역이 없으면 키 반환
    }
    
    /**
     * 아이템 설명(lore) 번역 가져오기
     * @param key 번역 키 (예: "items.mainmenu.profile-button.lore")
     * @param locale 로케일
     * @return 번역된 lore 리스트
     */
    @NotNull
    public static List<String> getItemLore(@NotNull String key, @NotNull Locale locale) {
        if (instance == null) return new ArrayList<>();
        
        List<String> loreLines = new ArrayList<>();
        Map<String, String> localeItems = instance.itemTranslations.get(locale);
        
        // fallback 체인: 정확한 로케일 -> 같은 언어 -> 영어
        if (localeItems == null) {
            // 언어만 맞는 로케일 찾기
            for (Map.Entry<Locale, Map<String, String>> entry : instance.itemTranslations.entrySet()) {
                if (entry.getKey().getLanguage().equals(locale.getLanguage())) {
                    localeItems = entry.getValue();
                    break;
                }
            }
        }
        
        if (localeItems == null) {
            localeItems = instance.itemTranslations.get(Locale.US);
        }
        
        if (localeItems != null) {
            // lore 크기 확인
            String sizeStr = localeItems.get(key + ".size");
            if (sizeStr != null) {
                try {
                    int size = Integer.parseInt(sizeStr);
                    for (int i = 0; i < size; i++) {
                        String line = localeItems.get(key + "." + i);
                        if (line != null) {
                            loreLines.add(line);
                        }
                    }
                } catch (NumberFormatException ignored) {
                    // 크기 파싱 실패 시 무시
                }
            }
        }
        
        return loreLines;
    }
    
    /**
     * 아이템 번역 Component 생성 헬퍼
     * @param key 번역 키
     * @param locale 로케일
     * @return 번역된 Component (색상 코드 적용됨)
     */
    @NotNull
    public static Component getItemComponent(@NotNull String key, @NotNull Locale locale) {
        String translatedText = getItemName(key, locale);
        
        // 키가 그대로 반환되었다면 번역이 없는 것
        if (translatedText.equals(key) && instance != null) {
            instance.plugin.getLogger().warning("Missing item translation key: " + key + " for locale: " + locale);
        }
        
        // 색상 코드 처리
        if (translatedText.contains("&") || translatedText.contains("§") || translatedText.contains("&#")) {
            return parseTranslation(translatedText, locale);
        }
        
        // 색상 코드가 있으면 UnifiedColorUtil로 파싱
        if (translatedText.contains("&") || translatedText.contains("§") || translatedText.contains("&#")) {
            return UnifiedColorUtil.parse(translatedText);
        }
        
        return Component.text(translatedText);
    }
    
    /**
     * 아이템 lore Component 리스트 생성 헬퍼
     * @param key 번역 키
     * @param locale 로케일
     * @return 번역된 Component 리스트 (색상 코드 적용됨)
     */
    @NotNull
    public static List<Component> getItemLoreComponents(@NotNull String key, @NotNull Locale locale) {
        List<String> loreLines = getItemLore(key, locale);
        List<Component> components = new ArrayList<>();
        
        // lore가 비어있다면 번역이 없는 것
        if (loreLines.isEmpty() && instance != null) {
            instance.plugin.getLogger().warning("Missing item lore translation key: " + key + " for locale: " + locale);
        }
        
        for (String line : loreLines) {
            // 색상 코드 처리
            if (line.contains("&") || line.contains("§") || line.contains("&#")) {
                // 색상 코드가 있으면 UnifiedColorUtil로 파싱
                components.add(UnifiedColorUtil.parse(line));
            } else {
                components.add(Component.text(line));
            }
        }
        
        return components;
    }

    /**
     * 아이템 lore 번역을 가져옴 (플레이스홀더 지원)
     * {0}, {1}, {2} 형식의 플레이스홀더를 제공된 값으로 치환
     * 
     * @param key 번역 키
     * @param locale 로케일
     * @param placeholders 플레이스홀더 값들
     * @return 번역된 Component 리스트 (색상 코드 및 플레이스홀더 적용됨)
     */
    @NotNull
    public static List<Component> getItemLoreComponents(@NotNull String key, @NotNull Locale locale, @NotNull String... placeholders) {
        List<String> loreLines = getItemLore(key, locale);
        List<Component> components = new ArrayList<>();
        
        // lore가 비어있다면 번역이 없는 것
        if (loreLines.isEmpty() && instance != null) {
            instance.plugin.getLogger().warning("Missing item lore translation key: " + key + " for locale: " + locale);
        }
        
        for (String line : loreLines) {
            // 플레이스홀더 치환
            String processedLine = line;
            for (int i = 0; i < placeholders.length; i++) {
                processedLine = processedLine.replace("{" + i + "}", placeholders[i]);
            }
            
            // 색상 코드 처리
            if (processedLine.contains("&") || processedLine.contains("§") || processedLine.contains("&#")) {
                // 색상 코드가 있으면 UnifiedColorUtil로 파싱
                components.add(UnifiedColorUtil.parse(processedLine));
            } else {
                components.add(Component.text(processedLine));
            }
        }
        
        return components;
    }

    /**
     * GUI 텍스트를 번역하여 String으로 반환
     * 
     * @param key 번역 키
     * @param locale 로케일
     * @param args 플레이스홀더 인수들
     * @return 번역된 문자열
     */
    @NotNull
    public static String getGuiString(@NotNull String key, @NotNull Locale locale, @NotNull Object... args) {
        Component component = getGuiText(key, locale, args);
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(component);
    }
    
    /**
     * 플레이어 로케일로 번역된 Component 가져오기
     */
    @NotNull
    public static Component get(@NotNull String key, @NotNull org.bukkit.entity.Player player) {
        return GlobalTranslator.renderer().render(Component.translatable(key), player.locale());
    }
    
    /**
     * 플레이어 로케일로 번역된 Component 가져오기 (인수 포함)
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
     * 플레이어 로케일로 번역된 String 가져오기
     */
    @NotNull
    public static String getString(@NotNull String key, @NotNull org.bukkit.entity.Player player) {
        Component component = get(key, player);
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(component);
    }
    
    /**
     * 플레이어 로케일로 번역된 String 가져오기 (인수 포함)
     */
    @NotNull
    public static String getString(@NotNull String key, @NotNull org.bukkit.entity.Player player, @NotNull Object... args) {
        Component component = get(key, player, args);
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(component);
    }
    
    /**
     * 특정 로케일로 번역된 Component 가져오기
     */
    @NotNull
    public static Component getComponent(@NotNull String key, @NotNull Locale locale, @NotNull Object... args) {
        return getGuiText(key, locale, args);
    }
    
    /**
     * 특정 키가 존재하는지 확인
     */
    public static boolean hasKey(@NotNull String key, @NotNull Locale locale) {
        if (instance == null) return false;
        Map<String, MessageFormat> localeTranslations = instance.translations.get(locale);
        return localeTranslations != null && localeTranslations.containsKey(key);
    }
    
    /**
     * 모든 등록된 키 반환
     */
    @NotNull
    public static Set<String> getAllKeys(@NotNull Locale locale) {
        if (instance == null) return new HashSet<>();
        Map<String, MessageFormat> localeTranslations = instance.translations.get(locale);
        return localeTranslations != null ? new HashSet<>(localeTranslations.keySet()) : new HashSet<>();
    }
    
    /**
     * 디버그 모드 토글 (더미 메서드)
     */
    public static boolean toggleDebugMode() {
        // 새 구현에서는 디버그 모드를 지원하지 않음
        return false;
    }
    
    /**
     * 플레이어 로케일로 번역된 Component 리스트 가져오기
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
     * 번역된 텍스트를 색상 코드 파싱
     */
    @NotNull
    private static Component parseTranslation(@NotNull String translatedText, @NotNull Locale locale) {
        // 색상 코드가 있으면 UnifiedColorUtil로 파싱
        return UnifiedColorUtil.parse(translatedText);
    }
}