package com.febrie.rpg.database.sync;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.helper.FirestoreHelper;
import com.febrie.rpg.database.helper.FirestoreHelper.DataPriority;
import com.febrie.rpg.dto.player.PlayerDataDTO;
import com.febrie.rpg.dto.player.WalletDTO;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 데이터 동기화 관리자
 * 메모리와 Firestore 간의 데이터 동기화를 담당
 * 
 * @author Febrie, CoffeeTory
 */
public class DataSyncManager {
    
    private final RPGMain plugin;
    private final Firestore firestore;
    private final FirestoreHelper firestoreHelper;
    private final PlayerDataCache cache;
    
    // 플레이어별 마지막 저장 시간 추적
    private final Map<UUID, Long> lastSaveTimes = new ConcurrentHashMap<>();
    
    // 데이터 타입별 저장 간격 (밀리초)
    private static final long CURRENCY_SAVE_INTERVAL = 60_000;     // 1분
    private static final long PROFILE_SAVE_INTERVAL = 180_000;     // 3분
    private static final long STATS_SAVE_INTERVAL = 300_000;       // 5분
    
    public DataSyncManager(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        this.plugin = plugin;
        this.firestore = firestore;
        this.firestoreHelper = new FirestoreHelper(firestore);
        this.firestoreHelper.initialize(); // 초기화
        this.cache = new PlayerDataCache(5, TimeUnit.MINUTES); // 5분 TTL
    }
    
    /**
     * 재화 변경 - 즉시 저장 (트랜잭션)
     */
    @NotNull
    public CompletableFuture<Boolean> modifyCurrency(@NotNull RPGPlayer player, 
                                                     @NotNull CurrencyType currency, 
                                                     long amount) {
        UUID playerId = player.getPlayerId();
        String currencyId = currency.getId();
        
        // 트랜잭션으로 원자적 업데이트
        return firestoreHelper.runTransaction(transaction -> {
            try {
                // 현재 재화 읽기
                var docRef = firestore.collection("Player").document(playerId.toString());
                var snapshot = transaction.get(docRef).get();
            
            PlayerDataDTO currentData = null;
            if (snapshot.exists()) {
                currentData = PlayerDataDTO.fromMap(snapshot.getData());
            }
            
            if (currentData == null) {
                currentData = PlayerDataDTO.createNew(playerId, player.getPlayer().getName());
            }
            
            // 재화 계산
            Map<String, Long> currencies = new HashMap<>(currentData.wallet().currencies());
            long currentAmount = currencies.getOrDefault(currencyId, 0L);
            long newAmount = currentAmount + amount;
            
            if (newAmount < 0) {
                return false; // 잔액 부족
            }
            
            // 최대값 체크
            if (newAmount > currency.getMaxAmount()) {
                newAmount = currency.getMaxAmount();
            }
            
            // 업데이트
            currencies.put(currencyId, newAmount);
            WalletDTO newWallet = new WalletDTO(currencies, System.currentTimeMillis());
            PlayerDataDTO updatedData = new PlayerDataDTO(currentData.profile(), newWallet);
            
            // Firestore에 저장
            transaction.set(docRef, updatedData.toMap());
            
            // 메모리 업데이트
            player.getWallet().setBalance(currency, newAmount);
            
            // 캐시 업데이트
            cache.put(playerId, updatedData);
            
            return true;
            } catch (Exception e) {
                throw new RuntimeException("재화 변경 실패", e);
            }
        }).exceptionally(ex -> {
            LogUtil.severe("재화 트랜잭션 실패 [" + playerId + "]: " + ex.getMessage());
            return false;
        });
    }
    
    /**
     * 닉네임 변경 - 즉시 저장
     */
    @NotNull
    public CompletableFuture<Void> updateNickname(@NotNull RPGPlayer player, @NotNull String newNickname) {
        UUID playerId = player.getPlayerId();
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("profile.name", newNickname);
        updates.put("profile.lastUpdate", System.currentTimeMillis());
        
        return firestoreHelper.updateFields("Player", playerId.toString(), updates)
            .thenRun(() -> {
                // 캐시 무효화
                cache.invalidate(playerId);
                LogUtil.debug("닉네임 업데이트 완료: " + playerId + " -> " + newNickname);
            });
    }
    
    /**
     * 플레이어 데이터 저장 (배치)
     */
    public void savePlayerData(@NotNull RPGPlayer player, @NotNull DataPriority priority) {
        UUID playerId = player.getPlayerId();
        
        // 저장 간격 체크
        if (!shouldSave(playerId, priority)) {
            return;
        }
        
        // DTO 변환
        PlayerDataDTO dto = player.toDTO();
        Map<String, Object> data = dto.toMap();
        
        // 배치 큐에 추가
        firestoreHelper.addToBatch("Player", playerId.toString(), data, priority);
        
        // 캐시 업데이트
        cache.put(playerId, dto);
        
        // 저장 시간 기록
        lastSaveTimes.put(playerId, System.currentTimeMillis());
    }
    
    /**
     * 플레이어 데이터 로드
     */
    @NotNull
    public CompletableFuture<PlayerDataDTO> loadPlayerData(@NotNull UUID playerId) {
        // 캐시 확인
        PlayerDataDTO cached = cache.get(playerId);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        
        // Firestore에서 로드
        return CompletableFuture.supplyAsync(() -> {
            try {
                var future = firestore.collection("Player")
                    .document(playerId.toString())
                    .get();
                    
                var snapshot = future.get(5, TimeUnit.SECONDS);
                
                if (snapshot.exists()) {
                    PlayerDataDTO data = PlayerDataDTO.fromMap(snapshot.getData());
                    cache.put(playerId, data);
                    return data;
                }
                
                return null;
            } catch (Exception e) {
                LogUtil.severe("플레이어 데이터 로드 실패 [" + playerId + "]: " + e.getMessage());
                return null;
            }
        });
    }
    
    /**
     * 플레이어 간 거래 (트랜잭션)
     */
    @NotNull
    public CompletableFuture<Boolean> transferCurrency(@NotNull RPGPlayer from, 
                                                       @NotNull RPGPlayer to,
                                                       @NotNull CurrencyType currency, 
                                                       long amount) {
        if (amount <= 0) {
            return CompletableFuture.completedFuture(false);
        }
        
        return firestoreHelper.runTransaction(transaction -> {
            try {
                String fromId = from.getPlayerId().toString();
                String toId = to.getPlayerId().toString();
                String currencyId = currency.getId();
                
                // 두 플레이어 데이터 읽기
                var fromRef = firestore.collection("Player").document(fromId);
                var toRef = firestore.collection("Player").document(toId);
                
                var fromSnapshot = transaction.get(fromRef).get();
                var toSnapshot = transaction.get(toRef).get();
            
            PlayerDataDTO fromData = fromSnapshot.exists() ? 
                PlayerDataDTO.fromMap(fromSnapshot.getData()) : null;
            PlayerDataDTO toData = toSnapshot.exists() ? 
                PlayerDataDTO.fromMap(toSnapshot.getData()) : null;
                
            if (fromData == null || toData == null) {
                return false;
            }
            
            // 재화 계산
            Map<String, Long> fromCurrencies = new HashMap<>(fromData.wallet().currencies());
            Map<String, Long> toCurrencies = new HashMap<>(toData.wallet().currencies());
            
            long fromAmount = fromCurrencies.getOrDefault(currencyId, 0L);
            if (fromAmount < amount) {
                return false; // 잔액 부족
            }
            
            long toAmount = toCurrencies.getOrDefault(currencyId, 0L);
            long newToAmount = Math.min(toAmount + amount, currency.getMaxAmount());
            long actualTransferred = newToAmount - toAmount;
            
            // 업데이트
            fromCurrencies.put(currencyId, fromAmount - actualTransferred);
            toCurrencies.put(currencyId, newToAmount);
            
            WalletDTO fromWallet = new WalletDTO(fromCurrencies, System.currentTimeMillis());
            WalletDTO toWallet = new WalletDTO(toCurrencies, System.currentTimeMillis());
            
            PlayerDataDTO updatedFromData = new PlayerDataDTO(fromData.profile(), fromWallet);
            PlayerDataDTO updatedToData = new PlayerDataDTO(toData.profile(), toWallet);
            
            // Firestore에 저장
            transaction.set(fromRef, updatedFromData.toMap());
            transaction.set(toRef, updatedToData.toMap());
            
            // 메모리 업데이트
            from.getWallet().setBalance(currency, fromAmount - actualTransferred);
            to.getWallet().setBalance(currency, newToAmount);
            
            // 캐시 업데이트
            cache.put(from.getPlayerId(), updatedFromData);
            cache.put(to.getPlayerId(), updatedToData);
            
            return true;
            } catch (Exception e) {
                throw new RuntimeException("거래 실패", e);
            }
        });
    }
    
    /**
     * 플레이어 로그아웃 시 강제 저장
     */
    @NotNull
    public CompletableFuture<Void> saveOnLogout(@NotNull RPGPlayer player) {
        UUID playerId = player.getPlayerId();
        
        // 즉시 저장
        PlayerDataDTO dto = player.toDTO();
        Map<String, Object> data = dto.toMap();
        
        return firestoreHelper.saveImmediate("Player", playerId.toString(), data)
            .thenRun(() -> {
                // 캐시 제거
                cache.invalidate(playerId);
                lastSaveTimes.remove(playerId);
                LogUtil.debug("플레이어 로그아웃 저장 완료: " + playerId);
            });
    }
    
    /**
     * 전체 배치 플러시 (서버 종료 시)
     */
    @NotNull
    public CompletableFuture<Void> flushAll() {
        return firestoreHelper.flushAll();
    }
    
    /**
     * 리소스 정리
     */
    public void shutdown() {
        firestoreHelper.shutdown();
        cache.cleanUp();
    }
    
    /**
     * 저장 필요 여부 확인
     */
    private boolean shouldSave(@NotNull UUID playerId, @NotNull DataPriority priority) {
        Long lastSave = lastSaveTimes.get(playerId);
        if (lastSave == null) {
            return true;
        }
        
        long elapsed = System.currentTimeMillis() - lastSave;
        return elapsed >= priority.getSaveIntervalMs();
    }
    
    /**
     * 캐시 통계 조회
     */
    @NotNull
    public PlayerDataCache.CacheStats getCacheStats() {
        return cache.getStats();
    }
}