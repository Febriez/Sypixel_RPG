package com.febrie.rpg.quest.impl.weekly;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
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
 * 월드 타이탄 정벌 - 주간 퀘스트
 * 전 세계 영웅들과 협력하여 거대한 월드 보스 타이탄을 처치하는 퀘스트
 *
 * @author Febrie
 */
public class WorldBossQuest extends Quest {

    /**
     * 기본 생성자
     */
    public WorldBossQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.WEEKLY_WORLD_BOSS)
                .objectives(List.of(
                    new VisitLocationObjective("locate_titan", "titan_spawn_location"),
                    new InteractNPCObjective("rally_heroes", "world_coordinator"),
                    new KillMobObjective("weaken_titan", EntityType.IRON_GOLEM, 5), // Representing weakening phases
                    new InteractNPCObjective("coordinate_attack", "battle_commander"),
                    new KillMobObjective("defeat_titan", EntityType.WITHER, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 15000)
                        .addItem(new ItemStack(Material.NETHER_STAR, 3))
                        .addItem(new ItemStack(Material.BEACON, 2))
                        .addExperience(6000)
                        .build())
                .sequential(true)
                .weekly(true)
                .category(QuestCategory.WEEKLY)
                .minLevel(55)
                .completionLimit(1);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_WORLD_BOSS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_WORLD_BOSS_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "locate_titan" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_WORLD_BOSS_OBJECTIVES_LOCATE_TITAN, who);
            case "rally_heroes" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_WORLD_BOSS_OBJECTIVES_RALLY_HEROES, who);
            case "weaken_titan" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_WORLD_BOSS_OBJECTIVES_WEAKEN_TITAN, who);
            case "coordinate_attack" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_WORLD_BOSS_OBJECTIVES_COORDINATE_ATTACK, who);
            case "defeat_titan" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_WORLD_BOSS_OBJECTIVES_DEFEAT_TITAN, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }

    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_WORLD_BOSS_DIALOGS, who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_WORLD_BOSS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_WORLD_BOSS_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_WORLD_BOSS_DECLINE, who);
    }
}