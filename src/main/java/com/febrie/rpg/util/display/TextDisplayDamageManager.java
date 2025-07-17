package com.febrie.rpg.util.display;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.player.PlayerSettings;
import com.febrie.rpg.player.RPGPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * TextDisplay를 사용한 데미지 표시 시스템
 * 1.19.4+ 에서 추가된 Display 엔티티 활용
 *
 * @author Febrie
 */
public class TextDisplayDamageManager {
    
    private static TextDisplayDamageManager instance;
    
    private final RPGMain plugin;
    private final Map<UUID, BukkitTask> displayTasks = new ConcurrentHashMap<>();
    
    // 설정
    private static final double DISPLAY_HEIGHT_OFFSET = 0.5;
    private static final long DISPLAY_DURATION = 60L; // 3초
    private static final double RANDOM_OFFSET_RANGE = 0.3;
    private static final double RISE_SPEED = 0.02; // 틱당 올라가는 높이
    
    public TextDisplayDamageManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        instance = this;
    }
    
    public static TextDisplayDamageManager getInstance() {
        return instance;
    }
    
    /**
     * 데미지를 표시합니다
     */
    public void displayDamage(@NotNull Player attacker, @NotNull Entity target, double damage, boolean isCritical) {
        // 플레이어 설정 확인
        RPGPlayer rpgPlayer = plugin.getRPGPlayerManager().getOrCreatePlayer(attacker);
        PlayerSettings settings = rpgPlayer.getPlayerSettings();
        
        if (!settings.isDamageDisplayEnabled()) {
            return;
        }
        
        // 위치 계산
        Location displayLocation = calculateDisplayLocation(target);
        
        // TextDisplay 생성
        createDamageDisplay(attacker, displayLocation, damage, isCritical);
    }
    
    /**
     * TextDisplay 생성 및 설정
     */
    private void createDamageDisplay(@NotNull Player viewer, @NotNull Location location, double damage, boolean isCritical) {
        // TextDisplay 엔티티 생성
        TextDisplay display = (TextDisplay) location.getWorld().spawnEntity(location, EntityType.TEXT_DISPLAY);
        
        // 텍스트 설정
        Component text = formatDamageText(damage, isCritical);
        display.text(text);
        
        // Display 설정
        display.setBillboard(Display.Billboard.CENTER); // 항상 플레이어를 향하도록
        display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0)); // 투명 배경
        display.setSeeThrough(true); // 블록을 통과해서 보임
        display.setBrightness(new Display.Brightness(15, 15)); // 최대 밝기
        
        // 크기 조정 (작게)
        Transformation transformation = display.getTransformation();
        transformation.getScale().set(0.8f); // 80% 크기
        display.setTransformation(transformation);
        
        // 다른 플레이어에게 숨기기
        display.setVisibleByDefault(false);
        viewer.showEntity(plugin, display);
        
        // 애니메이션 시작
        animateDisplay(display, viewer.getUniqueId());
    }
    
    /**
     * 데미지 텍스트 포맷팅
     */
    @NotNull
    private Component formatDamageText(double damage, boolean isCritical) {
        String damageStr;
        if (damage == Math.floor(damage)) {
            damageStr = String.valueOf((int) damage);
        } else {
            damageStr = String.format("%.1f", damage);
        }
        
        if (isCritical) {
            return Component.text("CRIT! ", NamedTextColor.GOLD, TextDecoration.BOLD)
                    .append(Component.text(damageStr, NamedTextColor.GOLD, TextDecoration.BOLD));
        } else {
            return Component.text(damageStr, NamedTextColor.RED);
        }
    }
    
    /**
     * Display 애니메이션
     */
    private void animateDisplay(@NotNull TextDisplay display, @NotNull UUID viewerId) {
        UUID taskId = UUID.randomUUID();
        
        BukkitTask animationTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            private int ticks = 0;
            private final Location startLocation = display.getLocation().clone();
            
            @Override
            public void run() {
                if (!display.isValid() || ticks >= DISPLAY_DURATION) {
                    display.remove();
                    BukkitTask task = displayTasks.remove(taskId);
                    if (task != null) {
                        task.cancel();
                    }
                    return;
                }
                
                // 위로 이동
                Location newLocation = startLocation.clone();
                newLocation.add(0, ticks * RISE_SPEED, 0);
                display.teleport(newLocation);
                
                // 페이드아웃 효과 (마지막 1초)
                if (ticks > 40) {
                    float progress = (float)(ticks - 40) / 20f; // 0.0 ~ 1.0
                    int alpha = (int)(255 * (1 - progress));
                    
                    // 투명도 조절을 위해 Transformation 사용
                    Transformation transformation = display.getTransformation();
                    float scale = 0.8f * (1 - progress * 0.3f); // 크기도 점점 작아짐
                    transformation.getScale().set(scale);
                    display.setTransformation(transformation);
                    
                    // 텍스트 색상으로 페이드 효과 (간접적)
                    if (alpha < 128) {
                        display.setBrightness(new Display.Brightness(alpha / 8, alpha / 8));
                    }
                }
                
                ticks++;
            }
        }, 1L, 1L);
        
        displayTasks.put(taskId, animationTask);
    }
    
    /**
     * 데미지 표시 위치 계산
     */
    @NotNull
    private Location calculateDisplayLocation(@NotNull Entity target) {
        Location location = target.getLocation().clone();
        
        // 엔티티 높이 위에 표시
        location.add(0, target.getHeight() + DISPLAY_HEIGHT_OFFSET, 0);
        
        // 랜덤 오프셋 (여러 데미지가 겹치지 않도록)
        double randomX = (ThreadLocalRandom.current().nextDouble() - 0.5) * RANDOM_OFFSET_RANGE;
        double randomZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * RANDOM_OFFSET_RANGE;
        location.add(randomX, 0, randomZ);
        
        return location;
    }
    
    /**
     * 특정 플레이어의 모든 데미지 표시 제거
     */
    public void clearDamageDisplays(@NotNull Player player) {
        // TextDisplay는 자동으로 제거되므로 특별한 처리 불필요
    }
    
    /**
     * 모든 데미지 표시 제거 및 정리
     */
    public void shutdown() {
        // 모든 활성 태스크 취소
        for (BukkitTask task : displayTasks.values()) {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
        displayTasks.clear();
        
        // 월드의 모든 TextDisplay 제거
        Bukkit.getWorlds().forEach(world -> {
            world.getEntitiesByClass(TextDisplay.class).forEach(display -> {
                Component text = display.text();
                if (text != null) {
                    String plainText = text.toString();
                    // 데미지 표시로 보이는 것들 제거
                    if (plainText.contains("CRIT") || plainText.matches(".*\\d+.*")) {
                        display.remove();
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