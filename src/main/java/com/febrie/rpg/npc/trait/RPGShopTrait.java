package com.febrie.rpg.npc.trait;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.Player;

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
     * 플레이어가 NPC와 상호작용할 때 호출
     */
    public void onInteract(Player player) {
        // 이 메서드는 NPCInteractListener에서 호출됩니다
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
     * Trait가 NPC에서 제거될 때 호출
     */
    @Override
    public void onRemove() {
        super.onRemove();
    }

    /**
     * NPC가 스폰될 때 호출
     */
    @Override
    public void onSpawn() {
        super.onSpawn();
    }

    /**
     * NPC가 디스폰될 때 호출
     */
    @Override
    public void onDespawn() {
        super.onDespawn();
    }
}