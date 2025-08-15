package com.febrie.rpg.quest;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.reward.QuestReward;
import com.febrie.rpg.quest.reward.RewardDeliveryType;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;

/**
 * 퀘스트 기본 추상 클래스
 * 모든 퀘스트가 상속받아야 하는 기본 클래스
 *
 * @author Febrie
 */
public abstract class Quest {

    protected final QuestID id;
    protected final String instanceId;
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
    protected final int completionLimit;
    
    // 보상 지급 방식
    protected final RewardDeliveryType rewardDeliveryType;
    
    // 선행 퀘스트 및 양자택일 퀘스트
    protected final Set<QuestID> prerequisiteQuests;
    protected final Set<QuestID> exclusiveQuests;

    /**
     * QuestBuilder를 통한 생성자
     *
     * @param builder QuestBuilder 인스턴스
     */
    protected Quest(@NotNull QuestBuilder builder) {
        this.id = builder.id;
        this.instanceId = UUID.randomUUID().toString();
        this.objectives = new ArrayList<>(builder.objectives);
        this.reward = builder.reward;
        this.sequential = builder.sequential;
        this.repeatable = builder.repeatable;
        this.daily = builder.daily;
        this.weekly = builder.weekly;
        this.minLevel = builder.minLevel;
        this.maxLevel = builder.maxLevel;
        this.category = builder.category;
        this.completionLimit = builder.completionLimit;
        this.rewardDeliveryType = builder.rewardDeliveryType;
        this.prerequisiteQuests = new HashSet<>(builder.prerequisiteQuests);
        this.exclusiveQuests = new HashSet<>(builder.exclusiveQuests);
    }

    /**
     * 빈 목표 리스트로 퀘스트 생성
     *
     * @param id       퀘스트 ID
     * @param reward   퀘스트 보상
     * @param category 퀘스트 카테고리
     */
    protected Quest(@NotNull QuestID id, @NotNull QuestReward reward, @NotNull QuestCategory category) {
        this.id = id;
        this.instanceId = UUID.randomUUID().toString();
        this.objectives = new ArrayList<>();
        this.reward = reward;
        this.sequential = false;
        this.repeatable = false;
        this.daily = false;
        this.weekly = false;
        this.minLevel = 1;
        this.maxLevel = 0;
        this.category = category;
        this.completionLimit = 1;
        this.rewardDeliveryType = RewardDeliveryType.INSTANT;
        this.prerequisiteQuests = new HashSet<>();
        this.exclusiveQuests = new HashSet<>();
    }

    /**
     * 퀘스트 ID 반환
     */
    public @NotNull QuestID getId() {
        return id;
    }

    /**
     * 퀘스트 카테고리 반환
     */
    public @NotNull QuestCategory getCategory() {
        return category;
    }

    /**
     * 퀘스트 목표 리스트 반환
     */
    public @NotNull List<QuestObjective> getObjectives() {
        return new ArrayList<>(objectives);
    }

    /**
     * 퀘스트 보상 반환
     */
    public @NotNull QuestReward getReward() {
        return reward;
    }

    /**
     * 퀘스트 인스턴스 ID 반환
     */
    public @NotNull String getInstanceId() {
        return instanceId;
    }

    /**
     * 퀘스트 표시 이름
     *
     * @param who 대상 플레이어
     * @return 퀘스트 이름
     */
    public abstract @NotNull Component getDisplayName(@NotNull Player who);

    /**
     * 퀘스트 기본 설명
     *
     * @param who 대상 플레이어
     * @return 퀘스트 기본 설명 (여러 줄)
     */
    public abstract @NotNull List<Component> getDisplayInfo(@NotNull Player who);

    /**
     * 퀘스트 목표 설명
     *
     * @param who 대상 플레이어
     * @return 퀘스트 목표 설명 (여러 줄)
     */
    public @NotNull List<Component> getGoalDescription(@NotNull Player who) {
        List<Component> goals = new ArrayList<>();
        goals.add(LangManager.getMessage(who, "quest.goals"));
        
        for (QuestObjective objective : objectives) {
            Component bullet = Component.text("• ", ColorUtil.WHITE);
            goals.add(bullet.append(getObjectiveDescription(objective, who)));
        }
        
        return goals;
    }

    /**
     * 퀘스트 보상 설명
     *
     * @param who 대상 플레이어
     * @return 퀘스트 보상 설명 (여러 줄)
     */
    public @NotNull List<Component> getRewardDescription(@NotNull Player who) {
        List<Component> rewards = new ArrayList<>();
        rewards.add(LangManager.getMessage(who, "quest.rewards"));
        
        // 보상 정보는 QuestReward의 getDisplayInfo를 사용
        Component rewardInfo = getRewardDisplayInfo(who);
        rewards.add(Component.text("• ", ColorUtil.WHITE).append(rewardInfo));
        
        return rewards;
    }

    /**
     * 퀘스트 전체 설명 (기본 설명 + 목표 + 보상)
     *
     * @param who 대상 플레이어
     * @return 퀘스트 전체 설명 (여러 줄)
     */
    public @NotNull List<Component> getDescription(@NotNull Player who) {

        // 기본 설명
        List<Component> description = new ArrayList<>(getDisplayInfo(who));
        description.add(Component.empty());
        
        // 목표 설명
        description.addAll(getGoalDescription(who));
        description.add(Component.empty());
        
        // 보상 설명
        description.addAll(getRewardDescription(who));
        
        return description;
    }

    /**
     * 퀘스트 정보를 책으로 표시
     *
     * @param player 대상 플레이어
     */
    public void displayBook(@NotNull Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta == null) return;
        
        meta.setTitle("§6RPG 퀘스트 안내서");
        meta.setAuthor("§f시스템");

        List<Component> pages = new ArrayList<>();

        // 첫 페이지 - 퀘스트 기본 정보
        Component page1 = Component.text("=== ", NamedTextColor.DARK_GRAY)
                .append(getDisplayName(player).color(ColorUtil.GOLD).decoration(TextDecoration.BOLD, true))
                .append(Component.text(" ===", NamedTextColor.DARK_GRAY))
                .append(Component.newline()).append(Component.newline());

        for (Component line : getDisplayInfo(player)) {
            page1 = page1.append(line.color(ColorUtil.GRAY))
                    .append(Component.newline());
        }

        page1 = page1.append(Component.newline())
                .append(LangManager.getMessage(player, "quest.category-label").color(ColorUtil.YELLOW))
                .append(getCategoryName(player).color(ColorUtil.WHITE))
                .append(Component.newline())
                .append(LangManager.getMessage(player, "quest.level-requirement").color(ColorUtil.YELLOW))
                .append(Component.text(minLevel + (maxLevel > 0 ? "-" + maxLevel : "+"), ColorUtil.WHITE));

        pages.add(page1);

        // 두 번째 페이지 - 목표
        Component page2 = LangManager.getMessage(player, "quest.quest-objectives").color(ColorUtil.GOLD).decoration(TextDecoration.BOLD, true)
                .append(Component.newline()).append(Component.newline());

        int index = 1;
        for (QuestObjective objective : objectives) {
            Component objectiveText = getObjectiveDescription(objective, player);
            page2 = page2.append(Component.text(index + ". ", ColorUtil.YELLOW))
                    .append(objectiveText.color(ColorUtil.WHITE))
                    .append(Component.newline());
            index++;
        }

        if (sequential) {
            page2 = page2.append(Component.newline())
                    .append(LangManager.getMessage(player, "quest.sequential-note").color(ColorUtil.RED));
        }

        pages.add(page2);

        // 세 번째 페이지 - 보상
        Component page3 = LangManager.getMessage(player, "quest.quest-rewards").color(ColorUtil.EMERALD).decoration(TextDecoration.BOLD, true)
                .append(Component.newline()).append(Component.newline());

        // 보상 정보 표시
        page3 = page3.append(getRewardDisplayInfo(player));

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
    private @NotNull Component getRewardDisplayInfo(@NotNull Player player) {
        // QuestReward의 getDisplayInfo 메소드 사용
        return reward.getDisplayInfo(player);
    }

    /**
     * 목표 설명 가져오기
     */
    public abstract @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who);

    /**
     * 카테고리 이름 가져오기
     */
    public @NotNull Component getCategoryName(@NotNull Player who) {
        return LangManager.getMessage(who, "quest.categories." + category.name().toLowerCase());
    }

    /**
     * 퀘스트 대화 (없으면 null) - 기존 호환성
     *
     * @return 퀘스트 대화
     */
    @Nullable
    public QuestDialog getDialog() {
        return null;
    }

    /**
     * 플레이어별 대화 (레거시 - 빈 구현)
     *
     * @param player 플레이어
     * @return null
     */
    @Nullable
    public QuestDialog getDialog(@NotNull Player player) {
        return getDialog();
    }
    
    /**
     * 대화 개수 반환 (기본 구현)
     * @return 대화 개수
     */
    public int getDialogCount() {
        QuestDialog dialog = getDialog();
        return dialog != null ? dialog.getDialogues().size() : 0;
    }
    
    /**
     * 특정 인덱스의 대화 반환
     * @param index 대화 인덱스
     * @param player 플레이어
     * @return 대화 텍스트
     */
    @Nullable
    public String getDialog(int index, @NotNull Player player) {
        QuestDialog dialog = getDialog();
        if (dialog != null && index >= 0 && index < dialog.getDialogues().size()) {
            return dialog.getDialogues().get(index);
        }
        return null;
    }
    
    /**
     * NPC 이름 반환
     * @param player 플레이어
     * @return NPC 이름
     */
    @NotNull
    public String getNPCName(@NotNull Player player) {
        QuestDialog dialog = getDialog();
        return dialog != null && dialog.getNpcName() != null ? dialog.getNpcName() : "Quest NPC";
    }
    
    /**
     * 퀘스트 수락 대화 반환
     * @param player 플레이어
     * @return 수락 대화
     */
    @NotNull
    public String getAcceptDialog(@NotNull Player player) {
        return LangManager.getString(player, "quest.dialog.accept-default");
    }
    
    /**
     * 퀘스트 거절 대화 반환
     * @param player 플레이어
     * @return 거절 대화
     */
    @NotNull
    public String getDeclineDialog(@NotNull Player player) {
        return LangManager.getString(player, "quest.dialog.decline-default");
    }

    /**
     * 플레이어가 시작 가능한지 확인
     *
     * @param player 플레이어
     * @return 시작 가능 여부
     */
    public boolean canStart(@NotNull Player player) {
        // 레벨 확인
        int level = player.getLevel();
        if (level < minLevel) return false;
        if (maxLevel > 0 && level > maxLevel) return false;

        return true;
    }

    /**
     * 순차 진행 여부
     */
    public boolean isSequential() {
        return sequential;
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
     * 최소 레벨
     */
    public int getMinLevel() {
        return minLevel;
    }

    /**
     * 최대 레벨
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * 하루 제한 횟수
     */
    public int getCompletionLimit() {
        return completionLimit;
    }

    /**
     * 보상 지급 방식
     */
    public @NotNull RewardDeliveryType getRewardDeliveryType() {
        return rewardDeliveryType;
    }

    /**
     * 퀘스트 진행 상황 업데이트
     * 퀘스트 목표별로 진행 상황을 업데이트
     *
     * @param progress 퀘스트 진행 상황
     * @param objective 목표
     * @param amount 증가량
     * @return 업데이트 성공 여부
     */
    public boolean updateProgress(@NotNull QuestProgress progress, @NotNull QuestObjective objective, int amount) {
        if (progress.isCompleted()) {
            return false;
        }

        ObjectiveProgress objProgress = progress.getObjectiveProgress(objective.getId());
        if (objProgress == null) {
            return false;
        }

        // 순차 진행인 경우 이전 목표가 완료되었는지 확인
        if (sequential) {
            int currentIndex = objectives.indexOf(objective);
            if (currentIndex > 0) {
                for (int i = 0; i < currentIndex; i++) {
                    ObjectiveProgress prevProgress = progress.getObjectiveProgress(objectives.get(i).getId());
                    if (prevProgress == null || !prevProgress.isCompleted()) {
                        return false;
                    }
                }
            }
        }

        objProgress.increment(amount);
        
        // 모든 목표가 완료되었는지 확인
        boolean allCompleted = true;
        for (QuestObjective obj : objectives) {
            ObjectiveProgress p = progress.getObjectiveProgress(obj.getId());
            if (p == null || !p.isCompleted()) {
                allCompleted = false;
                break;
            }
        }

        if (allCompleted) {
            progress.setState(QuestProgress.QuestState.COMPLETED);
            progress.setCompletedAt(Instant.now());
        }

        return true;
    }

    /**
     * 퀘스트 진행 상황 생성
     *
     * @return 새로운 퀘스트 진행 상황
     */
    public @NotNull QuestProgress createProgress(@NotNull UUID playerId) {
        Map<String, ObjectiveProgress> objectiveProgressMap = new HashMap<>();
        for (QuestObjective objective : objectives) {
            objectiveProgressMap.put(objective.getId(), new ObjectiveProgress(objective.getId(), playerId, objective.getRequiredAmount()));
        }
        return new QuestProgress(id, playerId, objectiveProgressMap);
    }
    
    /**
     * 선행 퀘스트가 모두 완료되었는지 확인
     * @param completedQuests 완료된 퀘스트 목록
     * @return 선행 퀘스트 모두 완료 여부
     */
    public boolean arePrerequisitesComplete(@NotNull Set<QuestID> completedQuests) {
        for (QuestID prerequisite : prerequisiteQuests) {
            if (!completedQuests.contains(prerequisite)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 양자택일 퀘스트 중 완료된 것이 있는지 확인
     * @param completedQuests 완료된 퀘스트 목록
     * @return 양자택일 퀘스트 완료 여부
     */
    public boolean hasCompletedExclusiveQuests(@NotNull Set<QuestID> completedQuests) {
        for (QuestID exclusive : exclusiveQuests) {
            if (completedQuests.contains(exclusive)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quest quest = (Quest) o;
        return Objects.equals(instanceId, quest.instanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId);
    }
}