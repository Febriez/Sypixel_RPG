package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 낚시 퀘스트 목표
 * 특정 아이템 낚기
 *
 * @author Febrie
 */
public class FishingObjective extends BaseObjective {

    public enum FishType {
        ANY("Any"),
        FISH("Fish"),
        TREASURE("Treasure"),
        JUNK("Junk"),
        SPECIFIC("Specific");

        private final String displayName;

        FishType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final FishType fishType;
    private final @Nullable Material specificItem;

    /**
     * 기본 생성자 - 아무거나 낚기
     *
     * @param id     목표 ID
     * @param amount 낚시 횟수
     */
    public FishingObjective(@NotNull String id, int amount) {
        this(id, FishType.ANY, amount, null);
    }

    /**
     * 타입 지정 생성자
     *
     * @param id       목표 ID
     * @param fishType 낚시 타입
     * @param amount   낚시 횟수
     */
    public FishingObjective(@NotNull String id, @NotNull FishType fishType, int amount) {
        this(id, fishType, amount, null);
    }

    /**
     * 특정 아이템 낚기 생성자
     *
     * @param id           목표 ID
     * @param fishType     낚시 타입
     * @param amount       낚시 횟수
     * @param specificItem 특정 아이템 (SPECIFIC 타입일 때만 사용)
     */
    public FishingObjective(@NotNull String id, @NotNull FishType fishType, int amount,
                            @Nullable Material specificItem) {
        super(id, amount);
        this.fishType = Objects.requireNonNull(fishType);
        this.specificItem = specificItem;

        if (fishType == FishType.SPECIFIC && specificItem == null) {
            throw new IllegalArgumentException("Specific item required for SPECIFIC type");
        }
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.FISHING;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        String typeStr = fishType == FishType.SPECIFIC && specificItem != null ?
                specificItem.translationKey() : fishType.getDisplayName();
        return typeStr + " " + getProgressString(progress);
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof PlayerFishEvent fishEvent)) {
            return false;
        }

        // 플레이어 확인
        if (!fishEvent.getPlayer().equals(player)) {
            return false;
        }

        // 낚시 성공 상태 확인
        if (fishEvent.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return false;
        }

        // 낚은 아이템 확인
        if (!(fishEvent.getCaught() instanceof Item item)) {
            return false;
        }

        ItemStack caught = item.getItemStack();

        return switch (fishType) {
            case ANY -> true;
            case FISH -> isFish(caught.getType());
            case TREASURE -> isTreasure(caught.getType());
            case JUNK -> isJunk(caught.getType());
            case SPECIFIC -> caught.getType() == specificItem;
        };
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
    }

    @Override
    protected @NotNull String serializeData() {
        return fishType.name() + ";" + (specificItem != null ? specificItem.name() : "");
    }

    /**
     * 물고기 아이템인지 확인
     */
    private boolean isFish(Material material) {
        return material == Material.COD ||
                material == Material.SALMON ||
                material == Material.TROPICAL_FISH ||
                material == Material.PUFFERFISH;
    }

    /**
     * 보물 아이템인지 확인
     */
    private boolean isTreasure(Material material) {
        return material == Material.BOW ||
                material == Material.ENCHANTED_BOOK ||
                material == Material.FISHING_ROD ||
                material == Material.NAME_TAG ||
                material == Material.NAUTILUS_SHELL ||
                material == Material.SADDLE;
    }

    /**
     * 쓰레기 아이템인지 확인
     */
    private boolean isJunk(Material material) {
        return material == Material.BOWL ||
                material == Material.LEATHER ||
                material == Material.LEATHER_BOOTS ||
                material == Material.ROTTEN_FLESH ||
                material == Material.STICK ||
                material == Material.STRING ||
                material == Material.POTION ||
                material == Material.BONE ||
                material == Material.INK_SAC ||
                material == Material.TRIPWIRE_HOOK;
    }

    public FishType getFishType() {
        return fishType;
    }

    public @Nullable Material getSpecificItem() {
        return specificItem;
    }
}