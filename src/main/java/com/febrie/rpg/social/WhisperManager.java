package com.febrie.rpg.social;

import com.febrie.rpg.RPGMain;
// import com.febrie.rpg.database.FirestoreRestService; // REMOVED - FirestoreRestService not available
import com.febrie.rpg.dto.social.WhisperMessageDTO;
import com.febrie.rpg.player.PlayerSettings;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 귓말 시스템 관리자
 * 귓말 보내기, 받기, 기록 관리
 * 
 * NOTE: FirestoreRestService가 제거되어 모든 기능이 비활성화됨
 *
 * @author Febrie
 */
public class WhisperManager {
    
    private static WhisperManager instance;
    
    private final RPGMain plugin;
    // private final FirestoreRestService firestoreService; // REMOVED - FirestoreRestService not available
    private final Gson gson = new Gson();
    
    // 캐시
    private final Map<UUID, String> lastWhisperTarget = new ConcurrentHashMap<>(); // /r 명령어용
    private final Map<UUID, Set<WhisperMessageDTO>> recentMessages = new ConcurrentHashMap<>();
    
    // Firestore 컬렉션 이름
    private static final String WHISPERS_COLLECTION = "whispers";
    
    // 시간 포맷터
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public WhisperManager(@NotNull RPGMain plugin /*, @NotNull FirestoreRestService firestoreService*/) {
        this.plugin = plugin;
        // this.firestoreService = firestoreService; // REMOVED - FirestoreRestService not available
        instance = this;
    }
    
    public static WhisperManager getInstance() {
        return instance;
    }
    
    /**
     * 귓말 보내기
     */
    @NotNull
    public CompletableFuture<Boolean> sendWhisper(@NotNull Player from, @NotNull String toPlayerName, @NotNull String message) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            from.sendMessage("§c귓말 기능이 현재 비활성화되어 있습니다.");
        });
        return CompletableFuture.completedFuture(false);
    }
    
    /**
     * 마지막 귓말 대상에게 답장 보내기 (/r 명령어)
     */
    @NotNull
    public CompletableFuture<Boolean> replyToLastWhisper(@NotNull Player from, @NotNull String message) {
        String lastTarget = lastWhisperTarget.get(from.getUniqueId());
        
        if (lastTarget == null) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                from.sendMessage("§c답장할 대상이 없습니다. 먼저 귓말을 주고받아야 합니다.");
            });
            return CompletableFuture.completedFuture(false);
        }
        
        return sendWhisper(from, lastTarget, message);
    }
    
    /**
     * 플레이어의 최근 귓말 기록 가져오기
     */
    @NotNull
    public CompletableFuture<List<WhisperMessageDTO>> getRecentWhispers(@NotNull UUID playerId, int limit) {
        return CompletableFuture.completedFuture(new ArrayList<>());
    }
    
    /**
     * 특정 플레이어와의 귓말 기록 가져오기
     */
    @NotNull
    public CompletableFuture<List<WhisperMessageDTO>> getWhisperHistory(@NotNull UUID player1Id, @NotNull UUID player2Id, int limit) {
        return CompletableFuture.completedFuture(new ArrayList<>());
    }
    
    /**
     * 마지막 귓말 대상 가져오기
     */
    public String getLastWhisperTarget(@NotNull UUID playerId) {
        return lastWhisperTarget.get(playerId);
    }
    
    /**
     * 캐시 정리
     */
    public void clearCache(@NotNull UUID playerId) {
        recentMessages.remove(playerId);
        lastWhisperTarget.remove(playerId);
    }
    
    /**
     * 모든 캐시 정리
     */
    public void clearAllCache() {
        recentMessages.clear();
        lastWhisperTarget.clear();
    }
    
    // Helper methods - DISABLED
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object obj) {
        String json = gson.toJson(obj);
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return gson.fromJson(json, type);
    }
    
    private <T> T convertFromMap(Map<String, Object> map, Class<T> clazz) {
        String json = gson.toJson(map);
        return gson.fromJson(json, clazz);
    }
    
    private <T> T parseFirestoreDocument(Map<String, Object> fields, Class<T> clazz) {
        Map<String, Object> converted = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> fieldValue = (Map<String, Object>) entry.getValue();
            Object value = fieldValue.values().iterator().next();
            converted.put(entry.getKey(), value);
        }
        
        return convertFromMap(converted, clazz);
    }
    
    private String extractDocumentId(String documentPath) {
        String[] parts = documentPath.split("/");
        return parts[parts.length - 1];
    }
}