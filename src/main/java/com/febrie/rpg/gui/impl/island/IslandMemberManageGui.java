package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandMemberDTO;
import com.febrie.rpg.dto.island.IslandWorkerDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 섬 멤버 관리 GUI
 * 멤버의 역할 변경, 추방 등을 관리
 *
 * @author Febrie, CoffeeTory
 */
public class IslandMemberManageGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final Player viewer;
    private final IslandDTO island;
    private final String targetUuid;
    private final boolean isOwner;
    private final boolean isCoOwner;
    
    // 대상 멤버 정보
    private final String targetName;
    private final String currentRole;
    private final boolean targetIsCoOwner;
    private final boolean targetIsWorker;
    
    private IslandMemberManageGui(@NotNull RPGMain plugin, @NotNull Player viewer,
                                  @NotNull IslandDTO island, @NotNull String targetUuid) {
        super(plugin, 45); // 5줄 GUI
        this.islandManager = plugin.getIslandManager();
        this.viewer = viewer;
        this.island = island;
        this.targetUuid = targetUuid;
        this.isOwner = island.ownerUuid().equals(viewer.getUniqueId().toString());
        
        // 뷰어가 부섬장인지 확인
        this.isCoOwner = island.members().stream()
                .anyMatch(m -> m.uuid().equals(viewer.getUniqueId().toString()) && m.isCoOwner());
        
        // 대상 멤버 정보 찾기
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(UUID.fromString(targetUuid));
        this.targetName = targetPlayer.getName() != null ? targetPlayer.getName() : "Unknown";
        
        // 멤버 찾기
        Optional<IslandMemberDTO> member = island.members().stream()
                .filter(m -> m.uuid().equals(targetUuid))
                .findFirst();
        
        if (member.isPresent()) {
            this.targetIsCoOwner = member.get().isCoOwner();
            this.targetIsWorker = false;
            this.currentRole = targetIsCoOwner ? "부섬장" : "멤버";
        } else {
            // 알바생 찾기
            Optional<IslandWorkerDTO> worker = island.workers().stream()
                    .filter(w -> w.uuid().equals(targetUuid))
                    .findFirst();
            
            this.targetIsCoOwner = false;
            this.targetIsWorker = worker.isPresent();
            this.currentRole = targetIsWorker ? "알바생" : "Unknown";
        }
    }
    
    /**
     * Factory method to create and open the member management GUI
     */
    public static IslandMemberManageGui create(@NotNull RPGMain plugin, @NotNull Player viewer,
                                              @NotNull IslandDTO island, @NotNull String targetUuid) {
        IslandMemberManageGui gui = new IslandMemberManageGui(plugin, viewer, island, targetUuid);
        return BaseGui.create(gui, ColorUtil.parseComponent("&9&l멤버 관리: " + gui.targetName));
    }
    
    @Override
    protected void setupItems() {
        fillBorder(Material.BLUE_STAINED_GLASS_PANE);
        
        // 멤버 정보
        setItem(13, createMemberInfoItem());
        
        // 관리 옵션들
        if (canManageMember()) {
            if (!targetIsWorker) {
                // 멤버 역할 변경
                if (targetIsCoOwner) {
                    setItem(20, createDemoteItem()); // 부섬장 → 일반 멤버
                } else {
                    setItem(20, createPromoteItem()); // 일반 멤버 → 부섬장
                }
                
                setItem(22, createToWorkerItem()); // 알바생으로 변경
            } else {
                // 알바생 → 멤버로 승급
                setItem(21, createToMemberItem());
            }
            
            // 추방
            setItem(24, createKickItem());
            
            // 권한 설정
            if (!targetIsWorker) {
                setItem(30, createPermissionItem());
            }
        } else {
            // 권한 없음 안내
            setItem(22, createNoPermissionItem());
        }
        
        // 뒤로가기
        setItem(40, createBackButton());
    }
    
    private ItemStack createMemberInfoItem() {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .displayName(ColorUtil.parseComponent("&b&l" + targetName))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7현재 역할: &f" + currentRole))
                .addLore(ColorUtil.parseComponent("&7UUID: &f" + targetUuid.substring(0, 8) + "..."))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7이 플레이어의 역할과 권한을"))
                .addLore(ColorUtil.parseComponent("&7관리할 수 있습니다"))
                .build();
    }
    
    private ItemStack createPromoteItem() {
        return new ItemBuilder(Material.GOLDEN_HELMET)
                .displayName(ColorUtil.parseComponent("&6&l부섬장으로 승급"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7이 멤버를 부섬장으로"))
                .addLore(ColorUtil.parseComponent("&7승급시킵니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&e부섬장 권한:"))
                .addLore(ColorUtil.parseComponent("&7• 멤버 초대/추방"))
                .addLore(ColorUtil.parseComponent("&7• 섬 설정 변경"))
                .addLore(ColorUtil.parseComponent("&7• 권한 관리"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&e▶ 클릭하여 승급"))
                .build();
    }
    
    private ItemStack createDemoteItem() {
        return new ItemBuilder(Material.IRON_HELMET)
                .displayName(ColorUtil.parseComponent("&7&l일반 멤버로 강등"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7부섬장을 일반 멤버로"))
                .addLore(ColorUtil.parseComponent("&7강등시킵니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&c주의: 부섬장 권한을 잃습니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&e▶ 클릭하여 강등"))
                .build();
    }
    
    private ItemStack createToWorkerItem() {
        return new ItemBuilder(Material.LEATHER_HELMET)
                .displayName(ColorUtil.parseComponent("&e&l알바생으로 변경"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7멤버를 알바생으로"))
                .addLore(ColorUtil.parseComponent("&7변경합니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&e알바생 특징:"))
                .addLore(ColorUtil.parseComponent("&7• 제한된 권한"))
                .addLore(ColorUtil.parseComponent("&7• 임시 멤버"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&e▶ 클릭하여 변경"))
                .build();
    }
    
    private ItemStack createToMemberItem() {
        return new ItemBuilder(Material.IRON_HELMET)
                .displayName(ColorUtil.parseComponent("&a&l정식 멤버로 승급"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7알바생을 정식 멤버로"))
                .addLore(ColorUtil.parseComponent("&7승급시킵니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&a정식 멤버 혜택:"))
                .addLore(ColorUtil.parseComponent("&7• 모든 기본 권한"))
                .addLore(ColorUtil.parseComponent("&7• 섬 기여도 추적"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&e▶ 클릭하여 승급"))
                .build();
    }
    
    private ItemStack createKickItem() {
        return new ItemBuilder(Material.BARRIER)
                .displayName(ColorUtil.parseComponent("&c&l섬에서 추방"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7이 플레이어를 섬에서"))
                .addLore(ColorUtil.parseComponent("&7추방합니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&c주의: 이 작업은 되돌릴 수 없습니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&c▶ 클릭하여 추방"))
                .build();
    }
    
    private ItemStack createPermissionItem() {
        return new ItemBuilder(Material.COMMAND_BLOCK)
                .displayName(ColorUtil.parseComponent("&b&l권한 설정"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7이 멤버의 개별 권한을"))
                .addLore(ColorUtil.parseComponent("&7설정합니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&c※ 준비 중인 기능입니다"))
                .build();
    }
    
    private ItemStack createNoPermissionItem() {
        return new ItemBuilder(Material.REDSTONE_BLOCK)
                .displayName(ColorUtil.parseComponent("&c&l권한 없음"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7이 멤버를 관리할"))
                .addLore(ColorUtil.parseComponent("&7권한이 없습니다"))
                .build();
    }
    
    private ItemStack createBackButton() {
        return new ItemBuilder(Material.ARROW)
                .displayName(ColorUtil.parseComponent("&c뒤로가기"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7멤버 목록으로 돌아갑니다"))
                .build();
    }
    
    @Override
    protected void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        if (!canManageMember()) {
            player.sendMessage(ColorUtil.colorize("&c이 멤버를 관리할 권한이 없습니다."));
            return;
        }
        
        int slot = event.getSlot();
        
        switch (slot) {
            case 20 -> {
                if (!targetIsWorker) {
                    if (targetIsCoOwner) {
                        handleDemote(player);
                    } else {
                        handlePromote(player);
                    }
                }
            }
            case 21 -> {
                if (targetIsWorker) {
                    handleWorkerToMember(player);
                }
            }
            case 22 -> {
                if (!targetIsWorker) {
                    handleMemberToWorker(player);
                }
            }
            case 24 -> handleKick(player);
            case 30 -> player.sendMessage(ColorUtil.colorize("&c개별 권한 설정은 아직 구현되지 않았습니다."));
            case 40 -> {
                player.closeInventory();
                IslandMemberGui.create(plugin.getGuiManager(), viewer, island).open(viewer);
            }
        }
    }
    
    private boolean canManageMember() {
        // 섬장은 모든 멤버 관리 가능
        if (isOwner) return true;
        
        // 부섬장은 일반 멤버와 알바생만 관리 가능
        if (isCoOwner && !targetIsCoOwner) return true;
        
        return false;
    }
    
    private void handlePromote(Player player) {
        List<IslandMemberDTO> updatedMembers = island.members().stream()
                .map(m -> {
                    if (m.uuid().equals(targetUuid)) {
                        return new IslandMemberDTO(
                                m.uuid(), m.name(), true, // 부섬장으로 설정
                                m.joinedAt(), m.lastActivity(),
                                m.personalSpawn()
                        );
                    }
                    return m;
                })
                .collect(Collectors.toList());
        
        updateIslandMembers(updatedMembers, island.workers());
        
        player.sendMessage(ColorUtil.colorize("&a" + targetName + "님을 부섬장으로 승급시켰습니다!"));
        refresh();
    }
    
    private void handleDemote(Player player) {
        List<IslandMemberDTO> updatedMembers = island.members().stream()
                .map(m -> {
                    if (m.uuid().equals(targetUuid)) {
                        return new IslandMemberDTO(
                                m.uuid(), m.name(), false, // 일반 멤버로 설정
                                m.joinedAt(), m.lastActivity(),
                                m.personalSpawn()
                        );
                    }
                    return m;
                })
                .collect(Collectors.toList());
        
        updateIslandMembers(updatedMembers, island.workers());
        
        player.sendMessage(ColorUtil.colorize("&e" + targetName + "님을 일반 멤버로 강등시켰습니다."));
        refresh();
    }
    
    private void handleMemberToWorker(Player player) {
        // 멤버에서 제거
        List<IslandMemberDTO> updatedMembers = island.members().stream()
                .filter(m -> !m.uuid().equals(targetUuid))
                .collect(Collectors.toList());
        
        // 알바생으로 추가
        List<IslandWorkerDTO> updatedWorkers = new ArrayList<>(island.workers());
        updatedWorkers.add(new IslandWorkerDTO(
                targetUuid, targetName,
                System.currentTimeMillis(),
                System.currentTimeMillis()
        ));
        
        updateIslandMembers(updatedMembers, updatedWorkers);
        
        player.sendMessage(ColorUtil.colorize("&e" + targetName + "님을 알바생으로 변경했습니다."));
        player.closeInventory();
        IslandMemberGui.create(plugin.getGuiManager(), viewer, island).open(viewer);
    }
    
    private void handleWorkerToMember(Player player) {
        // 알바생에서 제거
        List<IslandWorkerDTO> updatedWorkers = island.workers().stream()
                .filter(w -> !w.uuid().equals(targetUuid))
                .collect(Collectors.toList());
        
        // 정식 멤버로 추가
        List<IslandMemberDTO> updatedMembers = new ArrayList<>(island.members());
        updatedMembers.add(new IslandMemberDTO(
                targetUuid, targetName, false,
                System.currentTimeMillis(),
                0L, // 기여도
                null // 개인 스폰 null
        ));
        
        updateIslandMembers(updatedMembers, updatedWorkers);
        
        player.sendMessage(ColorUtil.colorize("&a" + targetName + "님을 정식 멤버로 승급시켰습니다!"));
        player.closeInventory();
        IslandMemberGui.create(plugin.getGuiManager(), viewer, island).open(viewer);
    }
    
    private void handleKick(Player player) {
        // 추방 확인
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    
                    String input = stateSnapshot.getText();
                    if (!"추방".equals(input)) {
                        player.sendMessage(ColorUtil.colorize("&c'추방'을 정확히 입력해야 합니다."));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                    
                    // 추방 실행
                    performKick(player);
                    
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .text("'추방' 입력")
                .title(targetName + "을(를) 추방하려면 '추방' 입력")
                .plugin(plugin)
                .open(player);
    }
    
    private void performKick(Player player) {
        // 멤버 또는 알바생에서 제거
        List<IslandMemberDTO> updatedMembers = island.members().stream()
                .filter(m -> !m.uuid().equals(targetUuid))
                .collect(Collectors.toList());
        
        List<IslandWorkerDTO> updatedWorkers = island.workers().stream()
                .filter(w -> !w.uuid().equals(targetUuid))
                .collect(Collectors.toList());
        
        updateIslandMembers(updatedMembers, updatedWorkers);
        
        // 추방된 플레이어가 온라인이면 알림
        Player targetPlayer = Bukkit.getPlayer(UUID.fromString(targetUuid));
        if (targetPlayer != null && targetPlayer.isOnline()) {
            targetPlayer.sendMessage(ColorUtil.colorize("&c" + island.islandName() + " 섬에서 추방되었습니다."));
            
            // 섬에 있다면 스폰으로 이동
            if (islandManager.getIslandAt(targetPlayer.getLocation()) != null &&
                islandManager.getIslandAt(targetPlayer.getLocation()).getId().equals(island.islandId())) {
                targetPlayer.teleport(targetPlayer.getWorld().getSpawnLocation());
            }
        }
        
        player.sendMessage(ColorUtil.colorize("&c" + targetName + "님을 섬에서 추방했습니다."));
        player.closeInventory();
        IslandMemberGui.create(plugin.getGuiManager(), viewer, island).open(viewer);
    }
    
    private void updateIslandMembers(List<IslandMemberDTO> members, List<IslandWorkerDTO> workers) {
        IslandDTO updated = new IslandDTO(
                island.islandId(), island.ownerUuid(), island.ownerName(),
                island.islandName(), island.size(), island.isPublic(),
                island.createdAt(), System.currentTimeMillis(),
                members, workers, island.contributions(),
                island.spawnData(), island.upgradeData(), island.permissions(),
                island.pendingInvites(), island.recentVisits(),
                island.totalResets(), island.deletionScheduledAt(),
                island.settings()
        );
        
        islandManager.updateIsland(updated);
        // 현재 인스턴스 변경 불가 (final)
    }
}