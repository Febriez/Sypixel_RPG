package com.febrie.rpg.quest.impl.exploration;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
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
 * 고대 유적 탐험 - 탐험 사이드 퀘스트
 * 숨겨진 고대 유적을 탐험하고 비밀을 밝혀내는 퀘스트
 *
 * @author Febrie
 */
public class AncientRuinsQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class AncientRuinsBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new AncientRuinsQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public AncientRuinsQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private AncientRuinsQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new AncientRuinsBuilder()
                .id(QuestID.SIDE_ANCIENT_RUINS)
                .objectives(Arrays.asList(
                        // 시작
                        new InteractNPCObjective("archaeologist_talk", "archaeologist_henry"), // 고고학자 헨리
                        new CollectItemObjective("gather_tools", Material.IRON_PICKAXE, 1),
                        new CollectItemObjective("gather_torches", Material.TORCH, 64),
                        
                        // 유적 입구 발견
                        new VisitLocationObjective("ruins_entrance", "ancient_ruins_entrance"),
                        new BreakBlockObjective("clear_entrance", Material.STONE, 30),
                        new BreakBlockObjective("excavate_dirt", Material.DIRT, 50),
                        
                        // 첫 번째 방 - 고대의 서고
                        new VisitLocationObjective("library_room", "ruins_library"),
                        new CollectItemObjective("ancient_books", Material.WRITTEN_BOOK, 3),
                        new KillMobObjective("library_guardians", EntityType.VEX, 10),
                        
                        // 두 번째 방 - 보물 창고
                        new VisitLocationObjective("treasure_room", "ruins_treasury"),
                        new BreakBlockObjective("break_pots", Material.DECORATED_POT, 10),
                        new CollectItemObjective("ancient_coins", Material.GOLD_NUGGET, 30),
                        new KillMobObjective("treasure_guardians", EntityType.SKELETON, 15),
                        
                        // 숨겨진 방 발견
                        new PlaceBlockObjective("place_lever", Material.LEVER, 1),
                        new VisitLocationObjective("secret_chamber", "ruins_secret_chamber"),
                        new KillMobObjective("ancient_sentinel", EntityType.IRON_GOLEM, 1),
                        new CollectItemObjective("ancient_artifact", Material.TOTEM_OF_UNDYING, 1),
                        
                        // 탈출
                        new SurviveObjective("escape_ruins", 180), // 3분
                        new DeliverItemObjective("return_artifact", "고고학자 헨리", Material.TOTEM_OF_UNDYING, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3000)
                        .addCurrency(CurrencyType.DIAMOND, 25)
                        .addItem(new ItemStack(Material.BRUSH))
                        .addItem(new ItemStack(Material.SPYGLASS))
                        .addItem(new ItemStack(Material.MAP))
                        .addExperience(2000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(Quest.QuestCategory.EXPLORATION)
                .minLevel(20)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "고대 유적의 비밀" : "Secrets of the Ancient Ruins";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "고고학자 헨리가 발견한 고대 유적을 탐험하세요.",
                    "숨겨진 보물과 고대의 비밀이 당신을 기다립니다.",
                    "",
                    "⚠️ 주의: 유적에는 위험한 함정과 수호자들이 있습니다!",
                    "",
                    "탐험 구역:",
                    "• 고대의 서고 - 잊혀진 지식",
                    "• 보물 창고 - 황금과 보석",
                    "• 숨겨진 방 - 최고의 비밀",
                    "",
                    "목표:",
                    "• 고고학자 헨리와 대화",
                    "• 탐험 도구 준비",
                    "• 유적 입구 발굴",
                    "• 고대의 서고 탐험",
                    "• 보물 창고 약탈",
                    "• 숨겨진 방 발견",
                    "• 고대 유물 획득",
                    "• 유적 탈출",
                    "• 헨리에게 유물 전달",
                    "",
                    "보상:",
                    "• 골드 3000",
                    "• 다이아몬드 25개",
                    "• 고고학 도구 세트",
                    "• 경험치 2000"
            );
        } else {
            return Arrays.asList(
                    "Explore the ancient ruins discovered by Archaeologist Henry.",
                    "Hidden treasures and ancient secrets await you.",
                    "",
                    "⚠️ WARNING: The ruins contain dangerous traps and guardians!",
                    "",
                    "Exploration Areas:",
                    "• Ancient Library - Forgotten Knowledge",
                    "• Treasure Vault - Gold and Gems",
                    "• Hidden Chamber - Ultimate Secret",
                    "",
                    "Objectives:",
                    "• Talk to Archaeologist Henry",
                    "• Prepare exploration tools",
                    "• Excavate ruins entrance",
                    "• Explore the Ancient Library",
                    "• Raid the Treasure Vault",
                    "• Discover the Hidden Chamber",
                    "• Obtain ancient artifact",
                    "• Escape the ruins",
                    "• Return artifact to Henry",
                    "",
                    "Rewards:",
                    "• 3000 Gold",
                    "• 25 Diamonds",
                    "• Archaeology tool set",
                    "• 2000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "archaeologist_talk" -> isKorean ? "고고학자 헨리와 대화" : "Talk to Archaeologist Henry";
            case "gather_tools" -> isKorean ? "철 곡괭이 준비" : "Prepare an Iron Pickaxe";
            case "gather_torches" -> isKorean ? "횃불 64개 준비" : "Prepare 64 Torches";
            case "ruins_entrance" -> isKorean ? "고대 유적 입구 도착" : "Reach Ancient Ruins entrance";
            case "clear_entrance" -> isKorean ? "입구의 돌 30개 제거" : "Clear 30 stones from entrance";
            case "excavate_dirt" -> isKorean ? "흙 50개 발굴" : "Excavate 50 dirt blocks";
            case "library_room" -> isKorean ? "고대의 서고 도달" : "Reach the Ancient Library";
            case "ancient_books" -> isKorean ? "고대 서적 3권 수집" : "Collect 3 Ancient Books";
            case "library_guardians" -> isKorean ? "서고 수호령 10마리 처치" : "Defeat 10 Library Spirits";
            case "treasure_room" -> isKorean ? "보물 창고 도달" : "Reach the Treasure Vault";
            case "break_pots" -> isKorean ? "고대 항아리 10개 파괴" : "Break 10 Ancient Pots";
            case "ancient_coins" -> isKorean ? "고대 동전 30개 수집" : "Collect 30 Ancient Coins";
            case "treasure_guardians" -> isKorean ? "보물 수호자 15마리 처치" : "Defeat 15 Treasure Guardians";
            case "place_lever" -> isKorean ? "레버 설치" : "Place a Lever";
            case "secret_chamber" -> isKorean ? "숨겨진 방 발견" : "Discover the Secret Chamber";
            case "ancient_sentinel" -> isKorean ? "고대 파수꾼 처치" : "Defeat the Ancient Sentinel";
            case "ancient_artifact" -> isKorean ? "고대 유물 획득" : "Obtain Ancient Artifact";
            case "escape_ruins" -> isKorean ? "3분 내에 유적 탈출" : "Escape ruins within 3 minutes";
            case "return_artifact" -> isKorean ? "헨리에게 유물 전달" : "Return artifact to Henry";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("ancient_ruins_dialog");

        dialog.addLine("고고학자 헨리",
                "드디어 찾았어! 전설로만 전해지던 고대 왕국의 유적이야!",
                "I finally found it! The ruins of the ancient kingdom from legends!");

        dialog.addLine("고고학자 헨리",
                "하지만 혼자서는 위험해. 내부에 무엇이 있을지 모르거든.",
                "But it's dangerous alone. We don't know what's inside.");

        dialog.addLine("플레이어",
                "제가 도와드리겠습니다. 무엇을 준비해야 하나요?",
                "I'll help you. What should I prepare?");

        dialog.addLine("고고학자 헨리",
                "곡괭이와 횃불은 필수야. 어둡고 막힌 곳이 많을 거야.",
                "A pickaxe and torches are essential. It'll be dark with many blocked areas.");

        dialog.addLine("고고학자 헨리",
                "그리고 조심해! 고대인들은 도굴꾼을 막기 위해 수호자들을 남겨뒀어.",
                "And be careful! The ancients left guardians to stop tomb raiders.");

        dialog.addLine("플레이어",
                "알겠습니다. 어떤 것을 찾아야 하나요?",
                "Got it. What should I look for?");

        dialog.addLine("고고학자 헨리",
                "뭐든 고대 문명의 흔적이면 좋아. 특히 유물이 있다면 꼭 가져와줘!",
                "Anything from the ancient civilization. Especially if you find artifacts!");

        return dialog;
    }
}