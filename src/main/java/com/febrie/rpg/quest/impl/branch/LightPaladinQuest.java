package com.febrie.rpg.quest.impl.branch;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.util.LangHelper;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
                .objectives(Arrays.asList(
                        new InteractNPCObjective("paladin_master", "light_paladin_master"),
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
        return LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_BRANCH_LIGHT_PALADIN_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return switch (id) {
            case "paladin_master" -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_PALADIN_MASTER, who);
            case "holy_water" -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_HOLY_WATER, who);
            case "purge_undead" -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_PURGE_UNDEAD, who);
            case "purge_skeletons" -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_PURGE_SKELETONS, who);
            case "holy_shrine" -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_HOLY_SHRINE, who);
            case "meditation" -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_MEDITATION, who);
            case "holy_sword" -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_HOLY_SWORD, who);
            case "build_altar" -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_BUILD_ALTAR, who);
            case "defeat_darkness" -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_DEFEAT_DARKNESS, who);
            case "light_essence" -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_LIGHT_ESSENCE, who);
            case "oath_completion" -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_OATH_COMPLETION, who);
            default -> LangManager.get("quest.branch.light_paladin.objectives." + id, who);
        };
    }
    
    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_DIALOGS_2, who);
            case 3 -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_DIALOGS_3, who);
            case 4 -> LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_DIALOGS_4, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_BRANCH_LIGHT_PALADIN_DECLINE, who);
    }
}