package com.febrie.rpg.gui.manager;

import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * GUI 관리자 - 간소화된 버전
 * GUI 열기, 닫기, 네비게이션 기능만 제공
 * 각 GUI는 버튼 클릭 시 직접 인스턴스를 생성하여 사용
 *
 * @author Febrie, CoffeeTory
 */
public class GuiManager {

    private final LangManager langManager;

    // 플레이어별 현재 열려있는 GUI
    private final Map<UUID, GuiFramework> activeGuis = new HashMap<>();

    // 네비게이션 스택 (뒤로가기 기능)
    private final Map<UUID, Deque<Class<? extends GuiFramework>>> navigationStacks = new HashMap<>();

    public GuiManager(@NotNull LangManager langManager) {
        this.langManager = langManager;
    }

    /**
     * GUI 열기 - 핵심 메소드
     * 모든 GUI는 이 메소드를 통해 열립니다
     *
     * @param player 플레이어
     * @param gui    열려는 GUI
     */
    public void openGui(@NotNull Player player, @NotNull GuiFramework gui) {
        UUID playerId = player.getUniqueId();

        // 현재 GUI를 네비게이션 스택에 추가
        GuiFramework currentGui = activeGuis.get(playerId);
        if (currentGui != null && !currentGui.getClass().equals(gui.getClass())) {
            navigationStacks.computeIfAbsent(playerId, k -> new ArrayDeque<>())
                    .push(currentGui.getClass());
        }

        // 새 GUI 등록
        activeGuis.put(playerId, gui);

        // GUI 열기
        gui.open(player);
    }

    /**
     * 이전 GUI로 돌아갈 수 있는지 확인
     *
     * @param player 플레이어
     * @return 뒤로갈 수 있는지 여부
     */
    public boolean canNavigateBack(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        Deque<Class<? extends GuiFramework>> stack = navigationStacks.get(playerId);
        return stack != null && !stack.isEmpty();
    }

    /**
     * 이전 GUI로 돌아가기
     *
     * @param player 플레이어
     * @return 이전 GUI로 돌아갔는지 여부
     */
    public boolean navigateBack(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        Deque<Class<? extends GuiFramework>> stack = navigationStacks.get(playerId);

        if (stack == null || stack.isEmpty()) {
            return false;
        }

        // 이전 GUI 클래스 가져오기
        Class<? extends GuiFramework> previousGuiClass = stack.pop();

        // 이전 GUI 재생성
        GuiFramework previousGui = recreateGui(previousGuiClass, player);
        if (previousGui != null) {
            activeGuis.put(playerId, previousGui);
            previousGui.open(player);
            return true;
        }

        return false;
    }

    /**
     * GUI 재생성 - 네비게이션용
     * 각 GUI 타입별로 적절한 생성자를 호출
     *
     * @param guiClass GUI 클래스
     * @param player   플레이어
     * @return 생성된 GUI 또는 null
     */
    @Nullable
    private GuiFramework recreateGui(@NotNull Class<? extends GuiFramework> guiClass, @NotNull Player player) {
        try {
            // 리플렉션을 사용하여 GUI 재생성
            // 기본적으로 (GuiManager, LangManager, Player) 생성자를 찾습니다
            var constructor = guiClass.getDeclaredConstructor(GuiManager.class, LangManager.class, Player.class);
            return constructor.newInstance(this, langManager, player);
        } catch (Exception e) {
            // 다른 시그니처의 생성자가 필요한 경우 (예: ProfileGui)
            try {
                // (Player, Player, GuiManager, LangManager) 시그니처 시도
                var constructor = guiClass.getDeclaredConstructor(Player.class, Player.class, GuiManager.class, LangManager.class);
                return constructor.newInstance(player, player, this, langManager); // 자기 자신의 프로필
            } catch (Exception e2) {
                LogUtil.error("Failed to recreate GUI: " + guiClass.getSimpleName(), e2);
                return null;
            }
        }
    }

    /**
     * 플레이어 관련 데이터 정리
     *
     * @param player 플레이어
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
     *
     * @param player 플레이어
     * @return 현재 GUI 또는 null
     */
    @Nullable
    public GuiFramework getActiveGui(@NotNull Player player) {
        return activeGuis.get(player.getUniqueId());
    }

    /**
     * 현재 GUI 새로고침
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
     * 네비게이션 스택 초기화
     * 메인 메뉴 등 루트 GUI를 열 때 사용
     *
     * @param player 플레이어
     */
    public void clearNavigationStack(@NotNull Player player) {
        navigationStacks.remove(player.getUniqueId());
    }

    /**
     * 현재 활성화된 모든 GUI 가져오기
     * 복사본을 반환하여 외부에서 직접 수정하지 못하도록 함
     *
     * @return 활성 GUI 맵의 복사본
     */
    @NotNull
    public Map<UUID, GuiFramework> getActiveGuis() {
        return new HashMap<>(activeGuis);
    }

    /**
     * LangManager 가져오기
     * GUI에서 필요할 때 사용
     *
     * @return LangManager 인스턴스
     */
    @NotNull
    public LangManager getLangManager() {
        return langManager;
    }
}