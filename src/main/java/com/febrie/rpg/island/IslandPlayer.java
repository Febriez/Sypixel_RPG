package com.febrie.rpg.island;

import com.febrie.rpg.dto.island.IslandRole;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import com.febrie.rpg.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 섬 플레이어 관리 클래스
 * 플레이어와 섬 간의 관계를 관리
 *
 * @author Febrie, CoffeeTory
 */
public class IslandPlayer {
    
    private final String playerUuid;
    private final String playerName;
    private PlayerIslandDataDTO playerData;
    
    /**
     * 기존 플레이어 데이터로 생성
     */
    public IslandPlayer(@NotNull String playerUuid, @NotNull String playerName, @Nullable PlayerIslandDataDTO playerData) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.playerData = playerData;
    }
    
    /**
     * 새 플레이어 생성
     */
    public static IslandPlayer createNew(@NotNull String playerUuid, @NotNull String playerName) {
        return new IslandPlayer(playerUuid, playerName, PlayerIslandDataDTO.createNew(playerUuid));
    }
    
    /**
     * 섬 소유 여부
     */
    public boolean hasIsland() {
        return playerData != null && playerData.hasIsland();
    }
    
    /**
     * 현재 소속된 섬 ID
     */
    @Nullable
    public String getCurrentIslandId() {
        return playerData != null ? playerData.currentIslandId() : null;
    }
    
    /**
     * 섬 역할
     */
    @Nullable
    public IslandRole getCurrentRole() {
        return playerData != null ? playerData.role() : null;
    }
    
    /**
     * 섬 소유자인지 확인
     */
    public boolean isIslandOwner() {
        return getCurrentRole() == IslandRole.OWNER;
    }
    
    /**
     * 섬 가입
     */
    public CompletableFuture<Boolean> joinIsland(@NotNull String islandId, @NotNull IslandRole role) {
        if (hasIsland()) {
            LogUtil.warning(playerName + "님은 이미 섬에 소속되어 있습니다.");
            return CompletableFuture.completedFuture(false);
        }
        
        if (playerData == null) {
            playerData = PlayerIslandDataDTO.createNew(playerUuid);
        }
        
        playerData = playerData.joinIsland(islandId, role);
        LogUtil.info(String.format("%s님이 섬에 가입했습니다. 역할: %s", playerName, role));
        
        return CompletableFuture.completedFuture(true);
    }
    
    /**
     * 섬 탈퇴
     */
    public CompletableFuture<Boolean> leaveIsland() {
        if (!hasIsland()) {
            LogUtil.warning(playerName + "님은 섬에 소속되어 있지 않습니다.");
            return CompletableFuture.completedFuture(false);
        }
        
        playerData = playerData.leaveIsland();
        LogUtil.info(playerName + "님이 섬을 떠났습니다.");
        
        return CompletableFuture.completedFuture(true);
    }
    
    /**
     * 섬 초기화 가능 여부
     */
    public boolean canResetIsland() {
        return playerData != null && playerData.canResetIsland();
    }
    
    /**
     * 섬 초기화 횟수 증가
     */
    public void incrementResetCount() {
        if (playerData != null) {
            playerData = playerData.incrementResetCount();
        }
    }
    
    /**
     * 총 플레이 시간 (밀리초)
     */
    public long getTotalPlaytime() {
        return playerData != null ? (System.currentTimeMillis() - playerData.lastJoined()) : 0L;
    }
    
    /**
     * 플레이 시간 업데이트 (활동 시간 업데이트)
     */
    public void updatePlaytime(long additionalTime) {
        if (playerData != null) {
            playerData = new PlayerIslandDataDTO(
                    playerData.playerUuid(),
                    playerData.currentIslandId(),
                    playerData.role(),
                    playerData.totalIslandResets(),
                    playerData.totalContribution(),
                    playerData.lastJoined(),
                    System.currentTimeMillis()
            );
        }
    }
    
    // Getters
    public String getUuid() { return playerUuid; }
    public String getName() { return playerName; }
    public PlayerIslandDataDTO getData() { return playerData; }
    
    // Setters
    public void setData(@NotNull PlayerIslandDataDTO data) { this.playerData = data; }
}