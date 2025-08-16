package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.BaseFirestoreService;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 섬 데이터 Firestore 서비스
 * islands 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class IslandFirestoreService extends BaseFirestoreService<IslandDTO> {
    
    private static final String COLLECTION_NAME = "Island";
    
    public IslandFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        super(plugin, firestore, COLLECTION_NAME, IslandDTO.class);
    }
    
    @Override
    protected Map<String, Object> toMap(@NotNull IslandDTO dto) {
        return dto.toMap();
    }
    
    @Override
    @Nullable
    protected IslandDTO fromDocument(@NotNull DocumentSnapshot document) {
        if (!document.exists()) {
            return null;
        }
        
        try {
            Map<String, Object> data = document.getData();
            if (data != null) {
                return IslandDTO.fromMap(data);
            }
            return null;
        } catch (Exception e) {
            LogUtil.warning("섬 데이터 파싱 실패 [" + document.getId() + "]: " + e.getMessage());
            return null;
        }
    }
    
    // ===== 추가 기능 메소드들 =====
    
    /**
     * 플레이어가 소유한 섬 찾기
     */
    @NotNull
    public CompletableFuture<IslandDTO> findByOwner(@NotNull String ownerUuid) {
        return query("ownerUuid", ownerUuid).thenApply(islands -> 
                islands.isEmpty() ? null : islands.get(0));
    }
    
    /**
     * 공개 섬 목록 조회
     */
    @NotNull
    public CompletableFuture<List<IslandDTO>> getPublicIslands() {
        return query("isPublic", true);
    }
    
    /**
     * 섬 이름으로 검색 (부분 일치)
     * 참고: Firestore는 부분 문자열 검색을 네이티브로 지원하지 않으므로
     * 전체 목록을 가져온 후 필터링
     */
    @NotNull
    public CompletableFuture<List<IslandDTO>> searchByName(@NotNull String namePart) {
        String lowerSearch = namePart.toLowerCase();
        
        return getPublicIslands().thenApply(islands -> 
                islands.stream()
                        .filter(island -> island.islandName().toLowerCase().contains(lowerSearch))
                        .collect(Collectors.toList()));
    }
    
    /**
     * 모든 섬 데이터 로드 (사전 로드용)
     * 서버 시작 시 모든 섬 데이터를 캐시에 로드하기 위해 사용
     */
    @NotNull
    public CompletableFuture<List<IslandDTO>> getAllIslands() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<IslandDTO> islands = new ArrayList<>();
                
                // 모든 섬 데이터를 가져옴
                var future = firestore.collection(COLLECTION_NAME)
                        .orderBy("lastActivity", com.google.cloud.firestore.Query.Direction.DESCENDING)
                        .get();
                
                var querySnapshot = future.get(30, java.util.concurrent.TimeUnit.SECONDS);
                
                for (var doc : querySnapshot.getDocuments()) {
                    IslandDTO island = fromDocument(doc);
                    if (island != null) {
                        islands.add(island);
                    }
                }
                
                LogUtil.debug("Firestore에서 " + islands.size() + "개 섬 데이터 로드");
                return islands;
                
            } catch (Exception e) {
                LogUtil.error("모든 섬 데이터 로드 실패", e);
                return new ArrayList<>();
            }
        });
    }
}