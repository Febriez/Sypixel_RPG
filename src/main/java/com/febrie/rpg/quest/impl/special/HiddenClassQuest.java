package com.febrie.rpg.quest.impl.special;

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
 * 숨겨진 직업 - 특수 퀘스트
 * 비밀스러운 방법으로만 얻을 수 있는 특별한 직업 전직 퀘스트
 *
 * @author Febrie
 */
public class HiddenClassQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class HiddenClassBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new HiddenClassQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public HiddenClassQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private HiddenClassQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new HiddenClassBuilder()
                .id(QuestID.SPECIAL_HIDDEN_CLASS)
                .objectives(Arrays.asList(
                        // 숨겨진 단서 발견
                        new CollectItemObjective("mysterious_letter", Material.PAPER, 1),
                        new VisitLocationObjective("secret_location", "shadow_guild_entrance"),
                        new CollectItemObjective("use_secret_knock", Material.STICK, 1), // 비밀 노크용 막대기
                        
                        // 그림자 길드 입단 시험
                        new InteractNPCObjective("shadow_master", 114), // 그림자 마스터
                        new SurviveObjective("darkness_test", 600), // 10분간 어둠 속 생존
                        new KillPlayerObjective("pvp_test", 1), // PvP 시험
                        
                        // 첫 번째 시련 - 은신술
                        new VisitLocationObjective("stealth_course", "shadow_training_ground"),
                        new SurviveObjective("stealth_test", 300), // 5분간 은신 생존
                        new CollectItemObjective("shadow_essence", Material.BLACK_DYE, 10),
                        new KillMobObjective("silent_kills", EntityType.ZOMBIE, 20), // 소리없이 처치
                        
                        // 두 번째 시련 - 독술
                        new CollectItemObjective("poison_ingredients", Material.SPIDER_EYE, 15),
                        new CollectItemObjective("fermented_eyes", Material.FERMENTED_SPIDER_EYE, 10),
                        new CraftItemObjective("craft_poisons", Material.POTION, 5),
                        new KillMobObjective("poison_test", EntityType.CAVE_SPIDER, 30),
                        
                        // 세 번째 시련 - 정보 수집
                        new InteractNPCObjective("spy_merchant", 115), // 정보상
                        new PayCurrencyObjective("bribe_informant", CurrencyType.GOLD, 2000),
                        new CollectItemObjective("secret_documents", Material.WRITTEN_BOOK, 3),
                        new DeliverItemObjective("deliver_intel", "shadow_master", Material.WRITTEN_BOOK, 3),
                        
                        // 네 번째 시련 - 암살 임무
                        new VisitLocationObjective("target_location", "noble_mansion"),
                        new KillMobObjective("corrupt_noble", EntityType.VINDICATOR, 1),
                        new CollectItemObjective("noble_seal", Material.GOLD_NUGGET, 1),
                        new SurviveObjective("escape_guards", 180), // 3분간 경비 회피
                        
                        // 최종 시련 - 그림자와의 계약
                        new VisitLocationObjective("shadow_sanctum", "shadow_guild_sanctum"),
                        new CollectItemObjective("shadow_materials", Material.OBSIDIAN, 20),
                        new PlaceBlockObjective("shadow_altar", Material.OBSIDIAN, 9),
                        new CollectItemObjective("shadow_sacrifice", Material.ENDER_PEARL, 10), // 희생물
                        new KillMobObjective("shadow_guardian", EntityType.ENDERMAN, 1),
                        
                        // 전직 의식
                        // 전직 선택은 NPC 대화로 처리
                        new InteractNPCObjective("final_ceremony", 114),
                        new CollectItemObjective("shadow_mark", Material.PLAYER_HEAD, 1) // 그림자의 표식
                ))
                .reward(BasicReward.builder()
                        .addCurrency(CurrencyType.GOLD, 7500)
                        .addCurrency(CurrencyType.DIAMOND, 50)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD)) // 그림자의 검
                        .addItem(new ItemStack(Material.LEATHER_CHESTPLATE)) // 은신 갑옷
                        .addItem(new ItemStack(Material.POTION, 10)) // 특수 포션
                        .addItem(new ItemStack(Material.ENDER_PEARL, 16))
                        .addExperience(7500)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.NORMAL)
                .minLevel(35)
                .maxLevel(0);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "그림자의 길" : "Path of Shadows";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "어둠 속에서만 볼 수 있는 길이 있습니다.",
                    "그림자 길드의 비밀스러운 시련을 통과하고 숨겨진 직업을 얻으세요.",
                    "",
                    "⚠️ 경고: 이 퀘스트는 매우 위험하며 PvP가 포함됩니다.",
                    "한 번 시작하면 돌이킬 수 없습니다.",
                    "",
                    "시련의 단계:",
                    "• 은신술 - 그림자가 되는 법",
                    "• 독술 - 조용한 죽음의 기술",
                    "• 정보술 - 비밀을 캐내는 방법",
                    "• 암살술 - 완벽한 처단",
                    "• 그림자 계약 - 영원한 맹세",
                    "",
                    "선택 가능한 숨겨진 직업:",
                    "• 그림자 암살자 - 일격필살의 달인",
                    "• 어둠의 마법사 - 금지된 마법 사용자",
                    "• 밤의 추적자 - 추적과 정찰의 전문가",
                    "",
                    "목표:",
                    "• 신비한 편지 획득",
                    "• 그림자 길드 찾기",
                    "• 5가지 시련 통과",
                    "• 그림자와의 계약",
                    "• 숨겨진 직업 선택",
                    "",
                    "보상:",
                    "• 골드 7,500",
                    "• 다이아몬드 50개",
                    "• 그림자의 검 (네더라이트)",
                    "• 은신 갑옷",
                    "• 특수 포션 10개",
                    "• 엔더 진주 16개",
                    "• 경험치 7,500"
            );
        } else {
            return Arrays.asList(
                    "There is a path that can only be seen in darkness.",
                    "Pass the secret trials of the Shadow Guild and obtain a hidden class.",
                    "",
                    "⚠️ WARNING: This quest is very dangerous and includes PvP.",
                    "Once started, there's no turning back.",
                    "",
                    "Trial Stages:",
                    "• Stealth - Becoming one with shadows",
                    "• Poison - The art of silent death",
                    "• Intelligence - Methods of uncovering secrets",
                    "• Assassination - Perfect elimination",
                    "• Shadow Contract - Eternal oath",
                    "",
                    "Available Hidden Classes:",
                    "• Shadow Assassin - Master of one-hit kills",
                    "• Dark Sorcerer - User of forbidden magic",
                    "• Night Stalker - Expert in tracking and scouting",
                    "",
                    "Objectives:",
                    "• Obtain mysterious letter",
                    "• Find the Shadow Guild",
                    "• Pass 5 trials",
                    "• Contract with shadows",
                    "• Choose hidden class",
                    "",
                    "Rewards:",
                    "• 7,500 Gold",
                    "• 50 Diamonds",
                    "• Shadow Blade (Netherite)",
                    "• Stealth Armor",
                    "• 10 Special Potions",
                    "• 16 Ender Pearls",
                    "• 7,500 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "mysterious_letter" -> isKorean ? "신비한 편지 획득" : "Obtain mysterious letter";
            case "secret_location" -> isKorean ? "비밀 장소 방문" : "Visit secret location";
            case "use_secret_knock" -> isKorean ? "비밀 노크 사용" : "Use secret knock";
            case "shadow_master" -> isKorean ? "그림자 마스터와 대화" : "Talk to Shadow Master";
            case "darkness_test" -> isKorean ? "10분간 어둠 속 생존" : "Survive in darkness for 10 minutes";
            case "pvp_test" -> isKorean ? "다른 플레이어 1명 처치" : "Defeat 1 player";
            case "stealth_course" -> isKorean ? "은신 훈련장 도착" : "Reach stealth training ground";
            case "stealth_test" -> isKorean ? "5분간 발각되지 않기" : "Remain undetected for 5 minutes";
            case "shadow_essence" -> isKorean ? "그림자 정수 10개 수집" : "Collect 10 Shadow Essence";
            case "silent_kills" -> isKorean ? "좀비 20마리 조용히 처치" : "Silently kill 20 zombies";
            case "poison_ingredients" -> isKorean ? "거미 눈 15개 수집" : "Collect 15 Spider Eyes";
            case "fermented_eyes" -> isKorean ? "발효된 거미 눈 10개 수집" : "Collect 10 Fermented Spider Eyes";
            case "craft_poisons" -> isKorean ? "독 포션 5개 제작" : "Craft 5 Poison Potions";
            case "poison_test" -> isKorean ? "동굴 거미 30마리 처치" : "Kill 30 Cave Spiders";
            case "spy_merchant" -> isKorean ? "정보상과 대화" : "Talk to Information Broker";
            case "bribe_informant" -> isKorean ? "정보원에게 2000골드 뇌물" : "Bribe informant with 2000 gold";
            case "secret_documents" -> isKorean ? "비밀 문서 3개 수집" : "Collect 3 Secret Documents";
            case "deliver_intel" -> isKorean ? "정보 전달" : "Deliver intelligence";
            case "target_location" -> isKorean ? "표적 위치 도달" : "Reach target location";
            case "corrupt_noble" -> isKorean ? "타락한 귀족 처치" : "Eliminate corrupt noble";
            case "noble_seal" -> isKorean ? "귀족의 인장 획득" : "Obtain noble's seal";
            case "escape_guards" -> isKorean ? "3분 내 경비 탈출" : "Escape guards within 3 minutes";
            case "shadow_sanctum" -> isKorean ? "그림자 성소 도달" : "Reach Shadow Sanctum";
            case "shadow_materials" -> isKorean ? "흑요석 20개 수집" : "Collect 20 Obsidian";
            case "shadow_altar" -> isKorean ? "그림자 제단 설치" : "Build Shadow Altar";
            case "shadow_sacrifice" -> isKorean ? "엔더 진주 10개 희생" : "Sacrifice 10 Ender Pearls";
            case "shadow_guardian" -> isKorean ? "그림자 수호자 처치" : "Defeat Shadow Guardian";
            case "shadow_path" -> isKorean ? "그림자의 길 선택" : "Choose your shadow path";
            case "final_ceremony" -> isKorean ? "최종 의식" : "Final ceremony";
            case "shadow_mark" -> isKorean ? "그림자의 표식 받기" : "Receive Shadow Mark";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("hidden_class_dialog");

        // 시작
        dialog.addLine("???",
                "이 편지를 읽고 있다면, 당신은 선택받은 자입니다...",
                "If you're reading this letter, you are chosen...");

        dialog.addLine("???",
                "자정에 버려진 우물 앞에서 막대기로 세 번 두드리시오.",
                "At midnight, knock three times with a stick at the abandoned well.");

        // 그림자 마스터
        dialog.addLine("그림자 마스터",
                "오랜만에 가능성 있는 자가 왔군. 그림자의 길을 걸을 준비가 되었나?",
                "It's been long since someone with potential arrived. Are you ready to walk the path of shadows?");

        dialog.addLine("그림자 마스터",
                "이 길은 돌이킬 수 없네. 한번 발을 들이면 끝까지 가야 해.",
                "This path cannot be undone. Once you step in, you must go to the end.");

        dialog.addLine("플레이어",
                "준비되었습니다.",
                "I am ready.");

        dialog.addLine("그림자 마스터",
                "좋아. 그럼 첫 번째 시련부터 시작하지. 진정한 그림자가 되는 법을 배워라.",
                "Good. Let's start with the first trial. Learn to become a true shadow.");

        // 중간 대화
        dialog.addLine("그림자 마스터",
                "인상적이군. 하지만 이제부터가 진짜 시험이야.",
                "Impressive. But the real test begins now.");

        dialog.addLine("그림자 마스터",
                "타락한 귀족을 제거하라. 증거로 그의 인장을 가져와야 한다.",
                "Eliminate the corrupt noble. Bring his seal as proof.");

        // 최종 선택
        dialog.addLine("그림자 마스터",
                "모든 시련을 통과했다. 이제 너의 길을 선택할 때다.",
                "You've passed all trials. Now it's time to choose your path.");

        dialog.addLine("그림자 마스터",
                "그림자 암살자, 어둠의 마법사, 밤의 추적자... 무엇이 되겠는가?",
                "Shadow Assassin, Dark Sorcerer, Night Stalker... What will you become?");

        // 완료
        dialog.addLine("그림자 마스터",
                "이제 너는 우리 중 하나다. 그림자의 표식을 받아라.",
                "Now you are one of us. Receive the Shadow Mark.");

        dialog.addLine("그림자 마스터",
                "이 표식은 너의 새로운 정체성이다. 현명하게 사용하거라.",
                "This mark is your new identity. Use it wisely.");

        return dialog;
    }
}