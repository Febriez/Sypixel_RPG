package com.febrie.rpg.talent;

import com.febrie.rpg.stat.Stat;
import com.febrie.rpg.util.ColorUtil;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 특성(탤런트) 시스템
 * 웹 형태의 트리 구조로 구성되어 있으며, 각 특성은 하위 특성 페이지를 가질 수 있음
 * 이름은 LangManager를 통해 관리됨 (talent.{id}.name)
 *
 * @author Febrie, CoffeeTory
 */
public class Talent {

    private final String id;
    private final Material icon;
    private final TextColor color;
    private final int maxLevel;
    private final int requiredPoints;
    private final TalentCategory category;

    // 특성 트리 관련
    private final List<Talent> children = new ArrayList<>();
    private Talent parent;
    private final Map<Talent, Integer> prerequisites = new HashMap<>();

    // 효과
    private final Map<Stat, Integer> statBonuses = new HashMap<>();
    private final List<String> effects = new ArrayList<>();

    // 페이지 정보
    private boolean hasSubPage = false;
    private String pageId;

    private Talent(Builder builder) {
        this.id = builder.id;
        this.icon = builder.icon;
        this.color = builder.color;
        this.maxLevel = builder.maxLevel;
        this.requiredPoints = builder.requiredPoints;
        this.category = builder.category;
        this.statBonuses.putAll(builder.statBonuses);
        this.effects.addAll(builder.effects);
        this.hasSubPage = builder.hasSubPage;
        this.pageId = builder.pageId;
    }

    /**
     * 하위 특성 추가
     * hasSubPage와 pageId는 Builder에서 설정하거나, 별도로 설정해야 함
     */
    public void addChild(@NotNull Talent child) {
        children.add(child);
        child.parent = this;
    }

    /**
     * 선행 조건 추가
     */
    public void addPrerequisite(@NotNull Talent talent, int requiredLevel) {
        prerequisites.put(talent, requiredLevel);
    }

    /**
     * 특성 활성화 가능 여부 확인
     */
    public boolean canActivate(@NotNull TalentHolder holder) {
        // 포인트 확인
        if (holder.getAvailablePoints() < requiredPoints) {
            return false;
        }

        // 선행 조건 확인
        for (Map.Entry<Talent, Integer> entry : prerequisites.entrySet()) {
            if (holder.getTalentLevel(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }

        // 현재 레벨이 최대치인지 확인
        return holder.getTalentLevel(this) < maxLevel;
    }

    /**
     * 특성 레벨업
     */
    public boolean levelUp(@NotNull TalentHolder holder) {
        if (!canActivate(holder)) {
            return false;
        }

        int currentLevel = holder.getTalentLevel(this);
        holder.setTalentLevel(this, currentLevel + 1);
        holder.spendPoints(requiredPoints);

        return true;
    }

    // Getters
    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public Material getIcon() {
        return icon;
    }

    @NotNull
    public TextColor getColor() {
        return color;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }

    @NotNull
    public TalentCategory getCategory() {
        return category;
    }

    @NotNull
    public List<Talent> getChildren() {
        return new ArrayList<>(children);
    }

    @Nullable
    public Talent getParent() {
        return parent;
    }

    public boolean hasSubPage() {
        return hasSubPage;
    }

    @Nullable
    public String getPageId() {
        return pageId;
    }

    /**
     * 특정 레벨에서의 스탯 보너스 반환
     * GUI에서 LangManager를 통해 스탯 이름을 표시
     */
    @NotNull
    public Map<Stat, Integer> getStatBonuses(int level) {
        Map<Stat, Integer> bonuses = new HashMap<>();
        for (Map.Entry<Stat, Integer> entry : statBonuses.entrySet()) {
            bonuses.put(entry.getKey(), entry.getValue() * level);
        }
        return bonuses;
    }

    /**
     * 특수 효과 목록 반환
     */
    @NotNull
    public List<String> getEffects() {
        return new ArrayList<>(effects);
    }

    /**
     * 선행 조건 목록 가져오기 (GUI에서 LangManager로 이름 번역 필요)
     */
    @NotNull
    public Map<Talent, Integer> getPrerequisites() {
        return new HashMap<>(prerequisites);
    }

    /**
     * 특성 카테고리
     * 이름은 LangManager에서 talent.category.{name}.name 형식으로 관리
     */
    public enum TalentCategory {
        OFFENSE(ColorUtil.ERROR),
        DEFENSE(ColorUtil.INFO),
        UTILITY(ColorUtil.SUCCESS),
        SPECIAL(ColorUtil.LEGENDARY);

        private final TextColor color;

        TalentCategory(TextColor color) {
            this.color = color;
        }

        public TextColor getColor() {
            return color;
        }
    }

    /**
     * 특성 빌더
     */
    public static class Builder {
        private final String id;
        private Material icon = Material.BOOK;
        private TextColor color = com.febrie.rpg.util.ColorUtil.WHITE;
        private int maxLevel = 1;
        private int requiredPoints = 1;
        private TalentCategory category = TalentCategory.UTILITY;
        private final Map<Stat, Integer> statBonuses = new HashMap<>();
        private final List<String> effects = new ArrayList<>();
        private boolean hasSubPage = false;
        private String pageId;

        public Builder(@NotNull String id) {
            this.id = id;
        }

        public Builder icon(@NotNull Material icon) {
            this.icon = icon;
            return this;
        }

        public Builder color(@NotNull TextColor color) {
            this.color = color;
            return this;
        }

        public Builder maxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public Builder requiredPoints(int points) {
            this.requiredPoints = points;
            return this;
        }

        public Builder category(@NotNull TalentCategory category) {
            this.category = category;
            return this;
        }

        public Builder addStatBonus(@NotNull Stat stat, int bonus) {
            this.statBonuses.put(stat, bonus);
            return this;
        }

        public Builder addEffect(@NotNull String effect) {
            this.effects.add(effect);
            return this;
        }

        public Builder hasSubPage(boolean hasSubPage) {
            this.hasSubPage = hasSubPage;
            return this;
        }

        public Builder pageId(@NotNull String pageId) {
            this.pageId = pageId;
            this.hasSubPage = true;
            return this;
        }

        public Talent build() {
            return new Talent(this);
        }
    }

    /**
     * 특성 보유자
     */
    public static class TalentHolder {
        private final Map<Talent, Integer> talentLevels = new HashMap<>();
        private int availablePoints = 0;
        private int spentPoints = 0;

        public void setTalentLevel(@NotNull Talent talent, int level) {
            talentLevels.put(talent, Math.max(0, Math.min(level, talent.getMaxLevel())));
        }

        public int getTalentLevel(@NotNull Talent talent) {
            return talentLevels.getOrDefault(talent, 0);
        }

        public void addPoints(int points) {
            availablePoints += points;
        }

        public void spendPoints(int points) {
            availablePoints -= points;
            spentPoints += points;
        }

        public int getAvailablePoints() {
            return availablePoints;
        }

        public int getSpentPoints() {
            return spentPoints;
        }

        public Map<Talent, Integer> getAllTalents() {
            return new HashMap<>(talentLevels);
        }

        /**
         * 모든 스탯 보너스 계산
         */
        public Map<Stat, Integer> calculateStatBonuses() {
            Map<Stat, Integer> totalBonuses = new HashMap<>();

            for (Map.Entry<Talent, Integer> entry : talentLevels.entrySet()) {
                Talent talent = entry.getKey();
                int level = entry.getValue();

                if (level > 0) {
                    Map<Stat, Integer> talentBonuses = talent.getStatBonuses(level);
                    for (Map.Entry<Stat, Integer> bonusEntry : talentBonuses.entrySet()) {
                        totalBonuses.merge(bonusEntry.getKey(), bonusEntry.getValue(), Integer::sum);
                    }
                }
            }

            return totalBonuses;
        }
    }
}