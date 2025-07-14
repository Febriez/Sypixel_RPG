package com.febrie.rpg.quest;

import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.reward.QuestReward;
import com.febrie.rpg.util.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 퀘스트 기본 추상 클래스
 * 모든 퀘스트가 상속받아야 하는 기본 클래스
 *
 * @author Febrie
 */
public abstract class Quest {

    protected final QuestID id;
    protected final List<QuestObjective> objectives;
    protected final QuestReward reward;

    // 퀘스트 속성
    protected final boolean sequential;
    protected final boolean repeatable;
    protected final boolean daily;
    protected final boolean weekly;
    protected final int minLevel;
    protected final int maxLevel;
    protected final QuestCategory category;

    // 선행 퀘스트 시스템
    private final Set<QuestID> prerequisiteQuests = new HashSet<>();

    // 양자택일 퀘스트 시스템
    private final Set<QuestID> exclusiveQuests = new HashSet<>();

    /**
     * 빌더를 통한 생성자
     */
    protected Quest(@NotNull Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "Quest ID cannot be null");
        this.objectives = new ArrayList<>(builder.objectives);
        this.reward = Objects.requireNonNull(builder.reward, "Quest reward cannot be null");

        this.sequential = builder.sequential;
        this.repeatable = builder.repeatable;
        this.daily = builder.daily;
        this.weekly = builder.weekly;
        this.minLevel = builder.minLevel;
        this.maxLevel = builder.maxLevel;
        this.category = builder.category;

        this.prerequisiteQuests.addAll(builder.prerequisiteQuests);
        this.exclusiveQuests.addAll(builder.exclusiveQuests);

        if (objectives.isEmpty()) {
            throw new IllegalArgumentException("Quest must have at least one objective");
        }
    }

    /**
     * 퀘스트 ID 반환
     */
    public @NotNull QuestID getId() {
        return id;
    }

    /**
     * 퀘스트 표시 이름 (하드코딩)
     *
     * @param isKorean 한국어 여부
     * @return 퀘스트 이름
     */
    public abstract @NotNull String getDisplayName(boolean isKorean);

    /**
     * 퀘스트 설명 (하드코딩)
     *
     * @param isKorean 한국어 여부
     * @return 퀘스트 설명 (여러 줄)
     */
    public abstract @NotNull List<String> getDescription(boolean isKorean);

    /**
     * 퀘스트 정보를 책으로 표시
     *
     * @param player 대상 플레이어
     */
    public void displayBook(@NotNull Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta == null) return;

        boolean isKorean = player.locale().getLanguage().equals("ko");

        meta.setTitle("§6RPG 퀘스트 안내서");
        meta.setAuthor("§f시스템");

        List<Component> pages = new ArrayList<>();

        // 첫 페이지 - 퀘스트 기본 정보
        Component page1 = Component.text("=== ", NamedTextColor.DARK_GRAY)
                .append(Component.text(getDisplayName(isKorean), ColorUtil.GOLD, TextDecoration.BOLD))
                .append(Component.text(" ===", NamedTextColor.DARK_GRAY))
                .append(Component.newline()).append(Component.newline());

        for (String line : getDescription(isKorean)) {
            page1 = page1.append(Component.text(line, ColorUtil.GRAY))
                    .append(Component.newline());
        }

        page1 = page1.append(Component.newline())
                .append(Component.text("카테고리: ", ColorUtil.YELLOW))
                .append(Component.text(getCategoryName(isKorean), ColorUtil.WHITE))
                .append(Component.newline())
                .append(Component.text("레벨 요구사항: ", ColorUtil.YELLOW))
                .append(Component.text(minLevel + (maxLevel > 0 ? "-" + maxLevel : "+"), ColorUtil.WHITE));

        pages.add(page1);

        // 두 번째 페이지 - 목표
        Component page2 = Component.text("=== 퀘스트 목표 ===", ColorUtil.GOLD, TextDecoration.BOLD)
                .append(Component.newline()).append(Component.newline());

        int index = 1;
        for (QuestObjective objective : objectives) {
            String objectiveText = getObjectiveDescription(objective, isKorean);
            page2 = page2.append(Component.text(index + ". ", ColorUtil.YELLOW))
                    .append(Component.text(objectiveText, ColorUtil.WHITE))
                    .append(Component.newline());
            index++;
        }

        if (sequential) {
            page2 = page2.append(Component.newline())
                    .append(Component.text("※ 순서대로 진행해야 합니다.", ColorUtil.RED));
        }

        pages.add(page2);

        // 세 번째 페이지 - 보상
        Component page3 = Component.text("=== 퀘스트 보상 ===", ColorUtil.EMERALD, TextDecoration.BOLD)
                .append(Component.newline()).append(Component.newline());

        // 보상 정보 표시
        page3 = page3.append(getRewardDisplayInfo(player, isKorean));

        pages.add(page3);

        // 페이지 설정
        meta.pages(pages);
        book.setItemMeta(meta);

        // 책 열기
        player.openBook(book);
    }

    /**
     * 보상 정보를 표시용 Component로 변환
     */
    private @NotNull Component getRewardDisplayInfo(@NotNull Player player, boolean isKorean) {
        // QuestReward의 getDisplayInfo 메소드 사용
        return reward.getDisplayInfo(player);
    }

    /**
     * 목표 설명 가져오기 (하드코딩용)
     */
    protected abstract @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean);

    /**
     * 카테고리 이름 가져오기
     */
    private @NotNull String getCategoryName(boolean isKorean) {
        return switch (category) {
            case MAIN -> isKorean ? "메인 퀘스트" : "Main Quest";
            case SIDE -> isKorean ? "사이드 퀘스트" : "Side Quest";
            case DAILY -> isKorean ? "일일 퀘스트" : "Daily Quest";
            case WEEKLY -> isKorean ? "주간 퀘스트" : "Weekly Quest";
            case EVENT -> isKorean ? "이벤트 퀘스트" : "Event Quest";
            case TUTORIAL -> isKorean ? "튜토리얼" : "Tutorial";
            case NORMAL -> isKorean ? "일반 퀘스트" : "Normal Quest";
        };
    }

    /**
     * 퀘스트 대화 (없으면 null)
     *
     * @return 퀘스트 대화
     */
    @Nullable
    public QuestDialog getDialog() {
        return null; // 기본적으로 대화 없음, 필요한 퀘스트만 오버라이드
    }

    /**
     * 퀘스트 목표 목록 반환
     */
    public @NotNull List<QuestObjective> getObjectives() {
        return new ArrayList<>(objectives);
    }

    /**
     * 순차적 진행 여부
     */
    public boolean isSequential() {
        return sequential;
    }

    /**
     * 퀘스트 보상
     */
    public @NotNull QuestReward getReward() {
        return reward;
    }

    /**
     * 퀘스트 시작 가능 여부 확인 (하위 클래스에서 추가 조건 구현 가능)
     */
    public boolean canStart(@NotNull UUID playerId) {
        return true;
    }

    /**
     * 최소 레벨 요구사항
     */
    public int getMinLevel() {
        return minLevel;
    }

    /**
     * 최대 레벨 요구사항
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * 선행 퀘스트 목록
     */
    public @NotNull Set<QuestID> getPrerequisiteQuests() {
        return new HashSet<>(prerequisiteQuests);
    }

    /**
     * 양자택일 퀘스트 목록
     */
    public @NotNull Set<QuestID> getExclusiveQuests() {
        return new HashSet<>(exclusiveQuests);
    }

    /**
     * 선행 퀘스트 확인
     */
    public boolean hasPrerequisiteQuests() {
        return !prerequisiteQuests.isEmpty();
    }

    /**
     * 양자택일 퀘스트 확인
     */
    public boolean hasExclusiveQuests() {
        return !exclusiveQuests.isEmpty();
    }

    /**
     * 선행 퀘스트 완료 확인
     */
    public boolean arePrerequisitesComplete(@NotNull Collection<QuestID> completedQuests) {
        return completedQuests.containsAll(prerequisiteQuests);
    }

    /**
     * 양자택일 퀘스트 완료 확인
     */
    public boolean hasCompletedExclusiveQuests(@NotNull Collection<QuestID> completedQuests) {
        for (QuestID exclusiveQuest : exclusiveQuests) {
            if (completedQuests.contains(exclusiveQuest)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 퀘스트 카테고리
     */
    public @NotNull QuestCategory getCategory() {
        return category;
    }

    /**
     * 반복 가능 여부
     */
    public boolean isRepeatable() {
        return repeatable;
    }

    /**
     * 일일 퀘스트 여부
     */
    public boolean isDaily() {
        return daily;
    }

    /**
     * 주간 퀘스트 여부
     */
    public boolean isWeekly() {
        return weekly;
    }

    /**
     * 퀘스트 진행도 생성
     */
    public @NotNull QuestProgress createProgress(@NotNull UUID playerId) {
        Map<String, ObjectiveProgress> objectives = new HashMap<>();

        for (QuestObjective objective : this.objectives) {
            objectives.put(objective.getId(),
                    new ObjectiveProgress(objective.getId(), playerId, objective.getRequiredAmount()));
        }

        return new QuestProgress(id, playerId, objectives);
    }

    /**
     * 퀘스트 카테고리
     */
    public enum QuestCategory {
        MAIN("quest.category.main"),
        SIDE("quest.category.side"),
        DAILY("quest.category.daily"),
        WEEKLY("quest.category.weekly"),
        EVENT("quest.category.event"),
        TUTORIAL("quest.category.tutorial"),
        NORMAL("quest.category.normal");

        private final String translationKey;

        QuestCategory(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getTranslationKey() {
            return translationKey;
        }
    }

    /**
     * 퀘스트 빌더
     */
    public static abstract class Builder {
        protected QuestID id;
        protected List<QuestObjective> objectives = new ArrayList<>();
        protected QuestReward reward;
        protected boolean sequential = false;
        protected boolean repeatable = false;
        protected boolean daily = false;
        protected boolean weekly = false;
        protected int minLevel = 1;
        protected int maxLevel = 0;
        protected QuestCategory category = QuestCategory.NORMAL;
        protected Set<QuestID> prerequisiteQuests = new HashSet<>();
        protected Set<QuestID> exclusiveQuests = new HashSet<>();

        public Builder id(@NotNull QuestID id) {
            this.id = id;
            return this;
        }

        public Builder objectives(@NotNull List<QuestObjective> objectives) {
            this.objectives = new ArrayList<>(objectives);
            return this;
        }

        public Builder addObjective(@NotNull QuestObjective objective) {
            this.objectives.add(objective);
            return this;
        }

        public Builder reward(@NotNull QuestReward reward) {
            this.reward = reward;
            return this;
        }

        public Builder sequential(boolean sequential) {
            this.sequential = sequential;
            return this;
        }

        public Builder repeatable(boolean repeatable) {
            this.repeatable = repeatable;
            return this;
        }

        public Builder daily(boolean daily) {
            this.daily = daily;
            if (daily) {
                this.repeatable = true; // 일일 퀘스트는 자동으로 반복 가능
            }
            return this;
        }

        public Builder weekly(boolean weekly) {
            this.weekly = weekly;
            if (weekly) {
                this.repeatable = true; // 주간 퀘스트는 자동으로 반복 가능
            }
            return this;
        }

        public Builder minLevel(int minLevel) {
            this.minLevel = minLevel;
            return this;
        }

        public Builder maxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public Builder category(@NotNull QuestCategory category) {
            this.category = category;
            return this;
        }

        public Builder addPrerequisite(@NotNull QuestID questId) {
            this.prerequisiteQuests.add(questId);
            return this;
        }

        public Builder addExclusive(@NotNull QuestID questId) {
            this.exclusiveQuests.add(questId);
            return this;
        }

        public abstract Quest build();
    }
}