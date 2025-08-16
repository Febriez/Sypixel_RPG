package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.BaseFirestoreService;
import com.febrie.rpg.dto.island.IslandRole;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import com.febrie.rpg.util.FirestoreUtils;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 플레이어 섬 데이터 Firestore 서비스
 * 플레이어별 섬 관련 데이터를 관리
 *
 * @author Febrie, CoffeeTory
 */
public class PlayerIslandDataService extends BaseFirestoreService<PlayerIslandDataDTO> {
    
    public PlayerIslandDataService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        super(plugin, firestore, "PlayerIslandData", PlayerIslandDataDTO.class);
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
        
        map.put("totalIslandResets", dto.totalIslandResets());
        map.put("totalContribution", dto.totalContribution());
        map.put("lastJoined", dto.lastJoined());
        map.put("lastActivity", dto.lastActivity());
        
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
            
            int totalIslandResets = FirestoreUtils.getInt(document, "totalIslandResets");
            long totalContribution = FirestoreUtils.getLong(document, "totalContribution");
            long lastJoined = FirestoreUtils.getLong(document, "lastJoined", System.currentTimeMillis());
            long lastActivity = FirestoreUtils.getLong(document, "lastActivity", lastJoined);
            
            return new PlayerIslandDataDTO(playerUuid, currentIslandId, role, totalIslandResets, totalContribution, lastJoined, lastActivity);
            
        } catch (Exception e) {
            LogUtil.warning("플레이어 섬 데이터 파싱 실패 [" + document.getId() + "]: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 모든 플레이어 섬 데이터 로드 (사전 로드용)
     * 서버 시작 시 모든 플레이어 데이터를 캐시에 로드하기 위해 사용
     */
    @NotNull
    public CompletableFuture<List<PlayerIslandDataDTO>> getAllPlayerData() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<PlayerIslandDataDTO> players = new ArrayList<>();
                
                // 모든 플레이어 데이터를 가져온 후 필터링
                var future = firestore.collection("PlayerIslandData").get();
                
                var querySnapshot = future.get(30, java.util.concurrent.TimeUnit.SECONDS);
                
                for (var doc : querySnapshot.getDocuments()) {
                    // currentIslandId가 null이 아닌 경우만 추가
                    if (doc.get("currentIslandId") != null) {
                        PlayerIslandDataDTO player = fromDocument(doc);
                        if (player != null) {
                            players.add(player);
                        }
                    }
                }
                
                LogUtil.debug("Firestore에서 " + players.size() + "명 플레이어 섬 데이터 로드");
                return players;
                
            } catch (Exception e) {
                LogUtil.error("모든 플레이어 섬 데이터 로드 실패", e);
                return new ArrayList<>();
            }
        });
    }
}