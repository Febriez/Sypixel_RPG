package com.febrie.rpg.talent;

import com.febrie.rpg.dto.TalentDTO;
import com.febrie.rpg.stat.Stat;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 특성(Talent) 시스템
 * 플레이어가 학습할 수 있는 특수 능력
 *
 * @author Febrie, CoffeeTory
 */
public class Talent {

    private static final Map<String, Talent> REGISTRY = new HashMap<>();

    // 기본 특성들 (예시)
    public static final Talent STRENGTH_MASTERY = register(new Talent(
            "strength_mastery", Material.IRON_SWORD, 5,
            level -> Map.of(Stat.STRENGTH, level * 3)
    ));

    public static final Talent INTELLIGENCE_MASTERY = register(new Talent(
            "intelligence_mastery", Material.BOOK, 5,
            level -> Map.of(Stat.INTELLIGENCE, level * 3)
    ));

    public static final Talent VITALITY_MASTERY = register(new Talent(
            "vitality_mastery", Material.GOLDEN_APPLE, 5,
            level -> Map.of(Stat.VITALITY, level * 2)
    ));

    private final String id;
    private final Material icon;
    private final int maxLevel;
    private final StatBonusProvider statBonusProvider;
    private final List<Talent> children;
    private final String pageId;

    /**
     * 특성 생성자
     */
    public Talent(@NotNull String id, @NotNull Material icon, int maxLevel,
                  @NotNull StatBonusProvider statBonusProvider) {
        this(id, icon, maxLevel, statBonusProvider, List.of(), "main");
    }

    /**
     * 특성 생성자 (하위 페이지 포함)
     */
    public Talent(@NotNull String id, @NotNull Material icon, int maxLevel,
                  @NotNull StatBonusProvider statBonusProvider,
                  @NotNull List<Talent> children, @NotNull String pageId) {
        this.id = id;
        this.icon = icon;
        this.maxLevel = maxLevel;
        this.statBonusProvider = statBonusProvider;
        this.children = children;
        this.pageId = pageId;
    }

    /**
     * 특성 등록
     */
    private static Talent register(@NotNull Talent talent) {
        REGISTRY.put(talent.getId(), talent);
        return talent;
    }

    /**
     * ID로 특성 가져오기
     */
    @Nullable
    public static Talent getById(@NotNull String id) {
        return REGISTRY.get(id);
    }

    /**
     * 모든 특성 가져오기
     */
    @NotNull
    public static List<Talent> getAllTalents() {
        return List.copyOf(REGISTRY.values());
    }

    /**
     * 스탯 보너스 계산
     */
    @NotNull
    public Map<Stat, Integer> getStatBonuses(int level) {
        if (level <= 0 || level > maxLevel) {
            return Map.of();
        }
        return statBonusProvider.getStatBonuses(level);
    }

    /**
     * 레벨업 가능 여부 확인
     */
    public boolean canLevelUp(@NotNull TalentHolder holder) {
        int currentLevel = holder.getTalentLevel(this);
        return currentLevel < maxLevel && holder.getAvailablePoints() > 0;
    }

    /**
     * 특성 활성화 가능 여부
     */
    public boolean canActivate(@NotNull TalentHolder holder) {
        // 기본적으로 포인트만 있으면 활성화 가능
        // 나중에 전제 조건 등을 추가할 수 있음
        return holder.getAvailablePoints() > 0;
    }

    /**
     * 레벨업
     */
    public boolean levelUp(@NotNull TalentHolder holder) {
        if (!canLevelUp(holder)) {
            return false;
        }

        int currentLevel = holder.getTalentLevel(this);
        holder.setTalentLevel(this, currentLevel + 1);
        holder.usePoint();
        return true;
    }

    /**
     * 하위 페이지 존재 여부
     */
    public boolean hasSubPage() {
        return !children.isEmpty();
    }

    // Getters
    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public Material getIcon() {
        return icon;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    @NotNull
    public List<Talent> getChildren() {
        return children;
    }

    @NotNull
    public String getPageId() {
        return pageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Talent talent = (Talent) o;
        return id.equals(talent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * 스탯 보너스 제공자 인터페이스
     */
    @FunctionalInterface
    public interface StatBonusProvider {
        @NotNull
        Map<Stat, Integer> getStatBonuses(int level);
    }

    /**
     * 특성 보유자 (플레이어의 특성 정보)
     */
    public static class TalentHolder {

        private int availablePoints = 0;
        private int spentPoints = 0;
        private final Map<Talent, Integer> talentLevels = new HashMap<>();

        public TalentHolder() {
            // 기본 생성자
        }

        /**
         * DTO로 변환
         */
        @NotNull
        public TalentDTO toDTO() {
            // 배운 특성들을 Map<String, Integer>로 변환
            Map<String, Integer> learnedTalentsMap = new HashMap<>();
            for (Map.Entry<Talent, Integer> entry : talentLevels.entrySet()) {
                learnedTalentsMap.put(entry.getKey().getId(), entry.getValue());
            }

            // TalentDTO는 record이므로 생성자로 생성
            return new TalentDTO(availablePoints, learnedTalentsMap);
        }

        /**
         * DTO에서 데이터 적용
         */
        public void applyFromDTO(@NotNull TalentDTO dto) {
            // 사용 가능한 포인트 설정
            this.availablePoints = dto.availablePoints();

            // 배운 특성들 설정
            this.talentLevels.clear();
            this.spentPoints = 0;
            Map<String, Integer> learnedTalentsMap = dto.learnedTalents();

            for (Map.Entry<String, Integer> entry : learnedTalentsMap.entrySet()) {
                try {
                    Talent talent = Talent.getById(entry.getKey());
                    if (talent != null) {
                        this.talentLevels.put(talent, entry.getValue());
                        this.spentPoints += entry.getValue(); // 사용된 포인트 계산
                    }
                } catch (Exception e) {
                    // 알 수 없는 특성 ID는 무시
                    LogUtil.warning("Unknown talent ID: " + entry.getKey());
                }
            }
        }

        /**
         * 모든 스탯 보너스 계산
         */
        public Map<Stat, Integer> calculateStatBonuses() {
            Map<Stat, Integer> totalBonuses = new HashMap<>();

            for (Map.Entry<Talent, Integer> entry : talentLevels.entrySet()) {
                Talent talent = entry.getKey();
                int level = entry.getValue();

                if (level > 0) {
                    Map<Stat, Integer> talentBonuses = talent.getStatBonuses(level);
                    for (Map.Entry<Stat, Integer> bonusEntry : talentBonuses.entrySet()) {
                        totalBonuses.merge(bonusEntry.getKey(), bonusEntry.getValue(), Integer::sum);
                    }
                }
            }

            return totalBonuses;
        }

        /**
         * 특성 레벨 가져오기
         */
        public int getTalentLevel(@NotNull Talent talent) {
            return talentLevels.getOrDefault(talent, 0);
        }

        /**
         * 특성 레벨 설정
         */
        public void setTalentLevel(@NotNull Talent talent, int level) {
            if (level <= 0) {
                talentLevels.remove(talent);
            } else {
                talentLevels.put(talent, Math.min(level, talent.getMaxLevel()));
            }
        }

        /**
         * 포인트 사용
         */
        public void usePoint() {
            if (availablePoints > 0) {
                availablePoints--;
                spentPoints++;
            }
        }

        /**
         * 포인트 추가
         */
        public void addPoints(int points) {
            this.availablePoints += points;
        }

        // Getters
        public int getAvailablePoints() {
            return availablePoints;
        }

        public int getSpentPoints() {
            return spentPoints;
        }

        public Map<Talent, Integer> getAllTalents() {
            return new HashMap<>(talentLevels);
        }
    }
}