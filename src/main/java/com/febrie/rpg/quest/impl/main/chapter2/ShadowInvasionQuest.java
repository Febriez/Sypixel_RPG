package com.febrie.rpg.quest.impl.main.chapter2;

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
 * 그림자의 침략 - 메인 스토리 퀘스트 (Chapter 2)
 * 어둠의 세력이 차원의 틈을 통해 침략하는 퀘스트
 *
 * @author Febrie
 */
public class ShadowInvasionQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class ShadowInvasionBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new ShadowInvasionQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public ShadowInvasionQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private ShadowInvasionQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new ShadowInvasionBuilder()
                .id(QuestID.MAIN_SHADOW_INVASION)
                .objectives(Arrays.asList(
                        // 침략 발견
                        new InteractNPCObjective("scout_report", "shadow_scout"), // 정찰병
                        new VisitLocationObjective("shadow_portal", "shadow_breach"),
                        new CollectItemObjective("portal_sample", Material.OBSIDIAN, 5),
                        
                        // 초반 전투
                        new KillMobObjective("shadow_scouts", EntityType.ENDERMAN, 15),
                        new KillMobObjective("shadow_warriors", EntityType.WITHER_SKELETON, 20),
                        new CollectItemObjective("shadow_essence", Material.ENDER_PEARL, 20),
                        new CollectItemObjective("dark_fragments", Material.COAL, 30),
                        
                        // 방어 준비
                        new InteractNPCObjective("commander_talk", "defense_commander"), // 방어 사령관
                        new CollectItemObjective("defense_materials", Material.IRON_INGOT, 50),
                        new PlaceBlockObjective("build_walls", Material.IRON_BARS, 30),
                        new PlaceBlockObjective("build_towers", Material.STONE_BRICKS, 20),
                        new CraftItemObjective("craft_arrows", Material.ARROW, 128),
                        
                        // 대규모 방어전
                        new VisitLocationObjective("defense_position", "castle_walls"),
                        new KillMobObjective("defend_wave1", EntityType.ZOMBIE, 40),
                        new KillMobObjective("defend_wave2", EntityType.SKELETON, 35),
                        new KillMobObjective("defend_wave3", EntityType.SPIDER, 30),
                        new SurviveObjective("hold_position", 600), // 10분간 방어
                        
                        // 엘리트 적 등장
                        new KillMobObjective("shadow_captains", EntityType.VINDICATOR, 5),
                        new KillMobObjective("shadow_mages", EntityType.EVOKER, 5),
                        new CollectItemObjective("captain_badges", Material.IRON_NUGGET, 5),
                        
                        // 그림자 장군과의 대결
                        new VisitLocationObjective("shadow_throne", "shadow_general_arena"),
                        new InteractNPCObjective("shadow_general_talk", "shadow_general"), // 그림자 장군
                        new KillMobObjective("shadow_general", EntityType.WITHER, 1),
                        new CollectItemObjective("general_crown", Material.WITHER_SKELETON_SKULL, 1),
                        
                        // 포탈 파괴
                        new CollectItemObjective("portal_keys", Material.ENDER_EYE, 3),
                        new VisitLocationObjective("return_portal", "shadow_breach"),
                        new PlaceBlockObjective("seal_portal", Material.END_STONE, 9),
                        new CollectItemObjective("sealed_core", Material.NETHER_STAR, 1),
                        
                        // 전리품 보고
                        new DeliverItemObjective("deliver_crown", "commander", Material.WITHER_SKELETON_SKULL, 1),
                        new DeliverItemObjective("deliver_core", "commander", Material.NETHER_STAR, 1),
                        new InteractNPCObjective("victory_ceremony", "defense_commander")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 12000)
                        .addCurrency(CurrencyType.DIAMOND, 80)
                        .addItem(new ItemStack(Material.NETHERITE_HELMET))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING))
                        .addItem(new ItemStack(Material.ENDER_CHEST))
                        .addItem(new ItemStack(Material.BEACON))
                        .addExperience(6000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_GUARDIAN_AWAKENING)
                .minLevel(35)
                .maxLevel(0);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "그림자의 침략" : "The Shadow Invasion";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "어둠의 세력이 차원의 틈을 통해 침략을 시작했습니다.",
                    "왕국을 지키고 그림자 군단을 물리치세요!",
                    "",
                    "⚔️ 주요 스토리 퀘스트 - Chapter 2",
                    "수호자의 각성 이후, 어둠의 세력이 본격적으로 움직이기 시작했습니다.",
                    "",
                    "장소:",
                    "• 그림자 차원문",
                    "• 왕성 방어선",
                    "• 그림자 장군의 왕좌",
                    "",
                    "전투 단계:",
                    "• 1단계: 정찰과 초반 전투",
                    "• 2단계: 방어 준비",
                    "• 3단계: 대규모 방어전",
                    "• 4단계: 엘리트 적 처치",
                    "• 5단계: 그림자 장군 퇴치",
                    "• 6단계: 차원문 봉인",
                    "",
                    "목표:",
                    "• 그림자 차원문 조사",
                    "• 침략군 전위대 격퇴",
                    "• 성 방어 준비",
                    "• 3파 방어전 승리",
                    "• 그림자 장군 처치",
                    "• 차원문 봉인",
                    "",
                    "보상:",
                    "• 골드 12,000",
                    "• 다이아몬드 80개",
                    "• 네더라이트 투구",
                    "• 불사의 토템",
                    "• 엔더 상자",
                    "• 신호기",
                    "• 경험치 6,000"
            );
        } else {
            return Arrays.asList(
                    "Dark forces have begun their invasion through dimensional rifts.",
                    "Defend the kingdom and defeat the shadow legion!",
                    "",
                    "⚔️ Main Story Quest - Chapter 2",
                    "After the Guardian's Awakening, the dark forces have begun to move in earnest.",
                    "",
                    "Locations:",
                    "• Shadow Portal",
                    "• Castle Defense Line",
                    "• Shadow General's Throne",
                    "",
                    "Battle Phases:",
                    "• Phase 1: Scouting and Initial Combat",
                    "• Phase 2: Defense Preparation",
                    "• Phase 3: Large-scale Defense Battle",
                    "• Phase 4: Elite Enemy Elimination",
                    "• Phase 5: Shadow General Defeat",
                    "• Phase 6: Portal Sealing",
                    "",
                    "Objectives:",
                    "• Investigate shadow portal",
                    "• Repel invasion vanguard",
                    "• Prepare castle defenses",
                    "• Win 3-wave defense battle",
                    "• Defeat Shadow General",
                    "• Seal the portal",
                    "",
                    "Rewards:",
                    "• 12,000 Gold",
                    "• 80 Diamonds",
                    "• Netherite Helmet",
                    "• Totem of Undying",
                    "• Ender Chest",
                    "• Beacon",
                    "• 6,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "scout_report" -> isKorean ? "정찰병의 보고 듣기" : "Listen to scout's report";
            case "shadow_portal" -> isKorean ? "그림자 차원문 조사" : "Investigate shadow portal";
            case "portal_sample" -> isKorean ? "차원문 샘플 5개 수집" : "Collect 5 portal samples";
            case "shadow_scouts" -> isKorean ? "그림자 정찰병 15마리 처치" : "Kill 15 Shadow Scouts";
            case "shadow_warriors" -> isKorean ? "그림자 전사 20마리 처치" : "Kill 20 Shadow Warriors";
            case "shadow_essence" -> isKorean ? "그림자 정수 20개 수집" : "Collect 20 Shadow Essence";
            case "dark_fragments" -> isKorean ? "어둠의 파편 30개 수집" : "Collect 30 Dark Fragments";
            case "commander_talk" -> isKorean ? "방어 사령관과 전략 회의" : "Strategic meeting with Defense Commander";
            case "defense_materials" -> isKorean ? "방어 자재 50개 수집" : "Gather 50 defense materials";
            case "build_walls" -> isKorean ? "철창 벽 30개 설치" : "Build 30 iron bar walls";
            case "build_towers" -> isKorean ? "방어탑 20개 건설" : "Build 20 defense towers";
            case "craft_arrows" -> isKorean ? "화살 128개 제작" : "Craft 128 arrows";
            case "defense_position" -> isKorean ? "성벽 방어 위치 도착" : "Reach castle wall defense position";
            case "defend_wave1" -> isKorean ? "1차 공격파: 좀비 40마리 격퇴" : "Wave 1: Repel 40 zombies";
            case "defend_wave2" -> isKorean ? "2차 공격파: 스켈레톤 35마리 격퇴" : "Wave 2: Repel 35 skeletons";
            case "defend_wave3" -> isKorean ? "3차 공격파: 거미 30마리 격퇴" : "Wave 3: Repel 30 spiders";
            case "hold_position" -> isKorean ? "10분간 방어선 사수" : "Hold position for 10 minutes";
            case "shadow_captains" -> isKorean ? "그림자 대장 5명 처치" : "Defeat 5 Shadow Captains";
            case "shadow_mages" -> isKorean ? "그림자 마법사 5명 처치" : "Defeat 5 Shadow Mages";
            case "captain_badges" -> isKorean ? "대장의 휘장 5개 수집" : "Collect 5 Captain Badges";
            case "shadow_throne" -> isKorean ? "그림자 장군의 왕좌 도달" : "Reach Shadow General's throne";
            case "shadow_general_talk" -> isKorean ? "그림자 장군과 대면" : "Confront the Shadow General";
            case "shadow_general" -> isKorean ? "그림자 장군 처치" : "Defeat the Shadow General";
            case "general_crown" -> isKorean ? "장군의 왕관 획득" : "Obtain General's Crown";
            case "portal_keys" -> isKorean ? "차원문 열쇠 3개 수집" : "Collect 3 portal keys";
            case "return_portal" -> isKorean ? "차원문으로 복귀" : "Return to the portal";
            case "seal_portal" -> isKorean ? "엔드 스톤으로 차원문 봉인" : "Seal portal with End Stone";
            case "sealed_core" -> isKorean ? "봉인된 핵 획득" : "Obtain Sealed Core";
            case "deliver_crown" -> isKorean ? "장군의 왕관 제출" : "Deliver General's Crown";
            case "deliver_core" -> isKorean ? "봉인된 핵 제출" : "Deliver Sealed Core";
            case "victory_ceremony" -> isKorean ? "승리 기념식 참가" : "Attend victory ceremony";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("shadow_invasion_dialog");

        // 시작
        dialog.addLine("정찰병",
                "대변이야! 북쪽 국경에서 이상한 균열이 발견되었습니다!",
                "This is terrible! Strange rifts have been discovered at the northern border!");

        dialog.addLine("정찰병",
                "균열에서 검은 안개와 함께 정체불명의 존재들이 나타나고 있습니다!",
                "Unknown beings are emerging from the rifts along with black mist!");

        dialog.addLine("플레이어",
                "즉시 조사하겠습니다. 위치를 알려주세요.",
                "I'll investigate immediately. Tell me the location.");

        // 방어 사령관
        dialog.addLine("방어 사령관",
                "그림자 군단이 대규모로 침공하고 있다! 성을 방어해야 한다!",
                "The shadow legion is invading on a large scale! We must defend the castle!");

        dialog.addLine("방어 사령관",
                "방어 시설을 구축하고 병사들을 배치하라. 큰 전투가 시작될 것이다.",
                "Build defenses and position the soldiers. A great battle is about to begin.");

        // 전투 중
        dialog.addLine("방어 사령관",
                "잘 버티고 있다! 하지만 이건 시작에 불과해. 더 강한 적들이 오고 있다!",
                "You're holding well! But this is just the beginning. Stronger enemies are coming!");

        // 그림자 장군
        dialog.addLine("그림자 장군",
                "어리석은 필멸자들이여... 어둠의 시대가 도래했다!",
                "Foolish mortals... The age of darkness has arrived!");

        dialog.addLine("그림자 장군",
                "우리의 주인께서 곧 깨어나실 것이다. 너희의 저항은 무의미하다!",
                "Our master will soon awaken. Your resistance is meaningless!");

        dialog.addLine("플레이어",
                "그런 일은 일어나지 않을 것이다. 여기서 끝이다!",
                "That will not happen. This ends here!");

        // 승리
        dialog.addLine("방어 사령관",
                "믿을 수 없군! 정말로 그림자 군단을 물리쳤어!",
                "Unbelievable! You really defeated the shadow legion!");

        dialog.addLine("방어 사령관",
                "차원문도 봉인했으니 당분간은 안전할 거야. 영웅이여, 왕국은 당신께 큰 빚을 졌소.",
                "With the portal sealed, we'll be safe for now. Hero, the kingdom owes you a great debt.");

        return dialog;
    }
}