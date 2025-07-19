package com.febrie.rpg.quest.impl.daily;

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
 * 일일 현상금 사냥 - 일일 퀘스트
 * 매일 갱신되는 현상금 목표를 추적하고 처치하는 퀘스트
 *
 * @author Febrie
 */
public class DailyBountyHunterQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class DailyBountyHunterBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new DailyBountyHunterQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DailyBountyHunterQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DailyBountyHunterQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new DailyBountyHunterBuilder()
                .id(QuestID.DAILY_BOUNTY_HUNTER)
                .objectives(Arrays.asList(
                        // 현상금 사무소 방문
                        new InteractNPCObjective("bounty_officer", "bounty_officer"), // 현상금 담당관
                        
                        // 첫 번째 현상금 - 일반 범죄자
                        new VisitLocationObjective("criminal_hideout", "bandit_camp"),
                        new KillMobObjective("wanted_bandits", EntityType.PILLAGER, 15),
                        new CollectItemObjective("bandit_badges", Material.IRON_NUGGET, 15),
                        
                        // 두 번째 현상금 - 위험한 몬스터
                        new VisitLocationObjective("monster_lair", "dangerous_cave"),
                        new KillMobObjective("alpha_spider", EntityType.CAVE_SPIDER, 20),
                        new KillMobObjective("pack_leader", EntityType.WOLF, 10),
                        new CollectItemObjective("monster_fangs", Material.SPIDER_EYE, 10),
                        
                        // 세 번째 현상금 - 마법사 추적
                        new VisitLocationObjective("wizard_tower", "dark_wizard_tower"),
                        new KillMobObjective("dark_wizards", EntityType.EVOKER, 5),
                        new KillMobObjective("summoned_vex", EntityType.VEX, 20),
                        new CollectItemObjective("wizard_staves", Material.STICK, 5),
                        new CollectItemObjective("magic_essence", Material.LAPIS_LAZULI, 20),
                        
                        // 네 번째 현상금 - 엘리트 표적
                        new InteractNPCObjective("informant", "bounty_informant"), // 정보원
                        new PayCurrencyObjective("buy_info", CurrencyType.GOLD, 500),
                        new VisitLocationObjective("elite_location", "abandoned_fortress"),
                        new KillMobObjective("elite_guard", EntityType.VINDICATOR, 8),
                        new KillMobObjective("bounty_boss", EntityType.RAVAGER, 1),
                        new CollectItemObjective("boss_head", Material.PLAYER_HEAD, 1),
                        
                        // 증거 수집
                        new CollectItemObjective("evidence_documents", Material.PAPER, 10),
                        new CollectItemObjective("stolen_goods", Material.EMERALD, 30),
                        
                        // 보고 및 보상
                        new DeliverItemObjective("deliver_badges", "bounty_officer", Material.IRON_NUGGET, 15),
                        new DeliverItemObjective("deliver_evidence", "bounty_officer", Material.PAPER, 10),
                        new DeliverItemObjective("deliver_head", "bounty_officer", Material.PLAYER_HEAD, 1),
                        new InteractNPCObjective("claim_bounty", "bounty_officer")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 2500)
                        .addCurrency(CurrencyType.DIAMOND, 15)
                        .addItem(new ItemStack(Material.CROSSBOW))
                        .addItem(new ItemStack(Material.ARROW, 64))
                        .addItem(new ItemStack(Material.SPYGLASS))
                        .addItem(new ItemStack(Material.IRON_SWORD))
                        .addExperience(1500)
                        .build())
                .sequential(false)  // 자유로운 순서로 진행 가능
                .repeatable(true)
                .daily(true)       // 일일 퀘스트
                .category(QuestCategory.DAILY)
                .minLevel(20)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "일일 현상금 사냥" : "Daily Bounty Hunt";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "오늘의 현상금 목표를 추적하고 처치하세요!",
                    "위험한 범죄자들과 몬스터들이 당신을 기다립니다.",
                    "",
                    "🎯 현상금 목표:",
                    "• 산적단 두목과 부하들",
                    "• 위험한 변이 몬스터들",
                    "• 어둠의 마법사 집단",
                    "• 엘리트 현상수배범",
                    "",
                    "사냥 팁:",
                    "• 각 목표는 특정 지역에 출현",
                    "• 정보원에게서 위치 정보 구매 가능",
                    "• 증거품을 반드시 수집할 것",
                    "• 팀으로 사냥하면 더 효율적",
                    "",
                    "목표:",
                    "• 현상금 담당관과 대화",
                    "• 산적 15명 처치",
                    "• 변이 거미 20마리 처치",
                    "• 늑대 우두머리 10마리 처치",
                    "• 어둠의 마법사 5명 처치",
                    "• 소환된 벡스 20마리 처치",
                    "• 엘리트 경비병 8명 처치",
                    "• 현상수배 보스 처치",
                    "• 증거품 수집 및 제출",
                    "",
                    "보상:",
                    "• 골드 2,500",
                    "• 다이아몬드 15개",
                    "• 석궁",
                    "• 화살 64개",
                    "• 망원경",
                    "• 철 검",
                    "• 경험치 1,500"
            );
        } else {
            return Arrays.asList(
                    "Track and eliminate today's bounty targets!",
                    "Dangerous criminals and monsters await you.",
                    "",
                    "🎯 Bounty Targets:",
                    "• Bandit leaders and their gangs",
                    "• Dangerous mutant monsters",
                    "• Dark wizard cults",
                    "• Elite wanted criminals",
                    "",
                    "Hunting Tips:",
                    "• Each target spawns in specific areas",
                    "• Buy location info from informants",
                    "• Always collect evidence",
                    "• Team hunting is more efficient",
                    "",
                    "Objectives:",
                    "• Talk to Bounty Officer",
                    "• Kill 15 bandits",
                    "• Kill 20 mutant spiders",
                    "• Kill 10 pack leaders",
                    "• Kill 5 dark wizards",
                    "• Kill 20 summoned vexes",
                    "• Kill 8 elite guards",
                    "• Kill the bounty boss",
                    "• Collect and deliver evidence",
                    "",
                    "Rewards:",
                    "• 2,500 Gold",
                    "• 15 Diamonds",
                    "• Crossbow",
                    "• 64 Arrows",
                    "• Spyglass",
                    "• Iron Sword",
                    "• 1,500 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "bounty_officer" -> isKorean ? "현상금 담당관과 대화" : "Talk to Bounty Officer";
            case "criminal_hideout" -> isKorean ? "범죄자 은신처 찾기" : "Find criminal hideout";
            case "wanted_bandits" -> isKorean ? "수배된 산적 15명 처치" : "Kill 15 wanted bandits";
            case "bandit_badges" -> isKorean ? "산적 휘장 15개 수집" : "Collect 15 bandit badges";
            case "monster_lair" -> isKorean ? "몬스터 소굴 찾기" : "Find monster lair";
            case "alpha_spider" -> isKorean ? "변이 거미 20마리 처치" : "Kill 20 mutant spiders";
            case "pack_leader" -> isKorean ? "무리 우두머리 10마리 처치" : "Kill 10 pack leaders";
            case "monster_fangs" -> isKorean ? "몬스터 송곳니 10개 수집" : "Collect 10 monster fangs";
            case "wizard_tower" -> isKorean ? "어둠의 마법사 탑 찾기" : "Find dark wizard tower";
            case "dark_wizards" -> isKorean ? "어둠의 마법사 5명 처치" : "Kill 5 dark wizards";
            case "summoned_vex" -> isKorean ? "소환된 벡스 20마리 처치" : "Kill 20 summoned vexes";
            case "wizard_staves" -> isKorean ? "마법사 지팡이 5개 수집" : "Collect 5 wizard staves";
            case "magic_essence" -> isKorean ? "마법 정수 20개 수집" : "Collect 20 magic essence";
            case "informant" -> isKorean ? "정보원과 접촉" : "Contact informant";
            case "buy_info" -> isKorean ? "정보료 500골드 지불" : "Pay 500 gold for information";
            case "elite_location" -> isKorean ? "엘리트 목표 위치 도달" : "Reach elite target location";
            case "elite_guard" -> isKorean ? "엘리트 경비병 8명 처치" : "Kill 8 elite guards";
            case "bounty_boss" -> isKorean ? "현상수배 보스 처치" : "Kill bounty boss";
            case "boss_head" -> isKorean ? "보스의 머리 획득" : "Obtain boss head";
            case "evidence_documents" -> isKorean ? "증거 문서 10장 수집" : "Collect 10 evidence documents";
            case "stolen_goods" -> isKorean ? "도난품 30개 회수" : "Recover 30 stolen goods";
            case "deliver_badges" -> isKorean ? "산적 휘장 제출" : "Deliver bandit badges";
            case "deliver_evidence" -> isKorean ? "증거 문서 제출" : "Deliver evidence documents";
            case "deliver_head" -> isKorean ? "보스의 머리 제출" : "Deliver boss head";
            case "claim_bounty" -> isKorean ? "현상금 수령" : "Claim bounty reward";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("daily_bounty_hunter_dialog");

        // 시작
        dialog.addLine("현상금 담당관",
                "또 다른 현상금 사냥꾼인가? 오늘은 특히 위험한 목표들이 많네.",
                "Another bounty hunter? Today we have particularly dangerous targets.");

        dialog.addLine("현상금 담당관",
                "네 개의 현상금이 걸려있다. 모두 처리하면 큰 보상이 기다리고 있지.",
                "Four bounties are posted. Complete them all for a substantial reward.");

        dialog.addLine("플레이어",
                "어떤 목표들인가요?",
                "What kind of targets?");

        dialog.addLine("현상금 담당관",
                "산적단, 변이 몬스터, 어둠의 마법사, 그리고... 특별한 목표 하나.",
                "Bandits, mutant monsters, dark wizards, and... one special target.");

        // 정보원
        dialog.addLine("정보원",
                "엘리트 목표의 위치를 알고 싶나? 500골드면 알려주지.",
                "Want to know the elite target's location? 500 gold and it's yours.");

        dialog.addLine("정보원",
                "폐허가 된 요새에 숨어있다. 하지만 조심해, 경비가 삼엄하거든.",
                "Hiding in an abandoned fortress. But be careful, heavily guarded.");

        // 현상금 수령
        dialog.addLine("현상금 담당관",
                "인상적이군! 모든 목표를 처리했나?",
                "Impressive! Did you handle all targets?");

        dialog.addLine("플레이어",
                "네, 증거품도 모두 가져왔습니다.",
                "Yes, I've brought all the evidence too.");

        dialog.addLine("현상금 담당관",
                "완벽해! 여기 약속한 현상금이다. 내일도 새로운 목표가 있을 거야.",
                "Perfect! Here's your promised bounty. Come back tomorrow for new targets.");

        dialog.addLine("현상금 담당관",
                "실력 있는 사냥꾼은 언제나 환영이야. 좋은 장비도 보너스로 주겠네.",
                "Skilled hunters are always welcome. Here's some good equipment as bonus.");

        return dialog;
    }
}