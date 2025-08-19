package com.febrie.rpg.quest.service;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.dto.quest.ActiveQuestDTO;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.util.QuestUtil;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import net.kyori.adventure.text.Component;
import java.util.List;
/**
 * 퀘스트 진행도 처리를 중앙화한 서비스
 * 모든 14개 목표 타입의 진행도 업데이트를 통합 관리
 * - 상태 없는 비즈니스 로직 처리
 * - 의존성 주입을 통한 관리
 * 
 * @author Febrie
 */
public final class QuestProgressService {
    
    private final RPGMain plugin;
    
    public QuestProgressService(@NotNull RPGMain plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 목표 처리기 인터페이스
     */
    @FunctionalInterface
    private interface ObjectiveProcessor {
        boolean process(QuestObjective objective, Event event, Player player);
    }
    
    /**
     * 퀘스트 진행 데이터
     */
    private record QuestProcessingData(
        String instanceId,
        Quest quest,
        QuestProgress progress,
        List<String> objectivesToCheck
    ) {}
    
    // ==================== Bukkit 이벤트 처리 ====================
    
    /**
     * EntityDeathEvent 처리 - KillMob, KillPlayer
     */
    public void handleEntityDeath(@NotNull EntityDeathEvent event, @NotNull Player killer) {
        processEvent(killer, event, (objective, evt, player) -> {
            if (objective instanceof KillMobObjective killMob) {
                return killMob.canProgress(evt, player);
            } else if (objective instanceof KillPlayerObjective killPlayer) {
                LivingEntity entity = ((EntityDeathEvent) evt).getEntity();
                return entity instanceof Player && killPlayer.canProgress(evt, player);
            }
            return false;
        });
    }
    
    /**
     * BlockBreakEvent 처리 - BreakBlock, Harvest
     */
    public void handleBlockBreak(@NotNull BlockBreakEvent event) {
        processEvent(event.getPlayer(), event, (objective, evt, player) -> {
            if (objective instanceof BreakBlockObjective breakBlock) {
                return breakBlock.canProgress(evt, player);
            } else if (objective instanceof HarvestObjective harvest) {
                return harvest.canProgress(evt, player);
            }
            return false;
        });
    }
    
    /**
     * EntityPickupItemEvent 처리 - CollectItem
     */
    public void handleItemPickup(@NotNull EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        
        processEvent(player, event, (objective, evt, p) -> 
            objective instanceof CollectItemObjective collectItem && 
            collectItem.canProgress(evt, p)
        );
    }
    
    /**
     * CraftItemEvent 처리 - CraftItem
     */
    public void handleCraftItem(@NotNull CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        processEvent(player, event, (objective, evt, p) ->
            objective instanceof CraftItemObjective craftItem && 
            craftItem.canProgress(evt, p)
        );
    }
    
    /**
     * BlockPlaceEvent 처리 - PlaceBlock
     */
    public void handleBlockPlace(@NotNull BlockPlaceEvent event) {
        processEvent(event.getPlayer(), event, (objective, evt, player) ->
            objective instanceof PlaceBlockObjective placeBlock && 
            placeBlock.canProgress(evt, player)
        );
    }
    
    /**
     * PlayerFishEvent 처리 - Fishing
     */
    public void handleFishing(@NotNull PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        
        processEvent(event.getPlayer(), event, (objective, evt, player) ->
            objective instanceof FishingObjective fishing && 
            fishing.canProgress(evt, player)
        );
    }
    
    /**
     * PlayerInteractEntityEvent 처리 - DeliverItem
     */
    public void handleEntityInteract(@NotNull PlayerInteractEntityEvent event, @NotNull String npcId) {
        // DeliverItem 목표는 별도 구현 필요
    }
    
    // ==================== 특수 처리 ====================
    
    /**
     * 위치 방문 체크 (LocationCheckTask에서 호출)
     */
    public void handleLocationCheck(@NotNull Player player, @NotNull Location location) {
        UUID playerId = player.getUniqueId();
        Map<String, ActiveQuestDTO> activeQuests = QuestManager.getInstance().getActiveQuests(playerId);
        if (activeQuests.isEmpty()) return;
        
        boolean anyProgressMade = false;
        
        for (QuestProcessingData data : prepareQuestData(playerId, activeQuests)) {
            boolean progressMade = false;
            
            for (String objectiveId : data.objectivesToCheck()) {
                QuestObjective objective = getObjective(data.quest(), objectiveId);
                if (objective instanceof VisitLocationObjective visitLocation) {
                    if (visitLocation.checkLocation(player, location)) {
                        ObjectiveProgress objProgress = data.progress().getObjective(objectiveId);
                        if (objProgress != null && !objective.isComplete(objProgress)) {
                            // 진행도 증가를 updateObjectiveProgress 내부에서 처리
                            if (updateObjectiveProgress(player, data.instanceId(), objectiveId, 
                                    1, data.quest(), data.progress())) {
                                progressMade = true;
                            }
                        }
                    }
                }
            }
            
            if (progressMade) {
                saveProgress(playerId, data.instanceId(), data.progress());
                anyProgressMade = true;
            }
        }
        
        if (anyProgressMade) {
            QuestManager.getInstance().markForSave(playerId);
        }
    }
    
    /**
     * NPC 상호작용 처리 (NPCInteractListener에서 호출)
     */
    public void handleNPCInteraction(@NotNull Player player, @NotNull String npcId) {
        // QuestManager의 기존 로직 활용
        QuestManager.getInstance().handleNPCInteraction(player, npcId);
    }
    
    // ==================== 공통 처리 로직 ====================
    
    /**
     * 이벤트 처리 공통 메소드
     */
    private void processEvent(@NotNull Player player, @NotNull Event event, 
                              @NotNull ObjectiveProcessor processor) {
        UUID playerId = player.getUniqueId();
        Map<String, ActiveQuestDTO> activeQuests = QuestManager.getInstance().getActiveQuests(playerId);
        if (activeQuests.isEmpty()) return;
        
        boolean anyProgressMade = false;
        
        for (QuestProcessingData data : prepareQuestData(playerId, activeQuests)) {
            boolean progressMade = processQuestObjectives(
                data, event, player, processor
            );
            
            if (progressMade) {
                saveProgress(playerId, data.instanceId(), data.progress());
                anyProgressMade = true;
            }
        }
        
        if (anyProgressMade) {
            QuestManager.getInstance().markForSave(playerId);
        }
    }
    
    /**
     * 퀘스트 데이터 준비
     */
    private List<QuestProcessingData> prepareQuestData(@NotNull UUID playerId, 
                                                       @NotNull Map<String, ActiveQuestDTO> activeQuests) {
        List<QuestProcessingData> result = new ArrayList<>();
        
        for (Map.Entry<String, ActiveQuestDTO> entry : activeQuests.entrySet()) {
            String instanceId = entry.getKey();
            ActiveQuestDTO activeData = entry.getValue();
            
            Quest quest = getQuest(activeData.questId());
            if (quest == null) continue;
            
            // ActiveQuestDTO에서 QuestProgress 생성
            Map<String, ObjectiveProgress> progressMap = new HashMap<>();
            activeData.progress().forEach((key, value) -> 
                progressMap.put(key, ObjectiveProgress.from(value, playerId)));
            QuestProgress progress = new QuestProgress(
                QuestID.valueOf(activeData.questId()), playerId, progressMap
            );
            
            List<String> objectivesToCheck = getObjectivesToProgress(quest, progress);
            
            result.add(new QuestProcessingData(instanceId, quest, progress, objectivesToCheck));
        }
        
        return result;
    }
    
    /**
     * 퀘스트 목표 처리
     */
    private boolean processQuestObjectives(@NotNull QuestProcessingData data,
                                          @NotNull Event event,
                                          @NotNull Player player,
                                          @NotNull ObjectiveProcessor processor) {
        boolean progressMade = false;
        
        for (String objectiveId : data.objectivesToCheck()) {
            QuestObjective objective = getObjective(data.quest(), objectiveId);
            if (objective == null) continue;
            
            if (processor.process(objective, event, player)) {
                int increment = objective.calculateIncrement(event, player);
                if (updateObjectiveProgress(player, data.instanceId(), objectiveId, 
                        increment, data.quest(), data.progress())) {
                    progressMade = true;
                }
            }
        }
        
        return progressMade;
    }
    
    // ==================== 헬퍼 메서드 ====================
    
    /**
     * 목표 진행도 업데이트
     */
    private boolean updateObjectiveProgress(@NotNull Player player, @NotNull String instanceId, 
                                           @NotNull String objectiveId, int increment,
                                           @NotNull Quest quest, @NotNull QuestProgress progress) {
        ObjectiveProgress objProgress = progress.getObjective(objectiveId);
        if (objProgress == null) return false;
        
        QuestObjective objective = getObjective(quest, objectiveId);
        if (objective == null) return false;
        
        // 이미 완료된 목표는 스킵
        if (objective.isComplete(objProgress)) return false;
        
        // 진행도 증가
        if (increment > 0) {
            objProgress.increment(increment);
        }
        
        // 목표 완료 체크
        if (objective.isComplete(objProgress)) {
            // 진행 알림
            QuestUtil.notifyObjectiveComplete(player, quest, progress, objective, plugin);
            
            // 순차 진행인 경우 다음 목표로
            if (quest.isSequential()) {
                progress.setCurrentObjectiveIndex(progress.getCurrentObjectiveIndex() + 1);
            }
            
            // 퀘스트 완료 체크
            if (progress.areAllObjectivesComplete()) {
                QuestManager.getInstance().completeQuest(player, instanceId);
            }
        }
        
        return true;
    }
    
    /**
     * 처리할 목표 목록 결정 (순차/자유 진행)
     */
    private List<String> getObjectivesToProgress(@NotNull Quest quest, @NotNull QuestProgress progress) {
        if (quest.isSequential()) {
            // 순차 진행 - 현재 목표만
            int currentIndex = progress.getCurrentObjectiveIndex();
            if (currentIndex < quest.getObjectives().size()) {
                return List.of(quest.getObjectives().get(currentIndex).getId());
            }
            return List.of();
        } else {
            // 자유 진행 - 모든 미완료 목표
            return quest.getObjectives().stream()
                .map(QuestObjective::getId)
                .filter(id -> !progress.isObjectiveComplete(id))
                .collect(Collectors.toList());
        }
    }
    
    /**
     * 퀘스트 가져오기
     */
    @Nullable
    private Quest getQuest(@NotNull String questId) {
        try {
            QuestID id = QuestID.valueOf(questId);
            return QuestManager.getInstance().getQuest(id);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * 목표 가져오기
     */
    @Nullable
    private QuestObjective getObjective(@NotNull Quest quest, @NotNull String objectiveId) {
        return quest.getObjectives().stream()
            .filter(obj -> obj.getId().equals(objectiveId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 진행도 저장
     */
    private void saveProgress(@NotNull UUID playerId, @NotNull String instanceId, @NotNull QuestProgress progress) {
        // ActiveQuestDTO로 변환하여 저장
        ActiveQuestDTO updatedData = ActiveQuestDTO.create(
            progress.getQuestId().name(),
            instanceId,
            progress.getObjectives()
        );
        
        // QuestManager를 통해 업데이트
        QuestManager.getInstance().updateActiveQuest(playerId, instanceId, updatedData);
    }
    
}