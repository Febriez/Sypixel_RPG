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
 * Side Quest: Cursed Artifact
 * Find and contain a dangerous cursed artifact spreading corruption
 *
 * @author Febrie
 */
public class CursedArtifactQuest extends Quest {

    /**
     * 기본 생성자
     */
    public CursedArtifactQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_CURSED_ARTIFACT)
                .objectives(List.of(
                        new InteractNPCObjective("talk_concerned_scholar", "concerned_scholar"),
                        new VisitLocationObjective("corrupted_grove", "Corrupted_Grove"),
                        new CollectItemObjective("sugar_collect", Material.SUGAR, 10),
                        new CollectItemObjective("golden_apple_collect", Material.GOLDEN_APPLE, 3),
                        new KillMobObjective("corrupted_creatures", EntityType.ZOMBIE, 8),
                        new VisitLocationObjective("artifact_chamber", "Artifact_Chamber"),
                        new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 1),
                        new InteractNPCObjective("seal_artifact", "artifact_sealer")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2000)
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 1))
                        .addItem(new ItemStack(Material.BEACON, 1))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(20);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.cursed.artifact.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.cursed.artifact.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_concerned_scholar" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.cursed.artifact.objectives.talk.concerned.scholar"), who);
            case "corrupted_grove" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.cursed.artifact.objectives.corrupted.grove"), who);
            case "sugar_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.cursed.artifact.objectives.sugar.collect"), who);
            case "golden_apple_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.cursed.artifact.objectives.golden.apple.collect"), who);
            case "corrupted_creatures" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.cursed.artifact.objectives.corrupted.creatures"), who);
            case "artifact_chamber" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.cursed.artifact.objectives.artifact.chamber"), who);
            case "nether_star_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.cursed.artifact.objectives.nether.star.collect"), who);
            case "seal_artifact" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.cursed.artifact.objectives.seal.artifact"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.cursed.artifact.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.cursed.artifact.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.cursed.artifact.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.cursed.artifact.decline"), who);
    }
}