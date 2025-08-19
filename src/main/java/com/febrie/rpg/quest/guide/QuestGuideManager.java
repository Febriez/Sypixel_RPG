package com.febrie.rpg.quest.guide;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.player.PlayerSettings;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.pathfinding.PathfindingUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import net.kyori.adventure.text.Component;
/**
 * 퀘스트 길안내 시스템 관리자
 * 플레이어에게 목표 지점까지의 경로를 파티클로 표시
 *
 * @author Febrie
 */
public class QuestGuideManager {
    
    private static QuestGuideManager instance;
    
    private final RPGMain plugin;
    private final Map<UUID, QuestGuide> activeGuides = new ConcurrentHashMap<>();
    private BukkitTask updateTask;
    
    // 설정
    private static final int PARTICLE_INTERVAL = 10; // 파티클 업데이트 간격 (틱)
    private static final double PARTICLE_SPACING = 2.0; // 파티클 간격 (블록)
    private static final int MAX_PARTICLE_DISTANCE = 50; // 최대 파티클 표시 거리
    private static final double ARRIVAL_DISTANCE = 3.0; // 도착 판정 거리
    
    public QuestGuideManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        instance = this;
        startUpdateTask();
    }
    
    public static QuestGuideManager getInstance() {
        return instance;
    }
    
    /**
     * 플레이어에게 퀘스트 길안내 시작
     *
     * @param player 플레이어
     * @param target 목표 위치
     * @param questName 퀘스트 이름 (표시용)
     */
    public void startGuide(@NotNull Player player, @NotNull Location target, @NotNull String questName) {
        // 플레이어 설정 확인
        RPGPlayer rpgPlayer = plugin.getRPGPlayerManager().getOrCreatePlayer(player);
        PlayerSettings settings = rpgPlayer.getPlayerSettings();
        
        if (!settings.isQuestAutoGuideEnabled()) {
            return; // 자동 길안내가 비활성화되어 있음
        }
        
        UUID playerId = player.getUniqueId();
        
        stopGuide(player);
        
        // 새로운 가이드 생성
        QuestGuide guide = new QuestGuide(player, target, questName);
        activeGuides.put(playerId, guide);
        
        // 경로 계산 (비동기)
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<Location> path = PathfindingUtil.findPath(player.getLocation(), target);
            
            Bukkit.getScheduler().runTask(plugin, () -> {
                QuestGuide activeGuide = activeGuides.get(playerId);
                if (activeGuide != null) {
                    if (path != null && !path.isEmpty()) {
                        activeGuide.setPath(path);
                        player.sendMessage("§a퀘스트 길안내가 시작되었습니다: " + questName);
                    } else {
                        player.sendMessage("§c경로를 찾을 수 없습니다. 직선 방향으로 이동해주세요.");
                        // 직선 경로 생성
                        List<Location> straightPath = createStraightPath(player.getLocation(), target);
                        activeGuide.setPath(straightPath);
                    }
                }
            });
        });
    }
    
    /**
     * 플레이어의 퀘스트 길안내 중지
     */
    public void stopGuide(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        QuestGuide guide = activeGuides.remove(playerId);
        
        if (guide != null) {
            player.sendMessage("§7퀘스트 길안내가 종료되었습니다.");
        }
    }
    
    /**
     * 모든 길안내 중지
     */
    public void stopAllGuides() {
        activeGuides.clear();
    }
    
    /**
     * 직선 경로 생성 (경로를 찾을 수 없을 때 사용)
     */
    @NotNull
    private List<Location> createStraightPath(@NotNull Location start, @NotNull Location target) {
        List<Location> path = new ArrayList<>();
        
        double distance = start.distance(target);
        int steps = (int) Math.ceil(distance / PARTICLE_SPACING);
        
        for (int i = 0; i <= steps; i++) {
            double ratio = (double) i / steps;
            Location point = start.clone().add(
                (target.getX() - start.getX()) * ratio,
                (target.getY() - start.getY()) * ratio,
                (target.getZ() - start.getZ()) * ratio
            );
            path.add(point);
        }
        
        return path;
    }
    
    /**
     * 업데이트 태스크 시작
     */
    private void startUpdateTask() {
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Iterator<Map.Entry<UUID, QuestGuide>> iterator = activeGuides.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<UUID, QuestGuide> entry = iterator.next();
                UUID playerId = entry.getKey();
                QuestGuide guide = entry.getValue();
                
                Player player = Bukkit.getPlayer(playerId);
                if (player == null || !player.isOnline()) {
                    iterator.remove();
                    continue;
                }
                
                updateGuide(player, guide, iterator);
            }
        }, 0L, PARTICLE_INTERVAL);
    }
    
    /**
     * 개별 가이드 업데이트
     */
    private void updateGuide(@NotNull Player player, @NotNull QuestGuide guide, @NotNull Iterator<Map.Entry<UUID, QuestGuide>> iterator) {
        if (guide.getPath() == null || guide.getPath().isEmpty()) {
            return; // 경로가 아직 계산되지 않음
        }
        
        Location playerLoc = player.getLocation();
        Location target = guide.getTarget();
        
        // 목표에 도달했는지 확인
        if (playerLoc.distance(target) <= ARRIVAL_DISTANCE) {
            player.sendMessage("§a목표 지점에 도달했습니다!");
            iterator.remove();
            return;
        }
        
        // 경로가 유효한지 확인 (주기적으로)
        if (guide.shouldRecalculatePath()) {
            recalculatePath(player, guide);
            return;
        }
        
        // 파티클 표시
        displayParticles(player, guide);
    }
    
    /**
     * 경로 재계산
     */
    private void recalculatePath(@NotNull Player player, @NotNull QuestGuide guide) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<Location> newPath = PathfindingUtil.findPath(player.getLocation(), guide.getTarget());
            
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (newPath != null && !newPath.isEmpty()) {
                    guide.setPath(newPath);
                } else {
                    // 직선 경로로 폴백
                    List<Location> straightPath = createStraightPath(player.getLocation(), guide.getTarget());
                    guide.setPath(straightPath);
                }
                guide.resetRecalculationTimer();
            });
        });
    }
    
    /**
     * 파티클 표시
     */
    private void displayParticles(@NotNull Player player, @NotNull QuestGuide guide) {
        Location playerLoc = player.getLocation();
        List<Location> path = guide.getPath();
        
        // 플레이어 근처의 경로 포인트만 표시
        for (Location point : path) {
            double distance = playerLoc.distance(point);
            
            if (distance <= MAX_PARTICLE_DISTANCE) {
                // 파티클 생성 (플레이어에게만 보임)
                player.spawnParticle(
                    Particle.END_ROD,
                    point.getX(),
                    point.getY() + 0.5, // 약간 위로
                    point.getZ(),
                    1, // 파티클 수
                    0, 0, 0, // 오프셋
                    0 // 속도
                );
            }
        }
    }
    
    /**
     * 플러그인 종료 시 정리
     */
    public void shutdown() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        activeGuides.clear();
    }
    
    /**
     * 플레이어가 활성 가이드를 가지고 있는지 확인
     */
    public boolean hasActiveGuide(@NotNull Player player) {
        return activeGuides.containsKey(player.getUniqueId());
    }
    
    /**
     * 플레이어의 현재 가이드 정보 가져오기
     */
    @Nullable
    public QuestGuide getActiveGuide(@NotNull Player player) {
        return activeGuides.get(player.getUniqueId());
    }
}