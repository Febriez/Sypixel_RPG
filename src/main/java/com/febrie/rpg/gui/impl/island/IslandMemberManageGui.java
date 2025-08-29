package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.dto.island.IslandMemberDTO;
import com.febrie.rpg.dto.island.IslandWorkerDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.GuiHandlerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Arrays;
import java.util.Optional;
/**
 * 섬 멤버 관리 GUI
 * 멤버의 역할 변경, 추방 등을 관리
 *
 * @author Febrie, CoffeeTory
 */
public class IslandMemberManageGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final IslandDTO island;
    private final String targetUuid;
    private final boolean isOwner;
    private final boolean isCoOwner;
    // 대상 멤버 정보
    private final String targetName;
    private final String currentRole;
    private final boolean targetIsCoOwner;
    private final boolean targetIsWorker;
    private IslandMemberManageGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                                  @NotNull RPGMain plugin, @NotNull IslandDTO island, @NotNull String targetUuid) {
        super(viewer, guiManager, 45, LangManager.getComponent("gui.island.member-manage.title".replace("-", "_"), viewer)); // 5줄 GUI
        this.islandManager = plugin.getIslandManager();
        this.island = island;
        this.targetUuid = targetUuid;
        this.isOwner = island.core().ownerUuid().equals(viewer.getUniqueId().toString());
        
        // 뷰어가 부섬장인지 확인
        this.isCoOwner = island.membership().members().stream()
                .anyMatch(m -> m.uuid().equals(viewer.getUniqueId().toString()) && m.isCoOwner());
        // 대상 멤버 정보 찾기
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(UUID.fromString(targetUuid));
        this.targetName = targetPlayer.getName() != null ? targetPlayer.getName() : "Unknown";
        // 멤버 찾기
        Optional<IslandMemberDTO> member = island.membership().members().stream()
                .filter(m -> m.uuid().equals(targetUuid))
                .findFirst();
        if (member.isPresent()) {
            this.targetIsCoOwner = member.get().isCoOwner();
            this.targetIsWorker = false;
            this.currentRole = targetIsCoOwner ? LangManager.getString("island.roles.sub-owner", getViewerLocale()) : LangManager.getString("island.roles.member", getViewerLocale());
        } else {
            // 알바생 찾기
            Optional<IslandWorkerDTO> worker = island.membership().workers().stream()
                    .filter(w -> w.uuid().equals(targetUuid))
                    .findFirst();
            
            this.targetIsCoOwner = false;
            this.targetIsWorker = worker.isPresent();
            this.currentRole = targetIsWorker ? LangManager.getString("island.roles.worker", getViewerLocale()) : "Unknown";
        }
    }
    /**
     * Factory method to create and open the member management GUI
     */
    public static IslandMemberManageGui create(@NotNull RPGMain plugin, @NotNull Player viewer,
                                              @NotNull IslandDTO island, @NotNull String targetUuid) {
        return new IslandMemberManageGui(viewer, plugin.getGuiManager(), plugin, island, targetUuid);
    }
    
    @Override
    protected void setupLayout() {
        fillBorder(Material.BLUE_STAINED_GLASS_PANE);
        // 멤버 정보
        setItem(13, new GuiItem(createMemberInfoItem()));
        // 관리 옵션들
        if (canManageMember()) {
            if (!targetIsWorker) {
                // 멤버 역할 변경
                if (targetIsCoOwner) {
                    setItem(20, new GuiItem(createDemoteItem()).onAnyClick(this::handleDemote)); // 부섬장 → 일반 멤버
                } else {
                    setItem(20, new GuiItem(createPromoteItem()).onAnyClick(this::handlePromote)); // 일반 멤버 → 부섬장
                }
                
                setItem(22, new GuiItem(createToWorkerItem()).onAnyClick(this::handleMemberToWorker)); // 알바생으로 변경
            } else {
                // 알바생 → 멤버로 승급
                setItem(21, new GuiItem(createToMemberItem()).onAnyClick(this::handleWorkerToMember));
            }
            // 추방
            setItem(24, new GuiItem(createKickItem()).onAnyClick(this::handleKick));
            // 권한 설정
            setItem(30, new GuiItem(createPermissionItem()).onAnyClick(player -> 
                player.sendMessage(LangManager.getComponent("gui.island.member-manage.permission-not-implemented".replace("-", "_"), viewer).color(NamedTextColor.RED))));
        } else {
            // 권한 없음 안내
            setItem(22, new GuiItem(createNoPermissionItem()));
        }
        // 뒤로가기
        setItem(40, new GuiItem(createBackButton()).onAnyClick(player -> {
            player.closeInventory();
            IslandMemberGui.create(plugin.getGuiManager(), viewer, island).open(viewer);
        }));
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return null; // Use back button with direct navigation
    }
    
    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("gui.island.member-manage.title-with-name", Component.text(targetName));
    }
    
    private ItemStack createMemberInfoItem() {
        return ItemBuilder.of(Material.PLAYER_HEAD)
                .displayNameTranslated("items.island.member-manage.member-info.name")
                .addLore(Component.empty())
                .addLore(LangManager.getComponent("gui.island.member-manage.current-role".replace("-", "_"), viewer).color(NamedTextColor.GRAY)
                        .append(Component.text(currentRole, NamedTextColor.WHITE)))
                .addLore(Component.text("UUID: ", NamedTextColor.GRAY)
                        .append(Component.text(targetUuid.substring(0, 8) + "...", NamedTextColor.WHITE)))
                .addLoreTranslated("items.island.member-manage.member-info.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createPromoteItem() {
        return ItemBuilder.of(Material.GOLDEN_HELMET)
                .displayNameTranslated("items.island.member-manage.promote.name")
                .loreTranslated("items.island.member-manage.promote.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createDemoteItem() {
        return ItemBuilder.of(Material.IRON_HELMET)
                .displayNameTranslated("items.island.member-manage.demote.name")
                .loreTranslated("items.island.member-manage.demote.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createToWorkerItem() {
        return ItemBuilder.of(Material.LEATHER_HELMET)
                .displayNameTranslated("items.island.member-manage.to-worker.name")
                .loreTranslated("items.island.member-manage.to-worker.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createToMemberItem() {
        return ItemBuilder.of(Material.DIAMOND_HELMET)
                .displayNameTranslated("items.island.member-manage.to-member.name")
                .loreTranslated("items.island.member-manage.to-member.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createKickItem() {
        return ItemBuilder.of(Material.BARRIER)
                .displayNameTranslated("items.island.member-manage.kick.name")
                .loreTranslated("items.island.member-manage.kick.lore")
                .hideAllFlags()
                .build();
    }
    private ItemStack createPermissionItem() {
        return ItemBuilder.of(Material.COMMAND_BLOCK)
                .displayNameTranslated("items.island.member-manage.permission.name")
                .loreTranslated("items.island.member-manage.permission.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createNoPermissionItem() {
        return ItemBuilder.of(Material.REDSTONE_BLOCK)
                .displayNameTranslated("items.island.member-manage.no-permission.name")
                .loreTranslated("items.island.member-manage.no-permission.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createBackButton() {
        return ItemBuilder.of(Material.ARROW)
                .displayName(LangManager.getComponent("items.buttons.back.name", getViewerLocale()))
                .addLore(LangManager.getComponent("items.buttons.back.lore", getViewerLocale()))
                .hideAllFlags()
                .build();
    }
    
    private boolean canManageMember() {
        // 섬장은 모든 멤버 관리 가능
        if (isOwner) return true;
        // 부섬장은 일반 멤버와 알바생만 관리 가능
        if (isCoOwner && !targetIsCoOwner) return true;
        return false;
    }
    
    private void handlePromote(Player player) {
        List<IslandMemberDTO> updatedMembers = island.membership().members().stream()
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
        updateIslandMembers(updatedMembers, island.membership().workers());
        player.sendMessage(UnifiedColorUtil.parse("&a" + targetName + "님을 부섬장으로 승급시켰습니다!"));
        setupLayout();
    }
    
    private void handleDemote(Player player) {
        List<IslandMemberDTO> updatedMembers = island.membership().members().stream()
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
        updateIslandMembers(updatedMembers, island.membership().workers());
        player.sendMessage(UnifiedColorUtil.parse("&e" + targetName + "님을 일반 멤버로 강등시켰습니다."));
        setupLayout();
    }
    
    private void handleMemberToWorker(Player player) {
        // 멤버에서 제거
        List<IslandMemberDTO> updatedMembers = island.membership().members().stream()
                .filter(m -> !m.uuid().equals(targetUuid))
                .collect(Collectors.toList());
        // 알바생으로 추가
        List<IslandWorkerDTO> updatedWorkers = new ArrayList<>(island.membership().workers());
        updatedWorkers.add(new IslandWorkerDTO(
                targetUuid, targetName,
                System.currentTimeMillis(),
                System.currentTimeMillis()
        ));
        updateIslandMembers(updatedMembers, updatedWorkers);
        player.sendMessage(UnifiedColorUtil.parse("&e" + targetName + "님을 알바생으로 변경했습니다."));
        player.closeInventory();
        IslandMemberGui.create(plugin.getGuiManager(), viewer, island).open(viewer);
    }
    
    private void handleWorkerToMember(Player player) {
        // 알바생에서 제거
        List<IslandWorkerDTO> updatedWorkers = island.membership().workers().stream()
                .filter(w -> !w.uuid().equals(targetUuid))
                .collect(Collectors.toList());
        // 정식 멤버로 추가
        List<IslandMemberDTO> updatedMembers = new ArrayList<>(island.membership().members());
        updatedMembers.add(new IslandMemberDTO(
                targetUuid, targetName, false,
                System.currentTimeMillis(), System.currentTimeMillis(),
                null // 개인 스폰 null
        ));
        updateIslandMembers(updatedMembers, updatedWorkers);
        player.sendMessage(UnifiedColorUtil.parse("&a" + targetName + "님을 정식 멤버로 승급시켰습니다!"));
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
                    String confirmWord = LangManager.getString("island.member.kick-confirm-word", player.locale());
                    if (!confirmWord.equals(input)) {
                        player.sendMessage(LangManager.getComponent("island.member.kick-input-error", player.locale()).color(NamedTextColor.RED));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                    // 추방 실행
                    performKick(player);
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .text(LangManager.getString("island.member.kick-input-text", player.locale()))
                .title(LangManager.getString("island.member.kick-input-title", player.locale()))
                .plugin(plugin)
                .open(player);
    }
    
    private void performKick(Player player) {
        // 멤버 또는 알바생에서 제거
        List<IslandMemberDTO> updatedMembers = island.membership().members().stream()
                .filter(m -> !m.uuid().equals(targetUuid))
                .collect(Collectors.toList());
        List<IslandWorkerDTO> updatedWorkers = island.membership().workers().stream()
                .filter(w -> !w.uuid().equals(targetUuid))
                .collect(Collectors.toList());
        updateIslandMembers(updatedMembers, updatedWorkers);
        
        // 추방된 플레이어가 온라인이면 알림
        Player targetPlayer = Bukkit.getPlayer(UUID.fromString(targetUuid));
        if (targetPlayer != null && targetPlayer.isOnline()) {
            targetPlayer.sendMessage(UnifiedColorUtil.parse("&c" + island.core().islandName() + " 섬에서 추방되었습니다."));
            // 섬에 있다면 스폰으로 이동
            if (islandManager.getIslandAt(targetPlayer.getLocation()) != null &&
                islandManager.getIslandAt(targetPlayer.getLocation()).getId().equals(island.core().islandId())) {
                targetPlayer.teleport(targetPlayer.getWorld().getSpawnLocation());
            }
        }
        player.sendMessage(UnifiedColorUtil.parse("&c" + targetName + "님을 섬에서 추방했습니다."));
        player.closeInventory();
        IslandMemberGui.create(plugin.getGuiManager(), viewer, island).open(viewer);
    }
    
    private void updateIslandMembers(List<IslandMemberDTO> members, List<IslandWorkerDTO> workers) {
        IslandCoreDTO updatedCore = GuiHandlerUtil.createUpdatedCore(island.core());
        
        IslandMembershipDTO updatedMembership = new IslandMembershipDTO(
                island.core().islandId(),
                members,
                workers,
                island.membership().contributions()
        );
        
        IslandDTO updated = new IslandDTO(updatedCore, updatedMembership, island.social(), island.configuration());
        islandManager.updateIsland(updated);
        // 현재 인스턴스 변경 불가 (final)
    }
}
