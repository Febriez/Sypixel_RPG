package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        ANY("quest.objective.fishing.type.any"),
        FISH("quest.objective.fishing.type.fish"),
        TREASURE("quest.objective.fishing.type.treasure"),
        JUNK("quest.objective.fishing.type.junk"),
        SPECIFIC("quest.objective.fishing.type.specific");

        private final String translationKey;

        FishType(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getTranslationKey() {
            return translationKey;
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
        super(id, amount, createDescription(fishType, amount, specificItem));
        this.fishType = Objects.requireNonNull(fishType);
        this.specificItem = specificItem;

        if (fishType == FishType.SPECIFIC && specificItem == null) {
            throw new IllegalArgumentException("Specific item required for SPECIFIC fish type");
        }
    }

    private static Component createDescription(FishType type, int amount, @Nullable Material item) {
        return (switch (type) {
            case ANY -> Component.translatable("quest.objective.fishing.any", Component.text(amount));
            case FISH -> Component.translatable("quest.objective.fishing.fish", Component.text(amount));
            case TREASURE -> Component.translatable("quest.objective.fishing.treasure", Component.text(amount));
            case JUNK -> Component.translatable("quest.objective.fishing.junk", Component.text(amount));
            case SPECIFIC -> Component.translatable("quest.objective.fishing.specific",
                    Component.translatable(item.translationKey()), Component.text(amount));
        }).color(NamedTextColor.AQUA);
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.FISHING;
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

        // 낚시 성공 확인
        if (fishEvent.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return false;
        }

        // 잡은 아이템 확인
        if (!(fishEvent.getCaught() instanceof Item item)) {
            return false;
        }

        ItemStack caught = item.getItemStack();
        Material caughtType = caught.getType();

        return switch (fishType) {
            case ANY -> true;
            case FISH -> isFish(caughtType);
            case TREASURE -> isTreasure(caughtType);
            case JUNK -> isJunk(caughtType);
            case SPECIFIC -> caughtType == specificItem;
        };
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
    }

    @Override
    protected @NotNull String serializeData() {
        return fishType.name() + (specificItem != null ? ":" + specificItem.name() : "");
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
                material == Material.WATER_BUCKET ||
                material == Material.BONE ||
                material == Material.TRIPWIRE_HOOK ||
                material == Material.BAMBOO;
    }

    public FishType getFishType() {
        return fishType;
    }

    public @Nullable Material getSpecificItem() {
        return specificItem;
    }
}