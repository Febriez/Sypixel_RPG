package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
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
    private final String questGiver;

    /**
     * 기본 생성자
     *
     * @param id       목표 ID
     * @param itemType 제작할 아이템 타입
     * @param amount   제작 수량
     */
    public CraftItemObjective(@NotNull String id, @NotNull Material itemType, int amount) {
        this(id, itemType, amount, "마을 대장장이");
    }

    /**
     * 퀘스트 제공자 포함 생성자
     *
     * @param id         목표 ID
     * @param itemType   제작할 아이템 타입
     * @param amount     제작 수량
     * @param questGiver 퀘스트 제공자
     */
    public CraftItemObjective(@NotNull String id, @NotNull Material itemType, int amount,
                              @NotNull String questGiver) {
        super(id, amount, "quest.objective.craft_item",
                "item_key", itemType.translationKey(),
                "amount", String.valueOf(amount));
        this.itemType = Objects.requireNonNull(itemType);
        this.questGiver = Objects.requireNonNull(questGiver);
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.CRAFT_ITEM;
    }

    @Override
    public @NotNull String getDescription(boolean isKorean) {
        String itemNameKo = getItemNameKorean(itemType);

        return isKorean ?
                "퀘스트를 준 사람: " + questGiver + "\n\n" +
                        "자네의 손재주가 필요하다네. " + itemNameKo + " " + requiredAmount + "개를 제작해 주게나. " +
                        "이 아이템들은 마을의 방어를 위해 꼭 필요한 것들이라네. " +
                        "재료는 자네가 직접 구해야 할 것이네." :

                "Quest Giver: " + questGiver + "\n\n" +
                        "I need your crafting skills. Please craft " + requiredAmount + " " +
                        itemType.name().toLowerCase().replace('_', ' ') + ". " +
                        "These items are essential for our village's defense. " +
                        "You'll need to gather the materials yourself.";
    }

    @Override
    public @NotNull String getGiverName(boolean isKorean) {
        return questGiver;
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
        return itemType.name() + ";" + questGiver;
    }

    /**
     * 아이템 이름 한국어 변환
     */
    private static String getItemNameKorean(Material material) {
        material.translationKey();
        return switch (material) {
            case DIAMOND_SWORD -> "다이아몬드 검";
            case IRON_SWORD -> "철 검";
            case IRON_CHESTPLATE -> "철 흉갑";
            case DIAMOND_CHESTPLATE -> "다이아몬드 흉갑";
            case GOLDEN_APPLE -> "황금 사과";
            case TNT -> "TNT";
            case IRON_PICKAXE -> "철 곡괭이";
            case DIAMOND_PICKAXE -> "다이아몬드 곡괭이";
            case CRAFTING_TABLE -> "제작대";
            case ENCHANTING_TABLE -> "마법 부여대";
            case ANVIL -> "모루";
            case BEACON -> "신호기";
            case NETHERITE_SWORD -> "네더라이트 검";
            case TOTEM_OF_UNDYING -> "불사의 토템";
            case ELYTRA -> "겉날개";
            case WITHER_SKELETON_SKULL -> "위더 스켈레톤 머리";
            default -> material.name().toLowerCase().replace('_', ' ');
        };
    }

    /**
     * 아이템 타입 반환
     */
    public @NotNull Material getItemType() {
        return itemType;
    }
}