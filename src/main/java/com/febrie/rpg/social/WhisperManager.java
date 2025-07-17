package com.febrie.rpg.social;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.FirestoreRestService;
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
 * @author Febrie
 */
public class WhisperManager {
    
    private static WhisperManager instance;
    
    private final RPGMain plugin;
    private final FirestoreRestService firestoreService;
    private final Gson gson = new Gson();
    
    // 캐시
    private final Map<UUID, String> lastWhisperTarget = new ConcurrentHashMap<>(); // /r 명령어용
    private final Map<UUID, Set<WhisperMessageDTO>> recentMessages = new ConcurrentHashMap<>();
    
    // Firestore 컬렉션 이름
    private static final String WHISPERS_COLLECTION = "whispers";
    
    // 시간 포맷터
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public WhisperManager(@NotNull RPGMain plugin, @NotNull FirestoreRestService firestoreService) {
        this.plugin = plugin;
        this.firestoreService = firestoreService;
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
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 받는 사람이 온라인인지 확인
                Player toPlayer = Bukkit.getPlayer(toPlayerName);
                if (toPlayer == null) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c해당 플레이어를 찾을 수 없습니다: " + toPlayerName);
                    });
                    return false;
                }
                
                // 자기 자신에게 귓말 방지
                if (from.equals(toPlayer)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c자기 자신에게는 귓말을 보낼 수 없습니다.");
                    });
                    return false;
                }
                
                // 받는 사람의 귓말 설정 확인
                RPGPlayer toRPGPlayer = plugin.getRPGPlayerManager().getOrCreatePlayer(toPlayer);
                PlayerSettings toSettings = toRPGPlayer.getPlayerSettings();
                
                String whisperMode = toSettings.getWhisperMode();
                
                switch (whisperMode) {
                    case "BLOCKED" -> {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            from.sendMessage("§c" + toPlayerName + "님은 모든 귓말을 차단했습니다.");
                        });
                        return false;
                    }
                    case "FRIENDS_ONLY" -> {
                        // 친구인지 확인
                        FriendManager friendManager = FriendManager.getInstance();
                        Boolean areFriends = friendManager.areFriends(from.getUniqueId(), toPlayer.getUniqueId()).join();
                        if (!areFriends) {
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                from.sendMessage("§c" + toPlayerName + "님은 친구로부터만 귓말을 받습니다.");
                            });
                            return false;
                        }
                    }
                    // "ALL"인 경우 통과
                }
                
                // 귓말 메시지 생성
                WhisperMessageDTO whisperMessage = new WhisperMessageDTO(
                    from.getUniqueId(), from.getName(),
                    toPlayer.getUniqueId(), toPlayer.getName(),
                    message
                );
                
                // Firestore에 저장
                Map<String, Object> messageData = convertToMap(whisperMessage);
                String documentId = UUID.randomUUID().toString();
                
                boolean success = firestoreService.setDocument(WHISPERS_COLLECTION, documentId, messageData);
                
                if (success) {
                    whisperMessage.setId(documentId);
                    
                    // 캐시 업데이트
                    recentMessages.computeIfAbsent(from.getUniqueId(), k -> ConcurrentHashMap.newKeySet()).add(whisperMessage);
                    recentMessages.computeIfAbsent(toPlayer.getUniqueId(), k -> ConcurrentHashMap.newKeySet()).add(whisperMessage);
                    
                    // 마지막 귓말 대상 저장 (/r 명령어용)
                    lastWhisperTarget.put(from.getUniqueId(), toPlayer.getName());
                    lastWhisperTarget.put(toPlayer.getUniqueId(), from.getName());
                    
                    String timeStr = whisperMessage.getSentTime().format(TIME_FORMATTER);
                    
                    // 메시지 전송
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        // 보낸 사람에게
                        from.sendMessage(String.format("§d[%s] §7%s에게: §f%s", 
                            timeStr, toPlayer.getName(), message));
                        
                        // 받는 사람에게
                        toPlayer.sendMessage(String.format("§d[%s] §7%s님: §f%s", 
                            timeStr, from.getName(), message));
                        
                        // 알림 설정에 따라 받는 사람에게 추가 알림
                        if (toSettings.isWhisperNotificationsEnabled()) {
                            toPlayer.sendMessage("§e귓말을 받았습니다! '/귓말 " + from.getName() + " <메시지>'로 답장하세요.");
                        }
                    });
                    
                    return true;
                } else {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c귓말 전송에 실패했습니다.");
                    });
                    return false;
                }
                
            } catch (Exception e) {
                LogUtil.error("귓말 전송 중 오류 발생", e);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    from.sendMessage("§c귓말 전송 중 오류가 발생했습니다.");
                });
                return false;
            }
        });
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
        return CompletableFuture.supplyAsync(() -> {
            try {
                String query = String.format("(fromPlayerId==\"%s\" OR toPlayerId==\"%s\")", 
                    playerId.toString(), playerId.toString());
                
                Map<String, Object> response = firestoreService.queryDocuments(WHISPERS_COLLECTION, query);
                List<WhisperMessageDTO> messages = new ArrayList<>();
                
                if (response != null && response.containsKey("documents")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
                    
                    for (Map<String, Object> doc : documents) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> fields = (Map<String, Object>) doc.get("fields");
                        WhisperMessageDTO message = parseFirestoreDocument(fields, WhisperMessageDTO.class);
                        String documentId = extractDocumentId((String) doc.get("name"));
                        message.setId(documentId);
                        messages.add(message);
                    }
                }
                
                // 시간순으로 정렬 (최신 순)
                messages.sort((m1, m2) -> m2.getSentTime().compareTo(m1.getSentTime()));
                
                // 제한된 개수만 반환
                if (messages.size() > limit) {
                    messages = messages.subList(0, limit);
                }
                
                return messages;
                
            } catch (Exception e) {
                LogUtil.error("귓말 기록 조회 중 오류 발생", e);
                return new ArrayList<>();
            }
        });
    }
    
    /**
     * 특정 플레이어와의 귓말 기록 가져오기
     */
    @NotNull
    public CompletableFuture<List<WhisperMessageDTO>> getWhisperHistory(@NotNull UUID player1Id, @NotNull UUID player2Id, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String query = String.format(
                    "(fromPlayerId==\"%s\" AND toPlayerId==\"%s\") OR (fromPlayerId==\"%s\" AND toPlayerId==\"%s\")", 
                    player1Id.toString(), player2Id.toString(),
                    player2Id.toString(), player1Id.toString()
                );
                
                Map<String, Object> response = firestoreService.queryDocuments(WHISPERS_COLLECTION, query);
                List<WhisperMessageDTO> messages = new ArrayList<>();
                
                if (response != null && response.containsKey("documents")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
                    
                    for (Map<String, Object> doc : documents) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> fields = (Map<String, Object>) doc.get("fields");
                        WhisperMessageDTO message = parseFirestoreDocument(fields, WhisperMessageDTO.class);
                        String documentId = extractDocumentId((String) doc.get("name"));
                        message.setId(documentId);
                        messages.add(message);
                    }
                }
                
                // 시간순으로 정렬 (오래된 순)
                messages.sort((m1, m2) -> m1.getSentTime().compareTo(m2.getSentTime()));
                
                // 제한된 개수만 반환
                if (messages.size() > limit) {
                    messages = messages.subList(Math.max(0, messages.size() - limit), messages.size());
                }
                
                return messages;
                
            } catch (Exception e) {
                LogUtil.error("귓말 기록 조회 중 오류 발생", e);
                return new ArrayList<>();
            }
        });
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
    
    // Helper methods
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