package com.febrie.rpg.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.util.display.DamageDisplayManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 데미지 표시 이벤트 리스너
 * EntityDamageByEntityEvent를 감지하여 데미지 홀로그램을 표시
 *
 * @author Febrie
 */
public class DamageDisplayListener implements Listener {
    
    private final RPGMain plugin;
    private final DamageDisplayManager damageDisplayManager;
    
    public DamageDisplayListener(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.damageDisplayManager = DamageDisplayManager.getInstance();
    }
    
    /**
     * 엔티티가 다른 엔티티에게 데미지를 받을 때 호출
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        
        // 피해를 입은 엔티티가 LivingEntity가 아니면 무시
        if (!(damaged instanceof LivingEntity)) {
            return;
        }
        
        // 실제 공격자 찾기 (플레이어여야 함)
        Player attacker = getActualAttacker(damager);
        if (attacker == null) {
            return;
        }
        
        // 데미지 표시가 활성화되어 있는지 확인
        if (!damageDisplayManager.isDamageDisplayEnabled(attacker)) {
            return;
        }
        
        // 데미지 계산
        double damage = event.getFinalDamage();
        
        // 크리티컬 판정 (임시로 랜덤 10% 확률)
        boolean isCritical = Math.random() < 0.1;
        
        // 크리티컬이면 데미지 1.5배
        if (isCritical) {
            damage *= 1.5;
        }
        
        // 데미지 표시
        damageDisplayManager.displayDamage(attacker, damaged, damage, isCritical);
    }
    
    /**
     * 실제 공격자 플레이어 찾기
     * 화살, 트라이던트 등의 투사체도 고려
     */
    @Nullable
    private Player getActualAttacker(@NotNull Entity damager) {
        // 직접 공격
        if (damager instanceof Player player) {
            return player;
        }
        
        // 투사체 공격
        if (damager instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player player) {
                return player;
            }
        }
        
        // 기타 간접 공격 (TNT, 늑대 등)은 현재 지원하지 않음
        return null;
    }
    
    /**
     * 크리티컬 히트 판정 로직
     * 나중에 더 정교한 로직으로 교체 가능
     */
    private boolean isCriticalHit(@NotNull Player attacker, @NotNull Entity target) {
        // 기본 크리티컬 확률 (나중에 플레이어 스탯으로 계산)
        double baseCritChance = 0.1; // 10%
        
        // 플레이어의 럭 스탯이나 장비에 따라 크리티컬 확률 조정 가능
        // RPGPlayer rpgPlayer = plugin.getRPGPlayerManager().getOrCreatePlayer(attacker);
        // double critChance = baseCritChance + (rpgPlayer.getStats().getTotalStat(Stat.LUCK) * 0.01);
        
        return Math.random() < baseCritChance;
    }
    
    /**
     * 크리티컬 데미지 배율 계산
     * 나중에 더 정교한 로직으로 교체 가능
     */
    private double getCriticalMultiplier(@NotNull Player attacker) {
        // 기본 크리티컬 배율
        double baseCritMultiplier = 1.5;
        
        // 플레이어의 스탯이나 장비에 따라 크리티컬 배율 조정 가능
        // RPGPlayer rpgPlayer = plugin.getRPGPlayerManager().getOrCreatePlayer(attacker);
        // return baseCritMultiplier + (rpgPlayer.getStats().getTotalStat(Stat.STRENGTH) * 0.01);
        
        return baseCritMultiplier;
    }
}