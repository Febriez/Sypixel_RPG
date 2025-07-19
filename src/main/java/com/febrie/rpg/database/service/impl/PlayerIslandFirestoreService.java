package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.BaseFirestoreService;
import com.febrie.rpg.dto.island.IslandRole;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 플레이어-섬 관계 데이터 Firestore 서비스
 * player-islands 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class PlayerIslandFirestoreService extends BaseFirestoreService<PlayerIslandDataDTO> {
    
    private static final String COLLECTION_NAME = "player-islands";
    
    public PlayerIslandFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        super(plugin, firestore, COLLECTION_NAME, PlayerIslandDataDTO.class);
    }
    
    @Override
    protected Map<String, Object> toMap(@NotNull PlayerIslandDataDTO dto) {
        Map<String, Object> map = new HashMap<>();
        
        map.put("playerUuid", dto.playerUuid());
        
        if (dto.currentIslandId() != null) {
            map.put("currentIslandId", dto.currentIslandId());
        }
        
        if (dto.role() != null) {
            map.put("role", dto.role().name());
        }
        
        map.put("lastJoined", dto.lastJoined());
        map.put("totalContribution", dto.totalContribution());
        
        return map;
    }
    
    @Override
    @Nullable
    protected PlayerIslandDataDTO fromDocument(@NotNull DocumentSnapshot document) {
        if (!document.exists()) {
            return null;
        }
        
        try {
            String playerUuid = document.getString("playerUuid");
            if (playerUuid == null) {
                playerUuid = document.getId();
            }
            
            String currentIslandId = document.getString("currentIslandId");
            
            IslandRole role = null;
            String roleStr = document.getString("role");
            if (roleStr != null) {
                role = IslandRole.valueOf(roleStr);
            }
            
            Long lastJoined = document.getLong("lastJoined");
            if (lastJoined == null) {
                lastJoined = System.currentTimeMillis();
            }
            
            Long totalContribution = document.getLong("totalContribution");
            if (totalContribution == null) {
                totalContribution = 0L;
            }
            
            return new PlayerIslandDataDTO(playerUuid, currentIslandId, role, lastJoined, totalContribution);
            
        } catch (Exception e) {
            LogUtil.warning("플레이어-섬 데이터 파싱 실패 [" + document.getId() + "]: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 플레이어의 섬 데이터 조회
     */
    @NotNull
    public CompletableFuture<PlayerIslandDataDTO> getPlayerIslandData(@NotNull UUID playerUuid) {
        return get(playerUuid.toString()).thenApply(data -> {
            if (data == null) {
                // 새 플레이어 데이터 생성
                return new PlayerIslandDataDTO(playerUuid.toString(), null, null, 
                        System.currentTimeMillis(), 0L);
            }
            return data;
        });
    }
    
    /**
     * 플레이어의 섬 데이터 저장
     */
    @NotNull
    public CompletableFuture<Void> savePlayerIslandData(@NotNull UUID playerUuid, 
                                                         @NotNull PlayerIslandDataDTO data) {
        return save(playerUuid.toString(), data);
    }
    
    /**
     * 플레이어를 섬에 가입시키기
     */
    @NotNull
    public CompletableFuture<Void> joinIsland(@NotNull UUID playerUuid, 
                                               @NotNull String islandId, 
                                               @NotNull IslandRole role) {
        PlayerIslandDataDTO data = new PlayerIslandDataDTO(
                playerUuid.toString(),
                islandId,
                role,
                System.currentTimeMillis(),
                0L  // 기여도는 0부터 시작
        );
        
        return savePlayerIslandData(playerUuid, data);
    }
    
    /**
     * 플레이어를 섬에서 탈퇴시키기
     */
    @NotNull
    public CompletableFuture<Void> leaveIsland(@NotNull UUID playerUuid) {
        return getPlayerIslandData(playerUuid).thenCompose(data -> {
            // 현재 섬 정보 제거, 기여도는 유지
            PlayerIslandDataDTO updated = new PlayerIslandDataDTO(
                    data.playerUuid(),
                    null,  // 섬 ID 제거
                    null,  // 역할 제거
                    data.lastJoined(),
                    data.totalContribution()  // 총 기여도는 유지
            );
            
            return savePlayerIslandData(playerUuid, updated);
        });
    }
    
    /**
     * 플레이어의 역할 변경
     */
    @NotNull
    public CompletableFuture<Void> updateRole(@NotNull UUID playerUuid, @NotNull IslandRole newRole) {
        return getPlayerIslandData(playerUuid).thenCompose(data -> {
            if (data.currentIslandId() == null) {
                return CompletableFuture.failedFuture(
                        new IllegalStateException("플레이어가 섬에 속해있지 않습니다"));
            }
            
            PlayerIslandDataDTO updated = new PlayerIslandDataDTO(
                    data.playerUuid(),
                    data.currentIslandId(),
                    newRole,
                    data.lastJoined(),
                    data.totalContribution()
            );
            
            return savePlayerIslandData(playerUuid, updated);
        });
    }
    
    /**
     * 기여도 증가
     */
    @NotNull
    public CompletableFuture<Void> addContribution(@NotNull UUID playerUuid, long amount) {
        return getPlayerIslandData(playerUuid).thenCompose(data -> {
            PlayerIslandDataDTO updated = new PlayerIslandDataDTO(
                    data.playerUuid(),
                    data.currentIslandId(),
                    data.role(),
                    data.lastJoined(),
                    data.totalContribution() + amount
            );
            
            return savePlayerIslandData(playerUuid, updated);
        });
    }
    
    /**
     * 플레이어가 섬에 속해있는지 확인
     */
    @NotNull
    public CompletableFuture<Boolean> hasIsland(@NotNull UUID playerUuid) {
        return getPlayerIslandData(playerUuid).thenApply(data -> 
                data.currentIslandId() != null);
    }
    
    /**
     * 플레이어의 현재 섬 ID 조회
     */
    @NotNull
    public CompletableFuture<String> getCurrentIslandId(@NotNull UUID playerUuid) {
        return getPlayerIslandData(playerUuid).thenApply(PlayerIslandDataDTO::currentIslandId);
    }
    
    /**
     * 플레이어의 현재 역할 조회
     */
    @NotNull
    public CompletableFuture<IslandRole> getCurrentRole(@NotNull UUID playerUuid) {
        return getPlayerIslandData(playerUuid).thenApply(PlayerIslandDataDTO::role);
    }
}