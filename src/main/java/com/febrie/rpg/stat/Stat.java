package com.febrie.rpg.stat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 동적 스탯 시스템
 * 새로운 스탯을 쉽게 추가할 수 있도록 설계
 *
 * @author Febrie, CoffeeTory
 */
public class Stat {

    private static final Map<String, Stat> REGISTRY = new HashMap<>();

    // 기본 스탯들
    public static final Stat STRENGTH = register(new Stat("strength", "STR", "힘", "Strength",
            Material.IRON_SWORD, com.febrie.rpg.util.ColorUtil.COPPER, 10, 1000));

    public static final Stat INTELLIGENCE = register(new Stat("intelligence", "INT", "지능", "Intelligence",
            Material.BOOK, com.febrie.rpg.util.ColorUtil.INFO, 10, 1000));

    public static final Stat DEXTERITY = register(new Stat("dexterity", "DEX", "민첩", "Dexterity",
            Material.FEATHER, com.febrie.rpg.util.ColorUtil.SUCCESS, 10, 1000));

    public static final Stat VITALITY = register(new Stat("vitality", "VIT", "체력", "Vitality",
            Material.GOLDEN_APPLE, com.febrie.rpg.util.ColorUtil.HEALTH, 10, 1000));

    public static final Stat WISDOM = register(new Stat("wisdom", "WIS", "지혜", "Wisdom",
            Material.ENCHANTED_BOOK, com.febrie.rpg.util.ColorUtil.MANA, 10, 1000));

    public static final Stat LUCK = register(new Stat("luck", "LUK", "행운", "Luck",
            Material.RABBIT_FOOT, com.febrie.rpg.util.ColorUtil.LEGENDARY, 5, 500));

    private final String id;
    private final String abbreviation;
    private final String koreanName;
    private final String englishName;
    private final Material icon;
    private final TextColor color;
    private final int defaultValue;
    private final int maxValue;
    private final NamespacedKey key;

    public Stat(@NotNull String id, @NotNull String abbreviation,
                @NotNull String koreanName, @NotNull String englishName,
                @NotNull Material icon, @NotNull TextColor color,
                int defaultValue, int maxValue) {
        this.id = id;
        this.abbreviation = abbreviation;
        this.koreanName = koreanName;
        this.englishName = englishName;
        this.icon = icon;
        this.color = color;
        this.defaultValue = defaultValue;
        this.maxValue = maxValue;
        this.key = new NamespacedKey("sypixelrpg", "stat_" + id);
    }

    /**
     * 스탯 등록
     */
    public static Stat register(@NotNull Stat stat) {
        REGISTRY.put(stat.getId(), stat);
        return stat;
    }

    /**
     * ID로 스탯 가져오기
     */
    public static Stat getById(@NotNull String id) {
        return REGISTRY.get(id);
    }

    /**
     * 모든 등록된 스탯 가져오기
     */
    public static Map<String, Stat> getAllStats() {
        return new HashMap<>(REGISTRY);
    }

    // Getters
    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getAbbreviation() {
        return abbreviation;
    }

    @NotNull
    public String getKoreanName() {
        return koreanName;
    }

    @NotNull
    public String getEnglishName() {
        return englishName;
    }

    @NotNull
    public String getName(boolean isKorean) {
        return isKorean ? koreanName : englishName;
    }

    @NotNull
    public Material getIcon() {
        return icon;
    }

    @NotNull
    public TextColor getColor() {
        return color;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    @NotNull
    public NamespacedKey getKey() {
        return key;
    }

    /**
     * 스탯 이름 컴포넌트 생성
     */
    @NotNull
    public Component getDisplayName(boolean isKorean) {
        return Component.text(getName(isKorean), color);
    }

    /**
     * 스탯 설명 컴포넌트 생성
     */
    @NotNull
    public Component getDescription(boolean isKorean) {
        return switch (id) {
            case "strength" -> Component.text(isKorean ? "물리 공격력을 증가시킵니다" : "Increases physical damage");
            case "intelligence" -> Component.text(isKorean ? "마법 공격력을 증가시킵니다" : "Increases magical damage");
            case "dexterity" ->
                    Component.text(isKorean ? "공격 속도와 회피율을 증가시킵니다" : "Increases attack speed and dodge rate");
            case "vitality" -> Component.text(isKorean ? "최대 체력을 증가시킵니다" : "Increases maximum health");
            case "wisdom" -> Component.text(isKorean ? "최대 마나를 증가시킵니다" : "Increases maximum mana");
            case "luck" ->
                    Component.text(isKorean ? "치명타 확률과 아이템 드롭률을 증가시킵니다" : "Increases critical rate and item drop rate");
            default -> Component.text(isKorean ? "알 수 없는 스탯입니다" : "Unknown stat");
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Stat stat = (Stat) obj;
        return id.equals(stat.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * 플레이어 스탯 홀더
     */
    public static class StatHolder {
        private final Map<Stat, Integer> baseStats = new HashMap<>();
        private final Map<Stat, Integer> bonusStats = new HashMap<>();

        public StatHolder() {
            // 기본값으로 초기화
            for (Stat stat : REGISTRY.values()) {
                baseStats.put(stat, stat.getDefaultValue());
                bonusStats.put(stat, 0);
            }
        }

        /**
         * 기본 스탯 설정
         */
        public void setBaseStat(@NotNull Stat stat, int value) {
            baseStats.put(stat, Math.max(0, Math.min(value, stat.getMaxValue())));
        }

        /**
         * 보너스 스탯 설정
         */
        public void setBonusStat(@NotNull Stat stat, int value) {
            bonusStats.put(stat, value);
        }

        /**
         * 기본 스탯 가져오기
         */
        public int getBaseStat(@NotNull Stat stat) {
            return baseStats.getOrDefault(stat, stat.getDefaultValue());
        }

        /**
         * 보너스 스탯 가져오기
         */
        public int getBonusStat(@NotNull Stat stat) {
            return bonusStats.getOrDefault(stat, 0);
        }

        /**
         * 총 스탯 가져오기 (기본 + 보너스)
         */
        public int getTotalStat(@NotNull Stat stat) {
            return getBaseStat(stat) + getBonusStat(stat);
        }

        /**
         * 남은 스탯 포인트로 스탯 증가
         */
        public boolean increaseStat(@NotNull Stat stat, int amount) {
            int current = getBaseStat(stat);
            int newValue = current + amount;

            if (newValue > stat.getMaxValue()) {
                return false;
            }

            setBaseStat(stat, newValue);
            return true;
        }

        /**
         * 모든 스탯 정보 가져오기
         */
        public Map<Stat, Integer> getAllBaseStats() {
            return new HashMap<>(baseStats);
        }

        public Map<Stat, Integer> getAllBonusStats() {
            return new HashMap<>(bonusStats);
        }
    }
}