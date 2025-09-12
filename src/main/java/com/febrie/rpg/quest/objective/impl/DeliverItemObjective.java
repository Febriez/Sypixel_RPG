package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public class DeliverItemObjective extends BaseObjective {
    private final Material material;
    private final String targetNpc;
    
    public DeliverItemObjective(String id, Material material, int requiredAmount, String targetNpc) {
        super(id, requiredAmount);
        this.material = material;
        this.targetNpc = targetNpc;
    }
    
    public boolean tryDeliver(Player player, ObjectiveProgress progress) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        
        if (count >= requiredAmount) {
            // Remove items from inventory
            int toRemove = requiredAmount;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == material) {
                    int amount = item.getAmount();
                    if (amount <= toRemove) {
                        player.getInventory().remove(item);
                        toRemove -= amount;
                    } else {
                        item.setAmount(amount - toRemove);
                        toRemove = 0;
                    }
                    
                    if (toRemove <= 0) {
                        break;
                    }
                }
            }
            
            progress.update(requiredAmount);
            return true;
        }
        
        progress.update(count);
        return false;
    }
    
    public boolean isCompleted(@NotNull ObjectiveProgress progress) {
        return progress.getCurrentValue() >= requiredAmount;
    }
    
    @Override
    public @NotNull String getProgressString(@NotNull ObjectiveProgress progress) {
        return String.format("Deliver %d/%d %s to %s", progress.getCurrentValue(), requiredAmount, material.name(), targetNpc);
    }
    
    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.DELIVER_ITEM;
    }
    
    public String getTargetNpc() {
        return targetNpc;
    }
    
    @Override
    protected @NotNull String serializeData() {
        return material.name() + "," + targetNpc;
    }
    
    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        return String.format("%d/%d", progress.getCurrentValue(), requiredAmount);
    }
    
    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        // DeliverItemObjective doesn't use event-based increments
        // Delivery is handled through tryDeliver method
        return 0;
    }
    
    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        // Progress is handled through tryDeliver method
        return false;
    }
}