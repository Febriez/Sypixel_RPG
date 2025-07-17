package com.febrie.rpg.social;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.FirestoreRestService;
import com.febrie.rpg.dto.social.MailDTO;
import com.febrie.rpg.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 우편함 시스템 관리자
 * 우편 보내기, 받기, 첨부물 관리
 *
 * @author Febrie
 */
public class MailManager {
    
    private static MailManager instance;
    
    private final RPGMain plugin;
    private final FirestoreRestService firestoreService;
    private final Gson gson = new Gson();
    
    // 캐시
    private final Map<UUID, Set<MailDTO>> mailCache = new ConcurrentHashMap<>();
    
    // Firestore 컬렉션 이름
    private static final String MAIL_COLLECTION = "mail";
    
    // 제한
    private static final int MAX_MAIL_PER_PLAYER = 50; // 플레이어당 최대 우편 개수
    private static final int MAX_ATTACHMENTS_PER_MAIL = 9; // 우편당 최대 첨부물 개수
    
    public MailManager(@NotNull RPGMain plugin, @NotNull FirestoreRestService firestoreService) {
        this.plugin = plugin;
        this.firestoreService = firestoreService;
        instance = this;
    }
    
    public static MailManager getInstance() {
        return instance;
    }
    
    /**
     * 우편 보내기 (텍스트만)
     */
    @NotNull
    public CompletableFuture<Boolean> sendMail(@NotNull Player from, @NotNull String toPlayerName, 
                                             @NotNull String subject, @Nullable String message) {
        return sendMailWithAttachments(from, toPlayerName, subject, message, new ArrayList<>());
    }
    
    /**
     * 우편 보내기 (첨부물 포함)
     */
    @NotNull
    public CompletableFuture<Boolean> sendMailWithAttachments(@NotNull Player from, @NotNull String toPlayerName, 
                                                             @NotNull String subject, @Nullable String message, 
                                                             @NotNull List<ItemStack> attachments) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 받는 사람 확인 (오프라인도 가능)
                Player toPlayer = Bukkit.getPlayer(toPlayerName);
                UUID toPlayerId;
                
                if (toPlayer != null) {
                    toPlayerId = toPlayer.getUniqueId();
                } else {
                    // 오프라인 플레이어는 나중에 UUID 조회 구현 필요
                    // 현재는 온라인 플레이어만 지원
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c해당 플레이어가 온라인이 아니거나 존재하지 않습니다: " + toPlayerName);
                    });
                    return false;
                }
                
                // 자기 자신에게 우편 방지
                if (from.equals(toPlayer)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c자기 자신에게는 우편을 보낼 수 없습니다.");
                    });
                    return false;
                }
                
                // 받는 사람의 우편함이 가득 찼는지 확인
                int currentMailCount = getMailCount(toPlayerId).join();
                if (currentMailCount >= MAX_MAIL_PER_PLAYER) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c" + toPlayerName + "님의 우편함이 가득 찼습니다.");
                    });
                    return false;
                }
                
                // 첨부물 개수 확인
                if (attachments.size() > MAX_ATTACHMENTS_PER_MAIL) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c첨부물은 최대 " + MAX_ATTACHMENTS_PER_MAIL + "개까지만 가능합니다.");
                    });
                    return false;
                }
                
                // 제목과 메시지 길이 확인
                if (subject.length() > 50) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c제목은 50자 이내로 입력해주세요.");
                    });
                    return false;
                }
                
                if (message != null && message.length() > 500) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c메시지는 500자 이내로 입력해주세요.");
                    });
                    return false;
                }
                
                // 우편 생성
                MailDTO mail = new MailDTO(
                    from.getUniqueId(), from.getName(),
                    toPlayerId, toPlayerName,
                    subject, message
                );
                
                // 첨부물 추가
                for (ItemStack attachment : attachments) {
                    if (attachment != null && !attachment.getType().isAir()) {
                        mail.addAttachment(attachment);
                    }
                }
                
                // Firestore에 저장
                Map<String, Object> mailData = convertToMap(mail);
                String documentId = UUID.randomUUID().toString();
                
                boolean success = firestoreService.setDocument(MAIL_COLLECTION, documentId, mailData);
                
                if (success) {
                    mail.setId(documentId);
                    
                    // 캐시 업데이트
                    mailCache.computeIfAbsent(toPlayerId, k -> ConcurrentHashMap.newKeySet()).add(mail);
                    
                    // 보낸 사람에게서 첨부물 제거
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        for (ItemStack attachment : attachments) {
                            if (attachment != null && !attachment.getType().isAir()) {
                                from.getInventory().removeItem(attachment);
                            }
                        }
                        
                        from.sendMessage("§a우편을 보냈습니다: " + toPlayerName);
                        from.sendMessage("§7제목: " + subject);
                        if (mail.hasAttachments()) {
                            from.sendMessage("§7첨부물: " + attachments.size() + "개");
                        }
                        
                        // 받는 사람에게 알림 (온라인인 경우)
                        if (toPlayer != null) {
                            toPlayer.sendMessage("§e" + from.getName() + "님으로부터 우편이 도착했습니다!");
                            toPlayer.sendMessage("§e제목: " + subject);
                            toPlayer.sendMessage("§e'/우편함'에서 확인하세요.");
                        }
                    });
                    
                    return true;
                } else {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c우편 전송에 실패했습니다.");
                    });
                    return false;
                }
                
            } catch (Exception e) {
                LogUtil.error("우편 전송 중 오류 발생", e);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    from.sendMessage("§c우편 전송 중 오류가 발생했습니다.");
                });
                return false;
            }
        });
    }
    
    /**
     * 플레이어의 우편 목록 가져오기
     */
    @NotNull
    public CompletableFuture<List<MailDTO>> getMails(@NotNull UUID playerId, boolean includeRead) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String query = String.format("toPlayerId==\"%s\"", playerId.toString());
                if (!includeRead) {
                    query += " AND isRead==false";
                }
                
                Map<String, Object> response = firestoreService.queryDocuments(MAIL_COLLECTION, query);
                List<MailDTO> mails = new ArrayList<>();
                
                if (response != null && response.containsKey("documents")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
                    
                    for (Map<String, Object> doc : documents) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> fields = (Map<String, Object>) doc.get("fields");
                        MailDTO mail = parseFirestoreDocument(fields, MailDTO.class);
                        String documentId = extractDocumentId((String) doc.get("name"));
                        mail.setId(documentId);
                        mails.add(mail);
                    }
                }
                
                // 시간순으로 정렬 (최신 순)
                mails.sort((m1, m2) -> m2.getSentTime().compareTo(m1.getSentTime()));
                
                return mails;
                
            } catch (Exception e) {
                LogUtil.error("우편 목록 조회 중 오류 발생", e);
                return new ArrayList<>();
            }
        });
    }
    
    /**
     * 우편 읽기 (읽음 상태로 변경)
     */
    @NotNull
    public CompletableFuture<Boolean> markAsRead(@NotNull String mailId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> mailData = firestoreService.getDocument(MAIL_COLLECTION, mailId);
                if (mailData == null) {
                    return false;
                }
                
                MailDTO mail = convertFromMap(mailData, MailDTO.class);
                mail.setRead(true);
                
                Map<String, Object> updatedData = convertToMap(mail);
                return firestoreService.setDocument(MAIL_COLLECTION, mailId, updatedData);
                
            } catch (Exception e) {
                LogUtil.error("우편 읽음 처리 중 오류 발생", e);
                return false;
            }
        });
    }
    
    /**
     * 우편 첨부물 수령
     */
    @NotNull
    public CompletableFuture<Boolean> collectAttachments(@NotNull Player player, @NotNull String mailId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> mailData = firestoreService.getDocument(MAIL_COLLECTION, mailId);
                if (mailData == null) {
                    return false;
                }
                
                MailDTO mail = convertFromMap(mailData, MailDTO.class);
                
                // 받는 사람이 맞는지 확인
                if (!mail.getToPlayerId().equals(player.getUniqueId())) {
                    return false;
                }
                
                // 이미 수령했는지 확인
                if (mail.isCollected()) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§c이미 첨부물을 수령했습니다.");
                    });
                    return false;
                }
                
                // 첨부물이 있는지 확인
                if (!mail.hasAttachments()) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§c이 우편에는 첨부물이 없습니다.");
                    });
                    return false;
                }
                
                // 인벤토리 공간 확인
                List<ItemStack> attachments = mail.getAttachmentsAsItemStacks();
                final int requiredSlots = attachments.size();
                int emptySlots = 0;
                
                for (ItemStack slot : player.getInventory().getContents()) {
                    if (slot == null || slot.getType().isAir()) {
                        emptySlots++;
                    }
                }
                
                final int finalEmptySlots = emptySlots;
                if (emptySlots < requiredSlots) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§c인벤토리 공간이 부족합니다. (" + requiredSlots + "칸 필요, " + finalEmptySlots + "칸 여유)");
                    });
                    return false;
                }
                
                // 첨부물을 플레이어에게 지급
                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (ItemStack attachment : attachments) {
                        player.getInventory().addItem(attachment);
                    }
                    
                    player.sendMessage("§a첨부물을 수령했습니다! (" + attachments.size() + "개)");
                });
                
                // 수령 상태로 업데이트
                mail.setCollected(true);
                Map<String, Object> updatedData = convertToMap(mail);
                return firestoreService.setDocument(MAIL_COLLECTION, mailId, updatedData);
                
            } catch (Exception e) {
                LogUtil.error("첨부물 수령 중 오류 발생", e);
                return false;
            }
        });
    }
    
    /**
     * 우편 삭제
     */
    @NotNull
    public CompletableFuture<Boolean> deleteMail(@NotNull String mailId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return firestoreService.deleteDocument(MAIL_COLLECTION, mailId);
            } catch (Exception e) {
                LogUtil.error("우편 삭제 중 오류 발생", e);
                return false;
            }
        });
    }
    
    /**
     * 플레이어의 우편 개수 가져오기
     */
    @NotNull
    public CompletableFuture<Integer> getMailCount(@NotNull UUID playerId) {
        return getMails(playerId, true).thenApply(List::size);
    }
    
    /**
     * 읽지 않은 우편 개수 가져오기
     */
    @NotNull
    public CompletableFuture<Integer> getUnreadMailCount(@NotNull UUID playerId) {
        return getMails(playerId, false).thenApply(List::size);
    }
    
    /**
     * 캐시 정리
     */
    public void clearCache(@NotNull UUID playerId) {
        mailCache.remove(playerId);
    }
    
    /**
     * 모든 캐시 정리
     */
    public void clearAllCache() {
        mailCache.clear();
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