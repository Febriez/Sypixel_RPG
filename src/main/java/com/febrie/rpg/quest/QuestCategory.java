package com.febrie.rpg.quest;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Quest categories with their display properties
 */
public enum QuestCategory {
    
    MAIN(ColorUtil.LEGENDARY, 100, false),
    SIDE(ColorUtil.RARE, 50, false),
    DAILY(ColorUtil.UNCOMMON, 30, true),
    WEEKLY(ColorUtil.EPIC, 40, true),
    TUTORIAL(ColorUtil.COMMON, 10, false),
    COMBAT(ColorUtil.RARE, 45, false),
    GUILD(ColorUtil.EPIC, 60, false),
    EVENT(ColorUtil.LEGENDARY, 90, false),
    SPECIAL(ColorUtil.MYTHIC, 95, false),
    ADVANCEMENT(ColorUtil.EPIC, 70, false),
    REPEATABLE(ColorUtil.COMMON, 20, true),
    SEASONAL(ColorUtil.RARE, 55, false),
    BRANCH(ColorUtil.RARE, 65, false),
    LIFE(ColorUtil.UNCOMMON, 35, false),
    CRAFTING(ColorUtil.UNCOMMON, 36, false),
    EXPLORATION(ColorUtil.RARE, 46, false);
    
    private final TextColor color;
    private final int priority;
    private final boolean repeatable;
    
    QuestCategory(@NotNull TextColor color, int priority, boolean repeatable) {
        this.color = color;
        this.priority = priority;
        this.repeatable = repeatable;
    }
    
    /**
     * Get the display name based on player's language
     */
    public @NotNull String getDisplayName(@NotNull Player player) {
        LangManager langManager = RPGMain.getPlugin().getLangManager();
        String categoryKey = this.name().toLowerCase();
        return langManager.getMessage(player, "quest.categories." + categoryKey);
    }
    
    /**
     * Get the display name based on language (deprecated, use Player-based method)
     * @deprecated Use {@link #getDisplayName(Player)} instead
     */
    @Deprecated
    public @NotNull String getDisplayName(boolean isKorean) {
        // For backward compatibility, use hardcoded values temporarily
        // This should be removed once all usages are updated
        String categoryKey = this.name().toLowerCase();
        return isKorean ? getKoreanNameFallback() : getEnglishNameFallback();
    }
    
    private String getKoreanNameFallback() {
        return switch (this) {
            case MAIN -> "메인 퀘스트";
            case SIDE -> "사이드 퀘스트";
            case DAILY -> "일일 퀘스트";
            case WEEKLY -> "주간 퀘스트";
            case TUTORIAL -> "튜토리얼";
            case COMBAT -> "전투 퀘스트";
            case GUILD -> "길드 퀘스트";
            case EVENT -> "이벤트 퀘스트";
            case SPECIAL -> "특별 퀘스트";
            case ADVANCEMENT -> "전직 퀘스트";
            case REPEATABLE -> "반복 퀘스트";
            case SEASONAL -> "시즌 퀘스트";
            case BRANCH -> "분기 퀘스트";
            case LIFE -> "생활 퀘스트";
            case CRAFTING -> "제작 퀘스트";
            case EXPLORATION -> "탐험 퀘스트";
        };
    }
    
    private String getEnglishNameFallback() {
        return switch (this) {
            case MAIN -> "Main Quest";
            case SIDE -> "Side Quest";
            case DAILY -> "Daily Quest";
            case WEEKLY -> "Weekly Quest";
            case TUTORIAL -> "Tutorial";
            case COMBAT -> "Combat Quest";
            case GUILD -> "Guild Quest";
            case EVENT -> "Event Quest";
            case SPECIAL -> "Special Quest";
            case ADVANCEMENT -> "Advancement Quest";
            case REPEATABLE -> "Repeatable Quest";
            case SEASONAL -> "Seasonal Quest";
            case BRANCH -> "Branch Quest";
            case LIFE -> "Life Quest";
            case CRAFTING -> "Crafting Quest";
            case EXPLORATION -> "Exploration Quest";
        };
    }
    
    /**
     * Get the color for this category
     */
    public @NotNull TextColor getColor() {
        return color;
    }
    
    /**
     * Get the priority for sorting (higher = first)
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * Check if quests in this category can be repeated
     */
    public boolean isRepeatable() {
        return repeatable;
    }
    
}