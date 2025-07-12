package com.febrie.rpg.gui.manager;

import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.MainMenuGui;
import com.febrie.rpg.gui.impl.ProfileGui;
import com.febrie.rpg.util.LangManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 중앙 집중식 GUI 네비게이션 관리 시스템
 * 스마트 스택 기반의 히스토리 관리와 부드러운 GUI 전환 제공
 *
 * @author Febrie, CoffeeTory
 */
public class GuiManager {

    private final Plugin plugin;
    private final LangManager langManager;

    // 플레이어별 현재 열려있는 GUI 추적
    private final Map<UUID, GuiFramework> activeGuis;

    // 플레이어별 GUI 네비게이션 스택 (스마트 스택)
    private final Map<UUID, NavigationStack> navigationStacks;

    // GUI 전환 중 플래그 (동시성 문제 방지)
    private final Set<UUID> transitioning;

    /**
     * 스마트 네비게이션 스택
     * 중복 제거 및 순환 방지 기능 포함
     */
    private static class NavigationStack {
        private final LinkedList<GuiEntry> stack = new LinkedList<>();
        private static final int MAX_STACK_SIZE = 10;

        static class GuiEntry {
            final Class<? extends GuiFramework> guiClass;
            final String identifier;
            final long timestamp;

            GuiEntry(GuiFramework gui) {
                this.guiClass = gui.getClass();
                this.identifier = gui.getClass().getSimpleName() + "_" + gui.hashCode();
                this.timestamp = System.currentTimeMillis();
            }
        }

        void push(GuiFramework gui) {
            GuiEntry newEntry = new GuiEntry(gui);

            // 중복 제거: 같은 GUI 클래스가 스택에 있으면 제거
            stack.removeIf(entry -> entry.guiClass.equals(newEntry.guiClass));

            // 스택에 추가
            stack.addLast(newEntry);

            // 스택 크기 제한
            while (stack.size() > MAX_STACK_SIZE) {
                stack.removeFirst();
            }
        }

        @Nullable
        GuiFramework pop() {
            if (stack.isEmpty()) {
                return null;
            }
            stack.removeLast(); // 현재 GUI 제거
            if (stack.isEmpty()) {
                return null;
            }
            // 실제로는 이전 GUI를 재생성해야 함
            return null; // GuiFactory에서 재생성 로직 필요
        }

        boolean canGoBack() {
            return stack.size() > 1;
        }

        void clear() {
            stack.clear();
        }

        int size() {
            return stack.size();
        }
    }

    public GuiManager(@NotNull Plugin plugin, @NotNull LangManager langManager) {
        this.plugin = plugin;
        this.langManager = langManager;
        this.activeGuis = new ConcurrentHashMap<>();
        this.navigationStacks = new ConcurrentHashMap<>();
        this.transitioning = ConcurrentHashMap.newKeySet();
    }

    /**
     * GUI를 열고 네비게이션 스택에 추가
     * 인벤토리를 닫지 않고 직접 전환하여 마우스 위치 유지
     */
    public void openGui(@NotNull Player player, @NotNull GuiFramework gui) {
        UUID playerId = player.getUniqueId();

        // 전환 중 플래그 설정
        if (!transitioning.add(playerId)) {
            return; // 이미 전환 중이면 무시
        }

        try {
            GuiFramework currentGui = activeGuis.get(playerId);
            NavigationStack navStack = navigationStacks.computeIfAbsent(playerId, k -> new NavigationStack());

            // 현재 GUI가 있으면 스택에 추가
            if (currentGui != null) {
                navStack.push(currentGui);
            }

            // 새 GUI로 전환
            activeGuis.put(playerId, gui);

            // 인벤토리 직접 업데이트 (닫지 않음)
            if (currentGui != null && player.getOpenInventory().getTopInventory() != null) {
                // 기존 인벤토리의 내용을 새 GUI의 인벤토리로 교체
                Inventory newInventory = gui.getInventory();
                Inventory currentInventory = player.getOpenInventory().getTopInventory();

                // 크기가 같으면 내용만 교체
                if (currentInventory.getSize() == newInventory.getSize()) {
                    currentInventory.clear();
                    currentInventory.setContents(newInventory.getContents());
                    // GUI 제목 업데이트는 Paper API 필요
                } else {
                    // 크기가 다르면 새로 열기
                    gui.open(player);
                }
            } else {
                // 처음 열기
                gui.open(player);
            }

            // 뒤로가기 버튼 업데이트
            updateBackButton(player, gui);

        } finally {
            transitioning.remove(playerId);
        }
    }

    /**
     * 뒤로가기 기능
     * 스택에서 이전 GUI로 돌아감
     */
    public boolean goBack(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        NavigationStack navStack = navigationStacks.get(playerId);

        if (navStack == null || !navStack.canGoBack()) {
            return false;
        }

        // 전환 중 플래그 설정
        if (!transitioning.add(playerId)) {
            return false;
        }

        try {
            // 현재 GUI 제거
            navStack.pop();

            // 이전 GUI 재생성 및 열기
            GuiFramework previousGui = recreatePreviousGui(player, navStack);
            if (previousGui != null) {
                activeGuis.put(playerId, previousGui);

                // 인벤토리 업데이트
                Inventory newInventory = previousGui.getInventory();
                Inventory currentInventory = player.getOpenInventory().getTopInventory();

                if (currentInventory != null && currentInventory.getSize() == newInventory.getSize()) {
                    currentInventory.clear();
                    currentInventory.setContents(newInventory.getContents());
                } else {
                    previousGui.open(player);
                }

                // 뒤로가기 버튼 업데이트
                updateBackButton(player, previousGui);
                return true;
            }

            return false;
        } finally {
            transitioning.remove(playerId);
        }
    }

    /**
     * 뒤로가기 가능 여부 확인
     */
    public boolean canGoBack(@NotNull Player player) {
        NavigationStack navStack = navigationStacks.get(player.getUniqueId());
        return navStack != null && navStack.canGoBack();
    }

    /**
     * 현재 GUI 새로고침
     */
    public void refreshCurrentGui(@NotNull Player player) {
        GuiFramework gui = activeGuis.get(player.getUniqueId());
        if (gui != null) {
            gui.refresh();
            updateBackButton(player, gui);
        }
    }

    /**
     * 뒤로가기 버튼 동적 업데이트
     * 뒤로갈 수 있을 때만 버튼 표시
     */
    private void updateBackButton(@NotNull Player player, @NotNull GuiFramework gui) {
        // GUI가 BaseGui나 ScrollableGui를 상속하는 경우
        // 45번 슬롯의 뒤로가기 버튼을 동적으로 표시/숨김
        if (canGoBack(player)) {
            // 뒤로가기 버튼 표시 로직
            // 이 부분은 BaseGui 업데이트에서 처리
        }
    }

    /**
     * 이전 GUI 재생성
     * GUI 타입에 따라 적절한 인스턴스 생성
     */
    @Nullable
    private GuiFramework recreatePreviousGui(@NotNull Player player, @NotNull NavigationStack navStack) {
        if (navStack.stack.isEmpty()) {
            return null;
        }

        NavigationStack.GuiEntry lastEntry = navStack.stack.getLast();
        Class<? extends GuiFramework> guiClass = lastEntry.guiClass;

        // GUI 타입별 재생성 로직
        if (guiClass == MainMenuGui.class) {
            return new MainMenuGui(this, langManager, player);
        } else if (guiClass == ProfileGui.class) {
            return new ProfileGui(player, player, this, langManager);
        }
        // 다른 GUI 타입들도 추가...

        return null;
    }

    /**
     * 특정 GUI 타입으로 직접 열기 (편의 메서드들)
     */
    public void openMainMenuGui(@NotNull Player player) {
        MainMenuGui gui = new MainMenuGui(this, langManager, player);
        openGui(player, gui);
    }

    public void openProfileGui(@NotNull Player player) {
        openProfileGui(player, player);
    }

    public void openProfileGui(@NotNull Player viewer, @NotNull Player target) {
        ProfileGui gui = new ProfileGui(target, viewer, this, langManager);
        openGui(viewer, gui);
    }

    /**
     * 현재 열려있는 GUI 닫기
     */
    public void closeCurrentGui(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        GuiFramework gui = activeGuis.remove(playerId);

        if (gui != null) {
            gui.close(player);
        }

        // 네비게이션 스택도 정리
        navigationStacks.remove(playerId);
    }

    /**
     * 플레이어 로그아웃 시 정리
     */
    public void onPlayerLogout(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        activeGuis.remove(playerId);
        navigationStacks.remove(playerId);
        transitioning.remove(playerId);
    }

    /**
     * 플러그인 종료 시 전체 정리
     */
    public void cleanup() {
        activeGuis.clear();
        navigationStacks.clear();
        transitioning.clear();
    }

    /**
     * 현재 GUI 가져오기
     */
    @Nullable
    public GuiFramework getCurrentGui(@NotNull Player player) {
        return activeGuis.get(player.getUniqueId());
    }

    /**
     * 특정 타입의 GUI가 열려있는지 확인
     */
    public boolean hasGuiOpen(@NotNull Player player, @NotNull Class<? extends GuiFramework> guiType) {
        GuiFramework gui = activeGuis.get(player.getUniqueId());
        return gui != null && guiType.isInstance(gui);
    }

    /**
     * 디버깅용 통계 정보
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeGuis", activeGuis.size());
        stats.put("navigationStacks", navigationStacks.size());
        stats.put("transitioning", transitioning.size());

        // 네비게이션 스택 깊이 통계
        Map<String, Integer> stackDepths = new HashMap<>();
        navigationStacks.forEach((uuid, stack) -> {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                stackDepths.put(player.getName(), stack.size());
            }
        });
        stats.put("stackDepths", stackDepths);

        return stats;
    }

    public LangManager getLangManager() {
        return langManager;
    }
}