package com.febrie.rpg.dto;

/**
 * 플레이어 진행도 정보 DTO
 * Firebase 저장용
 *
 * @author Febrie, CoffeeTory
 */
public class ProgressDTO {

    private int currentLevel = 1;
    private long totalExperience = 0;
    private double levelProgress = 0.0;

    // 전투 통계
    private long mobsKilled = 0;
    private long playersKilled = 0;
    private long deaths = 0;

    // 퀘스트 진행도 (나중에 추가)
    private int questsCompleted = 0;
    private int achievementsUnlocked = 0;

    public ProgressDTO() {
        // 기본 생성자
    }

    // Getters and Setters
    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = Math.max(1, currentLevel);
    }

    public long getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(long totalExperience) {
        this.totalExperience = Math.max(0, totalExperience);
    }

    public double getLevelProgress() {
        return levelProgress;
    }

    public void setLevelProgress(double levelProgress) {
        this.levelProgress = Math.max(0.0, Math.min(1.0, levelProgress));
    }

    public long getMobsKilled() {
        return mobsKilled;
    }

    public void setMobsKilled(long mobsKilled) {
        this.mobsKilled = Math.max(0, mobsKilled);
    }

    public long getPlayersKilled() {
        return playersKilled;
    }

    public void setPlayersKilled(long playersKilled) {
        this.playersKilled = Math.max(0, playersKilled);
    }

    public long getDeaths() {
        return deaths;
    }

    public void setDeaths(long deaths) {
        this.deaths = Math.max(0, deaths);
    }

    public int getQuestsCompleted() {
        return questsCompleted;
    }

    public void setQuestsCompleted(int questsCompleted) {
        this.questsCompleted = Math.max(0, questsCompleted);
    }

    public int getAchievementsUnlocked() {
        return achievementsUnlocked;
    }

    public void setAchievementsUnlocked(int achievementsUnlocked) {
        this.achievementsUnlocked = Math.max(0, achievementsUnlocked);
    }

    /**
     * 킬 증가
     */
    public void incrementMobKills() {
        this.mobsKilled++;
    }

    public void incrementPlayerKills() {
        this.playersKilled++;
    }

    public void incrementDeaths() {
        this.deaths++;
    }
}