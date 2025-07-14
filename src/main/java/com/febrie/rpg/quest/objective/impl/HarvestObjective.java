package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 농작물 수확 퀘스트 목표
 * 다 자란 농작물 수확
 *
 * @author Febrie
 */
public class HarvestObjective extends BaseObjective {

    private final Material cropType;
    private final boolean requireFullyGrown;
    private final String questGiver;

    /**
     * 기본 생성자 - 다 자란 농작물만
     *
     * @param id       목표 ID
     * @param cropType 농작물 타입
     * @param amount   수확 수량
     */
    public HarvestObjective(@NotNull String id, @NotNull Material cropType, int amount) {
        this(id, cropType, amount, true, "농부");
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
        this(id, cropType, amount, requireFullyGrown, "농부");
    }

    /**
     * 전체 옵션 생성자
     *
     * @param id                목표 ID
     * @param cropType          농작물 타입
     * @param amount            수확 수량
     * @param requireFullyGrown 완전히 자란 것만 카운트할지 여부
     * @param questGiver        퀘스트 제공자
     */
    public HarvestObjective(@NotNull String id, @NotNull Material cropType, int amount,
                            boolean requireFullyGrown, @NotNull String questGiver) {
        super(id, amount, "quest.objective.harvest",
                "crop_key", cropType.translationKey(),
                "amount", String.valueOf(amount));
        this.cropType = Objects.requireNonNull(cropType);
        this.requireFullyGrown = requireFullyGrown;
        this.questGiver = Objects.requireNonNull(questGiver);

        if (!isCrop(cropType)) {
            throw new IllegalArgumentException("Not a valid crop: " + cropType);
        }
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.HARVEST;
    }

    @Override
    public @NotNull String getDescription(boolean isKorean) {
        return isKorean ?
                "퀘스트를 준 사람: " + questGiver + "\n\n" +
                        "수확철이 왔다네! " + cropType.name().toLowerCase().replace('_', ' ') + " " + requiredAmount + "개를 수확해 주게나. " +
                        (requireFullyGrown ? "완전히 자란 것만 수확해야 한다네. " : "") +
                        "이번 수확은 마을의 식량 비축에 중요한 일이라네." :

                "Quest Giver: " + questGiver + "\n\n" +
                        "It's harvest season! Please harvest " + requiredAmount + " " + cropType.name().toLowerCase().replace('_', ' ') + ". " +
                        (requireFullyGrown ? "Only harvest fully grown crops. " : "") +
                        "This harvest is important for the village's food reserves.";
    }

    @Override
    public @NotNull String getGiverName(boolean isKorean) {
        return questGiver;
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