package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
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
 * // UUID로 NPC 지정 (가장 안전)
 * UUID npcUuid = entity.getUniqueId();
 * new InteractNPCObjective("talk_merchant", npcUuid)
 * 
 * // Citizens NPC ID로 지정 (권장)
 * new InteractNPCObjective("talk_merchant", 42) // NPC ID가 42인 경우
 * 
 * 
 * // 빌더 패턴 사용
 * InteractNPCObjective.builder()
 *     .id("talk_merchant")
 *     .npcUuid(npcUuid)
 *     .build()
 * 
 * // 빌더 패턴으로 NPC ID 사용
 * InteractNPCObjective.builder()
 *     .id("talk_merchant")
 *     .npcId(42)
 *     .build()
 * 
 *
 * @author Febrie
 */
public class InteractNPCObjective extends BaseObjective {

    private final @Nullable UUID npcUuid;
    private final @Nullable Integer npcId; // Citizens NPC ID

    /**
     * UUID 기반 생성자 (가장 안전)
     *
     * @param id      목표 ID
     * @param npcUuid NPC UUID
     */
    public InteractNPCObjective(@NotNull String id, @NotNull UUID npcUuid) {
        super(id, 1);
        this.npcUuid = Objects.requireNonNull(npcUuid);
        this.npcId = null;
    }
    
    /**
     * Citizens NPC ID 기반 생성자 (권장)
     *
     * @param id    목표 ID
     * @param npcId Citizens NPC ID
     */
    public InteractNPCObjective(@NotNull String id, int npcId) {
        super(id, 1);
        this.npcUuid = null;
        this.npcId = npcId;
    }
    

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.INTERACT_NPC;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        String status;
        if (npcUuid != null) {
            status = "NPC (UUID: " + npcUuid.toString().substring(0, 8) + "...)";
        } else if (npcId != null) {
            status = "NPC (ID: " + npcId + ")";
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
        
        // UUID로 확인 (가장 안전한 방법)
        if (npcUuid != null) {
            return entity.getUniqueId().equals(npcUuid);
        }
        
        // Citizens NPC ID로 확인
        if (npcId != null) {
            // Citizens API 확인
            if (entity.hasMetadata("NPC")) {
                net.citizensnpcs.api.npc.NPC npc = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().getNPC(entity);
                if (npc != null) {
                    return npc.getId() == npcId;
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
        if (npcUuid != null) {
            return "uuid:" + npcUuid.toString();
        } else if (npcId != null) {
            return "id:" + npcId;
        }
        return "unknown";
    }

    /**
     * 직렬화된 데이터에서 객체 생성 (역직렬화)
     */
    public static InteractNPCObjective deserialize(@NotNull String id, @NotNull String data) {
        if (data.startsWith("uuid:")) {
            String uuidStr = data.substring(5);
            try {
                UUID uuid = UUID.fromString(uuidStr);
                return new InteractNPCObjective(id, uuid);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UUID format: " + uuidStr);
            }
        } else if (data.startsWith("id:")) {
            String idStr = data.substring(3);
            try {
                int npcId = Integer.parseInt(idStr);
                return new InteractNPCObjective(id, npcId);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid NPC ID format: " + idStr);
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
     * NPC ID 반환 (Citizens)
     */
    public @Nullable Integer getNpcId() {
        return npcId;
    }
    
    
    /**
     * 빌더 클래스
     */
    public static class Builder {
        private String id;
        private UUID npcUuid;
        private Integer npcId;
        
        public Builder id(@NotNull String id) {
            this.id = id;
            return this;
        }
        
        public Builder npcUuid(@NotNull UUID uuid) {
            this.npcUuid = uuid;
            this.npcId = null;
            return this;
        }
        
        public Builder npcId(int id) {
            this.npcId = id;
            this.npcUuid = null;
            return this;
        }
        
        
        public InteractNPCObjective build() {
            if (id == null) {
                throw new IllegalStateException("ID is required");
            }
            
            if (npcUuid != null) {
                return new InteractNPCObjective(id, npcUuid);
            } else if (npcId != null) {
                return new InteractNPCObjective(id, npcId);
            } else {
                throw new IllegalStateException("NPC identifier (UUID, ID, or QuestNPC) is required");
            }
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public @Nullable String validate() {
        if (npcUuid == null && npcId == null) {
            return "InteractNPCObjective '" + id + "': NPC UUID 또는 ID가 설정되지 않았습니다.";
        }
        
        // Citizens NPC ID 유효성 검증
        if (npcId != null && npcId <= 0) {
            return "InteractNPCObjective '" + id + "': 잘못된 NPC ID (" + npcId + ")입니다. ID는 1 이상이어야 합니다.";
        }
        
        return null; // 유효함
    }
}