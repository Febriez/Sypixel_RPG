package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceBlockObjective extends BaseObjective {
    private final Material material;
    
    public PlaceBlockObjective(String id, Material material, int requiredAmount) {
        super(id, requiredAmount);
        this.material = material;
    }
    
    public void onBlockPlace(BlockPlaceEvent event, ObjectiveProgress progress) {
        if (event.getBlock().getType() == material) {
            progress.increment(1);
        }
    }
    
    public boolean isCompleted(@NotNull ObjectiveProgress progress) {
        return progress.getCurrentValue() >= requiredAmount;
    }
    
    @Override
    public @NotNull String getProgressString(@NotNull ObjectiveProgress progress) {
        return String.format("Placed %d/%d %s", progress.getCurrentValue(), requiredAmount, material.name());
    }
    
    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.PLACE_BLOCK;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    @Override
    protected @NotNull String serializeData() {
        return material.name();
    }
    
    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        return String.format("%d/%d", progress.getCurrentValue(), requiredAmount);
    }
    
    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        if (event instanceof BlockPlaceEvent placeEvent) {
            if (placeEvent.getBlock().getType() == material) {
                return 1;
            }
        }
        return 0;
    }
    
    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (event instanceof BlockPlaceEvent placeEvent) {
            return placeEvent.getPlayer().equals(player) && placeEvent.getBlock().getType() == material;
        }
        return false;
    }
}