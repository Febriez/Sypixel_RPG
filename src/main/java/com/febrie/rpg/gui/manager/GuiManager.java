package com.febrie.rpg.gui.manager;

import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.LeaderboardGui;
import com.febrie.rpg.gui.impl.MainMenuGui;
import com.febrie.rpg.gui.impl.ProfileGui;
import com.febrie.rpg.util.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 중앙 GUI 관리 시스템
 * 모든 GUI의 생성, 관리, 네비게이션을 담당
 *
 * @author Febrie, CoffeeTory
 */
public class GuiManager {

    private final Plugin plugin;
    private final LangManager langManager;
    private final Map<UUID, GuiFramework> activeGuis = new ConcurrentHashMap<>();

    // 네비게이션 히스토리 관리
    private final Map<UUID, Stack<NavigationEntry>> navigationStacks = new ConcurrentHashMap<>();

    /**
     * 네비게이션 엔트리
     */
    private record NavigationEntry(
            Class<? extends GuiFramework> guiClass,
            Object... args
    ) {
    }

    public GuiManager(@NotNull Plugin plugin, @NotNull LangManager langManager) {
        this.plugin = plugin;
        this.langManager = langManager;
    }

    /**
     * GUI 열기
     *
     * @param player 플레이어
     * @param gui    GUI 인스턴스
     */
    public void openGui(@NotNull Player player, @NotNull GuiFramework gui) {
        final UUID playerId = player.getUniqueId();

        // 이전 GUI 정리
        GuiFramework previousGui = activeGuis.get(playerId);
        if (previousGui != null) {
            previousGui.close(player);
        }

        // 현재 GUI를 네비게이션 스택에 추가 (메인 메뉴가 아닌 경우)
        if (!(gui instanceof MainMenuGui)) {
            NavigationEntry currentEntry = new NavigationEntry(gui.getClass());
            navigationStacks.computeIfAbsent(playerId, k -> new Stack<>()).push(currentEntry);
        }

        // 새 GUI 등록 및 열기
        activeGuis.put(playerId, gui);

        // 실제 인벤토리 열기는 다음 틱에 실행
        Bukkit.getScheduler().runTask(plugin, () -> {
            gui.open(player);

            // BaseGui의 네비게이션 버튼 업데이트
            if (gui instanceof com.febrie.rpg.gui.framework.BaseGui baseGui) {
                baseGui.updateNavigationButtons();
            }
        });
    }

    /**
     * 뒤로가기 기능
     */
    public void goBack(@NotNull Player player) {
        Stack<NavigationEntry> stack = navigationStacks.get(player.getUniqueId());

        if (stack == null || stack.isEmpty()) {
            openMainMenu(player);
            return;
        }

        // 현재 GUI 제거
        stack.pop();

        if (stack.isEmpty()) {
            openMainMenu(player);
            return;
        }

        // 이전 GUI 열기
        NavigationEntry previousEntry = stack.peek();
        GuiFramework previousGui = recreateGui(player, previousEntry);

        if (previousGui != null) {
            // 스택에서 pop을 한 번 더 해서 중복 방지
            stack.pop();
            openGui(player, previousGui);
        } else {
            openMainMenu(player);
        }
    }

    /**
     * 뒤로가기 가능 여부
     */
    public boolean canGoBack(@NotNull Player player) {
        Stack<NavigationEntry> stack = navigationStacks.get(player.getUniqueId());
        return stack != null && !stack.isEmpty();
    }

    /**
     * 현재 GUI 재생성
     */
    public void recreateCurrentGui(@NotNull Player player) {
        GuiFramework currentGui = activeGuis.get(player.getUniqueId());
        if (currentGui != null) {
            Class<? extends GuiFramework> guiClass = currentGui.getClass();

            // 현재 GUI 재생성
            final GuiFramework newGui = createGuiInstance(guiClass, player);

            if (newGui != null) {
                // 스택 유지하면서 GUI만 교체
                activeGuis.put(player.getUniqueId(), newGui);

                // 다음 틱에 열기
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.closeInventory();
                    newGui.open(player);
                });
            }
        }
    }

    /**
     * GUI 재생성
     */
    @Nullable
    private GuiFramework recreateGui(@NotNull Player player, @NotNull NavigationEntry entry) {
        return createGuiInstance(entry.guiClass, player);
    }

    /**
     * GUI 인스턴스 생성
     */
    @Nullable
    private GuiFramework createGuiInstance(@NotNull Class<? extends GuiFramework> guiClass, @NotNull Player player) {
        // GUI 타입별 재생성 로직
        if (guiClass.equals(MainMenuGui.class)) {
            return new MainMenuGui(this, langManager, player);
        } else if (guiClass.equals(ProfileGui.class)) {
            return new ProfileGui(player, player, this, langManager);
        } else if (guiClass.equals(LeaderboardGui.class)) {
            return new LeaderboardGui(this, langManager, player);
        }
        // 필요한 GUI 타입 추가...

        return null;
    }

    /**
     * 플레이어 관련 데이터 정리
     */
    public void removePlayer(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        activeGuis.remove(playerId);
        navigationStacks.remove(playerId);
    }

    /**
     * 모든 GUI 정리
     */
    public void cleanup() {
        activeGuis.clear();
        navigationStacks.clear();
    }

    /**
     * 현재 열려있는 GUI 가져오기
     */
    @Nullable
    public GuiFramework getActiveGui(@NotNull Player player) {
        return activeGuis.get(player.getUniqueId());
    }

    /**
     * 현재 GUI 새로고침
     */
    public void refreshCurrentGui(@NotNull Player player) {
        GuiFramework gui = activeGuis.get(player.getUniqueId());
        if (gui != null) {
            gui.refresh();
        }
    }

    /**
     * 메인 메뉴 열기
     * 메인 메뉴는 네비게이션의 시작점이므로 스택을 초기화
     */
    public void openMainMenu(@NotNull Player player) {
        UUID playerId = player.getUniqueId();

        // 네비게이션 스택 초기화
        navigationStacks.remove(playerId);

        // 메인 메뉴 열기
        MainMenuGui mainMenu = new MainMenuGui(this, langManager, player);
        openGui(player, mainMenu);
    }

    /**
     * 프로필 GUI 열기
     */
    public void openProfileGui(@NotNull Player player) {
        ProfileGui profileGui = new ProfileGui(player, player, this, langManager);
        openGui(player, profileGui);
    }

    /**
     * 특정 플레이어 프로필 GUI 열기
     */
    public void openProfileGui(@NotNull Player viewer, @NotNull Player target) {
        ProfileGui profileGui = new ProfileGui(target, viewer, this, langManager);
        openGui(viewer, profileGui);
    }

    /**
     * 리더보드 GUI 열기
     */
    public void openLeaderboardGui(@NotNull Player player) {
        LeaderboardGui leaderboardGui = new LeaderboardGui(this, langManager, player);
        openGui(player, leaderboardGui);
    }
}