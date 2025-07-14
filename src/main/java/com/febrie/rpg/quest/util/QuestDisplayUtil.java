package com.febrie.rpg.quest.util;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.impl.main.HeroesJourneyQuest;
import com.febrie.rpg.quest.impl.main.PathOfDarknessQuest;
import com.febrie.rpg.quest.impl.main.PathOfLightQuest;
import com.febrie.rpg.quest.manager.QuestManager;
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
     * 퀘스트 이름 가져오기 (Component)
     */
    public Component getQuestName(@NotNull Player player, @NotNull Quest quest) {
        return langManager.getComponent(player, quest.getNameKey());
    }

    /**
     * 퀘스트 이름을 문자열로 반환
     */
    public String getQuestNameAsString(@NotNull Player player, @NotNull Quest quest) {
        return langManager.getMessage(player, quest.getNameKey());
    }

    /**
     * 퀘스트 설명 가져오기 (리스트)
     */
    public List<Component> getQuestDescriptionList(@NotNull Player player, @NotNull Quest quest) {
        return langManager.getComponentList(player, quest.getDescriptionKey());
    }

    /**
     * 퀘스트 설명 가져오기 (단일 Component)
     */
    public Component getQuestDescription(@NotNull Player player, @NotNull Quest quest) {
        List<Component> descList = getQuestDescriptionList(player, quest);
        if (descList.isEmpty()) {
            return Component.text("No description available", NamedTextColor.GRAY);
        }

        // 여러 줄인 경우 줄바꿈으로 연결
        Component result = descList.get(0);
        for (int i = 1; i < descList.size(); i++) {
            result = result.append(Component.newline()).append(descList.get(i));
        }
        return result;
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
     * 퀘스트 카테고리 이름 가져오기 (String)
     */
    public String getQuestCategoryAsString(@NotNull Player player, @NotNull Quest.QuestCategory category) {
        return langManager.getMessage(player, category.getTranslationKey());
    }

    /**
     * 퀘스트 카테고리 가져오기 (Component)
     */
    public Component getQuestCategory(@NotNull Player player, @NotNull Quest quest) {
        // Quest 클래스에 getCategory() 메소드가 있다고 가정
        // 없다면 퀘스트 타입에 따라 기본 카테고리 반환
        String categoryKey = "quest.category.main"; // 기본값

        // 퀘스트 타입에 따른 카테고리 설정
        if (quest instanceof HeroesJourneyQuest ||
                quest instanceof PathOfLightQuest ||
                quest instanceof PathOfDarknessQuest) {
            categoryKey = "quest.category.main";
        }
        // 다른 퀘스트 타입들은 여기에 추가

        return langManager.getComponent(player, categoryKey);
    }

    /**
     * 퀘스트 난이도 표시
     */
    public Component getQuestDifficulty(@NotNull Player player, @NotNull Quest quest) {
        // Quest.QuestDifficulty가 구현되어 있다면 사용
        // 없다면 퀘스트별로 하드코딩된 난이도 사용
        String difficultyKey = "quest.difficulty.normal"; // 기본값

        if (quest instanceof HeroesJourneyQuest) {
            difficultyKey = "quest.difficulty.easy";
        } else if (quest instanceof PathOfLightQuest || quest instanceof PathOfDarknessQuest) {
            difficultyKey = "quest.difficulty.hard";
        }
        // 다른 퀘스트들의 난이도는 여기에 추가

        return langManager.getComponent(player, difficultyKey);
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

                // 선행 퀘스트 가져오기
                Quest prereqQuest = QuestManager.getInstance().getQuest(prereqId);
                Component prereqName;

                if (prereqQuest != null) {
                    prereqName = Component.text("- ", NamedTextColor.GRAY)
                            .append(getQuestName(player, prereqQuest));
                } else {
                    prereqName = Component.text("- " + prereqId, NamedTextColor.GRAY);
                }

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
                // 양자택일 퀘스트 가져오기
                Quest exclusiveQuest = QuestManager.getInstance().getQuest(exclusiveId);
                Component warning;

                if (exclusiveQuest != null) {
                    warning = Component.text("- ", NamedTextColor.GRAY)
                            .append(getQuestName(player, exclusiveQuest));
                } else {
                    warning = Component.text("- " + exclusiveId, NamedTextColor.GRAY);
                }

                lines.add(warning);
            }
        }

        return lines;
    }

    /**
     * 퀘스트 진행도 바 생성
     */
    public Component createProgressBar(double progress, int length) {
        int filled = (int) (progress * length);
        int empty = length - filled;

        Component bar = Component.text("[", NamedTextColor.GRAY);

        // 채워진 부분
        if (filled > 0) {
            bar = bar.append(Component.text("=".repeat(filled), NamedTextColor.GREEN));
        }

        // 빈 부분
        if (empty > 0) {
            bar = bar.append(Component.text("-".repeat(empty), NamedTextColor.DARK_GRAY));
        }

        bar = bar.append(Component.text("]", NamedTextColor.GRAY));

        return bar;
    }

    /**
     * 퀘스트 완료 시간 포맷
     */
    public Component formatQuestDuration(long durationMillis) {
        long seconds = durationMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return Component.text(days + "일 " + (hours % 24) + "시간", NamedTextColor.GRAY);
        } else if (hours > 0) {
            return Component.text(hours + "시간 " + (minutes % 60) + "분", NamedTextColor.GRAY);
        } else if (minutes > 0) {
            return Component.text(minutes + "분 " + (seconds % 60) + "초", NamedTextColor.GRAY);
        } else {
            return Component.text(seconds + "초", NamedTextColor.GRAY);
        }
    }
}