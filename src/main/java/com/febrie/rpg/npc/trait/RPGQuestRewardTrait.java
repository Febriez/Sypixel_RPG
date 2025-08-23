package com.febrie.rpg.npc.trait;

import com.febrie.rpg.quest.QuestID;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

import java.util.ArrayList;
import java.util.List;

/**
 * 퀘스트 보상 NPC를 위한 커스텀 Trait
 * 플레이어가 퀘스트를 완료한 후 이 NPC와 상호작용하여 보상을 받음
 *
 * @author Febrie
 */
@TraitName("rpgquestreward")
public class RPGQuestRewardTrait extends Trait {

    @Persist("questIds")
    private List<String> questIds = new ArrayList<>();
    
    @Persist("rewardNpcId")
    private String rewardNpcId = null;

    public RPGQuestRewardTrait() {
        super("rpgquestreward");
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
     * 보상 NPC ID 설정
     */
    public void setRewardNpcId(String rewardNpcId) {
        this.rewardNpcId = rewardNpcId;
    }
    
    /**
     * 보상 NPC ID 조회
     */
    public String getRewardNpcId() {
        return rewardNpcId;
    }
    
    /**
     * 보상 NPC ID가 설정되어 있는지 확인
     */
    public boolean hasRewardNpcId() {
        return rewardNpcId != null && !rewardNpcId.isEmpty();
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
     * 보상 NPC ID 초기화
     */
    public void clearRewardNpcId() {
        this.rewardNpcId = null;
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

}