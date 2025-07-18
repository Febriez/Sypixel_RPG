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
 * 선택받은 자 - 메인 퀘스트 Chapter 1
 * 영웅으로서의 자질을 시험받는 퀘스트
 *
 * @author Febrie
 */
public class ChosenOneQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class ChosenOneBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new ChosenOneQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public ChosenOneQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private ChosenOneQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new ChosenOneBuilder()
                .id(QuestID.MAIN_CHOSEN_ONE)
                .objectives(Arrays.asList(
                        // 시련의 동굴 입장
                        new VisitLocationObjective("enter_trial_cave", "trial_cave_entrance"),
                        
                        // 첫 번째 시련: 용기
                        new KillMobObjective("trial_courage", EntityType.IRON_GOLEM, 3),
                        new CollectItemObjective("courage_proof", Material.IRON_BLOCK, 1),
                        
                        // 두 번째 시련: 지혜
                        new BreakBlockObjective("solve_puzzle", Material.REDSTONE_LAMP, 5),
                        new CollectItemObjective("wisdom_proof", Material.EMERALD, 1),
                        
                        // 세 번째 시련: 희생
                        new PayCurrencyObjective("sacrifice_gold", 1000),
                        new CollectItemObjective("sacrifice_proof", Material.DIAMOND, 1),
                        
                        // 최종 시련
                        new KillMobObjective("final_guardian", EntityType.WITHER_SKELETON, 1),
                        new CollectItemObjective("chosen_emblem", Material.NETHER_STAR, 1),
                        
                        // 완료
                        new DeliverItemObjective("return_elder", 101, Material.NETHER_STAR, 1)
                ))
                .reward(BasicReward.builder()
                        .addCurrency(CurrencyType.GOLD, 2000)
                        .addCurrency(CurrencyType.DIAMOND, 20)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD))
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addExperience(2000)
                        .build())
                .sequential(true)  // 순차적으로 진행
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(15)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_ANCIENT_PROPHECY);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "선택받은 자의 시련" : "Trial of the Chosen One";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "예언은 당신이 선택받은 자임을 말합니다.",
                    "하지만 그 자격을 증명해야 합니다.",
                    "",
                    "세 가지 시련을 통과하고 진정한 영웅으로 거듭나세요:",
                    "• 용기의 시련 - 강력한 적과 맞서 싸우세요",
                    "• 지혜의 시련 - 고대의 퍼즐을 해결하세요",  
                    "• 희생의 시련 - 소중한 것을 기꺼이 포기하세요",
                    "",
                    "모든 시련은 순차적으로 진행됩니다.",
                    "",
                    "목표:",
                    "• 시련의 동굴 입장",
                    "• 용기의 수호자 3마리 처치",
                    "• 용기의 증표 획득",
                    "• 지혜의 퍼즐 해결 (레드스톤 램프 5개)",
                    "• 지혜의 증표 획득",
                    "• 희생의 제단에 1000골드 봉헌",
                    "• 희생의 증표 획득",
                    "• 최종 수호자 처치",
                    "• 선택받은 자의 문장 획득",
                    "• 고대의 장로에게 문장 전달",
                    "",
                    "보상:",
                    "• 골드 2000",
                    "• 다이아몬드 20개",
                    "• 네더라이트 검",
                    "• 겉날개",
                    "• 경험치 2000"
            );
        } else {
            return Arrays.asList(
                    "The prophecy says you are the chosen one.",
                    "But you must prove your worth.",
                    "",
                    "Pass three trials and become a true hero:",
                    "• Trial of Courage - Face powerful enemies",
                    "• Trial of Wisdom - Solve ancient puzzles",
                    "• Trial of Sacrifice - Willingly give up what's precious",
                    "",
                    "All trials must be completed sequentially.",
                    "",
                    "Objectives:",
                    "• Enter the Trial Cave",
                    "• Defeat 3 Guardians of Courage",
                    "• Obtain Proof of Courage",
                    "• Solve the Puzzle of Wisdom (5 Redstone Lamps)",
                    "• Obtain Proof of Wisdom",
                    "• Offer 1000 gold at the Altar of Sacrifice",
                    "• Obtain Proof of Sacrifice",
                    "• Defeat the Final Guardian",
                    "• Obtain the Emblem of the Chosen",
                    "• Deliver emblem to the Ancient Elder",
                    "",
                    "Rewards:",
                    "• 2000 Gold",
                    "• 20 Diamonds",
                    "• Netherite Sword",
                    "• Elytra",
                    "• 2000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "enter_trial_cave" -> isKorean ? "시련의 동굴 입장" : "Enter the Trial Cave";
            case "trial_courage" -> isKorean ? "용기의 수호자 3마리 처치" : "Defeat 3 Guardians of Courage";
            case "courage_proof" -> isKorean ? "용기의 증표 획득" : "Obtain Proof of Courage";
            case "solve_puzzle" -> isKorean ? "지혜의 퍼즐 해결" : "Solve the Puzzle of Wisdom";
            case "wisdom_proof" -> isKorean ? "지혜의 증표 획득" : "Obtain Proof of Wisdom";
            case "sacrifice_gold" -> isKorean ? "희생의 제단에 1000골드 봉헌" : "Offer 1000 gold at the Altar";
            case "sacrifice_proof" -> isKorean ? "희생의 증표 획득" : "Obtain Proof of Sacrifice";
            case "final_guardian" -> isKorean ? "최종 수호자 처치" : "Defeat the Final Guardian";
            case "chosen_emblem" -> isKorean ? "선택받은 자의 문장 획득" : "Obtain the Emblem of the Chosen";
            case "return_elder" -> isKorean ? "고대의 장로에게 문장 전달" : "Return emblem to the Elder";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("chosen_one_dialog");

        dialog.addLine("고대의 장로",
                "예언서를 해독했네. 자네가 바로 선택받은 자일지도 모르겠군.",
                "I've deciphered the prophecy. You might be the chosen one.");

        dialog.addLine("고대의 장로",
                "하지만 운명은 스스로 증명해야 하는 법. 시련의 동굴이 자네를 기다리고 있네.",
                "But destiny must be proven. The Trial Cave awaits you.");

        dialog.addLine("고대의 장로",
                "용기, 지혜, 그리고 희생. 이 세 가지가 진정한 영웅의 자질이라네.",
                "Courage, wisdom, and sacrifice. These are the qualities of a true hero.");

        dialog.addLine("고대의 장로",
                "모든 시련을 통과하고 선택받은 자의 문장을 가져온다면, 다음 단계를 알려주겠네.",
                "Pass all trials and bring me the Emblem of the Chosen, then I'll reveal the next step.");

        dialog.addLine("플레이어",
                "저는 준비되었습니다. 시련을 받아들이겠습니다.",
                "I am ready. I accept the trials.");

        dialog.addLine("고대의 장로",
                "그렇다면 가거라. 운명이 자네와 함께하기를.",
                "Then go. May destiny be with you.");

        return dialog;
    }
}