package com.febrie.rpg.quest.impl.special;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangKey;
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
 * 고대의 힘 - 특별 퀘스트
 * 고대의 힘을 해금하는 퀘스트
 *
 * @author Febrie
 */
public class AncientPowerQuest extends Quest {

    /**
     * 기본 생성자
     */
    public AncientPowerQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SPECIAL_ANCIENT_POWER)
                .objectives(List.of(
                        new CollectItemObjective("collect_echo_shard", Material.ECHO_SHARD, 10),
                        new KillMobObjective("defeat_ancient_guardians", EntityType.IRON_GOLEM, 3),
                        new VisitLocationObjective("activate_power_altars", "ancient_altar"),
                        new InteractNPCObjective("unlock_ancient_power", "ancient_sage")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 2000)
                        .addItem(new ItemStack(Material.NETHER_STAR, 3))
                        .addItem(new ItemStack(Material.ANCIENT_DEBRIS, 10))
                        .addExperience(3000)
                        .build())
                .sequential(true)
                .category(QuestCategory.SPECIAL)
                .minLevel(40);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_ANCIENT_POWER_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SPECIAL_ANCIENT_POWER_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "collect_echo_shard" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_ANCIENT_POWER_OBJECTIVES_COLLECT_ECHO_SHARD, who);
            case "defeat_ancient_guardians" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_ANCIENT_POWER_OBJECTIVES_DEFEAT_ANCIENT_GUARDIANS, who);
            case "activate_power_altars" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_ANCIENT_POWER_OBJECTIVES_ACTIVATE_POWER_ALTARS, who);
            case "unlock_ancient_power" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_ANCIENT_POWER_OBJECTIVES_UNLOCK_ANCIENT_POWER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SPECIAL_ANCIENT_POWER_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_ANCIENT_POWER_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_ANCIENT_POWER_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_ANCIENT_POWER_DECLINE, who);
    }
}