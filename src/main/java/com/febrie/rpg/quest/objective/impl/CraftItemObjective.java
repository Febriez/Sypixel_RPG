package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 아이템 제작 퀘스트 목표
 * 특정 아이템을 지정된 수만큼 제작
 *
 * @author Febrie
 */
public class CraftItemObjective extends BaseObjective {

    private final Material itemType;

    /**
     * 기본 생성자
     *
     * @param id       목표 ID
     * @param itemType 제작할 아이템 타입
     * @param amount   제작 수량
     */
    public CraftItemObjective(@NotNull String id, @NotNull Material itemType, int amount) {
        super(id, amount);
        this.itemType = Objects.requireNonNull(itemType);
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.CRAFT_ITEM;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        return itemType.translationKey() + " " + getProgressString(progress);
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof CraftItemEvent craftEvent)) {
            return false;
        }

        // 플레이어 확인
        if (!craftEvent.getWhoClicked().equals(player)) {
            return false;
        }

        // 취소된 이벤트 무시
        if (craftEvent.isCancelled()) {
            return false;
        }

        // 제작된 아이템 타입 확인
        ItemStack result = craftEvent.getRecipe().getResult();
        return result.getType() == itemType;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        if (!canProgress(event, player)) return 0;

        CraftItemEvent craftEvent = (CraftItemEvent) event;
        ItemStack result = craftEvent.getRecipe().getResult();

        // Shift 클릭으로 여러 개 제작하는 경우 계산
        if (craftEvent.isShiftClick()) {
            int maxCraftable = Integer.MAX_VALUE;

            // 재료로 만들 수 있는 최대 개수 계산
            for (ItemStack ingredient : craftEvent.getInventory().getMatrix()) {
                if (ingredient != null && ingredient.getType() != Material.AIR) {
                    maxCraftable = Math.min(maxCraftable, ingredient.getAmount());
                }
            }

            return result.getAmount() * maxCraftable;
        }

        return result.getAmount();
    }

    @Override
    protected @NotNull String serializeData() {
        return itemType.name();
    }

    /**
     * 아이템 타입 반환
     */
    public @NotNull Material getItemType() {
        return itemType;
    }
}