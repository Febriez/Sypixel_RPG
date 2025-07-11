package com.febrie.rpg.player;

import com.febrie.rpg.job.JobType;
import com.febrie.rpg.level.LevelSystem;
import com.febrie.rpg.stat.Stat;
import com.febrie.rpg.talent.Talent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * 플레이어의 RPG 데이터를 관리하는 클래스
 * PDC를 사용하여 데이터를 저장하고 불러옴
 *
 * @author Febrie, CoffeeTory
 */
public class RPGPlayer {

    // PDC 키들
    private static final NamespacedKey KEY_JOB = new NamespacedKey("sypixelrpg", "job");
    private static final NamespacedKey KEY_EXPERIENCE = new NamespacedKey("sypixelrpg", "experience");
    private static final NamespacedKey KEY_STAT_POINTS = new NamespacedKey("sypixelrpg", "stat_points");
    private static final NamespacedKey KEY_TALENT_POINTS = new NamespacedKey("sypixelrpg", "talent_points");

    private final UUID playerId;
    private final Player bukkitPlayer;

    // RPG 데이터
    private JobType job;
    private long experience = 0;
    private int statPoints = 0;
    private final Stat.StatHolder stats = new Stat.StatHolder();
    private final Talent.TalentHolder talents = new Talent.TalentHolder();

    // 캐시된 레벨 정보
    private LevelSystem.LevelInfo cachedLevelInfo;

    public RPGPlayer(@NotNull Player player) {
        this.playerId = player.getUniqueId();
        this.bukkitPlayer = player;
        loadFromPDC();
    }

    /**
     * PDC에서 데이터 불러오기
     */
    private void loadFromPDC() {
        PersistentDataContainer pdc = bukkitPlayer.getPersistentDataContainer();

        // 직업 불러오기
        if (pdc.has(KEY_JOB, PersistentDataType.STRING)) {
            String jobName = pdc.get(KEY_JOB, PersistentDataType.STRING);
            try {
                this.job = JobType.valueOf(jobName);
            } catch (IllegalArgumentException e) {
                this.job = null;
            }
        }

        // 경험치 불러오기
        if (pdc.has(KEY_EXPERIENCE, PersistentDataType.LONG)) {
            this.experience = pdc.get(KEY_EXPERIENCE, PersistentDataType.LONG);
        }

        // 스탯 포인트 불러오기
        if (pdc.has(KEY_STAT_POINTS, PersistentDataType.INTEGER)) {
            this.statPoints = pdc.get(KEY_STAT_POINTS, PersistentDataType.INTEGER);
        }

        // 특성 포인트 불러오기
        if (pdc.has(KEY_TALENT_POINTS, PersistentDataType.INTEGER)) {
            int talentPoints = pdc.get(KEY_TALENT_POINTS, PersistentDataType.INTEGER);
            talents.addPoints(talentPoints);
        }

        // 각 스탯 불러오기
        for (Stat stat : Stat.getAllStats().values()) {
            if (pdc.has(stat.getKey(), PersistentDataType.INTEGER)) {
                int value = pdc.get(stat.getKey(), PersistentDataType.INTEGER);
                stats.setBaseStat(stat, value);
            }
        }

        // 캐시 업데이트
        updateCachedLevelInfo();
    }

    /**
     * PDC에 데이터 저장하기
     */
    public void saveToPDC() {
        PersistentDataContainer pdc = bukkitPlayer.getPersistentDataContainer();

        // 직업 저장
        if (job != null) {
            pdc.set(KEY_JOB, PersistentDataType.STRING, job.name());
        }

        // 경험치 저장
        pdc.set(KEY_EXPERIENCE, PersistentDataType.LONG, experience);

        // 스탯 포인트 저장
        pdc.set(KEY_STAT_POINTS, PersistentDataType.INTEGER, statPoints);

        // 특성 포인트 저장
        pdc.set(KEY_TALENT_POINTS, PersistentDataType.INTEGER, talents.getAvailablePoints());

        // 각 스탯 저장
        for (Map.Entry<Stat, Integer> entry : stats.getAllBaseStats().entrySet()) {
            pdc.set(entry.getKey().getKey(), PersistentDataType.INTEGER, entry.getValue());
        }
    }

    /**
     * 직업 설정
     */
    public boolean setJob(@NotNull JobType job) {
        if (this.job != null) {
            return false; // 이미 직업이 있음
        }

        this.job = job;
        saveToPDC();
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

        saveToPDC();
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

        // 레벨업 메시지 (나중에 LangManager로 처리)
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
            saveToPDC();
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

    public int getLevel() {
        if (job == null) return 1;
        return cachedLevelInfo != null ? cachedLevelInfo.level :
                LevelSystem.getLevelFromExp(experience, job);
    }

    public double getLevelProgress() {
        if (job == null) return 0.0;
        return cachedLevelInfo != null ? cachedLevelInfo.progress :
                LevelSystem.getLevelProgress(experience, job);
    }

    public long getExpToNextLevel() {
        if (job == null) return 0;
        return LevelSystem.getExpToNextLevel(experience, job);
    }

    public int getStatPoints() {
        return statPoints;
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
    public Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    @NotNull
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * 총 전투력 계산 (나중에 구현)
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
}