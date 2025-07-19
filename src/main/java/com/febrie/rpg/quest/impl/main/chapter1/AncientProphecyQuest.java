package com.febrie.rpg.quest.impl.main.chapter1;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 고대의 예언 - 메인 퀘스트 Chapter 1
 * 운명의 시작
 */
public class AncientProphecyQuest extends Quest {

    private static class AncientProphecyBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new AncientProphecyQuest(this);
        }
    }

    public AncientProphecyQuest() {
        this(createBuilder());
    }

    private AncientProphecyQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    private static QuestBuilder createBuilder() {
        return new AncientProphecyBuilder()
                .id(QuestID.MAIN_ANCIENT_PROPHECY)
                .objectives(Arrays.asList(
                        new VisitLocationObjective("visit_elder", "ancient_temple"),
                        new InteractNPCObjective("talk_elder", "ancient_elder"), // 고대의 장로
                        new CollectItemObjective("collect_scrolls", Material.PAPER, 5),
                        new DeliverItemObjective("deliver_scrolls", "고대의 장로", Material.PAPER, 5)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addCurrency(CurrencyType.DIAMOND, 10)
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK))
                        .addExperience(1000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(10)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_HEROES_JOURNEY);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "고대의 예언" : "The Ancient Prophecy";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "오래된 예언서에는 세계를 구할 영웅의 이야기가 적혀있다.",
                    "고대의 장로를 찾아가 예언의 진실을 알아보자.",
                    "",
                    "이 퀘스트는 순차적으로 진행됩니다.",
                    "",
                    "목표:",
                    "• 고대 사원 방문하기",
                    "• 고대의 장로와 대화하기",
                    "• 고대 두루마리 5개 수집하기",
                    "• 고대의 장로에게 두루마리 전달하기",
                    "",
                    "보상:",
                    "• 골드 500",
                    "• 다이아몬드 10개",
                    "• 마법이 부여된 책",
                    "• 경험치 1000"
            );
        } else {
            return Arrays.asList(
                    "An ancient prophecy speaks of a hero who will save the world.",
                    "Seek the Elder to learn the truth of the prophecy.",
                    "",
                    "This quest progresses sequentially.",
                    "",
                    "Objectives:",
                    "• Visit the Ancient Temple",
                    "• Talk to the Ancient Elder",
                    "• Collect 5 Ancient Scrolls",
                    "• Deliver scrolls to the Ancient Elder",
                    "",
                    "Rewards:",
                    "• 500 Gold",
                    "• 10 Diamonds",
                    "• Enchanted Book",
                    "• 1000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "visit_elder" -> isKorean ? "고대 사원 방문하기" : "Visit the Ancient Temple";
            case "talk_elder" -> isKorean ? "고대의 장로와 대화하기" : "Talk to the Ancient Elder";
            case "collect_scrolls" -> isKorean ? "고대 두루마리 5개 수집하기" : "Collect 5 Ancient Scrolls";
            case "deliver_scrolls" -> isKorean ? "고대의 장로에게 두루마리 전달하기" : "Deliver scrolls to the Ancient Elder";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("ancient_prophecy_dialog");

        dialog.addLine("고대의 장로",
                "아... 드디어 예언의 아이가 나타났구나.",
                "Ah... The child of prophecy has finally appeared.");

        dialog.addLine("고대의 장로",
                "천 년 전, 위대한 현자가 남긴 예언이 있다네.",
                "A thousand years ago, a great sage left a prophecy.");

        dialog.addLine("고대의 장로",
                "어둠이 세계를 뒤덮을 때, 빛의 전사가 나타나리라...",
                "When darkness covers the world, a warrior of light shall appear...");

        dialog.addLine("고대의 장로",
                "하지만 예언서가 흩어져 있어. 모두 모아오면 진실을 알려주겠네.",
                "But the prophecy scrolls are scattered. Gather them all and I will reveal the truth.");

        return dialog;
    }
}