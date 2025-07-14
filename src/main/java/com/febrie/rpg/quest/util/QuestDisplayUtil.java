package com.febrie.rpg.quest.util;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 퀘스트 정보 표시를 위한 유틸리티 클래스
 * LangManager와 연동하여 퀘스트 정보를 표시
 *
 * @author Febrie
 */
public class QuestDisplayUtil {

    private final LangManager langManager;

    public QuestDisplayUtil() {
        this.langManager = RPGMain.getPlugin().getLangManager();
    }

    /**
     * 퀘스트 이름 가져오기
     */
    public Component getQuestName(@NotNull Player player, @NotNull Quest quest) {
        return langManager.getComponent(player, quest.getNameKey());
    }

    /**
     * 퀘스트 설명 가져오기
     */
    public List<Component> getQuestDescription(@NotNull Player player, @NotNull Quest quest) {
        return langManager.getComponentList(player, quest.getDescriptionKey());
    }

    /**
     * 목표 설명 가져오기
     */
    public Component getObjectiveDescription(@NotNull Player player, @NotNull QuestObjective objective) {
        String[] placeholders = objective.getDescriptionPlaceholders();

        // 특별 처리가 필요한 플레이스홀더 변환
        List<String> processedPlaceholders = new ArrayList<>();

        for (int i = 0; i < placeholders.length; i += 2) {
            String key = placeholders[i];
            String value = placeholders[i + 1];

            // 엔티티 타입 번역 키 처리
            if (key.equals("mob_key")) {
                // 마인크래프트 번역 키는 그대로 사용
                Component mobName = Component.translatable(value);
                processedPlaceholders.add("mob");
                processedPlaceholders.add(mobName.toString());
            }
            // 아이템 타입 번역 키 처리
            else if (key.equals("item_key")) {
                Component itemName = Component.translatable(value);
                processedPlaceholders.add("item");
                processedPlaceholders.add(itemName.toString());
            }
            // 블록 타입 번역 키 처리
            else if (key.equals("block_key")) {
                Component blockName = Component.translatable(value);
                processedPlaceholders.add("block");
                processedPlaceholders.add(blockName.toString());
            }
            // 일반 플레이스홀더
            else {
                processedPlaceholders.add(key);
                processedPlaceholders.add(value);
            }
        }

        return langManager.getComponent(player, objective.getDescriptionKey(),
                processedPlaceholders.toArray(new String[0]));
    }

    /**
     * 퀘스트 카테고리 이름 가져오기
     */
    public String getQuestCategory(@NotNull Player player, @NotNull Quest.QuestCategory category) {
        return langManager.getMessage(player, category.getTranslationKey());
    }

    /**
     * 퀘스트 상태 표시
     */
    public Component getQuestStatus(@NotNull Player player, @NotNull Quest quest, boolean isCompleted, boolean isActive) {
        if (isCompleted) {
            return langManager.getComponent(player, "quest.state.completed")
                    .color(NamedTextColor.GREEN);
        } else if (isActive) {
            return langManager.getComponent(player, "quest.state.active")
                    .color(NamedTextColor.YELLOW);
        } else {
            return langManager.getComponent(player, "quest.state.not_started")
                    .color(NamedTextColor.GRAY);
        }
    }

    /**
     * 선행 퀘스트 정보 표시
     */
    public List<Component> getPrerequisiteInfo(@NotNull Player player, @NotNull Quest quest, @NotNull List<String> completedQuests) {
        List<Component> lines = new ArrayList<>();

        if (quest.hasPrerequisiteQuests()) {
            lines.add(langManager.getComponent(player, "quest.prerequisite.header")
                    .color(NamedTextColor.GOLD));

            for (String prereqId : quest.getPrerequisiteQuests()) {
                boolean completed = completedQuests.contains(prereqId);
                Component status = completed
                        ? Component.text(" ✓").color(NamedTextColor.GREEN)
                        : Component.text(" ✗").color(NamedTextColor.RED);

                // 선행 퀘스트 이름은 해당 퀘스트의 nameKey를 사용해야 함
                // 여기서는 예시로 ID를 표시
                Component prereqName = Component.text("- " + prereqId).color(NamedTextColor.GRAY);
                lines.add(prereqName.append(status));
            }
        }

        return lines;
    }

    /**
     * 양자택일 퀘스트 경고 표시
     */
    public List<Component> getExclusiveWarning(@NotNull Player player, @NotNull Quest quest) {
        List<Component> lines = new ArrayList<>();

        if (quest.hasExclusiveQuests()) {
            lines.add(langManager.getComponent(player, "quest.exclusive.warning")
                    .color(NamedTextColor.RED));

            for (String exclusiveId : quest.getExclusiveQuests()) {
                Component warning = Component.text("- " + exclusiveId)
                        .color(NamedTextColor.GRAY);
                lines.add(warning);
            }
        }

        return lines;
    }
}