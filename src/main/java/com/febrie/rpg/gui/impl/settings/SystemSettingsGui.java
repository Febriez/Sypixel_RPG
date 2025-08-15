package com.febrie.rpg.gui.impl.settings;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.PlayerSettings;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 시스템 설정 GUI
 * 확인 대화상자 등의 시스템 관련 설정
 *
 * @author Febrie
 */
public class SystemSettingsGui extends BaseGui {

    private static final int GUI_SIZE = 27; // 3 rows

    // 설정 버튼 슬롯
    private static final int CONFIRMATION_DIALOGS_SLOT = 13;

    // 타이틀 슬롯
    private static final int TITLE_SLOT = 4;

    private SystemSettingsGui(@NotNull GuiManager guiManager,
                            @NotNull Player player) {
        super(player, guiManager, GUI_SIZE, "gui.system-settings.title");
    }

    /**
     * SystemSettingsGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param player 플레이어
     * @return 초기화된 SystemSettingsGui 인스턴스
     */
    public static SystemSettingsGui create(@NotNull GuiManager guiManager,
                                          @NotNull Player player) {
        SystemSettingsGui gui = new SystemSettingsGui(guiManager, player);
        gui.initialize("gui.system-settings.title");
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.text("시스템 설정", ColorUtil.LEGENDARY);
    }

    @Override
    protected GuiFramework getBackTarget() {
        return PlayerSettingsGui.create(guiManager, viewer);
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupSettingControls();
        setupStandardNavigation(false, true);
    }

    /**
     * 장식 요소 설정
     */
    private void setupDecorations() {
        createBorder();
        setupTitleItem();
    }

    /**
     * 타이틀 아이템 설정
     */
    private void setupTitleItem() {
        GuiItem titleItem = GuiItem.display(
                new ItemBuilder(Material.REDSTONE_BLOCK)
                        .displayName(Component.text("⚙ 시스템 설정", ColorUtil.LEGENDARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("시스템 관련 설정을 변경합니다", ColorUtil.GRAY))
                        .build()
        );
        setItem(TITLE_SLOT, titleItem);
    }

    /**
     * 설정 컨트롤 설정
     */
    private void setupSettingControls() {
        RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager().getOrCreatePlayer(viewer);
        PlayerSettings settings = rpgPlayer.getPlayerSettings();

        setupConfirmationDialogsToggle(settings);

        // 빈 슬롯들을 투명한 유리판으로 채우기
        for (int i = 0; i < GUI_SIZE; i++) {
            if (getItem(i) == null) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 확인 대화상자 토글 설정
     */
    private void setupConfirmationDialogsToggle(PlayerSettings settings) {
        updateConfirmationDialogsToggle(settings);
    }

    /**
     * 확인 대화상자 토글 업데이트
     */
    private void updateConfirmationDialogsToggle(PlayerSettings settings) {
        boolean enabled = settings.isConfirmationDialogsEnabled();
        
        GuiItem confirmationDialogsToggle = GuiItem.clickable(
                new ItemBuilder(enabled ? Material.WRITABLE_BOOK : Material.BOOK)
                        .displayName(Component.text("❓ 확인 대화상자", ColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("상태: " + (enabled ? "활성화" : "비활성화"), 
                                enabled ? ColorUtil.SUCCESS : ColorUtil.ERROR))
                        .addLore(Component.empty())
                        .addLore(Component.text("스탯 배분, 특성 학습 등", ColorUtil.GRAY))
                        .addLore(Component.text("중요한 행동을 할 때", ColorUtil.GRAY))
                        .addLore(Component.text("확인 메시지를 표시합니다", ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("예시:", ColorUtil.YELLOW))
                        .addLore(Component.text("'정말 스탯 포인트를 사용하시겠습니까?'", ColorUtil.GRAY))
                        .addLore(Component.text("'정말 특성을 배우시겠습니까?'", ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 " + (enabled ? "비활성화" : "활성화"), ColorUtil.YELLOW))
                        .build(),
                p -> {
                    settings.setConfirmationDialogsEnabled(!enabled);
                    updateConfirmationDialogsToggle(settings);
                    playClickSound(p);
                    com.febrie.rpg.util.LangManager.sendMessage(p, "확인 대화상자가 " + (settings.isConfirmationDialogsEnabled() ? "활성화" : "비활성화") + "되었습니다");
                }
        );
        setItem(CONFIRMATION_DIALOGS_SLOT, confirmationDialogsToggle);
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
}