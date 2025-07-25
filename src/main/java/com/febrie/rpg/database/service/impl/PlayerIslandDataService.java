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

/**
 * 플레이어 섬 데이터 Firestore 서비스
 * 플레이어별 섬 관련 데이터를 관리
 *
 * @author Febrie, CoffeeTory
 */
public class PlayerIslandDataService extends BaseFirestoreService<PlayerIslandDataDTO> {
    
    public PlayerIslandDataService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        super(plugin, firestore, "playerIslandData", PlayerIslandDataDTO.class);
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
            
            Integer totalIslandResets = document.getLong("totalIslandResets") != null 
                ? document.getLong("totalIslandResets").intValue() 
                : 0;
            
            Long totalContribution = document.getLong("totalContribution");
            if (totalContribution == null) {
                totalContribution = 0L;
            }
            
            Long lastJoined = document.getLong("lastJoined");
            if (lastJoined == null) {
                lastJoined = System.currentTimeMillis();
            }
            
            Long lastActivity = document.getLong("lastActivity");
            if (lastActivity == null) {
                lastActivity = lastJoined;
            }
            
            return new PlayerIslandDataDTO(playerUuid, currentIslandId, role, totalIslandResets, totalContribution, lastJoined, lastActivity);
            
        } catch (Exception e) {
            LogUtil.warning("플레이어 섬 데이터 파싱 실패 [" + document.getId() + "]: " + e.getMessage());
            return null;
        }
    }
}