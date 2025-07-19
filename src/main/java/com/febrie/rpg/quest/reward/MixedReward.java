package com.febrie.rpg.quest.reward;

import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 혼합 보상 (아이템 + 경험치 + 돈)
 * 
 * @author Febrie
 */
public class MixedReward implements QuestReward {
    
    private final List<ItemStack> items;
    private final long exp;
    private final long money;
    private final String descriptionKey;
    
    public MixedReward(List<ItemStack> items, long exp, long money, String descriptionKey) {
        this.items = new ArrayList<>(items);
        this.exp = exp;
        this.money = money;
        this.descriptionKey = descriptionKey;
    }
    
    @Override
    public void grant(@NotNull Player player) {
        // 아이템 지급
        if (!items.isEmpty()) {
            for (ItemStack item : items) {
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(item);
                } else {
                    player.getWorld().dropItem(player.getLocation(), item);
                }
            }
        }
        
        // 경험치 지급
        if (exp > 0) {
            player.giveExp((int) exp);
        }
        
        // 돈 지급 (실제 경제 시스템 연동 필요)
        if (money > 0) {
            // TODO: 경제 시스템과 연동
            player.sendMessage(Component.text("+ " + money + " 코인", ColorUtil.GOLD));
        }
    }
    
    @Override
    @NotNull
    public String getDescriptionKey() {
        return descriptionKey;
    }
    
    @Override
    @NotNull
    public String[] getPreviewKeys() {
        List<String> keys = new ArrayList<>();
        
        if (!items.isEmpty()) {
            keys.add("quest.reward.items");
        }
        if (exp > 0) {
            keys.add("quest.reward.exp");
        }
        if (money > 0) {
            keys.add("quest.reward.money");
        }
        
        return keys.toArray(new String[0]);
    }
    
    @Override
    @NotNull
    public RewardType getType() {
        return RewardType.MIXED;
    }
    
    @Override
    @NotNull
    public Component getDisplayInfo(@NotNull Player player) {
        Component display = Component.empty();
        
        if (!items.isEmpty()) {
            display = display.append(Component.text("아이템 보상:", ColorUtil.YELLOW))
                    .append(Component.newline());
            for (ItemStack item : items) {
                display = display.append(Component.text("  - ", ColorUtil.GRAY))
                        .append(item.displayName())
                        .append(Component.text(" x" + item.getAmount(), ColorUtil.YELLOW))
                        .append(Component.newline());
            }
        }
        
        if (exp > 0) {
            display = display.append(Component.text("경험치: ", ColorUtil.GREEN))
                    .append(Component.text("+" + exp, ColorUtil.YELLOW))
                    .append(Component.newline());
        }
        
        if (money > 0) {
            display = display.append(Component.text("코인: ", ColorUtil.GOLD))
                    .append(Component.text("+" + money, ColorUtil.YELLOW))
                    .append(Component.newline());
        }
        
        return display;
    }
    
    public List<ItemStack> getItems() {
        return new ArrayList<>(items);
    }
    
    public long getExp() {
        return exp;
    }
    
    public long getMoney() {
        return money;
    }
}