package com.febrie.rpg.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.quest.event.CurrencyPaymentEvent;
import com.febrie.rpg.quest.event.PlayerLevelUpEvent;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.service.QuestProgressService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
/**
 * 퀘스트 시스템을 위한 이벤트 리스너
 * 다양한 이벤트를 처리하여 퀘스트 목표 진행을 체크
 * - NPC 상호작용은 NPCInteractListener에서 처리
 * - 위치 방문은 LocationCheckTask에서 처리
 *
 * @author Febrie
 */
public class QuestEventListener implements Listener {

    private final RPGMain plugin;
    private final QuestManager questManager;
    private final QuestProgressService progressService;

    public QuestEventListener(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.questManager = plugin.getQuestManager();
        this.progressService = new QuestProgressService(plugin);
    }
    
    /**
     * 엔티티 처치 이벤트 - KillMob, KillPlayer
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            progressService.handleEntityDeath(event, killer);
        }
    }
    
    /**
     * 블록 파괴 이벤트 - BreakBlock, Harvest
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        progressService.handleBlockBreak(event);
    }
    
    /**
     * 블록 설치 이벤트 - PlaceBlock
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        progressService.handleBlockPlace(event);
    }
    
    /**
     * 아이템 줍기 이벤트 - CollectItem
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        progressService.handleItemPickup(event);
    }
    
    /**
     * 아이템 제작 이벤트 - CraftItem
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        progressService.handleCraftItem(event);
    }
    
    /**
     * 낚시 이벤트 - Fishing
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            progressService.handleFishing(event);
        }
    }
    
    /**
     * 화폐 지불 이벤트 - PayCurrency (커스텀 이벤트)
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCurrencyPayment(CurrencyPaymentEvent event) {
        // progressService.handleCurrencyPayment(event);
    }
    
    /**
     * 레벨업 이벤트 - ReachLevel (커스텀 이벤트)
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLevelUp(PlayerLevelUpEvent event) {
        // progressService.handleLevelUp(event);
    }
    
    /**
     * QuestProgressService 반환 (다른 클래스에서 필요한 경우)
     */
    public QuestProgressService getProgressService() {
        return progressService;
    }
}