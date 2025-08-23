package com.febrie.rpg.quest.task;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.quest.ActiveQuestDTO;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

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
                    Map<String, ActiveQuestDTO> activeQuests = questManager.getActiveQuests(playerId);

                    for (Map.Entry<String, ActiveQuestDTO> entry : activeQuests.entrySet()) {
                        ActiveQuestDTO activeData = entry.getValue();
                        Quest quest = QuestManager.getInstance().getQuest(QuestID.valueOf(activeData.questId()));
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
            for (Map.Entry<String, ActiveQuestDTO> questEntry : data.activeQuests.entrySet()) {
                String instanceId = questEntry.getKey();
                ActiveQuestDTO activeData = questEntry.getValue();
                // DTO에서 QuestProgress 복원
                java.util.Map<String, ObjectiveProgress> progressMap = new java.util.HashMap<>();
                activeData.progress().forEach((key, value) -> progressMap.put(key, ObjectiveProgress.from(value, data.player.getUniqueId())));
                QuestProgress progress = new QuestProgress(QuestID.valueOf(activeData.questId()), data.player.getUniqueId(), progressMap);

                if (!progress.isActive()) continue;

                Quest quest = QuestManager.getInstance().getQuest(QuestID.valueOf(activeData.questId()));
                if (quest == null) continue;

                // 각 목표 확인
                for (QuestObjective objective : quest.getObjectives()) {
                    if (!(objective instanceof VisitLocationObjective visitObj)) continue;

                    // 이미 완료된 목표는 스킵
                    ObjectiveProgress objProgress = progress.getObjective(objective.getId());
                    if (objProgress == null || objProgress.isCompleted()) {
                        continue;
                    }

                    boolean shouldProgress = visitObj.checkLocation(data.player, data.currentLocation);


                    // 메인 스레드에서 QuestProgressService를 통해 처리
                    if (shouldProgress) {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            // QuestProgressService를 통해 위치 체크 처리
                            QuestManager questManager = QuestManager.getInstance();
                            if (questManager.getProgressService() != null) {
                                questManager.getProgressService().handleLocationCheck(data.player, data.player.getLocation());
                            }
                        });
                    }
                }
            }
        }
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
    private record PlayerLocationData(Player player, Location currentLocation, Location lastLocation,
                                      Map<String, ActiveQuestDTO> activeQuests) {
    }
}