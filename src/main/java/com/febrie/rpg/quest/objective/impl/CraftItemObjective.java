package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 아이템 제작 퀘스트 목표
 * 특정 아이템을 제작대에서 제작
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
        super(id, amount, createDescription(itemType, amount));
        this.itemType = Objects.requireNonNull(itemType);
    }

    private static Component createDescription(Material material, int amount) {
        return Component.translatable("quest.objective.craft_item",
                        Component.translatable(material.translationKey()), Component.text(amount))
                .color(NamedTextColor.YELLOW);
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.CRAFT_ITEM;
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

        // 제작 결과물 확인
        ItemStack result = craftEvent.getRecipe().getResult();
        return result.getType() == itemType;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        if (!canProgress(event, player)) return 0;

        CraftItemEvent craftEvent = (CraftItemEvent) event;
        ItemStack result = craftEvent.getRecipe().getResult();

        // Shift 클릭으로 여러개 제작하는 경우 처리
        if (craftEvent.isShiftClick()) {
            int lowestAmount = Integer.MAX_VALUE;
            for (ItemStack item : craftEvent.getInventory().getMatrix()) {
                if (item != null && item.getType() != Material.AIR) {
                    lowestAmount = Math.min(lowestAmount, item.getAmount());
                }
            }
            return result.getAmount() * lowestAmount;
        } else {
            return result.getAmount();
        }
    }

    @Override
    protected @NotNull String serializeData() {
        return itemType.name();
    }

    public Material getItemType() {
        return itemType;
    }
}