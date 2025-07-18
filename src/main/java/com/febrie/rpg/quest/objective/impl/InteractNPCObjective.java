package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * NPC 방문 퀘스트 목표
 * 특정 NPC와 상호작용
 * 
 * 사용법:
 * // 퀘스트 ID로 trait 체크 (권장)
 * new InteractNPCObjective("talk_merchant", questId)
 * 
 * // UUID로 NPC 지정 (기존 방식)
 * UUID npcUuid = entity.getUniqueId();
 * new InteractNPCObjective("talk_merchant", npcUuid)
 * 
 * // Citizens NPC ID로 지정 (기존 방식)
 * new InteractNPCObjective("talk_merchant", 42) // NPC ID가 42인 경우
 * 
 * // 빌더 패턴 사용
 * InteractNPCObjective.builder()
 *     .id("talk_merchant")
 *     .questId(questId)
 *     .build()
 * 
 *
 * @author Febrie
 */
public class InteractNPCObjective extends BaseObjective {

    private final @Nullable UUID npcUuid;
    private final @Nullable Integer citizensNpcId; // Citizens NPC ID (legacy)
    private final @Nullable QuestID questId; // Quest ID for trait-based checking (legacy)
    private final @Nullable String npcId; // NPC ID for trait-based checking

    /**
     * NPC ID 기반 생성자 (trait 체크용 - 권장)
     *
     * @param id    목표 ID
     * @param npcId NPC ID
     */
    public InteractNPCObjective(@NotNull String id, @NotNull String npcId) {
        super(id, 1);
        this.npcId = Objects.requireNonNull(npcId);
        this.npcUuid = null;
        this.citizensNpcId = null;
        this.questId = null;
    }
    
    /**
     * Quest ID 기반 생성자 (레거시)
     *
     * @param id      목표 ID
     * @param questId 퀘스트 ID
     */
    @Deprecated
    public InteractNPCObjective(@NotNull String id, @NotNull QuestID questId) {
        super(id, 1);
        this.questId = Objects.requireNonNull(questId);
        this.npcUuid = null;
        this.citizensNpcId = null;
        this.npcId = null;
    }
    
    /**
     * UUID 기반 생성자 (가장 안전)
     *
     * @param id      목표 ID
     * @param npcUuid NPC UUID
     */
    public InteractNPCObjective(@NotNull String id, @NotNull UUID npcUuid) {
        super(id, 1);
        this.npcUuid = Objects.requireNonNull(npcUuid);
        this.citizensNpcId = null;
        this.questId = null;
        this.npcId = null;
    }
    
    /**
     * Citizens NPC ID 기반 생성자 (레거시)
     *
     * @param id    목표 ID
     * @param citizensNpcId Citizens NPC ID
     */
    @Deprecated
    public InteractNPCObjective(@NotNull String id, int citizensNpcId) {
        super(id, 1);
        this.npcUuid = null;
        this.citizensNpcId = citizensNpcId;
        this.questId = null;
        this.npcId = null;
    }
    

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.INTERACT_NPC;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        String status;
        if (npcId != null) {
            status = "NPC와 대화하기"; // NPC ID 기반
        } else if (questId != null) {
            status = "퀘스트 NPC와 대화하기";
        } else if (npcUuid != null) {
            status = "NPC (UUID: " + npcUuid.toString().substring(0, 8) + "...)";
        } else if (citizensNpcId != null) {
            status = "NPC (ID: " + citizensNpcId + ")";
        } else {
            status = "Unknown NPC";
        }
        
        if (progress.isCompleted()) {
            status += " ✓";
        }
        return status;
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof PlayerInteractEntityEvent interactEvent)) {
            return false;
        }

        // 플레이어 확인
        if (!interactEvent.getPlayer().equals(player)) {
            return false;
        }

        Entity entity = interactEvent.getRightClicked();
        
        // NPC ID로 trait 확인 (새로운 방식 - 권장)
        if (npcId != null) {
            // Citizens API 확인
            if (entity.hasMetadata("NPC")) {
                net.citizensnpcs.api.npc.NPC npc = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().getNPC(entity);
                if (npc != null && npc.hasTrait(RPGQuestTrait.class)) {
                    RPGQuestTrait trait = npc.getTrait(RPGQuestTrait.class);
                    return npcId.equals(trait.getNpcId());
                }
            }
            return false;
        }
        
        // Quest ID로 trait 확인 (레거시)
        if (questId != null) {
            // Citizens API 확인
            if (entity.hasMetadata("NPC")) {
                net.citizensnpcs.api.npc.NPC npc = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().getNPC(entity);
                if (npc != null && npc.hasTrait(RPGQuestTrait.class)) {
                    RPGQuestTrait trait = npc.getTrait(RPGQuestTrait.class);
                    return trait.hasQuest(questId);
                }
            }
            return false;
        }
        
        // UUID로 확인 (가장 안전한 방법)
        if (npcUuid != null) {
            return entity.getUniqueId().equals(npcUuid);
        }
        
        // Citizens NPC ID로 확인 (레거시)
        if (citizensNpcId != null) {
            // Citizens API 확인
            if (entity.hasMetadata("NPC")) {
                net.citizensnpcs.api.npc.NPC npc = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().getNPC(entity);
                if (npc != null) {
                    return npc.getId() == citizensNpcId;
                }
            }
            return false;
        }

        return false;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
    }

    @Override
    protected @NotNull String serializeData() {
        if (npcId != null) {
            return "id:" + npcId;
        } else if (questId != null) {
            return "quest:" + questId.name();
        } else if (npcUuid != null) {
            return "uuid:" + npcUuid.toString();
        } else if (citizensNpcId != null) {
            return "citizens:" + citizensNpcId;
        }
        return "unknown";
    }

    /**
     * 직렬화된 데이터에서 객체 생성 (역직렬화)
     */
    public static InteractNPCObjective deserialize(@NotNull String id, @NotNull String data) {
        if (data.startsWith("id:")) {
            String npcId = data.substring(3);
            return new InteractNPCObjective(id, npcId);
        } else if (data.startsWith("quest:")) {
            String questIdStr = data.substring(6);
            try {
                QuestID questId = QuestID.valueOf(questIdStr);
                return new InteractNPCObjective(id, questId);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid Quest ID format: " + questIdStr);
            }
        } else if (data.startsWith("uuid:")) {
            String uuidStr = data.substring(5);
            try {
                UUID uuid = UUID.fromString(uuidStr);
                return new InteractNPCObjective(id, uuid);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UUID format: " + uuidStr);
            }
        } else if (data.startsWith("citizens:")) {
            String idStr = data.substring(9);
            try {
                int citizensNpcId = Integer.parseInt(idStr);
                return new InteractNPCObjective(id, citizensNpcId);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid Citizens NPC ID format: " + idStr);
            }
        }
        
        throw new IllegalArgumentException("Unknown NPC data format: " + data);
    }

    /**
     * NPC UUID 반환
     */
    public @Nullable UUID getNpcUuid() {
        return npcUuid;
    }
    
    /**
     * Citizens NPC ID 반환 (레거시)
     */
    public @Nullable Integer getCitizensNpcId() {
        return citizensNpcId;
    }
    
    /**
     * Quest ID 반환
     */
    public @Nullable QuestID getQuestId() {
        return questId;
    }
    
    /**
     * NPC ID 반환
     */
    public @Nullable String getNpcId() {
        return npcId;
    }
    
    
    /**
     * 빌더 클래스
     */
    public static class Builder {
        private String id;
        private UUID npcUuid;
        private Integer citizensNpcId;
        private QuestID questId;
        private String npcId;
        
        public Builder id(@NotNull String id) {
            this.id = id;
            return this;
        }
        
        public Builder npcId(@NotNull String npcId) {
            this.npcId = npcId;
            this.npcUuid = null;
            this.citizensNpcId = null;
            this.questId = null;
            return this;
        }
        
        public Builder questId(@NotNull QuestID questId) {
            this.questId = questId;
            this.npcUuid = null;
            this.citizensNpcId = null;
            this.npcId = null;
            return this;
        }
        
        public Builder npcUuid(@NotNull UUID uuid) {
            this.npcUuid = uuid;
            this.citizensNpcId = null;
            this.questId = null;
            this.npcId = null;
            return this;
        }
        
        public Builder citizensNpcId(int id) {
            this.citizensNpcId = id;
            this.npcUuid = null;
            this.questId = null;
            this.npcId = null;
            return this;
        }
        
        
        public InteractNPCObjective build() {
            if (id == null) {
                throw new IllegalStateException("ID is required");
            }
            
            if (npcId != null) {
                return new InteractNPCObjective(id, npcId);
            } else if (questId != null) {
                return new InteractNPCObjective(id, questId);
            } else if (npcUuid != null) {
                return new InteractNPCObjective(id, npcUuid);
            } else if (citizensNpcId != null) {
                return new InteractNPCObjective(id, citizensNpcId);
            } else {
                throw new IllegalStateException("NPC identifier (NPC ID, QuestID, UUID, or Citizens ID) is required");
            }
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public @Nullable String validate() {
        if (npcId == null && questId == null && npcUuid == null && citizensNpcId == null) {
            return "InteractNPCObjective '" + id + "': NPC ID, Quest ID, NPC UUID 또는 Citizens ID가 설정되지 않았습니다.";
        }
        
        // Citizens NPC ID 유효성 검증
        if (citizensNpcId != null && citizensNpcId <= 0) {
            return "InteractNPCObjective '" + id + "': 잘못된 Citizens NPC ID (" + citizensNpcId + ")입니다. ID는 1 이상이어야 합니다.";
        }
        
        return null; // 유효함
    }
}