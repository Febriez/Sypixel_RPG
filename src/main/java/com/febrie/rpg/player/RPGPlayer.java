package com.febrie.rpg.player;

import com.febrie.rpg.economy.Wallet;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.level.LevelSystem;
import com.febrie.rpg.player.PlayerSettings;
import com.febrie.rpg.stat.Stat;
import com.febrie.rpg.talent.Talent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 플레이어의 RPG 데이터를 관리하는 클래스
 * 모든 데이터는 메모리에서 관리되며 Firebase를 통해서만 저장됨
 *
 * @author Febrie, CoffeeTory
 */
public class RPGPlayer {

    private final UUID playerId;
    private final Player bukkitPlayer;

    // RPG 데이터 (메모리에서만 관리)
    private JobType job;
    private long experience = 0;
    private int statPoints = 0;
    private final Stat.StatHolder stats = new Stat.StatHolder();
    private final Talent.TalentHolder talents = new Talent.TalentHolder();
    private final Wallet wallet = new Wallet();

    // 추가 데이터
    private long totalPlaytime = 0;
    private int mobsKilled = 0;
    private int playersKilled = 0;
    private int deaths = 0;

    // 캐시된 레벨 정보
    private LevelSystem.LevelInfo cachedLevelInfo;

    // 세션 정보
    private long sessionStartTime = System.currentTimeMillis();
    private boolean dataModified = false;
    
    // 플레이어 설정
    private PlayerSettings playerSettings;

    public RPGPlayer(@NotNull Player player) {
        this.playerId = player.getUniqueId();
        this.bukkitPlayer = player;
        // PDC 로드 제거 - 데이터는 RPGPlayerManager가 Firebase에서 로드하여 설정
        
        // PlayerSettings 초기화 (NamespacedKey는 RPGMain에서 생성)
        this.playerSettings = new PlayerSettings(
            player,
            new NamespacedKey("sypixelrpg", "dialog_speed"),
            new NamespacedKey("sypixelrpg", "sound_enabled")
        );
    }

    /**
     * 직업 설정
     */
    public boolean setJob(@Nullable JobType job) {
        if (this.job != null && job != null) {
            return false; // 이미 직업이 있고 새 직업을 설정하려는 경우
        }

        this.job = job;
        markModified();
        return true;
    }

    /**
     * 경험치 추가
     */
    public void addExperience(long exp) {
        if (job == null) return;

        long oldLevel = getLevel();
        this.experience += exp;

        // 최대 경험치 제한
        long maxExp = LevelSystem.getTotalExpForLevel(job.getMaxLevel(), job);
        if (this.experience > maxExp) {
            this.experience = maxExp;
        }

        updateCachedLevelInfo();

        // 레벨업 체크
        long newLevel = getLevel();
        if (newLevel > oldLevel) {
            onLevelUp(oldLevel, newLevel);
        }

        markModified();
    }

    /**
     * 레벨업 처리
     */
    private void onLevelUp(long oldLevel, long newLevel) {
        long levelDiff = newLevel - oldLevel;

        // 레벨당 스탯 포인트 지급
        statPoints += (int) (levelDiff * 5);

        // 10레벨마다 특성 포인트 지급
        long oldTens = oldLevel / 10;
        long newTens = newLevel / 10;
        if (newTens > oldTens) {
            talents.addPoints((int) (newTens - oldTens));
        }

        // 레벨업 메시지
        bukkitPlayer.sendMessage("레벨업! " + oldLevel + " → " + newLevel);
    }

    /**
     * 스탯 포인트 사용
     */
    public boolean useStatPoint(@NotNull Stat stat, int amount) {
        if (statPoints < amount) {
            return false;
        }

        if (stats.increaseStat(stat, amount)) {
            statPoints -= amount;
            updatePlayerAttributes();
            markModified();
            return true;
        }

        return false;
    }

    /**
     * 플레이어 속성 업데이트 (체력, 이동속도 등)
     */
    private void updatePlayerAttributes() {
        // 체력 업데이트
        int vitality = stats.getTotalStat(Stat.VITALITY);
        double maxHealth = 20.0 + (vitality * 2); // VIT당 2HP

        bukkitPlayer.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).setBaseValue(maxHealth);

        // 다른 속성들도 여기서 업데이트
    }

    /**
     * 캐시된 레벨 정보 업데이트
     */
    private void updateCachedLevelInfo() {
        if (job != null) {
            cachedLevelInfo = LevelSystem.getLevelInfo(experience, job);
        }
    }

    /**
     * 데이터 수정 표시
     */
    private void markModified() {
        dataModified = true;
    }

    /**
     * 데이터 수정 여부 확인 및 리셋
     */
    public boolean isDataModified() {
        boolean modified = dataModified;
        dataModified = false;
        return modified;
    }

    // 킬/데스 통계 메서드
    public void incrementMobKills() {
        mobsKilled++;
        markModified();
    }

    public void incrementPlayerKills() {
        playersKilled++;
        markModified();
    }

    public void incrementDeaths() {
        deaths++;
        markModified();
    }

    // 플레이타임 업데이트
    public void updatePlaytime(long additionalTime) {
        totalPlaytime += additionalTime;
        markModified();
    }

    /**
     * 다음 레벨까지 필요한 총 경험치
     */
    public long getExperienceToNextLevel() {
        if (job == null) return 0;
        int currentLevel = getLevel();
        if (currentLevel >= job.getMaxLevel()) return 0;

        long nextLevelTotal = LevelSystem.getTotalExpForLevel(currentLevel + 1, job);
        long currentLevelTotal = LevelSystem.getTotalExpForLevel(currentLevel, job);
        return nextLevelTotal - currentLevelTotal;
    }

    /**
     * 특정 레벨에 필요한 경험치 (직업 기반)
     */
    public long getExpForLevel(int level) {
        if (job == null) return 0;
        return LevelSystem.getExpForLevel(level, job);
    }

    // Getters
    @Nullable
    public JobType getJob() {
        return job;
    }

    public boolean hasJob() {
        return job != null;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
        updateCachedLevelInfo();
        markModified();
    }

    public int getLevel() {
        if (job == null) return 1;
        return cachedLevelInfo != null ? cachedLevelInfo.level() :
                LevelSystem.getLevelFromExp(experience, job);
    }

    public double getLevelProgress() {
        if (job == null) return 0.0;
        return cachedLevelInfo != null ? cachedLevelInfo.progress() :
                LevelSystem.getLevelProgress(experience, job);
    }

    public long getExpToNextLevel() {
        if (job == null) return 0;
        return LevelSystem.getExpToNextLevel(experience, job);
    }

    public long getRequiredExperience() {
        if (job == null) return 0;
        return getExperience() + getExpToNextLevel();
    }

    public int getStatPoints() {
        return statPoints;
    }

    public void setStatPoints(int statPoints) {
        this.statPoints = statPoints;
        markModified();
    }

    @NotNull
    public Stat.StatHolder getStats() {
        return stats;
    }

    @NotNull
    public Talent.TalentHolder getTalents() {
        return talents;
    }

    @NotNull
    public Wallet getWallet() {
        return wallet;
    }

    @NotNull
    public Player getPlayer() {
        return bukkitPlayer;
    }

    @NotNull
    public String getName() {
        return bukkitPlayer.getName();
    }

    @NotNull
    public UUID getPlayerId() {
        return playerId;
    }

    @NotNull
    public UUID getUuid() {
        return playerId;
    }

    public long getSessionStartTime() {
        return sessionStartTime;
    }

    public long getTotalPlaytime() {
        return totalPlaytime;
    }

    public void setTotalPlaytime(long totalPlaytime) {
        this.totalPlaytime = totalPlaytime;
        markModified();
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public void setMobsKilled(int mobsKilled) {
        this.mobsKilled = mobsKilled;
        markModified();
    }

    public int getPlayersKilled() {
        return playersKilled;
    }

    public void setPlayersKilled(int playersKilled) {
        this.playersKilled = playersKilled;
        markModified();
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        markModified();
    }

    /**
     * 총 전투력 계산
     */
    public int getCombatPower() {
        int cp = 0;

        // 레벨 기여도
        cp += getLevel() * 10;

        // 스탯 기여도
        cp += stats.getTotalStat(Stat.STRENGTH) * 5;
        cp += stats.getTotalStat(Stat.INTELLIGENCE) * 5;
        cp += stats.getTotalStat(Stat.DEXTERITY) * 3;
        cp += stats.getTotalStat(Stat.VITALITY) * 4;
        cp += stats.getTotalStat(Stat.WISDOM) * 4;
        cp += stats.getTotalStat(Stat.LUCK) * 2;

        return cp;
    }
    
    /**
     * 플레이어 설정 가져오기
     */
    @NotNull
    public PlayerSettings getPlayerSettings() {
        return playerSettings;
    }
}