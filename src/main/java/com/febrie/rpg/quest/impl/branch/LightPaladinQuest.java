package com.febrie.rpg.quest.impl.branch;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.economy.CurrencyType;
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
     * 퀘스트 빌더
     */
    private static class LightPaladinBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new LightPaladinQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public LightPaladinQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private LightPaladinQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new LightPaladinBuilder()
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
        return Component.translatable("quest.branch.light_paladin.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of() /* TODO: Convert LangManager.getList("quest.branch.light_paladin.description") manually */;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return Component.translatable("quest.branch.light_paladin.objectives.");
    }
}