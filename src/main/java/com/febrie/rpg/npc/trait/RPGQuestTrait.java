package com.febrie.rpg.npc.trait;

import com.febrie.rpg.quest.QuestID;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * RPG 퀘스트 NPC를 위한 커스텀 Trait
 * Citizens의 Trait 시스템을 사용해 NPC 데이터를 영구 저장
 *
 * @author Febrie
 */
@TraitName("rpgquest")
public class RPGQuestTrait extends Trait {

    @Persist("questIds")
    private List<String> questIds = new ArrayList<>();

    @Persist("npcType")
    private String npcType = "QUEST";

    public RPGQuestTrait() {
        super("rpgquest");
    }

    /**
     * 퀘스트 ID 추가
     */
    public void addQuest(QuestID questId) {
        String questIdStr = questId.name();
        if (!questIds.contains(questIdStr)) {
            questIds.add(questIdStr);
        }
    }
    
    /**
     * 퀘스트 ID 제거
     */
    public void removeQuest(QuestID questId) {
        questIds.remove(questId.name());
    }

    /**
     * 모든 퀘스트 ID 조회
     */
    public List<QuestID> getQuestIds() {
        List<QuestID> result = new ArrayList<>();
        for (String id : questIds) {
            try {
                result.add(QuestID.valueOf(id));
            } catch (IllegalArgumentException e) {
                // Invalid quest ID, skip
            }
        }
        return result;
    }
    
    /**
     * 특정 퀘스트가 있는지 확인
     */
    public boolean hasQuest(QuestID questId) {
        return questIds.contains(questId.name());
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