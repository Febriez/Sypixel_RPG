package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import org.bukkit.Bukkit;
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
        super(id, amount);
        this.itemType = Objects.requireNonNull(itemType);
        this.checkInventory = checkInventory;
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.COLLECT_ITEM;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        return itemType.translationKey() + " " + getProgressString(progress);
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (event instanceof EntityPickupItemEvent pickupEvent) {
            // 플레이어 확인
            if (!(pickupEvent.getEntity() instanceof Player)) {
                return false;
            }

            Player eventPlayer = (Player) pickupEvent.getEntity();
            if (!eventPlayer.equals(player)) {
                return false;
            }

            // 아이템 타입 확인
            ItemStack item = pickupEvent.getItem().getItemStack();
            return item.getType() == itemType;
        } else if (event instanceof InventoryClickEvent clickEvent) {
            // 인벤토리 클릭 이벤트 처리 (아이템 획득)
            if (!clickEvent.getWhoClicked().equals(player)) {
                return false;
            }

            ItemStack currentItem = clickEvent.getCurrentItem();
            if (currentItem == null || currentItem.getType() != itemType) {
                return false;
            }

            // Shift 클릭으로 아이템을 인벤토리로 이동하는 경우
            return clickEvent.isShiftClick() &&
                    clickEvent.getClickedInventory() != player.getInventory();
        }

        return false;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        if (!canProgress(event, player)) return 0;

        if (event instanceof EntityPickupItemEvent pickupEvent) {
            return pickupEvent.getItem().getItemStack().getAmount();
        } else if (event instanceof InventoryClickEvent clickEvent) {
            ItemStack item = clickEvent.getCurrentItem();
            return item != null ? item.getAmount() : 0;
        }

        return 0;
    }

    @Override
    public int getCurrentProgress(@NotNull ObjectiveProgress progress) {
        if (checkInventory) {
            // 인벤토리 체크 모드일 때는 플레이어 객체가 필요
            // QuestManager에서 호출 시 플레이어를 직접 확인해야 함
            Player player = Bukkit.getPlayer(progress.getPlayerId());
            if (player != null && player.isOnline()) {
                int count = 0;
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType() == itemType) {
                        count += item.getAmount();
                    }
                }
                return Math.min(count, requiredAmount);
            }
        }
        return super.getCurrentProgress(progress);
    }

    @Override
    protected @NotNull String serializeData() {
        return itemType.name() + ";" + checkInventory;
    }

    /**
     * 아이템 타입 반환
     */
    public @NotNull Material getItemType() {
        return itemType;
    }

    /**
     * 인벤토리 체크 여부 반환
     */
    public boolean isCheckInventory() {
        return checkInventory;
    }
}