package com.febrie.rpg.gui.impl.settings;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.player.ProfileGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.UnifiedColorUtil;
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
 * 플레이어 개인 설정 메인 GUI
 * 각 설정 카테고리에 접근할 수 있는 허브
 *
 * @author Febrie
 */
public class PlayerSettingsGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows

    // 설정 카테고리 버튼 슬롯
    private static final int GUI_SETTINGS_SLOT = 20;
    private static final int INGAME_SETTINGS_SLOT = 21;
    private static final int SOCIAL_SETTINGS_SLOT = 22;
    private static final int SYSTEM_SETTINGS_SLOT = 23;
    private static final int NOTIFICATION_SETTINGS_SLOT = 24;

    // 타이틀 슬롯
    private static final int TITLE_SLOT = 4;

    private PlayerSettingsGui(@NotNull GuiManager guiManager,
                            @NotNull Player player) {
        super(player, guiManager, GUI_SIZE, "gui.settings.title");
    }

    /**
     * PlayerSettingsGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param player 플레이어
     * @return 초기화된 PlayerSettingsGui 인스턴스
     */
    public static PlayerSettingsGui create(@NotNull GuiManager guiManager,
                                          @NotNull Player player) {
        PlayerSettingsGui gui = new PlayerSettingsGui(guiManager, player);
        gui.initialize("gui.settings.title");
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.text("개인 설정", UnifiedColorUtil.PRIMARY);
    }

    @Override
    protected GuiFramework getBackTarget() {
        return ProfileGui.create(guiManager, viewer);
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupSettingButtons();
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
                new ItemBuilder(Material.COMPARATOR)
                        .displayName(Component.text("⚙ 개인 설정", UnifiedColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("다양한 설정을 변경할 수 있습니다", UnifiedColorUtil.GRAY))
                        .build()
        );
        setItem(TITLE_SLOT, titleItem);
    }

    /**
     * 설정 버튼들 설정
     */
    private void setupSettingButtons() {
        // GUI 설정
        GuiItem guiSettingsButton = GuiItem.clickable(
                new ItemBuilder(Material.IRON_TRAPDOOR)
                        .displayName(Component.text("🖥 GUI 설정", UnifiedColorUtil.UNCOMMON)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("• GUI 사운드 볼륨 조절", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("• GUI 사운드 음소거/해제", UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 설정", UnifiedColorUtil.YELLOW))
                        .build(),
                p -> {
                    GuiSettingsGui guiSettingsGui = GuiSettingsGui.create(guiManager, p);
                    guiManager.openGui(p, guiSettingsGui);
                    playClickSound(p);
                }
        );
        setItem(GUI_SETTINGS_SLOT, guiSettingsButton);

        // 인게임 설정
        GuiItem ingameSettingsButton = GuiItem.clickable(
                new ItemBuilder(Material.GRASS_BLOCK)
                        .displayName(Component.text("🎮 인게임 설정", UnifiedColorUtil.RARE)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("• 퀘스트 대화 속도 조절", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("• 자동 길안내 설정", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("• 데미지 표시 설정", UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 설정", UnifiedColorUtil.YELLOW))
                        .build(),
                p -> {
                    IngameSettingsGui ingameSettingsGui = IngameSettingsGui.create(guiManager, p);
                    guiManager.openGui(p, ingameSettingsGui);
                    playClickSound(p);
                }
        );
        setItem(INGAME_SETTINGS_SLOT, ingameSettingsButton);

        // 소셜 설정
        GuiItem socialSettingsButton = GuiItem.clickable(
                new ItemBuilder(Material.PLAYER_HEAD)
                        .displayName(Component.text("👥 소셜 설정", UnifiedColorUtil.EPIC)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("• 친구 요청 받기 설정", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("• 길드 초대 받기 설정", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("• 귓말 모드 설정", UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 설정", UnifiedColorUtil.YELLOW))
                        .build(),
                p -> {
                    SocialSettingsGui socialSettingsGui = SocialSettingsGui.create(guiManager, p);
                    guiManager.openGui(p, socialSettingsGui);
                    playClickSound(p);
                }
        );
        setItem(SOCIAL_SETTINGS_SLOT, socialSettingsButton);

        // 시스템 설정
        GuiItem systemSettingsButton = GuiItem.clickable(
                new ItemBuilder(Material.REDSTONE_BLOCK)
                        .displayName(Component.text("⚙ 시스템 설정", UnifiedColorUtil.LEGENDARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("• 확인 대화상자 설정", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("• 시스템 관련 설정", UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 설정", UnifiedColorUtil.YELLOW))
                        .build(),
                p -> {
                    SystemSettingsGui systemSettingsGui = SystemSettingsGui.create(guiManager, p);
                    guiManager.openGui(p, systemSettingsGui);
                    playClickSound(p);
                }
        );
        setItem(SYSTEM_SETTINGS_SLOT, systemSettingsButton);

        // 알림 설정
        GuiItem notificationSettingsButton = GuiItem.clickable(
                new ItemBuilder(Material.BELL)
                        .displayName(Component.text("🔔 알림 설정", UnifiedColorUtil.MYTHIC)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("• 귓말 알림 설정", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("• 초대 알림 설정", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("• 서버 공지 알림 설정", UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 설정", UnifiedColorUtil.YELLOW))
                        .build(),
                p -> {
                    NotificationSettingsGui notificationSettingsGui = NotificationSettingsGui.create(guiManager, p);
                    guiManager.openGui(p, notificationSettingsGui);
                    playClickSound(p);
                }
        );
        setItem(NOTIFICATION_SETTINGS_SLOT, notificationSettingsButton);

        // 빈 슬롯들을 투명한 유리판으로 채우기
        for (int i = 0; i < GUI_SIZE; i++) {
            if (getItem(i) == null) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
    
    @Override
    public void onClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        event.setCancelled(true);
        // GuiItem이 클릭 처리를 담당합니다
    }
}