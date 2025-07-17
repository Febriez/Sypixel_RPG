package com.febrie.rpg.quest;

import com.febrie.rpg.util.ColorUtil;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

/**
 * Quest categories with their display properties
 */
public enum QuestCategory {
    
    MAIN("메인 퀘스트", "Main Quest", ColorUtil.LEGENDARY, 100, false),
    SIDE("사이드 퀘스트", "Side Quest", ColorUtil.RARE, 50, false),
    DAILY("일일 퀘스트", "Daily Quest", ColorUtil.UNCOMMON, 30, true),
    WEEKLY("주간 퀘스트", "Weekly Quest", ColorUtil.EPIC, 40, true),
    TUTORIAL("튜토리얼", "Tutorial", ColorUtil.COMMON, 10, false);
    
    private final String koreanName;
    private final String englishName;
    private final TextColor color;
    private final int priority;
    private final boolean repeatable;
    
    QuestCategory(@NotNull String koreanName, @NotNull String englishName, 
                  @NotNull TextColor color, int priority, boolean repeatable) {
        this.koreanName = koreanName;
        this.englishName = englishName;
        this.color = color;
        this.priority = priority;
        this.repeatable = repeatable;
    }
    
    /**
     * Get the display name based on language
     */
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? koreanName : englishName;
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