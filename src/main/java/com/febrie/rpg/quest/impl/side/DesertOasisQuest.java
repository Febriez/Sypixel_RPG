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
 * Desert Oasis - Side Quest
 * Guide a desert nomad to find a legendary hidden oasis in the vast wasteland.
 *
 * @author Febrie
 */
public class DesertOasisQuest extends Quest {

    /**
     * 기본 생성자
     */
    public DesertOasisQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_DESERT_OASIS)
                .objectives(List.of(
                        new InteractNPCObjective("talk_desert_nomad", "desert_nomad"),
                        new VisitLocationObjective("mirages_edge", "Mirages_Edge"),
                        new KillMobObjective("kill_husks", EntityType.HUSK, 20),
                        new CollectItemObjective("cactus_collect", Material.CACTUS, 12),
                        new VisitLocationObjective("hidden_oasis", "Hidden_Oasis"),
                        new CollectItemObjective("water_bucket_collect", Material.WATER_BUCKET, 3)
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(4000)
                        .addCurrency(CurrencyType.GOLD, 1000)
                        .addItem(new ItemStack(Material.DIAMOND_HELMET))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(25);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.desert.oasis.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.desert.oasis.info"), who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_desert_nomad" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.desert.oasis.objectives.talk.desert.nomad"), who);
            case "mirages_edge" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.desert.oasis.objectives.mirages.edge"), who);
            case "kill_husks" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.desert.oasis.objectives.kill.husks"), who);
            case "cactus_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.desert.oasis.objectives.cactus.collect"), who);
            case "hidden_oasis" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.desert.oasis.objectives.hidden.oasis"), who);
            case "water_bucket_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.desert.oasis.objectives.water.bucket.collect"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.desert.oasis.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.desert.oasis.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.desert.oasis.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.desert.oasis.decline"), who);
    }
}