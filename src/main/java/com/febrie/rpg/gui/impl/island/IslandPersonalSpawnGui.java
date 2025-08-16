package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.gui.BaseGui;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.GuiHandlerUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.SoundUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 개인 스폰 관리 GUI
 * 섬원들이 자신의 개인 스폰 위치를 설정할 수 있음
 *
 * @author Febrie, CoffeeTory
 */
public class IslandPersonalSpawnGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final Player viewer;
    private final IslandDTO island;
    private final boolean isOwner;
    private final boolean isMember;
    
    private IslandPersonalSpawnGui(@NotNull RPGMain plugin, @NotNull Player viewer, 
                                  @NotNull IslandDTO island) {
        super(plugin, 54);
        this.islandManager = plugin.getIslandManager();
        this.viewer = viewer;
        this.island = island;
        
        String playerUuid = viewer.getUniqueId().toString();
        this.isOwner = island.ownerUuid().equals(playerUuid);
        this.isMember = island.members().stream()
                .anyMatch(m -> m.uuid().equals(playerUuid));
    }
    
    /**
     * Factory method to create and open the personal spawn GUI
     */
    public static IslandPersonalSpawnGui create(@NotNull RPGMain plugin, @NotNull Player viewer, 
                                               @NotNull IslandDTO island) {
        IslandPersonalSpawnGui gui = new IslandPersonalSpawnGui(plugin, viewer, island);
        return BaseGui.create(gui, Component.text("개인 스폰 관리", ColorUtil.YELLOW));
    }
    
    @Override
    protected void setupItems() {
        fillBorder(Material.CYAN_STAINED_GLASS_PANE);
        
        if (!isOwner && !isMember) {
            // 권한 없음
            setItem(22, createNoPermissionItem().getItemStack());
        } else {
            // 현재 개인 스폰 정보
            setItem(13, createCurrentPersonalSpawnInfo().getItemStack());
            
            // 개인 스폰 설정 옵션
            setItem(20, createSetPersonalSpawnItem().getItemStack());
            setItem(22, createTeleportToPersonalSpawnItem().getItemStack());
            setItem(24, createRemovePersonalSpawnItem().getItemStack());
            
            if (isOwner) {
                // 섬장 전용: 섬원들의 개인 스폰 관리
                setItem(31, createManageMemberSpawnsItem().getItemStack());
            }
        }
        
        // 뒤로가기
        setItem(49, createBackButton().getItemStack());
    }
    
    private GuiItem createCurrentPersonalSpawnInfo() {
        String playerUuid = viewer.getUniqueId().toString();
        IslandSpawnDTO spawnData = island.spawnData();
        
        // 개인 스폰 확인
        IslandSpawnPointDTO personalSpawn = null;
        if (isOwner && !spawnData.ownerSpawns().isEmpty()) {
            personalSpawn = spawnData.ownerSpawns().get(0);
        } else {
            personalSpawn = spawnData.memberSpawns().get(playerUuid);
        }
        
        ItemBuilder builder = new ItemBuilder(Material.ENDER_EYE)
                .displayName(Component.text("내 개인 스폰", ColorUtil.AQUA));
        
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        
        if (personalSpawn != null) {
            String location = String.format("%.1f, %.1f, %.1f", 
                personalSpawn.x(), personalSpawn.y(), personalSpawn.z());
            lore.add(Component.text("위치: " + location, ColorUtil.WHITE));
            if (personalSpawn.alias() != null && !personalSpawn.alias().isEmpty()) {
                lore.add(Component.text("이름: " + personalSpawn.alias(), ColorUtil.GRAY));
            }
        } else {
            lore.add(Component.text("설정되지 않음", ColorUtil.RED));
            lore.add(Component.text("기본 스폰을 사용합니다", ColorUtil.GRAY));
        }
        
        lore.add(Component.empty());
        lore.add(Component.text("개인 스폰을 설정하면", ColorUtil.GRAY));
        lore.add(Component.text("/섬 워프 시 이곳으로 이동합니다", ColorUtil.GRAY));
        
        builder.lore(lore);
        return GuiItem.empty();
    }
    
    private GuiItem createSetPersonalSpawnItem() {
        return GuiItem.clickable(
            new ItemBuilder(Material.BEACON)
                .displayName(Component.text("개인 스폰 설정", ColorUtil.GREEN))
                .lore(Arrays.asList(
                    Component.empty(),
                    Component.text("현재 위치를", ColorUtil.GRAY),
                    Component.text("내 개인 스폰으로 설정합니다", ColorUtil.GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 설정", ColorUtil.YELLOW)
                ))
                .build(),
            player -> {
                handleSetPersonalSpawn(player);
                SoundUtil.playClickSound(player);
            }
        );
    }
    
    private GuiItem createTeleportToPersonalSpawnItem() {
        String playerUuid = viewer.getUniqueId().toString();
        IslandSpawnDTO spawnData = island.spawnData();
        
        boolean hasPersonalSpawn = isOwner ? 
            !spawnData.ownerSpawns().isEmpty() :
            spawnData.memberSpawns().containsKey(playerUuid);
        
        return GuiItem.clickable(
            new ItemBuilder(Material.ENDER_PEARL)
                .displayName(Component.text("개인 스폰으로 이동", ColorUtil.LIGHT_PURPLE))
                .lore(Arrays.asList(
                    Component.empty(),
                    Component.text("내 개인 스폰으로", ColorUtil.GRAY),
                    Component.text("즉시 이동합니다", ColorUtil.GRAY),
                    Component.empty(),
                    hasPersonalSpawn ?
                        Component.text("▶ 클릭하여 이동", ColorUtil.YELLOW) :
                        Component.text("✖ 개인 스폰이 없습니다", ColorUtil.RED)
                ))
                .build(),
            player -> {
                if (hasPersonalSpawn) {
                    handleTeleportToPersonalSpawn(player);
                } else {
                    player.sendMessage(ColorUtil.colorize("&c개인 스폰이 설정되지 않았습니다."));
                }
                SoundUtil.playClickSound(player);
            }
        );
    }
    
    private GuiItem createRemovePersonalSpawnItem() {
        return GuiItem.clickable(
            new ItemBuilder(Material.BARRIER)
                .displayName(Component.text("개인 스폰 제거", ColorUtil.RED))
                .lore(Arrays.asList(
                    Component.empty(),
                    Component.text("개인 스폰을 제거하고", ColorUtil.GRAY),
                    Component.text("기본 스폰을 사용합니다", ColorUtil.GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 제거", ColorUtil.YELLOW)
                ))
                .build(),
            player -> {
                handleRemovePersonalSpawn(player);
                SoundUtil.playClickSound(player);
            }
        );
    }
    
    private GuiItem createManageMemberSpawnsItem() {
        int memberCount = island.members().size();
        int spawnsSet = (int) island.members().stream()
                .filter(m -> island.spawnData().memberSpawns().containsKey(m.uuid()))
                .count();
        
        return GuiItem.clickable(
            new ItemBuilder(Material.COMMAND_BLOCK)
                .displayName(Component.text("섬원 스폰 관리", ColorUtil.GOLD))
                .lore(Arrays.asList(
                    Component.empty(),
                    Component.text("섬원들의 개인 스폰을", ColorUtil.GRAY),
                    Component.text("관리할 수 있습니다", ColorUtil.GRAY),
                    Component.empty(),
                    Component.text("설정된 스폰: " + spawnsSet + "/" + memberCount, ColorUtil.YELLOW),
                    Component.empty(),
                    Component.text("▶ 클릭하여 관리", ColorUtil.GREEN)
                ))
                .build(),
            player -> {
                player.sendMessage(ColorUtil.colorize("&c이 기능은 아직 구현되지 않았습니다."));
                SoundUtil.playClickSound(player);
            }
        );
    }
    
    private GuiItem createNoPermissionItem() {
        return GuiItem.empty();
    }
    
    private GuiItem createBackButton() {
        return GuiItem.clickable(
            new ItemBuilder(Material.ARROW)
                .displayName(Component.text("뒤로가기", ColorUtil.YELLOW))
                .lore(List.of(Component.text("스폰 설정으로 돌아갑니다", ColorUtil.GRAY)))
                .build(),
            player -> {
                player.closeInventory();
                IslandSpawnSettingsGui.create(plugin, viewer, island).open(viewer);
                SoundUtil.playClickSound(player);
            }
        );
    }
    
    private void handleSetPersonalSpawn(Player player) {
        Location loc = player.getLocation();
        
        // 섬 영역 내인지 확인
        if (!islandManager.getWorldManager().isIslandWorld(loc.getWorld())) {
            player.sendMessage(ColorUtil.colorize("&c섬 월드에서만 스폰을 설정할 수 있습니다."));
            return;
        }
        
        String playerUuid = player.getUniqueId().toString();
        
        // 개인 스폰 생성
        IslandSpawnPointDTO personalSpawn = new IslandSpawnPointDTO(
            loc.getX(), loc.getY(), loc.getZ(),
            loc.getYaw(), loc.getPitch(),
            loc.getWorld().getName()
        );
        
        // 스폰 데이터 업데이트
        IslandSpawnDTO currentSpawn = island.spawnData();
        IslandSpawnDTO newSpawn;
        
        if (isOwner) {
            // 섬장은 ownerSpawns 리스트 사용
            List<IslandSpawnPointDTO> ownerSpawns = new ArrayList<>(currentSpawn.ownerSpawns());
            if (ownerSpawns.isEmpty()) {
                ownerSpawns.add(personalSpawn);
            } else {
                ownerSpawns.set(0, personalSpawn); // 첫 번째 스폰 교체
            }
            
            newSpawn = new IslandSpawnDTO(
                currentSpawn.defaultSpawn(),
                currentSpawn.visitorSpawn(),
                ownerSpawns,
                currentSpawn.memberSpawns()
            );
        } else {
            // 일반 멤버는 memberSpawns 맵 사용
            Map<String, IslandSpawnPointDTO> memberSpawns = new HashMap<>(currentSpawn.memberSpawns());
            memberSpawns.put(playerUuid, personalSpawn);
            
            newSpawn = new IslandSpawnDTO(
                currentSpawn.defaultSpawn(),
                currentSpawn.visitorSpawn(),
                currentSpawn.ownerSpawns(),
                memberSpawns
            );
        }
        
        // 섬 업데이트
        IslandDTO updated = GuiHandlerUtil.updateIslandSpawn(island, newSpawn);
        islandManager.updateIsland(updated);
        
        player.sendMessage(ColorUtil.colorize("&a개인 스폰이 설정되었습니다!"));
        refresh();
    }
    
    private void handleTeleportToPersonalSpawn(Player player) {
        String playerUuid = player.getUniqueId().toString();
        IslandSpawnDTO spawnData = island.spawnData();
        
        IslandSpawnPointDTO personalSpawn = null;
        if (isOwner && !spawnData.ownerSpawns().isEmpty()) {
            personalSpawn = spawnData.ownerSpawns().get(0);
        } else {
            personalSpawn = spawnData.memberSpawns().get(playerUuid);
        }
        
        if (personalSpawn == null) {
            player.sendMessage(ColorUtil.colorize("&c개인 스폰이 설정되지 않았습니다."));
            return;
        }
        
        World world = plugin.getServer().getWorld(island.islandName());
        if (world == null) {
            player.sendMessage(ColorUtil.colorize("&c스폰 월드를 찾을 수 없습니다."));
            return;
        }
        
        Location spawnLoc = new Location(world,
            personalSpawn.x(), personalSpawn.y(), personalSpawn.z(),
            personalSpawn.yaw(), personalSpawn.pitch()
        );
        
        player.teleport(spawnLoc);
        player.sendMessage(ColorUtil.colorize("&a개인 스폰으로 이동했습니다!"));
    }
    
    private void handleRemovePersonalSpawn(Player player) {
        String playerUuid = player.getUniqueId().toString();
        IslandSpawnDTO currentSpawn = island.spawnData();
        
        IslandSpawnDTO newSpawn;
        
        if (isOwner) {
            // 섬장 스폰 제거
            newSpawn = new IslandSpawnDTO(
                currentSpawn.defaultSpawn(),
                currentSpawn.visitorSpawn(),
                List.of(), // 빈 리스트
                currentSpawn.memberSpawns()
            );
        } else {
            // 멤버 스폰 제거
            Map<String, IslandSpawnPointDTO> memberSpawns = new HashMap<>(currentSpawn.memberSpawns());
            memberSpawns.remove(playerUuid);
            
            newSpawn = new IslandSpawnDTO(
                currentSpawn.defaultSpawn(),
                currentSpawn.visitorSpawn(),
                currentSpawn.ownerSpawns(),
                memberSpawns
            );
        }
        
        // 섬 업데이트
        IslandDTO updated = GuiHandlerUtil.updateIslandSpawn(island, newSpawn);
        islandManager.updateIsland(updated);
        
        player.sendMessage(ColorUtil.colorize("&e개인 스폰이 제거되었습니다."));
        refresh();
    }
    
    @Override
    protected void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}