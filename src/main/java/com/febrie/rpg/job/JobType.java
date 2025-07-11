package com.febrie.rpg.job;

import com.febrie.rpg.util.ColorUtil;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * RPG 직업 타입 열거형
 * 각 직업의 기본 정보와 레벨 상한선을 정의
 *
 * @author Febrie, CoffeeTory
 */
public enum JobType {

    // 전사 계열 (레벨 200 내외)
    BERSERKER("버서커", "Berserker", JobCategory.WARRIOR, 195, ColorUtil.ERROR, "⚔", Material.DIAMOND_AXE),
    BRUISER("브루저", "Bruiser", JobCategory.WARRIOR, 200, ColorUtil.ORANGE, "🛡", Material.IRON_SWORD),
    TANK("탱커", "Tank", JobCategory.WARRIOR, 205, ColorUtil.NETHERITE, "🏛", Material.SHIELD),

    // 마법사 계열 (레벨 120 내외)
    PRIEST("사제", "Priest", JobCategory.MAGE, 115, ColorUtil.LEGENDARY, "✨", Material.GOLDEN_APPLE),
    DARK_MAGE("흑마법사", "Dark Mage", JobCategory.MAGE, 120, ColorUtil.EPIC, "🌑", Material.WITHER_SKELETON_SKULL),
    MERCY("메르시", "Mercy", JobCategory.MAGE, 125, ColorUtil.SUCCESS, "💚", Material.TOTEM_OF_UNDYING),

    // 궁수 계열 (레벨 100 내외)
    ARCHER("아처", "Archer", JobCategory.ARCHER, 95, ColorUtil.EMERALD, "🏹", Material.BOW),
    SNIPER("스나이퍼", "Sniper", JobCategory.ARCHER, 100, ColorUtil.INFO, "🎯", Material.CROSSBOW),
    SHOTGUNNER("샷건맨", "Shotgunner", JobCategory.ARCHER, 105, ColorUtil.WARNING, "💥", Material.FIRE_CHARGE);

    private final String koreanName;
    private final String englishName;
    private final JobCategory category;
    private final int maxLevel;
    private final TextColor color;
    private final String icon;
    private final Material material;

    JobType(@NotNull String koreanName, @NotNull String englishName,
            @NotNull JobCategory category, int maxLevel,
            @NotNull TextColor color, @NotNull String icon,
            @NotNull Material material) {
        this.koreanName = koreanName;
        this.englishName = englishName;
        this.category = category;
        this.maxLevel = maxLevel;
        this.color = color;
        this.icon = icon;
        this.material = material;
    }

    /**
     * 직업의 한국어 이름
     */
    @NotNull
    public String getKoreanName() {
        return koreanName;
    }

    /**
     * 직업의 영어 이름
     */
    @NotNull
    public String getEnglishName() {
        return englishName;
    }

    /**
     * 언어에 따른 직업 이름 반환
     */
    @NotNull
    public String getName(boolean isKorean) {
        return isKorean ? koreanName : englishName;
    }

    /**
     * 직업 카테고리
     */
    @NotNull
    public JobCategory getCategory() {
        return category;
    }

    /**
     * 최대 레벨
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * 직업 고유 색상
     */
    @NotNull
    public TextColor getColor() {
        return color;
    }

    /**
     * 직업 아이콘
     */
    @NotNull
    public String getIcon() {
        return icon;
    }

    /**
     * 직업을 나타내는 아이템 재료
     */
    @NotNull
    public Material getMaterial() {
        return material;
    }

    /**
     * 직업 카테고리 열거형
     */
    public enum JobCategory {
        WARRIOR("전사", "Warrior", ColorUtil.COPPER, org.bukkit.Material.IRON_SWORD),
        MAGE("마법사", "Mage", ColorUtil.EPIC, org.bukkit.Material.BLAZE_ROD),
        ARCHER("궁수", "Archer", ColorUtil.EMERALD, org.bukkit.Material.BOW);

        private final String koreanName;
        private final String englishName;
        private final TextColor color;
        private final org.bukkit.Material icon;

        JobCategory(@NotNull String koreanName, @NotNull String englishName,
                    @NotNull TextColor color, @NotNull org.bukkit.Material icon) {
            this.koreanName = koreanName;
            this.englishName = englishName;
            this.color = color;
            this.icon = icon;
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
        public TextColor getColor() {
            return color;
        }

        @NotNull
        public org.bukkit.Material getIcon() {
            return icon;
        }
    }
}