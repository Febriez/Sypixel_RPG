package com.febrie.rpg.quest.guide;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import net.kyori.adventure.text.Component;
/**
 * 개별 플레이어의 퀘스트 가이드 정보
 *
 * @author Febrie
 */
public class QuestGuide {
    
    private final Player player;
    private final Location target;
    private final String questName;
    private final long startTime;
    
    private List<Location> path;
    private long lastRecalculation;
    
    // 설정
    private static final long RECALCULATION_INTERVAL = 10000; // 10초마다 경로 재계산 체크
    
    public QuestGuide(@NotNull Player player, @NotNull Location target, @NotNull String questName) {
        this.player = player;
        this.target = target.clone();
        this.questName = questName;
        this.startTime = System.currentTimeMillis();
        this.lastRecalculation = startTime;
    }
    
    /**
     * 경로 재계산이 필요한지 확인
     */
    public boolean shouldRecalculatePath() {
        return System.currentTimeMillis() - lastRecalculation > RECALCULATION_INTERVAL;
    }
    
    /**
     * 재계산 타이머 리셋
     */
    public void resetRecalculationTimer() {
        this.lastRecalculation = System.currentTimeMillis();
    }
    
    /**
     * 가이드 지속 시간 (밀리초)
     */
    public long getDuration() {
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * 가이드 지속 시간 (초)
     */
    public long getDurationInSeconds() {
        return getDuration() / 1000;
    }
    
    // Getters and Setters
    @NotNull
    public Player getPlayer() {
        return player;
    }
    
    @NotNull
    public Location getTarget() {
        return target.clone();
    }
    
    @NotNull
    public String getQuestName() {
        return questName;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    @Nullable
    public List<Location> getPath() {
        return path;
    }
    
    public void setPath(@Nullable List<Location> path) {
        this.path = path;
    }
    
    public boolean hasPath() {
        return path != null && !path.isEmpty();
    }
    
    /**
     * 목표까지의 현재 거리
     */
    public double getDistanceToTarget() {
        return player.getLocation().distance(target);
    }
    
    /**
     * 경로의 총 길이 (대략적)
     */
    public double getPathLength() {
        if (path == null || path.size() < 2) {
            return getDistanceToTarget();
        }
        
        double totalLength = 0;
        for (int i = 1; i < path.size(); i++) {
            totalLength += path.get(i - 1).distance(path.get(i));
        }
        
        return totalLength;
    }
    
    @Override
    public String toString() {
        return String.format("QuestGuide{player=%s, quest=%s, distance=%.2f}", 
                player.getName(), questName, getDistanceToTarget());
    }
}