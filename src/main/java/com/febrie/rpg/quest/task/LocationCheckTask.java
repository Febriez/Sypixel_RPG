package com.febrie.rpg.quest.task;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.util.SoundUtil;
import com.febrie.rpg.util.ToastUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import com.febrie.rpg.util.LogUtil;

/**
 * 지역 방문 퀘스트 목표를 비동기로 체크하는 태스크
 * 3초마다 온라인 플레이어들의 위치를 확인하여 퀘스트 진행
 *
 * @author Febrie
 */
public class LocationCheckTask implements Runnable {
    
    private final RPGMain plugin;
    
    // 플레이어별 마지막 위치 캐시 (이동 감지용)
    private final Map<UUID, Location> lastLocations = new ConcurrentHashMap<>();
    
    
    public LocationCheckTask(@NotNull RPGMain plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        // 온라인 플레이어 목록 가져오기
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.isEmpty()) return;
        
        
        // 비동기 처리
        CompletableFuture.runAsync(() -> {
            Map<UUID, PlayerLocationData> playerDataMap = new HashMap<>();
            
            // 메인 스레드에서 플레이어 위치 정보 수집
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (Player player : onlinePlayers) {
                    if (!player.isOnline()) continue;
                    
                    UUID playerId = player.getUniqueId();
                    Location currentLocation = player.getLocation();
                    
                    // 지역 방문 목표가 있는 활성 퀘스트가 있는지 확인
                    boolean hasLocationObjective = false;
                    QuestManager questManager = QuestManager.getInstance();
                    List<QuestProgress> activeQuests = questManager.getActiveQuests(playerId);
                    
                    
                    for (QuestProgress progress : activeQuests) {
                        Quest quest = QuestManager.getInstance().getQuest(progress.getQuestId());
                        if (quest == null) continue;
                        
                        
                        for (QuestObjective objective : quest.getObjectives()) {
                            if (objective instanceof VisitLocationObjective) {
                                hasLocationObjective = true;
                                break;
                            }
                        }
                        if (hasLocationObjective) break;
                    }
                    
                    // 지역 방문 목표가 있는 경우만 처리
                    if (hasLocationObjective) {
                        playerDataMap.put(playerId, new PlayerLocationData(
                            player,
                            currentLocation.clone(),
                            lastLocations.get(playerId),
                            activeQuests
                        ));
                        lastLocations.put(playerId, currentLocation.clone());
                    }
                }
                
                // 비동기로 처리 계속
                CompletableFuture.runAsync(() -> processLocationChecks(playerDataMap));
            });
        });
    }
    
    /**
     * 비동기로 위치 체크 처리
     */
    private void processLocationChecks(@NotNull Map<UUID, PlayerLocationData> playerDataMap) {
        for (Map.Entry<UUID, PlayerLocationData> entry : playerDataMap.entrySet()) {
            UUID playerId = entry.getKey();
            PlayerLocationData data = entry.getValue();
            
            // 이동하지 않았으면 스킵
            if (data.lastLocation != null) {
                if (data.currentLocation.getBlockX() == data.lastLocation.getBlockX() &&
                    data.currentLocation.getBlockZ() == data.lastLocation.getBlockZ()) {
                    continue;
                }
            }
            
            
            // 퀘스트 목표 처리
            for (QuestProgress progress : data.activeQuests) {
                if (!progress.isActive()) continue;
                
                Quest quest = QuestManager.getInstance().getQuest(progress.getQuestId());
                if (quest == null) continue;
                
                // 각 목표 확인
                for (QuestObjective objective : quest.getObjectives()) {
                    if (!(objective instanceof VisitLocationObjective visitObj)) continue;
                    
                    
                    // 이미 완료된 목표는 스킵
                    ObjectiveProgress objProgress = progress.getObjective(objective.getId());
                    if (objProgress == null || objProgress.isCompleted()) {
                        continue;
                    }
                    
                    boolean shouldProgress = false;
                    
                    // VisitLocationObjective의 checkLocation 메서드 호출
                    if (visitObj.checkLocation(data.player, data.currentLocation)) {
                        shouldProgress = true;
                    }
                    
                    // 메인 스레드에서 퀘스트 진행 처리
                    if (shouldProgress) {
                        final String objId = objective.getId();
                        
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            // 퀘스트 진행 처리 - 목표 완료 처리
                            ObjectiveProgress objProg = progress.getObjective(objId);
                            if (objProg != null && !objProg.isCompleted()) {
                                int oldValue = objProg.getCurrentValue();
                                int requiredValue = objProg.getRequiredValue();
                                
                                objProg.increment(1); // 방문 목표는 1회만 완료하면 됨
                                
                                // 목표가 완료되었을 때만 알림
                                if (oldValue < requiredValue && objProg.getCurrentValue() >= requiredValue) {
                                    // 토스트 알림 표시 - 목표 완료
                                    ToastUtil.showObjectiveCompleteToast(data.player, quest, objective);
                                    
                                    // 채팅 메시지
                                    boolean isKorean = com.febrie.rpg.RPGMain.getPlugin().getLangManager().getPlayerLanguage(data.player).startsWith("ko");
                                    data.player.sendMessage(net.kyori.adventure.text.Component.text(
                                        "✓ " + quest.getObjectiveDescription(objective, isKorean),
                                        com.febrie.rpg.util.ColorUtil.SUCCESS
                                    ));
                                    
                                    // 소리 재생
                                    SoundUtil.playSuccessSound(data.player);
                                    
                                    // 순차 진행인 경우 다음 목표로
                                    if (quest.isSequential()) {
                                        progress.setCurrentObjectiveIndex(progress.getCurrentObjectiveIndex() + 1);
                                    }
                                }
                                
                                // 퀘스트 매니저를 통해 진행 상태 업데이트 및 완료 확인
                                QuestManager questManager = QuestManager.getInstance();
                                boolean questCompleted = questManager.checkQuestCompletion(data.player.getUniqueId(), quest.getId());
                                
                                // 퀘스트 전체가 완료되었을 때 토스트 메시지
                                if (questCompleted) {
                                    ToastUtil.showQuestCompleteToast(data.player, quest);
                                }
                                
                                // 데이터 저장 예약
                                questManager.markForSave(data.player.getUniqueId());
                            }
                        });
                    }
                }
            }
        }
    }
    
    /**
     * 위치의 WorldGuard 영역 목록 가져오기
     */
    private Set<String> getRegionsAt(@NotNull Location location) {
        Set<String> regionSet = new HashSet<>();
        
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            LogUtil.warning("[WorldGuard] Plugin not found!");
            return regionSet;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(location.getWorld()));

        if (regionManager != null) {
            var regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
            for (ProtectedRegion region : regions) {
                regionSet.add(region.getId());
            }
            if (!regionSet.isEmpty()) {
            }
        } else {
            LogUtil.warning("[WorldGuard] RegionManager is null for world: " + location.getWorld().getName());
        }

        return regionSet;
    }
    
    /**
     * 플레이어 캐시 정리
     */
    public void clearPlayerCache(@NotNull UUID playerId) {
        lastLocations.remove(playerId);
    }
    
    /**
     * 모든 캐시 정리
     */
    public void clearAllCaches() {
        lastLocations.clear();
    }
    
    /**
     * 플레이어 위치 데이터
     */
    private static class PlayerLocationData {
        final Player player;
        final Location currentLocation;
        final Location lastLocation;
        final List<QuestProgress> activeQuests;
        
        PlayerLocationData(Player player, Location currentLocation, Location lastLocation, List<QuestProgress> activeQuests) {
            this.player = player;
            this.currentLocation = currentLocation;
            this.lastLocation = lastLocation;
            this.activeQuests = activeQuests;
        }
    }
}