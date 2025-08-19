package com.febrie.rpg.npc.trait;

import com.febrie.rpg.quest.QuestID;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import net.kyori.adventure.text.Component;
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
    
    @Persist("npcId")
    private String npcId = null;

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
     * NPC ID 설정
     */
    public void setNpcId(String npcId) {
        this.npcId = npcId;
    }
    
    /**
     * NPC ID 조회
     */
    public String getNpcId() {
        return npcId;
    }
    
    /**
     * NPC ID가 설정되어 있는지 확인
     */
    public boolean hasNpcId() {
        return npcId != null && !npcId.isEmpty();
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
     * NPC ID 초기화
     */
    public void clearNpcId() {
        this.npcId = null;
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