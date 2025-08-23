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
        super(player, guiManager, GUI_SIZE, Component.translatable("gui.settings.title"));
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
        return Component.translatable("settings.personal").color(UnifiedColorUtil.PRIMARY);
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
                ItemBuilder.of(Material.COMPARATOR, viewer.locale())
                        .displayNameTranslated("items.settings.main.title.name")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.main.title.lore")
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
                ItemBuilder.of(Material.IRON_TRAPDOOR, viewer.locale())
                        .displayNameTranslated("items.settings.gui.name")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.gui.lore1")
                        .addLoreTranslated("items.settings.gui.lore2")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.click")
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
                ItemBuilder.of(Material.GRASS_BLOCK, viewer.locale())
                        .displayNameTranslated("items.settings.ingame.name")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.ingame.lore1")
                        .addLoreTranslated("items.settings.ingame.lore2")
                        .addLoreTranslated("items.settings.ingame.lore3")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.click")
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
                ItemBuilder.of(Material.PLAYER_HEAD, viewer.locale())
                        .displayNameTranslated("items.settings.social.name")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.social.lore1")
                        .addLoreTranslated("items.settings.social.lore2")
                        .addLoreTranslated("items.settings.social.lore3")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.click")
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
                ItemBuilder.of(Material.REDSTONE_BLOCK, viewer.locale())
                        .displayNameTranslated("items.settings.system.name")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.system.lore1")
                        .addLoreTranslated("items.settings.system.lore2")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.click")
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
                ItemBuilder.of(Material.BELL, viewer.locale())
                        .displayNameTranslated("items.settings.notification.name")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.notification.lore1")
                        .addLoreTranslated("items.settings.notification.lore2")
                        .addLoreTranslated("items.settings.notification.lore3")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.click")
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