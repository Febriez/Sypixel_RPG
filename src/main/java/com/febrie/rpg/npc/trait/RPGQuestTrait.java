package com.febrie.rpg.npc.trait;

import com.febrie.rpg.quest.QuestID;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.Player;

/**
 * RPG 퀘스트 NPC를 위한 커스텀 Trait
 * Citizens의 Trait 시스템을 사용해 NPC 데이터를 영구 저장
 *
 * @author Febrie
 */
@TraitName("rpgquest")
public class RPGQuestTrait extends Trait {

    @Persist("questId")
    private String questId;

    @Persist("npcType")
    private String npcType = "QUEST";

    public RPGQuestTrait() {
        super("rpgquest");
    }

    /**
     * 퀘스트 ID 설정
     */
    public void setQuestId(QuestID questId) {
        this.questId = questId.name();
    }

    /**
     * 퀘스트 ID 조회
     */
    public QuestID getQuestId() {
        if (questId == null || questId.isEmpty()) {
            return null;
        }

        try {
            return QuestID.valueOf(questId);
        } catch (IllegalArgumentException e) {
            return null;
        }
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
        // NPC가 생성될 때 기본 설정
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
        // 스폰 시 추가 설정이 필요한 경우
    }

    /**
     * NPC가 디스폰될 때 호출
     */
    @Override
    public void onDespawn() {
        super.onDespawn();
    }
}