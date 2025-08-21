package com.febrie.rpg.util;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 퀘스트 진행도 Toast 알림 유틸리티
 * UltimateAdvancementAPI를 사용하여 퀘스트 진행 상황을 표시
 */
public class ToastUtil {

    private ToastUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final UltimateAdvancementAPI api = UltimateAdvancementAPI.getInstance(RPGMain.getPlugin());

    /**
     * 퀘스트 진행도 Toast 표시
     *
     * @param player   플레이어
     * @param quest    퀘스트
     * @param progress 퀘스트 진행도
     */
    public static void showQuestProgressToast(@NotNull Player player, @NotNull Quest quest, @NotNull QuestProgress progress) {
        Component questNameComp = quest.getDisplayName(player);
        String questName = PlainTextComponentSerializer.plainText().serialize(questNameComp);

        // 진행도 정보 생성
        List<String> progressInfo = new ArrayList<>();
        List<QuestObjective> objectives = quest.getObjectives();

        for (QuestObjective objective : objectives) {
            ObjectiveProgress objProgress = progress.getObjectiveProgress(objective.getId());
            if (objProgress != null) {
                Component objDescComp = quest.getObjectiveDescription(objective, player);
                String objDesc = PlainTextComponentSerializer.plainText().serialize(objDescComp);
                String progressText = objProgress.isCompleted() ? "✓ " + objDesc : String.format("• %s (%d/%d)", objDesc, objProgress.getCurrentValue(), objProgress.getRequiredAmount());
                progressInfo.add(progressText);
            }
        }

        // 전체 진행률
        int percentage = progress.getCompletionPercentage();
        progressInfo.add("");
        Component progressComp = Component.translatable("quest.total-progress");
        String progressText = PlainTextComponentSerializer.plainText().serialize(progressComp);
        progressInfo.add(String.format("%s: %d%%", progressText, percentage));

        // Toast 표시
        AdvancementDisplay display = new AdvancementDisplay.Builder(Material.WRITABLE_BOOK, questName).description(String.join("\n", progressInfo))
                .frame(AdvancementFrameType.TASK).defaultColor(net.md_5.bungee.api.ChatColor.AQUA).build();

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
        Component questNameComp = quest.getDisplayName(player);
        String questName = PlainTextComponentSerializer.plainText().serialize(questNameComp);
        Component objectiveDescComp = quest.getObjectiveDescription(objective, player);
        String objectiveDesc = PlainTextComponentSerializer.plainText().serialize(objectiveDescComp);

        // Toast 표시 - 제목: 퀘스트 이름, 내용: 목표 완료 메시지
        Component achievedComp = Component.translatable("quest.objective-achieved");
        String achievedText = PlainTextComponentSerializer.plainText().serialize(achievedComp);
        AdvancementDisplay display = new AdvancementDisplay.Builder(Material.EMERALD, questName).description(objectiveDesc + achievedText)
                .frame(AdvancementFrameType.TASK).defaultColor(net.md_5.bungee.api.ChatColor.GREEN).build();

        api.displayCustomToast(player, display);
    }

    /**
     * 퀘스트 전체 완료 Toast 표시
     *
     * @param player 플레이어
     * @param quest  완료된 퀘스트
     */
    public static void showQuestCompleteToast(@NotNull Player player, @NotNull Quest quest) {
        Component questNameComp = quest.getDisplayName(player);
        String questName = PlainTextComponentSerializer.plainText().serialize(questNameComp);
        Component descComp = Component.translatable("quest.quest-completed-toast");
        String description = PlainTextComponentSerializer.plainText().serialize(descComp);

        // Toast 표시
        AdvancementDisplay display = new AdvancementDisplay.Builder(Material.NETHER_STAR, questName).description(description)
                .frame(AdvancementFrameType.CHALLENGE).defaultColor(net.md_5.bungee.api.ChatColor.GOLD).build();

        api.displayCustomToast(player, display);
    }
}