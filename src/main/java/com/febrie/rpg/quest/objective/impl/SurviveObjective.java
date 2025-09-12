package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class SurviveObjective extends BaseObjective {
    private final long durationSeconds;
    private long startTime;
    
    public SurviveObjective(String id, long durationSeconds) {
        super(id, (int) durationSeconds);
        this.durationSeconds = durationSeconds;
        this.startTime = 0;
    }
    
    public void onStart(Player player) {
        this.startTime = System.currentTimeMillis() / 1000;
    }
    
    public boolean checkCondition(Player player, ObjectiveProgress progress) {
        if (startTime == 0) {
            return false;
        }
        long currentTime = System.currentTimeMillis() / 1000;
        long elapsedTime = currentTime - startTime;
        
        progress.update((int) elapsedTime);
        return elapsedTime >= durationSeconds;
    }
    
    public boolean isCompleted(@NotNull ObjectiveProgress progress) {
        return progress.getCurrentValue() >= durationSeconds;
    }
    
    @Override
    public @NotNull String getProgressString(@NotNull ObjectiveProgress progress) {
        long elapsed = progress.getCurrentValue();
        return String.format("Survived %d/%d seconds", elapsed, durationSeconds);
    }
    
    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.SURVIVE;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    @Override
    protected @NotNull String serializeData() {
        return String.valueOf(durationSeconds);
    }
    
    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        return String.format("%d/%d", progress.getCurrentValue(), durationSeconds);
    }
    
    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        // SurviveObjective doesn't use event-based increments
        // Progress is tracked through checkCondition method
        return 0;
    }
    
    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        // Progress is handled through checkCondition method  
        return false;
    }
}