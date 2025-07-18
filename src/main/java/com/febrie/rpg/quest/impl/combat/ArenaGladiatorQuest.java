package com.febrie.rpg.quest.impl.combat;

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
 * 투기장의 검투사 - 전투 퀘스트
 * 투기장에서 명예와 부를 위해 싸우는 퀘스트
 *
 * @author Febrie
 */
public class ArenaGladiatorQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class ArenaGladiatorBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new ArenaGladiatorQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public ArenaGladiatorQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private ArenaGladiatorQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new ArenaGladiatorBuilder()
                .id(QuestID.COMBAT_ARENA_GLADIATOR)
                .objectives(Arrays.asList(
                        // 준비 단계
                        new InteractNPCObjective("arena_master", "arena_master"), // 투기장 관리인
                        new PayCurrencyObjective("entry_fee", CurrencyType.GOLD, 500),
                        new CollectItemObjective("prepare_armor", Material.IRON_CHESTPLATE, 1),
                        new CollectItemObjective("prepare_weapon", Material.IRON_SWORD, 1),
                        
                        // 투기장 입장
                        new VisitLocationObjective("enter_arena", "gladiator_arena"),
                        
                        // 1라운드 - 초보자전
                        new KillPlayerObjective("round1_pvp", 1),
                        new CollectItemObjective("round1_token", Material.GOLD_NUGGET, 1),
                        
                        // 2라운드 - 야수와의 대결
                        new KillMobObjective("round2_wolves", EntityType.WOLF, 5),
                        new KillMobObjective("round2_bears", EntityType.POLAR_BEAR, 3),
                        new CollectItemObjective("round2_token", Material.GOLD_NUGGET, 1),
                        
                        // 3라운드 - 팀전
                        new KillPlayerObjective("round3_team", 3),
                        new SurviveObjective("round3_survive", 300), // 5분
                        new CollectItemObjective("round3_token", Material.GOLD_NUGGET, 1),
                        
                        // 결승전 - 챔피언과의 대결
                        new InteractNPCObjective("challenge_champion", "arena_champion"), // 현 챔피언
                        new KillMobObjective("defeat_champion", EntityType.IRON_GOLEM, 1),
                        new CollectItemObjective("champion_belt", Material.GOLDEN_HELMET, 1),
                        
                        // 완료
                        new DeliverItemObjective("claim_victory", "arena_master", Material.GOLDEN_HELMET, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 5000)
                        .addCurrency(CurrencyType.DIAMOND, 40)
                        .addItem(new ItemStack(Material.DIAMOND_SWORD))
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 5))
                        .addItem(new ItemStack(Material.PLAYER_HEAD)) // 챔피언 트로피
                        .addExperience(3000)
                        .build())
                .sequential(true)
                .repeatable(true)  // 반복 가능
                .category(QuestCategory.COMBAT)
                .minLevel(25)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "투기장의 영광" : "Glory of the Arena";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "투기장에서 당신의 전투 실력을 증명하세요!",
                    "다른 검투사들과 맞서 싸우고 챔피언이 되세요.",
                    "",
                    "⚔️ 투기장 규칙:",
                    "• 입장료 500골드 필요",
                    "• 각 라운드를 통과해야 다음 라운드 진행 가능",
                    "• 사망 시 퀘스트 실패",
                    "• PvP 구역이 포함되어 있습니다",
                    "",
                    "라운드 구성:",
                    "• 1라운드: 초보자전 (1vs1 PvP)",
                    "• 2라운드: 야수와의 대결",
                    "• 3라운드: 팀 서바이벌",
                    "• 결승전: 현 챔피언과의 대결",
                    "",
                    "목표:",
                    "• 투기장 관리인과 대화",
                    "• 입장료 500골드 지불",
                    "• 장비 준비",
                    "• 각 라운드 승리",
                    "• 현 챔피언 격파",
                    "• 챔피언 벨트 획득",
                    "",
                    "보상:",
                    "• 골드 5000",
                    "• 다이아몬드 40개",
                    "• 다이아몬드 검",
                    "• 황금 사과 5개",
                    "• 챔피언 트로피",
                    "• 경험치 3000"
            );
        } else {
            return Arrays.asList(
                    "Prove your combat skills in the arena!",
                    "Fight against other gladiators and become the champion.",
                    "",
                    "⚔️ Arena Rules:",
                    "• 500 gold entry fee required",
                    "• Must pass each round to proceed",
                    "• Death means quest failure",
                    "• Contains PvP zones",
                    "",
                    "Round Structure:",
                    "• Round 1: Novice Match (1vs1 PvP)",
                    "• Round 2: Beast Battle",
                    "• Round 3: Team Survival",
                    "• Finals: Champion Duel",
                    "",
                    "Objectives:",
                    "• Talk to the Arena Master",
                    "• Pay 500 gold entry fee",
                    "• Prepare equipment",
                    "• Win each round",
                    "• Defeat the current champion",
                    "• Obtain the Champion Belt",
                    "",
                    "Rewards:",
                    "• 5000 Gold",
                    "• 40 Diamonds",
                    "• Diamond Sword",
                    "• 5 Golden Apples",
                    "• Champion Trophy",
                    "• 3000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "arena_master" -> isKorean ? "투기장 관리인과 대화" : "Talk to the Arena Master";
            case "entry_fee" -> isKorean ? "입장료 500골드 지불" : "Pay 500 gold entry fee";
            case "prepare_armor" -> isKorean ? "철 흉갑 준비" : "Prepare Iron Chestplate";
            case "prepare_weapon" -> isKorean ? "철 검 준비" : "Prepare Iron Sword";
            case "enter_arena" -> isKorean ? "투기장 입장" : "Enter the Gladiator Arena";
            case "round1_pvp" -> isKorean ? "1라운드: 검투사 1명 처치" : "Round 1: Defeat 1 gladiator";
            case "round1_token" -> isKorean ? "1라운드 승리 토큰 획득" : "Obtain Round 1 victory token";
            case "round2_wolves" -> isKorean ? "2라운드: 늑대 5마리 처치" : "Round 2: Kill 5 wolves";
            case "round2_bears" -> isKorean ? "2라운드: 곰 3마리 처치" : "Round 2: Kill 3 bears";
            case "round2_token" -> isKorean ? "2라운드 승리 토큰 획득" : "Obtain Round 2 victory token";
            case "round3_team" -> isKorean ? "3라운드: 적 팀원 3명 처치" : "Round 3: Defeat 3 enemy team members";
            case "round3_survive" -> isKorean ? "3라운드: 5분간 생존" : "Round 3: Survive for 5 minutes";
            case "round3_token" -> isKorean ? "3라운드 승리 토큰 획득" : "Obtain Round 3 victory token";
            case "challenge_champion" -> isKorean ? "현 챔피언에게 도전" : "Challenge the current champion";
            case "defeat_champion" -> isKorean ? "챔피언 격파" : "Defeat the champion";
            case "champion_belt" -> isKorean ? "챔피언 벨트 획득" : "Obtain the Champion Belt";
            case "claim_victory" -> isKorean ? "승리 신고" : "Claim victory";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("arena_gladiator_dialog");

        dialog.addLine("투기장 관리인",
                "오호! 새로운 도전자인가? 투기장에서 명예를 얻고 싶다면 제대로 찾아왔네.",
                "Oh! A new challenger? You've come to the right place if you want glory in the arena.");

        dialog.addLine("투기장 관리인",
                "하지만 쉽지 않을 거야. 많은 이들이 도전했지만 살아남은 자는 적지.",
                "But it won't be easy. Many have challenged, but few have survived.");

        dialog.addLine("플레이어",
                "준비되었습니다. 무엇부터 시작해야 하나요?",
                "I'm ready. Where do I start?");

        dialog.addLine("투기장 관리인",
                "먼저 입장료 500골드를 내야 해. 그리고 제대로 된 장비도 준비하고.",
                "First, pay the 500 gold entry fee. And prepare proper equipment.");

        dialog.addLine("투기장 관리인",
                "4개의 라운드를 모두 통과하면 현 챔피언과 대결할 자격을 얻게 될 거야.",
                "Pass all four rounds and you'll earn the right to face the current champion.");

        dialog.addLine("플레이어",
                "챔피언을 이기면 무엇을 얻나요?",
                "What do I get for beating the champion?");

        dialog.addLine("투기장 관리인",
                "챔피언 벨트와 막대한 상금, 그리고 영원한 명예지! 도전할 텐가?",
                "The Champion Belt, a huge prize, and eternal glory! Will you challenge?");

        return dialog;
    }
}