package com.febrie.rpg.gui.manager;

import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.MainMenuGui;
import com.febrie.rpg.gui.impl.ProfileGui;
import com.febrie.rpg.util.LangManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages GUI instances for players
 * Handles creation, caching, and cleanup of GUIs
 *
 * @author Febrie, CoffeeTory
 */
public class GuiManager {

    private final Plugin plugin;
    private final LangManager langManager;

    // 플레이어별 현재 열려있는 GUI 추적
    private final Map<UUID, GuiFramework> activeGuis;

    // 플레이어별 GUI 히스토리 (뒤로가기 기능용)
    private final Map<UUID, java.util.Deque<GuiFramework>> guiHistory;

    // GUI 캐시 (필요시 재사용)
    private final Map<String, GuiFramework> guiCache;

    public GuiManager(@NotNull Plugin plugin, @NotNull LangManager langManager) {
        this.plugin = plugin;
        this.langManager = langManager;
        this.activeGuis = new ConcurrentHashMap<>();
        this.guiHistory = new ConcurrentHashMap<>();
        this.guiCache = new ConcurrentHashMap<>();
    }

    /**
     * 메인 메뉴 GUI를 엽니다
     *
     * @param player 플레이어
     */
    public void openMainMenuGui(@NotNull Player player) {
        closeCurrentGui(player);

        MainMenuGui gui = new MainMenuGui(this, langManager, player);
        openGui(player, gui);
    }

    /**
     * 플레이어의 프로필 GUI를 엽니다
     *
     * @param player 플레이어 (자신의 프로필을 보는 경우)
     */
    public void openProfileGui(@NotNull Player player) {
        openProfileGui(player, player);
    }

    /**
     * 특정 플레이어의 프로필 GUI를 엽니다
     *
     * @param viewer 보는 플레이어
     * @param target 프로필을 볼 대상 플레이어
     */
    public void openProfileGui(@NotNull Player viewer, @NotNull Player target) {
        // 기존 GUI 정리
        closeCurrentGui(viewer);

        // 새 GUI 생성 및 열기
        ProfileGui gui = new ProfileGui(target, this, langManager);
        openGui(viewer, gui);
    }

    /**
     * GUI를 열고 관리에 등록합니다
     *
     * @param player 플레이어
     * @param gui    GUI 인스턴스
     */
    public void openGui(@NotNull Player player, @NotNull GuiFramework gui) {
        UUID playerId = player.getUniqueId();

        // 현재 GUI를 히스토리에 추가 (뒤로가기용)
        GuiFramework currentGui = activeGuis.get(playerId);
        if (currentGui != null) {
            addToHistory(playerId, currentGui);
        }

        // 새 GUI 등록 및 열기
        activeGuis.put(playerId, gui);
        gui.open(player);
    }

    /**
     * 현재 열려있는 GUI를 닫습니다
     *
     * @param player 플레이어
     */
    public void closeCurrentGui(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        GuiFramework gui = activeGuis.remove(playerId);

        if (gui != null) {
            gui.close(player);
        }

        // 히스토리도 정리
        clearHistory(playerId);
    }

    /**
     * 이전 GUI로 돌아갑니다
     *
     * @param player 플레이어
     * @return 성공 여부
     */
    public boolean goBack(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        java.util.Deque<GuiFramework> history = guiHistory.get(playerId);

        if (history == null || history.isEmpty()) {
            return false;
        }

        // 현재 GUI 닫기
        GuiFramework currentGui = activeGuis.get(playerId);
        if (currentGui != null) {
            currentGui.close(player);
        }

        // 이전 GUI 열기
        GuiFramework previousGui = history.pollLast();
        if (previousGui != null) {
            activeGuis.put(playerId, previousGui);
            previousGui.open(player);
            return true;
        }

        return false;
    }

    /**
     * 플레이어의 현재 GUI를 새로고침합니다
     *
     * @param player 플레이어
     */
    public void refreshCurrentGui(@NotNull Player player) {
        GuiFramework gui = activeGuis.get(player.getUniqueId());
        if (gui != null) {
            gui.refresh();
        }
    }

    /**
     * 플레이어의 현재 GUI를 가져옵니다
     *
     * @param player 플레이어
     * @return 현재 GUI 또는 null
     */
    @Nullable
    public GuiFramework getCurrentGui(@NotNull Player player) {
        return activeGuis.get(player.getUniqueId());
    }

    /**
     * 플레이어가 특정 타입의 GUI를 열고 있는지 확인합니다
     *
     * @param player  플레이어
     * @param guiType GUI 클래스 타입
     * @return 해당 타입의 GUI를 열고 있으면 true
     */
    public boolean hasGuiOpen(@NotNull Player player, @NotNull Class<? extends GuiFramework> guiType) {
        GuiFramework gui = activeGuis.get(player.getUniqueId());
        return gui != null && guiType.isInstance(gui);
    }

    /**
     * 플레이어가 로그아웃할 때 정리 작업을 수행합니다
     *
     * @param player 플레이어
     */
    public void onPlayerLogout(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        activeGuis.remove(playerId);
        clearHistory(playerId);
    }

    /**
     * 모든 플레이어의 GUI를 정리합니다 (플러그인 종료 시)
     */
    public void cleanup() {
        activeGuis.clear();
        guiHistory.clear();
        guiCache.clear();
    }

    /**
     * GUI 캐시에서 GUI를 가져오거나 생성합니다
     *
     * @param cacheKey 캐시 키
     * @param supplier GUI 생성 함수
     * @return GUI 인스턴스
     */
    @SuppressWarnings("unchecked")
    public <T extends GuiFramework> T getOrCreateGui(@NotNull String cacheKey,
                                                     @NotNull java.util.function.Supplier<T> supplier) {
        return (T) guiCache.computeIfAbsent(cacheKey, k -> supplier.get());
    }

    /**
     * 캐시에서 GUI를 제거합니다
     *
     * @param cacheKey 캐시 키
     */
    public void removeFromCache(@NotNull String cacheKey) {
        guiCache.remove(cacheKey);
    }

    // Private helper methods
    private void addToHistory(@NotNull UUID playerId, @NotNull GuiFramework gui) {
        guiHistory.computeIfAbsent(playerId, k -> new java.util.ArrayDeque<>()).addLast(gui);

        // 히스토리 크기 제한 (최대 10개)
        java.util.Deque<GuiFramework> history = guiHistory.get(playerId);
        while (history.size() > 10) {
            history.pollFirst();
        }
    }

    private void clearHistory(@NotNull UUID playerId) {
        guiHistory.remove(playerId);
    }

    /**
     * GUI 매니저의 통계 정보를 가져옵니다 (디버깅용)
     *
     * @return 통계 정보 맵
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeGuis", activeGuis.size());
        stats.put("cachedGuis", guiCache.size());
        stats.put("playersWithHistory", guiHistory.size());

        // GUI 타입별 통계
        Map<String, Integer> guiTypes = new HashMap<>();
        activeGuis.values().forEach(gui -> {
            String type = gui.getClass().getSimpleName();
            guiTypes.merge(type, 1, Integer::sum);
        });
        stats.put("guiTypeStats", guiTypes);

        return stats;
    }

    /**
     * Gets the LangManager instance
     */
    public LangManager getLangManager() {
        return langManager;
    }
}