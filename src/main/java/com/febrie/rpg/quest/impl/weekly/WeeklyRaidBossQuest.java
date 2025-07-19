package com.febrie.rpg.quest.impl.weekly;

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
 * 주간 레이드 보스 - 주간 퀘스트
 * 매주 초기화되는 대규모 레이드 도전
 *
 * @author Febrie
 */
public class WeeklyRaidBossQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class WeeklyRaidBossBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new WeeklyRaidBossQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public WeeklyRaidBossQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private WeeklyRaidBossQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new WeeklyRaidBossBuilder()
                .id(QuestID.WEEKLY_RAID_BOSS)
                .objectives(Arrays.asList(
                        // 준비 단계
                        new InteractNPCObjective("raid_commander", "raid_commander"), // 레이드 사령관
                        new ReachLevelObjective("level_requirement", 40),
                        new CollectItemObjective("raid_key", Material.TRIPWIRE_HOOK, 3),
                        
                        // 레이드 던전 입장
                        new VisitLocationObjective("raid_entrance", "chaos_fortress_entrance"),
                        
                        // 첫 번째 구역 - 혼돈의 전당
                        new KillMobObjective("chaos_minions", EntityType.WITHER_SKELETON, 30),
                        new KillMobObjective("chaos_knights", EntityType.PIGLIN_BRUTE, 20),
                        new KillMobObjective("mini_boss_1", EntityType.ELDER_GUARDIAN, 1),
                        new CollectItemObjective("chaos_fragment", Material.NETHERITE_SCRAP, 5),
                        
                        // 두 번째 구역 - 어둠의 성소
                        new VisitLocationObjective("dark_sanctuary", "chaos_fortress_sanctuary"),
                        new SurviveObjective("darkness_trial", 300), // 5분
                        new KillMobObjective("shadow_assassins", EntityType.EVOKER, 10),
                        new KillMobObjective("void_walkers", EntityType.ENDERMAN, 25),
                        new KillMobObjective("mini_boss_2", EntityType.RAVAGER, 1),
                        new CollectItemObjective("void_essence", Material.ENDER_EYE, 10),
                        
                        // 최종 보스 구역
                        new VisitLocationObjective("throne_room", "chaos_fortress_throne"),
                        new KillPlayerObjective("pvp_zone", 3), // PvP 구역에서 3명 처치
                        new PayCurrencyObjective("boss_summon", CurrencyType.GOLD, 5000),
                        new KillMobObjective("chaos_lord", EntityType.WITHER, 1),
                        
                        // 보상 획득
                        new CollectItemObjective("legendary_loot", Material.NETHER_STAR, 1),
                        new DeliverItemObjective("raid_complete", "raid_commander", Material.NETHER_STAR, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 15000)
                        .addCurrency(CurrencyType.DIAMOND, 100)
                        .addItem(new ItemStack(Material.NETHERITE_INGOT, 3))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 5))
                        .addItem(new ItemStack(Material.BEACON))
                        .addExperience(10000)
                        .build())
                .sequential(false)  // 자유로운 진행 가능
                .repeatable(true)
                .weekly(true)      // 주간 퀘스트
                .category(QuestCategory.WEEKLY)
                .minLevel(40)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "주간 레이드: 혼돈의 요새" : "Weekly Raid: Fortress of Chaos";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "매주 초기화되는 최고 난이도의 레이드 던전입니다.",
                    "혼돈의 군주와 그의 부하들을 물리치고 전설적인 보상을 획득하세요!",
                    "",
                    "⚠️ 경고: 이 레이드는 매우 어렵습니다!",
                    "• 권장 레벨: 40 이상",
                    "• 권장 인원: 3-5명의 파티",
                    "• PvP 구역이 포함되어 있습니다",
                    "",
                    "던전 구성:",
                    "• 1구역: 혼돈의 전당",
                    "• 2구역: 어둠의 성소",
                    "• 3구역: 왕좌의 방 (최종 보스)",
                    "",
                    "목표:",
                    "• 레이드 사령관과 대화",
                    "• 레벨 40 달성",
                    "• 레이드 열쇠 3개 수집",
                    "• 혼돈의 요새 입장",
                    "• 각 구역의 몬스터 처치",
                    "• 미니 보스 2마리 처치",
                    "• PvP 구역에서 3명 처치",
                    "• 혼돈의 군주(최종 보스) 처치",
                    "• 전설의 전리품 획득",
                    "",
                    "보상:",
                    "• 골드 15,000",
                    "• 다이아몬드 100개",
                    "• 네더라이트 주괴 3개",
                    "• 마법이 부여된 책 5개",
                    "• 신호기",
                    "• 경험치 10,000"
            );
        } else {
            return Arrays.asList(
                    "The highest difficulty raid dungeon that resets weekly.",
                    "Defeat the Chaos Lord and his minions to earn legendary rewards!",
                    "",
                    "⚠️ WARNING: This raid is very difficult!",
                    "• Recommended Level: 40+",
                    "• Recommended Party: 3-5 players",
                    "• Contains PvP zones",
                    "",
                    "Dungeon Layout:",
                    "• Zone 1: Hall of Chaos",
                    "• Zone 2: Dark Sanctuary",
                    "• Zone 3: Throne Room (Final Boss)",
                    "",
                    "Objectives:",
                    "• Talk to the Raid Commander",
                    "• Reach Level 40",
                    "• Collect 3 Raid Keys",
                    "• Enter the Fortress of Chaos",
                    "• Clear monsters in each zone",
                    "• Defeat 2 Mini Bosses",
                    "• Kill 3 players in PvP zone",
                    "• Defeat the Chaos Lord (Final Boss)",
                    "• Obtain legendary loot",
                    "",
                    "Rewards:",
                    "• 15,000 Gold",
                    "• 100 Diamonds",
                    "• 3 Netherite Ingots",
                    "• 5 Enchanted Books",
                    "• Beacon",
                    "• 10,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "raid_commander" -> isKorean ? "레이드 사령관과 대화" : "Talk to the Raid Commander";
            case "level_requirement" -> isKorean ? "레벨 40 달성" : "Reach Level 40";
            case "raid_key" -> isKorean ? "레이드 열쇠 3개 수집" : "Collect 3 Raid Keys";
            case "raid_entrance" -> isKorean ? "혼돈의 요새 입장" : "Enter the Fortress of Chaos";
            case "chaos_minions" -> isKorean ? "혼돈의 하수인 30마리 처치" : "Kill 30 Chaos Minions";
            case "chaos_knights" -> isKorean ? "혼돈의 기사 20마리 처치" : "Kill 20 Chaos Knights";
            case "mini_boss_1" -> isKorean ? "첫 번째 미니 보스 처치" : "Defeat the First Mini Boss";
            case "chaos_fragment" -> isKorean ? "혼돈의 파편 5개 수집" : "Collect 5 Chaos Fragments";
            case "dark_sanctuary" -> isKorean ? "어둠의 성소 도달" : "Reach the Dark Sanctuary";
            case "darkness_trial" -> isKorean ? "어둠의 시련 5분간 생존" : "Survive the Darkness Trial for 5 minutes";
            case "shadow_assassins" -> isKorean ? "그림자 암살자 10마리 처치" : "Kill 10 Shadow Assassins";
            case "void_walkers" -> isKorean ? "공허 방랑자 25마리 처치" : "Kill 25 Void Walkers";
            case "mini_boss_2" -> isKorean ? "두 번째 미니 보스 처치" : "Defeat the Second Mini Boss";
            case "void_essence" -> isKorean ? "공허의 정수 10개 수집" : "Collect 10 Void Essences";
            case "throne_room" -> isKorean ? "왕좌의 방 도달" : "Reach the Throne Room";
            case "pvp_zone" -> isKorean ? "PvP 구역에서 3명 처치" : "Kill 3 players in PvP zone";
            case "boss_summon" -> isKorean ? "보스 소환 비용 5000골드 지불" : "Pay 5000 gold to summon boss";
            case "chaos_lord" -> isKorean ? "혼돈의 군주 처치" : "Defeat the Chaos Lord";
            case "legendary_loot" -> isKorean ? "전설의 전리품 획득" : "Obtain legendary loot";
            case "raid_complete" -> isKorean ? "레이드 완료 보고" : "Report raid completion";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("weekly_raid_boss_dialog");

        dialog.addLine("레이드 사령관",
                "용사여, 혼돈의 요새에 도전할 준비가 되었는가?",
                "Warrior, are you ready to challenge the Fortress of Chaos?");

        dialog.addLine("레이드 사령관",
                "이곳은 매주 한 번만 도전할 수 있는 최고 난이도의 던전이네.",
                "This is the highest difficulty dungeon that can only be challenged once per week.");

        dialog.addLine("플레이어",
                "어떤 위험이 기다리고 있나요?",
                "What dangers await?");

        dialog.addLine("레이드 사령관",
                "혼돈의 군주와 그의 정예 부대, 그리고... 다른 모험자들이지.",
                "The Chaos Lord and his elite forces, and... other adventurers.");

        dialog.addLine("레이드 사령관",
                "맞아, 왕좌의 방 앞에는 PvP 구역이 있어. 경쟁도 피할 수 없다네.",
                "Yes, there's a PvP zone before the Throne Room. Competition is unavoidable.");

        dialog.addLine("플레이어",
                "보상이 그만한 가치가 있나요?",
                "Are the rewards worth it?");

        dialog.addLine("레이드 사령관",
                "당연하지! 네더라이트, 전설의 장비, 그리고 막대한 부... 모든 것을 얻을 수 있네.",
                "Of course! Netherite, legendary equipment, and vast wealth... you can obtain everything.");

        dialog.addLine("레이드 사령관",
                "하지만 먼저 레이드 열쇠 3개를 모아와야 해. 그래야 입장할 수 있다네.",
                "But first, you need to collect 3 Raid Keys. Only then can you enter.");

        return dialog;
    }
}