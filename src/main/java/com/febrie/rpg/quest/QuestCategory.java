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
        String categoryKey = this.name().toLowerCase();
        net.kyori.adventure.text.Component comp = LangManager.getMessage(player, "quest.categories." + categoryKey);
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(comp);
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