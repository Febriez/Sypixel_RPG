package com.febrie.rpg.job;

import com.febrie.rpg.util.ColorUtil;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * RPG 직업 타입 열거형
 * 각 직업의 기본 정보와 레벨 상한선을 정의
 * <p>
 * 중요: 새로운 직업을 추가할 때는 반드시 언어 파일(ko_KR.json, en_US.json)에
 * job.{id}.name과 job.{id}.description을 추가해야 합니다.
 * 예시:
 * - job.berserker.name = "버서커" (ko_KR) / "Berserker" (en_US)
 * - job.berserker.description = [...] (ko_KR) / [...] (en_US)
 *
 * @author Febrie, CoffeeTory
 */
public enum JobType {

    // 전사 계열 (레벨 200 내외)
    BERSERKER(JobCategory.WARRIOR, 195, ColorUtil.ERROR, "⚔", Material.DIAMOND_AXE),
    BRUISER(JobCategory.WARRIOR, 200, ColorUtil.ORANGE, "🛡", Material.IRON_SWORD),
    TANK(JobCategory.WARRIOR, 205, ColorUtil.NETHERITE, "🏛", Material.SHIELD),

    // 마법사 계열 (레벨 120 내외)
    PRIEST(JobCategory.MAGE, 115, ColorUtil.LEGENDARY, "✨", Material.GOLDEN_APPLE),
    DARK_MAGE(JobCategory.MAGE, 120, ColorUtil.EPIC, "🌑", Material.WITHER_SKELETON_SKULL),
    MERCY(JobCategory.MAGE, 125, ColorUtil.SUCCESS, "💚", Material.TOTEM_OF_UNDYING),

    // 궁수 계열 (레벨 100 내외)
    ARCHER(JobCategory.ARCHER, 95, ColorUtil.EMERALD, "🏹", Material.BOW),
    SNIPER(JobCategory.ARCHER, 100, ColorUtil.INFO, "🎯", Material.CROSSBOW),
    SHOTGUNNER(JobCategory.ARCHER, 105, ColorUtil.WARNING, "💥", Material.FIRE_CHARGE);

    private final JobCategory category;
    private final int maxLevel;
    private final TextColor color;
    private final String icon;
    private final Material material;

    JobType(@NotNull JobCategory category, int maxLevel,
            @NotNull TextColor color, @NotNull String icon,
            @NotNull Material material) {
        this.category = category;
        this.maxLevel = maxLevel;
        this.color = color;
        this.icon = icon;
        this.material = material;
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
     * 이름은 LangManager에서 job.categories.{name}.name 형식으로 관리
     */
    public enum JobCategory {
        WARRIOR(ColorUtil.COPPER, Material.IRON_SWORD),
        MAGE(ColorUtil.EPIC, Material.BLAZE_ROD),
        ARCHER(ColorUtil.EMERALD, Material.BOW);

        private final TextColor color;
        private final Material icon;

        JobCategory(@NotNull TextColor color, @NotNull Material icon) {
            this.color = color;
            this.icon = icon;
        }

        @NotNull
        public TextColor getColor() {
            return color;
        }

        @NotNull
        public Material getIcon() {
            return icon;
        }
    }
}