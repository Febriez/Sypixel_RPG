package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import net.kyori.adventure.text.Component;
/**
 * 농작물 수확 퀘스트 목표
 * 다 자란 농작물 수확
 *
 * @author Febrie
 */
public class HarvestObjective extends BaseObjective {

    private final Material cropType;
    private final boolean requireFullyGrown;

    /**
     * 기본 생성자 - 다 자란 농작물만
     *
     * @param id       목표 ID
     * @param cropType 농작물 타입
     * @param amount   수확 수량
     */
    public HarvestObjective(@NotNull String id, @NotNull Material cropType, int amount) {
        this(id, cropType, amount, true);
    }

    /**
     * 성장 상태 옵션 포함 생성자
     *
     * @param id                목표 ID
     * @param cropType          농작물 타입
     * @param amount            수확 수량
     * @param requireFullyGrown 완전히 자란 것만 카운트할지 여부
     */
    public HarvestObjective(@NotNull String id, @NotNull Material cropType, int amount,
                            boolean requireFullyGrown) {
        super(id, amount);
        this.cropType = Objects.requireNonNull(cropType);
        this.requireFullyGrown = requireFullyGrown;

        if (!isCrop(cropType)) {
            throw new IllegalArgumentException("Not a valid crop: " + cropType);
        }
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.HARVEST;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        String status = cropType.translationKey() + " " + getProgressString(progress);
        if (requireFullyGrown) {
            status += " (Fully Grown)";
        }
        return status;
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

        // 농작물 타입 확인
        Material blockType = breakEvent.getBlock().getType();
        if (blockType != cropType) {
            return false;
        }

        // 성장 상태 확인
        if (requireFullyGrown && breakEvent.getBlock().getBlockData() instanceof Ageable ageable) {
            return ageable.getAge() == ageable.getMaximumAge();
        }

        return true;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        if (!canProgress(event, player)) return 0;

        // 일부 농작물은 여러 개 드롭
        return switch (cropType) {
            case CARROTS, POTATOES -> 1 + (int) (Math.random() * 3); // 1-3개
            case BEETROOTS -> 1;
            case WHEAT -> 1;
            case NETHER_WART -> 2 + (int) (Math.random() * 3); // 2-4개
            default -> 1;
        };
    }

    @Override
    protected @NotNull String serializeData() {
        return cropType.name() + ":" + requireFullyGrown;
    }

    /**
     * 농작물인지 확인
     */
    private static boolean isCrop(Material material) {
        return material == Material.WHEAT ||
                material == Material.CARROTS ||
                material == Material.POTATOES ||
                material == Material.BEETROOTS ||
                material == Material.NETHER_WART ||
                material == Material.COCOA ||
                material == Material.SWEET_BERRY_BUSH ||
                material == Material.MELON ||
                material == Material.PUMPKIN ||
                material == Material.SUGAR_CANE ||
                material == Material.BAMBOO ||
                material == Material.KELP ||
                material == Material.CACTUS;
    }

    public Material getCropType() {
        return cropType;
    }

    public boolean isRequireFullyGrown() {
        return requireFullyGrown;
    }
}