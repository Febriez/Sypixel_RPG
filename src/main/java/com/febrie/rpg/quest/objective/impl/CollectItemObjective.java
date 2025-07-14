package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 아이템 수집 퀘스트 목표
 * 특정 아이템을 지정된 수만큼 수집
 *
 * @author Febrie
 */
public class CollectItemObjective extends BaseObjective {

    private final Material itemType;
    private final boolean checkInventory;

    /**
     * 기본 생성자
     *
     * @param id       목표 ID
     * @param itemType 수집할 아이템 타입
     * @param amount   수집 수량
     */
    public CollectItemObjective(@NotNull String id, @NotNull Material itemType, int amount) {
        this(id, itemType, amount, false);
    }

    /**
     * 인벤토리 체크 옵션 포함 생성자
     *
     * @param id             목표 ID
     * @param itemType       수집할 아이템 타입
     * @param amount         수집 수량
     * @param checkInventory true면 인벤토리의 현재 보유량을 체크
     */
    public CollectItemObjective(@NotNull String id, @NotNull Material itemType, int amount,
                                boolean checkInventory) {
        super(id, amount, "quest.objective.collect_item", createPlaceholders(itemType, amount));
        this.itemType = Objects.requireNonNull(itemType);
        this.checkInventory = checkInventory;
    }

    private static String[] createPlaceholders(Material material, int amount) {
        // 아이템 타입은 마인크래프트 번역 키 사용
        return new String[]{
                "item_key", material.translationKey(),
                "amount", String.valueOf(amount)
        };
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.COLLECT_ITEM;
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        // 인벤토리 체크 모드인 경우
        if (checkInventory) {
            return event instanceof InventoryClickEvent;
        }

        // 아이템 줍기 이벤트
        if (event instanceof EntityPickupItemEvent pickupEvent) {
            if (!(pickupEvent.getEntity() instanceof Player p) || !p.equals(player)) {
                return false;
            }
            ItemStack item = pickupEvent.getItem().getItemStack();
            return item.getType() == itemType;
        }

        return false;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        if (!canProgress(event, player)) return 0;

        // 인벤토리 체크 모드
        if (checkInventory) {
            int totalAmount = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == itemType) {
                    totalAmount += item.getAmount();
                }
            }
            return totalAmount;
        }

        // 아이템 줍기 모드
        if (event instanceof EntityPickupItemEvent pickupEvent) {
            return pickupEvent.getItem().getItemStack().getAmount();
        }

        return 0;
    }

    @Override
    protected @NotNull String serializeData() {
        return itemType.name() + ":" + checkInventory;
    }

    public Material getItemType() {
        return itemType;
    }

    public boolean isCheckInventory() {
        return checkInventory;
    }
}