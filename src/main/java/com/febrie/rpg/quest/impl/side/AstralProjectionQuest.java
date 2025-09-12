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
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.quest.side.AstralProjectionLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Astral Projection
 * Help Brother Transcendence return from the astral plane
 *
 * @author Febrie
 */
public class AstralProjectionQuest extends Quest {

    /**
     * 기본 생성자
     */
    public AstralProjectionQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_ASTRAL_PROJECTION)
                .objectives(List.of(
                        new InteractNPCObjective("talk_brother_seeker", "brother_seeker"),
                        new CollectItemObjective("amethyst_shard_collect", Material.AMETHYST_SHARD, 5),
                        new VisitLocationObjective("astral_gateway", "Astral_Gateway"),
                        new CollectItemObjective("soul_lantern_collect", Material.SOUL_LANTERN, 3),
                        new VisitLocationObjective("ethereal_maze", "Ethereal_Maze"),
                        new CollectItemObjective("experience_bottle_collect", Material.EXPERIENCE_BOTTLE, 10),
                        new InteractNPCObjective("rescue_brother_transcendence", "brother_transcendence")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1800)
                        .addCurrency(CurrencyType.GOLD, 400)
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 1))
                        .addItem(new ItemStack(Material.SOUL_LANTERN, 2))
                        .addItem(new ItemStack(Material.AMETHYST_BLOCK, 1))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(15);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(AstralProjectionLangKey.QUEST_SIDE_ASTRAL_PROJECTION_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(AstralProjectionLangKey.QUEST_SIDE_ASTRAL_PROJECTION_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_brother_seeker" -> LangManager.text(AstralProjectionLangKey.QUEST_SIDE_ASTRAL_PROJECTION_OBJECTIVES_TALK_BROTHER_SEEKER, who);
            case "amethyst_shard_collect" -> LangManager.text(AstralProjectionLangKey.QUEST_SIDE_ASTRAL_PROJECTION_OBJECTIVES_AMETHYST_SHARD_COLLECT, who);
            case "astral_gateway" -> LangManager.text(AstralProjectionLangKey.QUEST_SIDE_ASTRAL_PROJECTION_OBJECTIVES_ASTRAL_GATEWAY, who);
            case "soul_lantern_collect" -> LangManager.text(AstralProjectionLangKey.QUEST_SIDE_ASTRAL_PROJECTION_OBJECTIVES_SOUL_LANTERN_COLLECT, who);
            case "ethereal_maze" -> LangManager.text(AstralProjectionLangKey.QUEST_SIDE_ASTRAL_PROJECTION_OBJECTIVES_ETHEREAL_MAZE, who);
            case "experience_bottle_collect" -> LangManager.text(AstralProjectionLangKey.QUEST_SIDE_ASTRAL_PROJECTION_OBJECTIVES_EXPERIENCE_BOTTLE_COLLECT, who);
            case "rescue_brother_transcendence" -> LangManager.text(AstralProjectionLangKey.QUEST_SIDE_ASTRAL_PROJECTION_OBJECTIVES_RESCUE_BROTHER_TRANSCENDENCE, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(AstralProjectionLangKey.QUEST_SIDE_ASTRAL_PROJECTION_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(AstralProjectionLangKey.QUEST_SIDE_ASTRAL_PROJECTION_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(AstralProjectionLangKey.QUEST_SIDE_ASTRAL_PROJECTION_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(AstralProjectionLangKey.QUEST_SIDE_ASTRAL_PROJECTION_DECLINE, who);
    }
}