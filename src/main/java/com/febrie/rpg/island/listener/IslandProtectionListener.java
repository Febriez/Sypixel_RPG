package com.febrie.rpg.island.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.ColorUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            player.sendMessage(ColorUtil.colorize("&c이 섬에서 블록을 설치할 권한이 없습니다."));
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
            player.sendMessage(ColorUtil.colorize("&c이 섬에서 블록을 파괴할 권한이 없습니다."));
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
                player.sendMessage(ColorUtil.colorize("&c이 섬에서 상자를 열 권한이 없습니다."));
                return;
            }
        }
        
        // 상호작용 가능한 블록들
        if (isInteractableBlock(type)) {
            if (!checkPermission(player, block.getLocation(), "USE_ITEMS")) {
                event.setCancelled(true);
                player.sendMessage(ColorUtil.colorize("&c이 섬에서 이 아이템을 사용할 권한이 없습니다."));
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
            player.sendMessage(ColorUtil.colorize("&c이 섬에서 양동이를 사용할 권한이 없습니다."));
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
            player.sendMessage(ColorUtil.colorize("&c이 섬에서 양동이를 사용할 권한이 없습니다."));
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
                player.sendMessage(ColorUtil.colorize("&c이 섬에서 이 아이템을 사용할 권한이 없습니다."));
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
        IslandDTO island = islandManager.getIslandAt(location);
        if (island == null) {
            // 섬이 없는 지역은 보호
            return false;
        }
        
        // 권한 확인
        return IslandPermissionHandler.hasPermission(island, player, permission);
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
     * 방문 추적 - 플레이어가 섬에 들어올 때
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // 큰 움직임만 체크 (블록 변경)
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        
        // 섬 월드가 아니면 무시
        if (!islandManager.getWorldManager().isIslandWorld(to.getWorld())) {
            return;
        }
        
        IslandDTO fromIsland = islandManager.getIslandAt(from);
        IslandDTO toIsland = islandManager.getIslandAt(to);
        
        // 다른 섬으로 이동했을 때
        if (fromIsland != toIsland) {
            if (toIsland != null) {
                // 새로운 섬에 입장
                handleIslandEntry(player, toIsland);
            } else if (fromIsland != null) {
                // 섬에서 나감
                handleIslandExit(player, fromIsland);
            }
        }
    }
    
    /**
     * 섬 입장 처리
     */
    private void handleIslandEntry(@NotNull Player player, @NotNull IslandDTO island) {
        String playerUuid = player.getUniqueId().toString();
        
        // 자기 섬이면 환영 메시지
        if (island.ownerUuid().equals(playerUuid)) {
            player.sendActionBar(ColorUtil.colorize("&a당신의 섬에 오신 것을 환영합니다!"));
        } else if (IslandPermissionHandler.isMember(island, player)) {
            player.sendActionBar(ColorUtil.colorize("&b" + island.islandName() + " &f섬에 오신 것을 환영합니다!"));
        } else {
            // 방문자
            player.sendActionBar(ColorUtil.colorize("&e" + island.islandName() + " &f섬을 방문중입니다."));
            
            // 비공개 섬이면 경고
            if (!island.isPublic()) {
                player.sendMessage(ColorUtil.colorize("&c주의: 이 섬은 비공개 섬입니다. 권한이 없으면 추방될 수 있습니다."));
            }
        }
        
        // TODO: 방문 기록 추가
    }
    
    /**
     * 섬 퇴장 처리
     */
    private void handleIslandExit(@NotNull Player player, @NotNull IslandDTO island) {
        // TODO: 방문 시간 계산 및 저장
    }
}