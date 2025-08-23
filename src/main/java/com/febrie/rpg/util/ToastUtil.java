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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
        String questName = extractPlainText(questNameComp);

        // 진행도 정보 생성
        List<String> progressInfo = new ArrayList<>();
        List<QuestObjective> objectives = quest.getObjectives();

        for (QuestObjective objective : objectives) {
            ObjectiveProgress objProgress = progress.getObjectiveProgress(objective.getId());
            if (objProgress != null) {
                Component objDescComp = quest.getObjectiveDescription(objective, player);
                String objDesc = extractPlainText(objDescComp);
                String progressText = objProgress.isCompleted() ? "✓ " + objDesc : String.format("• %s (%d/%d)", objDesc, objProgress.getCurrentValue(), objProgress.getRequiredAmount());
                progressInfo.add(progressText);
            }
        }

        // 전체 진행률
        int percentage = progress.getCompletionPercentage();
        progressInfo.add("");
        Component progressComp = Component.translatable("quest.total-progress");
        String progressText = extractPlainText(progressComp);
        progressInfo.add(String.format("%s: %d%%", progressText, percentage));

        // Toast 표시
        AdvancementDisplay display = new AdvancementDisplay.Builder(Material.WRITABLE_BOOK, questName).description(String.join("\n", progressInfo))
                .frame(AdvancementFrameType.TASK).defaultColor(toBungeeColor(NamedTextColor.AQUA)).build();

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
        String questName = extractPlainText(questNameComp);
        Component objectiveDescComp = quest.getObjectiveDescription(objective, player);
        String objectiveDesc = extractPlainText(objectiveDescComp);

        // Toast 표시 - 제목: 퀘스트 이름, 내용: 목표 완료 메시지
        Component achievedComp = Component.translatable("quest.objective-achieved");
        String achievedText = extractPlainText(achievedComp);
        AdvancementDisplay display = new AdvancementDisplay.Builder(Material.EMERALD, questName).description(objectiveDesc + achievedText)
                .frame(AdvancementFrameType.TASK).defaultColor(toBungeeColor(NamedTextColor.GREEN)).build();

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
        String questName = extractPlainText(questNameComp);
        Component descComp = Component.translatable("quest.quest-completed-toast");
        String description = extractPlainText(descComp);

        // Toast 표시
        AdvancementDisplay display = new AdvancementDisplay.Builder(Material.NETHER_STAR, questName).description(description)
                .frame(AdvancementFrameType.CHALLENGE).defaultColor(toBungeeColor(NamedTextColor.GOLD)).build();

        api.displayCustomToast(player, display);
    }
    
    /**
     * Component에서 순수 텍스트 추출
     * PlainTextComponentSerializer를 사용하지 않고 직접 처리
     */
    private static String extractPlainText(@NotNull Component component) {
        // 간단한 구현 - Component의 content를 직접 가져옴
        // 더 복잡한 Component의 경우 재귀적으로 처리 필요
        StringBuilder result = new StringBuilder();
        appendComponentText(component, result);
        return result.toString();
    }
    
    private static void appendComponentText(Component component, StringBuilder builder) {
        // Component의 컨텐츠 추가
        if (component instanceof net.kyori.adventure.text.TextComponent textComp) {
            builder.append(textComp.content());
        } else if (component instanceof net.kyori.adventure.text.TranslatableComponent transComp) {
            // 번역 키는 그대로 사용 (실제 번역은 클라이언트에서 처리)
            builder.append(transComp.key());
        }
        
        // 자식 컴포넌트들 처리
        for (Component child : component.children()) {
            appendComponentText(child, builder);
        }
    }
    
    /**
     * Adventure API TextColor를 Bungee ChatColor로 변환
     * UltimateAdvancementAPI 호환성을 위한 변환 메소드
     * deprecated API 사용이 불가피하여 SuppressWarnings 적용
     */
    @SuppressWarnings("deprecation")
    private static net.md_5.bungee.api.ChatColor toBungeeColor(TextColor color) {
        // Adventure TextColor를 Bungee ChatColor로 변환
        if (color.equals(NamedTextColor.AQUA)) return net.md_5.bungee.api.ChatColor.AQUA;
        if (color.equals(NamedTextColor.GREEN)) return net.md_5.bungee.api.ChatColor.GREEN;
        if (color.equals(NamedTextColor.GOLD)) return net.md_5.bungee.api.ChatColor.GOLD;
        if (color.equals(NamedTextColor.RED)) return net.md_5.bungee.api.ChatColor.RED;
        if (color.equals(NamedTextColor.YELLOW)) return net.md_5.bungee.api.ChatColor.YELLOW;
        if (color.equals(NamedTextColor.BLUE)) return net.md_5.bungee.api.ChatColor.BLUE;
        if (color.equals(NamedTextColor.DARK_PURPLE)) return net.md_5.bungee.api.ChatColor.DARK_PURPLE;
        if (color.equals(NamedTextColor.LIGHT_PURPLE)) return net.md_5.bungee.api.ChatColor.LIGHT_PURPLE;
        // 기본값
        return net.md_5.bungee.api.ChatColor.WHITE;
    }
}