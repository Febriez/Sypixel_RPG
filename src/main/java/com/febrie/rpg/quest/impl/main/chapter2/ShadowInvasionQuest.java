package com.febrie.rpg.quest.impl.main.chapter2;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Chapter 2: Shadow Invasion Quest
 * The darkness begins to spread across the land
 *
 * @author Febrie
 */
public class ShadowInvasionQuest extends Quest {
    
    /**
     * Default constructor
     */
    public ShadowInvasionQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_SHADOW_INVASION)
                .objectives(List.of(
                        new InteractNPCObjective("scout_captain", "scout_captain"),
                        new VisitLocationObjective("shadow_portal", "shadow_portal_area"),
                        new KillMobObjective("kill_wither_skeletons", EntityType.WITHER_SKELETON, 15),
                        new KillMobObjective("kill_phantoms", EntityType.PHANTOM, 10),
                        new VisitLocationObjective("corrupted_fortress", "corrupted_fortress_area"),
                        new InteractNPCObjective("resistance_leader", "resistance_leader")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 2500)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD))
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 5))
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(20)
                .addPrerequisite(QuestID.MAIN_GUARDIAN_AWAKENING);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_SHADOW_INVASION_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_SHADOW_INVASION_INFO, who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "scout_captain" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SHADOW_INVASION_OBJECTIVES_SCOUT_CAPTAIN, who);
            case "shadow_portal" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SHADOW_INVASION_OBJECTIVES_SHADOW_PORTAL, who);
            case "kill_wither_skeletons" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SHADOW_INVASION_OBJECTIVES_KILL_WITHER_SKELETONS, who);
            case "kill_phantoms" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SHADOW_INVASION_OBJECTIVES_KILL_PHANTOMS, who);
            case "corrupted_fortress" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SHADOW_INVASION_OBJECTIVES_CORRUPTED_FORTRESS, who);
            case "resistance_leader" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SHADOW_INVASION_OBJECTIVES_RESISTANCE_LEADER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_SHADOW_INVASION_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_SHADOW_INVASION_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_SHADOW_INVASION_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_SHADOW_INVASION_DECLINE, who);
    }
}