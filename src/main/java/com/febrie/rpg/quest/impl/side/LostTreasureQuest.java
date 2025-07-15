package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 잃어버린 보물 - 사이드 퀘스트
 * 모험가가 잃어버린 보물을 찾아주는 퀘스트
 *
 * @author Febrie
 */
public class LostTreasureQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class LostTreasureBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new LostTreasureQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public LostTreasureQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private LostTreasureQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static Builder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 위치 탐색 목표 (순차적으로 진행)
        // 주의: 실제 서버에서는 월드 이름을 확인해야 함
        objectives.add(new VisitLocationObjective("visit_old_ruins", 
                new Location(Bukkit.getWorld("world"), 100, 70, -200), 10.0, "오래된 유적지"));
        objectives.add(new VisitLocationObjective("visit_ancient_cave", 
                new Location(Bukkit.getWorld("world"), -150, 50, 300), 10.0, "고대 동굴"));
        objectives.add(new VisitLocationObjective("visit_hidden_shrine", 
                new Location(Bukkit.getWorld("world"), 250, 80, 150), 10.0, "숨겨진 신전"));
        
        // NPC와 상호작용 (보물 수호자)
        objectives.add(new InteractNPCObjective("talk_to_guardian", 
                "보물 수호자"));

        return new LostTreasureBuilder()
                .id(QuestID.SIDE_LOST_TREASURE)
                .objectives(objectives)
                .reward(BasicReward.builder()
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addCurrency(CurrencyType.DIAMOND, 5)
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 2))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK))
                        .addExperience(300)
                        .build())
                .sequential(true) // 순차적으로 진행
                .category(QuestCategory.SIDE)
                .minLevel(10);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "잃어버린 보물" : "Lost Treasure";
    }

    @Override
    public @NotNull List<String> getDescription(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "오래된 지도에 표시된 보물을 찾아주세요.",
                    "세 곳의 장소를 순서대로 방문해야 합니다.",
                    "",
                    "목표:",
                    "• 오래된 유적지 방문",
                    "• 고대 동굴 방문",
                    "• 숨겨진 신전 방문",
                    "• 보물 수호자와 대화",
                    "",
                    "보상:",
                    "• 골드 500",
                    "• 다이아몬드 5",
                    "• 황금 사과 2개",
                    "• 마법이 부여된 책",
                    "• 경험치 300"
            );
        } else {
            return Arrays.asList(
                    "Find the treasure marked on the old map.",
                    "You must visit three locations in order.",
                    "",
                    "Objectives:",
                    "• Visit Old Ruins",
                    "• Visit Ancient Cave",
                    "• Visit Hidden Shrine",
                    "• Talk to Treasure Guardian",
                    "",
                    "Rewards:",
                    "• 500 Gold",
                    "• 5 Diamonds",
                    "• 2 Golden Apples",
                    "• Enchanted Book",
                    "• 300 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "visit_old_ruins" -> isKorean ? "오래된 유적지 방문" : "Visit Old Ruins";
            case "visit_ancient_cave" -> isKorean ? "고대 동굴 방문" : "Visit Ancient Cave";
            case "visit_hidden_shrine" -> isKorean ? "숨겨진 신전 방문" : "Visit Hidden Shrine";
            case "talk_to_guardian" -> isKorean ? "보물 수호자와 대화" : "Talk to Treasure Guardian";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("lost_treasure_dialog");

        dialog.addLine("탐험가 존",
                "이 오래된 지도를 보세요. 전설의 보물이 숨겨져 있다고 합니다.",
                "Look at this old map. It's said to lead to a legendary treasure.");

        dialog.addLine("탐험가 존",
                "저는 나이가 들어 직접 찾으러 갈 수가 없네요.",
                "I'm too old to search for it myself.");

        dialog.addLine("탐험가 존",
                "지도에 표시된 세 곳을 순서대로 방문하면 보물을 찾을 수 있을 거예요.",
                "Visit the three marked locations in order, and you'll find the treasure.");

        dialog.addLine("탐험가 존",
                "보물을 찾으면 일부는 당신이 가지세요. 나머지만 가져와 주시면 됩니다.",
                "When you find the treasure, keep some for yourself. Just bring me the rest.");

        return dialog;
    }
}