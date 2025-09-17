package com.febrie.rpg.quest.impl.combat;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.SurviveObjective;
import com.febrie.rpg.quest.objective.impl.ReachLevelObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 생존 전문가 - 전투 퀘스트
 * 적들의 파도를 견뎌내는 생존 퀘스트
 *
 * @author Febrie
 */
public class SurvivalExpertQuest extends Quest {

    /**
     * 기본 생성자
     */
    public SurvivalExpertQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.COMBAT_SURVIVAL_EXPERT)
                .objectives(List.of(
                        new SurviveObjective("survive_enemy_waves", 25),
                        new ReachLevelObjective("reach_survival_level", 30), // Reach level 30
                        new KillMobObjective("kill_while_surviving", EntityType.ZOMBIE, 100),
                        new CollectItemObjective("bread_collect", Material.BREAD, 64)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 600)
                        .addItem(new ItemStack(Material.SHIELD, 1))
                        .addItem(new ItemStack(Material.NETHERITE_HELMET, 1))
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 10))
                        .addExperience(900)
                        .build())
                .sequential(false)
                .category(QuestCategory.COMBAT)
                .minLevel(20);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_COMBAT_SURVIVAL_EXPERT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_COMBAT_SURVIVAL_EXPERT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "survive_enemy_waves" -> LangManager.text(QuestCommonLangKey.QUEST_COMBAT_SURVIVAL_EXPERT_OBJECTIVES_SURVIVE_ENEMY_WAVES, who);
            case "reach_survival_level" -> LangManager.text(QuestCommonLangKey.QUEST_COMBAT_SURVIVAL_EXPERT_OBJECTIVES_REACH_SURVIVAL_LEVEL, who);
            case "kill_while_surviving" -> LangManager.text(QuestCommonLangKey.QUEST_COMBAT_SURVIVAL_EXPERT_OBJECTIVES_KILL_WHILE_SURVIVING, who);
            case "bread_collect" -> LangManager.text(QuestCommonLangKey.QUEST_COMBAT_SURVIVAL_EXPERT_OBJECTIVES_BREAD_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_COMBAT_SURVIVAL_EXPERT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_COMBAT_SURVIVAL_EXPERT_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_COMBAT_SURVIVAL_EXPERT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_COMBAT_SURVIVAL_EXPERT_DECLINE, who);
    }
}