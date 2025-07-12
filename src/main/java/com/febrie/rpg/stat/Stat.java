package com.febrie.rpg.stat;

import com.febrie.rpg.dto.StatsDTO;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * RPG 스탯 시스템
 * 플레이어의 기본 능력치를 정의하고 관리
 * <p>
 * 중요: 새로운 스탯을 추가할 때는 반드시 언어 파일(ko_KR.json, en_US.json)에
 * stat.{id}.name과 stat.{id}.description을 추가해야 합니다.
 * 예시:
 * - stat.strength.name = "근력" (ko_KR) / "Strength" (en_US)
 * - stat.strength.description = "물리 공격력을 증가시킵니다" (ko_KR) / "Increases physical damage" (en_US)
 *
 * @author Febrie, CoffeeTory
 */
public class Stat {

    // 스탯 레지스트리
    private static final Map<String, Stat> REGISTRY = new HashMap<>();

    // 기본 스탯들
    public static final Stat STRENGTH = register(new Stat(
            "strength", "STR",
            Material.IRON_SWORD,
            com.febrie.rpg.util.ColorUtil.COPPER,
            10, 999
    ));

    public static final Stat INTELLIGENCE = register(new Stat(
            "intelligence", "INT",
            Material.ENCHANTED_BOOK,
            com.febrie.rpg.util.ColorUtil.INFO,
            10, 999
    ));

    public static final Stat DEXTERITY = register(new Stat(
            "dexterity", "DEX",
            Material.FEATHER,
            com.febrie.rpg.util.ColorUtil.SUCCESS,
            10, 999
    ));

    public static final Stat VITALITY = register(new Stat(
            "vitality", "VIT",
            Material.GOLDEN_APPLE,
            com.febrie.rpg.util.ColorUtil.HEALTH,
            10, 999
    ));

    public static final Stat WISDOM = register(new Stat(
            "wisdom", "WIS",
            Material.EXPERIENCE_BOTTLE,
            com.febrie.rpg.util.ColorUtil.MANA,
            10, 999
    ));

    public static final Stat LUCK = register(new Stat(
            "luck", "LUK",
            Material.EMERALD,
            com.febrie.rpg.util.ColorUtil.LEGENDARY,
            1, 100
    ));

    // 스탯 속성들
    private final String id;
    private final String abbreviation;
    private final Material icon;
    private final TextColor color;
    private final int defaultValue;
    private final int maxValue;
    private final NamespacedKey key;

    private Stat(@NotNull String id, @NotNull String abbreviation,
                 @NotNull Material icon, @NotNull TextColor color,
                 int defaultValue, int maxValue) {
        this.id = id;
        this.abbreviation = abbreviation;
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
         * DTO로 변환
         */
        @NotNull
        public StatsDTO toDTO() {
            // StatsDTO는 record이므로 생성자로 생성
            return new StatsDTO(
                    getBaseStat(Stat.STRENGTH),
                    getBaseStat(Stat.INTELLIGENCE),
                    getBaseStat(Stat.DEXTERITY),
                    getBaseStat(Stat.VITALITY),
                    getBaseStat(Stat.WISDOM),
                    getBaseStat(Stat.LUCK)
            );
        }

        /**
         * DTO에서 데이터 적용
         */
        public void applyFromDTO(@NotNull StatsDTO dto) {
            // record의 accessor 메소드 사용
            setBaseStat(Stat.STRENGTH, dto.strength());
            setBaseStat(Stat.INTELLIGENCE, dto.intelligence());
            setBaseStat(Stat.DEXTERITY, dto.dexterity());
            setBaseStat(Stat.VITALITY, dto.vitality());
            setBaseStat(Stat.WISDOM, dto.wisdom());
            setBaseStat(Stat.LUCK, dto.luck());
        }

        /**
         * 기본 스탯 설정
         */
        public void setBaseStat(@NotNull Stat stat, int value) {
            baseStats.put(stat, Math.max(stat.getDefaultValue(), Math.min(value, stat.getMaxValue())));
        }

        /**
         * PDC에서 스탯 로드
         */
        public void loadFromPDC(@NotNull PersistentDataContainer pdc) {
            for (Stat stat : Stat.getAllStats().values()) {
                Integer value = pdc.get(stat.getKey(), PersistentDataType.INTEGER);
                if (value != null) {
                    setBaseStat(stat, value);
                }
            }
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