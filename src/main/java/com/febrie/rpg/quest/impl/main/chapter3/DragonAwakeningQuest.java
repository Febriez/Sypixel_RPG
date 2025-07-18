package com.febrie.rpg.quest.impl.main.chapter3;

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
 * 용의 각성 - 메인 스토리 퀘스트 (Chapter 3)
 * 고대 용을 깨우고 동맹을 맺는 퀘스트
 *
 * @author Febrie
 */
public class DragonAwakeningQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class DragonAwakeningBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new DragonAwakeningQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DragonAwakeningQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DragonAwakeningQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new DragonAwakeningBuilder()
                .id(QuestID.MAIN_DRAGON_AWAKENING)
                .objectives(Arrays.asList(
                        // 전설 조사
                        new InteractNPCObjective("dragon_sage", 124), // 용의 현자
                        new CollectItemObjective("ancient_scrolls", Material.WRITTEN_BOOK, 5),
                        new VisitLocationObjective("ancient_library", "dragon_library"),
                        new CollectItemObjective("dragon_lore", Material.ENCHANTED_BOOK, 3),
                        
                        // 용의 신전 찾기
                        new VisitLocationObjective("mountain_peak", "dragon_mountain_peak"),
                        new BreakBlockObjective("clear_path", Material.STONE, 50),
                        new KillMobObjective("mountain_guardians", EntityType.IRON_GOLEM, 5),
                        new VisitLocationObjective("dragon_shrine", "ancient_dragon_shrine"),
                        
                        // 각성 의식 준비
                        new CollectItemObjective("dragon_tears", Material.GHAST_TEAR, 3),
                        new CollectItemObjective("ancient_gold", Material.GOLD_BLOCK, 10),
                        new CollectItemObjective("emerald_offering", Material.EMERALD_BLOCK, 5),
                        new CollectItemObjective("dragon_breath", Material.DRAGON_BREATH, 3),
                        
                        // 각성 의식
                        new PlaceBlockObjective("place_gold", Material.GOLD_BLOCK, 10),
                        new PlaceBlockObjective("place_emerald", Material.EMERALD_BLOCK, 5),
                        new CollectItemObjective("ritual_catalyst", Material.END_CRYSTAL, 4),
                        new PlaceBlockObjective("place_crystals", Material.END_CRYSTAL, 4),
                        new SurviveObjective("ritual_duration", 600), // 10분
                        
                        // 용의 시험
                        new KillMobObjective("flame_dragons", EntityType.BLAZE, 20),
                        new KillMobObjective("dragon_priests", EntityType.EVOKER, 10),
                        new CollectItemObjective("dragon_scales", Material.PRISMARINE_SHARD, 20),
                        new CollectItemObjective("dragon_bones", Material.BONE, 50),
                        
                        // 용의 둥지 진입
                        new VisitLocationObjective("dragon_lair_entrance", "dragon_lair_entrance"),
                        new KillMobObjective("lair_guardians", EntityType.ELDER_GUARDIAN, 3),
                        new CollectItemObjective("lair_key", Material.HEART_OF_THE_SEA, 1),
                        new VisitLocationObjective("inner_lair", "dragon_inner_lair"),
                        
                        // 용과의 만남
                        new InteractNPCObjective("sleeping_dragon", 125), // 잠든 고대 용
                        new CollectItemObjective("dragon_egg", Material.DRAGON_EGG, 1),
                        new DeliverItemObjective("offer_treasures", "sleeping_dragon", Material.DIAMOND_BLOCK, 10),
                        
                        // 용과의 결투
                        new KillMobObjective("dragon_test", EntityType.ENDER_DRAGON, 1),
                        
                        // 동맹 체결
                        new InteractNPCObjective("dragon_pact", 125),
                        new CollectItemObjective("dragon_heart", Material.NETHER_STAR, 1),
                        new CollectItemObjective("pact_scroll", Material.WRITTEN_BOOK, 1),
                        new DeliverItemObjective("complete_pact", "dragon_sage", Material.WRITTEN_BOOK, 1)
                ))
                .reward(BasicReward.builder()
                        .addCurrency(CurrencyType.GOLD, 20000)
                        .addCurrency(CurrencyType.DIAMOND, 150)
                        .addItem(new ItemStack(Material.ELYTRA)) // 용의 날개
                        .addItem(new ItemStack(Material.DRAGON_HEAD))
                        .addItem(new ItemStack(Material.DRAGON_BREATH, 10))
                        .addItem(new ItemStack(Material.END_CRYSTAL, 4))
                        .addItem(new ItemStack(Material.SHULKER_BOX, 3))
                        .addExperience(10000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .prerequisiteQuests(Arrays.asList(QuestID.MAIN_CORRUPTED_LANDS))
                .minLevel(45)
                .maxLevel(0);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "용의 귀환" : "Return of the Dragon";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "전설 속에만 존재하던 고대 용이 깨어나고 있습니다.",
                    "용과의 동맹을 맺고 다가올 대재앙에 대비하세요.",
                    "",
                    "🐉 주요 스토리 퀘스트 - Chapter 3",
                    "어둠의 세력을 물리친 후, 더 큰 위협이 다가오고 있습니다.",
                    "오직 고대 용의 힘만이 희망이 될 수 있습니다.",
                    "",
                    "주요 장소:",
                    "• 용의 도서관",
                    "• 고대 용의 신전",
                    "• 용의 둥지",
                    "",
                    "퀘스트 단계:",
                    "• 1단계: 전설 조사",
                    "• 2단계: 신전 발견",
                    "• 3단계: 각성 의식",
                    "• 4단계: 용의 시험",
                    "• 5단계: 용과의 대결",
                    "• 6단계: 동맹 체결",
                    "",
                    "목표:",
                    "• 용의 현자와 만남",
                    "• 고대 문헌 조사",
                    "• 용의 신전 발견",
                    "• 각성 의식 준비",
                    "• 용의 시험 통과",
                    "• 용과의 결투",
                    "• 동맹 협정 체결",
                    "",
                    "보상:",
                    "• 골드 20,000",
                    "• 다이아몬드 150개",
                    "• 겉날개 (용의 날개)",
                    "• 용의 머리",
                    "• 용의 숨결 10개",
                    "• 엔드 수정 4개",
                    "• 셜커 상자 3개",
                    "• 경험치 10,000"
            );
        } else {
            return Arrays.asList(
                    "The ancient dragon that existed only in legends is awakening.",
                    "Form an alliance with the dragon and prepare for the coming catastrophe.",
                    "",
                    "🐉 Main Story Quest - Chapter 3",
                    "After repelling the dark forces, an even greater threat approaches.",
                    "Only the power of the ancient dragon can be our hope.",
                    "",
                    "Key Locations:",
                    "• Dragon Library",
                    "• Ancient Dragon Shrine",
                    "• Dragon's Lair",
                    "",
                    "Quest Stages:",
                    "• Stage 1: Legend Investigation",
                    "• Stage 2: Shrine Discovery",
                    "• Stage 3: Awakening Ritual",
                    "• Stage 4: Dragon's Trial",
                    "• Stage 5: Battle with Dragon",
                    "• Stage 6: Alliance Formation",
                    "",
                    "Objectives:",
                    "• Meet the Dragon Sage",
                    "• Research ancient texts",
                    "• Discover Dragon Shrine",
                    "• Prepare awakening ritual",
                    "• Pass dragon's trial",
                    "• Battle the dragon",
                    "• Form alliance pact",
                    "",
                    "Rewards:",
                    "• 20,000 Gold",
                    "• 150 Diamonds",
                    "• Elytra (Dragon Wings)",
                    "• Dragon Head",
                    "• 10 Dragon's Breath",
                    "• 4 End Crystals",
                    "• 3 Shulker Boxes",
                    "• 10,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "dragon_sage" -> isKorean ? "용의 현자와 대화" : "Talk to the Dragon Sage";
            case "ancient_scrolls" -> isKorean ? "고대 두루마리 5개 수집" : "Collect 5 ancient scrolls";
            case "ancient_library" -> isKorean ? "용의 도서관 방문" : "Visit Dragon Library";
            case "dragon_lore" -> isKorean ? "용의 전설서 3권 수집" : "Collect 3 Dragon Lore books";
            case "mountain_peak" -> isKorean ? "용의 산 정상 도달" : "Reach Dragon Mountain peak";
            case "clear_path" -> isKorean ? "길 개척 (돌 50개 파괴)" : "Clear path (break 50 stones)";
            case "mountain_guardians" -> isKorean ? "산의 수호자 5마리 처치" : "Defeat 5 Mountain Guardians";
            case "dragon_shrine" -> isKorean ? "고대 용의 신전 도달" : "Reach Ancient Dragon Shrine";
            case "dragon_tears" -> isKorean ? "용의 눈물 3개 수집" : "Collect 3 Dragon Tears";
            case "ancient_gold" -> isKorean ? "고대 금괴 10개 수집" : "Collect 10 Ancient Gold Blocks";
            case "emerald_offering" -> isKorean ? "에메랄드 제물 5개 수집" : "Collect 5 Emerald Offerings";
            case "dragon_breath" -> isKorean ? "용의 숨결 3개 수집" : "Collect 3 Dragon's Breath";
            case "place_gold" -> isKorean ? "금 블록 10개 배치" : "Place 10 Gold Blocks";
            case "place_emerald" -> isKorean ? "에메랄드 블록 5개 배치" : "Place 5 Emerald Blocks";
            case "ritual_catalyst" -> isKorean ? "의식 촉매 4개 수집" : "Collect 4 Ritual Catalysts";
            case "place_crystals" -> isKorean ? "엔드 수정 4개 설치" : "Place 4 End Crystals";
            case "ritual_duration" -> isKorean ? "10분간 의식 수행" : "Perform ritual for 10 minutes";
            case "flame_dragons" -> isKorean ? "화염 용족 20마리 처치" : "Defeat 20 Flame Dragons";
            case "dragon_priests" -> isKorean ? "용의 사제 10명 처치" : "Defeat 10 Dragon Priests";
            case "dragon_scales" -> isKorean ? "용의 비늘 20개 수집" : "Collect 20 Dragon Scales";
            case "dragon_bones" -> isKorean ? "용의 뼈 50개 수집" : "Collect 50 Dragon Bones";
            case "dragon_lair_entrance" -> isKorean ? "용의 둥지 입구 도달" : "Reach Dragon Lair entrance";
            case "lair_guardians" -> isKorean ? "둥지 수호자 3마리 처치" : "Defeat 3 Lair Guardians";
            case "lair_key" -> isKorean ? "둥지의 열쇠 획득" : "Obtain Lair Key";
            case "inner_lair" -> isKorean ? "용의 둥지 내부 진입" : "Enter Inner Dragon Lair";
            case "sleeping_dragon" -> isKorean ? "잠든 고대 용과 대화" : "Talk to Sleeping Ancient Dragon";
            case "dragon_egg" -> isKorean ? "용의 알 획득" : "Obtain Dragon Egg";
            case "offer_treasures" -> isKorean ? "보물 바치기" : "Offer treasures";
            case "dragon_test" -> isKorean ? "용과의 결투에서 승리" : "Win duel against dragon";
            case "dragon_pact" -> isKorean ? "용과 동맹 협정" : "Form pact with dragon";
            case "dragon_heart" -> isKorean ? "용의 심장 획득" : "Obtain Dragon Heart";
            case "pact_scroll" -> isKorean ? "동맹 계약서 획득" : "Obtain Pact Scroll";
            case "complete_pact" -> isKorean ? "계약서 전달" : "Deliver pact scroll";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("dragon_awakening_dialog");

        // 시작
        dialog.addLine("용의 현자",
                "때가 왔군요. 고대의 예언이 현실이 되고 있습니다.",
                "The time has come. The ancient prophecy is becoming reality.");

        dialog.addLine("용의 현자",
                "어둠의 세력은 시작에 불과했습니다. 진정한 재앙이 다가오고 있죠.",
                "The dark forces were just the beginning. The true catastrophe approaches.");

        dialog.addLine("플레이어",
                "무엇을 해야 하나요?",
                "What must I do?");

        dialog.addLine("용의 현자",
                "고대 용을 깨워야 합니다. 오직 그들의 힘만이 우리를 구할 수 있습니다.",
                "We must awaken the ancient dragon. Only their power can save us.");

        // 신전 발견
        dialog.addLine("용의 현자",
                "신전을 찾았군요! 이제 각성 의식을 준비해야 합니다.",
                "You found the shrine! Now we must prepare the awakening ritual.");

        dialog.addLine("용의 현자",
                "용의 눈물, 고대의 금, 그리고 순수한 에메랄드가 필요합니다.",
                "We need dragon tears, ancient gold, and pure emeralds.");

        // 용과의 대면
        dialog.addLine("잠든 고대 용",
                "누가... 천년의 잠을... 방해하는가...",
                "Who... disturbs... my thousand year slumber...");

        dialog.addLine("잠든 고대 용",
                "아... 예언의 용사로군. 하지만 내 힘을 원한다면... 증명하라!",
                "Ah... the prophesied warrior. But if you seek my power... prove yourself!");

        dialog.addLine("플레이어",
                "어떻게 증명해야 하나요?",
                "How must I prove myself?");

        dialog.addLine("잠든 고대 용",
                "나와 싸워라! 네가 진정한 용사인지 시험하겠다!",
                "Fight me! I shall test if you are a true warrior!");

        // 동맹 체결
        dialog.addLine("고대 용",
                "훌륭하다... 너는 진정한 용사다. 내 힘을 빌려주겠다.",
                "Excellent... you are a true warrior. I shall lend you my power.");

        dialog.addLine("고대 용",
                "이 계약으로 우리는 하나가 된다. 다가올 재앙을 함께 막아내자.",
                "With this pact, we become one. Let us face the coming catastrophe together.");

        return dialog;
    }
}