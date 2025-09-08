package com.febrie.rpg.quest.impl.branch;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.economy.CurrencyType;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * 빛의 성기사 전직 퀘스트
 * 빛의 길을 선택한 플레이어가 성기사가 되는 퀘스트
 *
 * @author Febrie
 */
public class LightPaladinQuest extends Quest {

    /**
     * 기본 생성자
     */
    public LightPaladinQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.BRANCH_LIGHT_PALADIN)
                .objectives(List.of(
                        new InteractNPCObjective("paladin_master", "light_paladin_master", 1),
                        new CollectItemObjective("holy_water", Material.POTION, 10),
                        new KillMobObjective("purge_undead", EntityType.ZOMBIE, 50),
                        new KillMobObjective("purge_skeletons", EntityType.SKELETON, 50),
                        new VisitLocationObjective("holy_shrine", "light_shrine"),
                        new SurviveObjective("meditation", 600), // 10 minutes
                        new CraftItemObjective("holy_sword", Material.GOLDEN_SWORD, 1),
                        new PlaceBlockObjective("build_altar", Material.GLOWSTONE, 9),
                        new KillMobObjective("defeat_darkness", EntityType.WITHER_SKELETON, 20),
                        new CollectItemObjective("light_essence", Material.GLOWSTONE_DUST, 30),
                        new DeliverItemObjective("oath_completion", "paladin_master", Material.GOLDEN_SWORD, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 12000)
                        .addExperience(2500)
                        .build())
                .sequential(true)
                .category(QuestCategory.BRANCH)
                .minLevel(40)
                .maxLevel(100)
                .addPrerequisite(QuestID.MAIN_PATH_OF_LIGHT)
                .addExclusive(QuestID.BRANCH_DARK_KNIGHT)
                .addExclusive(QuestID.BRANCH_NEUTRAL_GUARDIAN);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_BRANCH_LIGHT_PALADIN_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "light_essence" -> LangManager.list(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_LIGHT_ESSENCE, who);
            case "purge_skeletons" -> LangManager.list(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_PURGE_SKELETONS, who);
            case "meditation" -> LangManager.list(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_MEDITATION, who);
            case "holy_shrine" -> LangManager.list(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_HOLY_SHRINE, who);
            case "paladin_master" -> LangManager.list(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_PALADIN_MASTER, who);
            case "holy_water" -> LangManager.list(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_HOLY_WATER, who);
            case "purge_undead" -> LangManager.list(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_PURGE_UNDEAD, who);
            case "oath_completion" -> LangManager.list(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_OATH_COMPLETION, who);
            case "holy_sword" -> LangManager.list(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_HOLY_SWORD, who);
            case "build_altar" -> LangManager.list(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_BUILD_ALTAR, who);
            case "defeat_darkness" -> LangManager.list(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_DEFEAT_DARKNESS, who);
            default -> new ArrayList<>();
        };
    }
    
    @Override
    public int getDialogCount() {
        return 5;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_BRANCH_LIGHT_PALADIN_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_DECLINE, who);
    }
}