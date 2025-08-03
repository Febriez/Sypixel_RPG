package com.febrie.rpg.social;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.FirestoreManager;
import com.febrie.rpg.database.service.impl.MailFirestoreService;
import com.febrie.rpg.dto.social.MailDTO;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.Firestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 우편함 시스템 관리자
 * 우편 보내기, 받기, 첨부물 관리
 *
 * @author Febrie
 */
public class MailManager {

    private static MailManager instance;

    private final RPGMain plugin;
    private final MailFirestoreService mailService;
    private final Gson gson = new Gson();

    // 캐시
    private final Map<UUID, List<MailDTO>> mailCache = new ConcurrentHashMap<>();
    
    // 캐시 유효 시간 (3분)
    private static final long CACHE_DURATION_MS = 180_000;
    private final Map<UUID, Long> cacheTimestamps = new ConcurrentHashMap<>();

    // 제한
    private static final int MAX_MAIL_PER_PLAYER = 50; // 플레이어당 최대 우편 개수
    private static final int MAX_ATTACHMENTS_PER_MAIL = 9; // 우편당 최대 첨부물 개수
    private static final long MAIL_EXPIRY_DAYS = 30; // 우편 만료 기간 (30일)

    public MailManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        Firestore firestore = plugin.getFirestoreManager().getFirestore();
        this.mailService = new MailFirestoreService(plugin, firestore);
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
     * 우편 보내기 (첨부물 포함) - 현재 첨부물 미지원
     */
    @NotNull
    public CompletableFuture<Boolean> sendMailWithAttachments(@NotNull Player from, @NotNull String toPlayerName,
                                                              @NotNull String subject, @Nullable String message,
                                                              @NotNull List<ItemStack> attachments) {
        // 대상 플레이어 조회
        OfflinePlayer toPlayer = Bukkit.getOfflinePlayer(toPlayerName);
        if (!toPlayer.hasPlayedBefore() && !toPlayer.isOnline()) {
            from.sendMessage("§c해당 플레이어를 찾을 수 없습니다: " + toPlayerName);
            return CompletableFuture.completedFuture(false);
        }

        UUID fromId = from.getUniqueId();
        UUID toId = toPlayer.getUniqueId();

        // 자기 자신에게 우편 보내기 방지
        if (fromId.equals(toId)) {
            from.sendMessage("§c자기 자신에게는 우편을 보낼 수 없습니다.");
            return CompletableFuture.completedFuture(false);
        }

        // 첨부물 기능은 현재 미지원
        if (!attachments.isEmpty()) {
            from.sendMessage("§c현재 첨부물 기능은 지원하지 않습니다.");
            return CompletableFuture.completedFuture(false);
        }

        // 받는 사람의 우편함이 가득 찼는지 확인
        return getMailCount(toId).thenCompose(mailCount -> {
            if (mailCount >= MAX_MAIL_PER_PLAYER) {
                from.sendMessage("§c" + toPlayerName + "님의 우편함이 가득 찼습니다.");
                return CompletableFuture.completedFuture(false);
            }

            // 우편 생성
            MailDTO mail = new MailDTO(
                UUID.randomUUID().toString(),
                fromId,
                from.getName(),
                toId,
                toPlayerName,
                subject,
                message != null ? message : ""
            );

            // 우편 전송
            return mailService.sendMail(mail).thenApply(v -> {
                // 캐시 무효화
                clearCache(toId);

                // 알림
                from.sendMessage("§a우편을 성공적으로 전송했습니다!");
                
                Player onlineReceiver = Bukkit.getPlayer(toId);
                if (onlineReceiver != null) {
                    onlineReceiver.sendMessage("§e새로운 우편이 도착했습니다! §7(/mail)");
                }

                LogUtil.info("우편 전송: " + from.getName() + " → " + toPlayerName + " [" + subject + "]");
                return true;
            }).exceptionally(ex -> {
                LogUtil.warning("우편 전송 실패: " + ex.getMessage());
                from.sendMessage("§c우편 전송 중 오류가 발생했습니다.");
                return false;
            });
        });
    }

    /**
     * 플레이어의 우편 목록 가져오기
     */
    @NotNull
    public CompletableFuture<List<MailDTO>> getMails(@NotNull UUID playerId, boolean includeRead) {
        // 캐시 확인
        Long lastCached = cacheTimestamps.get(playerId);
        if (lastCached != null && System.currentTimeMillis() - lastCached < CACHE_DURATION_MS) {
            List<MailDTO> cached = mailCache.get(playerId);
            if (cached != null) {
                List<MailDTO> filtered = includeRead ? 
                    new ArrayList<>(cached) : 
                    cached.stream().filter(MailDTO::isUnread).collect(Collectors.toList());
                return CompletableFuture.completedFuture(filtered);
            }
        }

        // DB에서 조회
        return mailService.getReceivedMails(playerId)
            .thenApply(mails -> {
                // 캐시 업데이트
                mailCache.put(playerId, mails);
                cacheTimestamps.put(playerId, System.currentTimeMillis());

                // 필터링
                return includeRead ? 
                    mails : 
                    mails.stream().filter(MailDTO::isUnread).collect(Collectors.toList());
            })
            .exceptionally(ex -> {
                LogUtil.warning("우편 목록 조회 실패 [" + playerId + "]: " + ex.getMessage());
                return new ArrayList<>();
            });
    }

    /**
     * 우편 읽기 (읽음 상태로 변경)
     */
    @NotNull
    public CompletableFuture<Boolean> markAsRead(@NotNull String mailId) {
        return mailService.markMailAsRead(mailId)
            .thenApply(v -> {
                // 캐시에서도 업데이트
                mailCache.values().forEach(mails -> 
                    mails.stream()
                        .filter(mail -> mail.mailId().equals(mailId))
                        .findFirst()
                        .ifPresent(mail -> {
                            int index = mails.indexOf(mail);
                            if (index >= 0) {
                                mails.set(index, mail.markAsRead());
                            }
                        })
                );
                return true;
            })
            .exceptionally(ex -> {
                LogUtil.warning("우편 읽음 처리 실패 [" + mailId + "]: " + ex.getMessage());
                return false;
            });
    }

    /**
     * 우편 첨부물 수령
     */
    @NotNull
    public CompletableFuture<Boolean> collectAttachments(@NotNull Player player, @NotNull String mailId) {
        return mailService.getMail(mailId).thenCompose(mail -> {
            if (mail == null) {
                player.sendMessage("§c해당 우편을 찾을 수 없습니다.");
                return CompletableFuture.completedFuture(false);
            }

            // 권한 확인
            if (!mail.receiverUuid().equals(player.getUniqueId())) {
                player.sendMessage("§c이 우편의 수신자가 아닙니다.");
                return CompletableFuture.completedFuture(false);
            }

            // 현재 첨부물 기능 미지원
            player.sendMessage("§c현재 첨부물 기능은 지원하지 않습니다.");
            return CompletableFuture.completedFuture(false);
        });
    }

    /**
     * 우편 삭제
     */
    @NotNull
    public CompletableFuture<Boolean> deleteMail(@NotNull String mailId) {
        return mailService.deleteMail(mailId)
            .thenApply(v -> {
                // 캐시에서도 제거
                mailCache.values().forEach(mails -> 
                    mails.removeIf(mail -> mail.mailId().equals(mailId))
                );
                return true;
            })
            .exceptionally(ex -> {
                LogUtil.warning("우편 삭제 실패 [" + mailId + "]: " + ex.getMessage());
                return false;
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
        return mailService.getUnreadMailCount(playerId);
    }

    /**
     * 오래된 우편 정리 (30일 이상)
     */
    public CompletableFuture<Void> cleanupOldMails() {
        return mailService.deleteOldMails((int) MAIL_EXPIRY_DAYS)
            .thenRun(() -> {
                clearAllCache();
                LogUtil.info("오래된 우편 정리 완료");
            });
    }

    /**
     * 캐시 정리
     */
    public void clearCache(@NotNull UUID playerId) {
        mailCache.remove(playerId);
        cacheTimestamps.remove(playerId);
    }

    /**
     * 모든 캐시 정리
     */
    public void clearAllCache() {
        mailCache.clear();
        cacheTimestamps.clear();
    }

    /**
     * 서비스 종료
     */
    public void shutdown() {
        clearAllCache();
        mailService.shutdown();
    }
}