package com.febrie.rpg.quest.impl.tutorial;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;

import com.febrie.rpg.util.LangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 기초 전투 - 튜토리얼 퀘스트 2
 * 전투의 기본을 배우는 퀘스트
 *
 * @author Febrie
 */
public class BasicCombatQuest extends Quest {

    /**
     * 기본 생성자
     */
    public BasicCombatQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.TUTORIAL_BASIC_COMBAT)
                .objectives(List.of(
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 5),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 3)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 200)
                        .addItem(new ItemStack(Material.IRON_SWORD))
                        .addItem(new ItemStack(Material.IRON_CHESTPLATE))
                        .addItem(new ItemStack(Material.COOKED_BEEF, 20))
                        .addExperience(100)
                        .build())
                .sequential(false)  // 순서 상관없이 진행 가능
                .category(QuestCategory.TUTORIAL)
                .minLevel(1)
                .addPrerequisite(QuestID.TUTORIAL_FIRST_STEPS);  // 첫 걸음 퀘스트 완료 필요
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_TUTORIAL_BASIC_COMBAT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_TUTORIAL_BASIC_COMBAT_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "kill_zombies" -> LangManager.list(LangKey.QUEST_TUTORIAL_BASIC_COMBAT_OBJECTIVES_KILL_ZOMBIES, who);
            case "kill_skeletons" -> LangManager.list(LangKey.QUEST_TUTORIAL_BASIC_COMBAT_OBJECTIVES_KILL_SKELETONS, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_TUTORIAL_BASIC_COMBAT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_TUTORIAL_BASIC_COMBAT_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_TUTORIAL_BASIC_COMBAT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_TUTORIAL_BASIC_COMBAT_DECLINE, who);
    }
}