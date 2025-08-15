package com.febrie.rpg.quest.impl.main.chapter1;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.dialog.QuestDialog;
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

import java.util.Arrays;
import java.util.List;

/**
 * 첫 번째 시험 - 메인 퀘스트 Chapter 1
 * 선택받은 자로서의 첫 번째 대규모 시험
 *
 * @author Febrie
 */
public class FirstTrialQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class FirstTrialBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new FirstTrialQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public FirstTrialQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private FirstTrialQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new FirstTrialBuilder().id(QuestID.MAIN_FIRST_TRIAL)
                .objectives(Arrays.asList(
                        // 준비 단계
                        new InteractNPCObjective("meet_trainer", "trial_trainer"), // 시련의 훈련관
                        new CollectItemObjective("gather_potions", Material.POTION, 10), new CraftItemObjective("craft_shield", Material.SHIELD, 1),

                        // 시련의 경기장 입장
                        new VisitLocationObjective("enter_arena", "trial_arena"),

                        // 1차 웨이브 - 언데드
                        new SurviveObjective("survive_wave1", 180), // 3분
                        new KillMobObjective("wave1_zombies", EntityType.ZOMBIE, 20), new KillMobObjective("wave1_skeletons", EntityType.SKELETON, 15),

                        // 2차 웨이브 - 거미와 크리퍼
                        new SurviveObjective("survive_wave2", 180), // 3분
                        new KillMobObjective("wave2_spiders", EntityType.SPIDER, 15), new KillMobObjective("wave2_creepers", EntityType.CREEPER, 10),

                        // 3차 웨이브 - 보스전
                        new KillMobObjective("boss_fight", EntityType.RAVAGER, 1), new CollectItemObjective("trial_medal", Material.GOLD_INGOT, 1),

                        // 완료
                        new DeliverItemObjective("return_medal", "현자 도란", Material.GOLD_INGOT, 1)))
                .reward(new BasicReward.Builder().addCurrency(CurrencyType.GOLD, 3000)
                        .addCurrency(CurrencyType.DIAMOND, 30)
                        .addItem(new ItemStack(Material.DIAMOND_CHESTPLATE))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING))
                        .addExperience(3000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(20)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_CHOSEN_ONE);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.main.first_trial.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getList(who, "quest.main.first_trial.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.main.first_trial.objectives." + id);
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("first_trial_dialog");
        dialog.addLine("quest.main.first_trial.dialog.trainer1", "quest.main.first_trial.dialog.trainer1");
        dialog.addLine("quest.main.first_trial.dialog.trainer2", "quest.main.first_trial.dialog.trainer2");
        dialog.addLine("quest.main.first_trial.dialog.trainer3", "quest.main.first_trial.dialog.trainer3");
        dialog.addLine("quest.main.first_trial.dialog.player1", "quest.main.first_trial.dialog.player1");
        dialog.addLine("quest.main.first_trial.dialog.trainer4", "quest.main.first_trial.dialog.trainer4");
        dialog.addLine("quest.main.first_trial.dialog.trainer5", "quest.main.first_trial.dialog.trainer5");
        dialog.addLine("quest.main.first_trial.dialog.player2", "quest.main.first_trial.dialog.player2");
        return dialog;
    }
}