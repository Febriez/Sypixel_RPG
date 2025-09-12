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
import com.febrie.rpg.util.LangKey;
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
 * 주간 레이드: 혼돈의 요새 - 주간 퀘스트
 * 매주 등장하는 강력한 레이드 보스를 다수의 영웅과 함께 처치하는 퀘스트
 *
 * @author Febrie
 */
public class RaidBossQuest extends Quest {

    /**
     * 기본 생성자
     */
    public RaidBossQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.WEEKLY_RAID_BOSS)
                .objectives(List.of(
                    new InteractNPCObjective("form_party", "raid_party_coordinator"),
                    new BreakBlockObjective("breach_gates", Material.IRON_DOOR, 1),
                    new KillMobObjective("defeat_minibosses", EntityType.VINDICATOR, 3),
                    new VisitLocationObjective("reach_throne", "fortress_throne_room"),
                    new KillMobObjective("defeat_raid_boss", EntityType.ENDER_DRAGON, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 10000)
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addItem(new ItemStack(Material.DRAGON_HEAD))
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .weekly(true)
                .category(QuestCategory.WEEKLY)
                .minLevel(50)
                .completionLimit(1);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RAID_BOSS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_RAID_BOSS_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "form_party" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RAID_BOSS_OBJECTIVES_FORM_PARTY, who);
            case "breach_gates" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RAID_BOSS_OBJECTIVES_BREACH_GATES, who);
            case "defeat_minibosses" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RAID_BOSS_OBJECTIVES_DEFEAT_MINIBOSSES, who);
            case "reach_throne" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RAID_BOSS_OBJECTIVES_REACH_THRONE, who);
            case "defeat_raid_boss" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RAID_BOSS_OBJECTIVES_DEFEAT_RAID_BOSS, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }

    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_RAID_BOSS_DIALOGS, who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RAID_BOSS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RAID_BOSS_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RAID_BOSS_DECLINE, who);
    }
}