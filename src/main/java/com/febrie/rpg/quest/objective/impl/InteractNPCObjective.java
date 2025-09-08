package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * NPC 상호작용 퀘스트 목표
 * Citizens NPC와 상호작용시 진행
 * 
 * 사용법:
 * new InteractNPCObjective("talk_merchant", "village_merchant", 1)
 * 
 * NPC에 RPGQuestTrait를 부착하고 npcId를 설정하여 사용
 *
 * @author Febrie
 */
public class InteractNPCObjective extends BaseObjective {

    // NPC 식별
    private final @NotNull String npcId; // Trait에 설정된 NPC ID

    /**
     * NPC ID 기반 생성자
     * 
     * @param id 목표 ID  
     * @param npcId NPC ID (trait에 등록된 ID)
     */
    public InteractNPCObjective(@NotNull String id, @NotNull String npcId) {
        super(id, 1);
        this.npcId = Objects.requireNonNull(npcId, "NPC ID cannot be null");
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.INTERACT_NPC;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        return npcId + "와 대화 " + getProgressString(progress);
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof PlayerInteractEntityEvent interactEvent)) {
            return false;
        }

        Entity entity = interactEvent.getRightClicked();

        // 플레이어 확인
        if (!interactEvent.getPlayer().equals(player)) {
            return false;
        }

        // 이미 상호작용한 경우 재상호작용 방지 (제거 - QuestProgress가 이미 완료 여부를 추적함)

        // Citizens NPC인지 확인
        if (!entity.hasMetadata("NPC")) {
            return false;
        }
        
        // NPC에서 RPGQuestTrait 확인
        net.citizensnpcs.api.npc.NPC npc = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().getNPC(entity);
        if (npc == null) {
            return false;
        }
        
        // Trait에서 NPC ID 확인
        RPGQuestTrait trait = npc.getTraitNullable(RPGQuestTrait.class);
        if (trait == null || !trait.hasNpcId()) {
            return false;
        }
        
        String traitNpcId = trait.getNpcId();
        
        return npcId.equals(traitNpcId);
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
    }

    @Override
    protected @NotNull String serializeData() {
        return "id:" + npcId;
    }

    /**
     * 직렬화된 데이터에서 객체 생성 (역직렬화)
     */
    public static InteractNPCObjective deserialize(@NotNull String id, @NotNull String data) {
        if (data.startsWith("id:")) {
            String npcId = data.substring(3);
            return new InteractNPCObjective(id, npcId);
        }
        throw new IllegalArgumentException("Unknown serialization format: " + data);
    }

    /**
     * NPC ID 반환
     */
    public @NotNull String getNpcId() {
        return npcId;
    }
    
    
    /**
     * 빌더 클래스
     */
    public static class Builder {
        private String id;
        private String npcId;
        
        public Builder id(@NotNull String id) {
            this.id = id;
            return this;
        }
        
        public Builder npcId(@NotNull String npcId) {
            this.npcId = npcId;
            return this;
        }
        
        public InteractNPCObjective build() {
            if (id == null) {
                throw new IllegalStateException("ID is required");
            }
            if (npcId == null) {
                throw new IllegalStateException("NPC ID is required");
            }
            
            return new InteractNPCObjective(id, npcId);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public @Nullable String validate() {
        // NPC ID가 비어있지 않으면 유효함
        if (npcId.trim().isEmpty()) {
            return "InteractNPCObjective '" + id + "': NPC ID가 비어있습니다.";
        }
        
        return null; // 유효함
    }
}