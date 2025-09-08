package com.febrie.rpg.gui.impl.settings;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.player.ProfileGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
        super(player, guiManager, GUI_SIZE, LangManager.text(LangKey.GUI_SETTINGS_TITLE, player));
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
        return new PlayerSettingsGui(guiManager, player);
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(LangKey.SETTINGS_PERSONAL, viewer).color(UnifiedColorUtil.PRIMARY);
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
                ItemBuilder.of(Material.COMPARATOR)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_MAIN_TITLE_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_MAIN_TITLE_LORE, viewer))
                        .hideAllFlags()
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
                ItemBuilder.of(Material.IRON_TRAPDOOR)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_GUI_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_GUI_LORE1, viewer))
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_GUI_LORE2, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_CLICK, viewer))
                        .hideAllFlags()
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
                ItemBuilder.of(Material.GRASS_BLOCK)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_INGAME_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_INGAME_LORE1, viewer))
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_INGAME_LORE2, viewer))
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_INGAME_LORE3, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_CLICK, viewer))
                        .hideAllFlags()
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
                ItemBuilder.of(Material.PLAYER_HEAD)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_SOCIAL_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_SOCIAL_LORE1, viewer))
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_SOCIAL_LORE2, viewer))
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_SOCIAL_LORE3, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_CLICK, viewer))
                        .hideAllFlags()
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
                ItemBuilder.of(Material.REDSTONE_BLOCK)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_SYSTEM_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_SYSTEM_LORE1, viewer))
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_SYSTEM_LORE2, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_CLICK, viewer))
                        .hideAllFlags()
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
                ItemBuilder.of(Material.BELL)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_NOTIFICATION_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_NOTIFICATION_LORE1, viewer))
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_NOTIFICATION_LORE2, viewer))
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_NOTIFICATION_LORE3, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_CLICK, viewer))
                        .hideAllFlags()
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
    
}