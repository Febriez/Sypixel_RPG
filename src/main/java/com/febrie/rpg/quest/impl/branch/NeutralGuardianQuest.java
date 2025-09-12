package com.febrie.rpg.quest.impl.branch;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 중립 수호자 전직 퀘스트
 * 균형의 길을 선택한 플레이어가 중립 수호자가 되는 퀘스트
 *
 * @author Febrie
 */
public class NeutralGuardianQuest extends Quest {

    /**
     * 기본 생성자
     */
    public NeutralGuardianQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.BRANCH_NEUTRAL_GUARDIAN)
                .objectives(List.of(
                        new InteractNPCObjective("guardian_sage", "neutral_guardian_sage"),
                        new CollectItemObjective("amethyst_shard_collect", Material.AMETHYST_SHARD, 12),
                        new KillMobObjective("maintain_peace", EntityType.RAVAGER, 20),
                        new KillMobObjective("protect_balance", EntityType.VEX, 35),
                        new VisitLocationObjective("harmony_temple", "balance_shrine"),
                        new SurviveObjective("meditation_balance", 720), // 12 minutes
                        new CraftItemObjective("diamond_hoe_craft", Material.DIAMOND_HOE, 1),
                        new PlaceBlockObjective("build_sanctuary", Material.SMOOTH_QUARTZ, 16),
                        new KillMobObjective("stop_chaos", EntityType.SHULKER, 15),
                        new CollectItemObjective("ender_pearl_collect", Material.ENDER_PEARL, 5),
                        new DeliverItemObjective("diamond_hoe_deliver", Material.DIAMOND_HOE, 1, "neutral_guardian_sage")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 13500)
                        .addExperience(2750)
                        .build())
                .sequential(true)
                .category(QuestCategory.BRANCH)
                .minLevel(40)
                .maxLevel(100)
                .addExclusive(QuestID.BRANCH_LIGHT_PALADIN)
                .addExclusive(QuestID.BRANCH_DARK_KNIGHT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "guardian_sage" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_OBJECTIVES_GUARDIAN_SAGE, who);
            case "amethyst_shard_collect" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_OBJECTIVES_AMETHYST_SHARD_COLLECT, who);
            case "maintain_peace" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_OBJECTIVES_MAINTAIN_PEACE, who);
            case "protect_balance" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_OBJECTIVES_PROTECT_BALANCE, who);
            case "harmony_temple" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_OBJECTIVES_HARMONY_TEMPLE, who);
            case "meditation_balance" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_OBJECTIVES_MEDITATION_BALANCE, who);
            case "diamond_hoe_craft" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_OBJECTIVES_DIAMOND_HOE_CRAFT, who);
            case "build_sanctuary" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_OBJECTIVES_BUILD_SANCTUARY, who);
            case "stop_chaos" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_OBJECTIVES_STOP_CHAOS, who);
            case "ender_pearl_collect" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_OBJECTIVES_ENDER_PEARL_COLLECT, who);
            case "diamond_hoe_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_OBJECTIVES_DIAMOND_HOE_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_BRANCH_NEUTRAL_GUARDIAN_DECLINE, who);
    }
}