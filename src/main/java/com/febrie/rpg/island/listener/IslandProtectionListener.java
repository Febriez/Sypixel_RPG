package com.febrie.rpg.island.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.island.Island;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.UnifiedColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;

/**
 * 섬 보호 리스너
 * 권한에 따른 섬 내 행동 제한
 *
 * @author Febrie, CoffeeTory
 */
public class IslandProtectionListener implements Listener {
    
    private final RPGMain plugin;
    private final IslandManager islandManager;
    
    public IslandProtectionListener(@NotNull RPGMain plugin, @NotNull IslandManager islandManager) {
        this.plugin = plugin;
        this.islandManager = islandManager;
    }
    
    /**
     * 블록 설치 이벤트
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (!checkPermission(player, block.getLocation(), "BUILD")) {
            event.setCancelled(true);
            player.sendMessage(UnifiedColorUtil.parse("&c이 섬에서 블록을 설치할 권한이 없습니다."));
        }
    }
    
    /**
     * 블록 파괴 이벤트
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (!checkPermission(player, block.getLocation(), "BUILD")) {
            event.setCancelled(true);
            player.sendMessage(UnifiedColorUtil.parse("&c이 섬에서 블록을 파괴할 권한이 없습니다."));
        }
    }
    
    /**
     * 블록 상호작용 이벤트
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) return;
        
        Material type = block.getType();
        
        // 컨테이너 확인
        if (block.getState() instanceof Container) {
            if (!checkPermission(player, block.getLocation(), "OPEN_CONTAINERS")) {
                event.setCancelled(true);
                player.sendMessage(UnifiedColorUtil.parse("&c이 섬에서 상자를 열 권한이 없습니다."));
                return;
            }
        }
        
        // 상호작용 가능한 블록들
        if (isInteractableBlock(type)) {
            if (!checkPermission(player, block.getLocation(), "USE_ITEMS")) {
                event.setCancelled(true);
                player.sendMessage(UnifiedColorUtil.parse("&c이 섬에서 이 아이템을 사용할 권한이 없습니다."));
            }
        }
    }
    
    /**
     * 양동이 채우기 이벤트
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (!checkPermission(player, block.getLocation(), "USE_ITEMS")) {
            event.setCancelled(true);
            player.sendMessage(UnifiedColorUtil.parse("&c이 섬에서 양동이를 사용할 권한이 없습니다."));
        }
    }
    
    /**
     * 양동이 비우기 이벤트
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (!checkPermission(player, block.getLocation(), "USE_ITEMS")) {
            event.setCancelled(true);
            player.sendMessage(UnifiedColorUtil.parse("&c이 섬에서 양동이를 사용할 권한이 없습니다."));
        }
    }
    
    /**
     * 아이템 프레임 상호작용
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Location location = event.getRightClicked().getLocation();
        
        // 아이템 프레임, 그림 등
        if (event.getRightClicked().getType().name().contains("FRAME") || 
            event.getRightClicked().getType().name().contains("PAINTING")) {
            if (!checkPermission(player, location, "USE_ITEMS")) {
                event.setCancelled(true);
                player.sendMessage(UnifiedColorUtil.parse("&c이 섬에서 이 아이템을 사용할 권한이 없습니다."));
            }
        }
    }
    
    /**
     * 아이템 줍기 방지
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        Location location = event.getItem().getLocation();
        
        // 섬 월드가 아니면 허용
        if (!islandManager.getWorldManager().isIslandWorld(location.getWorld())) {
            return;
        }
        
        // 해당 위치의 섬 찾기
        Island island = islandManager.getIslandAt(location);
        if (island == null) {
            // 섬이 없는 지역에서는 줍기 방지
            event.setCancelled(true);
            return;
        }
        
        // 권한 확인 - 기본적으로 섬원만 아이템을 줍을 수 있음
        if (!IslandPermissionHandler.isMember(island.getData(), player) && !player.isOp()) {
            event.setCancelled(true);
            player.sendActionBar(Component.text("이 섬에서 아이템을 줍을 수 없습니다.", UnifiedColorUtil.ERROR));
        }
    }
    
    /**
     * 아이템 버리기 방지
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        
        // 섬 월드가 아니면 허용
        if (!islandManager.getWorldManager().isIslandWorld(location.getWorld())) {
            return;
        }
        
        // 해당 위치의 섬 찾기
        Island island = islandManager.getIslandAt(location);
        if (island == null) {
            // 섬이 없는 지역에서는 버리기 방지
            event.setCancelled(true);
            player.sendMessage(UnifiedColorUtil.parse("&c이 지역에서는 아이템을 버릴 수 없습니다."));
            return;
        }
        
        // 권한 확인 - 기본적으로 섬원만 아이템을 버릴 수 있음
        if (!IslandPermissionHandler.isMember(island.getData(), player) && !player.isOp()) {
            event.setCancelled(true);
            player.sendMessage(UnifiedColorUtil.parse("&c이 섬에서 아이템을 버릴 수 없습니다."));
        }
    }
    
    /**
     * 액자, 그림 등 걸린 엔티티 파괴 방지
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player player)) {
            return;
        }
        
        Location location = event.getEntity().getLocation();
        
        if (!checkPermission(player, location, "BUILD")) {
            event.setCancelled(true);
            player.sendMessage(UnifiedColorUtil.parse("&c이 섬에서 이 아이템을 파괴할 권한이 없습니다."));
        }
    }
    
    /**
     * 엔티티 손상 방지 (아이템 프레임 등)
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }
        
        // 아이템 프레임, 그림 등에 대한 손상 방지
        if (event.getEntityType() == EntityType.ITEM_FRAME ||
            event.getEntityType() == EntityType.GLOW_ITEM_FRAME ||
            event.getEntityType() == EntityType.PAINTING ||
            event.getEntityType() == EntityType.ARMOR_STAND) {
            
            Location location = event.getEntity().getLocation();
            
            if (!checkPermission(player, location, "BUILD")) {
                event.setCancelled(true);
                player.sendMessage(UnifiedColorUtil.parse("&c이 섬에서 이 엔티티를 파괴할 권한이 없습니다."));
            }
        }
    }
    
    /**
     * 권한 확인
     */
    private boolean checkPermission(@NotNull Player player, @NotNull Location location, @NotNull String permission) {
        // OP는 모든 권한 보유
        if (player.isOp()) {
            return true;
        }
        
        // 섬 월드가 아니면 허용
        if (!islandManager.getWorldManager().isIslandWorld(location.getWorld())) {
            return true;
        }
        
        // 해당 위치의 섬 찾기
        Island island = islandManager.getIslandAt(location);
        if (island == null) {
            // 섬이 없는 지역은 보호
            return false;
        }
        
        // 권한 확인
        return IslandPermissionHandler.hasPermission(island.getData(), player, permission);
    }
    
    /**
     * 상호작용 가능한 블록인지 확인
     */
    private boolean isInteractableBlock(@NotNull Material material) {
        String name = material.name();
        return name.contains("DOOR") || 
               name.contains("GATE") || 
               name.contains("BUTTON") || 
               name.contains("LEVER") || 
               name.contains("PLATE") || // Pressure plates
               name.contains("TRAPDOOR") ||
               name.contains("SIGN") ||
               name.contains("BED") ||
               name.contains("ANVIL") ||
               name.contains("BEACON") ||
               name.contains("BREWING") ||
               name.contains("CAULDRON") ||
               name.contains("COMPARATOR") ||
               name.contains("DAYLIGHT") ||
               name.contains("DIODE") || // Repeater
               name.contains("DISPENSER") ||
               name.contains("DROPPER") ||
               name.contains("ENCHANT") ||
               name.contains("FENCE") ||
               name.contains("FURNACE") ||
               name.contains("HOPPER") ||
               name.contains("JUKEBOX") ||
               name.contains("NOTE_BLOCK") ||
               name.contains("REDSTONE") ||
               name.contains("REPEATER") ||
               name.contains("WORKBENCH") ||
               name.contains("CRAFTING") ||
               material == Material.BEACON ||
               material == Material.BELL ||
               material == Material.CAMPFIRE ||
               material == Material.SOUL_CAMPFIRE ||
               material == Material.CARTOGRAPHY_TABLE ||
               material == Material.COMPOSTER ||
               material == Material.GRINDSTONE ||
               material == Material.LECTERN ||
               material == Material.LOOM ||
               material == Material.SMITHING_TABLE ||
               material == Material.SMOKER ||
               material == Material.STONECUTTER;
    }
    
    
    /**
     * 섬 입장 처리
     */
    public void handleIslandEntry(@NotNull Player player, @NotNull IslandDTO island) {
        String playerUuid = player.getUniqueId().toString();
        
        // 자기 섬이면 환영 메시지
        if (island.core().ownerUuid().equals(playerUuid)) {
            player.sendActionBar(Component.text("당신의 섬에 오신 것을 환영합니다!", UnifiedColorUtil.GREEN));
        } else if (IslandPermissionHandler.isMember(island, player)) {
            player.sendActionBar(Component.text(island.core().islandName(), UnifiedColorUtil.AQUA)
                    .append(Component.text(" 섬에 오신 것을 환영합니다!", UnifiedColorUtil.WHITE)));
        } else {
            // 방문자
            player.sendActionBar(Component.text(island.core().islandName(), UnifiedColorUtil.YELLOW)
                    .append(Component.text(" 섬을 방문중입니다.", UnifiedColorUtil.WHITE)));
            
            // 비공개 섬이면 경고
            if (!island.core().isPublic()) {
                player.sendMessage(UnifiedColorUtil.parse("&c주의: 이 섬은 비공개 섬입니다. 권한이 없으면 추방될 수 있습니다."));
            }
        }
        
        // 방문 기록 추가
        addVisitRecord(player, island);
    }
    
    /**
     * 섬 퇴장 처리
     */
    public void handleIslandExit(@NotNull Player player, @NotNull IslandDTO island) {
        // 방문 종료 메시지
        player.sendActionBar(Component.text(island.core().islandName(), UnifiedColorUtil.YELLOW)
                .append(Component.text(" 섬에서 나가십니다.", UnifiedColorUtil.GRAY)));
    }
    
    /**
     * 방문 기록 추가
     */
    private void addVisitRecord(@NotNull Player player, @NotNull IslandDTO island) {
        String playerUuid = player.getUniqueId().toString();
        
        // 자신의 섬이면 기록하지 않음
        if (island.core().ownerUuid().equals(playerUuid)) {
            return;
        }
        
        // 멤버라면 기록하지 않음
        if (island.membership().members().stream().anyMatch(m -> m.uuid().equals(playerUuid))) {
            return;
        }
        
        // 새로운 방문 기록 생성
        IslandVisitDTO newVisit = IslandVisitDTO.startVisit(
                playerUuid,
                player.getName()
        );
        
        java.util.List<IslandVisitDTO> updatedVisits = new java.util.ArrayList<>(island.social().recentVisits());
        
        // 중복 방지 - 24시간 이내 동일한 방문자는 기록하지 않음
        long oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        boolean recentlyVisited = updatedVisits.stream()
                .anyMatch(v -> v.visitorUuid().equals(playerUuid) && v.visitedAt() > oneDayAgo);
        
        if (!recentlyVisited) {
            updatedVisits.add(0, newVisit); // 최신 방문을 앞에 추가
            
            // 최대 50개까지만 보관
            if (updatedVisits.size() > 50) {
                updatedVisits = updatedVisits.subList(0, 50);
            }
            
            // 섬 업데이트
            IslandCoreDTO updatedCore = new IslandCoreDTO(
                    island.core().islandId(), island.core().ownerUuid(), island.core().ownerName(),
                    island.core().islandName(), island.core().size(), island.core().isPublic(),
                    island.core().createdAt(), System.currentTimeMillis(),
                    island.core().totalResets(), island.core().deletionScheduledAt(),
                    island.core().location()
            );
            
            IslandSocialDTO updatedSocial = new IslandSocialDTO(
                    island.core().islandId(),
                    island.social().pendingInvites(),
                    updatedVisits
            );
            
            IslandDTO updated = new IslandDTO(updatedCore, island.membership(), updatedSocial, island.configuration());
            
            islandManager.updateIsland(updated);
        }
    }
}