package com.febrie.rpg.quest.impl.combat;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.KillPlayerObjective;
import com.febrie.rpg.quest.objective.impl.SurviveObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 아레나 검투사 - 전투 퀘스트
 * 아레나에서 싸우는 전투 챌린지
 *
 * @author Febrie
 */
public class ArenaGladiatorQuest extends Quest {

    /**
     * 기본 생성자
     */
    public ArenaGladiatorQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.COMBAT_ARENA_GLADIATOR)
                .objectives(List.of(
                        new KillPlayerObjective("defeat_players", 10),
                        new KillPlayerObjective("pvp_kills", 5),
                        new SurviveObjective("survive_combat", 15)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD, 1))
                        .addItem(new ItemStack(Material.NETHERITE_CHESTPLATE, 1))
                        .addExperience(800)
                        .build())
                .sequential(true)
                .category(QuestCategory.COMBAT)
                .minLevel(25);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_COMBAT_ARENA_GLADIATOR_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_COMBAT_ARENA_GLADIATOR_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "defeat_players" -> LangManager.text(QuestCommonLangKey.QUEST_COMBAT_ARENA_GLADIATOR_OBJECTIVES_DEFEAT_PLAYERS, who);
            case "pvp_kills" -> LangManager.text(QuestCommonLangKey.QUEST_COMBAT_ARENA_GLADIATOR_OBJECTIVES_PVP_KILLS, who);
            case "survive_combat" -> LangManager.text(QuestCommonLangKey.QUEST_COMBAT_ARENA_GLADIATOR_OBJECTIVES_SURVIVE_COMBAT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_COMBAT_ARENA_GLADIATOR_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_COMBAT_ARENA_GLADIATOR_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_COMBAT_ARENA_GLADIATOR_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_COMBAT_ARENA_GLADIATOR_DECLINE, who);
    }
}