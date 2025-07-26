package com.febrie.rpg.npc.trait;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * RPG 상점 NPC를 위한 커스텀 Trait
 * Citizens의 Trait 시스템을 사용해 NPC 데이터를 영구 저장
 *
 * @author Febrie
 */
@TraitName("rpgshop")
public class RPGShopTrait extends Trait {

    @Persist("shopType")
    private String shopType = "GENERAL";

    @Persist("npcType")
    private String npcType = "SHOP";

    @Persist("welcomeText")
    private String welcomeText = "어서오세요! 무엇을 찾고 계신가요?";

    @Persist("goodbyeText")
    private String goodbyeText = "감사합니다! 또 오세요!";

    @Persist("shopTitle")
    private String shopTitle = "상점";
    
    // 상점 아이템 목록
    private final List<ShopItem> shopItems = new ArrayList<>();

    public RPGShopTrait() {
        super("rpgshop");
    }

    /**
     * 상점 타입 설정
     */
    public void setShopType(String shopType) {
        this.shopType = shopType;
    }

    /**
     * 상점 타입 조회
     */
    public String getShopType() {
        return shopType;
    }

    /**
     * NPC 타입 설정
     */
    public void setNpcType(String npcType) {
        this.npcType = npcType;
    }

    /**
     * NPC 타입 조회
     */
    public String getNpcType() {
        return npcType;
    }

    /**
     * 환영 텍스트 설정
     */
    public void setWelcomeText(String welcomeText) {
        this.welcomeText = welcomeText;
    }

    /**
     * 환영 텍스트 조회
     */
    public String getWelcomeText() {
        return welcomeText;
    }

    /**
     * 작별 텍스트 설정
     */
    public void setGoodbyeText(String goodbyeText) {
        this.goodbyeText = goodbyeText;
    }

    /**
     * 작별 텍스트 조회
     */
    public String getGoodbyeText() {
        return goodbyeText;
    }

    /**
     * 상점 제목 설정
     */
    public void setShopTitle(String shopTitle) {
        this.shopTitle = shopTitle;
    }

    /**
     * 상점 제목 조회
     */
    public String getShopTitle() {
        return shopTitle;
    }


    /**
     * Trait가 NPC에 추가될 때 호출
     */
    @Override
    public void onAttach() {
        super.onAttach();
        if (npc != null) {
            npc.setProtected(true);
        }
    }

    
    /**
     * 상점 아이템 추가
     */
    public void addShopItem(@NotNull ItemStack item, long buyPrice, long sellPrice, boolean sellable) {
        shopItems.add(new ShopItem(item, buyPrice, sellPrice, sellable));
    }
    
    /**
     * 상점 아이템 목록 조회
     */
    @NotNull
    public List<ShopItem> getShopItems() {
        return new ArrayList<>(shopItems);
    }
    
    /**
     * 상점 아이템 정보
     */
    public static class ShopItem {
        private final ItemStack item;
        private final long buyPrice;
        private final long sellPrice;
        private final boolean sellable;
        
        public ShopItem(@NotNull ItemStack item, long buyPrice, long sellPrice, boolean sellable) {
            this.item = item.clone();
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
            this.sellable = sellable;
        }
        
        @NotNull
        public ItemStack getItem() {
            return item.clone();
        }
        
        public long getBuyPrice() {
            return buyPrice;
        }
        
        public long getSellPrice() {
            return sellPrice;
        }
        
        public boolean isSellable() {
            return sellable;
        }
    }
}