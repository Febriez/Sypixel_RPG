package com.febrie.rpg.social;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.social.MailDTO;
import com.febrie.rpg.util.FirestoreUtil;
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
 * NOTE: 현재 모든 기능이 비활성화됨 - Firestore 서비스 재구현 필요
 *
 * @author Febrie
 */
public class MailManager {
    
    private static MailManager instance;
    
    private final RPGMain plugin;
    private final Gson gson = new Gson();
    
    // 캐시
    private final Map<UUID, Set<MailDTO>> mailCache = new ConcurrentHashMap<>();
    
    // Firestore 컬렉션 이름
    private static final String MAIL_COLLECTION = "mail";
    
    // 제한
    private static final int MAX_MAIL_PER_PLAYER = 50; // 플레이어당 최대 우편 개수
    private static final int MAX_ATTACHMENTS_PER_MAIL = 9; // 우편당 최대 첨부물 개수
    
    public MailManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
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
        Bukkit.getScheduler().runTask(plugin, () -> {
            from.sendMessage("§c우편 기능이 현재 비활성화되어 있습니다.");
        });
        return CompletableFuture.completedFuture(false);
    }
    
    /**
     * 플레이어의 우편 목록 가져오기
     */
    @NotNull
    public CompletableFuture<List<MailDTO>> getMails(@NotNull UUID playerId, boolean includeRead) {
        return CompletableFuture.completedFuture(new ArrayList<>());
    }
    
    /**
     * 우편 읽기 (읽음 상태로 변경)
     */
    @NotNull
    public CompletableFuture<Boolean> markAsRead(@NotNull String mailId) {
        return CompletableFuture.completedFuture(false);
    }
    
    /**
     * 우편 첨부물 수령
     */
    @NotNull
    public CompletableFuture<Boolean> collectAttachments(@NotNull Player player, @NotNull String mailId) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.sendMessage("§c우편 첨부물 수령 기능이 현재 비활성화되어 있습니다.");
        });
        return CompletableFuture.completedFuture(false);
    }
    
    /**
     * 우편 삭제
     */
    @NotNull
    public CompletableFuture<Boolean> deleteMail(@NotNull String mailId) {
        return CompletableFuture.completedFuture(false);
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
}