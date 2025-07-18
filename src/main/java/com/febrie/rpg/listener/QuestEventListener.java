package com.febrie.rpg.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.quest.manager.QuestManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 퀘스트 시스템을 위한 이벤트 리스너
 * 다양한 이벤트를 처리하여 퀘스트 목표 진행을 체크
 * - PlayerInteractEntityEvent: InteractNPCObjective (NPC 상호작용)
 * 
 * 참고: VisitLocationObjective는 성능 최적화를 위해 LocationCheckTask에서 별도로 처리됨
 *
 * @author Febrie
 */
public class QuestEventListener implements Listener {

    private final RPGMain plugin;
    private final QuestManager questManager;

    public QuestEventListener(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.questManager = plugin.getQuestManager();
    }
    
    /**
     * 플레이어 엔티티 상호작용 이벤트 처리
     * InteractNPCObjective (NPC와 대화)를 위해 필요
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEntity(@NotNull PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        
        // QuestManager에 이벤트 전달
        questManager.progressObjective(event, player);
    }
}