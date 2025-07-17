package com.febrie.rpg.util.display;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.player.PlayerSettings;
import com.febrie.rpg.player.RPGPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 데미지 표시 시스템 관리자
 * ArmorStand를 사용하여 데미지 홀로그램을 표시
 *
 * @author Febrie
 */
public class DamageDisplayManager {
    
    private static DamageDisplayManager instance;
    
    private final RPGMain plugin;
    private final Map<UUID, BukkitTask> damageDisplayTasks = new ConcurrentHashMap<>();
    
    // 설정
    private static final double DISPLAY_HEIGHT_OFFSET = 2.0; // 엔티티 위 2블록 높이에 표시
    private static final long DISPLAY_DURATION = 60L; // 3초 (20틱 * 3)
    private static final double RANDOM_OFFSET_RANGE = 0.5; // 랜덤 오프셋 범위
    
    public DamageDisplayManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        instance = this;
    }
    
    public static DamageDisplayManager getInstance() {
        return instance;
    }
    
    /**
     * 데미지를 표시합니다
     *
     * @param attacker 공격자 (데미지 표시를 볼 플레이어)
     * @param target 피해자 (데미지 표시가 나타날 위치의 기준)
     * @param damage 데미지 양
     * @param isCritical 크리티컬 히트 여부
     */
    public void displayDamage(@NotNull Player attacker, @NotNull Entity target, double damage, boolean isCritical) {
        // 플레이어 설정 확인
        RPGPlayer rpgPlayer = plugin.getRPGPlayerManager().getOrCreatePlayer(attacker);
        PlayerSettings settings = rpgPlayer.getPlayerSettings();
        
        if (!settings.isDamageDisplayEnabled()) {
            return; // 데미지 표시가 비활성화되어 있음
        }
        
        // 데미지 표시 위치 계산
        Location displayLocation = calculateDisplayLocation(target);
        
        // 데미지 텍스트 생성
        String damageText = formatDamageText(damage, isCritical);
        
        // ArmorStand 생성 (플레이어에게만 보임)
        createDamageHologram(attacker, displayLocation, damageText);
    }
    
    /**
     * 데미지 표시 위치 계산
     */
    @NotNull
    private Location calculateDisplayLocation(@NotNull Entity target) {
        Location location = target.getLocation().clone();
        
        // 엔티티 위쪽으로 오프셋
        location.add(0, DISPLAY_HEIGHT_OFFSET, 0);
        
        // 랜덤 오프셋 추가 (여러 데미지가 겹치지 않도록)
        double randomX = (Math.random() - 0.5) * RANDOM_OFFSET_RANGE;
        double randomZ = (Math.random() - 0.5) * RANDOM_OFFSET_RANGE;
        location.add(randomX, 0, randomZ);
        
        return location;
    }
    
    /**
     * 데미지 텍스트 포맷팅
     */
    @NotNull
    private String formatDamageText(double damage, boolean isCritical) {
        String color = isCritical ? "§6§l" : "§c"; // 크리티컬은 금색, 일반은 빨간색
        String prefix = isCritical ? "CRIT! " : "";
        
        // 소수점 제거
        if (damage == Math.floor(damage)) {
            return color + prefix + (int) damage;
        } else {
            return color + prefix + String.format("%.1f", damage);
        }
    }
    
    /**
     * 데미지 홀로그램 생성
     */
    private void createDamageHologram(@NotNull Player viewer, @NotNull Location location, @NotNull String text) {
        // ArmorStand 생성
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        
        // ArmorStand 설정
        armorStand.customName(Component.text(text));
        armorStand.setCustomNameVisible(true);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setCollidable(false);
        armorStand.setSmall(true);
        armorStand.setSilent(true);
        
        // 다른 플레이어에게는 보이지 않도록 처리 (Paper/Spigot의 경우)
        try {
            // 다른 플레이어들에게 ArmorStand 숨기기
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.equals(viewer)) {
                    player.hideEntity(plugin, armorStand);
                }
            }
        } catch (Exception e) {
            // hideEntity 메서드가 없는 경우 무시 (오래된 버전)
        }
        
        // 위쪽으로 이동 애니메이션
        animateDamageDisplay(armorStand, viewer);
        
        // 3초 후 제거
        UUID taskId = UUID.randomUUID();
        BukkitTask removeTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            armorStand.remove();
            damageDisplayTasks.remove(taskId);
        }, DISPLAY_DURATION);
        
        damageDisplayTasks.put(taskId, removeTask);
    }
    
    /**
     * 데미지 표시 애니메이션
     */
    private void animateDamageDisplay(@NotNull ArmorStand armorStand, @NotNull Player viewer) {
        UUID taskId = UUID.randomUUID();
        
        BukkitTask animationTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            private int ticks = 0;
            private final double initialY = armorStand.getLocation().getY();
            
            @Override
            public void run() {
                if (armorStand.isDead() || !armorStand.isValid()) {
                    BukkitTask task = damageDisplayTasks.remove(taskId);
                    if (task != null) {
                        task.cancel();
                    }
                    return;
                }
                
                // 위로 천천히 이동
                double newY = initialY + (ticks * 0.02); // 틱당 0.02블록씩 위로
                Location newLocation = armorStand.getLocation();
                newLocation.setY(newY);
                armorStand.teleport(newLocation);
                
                // 투명도 효과 (마지막 1초 동안)
                if (ticks > 40) { // 2초 후부터
                    double alpha = 1.0 - ((ticks - 40) / 20.0); // 점점 투명해짐
                    if (alpha <= 0) {
                        armorStand.remove();
                        BukkitTask task = damageDisplayTasks.remove(taskId);
                        if (task != null) {
                            task.cancel();
                        }
                        return;
                    }
                }
                
                ticks++;
                
                // 최대 3초 후 강제 종료
                if (ticks >= 60) {
                    armorStand.remove();
                    BukkitTask task = damageDisplayTasks.remove(taskId);
                    if (task != null) {
                        task.cancel();
                    }
                }
            }
        }, 1L, 1L);
        
        damageDisplayTasks.put(taskId, animationTask);
    }
    
    /**
     * 특정 플레이어의 모든 데미지 표시 제거
     */
    public void clearDamageDisplays(@NotNull Player player) {
        // 해당 플레이어 관련 태스크들을 찾아서 제거하는 것은 복잡하므로
        // 전체 정리는 shutdown에서 처리
    }
    
    /**
     * 모든 데미지 표시 제거 및 정리
     */
    public void shutdown() {
        // 모든 활성 태스크 취소
        for (BukkitTask task : damageDisplayTasks.values()) {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
        damageDisplayTasks.clear();
        
        // 월드의 모든 ArmorStand 중 데미지 표시용인 것들 제거
        Bukkit.getWorlds().forEach(world -> {
            world.getEntitiesByClass(ArmorStand.class).forEach(armorStand -> {
                Component customName = armorStand.customName();
                if (customName != null) {
                    String nameString = customName.toString();
                    if (nameString.contains("§c") || nameString.contains("§6")) {
                        armorStand.remove();
                    }
                }
            });
        });
    }
    
    /**
     * 데미지 표시 시스템이 활성화되어 있는지 확인
     */
    public boolean isDamageDisplayEnabled(@NotNull Player player) {
        RPGPlayer rpgPlayer = plugin.getRPGPlayerManager().getOrCreatePlayer(player);
        return rpgPlayer.getPlayerSettings().isDamageDisplayEnabled();
    }
}