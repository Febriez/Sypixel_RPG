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
    private final String questGiver;

    /**
     * 기본 생성자
     *
     * @param id       목표 ID
     * @param itemType 수집할 아이템 타입
     * @param amount   수집 수량
     */
    public CollectItemObjective(@NotNull String id, @NotNull Material itemType, int amount) {
        this(id, itemType, amount, false, "상인");
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
        this(id, itemType, amount, checkInventory, "상인");
    }

    /**
     * 전체 옵션 생성자
     *
     * @param id             목표 ID
     * @param itemType       수집할 아이템 타입
     * @param amount         수집 수량
     * @param checkInventory true면 인벤토리의 현재 보유량을 체크
     * @param questGiver     퀘스트 제공자
     */
    public CollectItemObjective(@NotNull String id, @NotNull Material itemType, int amount,
                                boolean checkInventory, @NotNull String questGiver) {
        super(id, amount, "quest.objective.collect_item", createPlaceholders(itemType, amount));
        this.itemType = Objects.requireNonNull(itemType);
        this.checkInventory = checkInventory;
        this.questGiver = Objects.requireNonNull(questGiver);
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
    public @NotNull String getDescription(boolean isKorean) {
        String itemNameKo = getItemNameKorean(itemType);

        return isKorean ?
                "퀘스트를 준 사람: " + questGiver + "\n\n" +
                        "모험가여, 나에게 " + itemNameKo + " " + requiredAmount + "개가 필요하다네. " +
                        "이것들은 매우 중요한 물품이라 꼭 구해다 주게나. " +
                        "자네라면 충분히 구할 수 있을 거라 믿네." :

                "Quest Giver: " + questGiver + "\n\n" +
                        "Adventurer, I need " + requiredAmount + " " + itemType.name().toLowerCase().replace('_', ' ') + ". " +
                        "These are very important items, so please bring them to me. " +
                        "I believe you can find them.";
    }

    @Override
    public @NotNull String getGiverName(boolean isKorean) {
        return questGiver;
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        // 인벤토리 체크 모드인 경우
        if (checkInventory) {
            return event instanceof InventoryClickEvent;
        }

        // 아이템 줍기 이벤트
        if (event instanceof EntityPickupItemEvent pickupEvent) {
            if (!pickupEvent.getEntity().equals(player)) {
                return false;
            }

            ItemStack item = pickupEvent.getItem().getItemStack();
            return item.getType() == itemType;
        }

        return false;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        if (!canProgress(event, player)) {
            return 0;
        }

        // 인벤토리 체크 모드
        if (checkInventory) {
            int count = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == itemType) {
                    count += item.getAmount();
                }
            }
            // 현재 보유량을 그대로 반환 (전체 진행도로 설정)
            return Math.min(count, requiredAmount);
        }

        // 아이템 줍기 모드
        if (event instanceof EntityPickupItemEvent pickupEvent) {
            return pickupEvent.getItem().getItemStack().getAmount();
        }

        return 0;
    }

    @Override
    protected @NotNull String serializeData() {
        return itemType.name() + ";" + checkInventory + ";" + questGiver;
    }

    /**
     * 아이템 이름 한국어 변환
     */
    private static String getItemNameKorean(Material material) {
        return switch (material) {
            case DIAMOND -> "다이아몬드";
            case IRON_INGOT -> "철괴";
            case GOLD_INGOT -> "금괴";
            case EMERALD -> "에메랄드";
            case COAL -> "석탄";
            case BREAD -> "빵";
            case WHEAT -> "밀";
            case GOLDEN_APPLE -> "황금 사과";
            case IRON_SWORD -> "철 검";
            case DIAMOND_SWORD -> "다이아몬드 검";
            case TNT -> "TNT";
            default -> material.name().toLowerCase().replace('_', ' ');
        };
    }

    /**
     * 아이템 타입 반환
     */
    public @NotNull Material getItemType() {
        return itemType;
    }

    /**
     * 인벤토리 체크 모드 여부
     */
    public boolean isCheckInventory() {
        return checkInventory;
    }
}