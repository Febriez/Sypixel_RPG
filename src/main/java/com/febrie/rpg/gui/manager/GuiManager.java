package com.febrie.rpg.gui.manager;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.framework.GuiFramework;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;
import net.kyori.adventure.text.Component;
/**
 * GUI 관리자 - 간소화된 버전
 * GUI 열기, 닫기, 네비게이션 기능만 제공
 * 각 GUI는 버튼 클릭 시 직접 인스턴스를 생성하여 사용
 *
 * @author Febrie, CoffeeTory
 */
public class GuiManager {
    private final RPGMain plugin;
    // 플레이어별 현재 열려있는 GUI
    private final Map<UUID, GuiFramework> activeGuis = new HashMap<>();
    public GuiManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
    }
    /**
     * Get the plugin instance
     * @return RPGMain plugin
     */
    public RPGMain getPlugin() {
        return plugin;
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
        
        // 새 GUI 등록
        activeGuis.put(playerId, gui);
        // GUI 열기
        gui.open(player);
    }
    
    /**
     * 퀘스트 대화 GUI 열기
     * @param player 플레이어
     * @param quest  퀘스트
     */
    public void openQuestDialogGui(@NotNull Player player, @NotNull com.febrie.rpg.quest.Quest quest) {
        com.febrie.rpg.gui.impl.quest.QuestDialogGui questDialogGui =
                com.febrie.rpg.gui.impl.quest.QuestDialogGui.create(this, player, quest);
        openGui(player, questDialogGui);
    }
    
    /**
     * 플레이어 관련 데이터 정리
     * @param player 플레이어
     */
    public void removePlayer(@NotNull Player player) {
        activeGuis.remove(player.getUniqueId());
    }
    
    /**
     * 모든 GUI 정리
     */
    public void cleanup() {
        activeGuis.clear();
    }
    
    /**
     * 현재 열려있는 GUI 가져오기
     * @param player 플레이어
     * @return 현재 GUI 또는 null
    @Nullable
    public GuiFramework getActiveGui(@NotNull Player player) {
        return activeGuis.get(player.getUniqueId());
    }
    
    /**
     * 현재 GUI 새로고침
     * @param player 플레이어
     */
    public void refreshCurrentGui(@NotNull Player player) {
        GuiFramework gui = activeGuis.get(player.getUniqueId());
        if (gui != null) {
            gui.refresh();
        }
    }
    
    /**
     * 현재 활성화된 모든 GUI 가져오기
     * 복사본을 반환하여 외부에서 직접 수정하지 못하도록 함
     * @return 활성 GUI 맵의 복사본
     */
    @NotNull
    public Map<UUID, GuiFramework> getActiveGuis() {
        return new HashMap<>(activeGuis);
    }
}
