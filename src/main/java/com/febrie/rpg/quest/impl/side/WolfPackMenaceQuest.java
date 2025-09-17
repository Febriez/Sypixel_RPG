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
 * Side Quest: Wolf Pack Menace
 * Deal with the organized wolf pack threatening trade routes
 *
 * @author Febrie
 */
public class WolfPackMenaceQuest extends Quest {

    /**
     * 기본 생성자
     */
    public WolfPackMenaceQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_WOLF_PACK_MENACE)
                .objectives(List.of(
                        new InteractNPCObjective("talk_caravan_leader", "caravan_leader"),
                        new VisitLocationObjective("wolf_territory", "Wolf_Territory"),
                        new CollectItemObjective("compass_collect", Material.COMPASS, 3),
                        new KillMobObjective("wolf_scouts", EntityType.WOLF, 8),
                        new VisitLocationObjective("pack_den", "Pack_Den"),
                        new KillMobObjective("pack_members", EntityType.WOLF, 15),
                        new KillMobObjective("alpha_wolf", EntityType.WOLF, 1),
                        new CollectItemObjective("leather_collect", Material.LEATHER, 10),
                        new InteractNPCObjective("report_success", "caravan_leader")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1400)
                        .addCurrency(CurrencyType.GOLD, 350)
                        .addItem(new ItemStack(Material.BOW, 1))
                        .addItem(new ItemStack(Material.ARROW, 64))
                        .addItem(new ItemStack(Material.LEATHER_CHESTPLATE, 1))
                        .build())
                .sequential(false)
                .category(QuestCategory.SIDE)
                .minLevel(10);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_caravan_leader" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.objectives.talk.caravan.leader"), who);
            case "wolf_territory" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.objectives.wolf.territory"), who);
            case "compass_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.objectives.compass.collect"), who);
            case "wolf_scouts" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.objectives.wolf.scouts"), who);
            case "pack_den" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.objectives.pack.den"), who);
            case "pack_members" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.objectives.pack.members"), who);
            case "alpha_wolf" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.objectives.alpha.wolf"), who);
            case "leather_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.objectives.leather.collect"), who);
            case "report_success" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.objectives.report.success"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.wolf.pack.menace.decline"), who);
    }
}