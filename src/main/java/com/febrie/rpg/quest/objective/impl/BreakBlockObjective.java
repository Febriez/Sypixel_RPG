package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 블럭 파괴 퀘스트 목표
 * 특정 블럭을 파괴
 *
 * @author Febrie
 */
public class BreakBlockObjective extends BaseObjective {

    private final Material blockType;

    /**
     * 기본 생성자
     *
     * @param id        목표 ID
     * @param blockType 파괴할 블럭 타입
     * @param amount    파괴 수량
     */
    public BreakBlockObjective(@NotNull String id, @NotNull Material blockType, int amount) {
        super(id, amount, createDescription(blockType, amount));
        this.blockType = Objects.requireNonNull(blockType);

        if (!blockType.isBlock()) {
            throw new IllegalArgumentException("Material must be a block: " + blockType);
        }
    }

    private static Component createDescription(Material material, int amount) {
        return Component.translatable("quest.objective.break_block",
                        Component.translatable(material.translationKey()), Component.text(amount))
                .color(NamedTextColor.YELLOW);
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.BREAK_BLOCK;
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof BlockBreakEvent breakEvent)) {
            return false;
        }

        // 플레이어 확인
        if (!breakEvent.getPlayer().equals(player)) {
            return false;
        }

        // 블럭 타입 확인
        return breakEvent.getBlock().getType() == blockType;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
    }

    @Override
    protected @NotNull String serializeData() {
        return blockType.name();
    }

    public Material getBlockType() {
        return blockType;
    }
}