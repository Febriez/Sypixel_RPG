package com.febrie.rpg.quest.reward;

/**
 * 퀘스트 보상 지급 방식
 * 
 * @author Febrie
 */
public enum RewardDeliveryType {
    /**
     * 즉시 지급 - 재화(경험치, 골드 등)는 퀘스트 완료 시 바로 지급
     * 아이템은 인벤토리 공간이 없으면 바닥에 드롭
     */
    INSTANT("quest.reward.delivery.instant"),
    
    /**
     * 우편 지급 - 모든 보상을 우편함으로 전송
     * 아이템과 재화 모두 우편으로 수령
     */
    MAIL("quest.reward.delivery.mail"),
    
    /**
     * NPC 방문 수령 - 보상 NPC를 찾아가서 수령
     * 기존 방식과 동일
     */
    NPC_VISIT("quest.reward.delivery.npc_visit");
    
    private final String translationKey;
    
    RewardDeliveryType(String translationKey) {
        this.translationKey = translationKey;
    }
    
    public String getTranslationKey() {
        return translationKey;
    }
    
    /**
     * 재화(경험치, 골드)를 즉시 지급할 수 있는지 확인
     */
    public boolean canInstantDeliverCurrency() {
        return this == INSTANT;
    }
    
    /**
     * 아이템을 즉시 지급할 수 있는지 확인
     */
    public boolean canInstantDeliverItems() {
        return this == INSTANT;
    }
    
    /**
     * 우편으로 보상을 전송하는지 확인
     */
    public boolean isMailDelivery() {
        return this == MAIL;
    }
    
    /**
     * NPC 방문이 필요한지 확인
     */
    public boolean requiresNpcVisit() {
        return this == NPC_VISIT;
    }
}