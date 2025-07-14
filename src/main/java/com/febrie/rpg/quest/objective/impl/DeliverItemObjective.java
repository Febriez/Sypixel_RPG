package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 아이템 전달 퀘스트 목표
 * NPC에게 특정 아이템 전달
 *
 * @author Febrie
 */
public class DeliverItemObjective extends BaseObjective {

    private final String npcName;
    private final Map<Material, Integer> requiredItems;

    /**
     * 단일 아이템 전달 생성자
     *
     * @param id       목표 ID
     * @param npcName  대상 NPC 이름
     * @param itemType 전달할 아이템
     * @param amount   수량
     */
    public DeliverItemObjective(@NotNull String id, @NotNull String npcName,
                                @NotNull Material itemType, int amount) {
        super(id, 1, createDescription(npcName, Map.of(itemType, amount)));
        this.npcName = Objects.requireNonNull(npcName);
        this.requiredItems = Map.of(itemType, amount);
    }

    /**
     * 다중 아이템 전달 생성자
     *
     * @param id            목표 ID
     * @param npcName       대상 NPC 이름
     * @param requiredItems 전달할 아이템 맵
     */
    public DeliverItemObjective(@NotNull String id, @NotNull String npcName,
                                @NotNull Map<Material, Integer> requiredItems) {
        super(id, 1, createDescription(npcName, requiredItems));
        this.npcName = Objects.requireNonNull(npcName);
        this.requiredItems = new HashMap<>(requiredItems);
    }

    private static Component createDescription(String npcName, Map<Material, Integer> items) {
        Component itemList = Component.empty();
        int index = 0;

        for (Map.Entry<Material, Integer> entry : items.entrySet()) {
            if (index > 0) {
                itemList = itemList.append(Component.text(", "));
            }
            itemList = itemList.append(Component.translatable(entry.getKey().translationKey()))
                    .append(Component.text(" " + entry.getValue()));
            index++;
        }

        return Component.translatable("quest.objective.deliver_item",
                        Component.text(npcName), itemList)
                .color(NamedTextColor.YELLOW);
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.DELIVER_ITEM;
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof PlayerInteractEntityEvent interactEvent)) {
            return false;
        }

        // 플레이어 확인
        if (!interactEvent.getPlayer().equals(player)) {
            return false;
        }

        // 주민인지 확인
        if (!(interactEvent.getRightClicked() instanceof Villager villager)) {
            return false;
        }

        // NPC 이름 확인
        Component customName = villager.customName();
        if (customName == null) return false;

        String entityName = customName.toString();
        if (!entityName.contains(npcName)) {
            return false;
        }

        // 아이템 보유 확인
        return hasRequiredItems(player);
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        if (!canProgress(event, player)) return 0;

        // 아이템 제거
        removeRequiredItems(player);
        return 1;
    }

    /**
     * 플레이어가 필요한 아이템을 모두 가지고 있는지 확인
     */
    private boolean hasRequiredItems(@NotNull Player player) {
        Map<Material, Integer> inventory = new HashMap<>();

        // 인벤토리 아이템 카운트
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                inventory.merge(item.getType(), item.getAmount(), Integer::sum);
            }
        }

        // 필요 아이템 확인
        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            if (inventory.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 플레이어로부터 필요한 아이템 제거
     */
    private void removeRequiredItems(@NotNull Player player) {
        Map<Material, Integer> toRemove = new HashMap<>(requiredItems);

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;

            Integer needed = toRemove.get(item.getType());
            if (needed != null && needed > 0) {
                int remove = Math.min(item.getAmount(), needed);
                item.setAmount(item.getAmount() - remove);
                toRemove.put(item.getType(), needed - remove);
            }
        }
    }

    @Override
    protected @NotNull String serializeData() {
        StringBuilder data = new StringBuilder(npcName);
        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            data.append(":").append(entry.getKey().name()).append(":").append(entry.getValue());
        }
        return data.toString();
    }

    public String getNpcName() {
        return npcName;
    }

    public Map<Material, Integer> getRequiredItems() {
        return new HashMap<>(requiredItems);
    }
}