package com.febrie.rpg.quest.impl.main.chapter1;

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
 * 첫 번째 시험 - 메인 퀘스트 Chapter 1
 * 선택받은 자로서의 첫 번째 대규모 시험
 *
 * @author Febrie
 */
public class FirstTrialQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class FirstTrialBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new FirstTrialQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public FirstTrialQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private FirstTrialQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new FirstTrialBuilder()
                .id(QuestID.MAIN_FIRST_TRIAL)
                .objectives(Arrays.asList(
                        // 준비 단계
                        new InteractNPCObjective("meet_trainer", 102), // 시련의 훈련관
                        new CollectItemObjective("gather_potions", Material.POTION, 10),
                        new CraftItemObjective("craft_shield", Material.SHIELD, 1),
                        
                        // 시련의 경기장 입장
                        new VisitLocationObjective("enter_arena", "trial_arena"),
                        
                        // 1차 웨이브 - 언데드
                        new SurviveObjective("survive_wave1", 180), // 3분
                        new KillMobObjective("wave1_zombies", EntityType.ZOMBIE, 20),
                        new KillMobObjective("wave1_skeletons", EntityType.SKELETON, 15),
                        
                        // 2차 웨이브 - 거미와 크리퍼
                        new SurviveObjective("survive_wave2", 180), // 3분
                        new KillMobObjective("wave2_spiders", EntityType.SPIDER, 15),
                        new KillMobObjective("wave2_creepers", EntityType.CREEPER, 10),
                        
                        // 3차 웨이브 - 보스전
                        new KillMobObjective("boss_fight", EntityType.RAVAGER, 1),
                        new CollectItemObjective("trial_medal", Material.GOLD_INGOT, 1),
                        
                        // 완료
                        new DeliverItemObjective("return_medal", "현자 도란", Material.GOLD_INGOT, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3000)
                        .addCurrency(CurrencyType.DIAMOND, 30)
                        .addItem(new ItemStack(Material.DIAMOND_CHESTPLATE))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING))
                        .addExperience(3000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(20)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_CHOSEN_ONE);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "첫 번째 시험: 생존의 경기장" : "First Trial: Arena of Survival";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "선택받은 자로서의 첫 번째 대규모 시험입니다.",
                    "시련의 경기장에서 세 차례의 몬스터 웨이브를 막아내세요.",
                    "",
                    "경기장에서는 다음과 같은 시험이 기다립니다:",
                    "• 1차 웨이브: 언데드 군단",
                    "• 2차 웨이브: 거미와 폭발의 위협",
                    "• 3차 웨이브: 파괴자와의 결전",
                    "",
                    "철저한 준비가 생존의 열쇠입니다!",
                    "",
                    "목표:",
                    "• 시련의 훈련관과 대화",
                    "• 포션 10개 준비",
                    "• 방패 1개 제작",
                    "• 시련의 경기장 입장",
                    "• 1차 웨이브 3분간 생존",
                    "• 좀비 20마리, 스켈레톤 15마리 처치",
                    "• 2차 웨이브 3분간 생존",
                    "• 거미 15마리, 크리퍼 10마리 처치",
                    "• 파괴자(보스) 처치",
                    "• 시련의 메달 획득",
                    "• 훈련관에게 메달 전달",
                    "",
                    "보상:",
                    "• 골드 3000",
                    "• 다이아몬드 30개",
                    "• 다이아몬드 흉갑",
                    "• 불사의 토템",
                    "• 경험치 3000"
            );
        } else {
            return Arrays.asList(
                    "Your first major trial as the chosen one.",
                    "Survive three waves of monsters in the Trial Arena.",
                    "",
                    "The arena holds these challenges:",
                    "• Wave 1: Undead Legion",
                    "• Wave 2: Spiders and Explosive Threats",
                    "• Wave 3: Battle with the Ravager",
                    "",
                    "Thorough preparation is the key to survival!",
                    "",
                    "Objectives:",
                    "• Talk to the Trial Instructor",
                    "• Prepare 10 potions",
                    "• Craft 1 shield",
                    "• Enter the Trial Arena",
                    "• Survive Wave 1 for 3 minutes",
                    "• Kill 20 zombies, 15 skeletons",
                    "• Survive Wave 2 for 3 minutes",
                    "• Kill 15 spiders, 10 creepers",
                    "• Defeat the Ravager (boss)",
                    "• Obtain the Trial Medal",
                    "• Return medal to instructor",
                    "",
                    "Rewards:",
                    "• 3000 Gold",
                    "• 30 Diamonds",
                    "• Diamond Chestplate",
                    "• Totem of Undying",
                    "• 3000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "meet_trainer" -> isKorean ? "시련의 훈련관과 대화" : "Talk to the Trial Instructor";
            case "gather_potions" -> isKorean ? "포션 10개 준비" : "Prepare 10 potions";
            case "craft_shield" -> isKorean ? "방패 제작" : "Craft a shield";
            case "enter_arena" -> isKorean ? "시련의 경기장 입장" : "Enter the Trial Arena";
            case "survive_wave1" -> isKorean ? "1차 웨이브 3분간 생존" : "Survive Wave 1 for 3 minutes";
            case "wave1_zombies" -> isKorean ? "좀비 20마리 처치" : "Kill 20 zombies";
            case "wave1_skeletons" -> isKorean ? "스켈레톤 15마리 처치" : "Kill 15 skeletons";
            case "survive_wave2" -> isKorean ? "2차 웨이브 3분간 생존" : "Survive Wave 2 for 3 minutes";
            case "wave2_spiders" -> isKorean ? "거미 15마리 처치" : "Kill 15 spiders";
            case "wave2_creepers" -> isKorean ? "크리퍼 10마리 처치" : "Kill 10 creepers";
            case "boss_fight" -> isKorean ? "파괴자 처치" : "Defeat the Ravager";
            case "trial_medal" -> isKorean ? "시련의 메달 획득" : "Obtain the Trial Medal";
            case "return_medal" -> isKorean ? "훈련관에게 메달 전달" : "Return medal to instructor";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("first_trial_dialog");

        dialog.addLine("시련의 훈련관",
                "선택받은 자의 문장을 가지고 있군. 대단하네!",
                "You carry the Emblem of the Chosen. Impressive!");

        dialog.addLine("시련의 훈련관",
                "하지만 진정한 시험은 이제부터야. 시련의 경기장에서 자네의 전투 실력을 증명해야 하네.",
                "But the real test begins now. You must prove your combat skills in the Trial Arena.");

        dialog.addLine("시련의 훈련관",
                "세 차례의 웨이브가 자네를 기다리고 있어. 각각이 이전보다 더 어려울 거야.",
                "Three waves await you. Each more difficult than the last.");

        dialog.addLine("플레이어",
                "어떤 준비가 필요한가요?",
                "What preparations do I need?");

        dialog.addLine("시련의 훈련관",
                "포션과 방패는 필수야. 특히 2차 웨이브의 크리퍼를 조심하게.",
                "Potions and a shield are essential. Be especially careful of the creepers in wave 2.");

        dialog.addLine("시련의 훈련관",
                "마지막 웨이브에서는 파괴자가 나타날 거야. 그 녀석을 쓰러뜨리면 시련의 메달을 얻게 될 거네.",
                "In the final wave, a Ravager will appear. Defeat it to earn the Trial Medal.");

        dialog.addLine("플레이어",
                "준비가 되었습니다. 시작하죠!",
                "I'm ready. Let's begin!");

        return dialog;
    }
}