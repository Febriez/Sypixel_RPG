package com.febrie.rpg.quest.impl.main;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.KillPlayerObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 어둠의 길 - 선택 퀘스트 (악)
 * 빛의 길과 양자택일
 *
 * @author Febrie
 */
public class PathOfDarknessQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class PathOfDarknessBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new PathOfDarknessQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public PathOfDarknessQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private PathOfDarknessQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static Builder createBuilder() {
        return new PathOfDarknessBuilder()
                .id(QuestID.MAIN_PATH_OF_DARKNESS)
                .objectives(Arrays.asList(
                        // 1. 마을 주민 처치 (어둠의 길...)
                        new KillMobObjective("eliminate_villagers", EntityType.VILLAGER, 10),

                        // 2. 플레이어 처치 (PvP)
                        new KillPlayerObjective("dominate_players", 5),

                        // 3. 위더 스켈레톤 처치하여 재료 수집
                        new KillMobObjective("hunt_wither_skeletons", EntityType.WITHER_SKELETON, 20),

                        // 4. 어둠의 아이템 제작
                        new CraftItemObjective("craft_tnt", Material.TNT, 10)
                ))
                .reward(BasicReward.builder()
                        .addCurrency(CurrencyType.GOLD, 1500)  // 더 많은 골드
                        .addCurrency(CurrencyType.GHAST_TEAR, 50)  // 특별 재화
                        .addItem(new ItemStack(Material.WITHER_SKELETON_SKULL, 3))  // 위더 소환 재료
                        .addItem(new ItemStack(Material.NETHERITE_SWORD))  // 네더라이트 검
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 5))  // 인챈트된 황금사과
                        .addExperience(2500)
                        .setDescriptionKey("quest.main.path_of_darkness.reward.description")
                        .build())
                .sequential(false)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(20)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_HEROES_JOURNEY)  // 영웅의 여정 완료 필요
                .addExclusive(QuestID.MAIN_PATH_OF_LIGHT);  // 빛의 길과 양자택일
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "✦ 어둠의 길" : "✦ Path of Darkness";
    }

    @Override
    public @NotNull List<String> getDescription(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "힘과 지배의 길을 걸어갑니다.",
                    "어둠의 힘을 받아들이세요.",
                    "",
                    "⚠ 이 퀘스트를 선택하면",
                    "빛의 길은 선택할 수 없습니다!",
                    "",
                    "목표:",
                    "• 마을 주민 10명 제거",
                    "• 플레이어 5명 처치",
                    "• 위더 스켈레톤 20마리 사냥",
                    "• TNT 10개 제작",
                    "",
                    "보상:",
                    "• 골드 1,500",
                    "• 가스트의 눈물 50개",
                    "• 위더 스켈레톤 머리 3개",
                    "• 네더라이트 검",
                    "• 인챈트된 황금사과 5개",
                    "• 경험치 2,500"
            );
        } else {
            return Arrays.asList(
                    "Walk the path of power and domination.",
                    "Embrace the power of darkness.",
                    "",
                    "⚠ If you choose this quest,",
                    "Path of Light cannot be selected!",
                    "",
                    "Objectives:",
                    "• Eliminate 10 villagers",
                    "• Kill 5 players",
                    "• Hunt 20 wither skeletons",
                    "• Craft 10 TNT",
                    "",
                    "Rewards:",
                    "• 1,500 Gold",
                    "• 50 Ghast Tears",
                    "• 3 Wither Skeleton Skulls",
                    "• Netherite Sword",
                    "• 5 Enchanted Golden Apples",
                    "• 2,500 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "eliminate_villagers" -> isKorean ? "마을 주민 10명 제거" : "Eliminate 10 villagers";
            case "dominate_players" -> isKorean ? "플레이어 5명 처치" : "Kill 5 players";
            case "hunt_wither_skeletons" -> isKorean ? "위더 스켈레톤 20마리 사냥" : "Hunt 20 wither skeletons";
            case "craft_tnt" -> isKorean ? "TNT 10개 제작" : "Craft 10 TNT";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("path_of_darkness_dialog");

        dialog.addLine("어둠의 대사제",
                "힘을 원하는가? 진정한 힘은 어둠 속에 있다.",
                "Do you seek power? True power lies in darkness.");

        dialog.addLine("어둠의 대사제",
                "약한 자들을 지배하고, 이 세상의 진정한 주인이 되어라.",
                "Dominate the weak and become the true master of this world.");

        dialog.addLine("어둠의 대사제",
                "경고하건대, 이 길을 선택하면 모두가 너의 적이 될 것이다.",
                "Be warned, if you choose this path, everyone will become your enemy.");

        return dialog;
    }
}