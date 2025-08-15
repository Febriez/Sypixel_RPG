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
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 선택받은 자 - 메인 퀘스트 Chapter 1
 * 영웅으로서의 자질을 시험받는 퀘스트
 *
 * @author Febrie
 */
public class ChosenOneQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class ChosenOneBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new ChosenOneQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public ChosenOneQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private ChosenOneQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new ChosenOneBuilder().id(QuestID.MAIN_CHOSEN_ONE)
                .objectives(Arrays.asList(
                        // 시련의 동굴 입장
                        new VisitLocationObjective("enter_trial_cave", "trial_cave_entrance"),

                        // 첫 번째 시련: 용기
                        new KillMobObjective("trial_courage", EntityType.IRON_GOLEM, 3), new CollectItemObjective("courage_proof", Material.IRON_BLOCK, 1),

                        // 두 번째 시련: 지혜
                        new BreakBlockObjective("solve_puzzle", Material.REDSTONE_LAMP, 5), new CollectItemObjective("wisdom_proof", Material.EMERALD, 1),

                        // 세 번째 시련: 희생
                        new PayCurrencyObjective("sacrifice_gold", CurrencyType.GOLD, 1000), new CollectItemObjective("sacrifice_proof", Material.DIAMOND, 1),

                        // 최종 시련
                        new KillMobObjective("final_guardian", EntityType.WITHER_SKELETON, 1), new CollectItemObjective("chosen_emblem", Material.NETHER_STAR, 1),

                        // 완료
                        new DeliverItemObjective("return_elder", "고대의 장로", Material.NETHER_STAR, 1)))
                .reward(new BasicReward.Builder().addCurrency(CurrencyType.GOLD, 2000)
                        .addCurrency(CurrencyType.DIAMOND, 20)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD))
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addExperience(2000)
                        .build())
                .sequential(true)  // 순차적으로 진행
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(15)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_ANCIENT_PROPHECY);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.main.chosen_one.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getList(who, "quest.main.chosen_one.info");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.main.chosen_one.objectives." + id);
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("chosen_one_dialog");

        dialog.addLine("quest.chosen_one.npcs.elder", "quest.chosen_one.dialogs.line1");
        dialog.addLine("quest.chosen_one.npcs.elder", "quest.chosen_one.dialogs.line2");
        dialog.addLine("quest.chosen_one.npcs.elder", "quest.chosen_one.dialogs.line3");
        dialog.addLine("quest.chosen_one.npcs.elder", "quest.chosen_one.dialogs.line4");
        dialog.addLine("quest.dialog.player", "quest.chosen_one.dialogs.player_line1");
        dialog.addLine("quest.chosen_one.npcs.elder", "quest.chosen_one.dialogs.line5");

        return dialog;
    }
}