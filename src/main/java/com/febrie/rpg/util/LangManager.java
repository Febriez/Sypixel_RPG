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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 초경량 언어 관리 시스템
 * Component 사전 파싱으로 극한의 성능 최적화
 * Static 메서드로 메모리 효율성 극대화
 */
public class LangManager {

    private static RPGMain plugin;
    private static final Gson gson = new Gson();
    private static final Map<String, JsonObject> configs = new HashMap<>();

    // 키 인덱싱
    private static Map<String, Integer> keyIndex;

    // Component 배열 (색상 이미 파싱됨)
    private static Component[] dataKO;
    private static Component[] dataEN;
    private static Component[][] listKO;
    private static Component[][] listEN;

    // 플레이스홀더용 원본 텍스트
    private static String[] rawKO;
    private static String[] rawEN;

    private static final String[] SECTIONS = {"general", "gui", "items", "job", "talent", "stat", "messages", "commands", "status", "currency", "quest", "island"};

    /**
     * 초기화 (플러그인 시작 시 한 번만 호출)
     */
    public static void initialize(@NotNull RPGMain pluginInstance) {
        plugin = pluginInstance;
        loadLanguages();
    }

    private static void loadLanguages() {
        // JSON 로드
        loadJson("ko_KR");
        loadJson("en_US");

        // 키 수집 및 인덱싱
        Set<String> keys = new HashSet<>();
        configs.values()
                .forEach(cfg -> collectKeys(cfg, "", keys));

        keyIndex = new HashMap<>();
        int idx = 0;
        for (String key : keys) {
            keyIndex.put(key, idx++);
        }

        // 배열 할당
        int size = keys.size();
        dataKO = new Component[size];
        dataEN = new Component[size];
        listKO = new Component[size][];
        listEN = new Component[size][];
        rawKO = new String[size];
        rawEN = new String[size];

        // 데이터 로드 및 Component 변환
        for (Map.Entry<String, Integer> e : keyIndex.entrySet()) {
            loadData("ko_KR", e.getKey(), e.getValue());
            loadData("en_US", e.getKey(), e.getValue());
        }
    }

    private static void loadJson(String lang) {
        JsonObject combined = new JsonObject();
        for (String section : SECTIONS) {
            try (InputStream is = plugin.getResource("lang/" + lang + "/" + section + ".json")) {
                if (is != null) {
                    JsonObject obj = gson.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), JsonObject.class);
                    // 섹션 이름을 prefix로 사용하여 모든 키를 플랫하게 저장
                    flattenJson(obj, section, combined);
                }
            } catch (Exception ignored) {
            }
        }
        configs.put(lang, combined);
    }
    
    private static void flattenJson(JsonObject source, String prefix, JsonObject target) {
        for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
            String key = prefix + "." + entry.getKey();
            JsonElement value = entry.getValue();
            
            if (value.isJsonObject()) {
                // 중첩된 객체는 재귀적으로 처리
                flattenJson(value.getAsJsonObject(), key, target);
            } else {
                // 기본값이나 배열은 그대로 저장
                target.add(key, value);
            }
        }
    }

    private static void collectKeys(@NotNull JsonObject obj, String prefix, Set<String> keys) {
        for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
            // 이미 플랫하게 저장되어 있으므로 키를 그대로 추가
            keys.add(e.getKey());
        }
    }

    private static void loadData(String lang, String key, int idx) {
        JsonElement elem = getJson(configs.get(lang), key);
        if (elem == null) return;

        boolean isKO = lang.equals("ko_KR");

        if (elem.isJsonArray()) {
            JsonArray arr = elem.getAsJsonArray();
            Component[] list = new Component[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                String text = arr.get(i)
                        .getAsString();
                list[i] = parseComponent(text);
                if (text.contains("{")) {
                    if (isKO) rawKO[idx] = text;
                    else rawEN[idx] = text;
                }
            }
            if (isKO) listKO[idx] = list;
            else listEN[idx] = list;
        } else {
            String text = elem.getAsString();
            Component comp = parseComponent(text);
            if (text.contains("{")) {
                if (isKO) rawKO[idx] = text;
                else rawEN[idx] = text;
            }
            if (isKO) dataKO[idx] = comp;
            else dataEN[idx] = comp;
        }
    }

    private static Component parseComponent(@NotNull String text) {
        if (!text.contains("%COLOR_")) {
            return Component.text(text);
        }

        TextComponent.Builder result = Component.text();
        TextColor color = NamedTextColor.WHITE;
        int pos = 0;

        while (pos < text.length()) {
            int start = text.indexOf("%COLOR_", pos);
            if (start == -1) {
                result.append(Component.text(text.substring(pos), color));
                break;
            }

            if (start > pos) {
                result.append(Component.text(text.substring(pos, start), color));
            }

            int end = text.indexOf('%', start + 7);
            if (end == -1) break;

            color = UnifiedColorUtil.fromName(text.substring(start + 7, end));
            pos = end + 1;
        }

        return result.build();
    }

    // 플레이스홀더 처리 헬퍼 메서드
    @Contract(pure = true)
    private static String processPlaceholders(@NotNull String raw, String @NotNull ... args) {
        if (args.length == 0) return raw;

        String result = raw;
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                result = result.replace("{" + args[i] + "}", args[i + 1]);
            }
        }
        return result;
    }

    // ========== 핵심 Static Component 반환 메서드들 ==========

    /**
     * Player 기반 메시지 가져오기
     */
    @NotNull
    public static Component getMessage(@NotNull Player player, @NotNull String key, String... args) {
        Integer idx = keyIndex.get(key);
        if (idx == null) {
            plugin.getLogger().warning("Missing translation key: " + key);
            return Component.text(key);
        }

        boolean ko = player.locale()
                .getLanguage()
                .equals("ko");

        // 플레이스홀더 있으면 원본에서 처리
        if (args.length > 0) {
            String raw = ko ? rawKO[idx] : rawEN[idx];
            if (raw != null) {
                return parseComponent(processPlaceholders(raw, args));
            }
        }

        // 이미 파싱된 Component 반환
        Component data = ko ? dataKO[idx] : dataEN[idx];
        if (data != null) return data;

        Component[] list = ko ? listKO[idx] : listEN[idx];
        return (list != null && list.length > 0) ? list[0] : Component.text(key);
    }

    /**
     * Player 기반 리스트 가져오기
     */
    @NotNull
    public static List<Component> getList(@NotNull Player player, @NotNull String key) {
        Integer idx = keyIndex.get(key);
        if (idx == null) {
            plugin.getLogger().warning("Missing translation key: " + key);
            return List.of(Component.text(key));
        }

        boolean ko = player.locale()
                .getLanguage()
                .equals("ko");
        Component[] list = ko ? listKO[idx] : listEN[idx];

        if (list != null) return Arrays.asList(list);

        Component single = ko ? dataKO[idx] : dataEN[idx];
        return single != null ? List.of(single) : List.of(Component.text(key));
    }

    /**
     * CommandSender용 메시지 가져오기 (콘솔 지원)
     */
    @NotNull
    public static Component getComponent(@NotNull CommandSender sender, @NotNull String key, String... args) {
        if (sender instanceof Player player) return getMessage(player, key, args);

        // 콘솔은 영어로 기본 처리
        Integer idx = keyIndex.get(key);
        if (idx == null) {
            plugin.getLogger().warning("Missing translation key: " + key);
            return Component.text(key);
        }

        // 플레이스홀더 처리
        if (args.length > 0) {
            String raw = rawEN[idx];
            if (raw != null) return parseComponent(processPlaceholders(raw, args));
        }

        Component data = dataEN[idx];
        if (data != null) return data;

        Component[] list = listEN[idx];
        return (list != null && list.length > 0) ? list[0] : Component.text(key);
    }



    /**
     * 리로드
     */
    public static void reload() {
        configs.clear();
        loadLanguages();
    }

    // ========== 내부 헬퍼 메서드 ==========

    private static @Nullable JsonElement getJson(JsonObject obj, @NotNull String path) {
        // 이미 플랫하게 저장되어 있으므로 직접 키로 조회
        return obj.get(path);
    }

    // ========== 추가 유틸리티 메서드 ==========

    /**
     * 플레이어에게 메시지 전송
     */
    public static void sendMessage(@NotNull Player player, @NotNull String key, String... args) {
        player.sendMessage(getMessage(player, key, args));
    }

    /**
     * CommandSender에게 메시지 전송
     */
    public static void sendMessage(@NotNull CommandSender sender, @NotNull String key, String... args) {
        if (sender instanceof Player player) {
            sendMessage(player, key, args);
        } else {
            sender.sendMessage(getComponent(sender, key, args));
        }
    }

    /**
     * Get plain text string from language key (static method)
     *
     * @param player player for locale
     * @param key    language key
     * @param args   arguments
     * @return plain text string
     */
    public static @NotNull String getString(@NotNull Player player, @NotNull String key, String... args) {
        Component comp = getMessage(player, key, args);
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(comp);
    }
}