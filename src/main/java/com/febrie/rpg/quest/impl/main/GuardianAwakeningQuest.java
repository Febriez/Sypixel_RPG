package com.febrie.rpg.quest.impl.main;

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
 * 수호자의 각성 - 메인 스토리 퀘스트
 * 고대 수호자를 깨우고 그들의 힘을 얻는 퀘스트
 *
 * @author Febrie
 */
public class GuardianAwakeningQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class GuardianAwakeningBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new GuardianAwakeningQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public GuardianAwakeningQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private GuardianAwakeningQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new GuardianAwakeningBuilder()
                .id(QuestID.MAIN_GUARDIAN_AWAKENING)
                .objectives(Arrays.asList(
                        // 고대 서적 발견
                        new InteractNPCObjective("ancient_scholar", "ancient_scholar"), // 고대 학자
                        new VisitLocationObjective("library_archives", "ancient_library_archives"),
                        new CollectItemObjective("ancient_tome", Material.WRITTEN_BOOK, 3),
                        new CollectItemObjective("guardian_runes", Material.ENCHANTED_BOOK, 5),
                        
                        // 각성의 재료 수집
                        new KillMobObjective("elemental_cores", EntityType.BLAZE, 20),
                        new CollectItemObjective("fire_essence", Material.BLAZE_POWDER, 30),
                        new KillMobObjective("frost_spirits", EntityType.STRAY, 20),
                        new CollectItemObjective("ice_crystals", Material.PACKED_ICE, 20),
                        new BreakBlockObjective("earth_stones", Material.ANCIENT_DEBRIS, 5),
                        new CollectItemObjective("wind_feathers", Material.PHANTOM_MEMBRANE, 10),
                        
                        // 수호자의 신전 찾기
                        new VisitLocationObjective("guardian_temple", "guardian_temple_entrance"),
                        new PlaceBlockObjective("place_runes", Material.CHISELED_STONE_BRICKS, 4),
                        new CollectItemObjective("activate_altar", Material.ENDER_EYE, 1), // 제단 활성화
                        
                        // 시련 통과
                        new KillMobObjective("temple_guardians", EntityType.IRON_GOLEM, 4),
                        new SurviveObjective("elemental_storm", 300), // 5분
                        new CollectItemObjective("guardian_keys", Material.TRIPWIRE_HOOK, 4),
                        
                        // 수호자 각성
                        new VisitLocationObjective("inner_sanctum", "guardian_temple_sanctum"),
                        new InteractNPCObjective("sleeping_guardian", "sleeping_guardian"), // 잠든 수호자
                        new DeliverItemObjective("offer_essences", "sleeping_guardian", Material.NETHER_STAR, 1),
                        
                        // 수호자의 시험
                        new KillMobObjective("guardian_avatar", EntityType.ELDER_GUARDIAN, 1),
                        // 수호자 선택은 NPC 대화로 처리
                        
                        // 수호자의 축복 받기
                        new InteractNPCObjective("awakened_guardian", "awakened_guardian"),
                        new CollectItemObjective("guardian_blessing", Material.BEACON, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 10000)
                        .addCurrency(CurrencyType.DIAMOND, 75)
                        .addItem(new ItemStack(Material.BEACON))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING))
                        .addItem(new ItemStack(Material.NETHERITE_CHESTPLATE))
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_ELEMENTAL_STONES)
                .minLevel(30)
                .maxLevel(0);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "수호자의 각성" : "Guardian's Awakening";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "고대의 수호자들이 깊은 잠에 빠져있습니다.",
                    "그들을 깨우고 시련을 통과하여 수호자의 힘을 얻으세요.",
                    "",
                    "⚔️ 주요 스토리 퀘스트",
                    "이 퀘스트는 당신의 운명을 결정할 중요한 선택을 포함합니다.",
                    "",
                    "수호자의 종류:",
                    "• 빛의 수호자 - 치유와 보호의 힘",
                    "• 어둠의 수호자 - 파괴와 흡수의 힘",
                    "• 균형의 수호자 - 조화와 변환의 힘",
                    "",
                    "목표:",
                    "• 고대 학자와 대화",
                    "• 고대 서적과 룬 수집",
                    "• 원소의 정수 수집",
                    "• 수호자의 신전 찾기",
                    "• 신전의 시련 통과",
                    "• 수호자 각성시키기",
                    "• 수호자의 아바타와 전투",
                    "• 수호자 선택",
                    "• 수호자의 축복 받기",
                    "",
                    "보상:",
                    "• 골드 10,000",
                    "• 다이아몬드 75개",
                    "• 신호기 (수호자의 빛)",
                    "• 불사의 토템",
                    "• 네더라이트 흉갑",
                    "• 경험치 5,000"
            );
        } else {
            return Arrays.asList(
                    "The ancient guardians lie in deep slumber.",
                    "Awaken them and pass their trials to gain the guardian's power.",
                    "",
                    "⚔️ Main Story Quest",
                    "This quest includes an important choice that will determine your destiny.",
                    "",
                    "Guardian Types:",
                    "• Guardian of Light - Power of healing and protection",
                    "• Guardian of Darkness - Power of destruction and absorption",
                    "• Guardian of Balance - Power of harmony and transformation",
                    "",
                    "Objectives:",
                    "• Talk to the Ancient Scholar",
                    "• Collect ancient tomes and runes",
                    "• Gather elemental essences",
                    "• Find the Guardian Temple",
                    "• Pass the temple trials",
                    "• Awaken the guardian",
                    "• Battle the guardian avatar",
                    "• Choose your guardian",
                    "• Receive guardian's blessing",
                    "",
                    "Rewards:",
                    "• 10,000 Gold",
                    "• 75 Diamonds",
                    "• Beacon (Guardian's Light)",
                    "• Totem of Undying",
                    "• Netherite Chestplate",
                    "• 5,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "ancient_scholar" -> isKorean ? "고대 학자와 대화" : "Talk to the Ancient Scholar";
            case "library_archives" -> isKorean ? "고대 도서관 서고 방문" : "Visit Ancient Library Archives";
            case "ancient_tome" -> isKorean ? "고대 서적 3권 수집" : "Collect 3 Ancient Tomes";
            case "guardian_runes" -> isKorean ? "수호자 룬 5개 수집" : "Collect 5 Guardian Runes";
            case "elemental_cores" -> isKorean ? "블레이즈 20마리 처치 (화염 정수)" : "Kill 20 Blazes (Fire Essence)";
            case "fire_essence" -> isKorean ? "화염의 정수 30개 수집" : "Collect 30 Fire Essence";
            case "frost_spirits" -> isKorean ? "스트레이 20마리 처치 (서리 정령)" : "Kill 20 Strays (Frost Spirits)";
            case "ice_crystals" -> isKorean ? "얼음 결정 20개 수집" : "Collect 20 Ice Crystals";
            case "earth_stones" -> isKorean ? "고대 잔해 5개 채굴 (대지의 돌)" : "Mine 5 Ancient Debris (Earth Stones)";
            case "wind_feathers" -> isKorean ? "바람의 깃털 10개 수집" : "Collect 10 Wind Feathers";
            case "guardian_temple" -> isKorean ? "수호자의 신전 입구 도달" : "Reach Guardian Temple Entrance";
            case "place_runes" -> isKorean ? "조각된 석재 4개 배치 (룬 활성화)" : "Place 4 Chiseled Stone (Activate Runes)";
            case "activate_altar" -> isKorean ? "엔더의 눈으로 제단 활성화" : "Activate altar with Ender Eye";
            case "temple_guardians" -> isKorean ? "신전 수호자 4마리 처치" : "Defeat 4 Temple Guardians";
            case "elemental_storm" -> isKorean ? "원소의 폭풍 5분간 생존" : "Survive Elemental Storm for 5 minutes";
            case "guardian_keys" -> isKorean ? "수호자의 열쇠 4개 수집" : "Collect 4 Guardian Keys";
            case "inner_sanctum" -> isKorean ? "내부 성소 도달" : "Reach Inner Sanctum";
            case "sleeping_guardian" -> isKorean ? "잠든 수호자와 대화" : "Talk to the Sleeping Guardian";
            case "offer_essences" -> isKorean ? "네더의 별 바치기" : "Offer Nether Star";
            case "guardian_avatar" -> isKorean ? "수호자의 아바타 처치" : "Defeat Guardian Avatar";
            case "guardian_choice" -> isKorean ? "수호자 선택" : "Choose Your Guardian";
            case "awakened_guardian" -> isKorean ? "각성한 수호자와 대화" : "Talk to Awakened Guardian";
            case "guardian_blessing" -> isKorean ? "수호자의 축복 받기" : "Receive Guardian's Blessing";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("guardian_awakening_dialog");

        // 시작 대화
        dialog.addLine("고대 학자",
                "드디어 때가 왔군요. 고대의 예언이 말하는 그 시간이...",
                "Finally, the time has come. The time spoken of in the ancient prophecy...");

        dialog.addLine("고대 학자",
                "수호자들이 잠들어 있는 동안 세상은 균형을 잃어가고 있습니다.",
                "While the guardians slumber, the world is losing its balance.");

        dialog.addLine("플레이어",
                "수호자들을 깨울 수 있나요?",
                "Can we awaken the guardians?");

        dialog.addLine("고대 학자",
                "가능합니다. 하지만 그들을 깨우려면 네 가지 원소의 정수가 필요합니다.",
                "It's possible. But to awaken them, we need the essence of four elements.");

        // 중간 대화 - 잠든 수호자
        dialog.addLine("잠든 수호자",
                "누가... 나의 잠을... 깨우는가...",
                "Who... disturbs... my slumber...");

        dialog.addLine("잠든 수호자",
                "아... 예언의 아이로구나. 하지만 나의 힘을 원한다면 증명해야 한다.",
                "Ah... the child of prophecy. But if you seek my power, you must prove yourself.");

        // 선택 후 대화
        dialog.addLine("각성한 수호자",
                "너의 선택을 존중한다. 이제 너는 수호자의 축복을 받을 자격이 있다.",
                "I respect your choice. Now you are worthy of the guardian's blessing.");

        dialog.addLine("각성한 수호자",
                "이 힘을 현명하게 사용하거라. 세상의 운명이 너의 손에 달려있다.",
                "Use this power wisely. The fate of the world rests in your hands.");

        return dialog;
    }
}