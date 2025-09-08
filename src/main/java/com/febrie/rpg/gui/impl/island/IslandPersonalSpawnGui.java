package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.GuiHandlerUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.SoundUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * 개인 스폰 관리 GUI
 * 섬원들이 자신의 개인 스폰 위치를 설정할 수 있음
 *
 * @author Febrie, CoffeeTory
 */
public class IslandPersonalSpawnGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final IslandDTO island;
    private final String playerUuid;
    private final boolean isOwner;
    private final boolean isMember;
    private IslandPersonalSpawnGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                                  @NotNull RPGMain plugin, @NotNull IslandDTO island) {
        super(viewer, guiManager, 54, LangManager.text(LangKey.GUI_ISLAND_PERSONAL_SPAWN_TITLE, viewer.locale()));
        this.islandManager = plugin.getIslandManager();
        this.island = island;
        
        this.playerUuid = viewer.getUniqueId().toString();
        this.isOwner = island.core().ownerUuid().equals(playerUuid);
        this.isMember = island.membership().members().stream()
                .anyMatch(m -> m.uuid().equals(playerUuid));
    }
    /**
     * Factory method to create and open the personal spawn GUI
     */
    public static IslandPersonalSpawnGui create(@NotNull RPGMain plugin, @NotNull Player viewer, 
                                               @NotNull IslandDTO island) {
        return new IslandPersonalSpawnGui(viewer, plugin.getGuiManager(), plugin, island);
    }
    
    @Override
    protected void setupLayout() {
        fillBorder(Material.CYAN_STAINED_GLASS_PANE);
        if (!isOwner && !isMember) {
            // 권한 없음
            setItem(22, createNoPermissionItem());
        } else {
            // 현재 개인 스폰 정보
            setItem(13, createCurrentPersonalSpawnInfo());
            
            // 개인 스폰 설정 옵션
            setItem(20, createSetPersonalSpawnItem());
            setItem(22, createTeleportToPersonalSpawnItem());
            setItem(24, createRemovePersonalSpawnItem());
            if (isOwner) {
                // 섬장 전용: 섬원들의 개인 스폰 관리
                setItem(31, createManageMemberSpawnsItem());
            }
        }
        // 뒤로가기
        setItem(49, createBackButton());
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return null; // Use back button with direct navigation
    }
    
    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(LangKey.GUI_ISLAND_PERSONAL_SPAWN_TITLE, viewer.locale());
    }
    
    private GuiItem createCurrentPersonalSpawnInfo() {
        IslandSpawnDTO spawnData = island.configuration().spawnData();
        // 개인 스폰 확인
        IslandSpawnPointDTO personalSpawn = null;
        if (isOwner && !spawnData.ownerSpawns().isEmpty()) {
            personalSpawn = spawnData.ownerSpawns().get(0);
        } else if (isMember && spawnData.memberSpawns().containsKey(playerUuid)) {
            personalSpawn = spawnData.memberSpawns().get(playerUuid);
        }
        ItemBuilder builder = ItemBuilder.of(Material.ENDER_EYE)
                .displayName(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_INFO_NAME, viewer.locale()));
        
        if (personalSpawn != null) {
            String location = String.format("%.1f, %.1f, %.1f", 
                personalSpawn.x(), personalSpawn.y(), personalSpawn.z());
            builder.addLore(LangManager.list(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_INFO_LORE, viewer.locale(), location));
        } else {
            builder.addLore(LangManager.list(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_INFO_NOT_SET_LORE, viewer.locale()));
        }
        
        return new GuiItem(builder.build());
    }
    
    private GuiItem createSetPersonalSpawnItem() {
        return GuiItem.clickable(
            ItemBuilder.of(Material.BEACON)
                .displayName(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_SET_NAME, viewer.locale()))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_SET_LORE1, viewer.locale()))
                .addLore(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_SET_LORE2, viewer.locale()))
                .addLore(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_SET_CLICK, viewer.locale()))
                .hideAllFlags()
                .build(),
            player -> {
                handleSetPersonalSpawn(player);
                SoundUtil.playClickSound(player);
            }
        );
    }
    
    private GuiItem createTeleportToPersonalSpawnItem() {
        IslandSpawnDTO spawnData = island.configuration().spawnData();
        boolean hasPersonalSpawn = isOwner ? 
            !spawnData.ownerSpawns().isEmpty() :
            spawnData.memberSpawns().containsKey(playerUuid);
        return GuiItem.clickable(
            ItemBuilder.of(Material.ENDER_PEARL)
                .displayName(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_TELEPORT_NAME, viewer.locale()))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_TELEPORT_LORE1, viewer.locale()))
                .addLore(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_TELEPORT_LORE2, viewer.locale()))
                .addLore(hasPersonalSpawn ?
                    LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_TELEPORT_CLICK, viewer.locale()).color(UnifiedColorUtil.YELLOW) :
                    LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_TELEPORT_NO_SPAWN, viewer.locale()).color(UnifiedColorUtil.RED))
                .hideAllFlags()
                .build(),
            player -> {
                if (hasPersonalSpawn) {
                    handleTeleportToPersonalSpawn(player);
                } else {
                    player.sendMessage(UnifiedColorUtil.parse("&c개인 스폰이 설정되지 않았습니다."));
                }
                SoundUtil.playClickSound(player);
            }
        );
    }
    
    private GuiItem createRemovePersonalSpawnItem() {
        return GuiItem.clickable(
            ItemBuilder.of(Material.BARRIER)
                .displayName(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_REMOVE_NAME, viewer.locale()))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_REMOVE_LORE1, viewer.locale()))
                .addLore(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_REMOVE_LORE2, viewer.locale()))
                .addLore(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_REMOVE_CLICK, viewer.locale()))
                .hideAllFlags()
                .build(),
            player -> {
                handleRemovePersonalSpawn(player);
                SoundUtil.playClickSound(player);
            }
        );
    }
    
    private GuiItem createManageMemberSpawnsItem() {
        int memberCount = island.membership().members().size();
        int spawnsSet = (int) island.membership().members().stream()
                .filter(m -> island.configuration().spawnData().memberSpawns().containsKey(m.uuid()))
                .count();
        return GuiItem.clickable(
            ItemBuilder.of(Material.COMMAND_BLOCK)
                .displayName(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_MANAGE_NAME, viewer.locale()))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_MANAGE_LORE1, viewer.locale()))
                .addLore(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_MANAGE_LORE2, viewer.locale()))
                .addLore(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_MANAGE_STATUS, getViewerLocale(), Component.text(spawnsSet + "/" + memberCount)))
                .addLore(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_MANAGE_CLICK, viewer.locale()))
                .hideAllFlags()
                .build(),
            player -> {
                player.sendMessage(UnifiedColorUtil.parse("&c이 기능은 아직 구현되지 않았습니다."));
                SoundUtil.playClickSound(player);
            }
        );
    }
    
    private GuiItem createNoPermissionItem() {
        return new GuiItem(
            ItemBuilder.of(Material.REDSTONE_BLOCK)
                .displayName(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_NO_PERMISSION_NAME, viewer.locale()))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_NO_PERMISSION_LORE1, viewer.locale()))
                .addLore(LangManager.text(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_NO_PERMISSION_LORE2, viewer.locale()))
                .hideAllFlags()
                .build()
        );
    }
    
    private GuiItem createBackButton() {
        return GuiItem.clickable(
            ItemBuilder.of(Material.ARROW)
                .displayName(LangManager.text(LangKey.GUI_COMMON_BACK, viewer.locale()))
                .addLore(LangManager.list(LangKey.ITEMS_ISLAND_PERSONAL_SPAWN_BACK_LORE, viewer.locale()))
                .hideAllFlags()
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
            player.sendMessage(UnifiedColorUtil.parse("&c섬 월드에서만 스폰을 설정할 수 있습니다."));
            return;
        }
        String playerUuid = player.getUniqueId().toString();
        // 개인 스폰 생성
        IslandSpawnPointDTO personalSpawn = new IslandSpawnPointDTO(
            loc.getX(), loc.getY(), loc.getZ(),
            loc.getYaw(), loc.getPitch(),
            "개인 스폰"
        );
        // 스폰 데이터 업데이트
        IslandSpawnDTO currentSpawn = island.configuration().spawnData();
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
        player.sendMessage(UnifiedColorUtil.parse("&a개인 스폰이 설정되었습니다!"));
        refresh();
    }
    
    private void handleTeleportToPersonalSpawn(Player player) {
        IslandSpawnDTO spawnData = island.configuration().spawnData();
        IslandSpawnPointDTO personalSpawn = null;
        
        if (isOwner && !spawnData.ownerSpawns().isEmpty()) {
            personalSpawn = spawnData.ownerSpawns().get(0);
        } else if (isMember && spawnData.memberSpawns().containsKey(playerUuid)) {
            personalSpawn = spawnData.memberSpawns().get(playerUuid);
        }
        
        if (personalSpawn == null) {
            player.sendMessage(UnifiedColorUtil.parse("&c개인 스폰이 설정되지 않았습니다."));
            return;
        }
        
        World world = plugin.getServer().getWorld("island_" + island.core().islandId());
        if (world == null) {
            player.sendMessage(UnifiedColorUtil.parse("&c스폰 월드를 찾을 수 없습니다."));
            return;
        }
        
        Location spawnLoc = new Location(world,
            personalSpawn.x(), personalSpawn.y(), personalSpawn.z(),
            personalSpawn.yaw(), personalSpawn.pitch()
        );
        player.teleport(spawnLoc);
        player.sendMessage(UnifiedColorUtil.parse("&a개인 스폰으로 이동했습니다!"));
    }
    
    private void handleRemovePersonalSpawn(Player player) {
        IslandSpawnDTO currentSpawn = island.configuration().spawnData();
        IslandSpawnDTO newSpawn;
        
        if (isOwner) {
            // 섬장 스폰 제거
            newSpawn = new IslandSpawnDTO(
                currentSpawn.defaultSpawn(),
                currentSpawn.visitorSpawn(),
                new ArrayList<>(), // 빈 리스트
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
        player.sendMessage(UnifiedColorUtil.parse("&e개인 스폰이 제거되었습니다."));
        refresh();
    }
}
