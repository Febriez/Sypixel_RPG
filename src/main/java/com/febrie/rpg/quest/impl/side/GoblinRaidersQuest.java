package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Goblin Raiders
 * Clear the goblin raiders threatening trade routes
 *
 * @author Febrie
 */
public class GoblinRaidersQuest extends Quest {

    /**
     * 기본 생성자
     */
    public GoblinRaidersQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_GOBLIN_RAIDERS)
                .objectives(List.of(
                        new InteractNPCObjective("talk_merchant_leader", "merchant_leader"),
                        new KillMobObjective("goblin_scouts", EntityType.ZOMBIE, 8),
                        new VisitLocationObjective("goblin_camps", "Goblin_Camps"),
                        new CollectItemObjective("gold_ingot_collect", Material.GOLD_INGOT, 15),
                        new InteractNPCObjective("rescue_merchants", "captured_merchant"),
                        new VisitLocationObjective("goblin_stronghold", "Goblin_Stronghold"),
                        new KillMobObjective("goblin_chieftain", EntityType.ZOMBIE_VILLAGER, 1),
                        new InteractNPCObjective("report_success", "merchant_leader")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1600)
                        .addCurrency(CurrencyType.GOLD, 400)
                        .addItem(new ItemStack(Material.DIAMOND_SWORD, 1))
                        .addItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1))
                        .addItem(new ItemStack(Material.EMERALD, 10))
                        .build())
                .sequential(false)
                .category(QuestCategory.SIDE)
                .minLevel(14);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.goblin.raiders.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.goblin.raiders.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_merchant_leader" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.goblin.raiders.objectives.talk.merchant.leader"), who);
            case "goblin_scouts" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.goblin.raiders.objectives.goblin.scouts"), who);
            case "goblin_camps" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.goblin.raiders.objectives.goblin.camps"), who);
            case "gold_ingot_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.goblin.raiders.objectives.gold.ingot.collect"), who);
            case "rescue_merchants" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.goblin.raiders.objectives.rescue.merchants"), who);
            case "goblin_stronghold" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.goblin.raiders.objectives.goblin.stronghold"), who);
            case "goblin_chieftain" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.goblin.raiders.objectives.goblin.chieftain"), who);
            case "report_success" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.goblin.raiders.objectives.report.success"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.goblin.raiders.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.goblin.raiders.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.goblin.raiders.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.goblin.raiders.decline"), who);
    }
}