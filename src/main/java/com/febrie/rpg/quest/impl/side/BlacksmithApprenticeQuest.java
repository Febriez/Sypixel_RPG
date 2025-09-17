package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
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
 * Blacksmith Apprentice - Side Quest
 * Help the master blacksmith by gathering materials and learning the basics of smithing.
 *
 * @author Febrie
 */
public class BlacksmithApprenticeQuest extends Quest {

    /**
     * 기본 생성자
     */
    public BlacksmithApprenticeQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_BLACKSMITH_APPRENTICE)
                .objectives(List.of(
                        new InteractNPCObjective("talk_master_blacksmith", "master_blacksmith"),
                        new CollectItemObjective("iron_ore_collect", Material.IRON_ORE, 20),
                        new CollectItemObjective("coal_collect", Material.COAL, 15),
                        new VisitLocationObjective("mining_site", "Mining_Site"),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 10),
                        new CollectItemObjective("iron_ingot_collect", Material.IRON_INGOT, 12),
                        new InteractNPCObjective("return_master_blacksmith", "master_blacksmith")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1500)
                        .addCurrency(CurrencyType.GOLD, 400)
                        .addItem(new ItemStack(Material.IRON_SWORD))
                        .addItem(new ItemStack(Material.IRON_HELMET))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(12);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.blacksmith_apprentice.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.blacksmith_apprentice.info"), who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_master_blacksmith" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.blacksmith_apprentice.objectives.talk_master_blacksmith"), who);
            case "iron_ore_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.blacksmith_apprentice.objectives.iron_ore_collect"), who);
            case "coal_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.blacksmith_apprentice.objectives.coal_collect"), who);
            case "mining_site" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.blacksmith_apprentice.objectives.mining_site"), who);
            case "kill_skeletons" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.blacksmith_apprentice.objectives.kill_skeletons"), who);
            case "iron_ingot_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.blacksmith_apprentice.objectives.iron_ingot_collect"), who);
            case "return_master_blacksmith" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.blacksmith_apprentice.objectives.return_master_blacksmith"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.blacksmith_apprentice.dialogs"), who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.blacksmith_apprentice.npc_name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.blacksmith_apprentice.accept"), who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.blacksmith_apprentice.decline"), who);
    }
}