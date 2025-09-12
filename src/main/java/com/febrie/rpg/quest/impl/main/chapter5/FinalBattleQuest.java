package com.febrie.rpg.quest.impl.main.chapter5;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Chapter 5: Final Battle Quest
 * The ultimate confrontation with the forces of darkness
 *
 * @author Febrie
 */
public class FinalBattleQuest extends Quest {
    
    /**
     * Default constructor
     */
    public FinalBattleQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_FINAL_BATTLE)
                .objectives(List.of(
                        new InteractNPCObjective("chosen_champion", "chosen_champion"),
                        new VisitLocationObjective("battlefield_gates", "battlefield_gates_area"),
                        new KillMobObjective("kill_withers", EntityType.WITHER, 3),
                        new CollectItemObjective("wither_skeleton_skull_collect", Material.WITHER_SKELETON_SKULL, 5),
                        new KillMobObjective("kill_ender_dragon", EntityType.ENDER_DRAGON, 1),
                        new CollectItemObjective("dragon_egg_collect", Material.DRAGON_EGG, 1),
                        new VisitLocationObjective("void_nexus", "void_nexus_area"),
                        new KillMobObjective("kill_endermen", EntityType.ENDERMAN, 50),
                        new InteractNPCObjective("ancient_oracle", "ancient_oracle")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 10000)
                        .addItem(new ItemStack(Material.NETHERITE_BOOTS))
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addItem(new ItemStack(Material.DRAGON_HEAD))
                        .addExperience(20000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(44)
                .addPrerequisite(QuestID.MAIN_LAST_STAND);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_INFO, who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "chosen_champion" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_OBJECTIVES_CHOSEN_CHAMPION, who);
            case "battlefield_gates" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_OBJECTIVES_BATTLEFIELD_GATES, who);
            case "kill_withers" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_OBJECTIVES_KILL_WITHERS, who);
            case "wither_skeleton_skull_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_OBJECTIVES_WITHER_SKELETON_SKULL_COLLECT, who);
            case "kill_ender_dragon" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_OBJECTIVES_KILL_ENDER_DRAGON, who);
            case "dragon_egg_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_OBJECTIVES_DRAGON_EGG_COLLECT, who);
            case "void_nexus" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_OBJECTIVES_VOID_NEXUS, who);
            case "kill_endermen" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_OBJECTIVES_KILL_ENDERMEN, who);
            case "ancient_oracle" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_OBJECTIVES_ANCIENT_ORACLE, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_FINAL_BATTLE_DECLINE, who);
    }
}