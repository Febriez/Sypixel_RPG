package com.febrie.rpg.quest.impl.guild;

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
 * 길드 요새 공성전 - 길드 퀘스트
 * 길드원들과 함께 적대 요새를 공략하는 대규모 전투 퀘스트
 *
 * @author Febrie
 */
public class GuildFortressSiegeQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class GuildFortressSiegeBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new GuildFortressSiegeQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public GuildFortressSiegeQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private GuildFortressSiegeQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new GuildFortressSiegeBuilder()
                .id(QuestID.GUILD_FORTRESS_SIEGE)
                .objectives(Arrays.asList(
                        // 공성전 준비
                        new InteractNPCObjective("siege_commander", "siege_commander"), // 공성 사령관
                        new ReachLevelObjective("guild_requirement", 35),
                        new PayCurrencyObjective("siege_registration", CurrencyType.GOLD, 15000),
                        
                        // 공성 무기 준비
                        new CollectItemObjective("siege_ladders", Material.LADDER, 50),
                        new CollectItemObjective("battering_ram", Material.OAK_LOG, 100),
                        new CollectItemObjective("catapult_parts", Material.IRON_BLOCK, 20),
                        new CraftItemObjective("craft_tnt", Material.TNT, 30),
                        new CollectItemObjective("arrows_supply", Material.ARROW, 500),
                        
                        // 전진 기지 구축
                        new VisitLocationObjective("siege_camp", "fortress_outskirts"),
                        new PlaceBlockObjective("build_camp", Material.WHITE_WOOL, 20),
                        new PlaceBlockObjective("place_banners", Material.WHITE_BANNER, 5),
                        new PlaceBlockObjective("setup_supplies", Material.CHEST, 10),
                        
                        // 외벽 공략
                        new VisitLocationObjective("outer_walls", "fortress_outer_walls"),
                        new KillMobObjective("wall_archers", EntityType.SKELETON, 50),
                        new KillMobObjective("wall_guards", EntityType.IRON_GOLEM, 15),
                        new BreakBlockObjective("breach_wall", Material.STONE_BRICKS, 100),
                        new PlaceBlockObjective("place_ladders", Material.LADDER, 20),
                        
                        // 외부 정원 전투
                        new VisitLocationObjective("fortress_gardens", "fortress_courtyard"),
                        new KillMobObjective("garden_defenders", EntityType.VINDICATOR, 30),
                        new KillMobObjective("war_hounds", EntityType.WOLF, 40),
                        new KillPlayerObjective("enemy_players", 10), // 적대 길드원
                        new SurviveObjective("hold_gardens", 600), // 10분간 점령
                        
                        // 내부 성채 침투
                        new VisitLocationObjective("inner_keep", "fortress_inner_keep"),
                        new KillMobObjective("elite_knights", EntityType.PIGLIN_BRUTE, 25),
                        new KillMobObjective("battle_mages", EntityType.EVOKER, 15),
                        new CollectItemObjective("keep_keys", Material.TRIPWIRE_HOOK, 3),
                        
                        // 보물고 약탈
                        new VisitLocationObjective("treasure_vault", "fortress_treasury"),
                        new BreakBlockObjective("break_vault", Material.IRON_BARS, 30),
                        new CollectItemObjective("gold_treasures", Material.GOLD_BLOCK, 50),
                        new CollectItemObjective("guild_artifacts", Material.ENCHANTED_BOOK, 10),
                        
                        // 왕좌의 방 최종전
                        new VisitLocationObjective("throne_room", "fortress_throne_room"),
                        new KillMobObjective("fortress_champion", EntityType.RAVAGER, 3),
                        new KillMobObjective("fortress_lord", EntityType.WITHER, 1),
                        new CollectItemObjective("lord_crown", Material.GOLDEN_HELMET, 1),
                        
                        // 요새 점령
                        new PlaceBlockObjective("plant_flag", Material.WHITE_BANNER, 1),
                        new InteractNPCObjective("claim_fortress", "fortress_keeper"), // 요새 관리인
                        new PayCurrencyObjective("fortress_tax", CurrencyType.GOLD, 5000),
                        
                        // 승리 보고
                        new DeliverItemObjective("deliver_crown", "siege_commander", Material.GOLDEN_HELMET, 1),
                        new DeliverItemObjective("deliver_artifacts", "guild_master", Material.ENCHANTED_BOOK, 10),
                        new InteractNPCObjective("victory_report", "siege_commander")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 50000)
                        .addCurrency(CurrencyType.DIAMOND, 300)
                        .addItem(new ItemStack(Material.BEACON, 3))
                        .addItem(new ItemStack(Material.NETHERITE_BLOCK, 2))
                        .addItem(new ItemStack(Material.SHULKER_BOX, 5))
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addItem(new ItemStack(Material.TRIDENT))
                        .addExperience(12000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.GUILD)
                .addPrerequisite(QuestID.GUILD_ESTABLISHMENT)
                .minLevel(35)
                .maxLevel(0);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "요새 공성전: 철의 성채" : "Fortress Siege: Iron Citadel";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "길드원들과 함께 적대 세력의 요새를 공략하세요!",
                    "전략적 계획과 팀워크가 승리의 열쇠입니다.",
                    "",
                    "⚔️ 공성전 정보:",
                    "• 목표: 철의 성채 점령",
                    "• 적대 세력: 검은 철 길드",
                    "• 예상 방어군: 500+ 유닛",
                    "• PvP 전투 포함",
                    "",
                    "공성 단계:",
                    "• 1단계: 공성 무기 준비",
                    "• 2단계: 전진 기지 구축",
                    "• 3단계: 외벽 돌파",
                    "• 4단계: 정원 점령",
                    "• 5단계: 내부 성채 침투",
                    "• 6단계: 보물고 약탈",
                    "• 7단계: 왕좌의 방 제압",
                    "• 8단계: 요새 점령",
                    "",
                    "필요 사항:",
                    "• 길드 레벨 10 이상",
                    "• 최소 길드원 10명",
                    "• 공성 무기와 보급품",
                    "• 전략적 역할 분담",
                    "",
                    "주요 목표:",
                    "• 외벽 방어군 격파",
                    "• 적 길드원 10명 처치",
                    "• 정원 10분간 점령 유지",
                    "• 요새 군주 처치",
                    "• 길드 깃발 게양",
                    "",
                    "특별 보상:",
                    "• 요새 소유권 (1주일)",
                    "• 매일 자원 생산",
                    "• 길드 상점 접근권",
                    "• 특별 칭호: '요새 정복자'",
                    "",
                    "기본 보상:",
                    "• 골드 50,000",
                    "• 다이아몬드 300개",
                    "• 신호기 3개",
                    "• 네더라이트 블록 2개",
                    "• 셜커 상자 5개",
                    "• 겉날개",
                    "• 삼지창",
                    "• 경험치 12,000"
            );
        } else {
            return Arrays.asList(
                    "Conquer the enemy fortress with your guild members!",
                    "Strategic planning and teamwork are the keys to victory.",
                    "",
                    "⚔️ Siege Information:",
                    "• Target: Iron Citadel",
                    "• Enemy Force: Black Iron Guild",
                    "• Expected Defenders: 500+ units",
                    "• Includes PvP combat",
                    "",
                    "Siege Phases:",
                    "• Phase 1: Prepare siege weapons",
                    "• Phase 2: Establish forward base",
                    "• Phase 3: Breach outer walls",
                    "• Phase 4: Capture gardens",
                    "• Phase 5: Infiltrate inner keep",
                    "• Phase 6: Raid treasury",
                    "• Phase 7: Conquer throne room",
                    "• Phase 8: Claim fortress",
                    "",
                    "Requirements:",
                    "• Guild Level 10+",
                    "• Minimum 10 guild members",
                    "• Siege weapons and supplies",
                    "• Strategic role distribution",
                    "",
                    "Main Objectives:",
                    "• Defeat wall defenders",
                    "• Kill 10 enemy guild members",
                    "• Hold gardens for 10 minutes",
                    "• Defeat fortress lord",
                    "• Plant guild banner",
                    "",
                    "Special Rewards:",
                    "• Fortress ownership (1 week)",
                    "• Daily resource production",
                    "• Guild shop access",
                    "• Special title: 'Fortress Conqueror'",
                    "",
                    "Base Rewards:",
                    "• 50,000 Gold",
                    "• 300 Diamonds",
                    "• 3 Beacons",
                    "• 2 Netherite Blocks",
                    "• 5 Shulker Boxes",
                    "• Elytra",
                    "• Trident",
                    "• 12,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "siege_commander" -> isKorean ? "공성 사령관과 대화" : "Talk to Siege Commander";
            case "guild_requirement" -> isKorean ? "길드 레벨 35 달성" : "Reach Guild Level 35";
            case "siege_registration" -> isKorean ? "공성전 등록비 15,000골드" : "Pay 15,000 gold siege registration";
            case "siege_ladders" -> isKorean ? "공성 사다리 50개 준비" : "Prepare 50 siege ladders";
            case "battering_ram" -> isKorean ? "공성추 재료 100개 수집" : "Collect 100 battering ram materials";
            case "catapult_parts" -> isKorean ? "투석기 부품 20개 수집" : "Collect 20 catapult parts";
            case "craft_tnt" -> isKorean ? "TNT 30개 제작" : "Craft 30 TNT";
            case "arrows_supply" -> isKorean ? "화살 500개 준비" : "Prepare 500 arrows";
            case "siege_camp" -> isKorean ? "요새 외곽 도착" : "Arrive at fortress outskirts";
            case "build_camp" -> isKorean ? "진영 텐트 20개 설치" : "Build 20 camp tents";
            case "place_banners" -> isKorean ? "길드 깃발 5개 설치" : "Place 5 guild banners";
            case "setup_supplies" -> isKorean ? "보급 상자 10개 설치" : "Setup 10 supply chests";
            case "outer_walls" -> isKorean ? "외벽 도달" : "Reach outer walls";
            case "wall_archers" -> isKorean ? "성벽 궁수 50명 처치" : "Kill 50 wall archers";
            case "wall_guards" -> isKorean ? "성벽 수비대 15명 처치" : "Kill 15 wall guards";
            case "breach_wall" -> isKorean ? "성벽 100블록 파괴" : "Breach 100 wall blocks";
            case "place_ladders" -> isKorean ? "공성 사다리 20개 설치" : "Place 20 siege ladders";
            case "fortress_gardens" -> isKorean ? "요새 정원 진입" : "Enter fortress gardens";
            case "garden_defenders" -> isKorean ? "정원 수비대 30명 처치" : "Kill 30 garden defenders";
            case "war_hounds" -> isKorean ? "전투견 40마리 처치" : "Kill 40 war hounds";
            case "enemy_players" -> isKorean ? "적 길드원 10명 처치" : "Kill 10 enemy guild members";
            case "hold_gardens" -> isKorean ? "정원 10분간 점령 유지" : "Hold gardens for 10 minutes";
            case "inner_keep" -> isKorean ? "내부 성채 진입" : "Enter inner keep";
            case "elite_knights" -> isKorean ? "정예 기사 25명 처치" : "Kill 25 elite knights";
            case "battle_mages" -> isKorean ? "전투 마법사 15명 처치" : "Kill 15 battle mages";
            case "keep_keys" -> isKorean ? "성채 열쇠 3개 획득" : "Obtain 3 keep keys";
            case "treasure_vault" -> isKorean ? "보물고 도달" : "Reach treasure vault";
            case "break_vault" -> isKorean ? "금고 방어막 30개 파괴" : "Break 30 vault barriers";
            case "gold_treasures" -> isKorean ? "금 보물 50개 약탈" : "Loot 50 gold treasures";
            case "guild_artifacts" -> isKorean ? "길드 유물 10개 획득" : "Obtain 10 guild artifacts";
            case "throne_room" -> isKorean ? "왕좌의 방 진입" : "Enter throne room";
            case "fortress_champion" -> isKorean ? "요새 챔피언 3명 처치" : "Kill 3 fortress champions";
            case "fortress_lord" -> isKorean ? "요새 군주 처치" : "Defeat fortress lord";
            case "lord_crown" -> isKorean ? "군주의 왕관 획득" : "Obtain lord's crown";
            case "plant_flag" -> isKorean ? "길드 깃발 게양" : "Plant guild flag";
            case "claim_fortress" -> isKorean ? "요새 점령 선언" : "Claim fortress";
            case "fortress_tax" -> isKorean ? "요새세 5,000골드 납부" : "Pay 5,000 gold fortress tax";
            case "deliver_crown" -> isKorean ? "왕관 제출" : "Deliver crown";
            case "deliver_artifacts" -> isKorean ? "유물 제출" : "Deliver artifacts";
            case "victory_report" -> isKorean ? "승리 보고" : "Report victory";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("guild_fortress_siege_dialog");

        // 시작
        dialog.addLine("공성 사령관",
                "길드 마스터! 철의 성채를 공략할 준비가 되었는가?",
                "Guild Master! Are you ready to assault the Iron Citadel?");

        dialog.addLine("공성 사령관",
                "검은 철 길드가 수 년간 이 요새를 지배해왔다. 이제 그들의 시대를 끝낼 때다.",
                "The Black Iron Guild has dominated this fortress for years. It's time to end their reign.");

        dialog.addLine("플레이어",
                "우리 길드는 준비되었습니다. 작전은?",
                "Our guild is ready. What's the plan?");

        dialog.addLine("공성 사령관",
                "먼저 공성 무기를 준비하고, 외벽을 돌파한 후 단계적으로 진격한다.",
                "First prepare siege weapons, breach the outer walls, then advance step by step.");

        // 전투 중
        dialog.addLine("공성 사령관",
                "훌륭해! 외벽을 돌파했다! 이제 정원을 점령하라!",
                "Excellent! You've breached the outer walls! Now capture the gardens!");

        dialog.addLine("길드원",
                "적 길드원들이 반격하고 있습니다!",
                "Enemy guild members are counterattacking!");

        dialog.addLine("공성 사령관",
                "예상했던 일이다. 진형을 유지하고 계속 전진하라!",
                "As expected. Maintain formation and keep advancing!");

        // 내부 진입
        dialog.addLine("공성 사령관",
                "내부 성채에 진입했군! 이제 보물고와 왕좌의 방만 남았다.",
                "You've entered the inner keep! Only the treasury and throne room remain.");

        // 최종전
        dialog.addLine("요새 군주",
                "감히 내 성채에! 너희는 여기서 끝이다!",
                "How dare you enter my citadel! This is where you end!");

        dialog.addLine("플레이어",
                "이 요새는 이제 우리 것이다!",
                "This fortress is ours now!");

        // 승리
        dialog.addLine("공성 사령관",
                "해냈다! 철의 성채는 이제 우리 길드의 것이다!",
                "You did it! The Iron Citadel now belongs to our guild!");

        dialog.addLine("요새 관리인",
                "새로운 주인을 모시게 되어 영광입니다. 요새의 모든 시설을 이용하실 수 있습니다.",
                "It's an honor to serve new masters. You may use all fortress facilities.");

        dialog.addLine("공성 사령관",
                "이 승리로 우리 길드는 역사에 이름을 남기게 될 것이다!",
                "With this victory, our guild will be remembered in history!");

        return dialog;
    }
}