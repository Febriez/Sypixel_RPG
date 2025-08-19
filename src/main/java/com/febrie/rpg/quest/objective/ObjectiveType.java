package com.febrie.rpg.quest.objective;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
/**
 * 퀘스트 목표 타입 열거형
 * 지원하는 모든 목표 유형을 정의
 *
 * @author Febrie
 */
public enum ObjectiveType {

    KILL_MOB("몹 처치", Material.IRON_SWORD),
    KILL_PLAYER("플레이어 처치", Material.DIAMOND_SWORD),
    COLLECT_ITEM("아이템 수집", Material.CHEST),
    CRAFT_ITEM("아이템 제작", Material.CRAFTING_TABLE),
    INTERACT_NPC("NPC 방문", Material.PLAYER_HEAD),
    VISIT_LOCATION("지역 방문", Material.COMPASS),
    BREAK_BLOCK("블럭 파괴", Material.IRON_PICKAXE),
    PLACE_BLOCK("블럭 설치", Material.GRASS_BLOCK),
    DELIVER_ITEM("아이템 전달", Material.PAPER),
    PAY_CURRENCY("재화 지불", Material.GOLD_NUGGET),
    FISHING("낚시", Material.FISHING_ROD),
    HARVEST("농작물 수확", Material.WHEAT),
    EXPLORE("탐험", Material.MAP),
    SURVIVE("생존", Material.SHIELD),
    REACH_LEVEL("레벨 달성", Material.EXPERIENCE_BOTTLE);

    private final String displayName;
    private final Material icon;

    ObjectiveType(@NotNull String displayName, @NotNull Material icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    /**
     * 표시 이름 반환
     *
     * @return 한글 표시 이름
     */
    public @NotNull String getDisplayName() {
        return displayName;
    }

    /**
     * 아이콘 아이템 반환
     *
     * @return 아이콘으로 사용할 Material
     */
    public @NotNull Material getIcon() {
        return icon;
    }

    /**
     * ID로 타입 찾기
     *
     * @param name 타입 이름
     * @return 목표 타입
     */
    public static @NotNull ObjectiveType fromName(@NotNull String name) {
        try {
            return ObjectiveType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown objective type: " + name);
        }
    }
}