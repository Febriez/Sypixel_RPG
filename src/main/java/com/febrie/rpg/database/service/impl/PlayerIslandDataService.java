package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.GenericFirestoreService;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.Firestore;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 플레이어 섬 데이터 Firestore 서비스
 * 플레이어별 섬 관련 데이터를 관리
 *
 * @author Febrie, CoffeeTory
 */
public class PlayerIslandDataService {
    
    private static final String COLLECTION_NAME = "PlayerIslandData";
    private final GenericFirestoreService<PlayerIslandDataDTO> service;
    private final Firestore firestore;
    
    public PlayerIslandDataService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        this.firestore = firestore;
        this.service = GenericFirestoreService.create(
            plugin,
            firestore,
            COLLECTION_NAME,
            PlayerIslandDataDTO.class,
            PlayerIslandDataDTO::toMap,
            PlayerIslandDataDTO::fromMap,
            id -> null // 플레이어 섬 데이터는 기본값이 없음
        );
    }
    
    /**
     * 문서 ID로 데이터 조회
     */
    @NotNull
    public CompletableFuture<PlayerIslandDataDTO> get(@NotNull String id) {
        return service.get(id);
    }
    
    /**
     * 데이터 저장
     */
    @NotNull
    public CompletableFuture<Void> save(@NotNull String id, @NotNull PlayerIslandDataDTO data) {
        return service.save(id, data);
    }
    
    /**
     * 데이터 삭제
     */
    @NotNull
    public CompletableFuture<Void> delete(@NotNull String id) {
        return service.delete(id);
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
                var future = firestore.collection(COLLECTION_NAME).get();
                
                var querySnapshot = future.get(30, java.util.concurrent.TimeUnit.SECONDS);
                
                for (var doc : querySnapshot.getDocuments()) {
                    // currentIslandId가 null이 아닌 경우만 추가
                    if (doc.get("currentIslandId") != null) {
                        PlayerIslandDataDTO player = PlayerIslandDataDTO.fromMap(doc.getData());
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