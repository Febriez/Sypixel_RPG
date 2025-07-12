package com.febrie.rpg.economy;

import com.febrie.rpg.util.ColorUtil;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * RPG 통화 타입 열거형
 * 게임 내 다양한 재화를 정의
 * <p>
 * 중요: 새로운 통화를 추가할 때는 반드시 언어 파일(ko_KR.json, en_US.json)에
 * currency.{id}.name과 currency.{id}.description을 추가해야 합니다.
 * 예시:
 * - currency.gold.name = "골드" (ko_KR) / "Gold" (en_US)
 * - currency.gold.description = "기본 재화입니다" (ko_KR) / "Basic currency" (en_US)
 *
 * @author Febrie, CoffeeTory
 */
public enum CurrencyType {

    GOLD("gold", Material.GOLD_INGOT, ColorUtil.GOLD, "💰", 1000000000L), // 최대 10억
    DIAMOND("diamond", Material.DIAMOND, ColorUtil.DIAMOND, "💎", 100000L), // 최대 10만
    EMERALD("emerald", Material.EMERALD, ColorUtil.EMERALD, "💚", 100000L), // 최대 10만
    GHAST_TEAR("ghast_tear", Material.GHAST_TEAR, ColorUtil.LEGENDARY, "✨", 10000L), // 최대 1만 (별가루)
    NETHER_STAR("nether_star", Material.NETHER_STAR, ColorUtil.EPIC, "⭐", 1000L); // 최대 1천 (별)

    private final String id;
    private final Material material;
    private final TextColor color;
    private final String icon;
    private final long maxAmount;

    CurrencyType(@NotNull String id, @NotNull Material material,
                 @NotNull TextColor color, @NotNull String icon, long maxAmount) {
        this.id = id;
        this.material = material;
        this.color = color;
        this.icon = icon;
        this.maxAmount = maxAmount;
    }

    /**
     * 통화 ID (언어 파일 키에 사용)
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * 통화를 나타내는 아이템 재료
     */
    @NotNull
    public Material getMaterial() {
        return material;
    }

    /**
     * 통화 색상
     */
    @NotNull
    public TextColor getColor() {
        return color;
    }

    /**
     * 통화 아이콘
     */
    @NotNull
    public String getIcon() {
        return icon;
    }

    /**
     * 최대 보유 가능 수량
     */
    public long getMaxAmount() {
        return maxAmount;
    }

    /**
     * ID로 통화 타입 찾기
     */
    @NotNull
    public static CurrencyType getById(@NotNull String id) {
        for (CurrencyType type : values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown currency type: " + id);
    }
}