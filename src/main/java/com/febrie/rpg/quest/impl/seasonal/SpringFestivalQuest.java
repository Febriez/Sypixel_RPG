package com.febrie.rpg.quest.impl.seasonal;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.economy.CurrencyType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SpringFestivalQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class SpringFestivalBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new SpringFestivalQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public SpringFestivalQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private SpringFestivalQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 빌더 생성 메서드
     */
    private static QuestBuilder createBuilder() {
        return new SpringFestivalBuilder()
                .id(QuestID.SEASON_SPRING_FESTIVAL)
                .category(QuestCategory.EVENT)
                .sequential(false)
                .repeatable(true)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("festival_host", "spring_festival_host"),
                        new CollectItemObjective("collect_flowers", Material.DANDELION, 20),
                        new CollectItemObjective("collect_tulips", Material.RED_TULIP, 15),
                        new CollectItemObjective("collect_seeds", Material.WHEAT_SEEDS, 30),
                        new PlaceBlockObjective("plant_flowers", Material.DANDELION, 10),
                        new PlaceBlockObjective("plant_tulips", Material.RED_TULIP, 10),
                        new HarvestObjective("harvest_crops", Material.WHEAT, 20),
                        new CraftItemObjective("make_dyes", Material.YELLOW_DYE, 10),
                        new KillMobObjective("protect_festival", EntityType.ZOMBIE, 15),
                        new DeliverItemObjective("deliver_decorations", "축제_진행자", Material.YELLOW_DYE, 10)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3000)
                        .addExperience(1000)
                        .build())
                .minLevel(10)
                .maxLevel(100)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.spring_festival.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getComponentList(who, "quest.spring_festival.info");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.spring_festival.objective." + id);
    }
    
    @Override
    public QuestDialog getDialog(@NotNull Player player) {
        QuestDialog dialog = new QuestDialog("spring_festival_dialog");
        
        dialog.addLine("quest.spring_festival.npcs.festival_host", "quest.spring_festival.dialogs.start_line1");
        
        return dialog;
    }
}