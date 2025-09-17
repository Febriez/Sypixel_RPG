package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
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
 * Side Quest: Innkeeper Trouble
 * Help the innkeeper deal with pests in the basement and recover stolen supplies.
 *
 * @author Febrie
 */
public class InnkeeperTroubleQuest extends Quest {

    /**
     * 기본 생성자
     */
    public InnkeeperTroubleQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_INNKEEPER_TROUBLE)
                .objectives(List.of(
                        new InteractNPCObjective("talk_worried_innkeeper", "worried_innkeeper"),
                        new VisitLocationObjective("visit_inn_basement", "inn_basement"),
                        new KillMobObjective("kill_spiders", EntityType.SPIDER, 12),
                        new CollectItemObjective("bread_collect", Material.BREAD, 20),
                        new CollectItemObjective("barrel_collect", Material.BARREL, 3),
                        new VisitLocationObjective("visit_storage_room", "storage_room"),
                        new InteractNPCObjective("return_worried_innkeeper", "worried_innkeeper")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1000)
                        .addCurrency(CurrencyType.GOLD, 250)
                        .addItem(new ItemStack(Material.BREAD, 10))
                        .addItem(new ItemStack(Material.COOKED_BEEF, 5))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(8);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.innkeeper.trouble.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.innkeeper.trouble.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_worried_innkeeper" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.innkeeper.trouble.objectives.talk.worried.innkeeper"), who);
            case "visit_inn_basement" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.innkeeper.trouble.objectives.visit.inn.basement"), who);
            case "kill_spiders" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.innkeeper.trouble.objectives.kill.spiders"), who);
            case "bread_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.innkeeper.trouble.objectives.bread.collect"), who);
            case "barrel_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.innkeeper.trouble.objectives.barrel.collect"), who);
            case "visit_storage_room" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.innkeeper.trouble.objectives.visit.storage.room"), who);
            case "return_worried_innkeeper" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.innkeeper.trouble.objectives.return.worried.innkeeper"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.innkeeper.trouble.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.innkeeper.trouble.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.innkeeper.trouble.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.innkeeper.trouble.decline"), who);
    }
}