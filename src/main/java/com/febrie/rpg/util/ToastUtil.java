package com.febrie.rpg.util;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 퀘스트 진행도 Toast 알림 유틸리티
 * UltimateAdvancementAPI를 사용하여 퀘스트 진행 상황을 표시
 */
@SuppressWarnings("deprecation") // UltimateAdvancementAPI uses Bungee ChatColor
public class ToastUtil {

    private static final UltimateAdvancementAPI api = UltimateAdvancementAPI.getInstance(RPGMain.getPlugin());
    private static final LangManager langManager = RPGMain.getPlugin().getLangManager();

    /**
     * 퀘스트 진행도 Toast 표시
     *
     * @param player   플레이어
     * @param quest    퀘스트
     * @param progress 퀘스트 진행도
     */
    public static void showQuestProgressToast(@NotNull Player player, @NotNull Quest quest, QuestProgress progress) {
        boolean isKorean = player.locale().getLanguage().equals("ko");
        String questName = quest.getDisplayName(isKorean);

        // 진행도 정보 생성
        List<String> progressInfo = new ArrayList<>();
        List<QuestObjective> objectives = quest.getObjectives();

        for (QuestObjective objective : objectives) {
            ObjectiveProgress objProgress = progress.getObjectiveProgress(objective.getId());
            if (objProgress != null) {
                String objDesc = quest.getObjectiveDescription(objective, isKorean);
                String progressText = objProgress.isCompleted()
                        ? "✓ " + objDesc
                        : String.format("• %s (%d/%d)", objDesc, objProgress.getCurrentValue(), objProgress.getRequiredAmount());
                progressInfo.add(progressText);
            }
        }

        // 전체 진행률
        int percentage = progress.getCompletionPercentage();
        progressInfo.add("");
        progressInfo.add(String.format("%s: %d%%", langManager.getMessage(player, "quest.total-progress"), percentage));

        // Toast 표시
        AdvancementDisplay display = new AdvancementDisplay.Builder(Material.WRITABLE_BOOK, questName)
                .description(String.join("\n", progressInfo))
                .frame(AdvancementFrameType.TASK)
                .defaultColor(net.md_5.bungee.api.ChatColor.AQUA)
                .build();

        api.displayCustomToast(player, display);
    }

    /**
     * 퀘스트 목표 완료 Toast 표시
     *
     * @param player    플레이어
     * @param quest     퀘스트
     * @param objective 완료된 목표
     */
    public static void showObjectiveCompleteToast(@NotNull Player player, @NotNull Quest quest, QuestObjective objective) {
        boolean isKorean = player.locale().getLanguage().equals("ko");
        String questName = quest.getDisplayName(isKorean);
        String objectiveDesc = quest.getObjectiveDescription(objective, isKorean);

        // Toast 표시 - 제목: 퀘스트 이름, 내용: 목표 완료 메시지
        AdvancementDisplay display = new AdvancementDisplay.Builder(Material.EMERALD, questName)
                .description(objectiveDesc + langManager.getMessage(player, "quest.objective-achieved"))
                .frame(AdvancementFrameType.TASK)
                .defaultColor(net.md_5.bungee.api.ChatColor.GREEN)
                .build();

        api.displayCustomToast(player, display);
    }

    /**
     * 퀘스트 전체 완료 Toast 표시
     *
     * @param player 플레이어
     * @param quest  완료된 퀘스트
     */
    public static void showQuestCompleteToast(@NotNull Player player, @NotNull Quest quest) {
        boolean isKorean = player.locale().getLanguage().equals("ko");
        String questName = quest.getDisplayName(isKorean);
        String description = langManager.getMessage(player, "quest.quest-completed-toast");

        // Toast 표시
        AdvancementDisplay display = new AdvancementDisplay.Builder(Material.NETHER_STAR, questName)
                .description(description)
                .frame(AdvancementFrameType.CHALLENGE)
                .defaultColor(net.md_5.bungee.api.ChatColor.GOLD)
                .build();

        api.displayCustomToast(player, display);
    }
}