package com.febrie.rpg.level;

import com.febrie.rpg.job.JobType;
import org.jetbrains.annotations.NotNull;

/**
 * RPG 레벨 시스템
 * 직업별로 다른 경험치 곡선을 사용하여 만렙 직전 레벨에서 2억 경험치가 필요하도록 설계
 *
 * @author Febrie, CoffeeTory
 */
public class LevelSystem {

    // 2억 경험치 상수
    private static final long MAX_LEVEL_EXP = 200_000_000L;

    // 기본 경험치 (1->2 레벨)
    private static final int BASE_EXP = 500;

    /**
     * 특정 레벨에 도달하기 위해 필요한 경험치 계산
     *
     * @param level   목표 레벨
     * @param jobType 직업 타입
     * @return 필요 경험치
     */
    public static long getExpForLevel(int level, @NotNull JobType jobType) {
        if (level <= 1) return 0;
        if (level > jobType.getMaxLevel()) return Long.MAX_VALUE;

        int maxLevel = jobType.getMaxLevel();

        // 만렙 직전 레벨에서 2억 경험치 필요
        if (level == maxLevel) {
            return MAX_LEVEL_EXP;
        }

        // 직업별 경험치 계수 계산
        double power = getPowerForJob(jobType);
        double base = getBaseForJob(jobType);

        // 경험치 공식: base * (level-1)^power
        return (long) (base * Math.pow(level - 1, power));
    }

    /**
     * 누적 경험치로부터 현재 레벨 계산
     *
     * @param totalExp 총 경험치
     * @param jobType  직업 타입
     * @return 현재 레벨
     */
    public static int getLevelFromExp(long totalExp, @NotNull JobType jobType) {
        int maxLevel = jobType.getMaxLevel();

        // 이진 탐색으로 레벨 찾기
        int left = 1;
        int right = maxLevel;
        int result = 1;

        while (left <= right) {
            int mid = (left + right) / 2;
            long requiredExp = getTotalExpForLevel(mid, jobType);

            if (requiredExp <= totalExp) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    /**
     * 특정 레벨까지의 누적 경험치 계산
     *
     * @param level   목표 레벨
     * @param jobType 직업 타입
     * @return 누적 경험치
     */
    public static long getTotalExpForLevel(int level, @NotNull JobType jobType) {
        if (level <= 1) return 0;

        long total = 0;
        for (int i = 2; i <= level; i++) {
            total += getExpForLevel(i, jobType);
        }

        return total;
    }

    /**
     * 현재 레벨에서의 진행도 계산 (0.0 ~ 1.0)
     *
     * @param totalExp 총 경험치
     * @param jobType  직업 타입
     * @return 진행도
     */
    public static double getLevelProgress(long totalExp, @NotNull JobType jobType) {
        int currentLevel = getLevelFromExp(totalExp, jobType);
        if (currentLevel >= jobType.getMaxLevel()) return 1.0;

        long currentLevelTotalExp = getTotalExpForLevel(currentLevel, jobType);
        long nextLevelTotalExp = getTotalExpForLevel(currentLevel + 1, jobType);

        long currentLevelExp = totalExp - currentLevelTotalExp;
        long requiredExp = nextLevelTotalExp - currentLevelTotalExp;

        return (double) currentLevelExp / requiredExp;
    }

    /**
     * 다음 레벨까지 남은 경험치 계산
     *
     * @param totalExp 총 경험치
     * @param jobType  직업 타입
     * @return 남은 경험치
     */
    public static long getExpToNextLevel(long totalExp, @NotNull JobType jobType) {
        int currentLevel = getLevelFromExp(totalExp, jobType);
        if (currentLevel >= jobType.getMaxLevel()) return 0;

        long nextLevelTotalExp = getTotalExpForLevel(currentLevel + 1, jobType);
        return nextLevelTotalExp - totalExp;
    }

    /**
     * 직업별 power 계수 반환
     * 만렙 직전 레벨에서 2억이 되도록 조정된 값
     */
    private static double getPowerForJob(@NotNull JobType jobType) {
        return switch (jobType.getCategory()) {
            case WARRIOR -> 2.85;  // 200레벨 기준
            case MAGE -> 3.05;     // 120레벨 기준
            case ARCHER -> 3.15;   // 100레벨 기준
        };
    }

    /**
     * 직업별 base 계수 반환
     */
    private static double getBaseForJob(@NotNull JobType jobType) {
        int maxLevel = jobType.getMaxLevel();
        double power = getPowerForJob(jobType);

        // base = MAX_LEVEL_EXP / (maxLevel-1)^power
        return MAX_LEVEL_EXP / Math.pow(maxLevel - 1, power);
    }

    /**
     * 레벨업에 필요한 경험치 정보
     */
    public record LevelInfo(int level, long expRequired, long totalExpRequired, double progress) {
    }

    /**
     * 플레이어의 현재 레벨 정보 가져오기
     */
    public static LevelInfo getLevelInfo(long totalExp, @NotNull JobType jobType) {
        int level = getLevelFromExp(totalExp, jobType);
        long expRequired = getExpForLevel(level + 1, jobType);
        long totalExpRequired = getTotalExpForLevel(level + 1, jobType);
        double progress = getLevelProgress(totalExp, jobType);

        return new LevelInfo(level, expRequired, totalExpRequired, progress);
    }
}