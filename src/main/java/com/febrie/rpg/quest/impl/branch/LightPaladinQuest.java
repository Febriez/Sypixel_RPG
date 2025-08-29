package com.febrie.rpg.quest.impl.branch;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.economy.CurrencyType;
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
        return LangManager.get("quest.branch.light_paladin.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.branch.light_paladin.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return switch (id) {
            case "paladin_master" -> LangManager.get("quest.branch.light_paladin.objectives.paladin_master", who);
            case "holy_water" -> LangManager.get("quest.branch.light_paladin.objectives.holy_water", who);
            case "purge_undead" -> LangManager.get("quest.branch.light_paladin.objectives.purge_undead", who);
            case "purge_skeletons" -> LangManager.get("quest.branch.light_paladin.objectives.purge_skeletons", who);
            case "holy_shrine" -> LangManager.get("quest.branch.light_paladin.objectives.holy_shrine", who);
            case "meditation" -> LangManager.get("quest.branch.light_paladin.objectives.meditation", who);
            case "holy_sword" -> LangManager.get("quest.branch.light_paladin.objectives.holy_sword", who);
            case "build_altar" -> LangManager.get("quest.branch.light_paladin.objectives.build_altar", who);
            case "defeat_darkness" -> LangManager.get("quest.branch.light_paladin.objectives.defeat_darkness", who);
            case "light_essence" -> LangManager.get("quest.branch.light_paladin.objectives.light_essence", who);
            case "oath_completion" -> LangManager.get("quest.branch.light_paladin.objectives.oath_completion", who);
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
            case 0 -> LangManager.get("quest.branch.light_paladin.dialogs.0", who);
            case 1 -> LangManager.get("quest.branch.light_paladin.dialogs.1", who);
            case 2 -> LangManager.get("quest.branch.light_paladin.dialogs.2", who);
            case 3 -> LangManager.get("quest.branch.light_paladin.dialogs.3", who);
            case 4 -> LangManager.get("quest.branch.light_paladin.dialogs.4", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.branch.light_paladin.npc_name", who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.branch.light_paladin.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.branch.light_paladin.decline", who);
    }
}