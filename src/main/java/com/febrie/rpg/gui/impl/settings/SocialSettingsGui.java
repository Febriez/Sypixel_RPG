package com.febrie.rpg.gui.impl.settings;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.PlayerSettings;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.lang.GuiLangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 소셜 설정 GUI
 * 친구 요청, 길드 초대, 귓말 설정
 *
 * @author Febrie
 */
public class SocialSettingsGui extends BaseGui {

    private static final int GUI_SIZE = 45; // 5 rows

    // 설정 버튼 슬롯
    private static final int FRIEND_REQUESTS_SLOT = 20;
    private static final int GUILD_INVITES_SLOT = 22;
    private static final int WHISPER_MODE_SLOT = 24;

    // 타이틀 슬롯
    private static final int TITLE_SLOT = 4;

    private SocialSettingsGui(@NotNull GuiManager guiManager,
                            @NotNull Player player) {
        super(player, guiManager, GUI_SIZE, LangManager.text(GuiLangKey.GUI_SOCIAL_SETTINGS_TITLE));
    }

    /**
     * SocialSettingsGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param player 플레이어
     * @return 초기화된 SocialSettingsGui 인스턴스
     */
    public static SocialSettingsGui create(@NotNull GuiManager guiManager,
                                          @NotNull Player player) {
        return new SocialSettingsGui(guiManager, player);
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(GuiLangKey.GUI_SOCIAL_SETTINGS_TITLE);
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
                ItemBuilder.of(Material.PLAYER_HEAD)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_SOCIAL_SETTINGS_TITLE_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_SOCIAL_SETTINGS_TITLE_LORE, viewer))
                        .hideAllFlags()
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

        setupFriendRequestsToggle(settings);
        setupGuildInvitesToggle(settings);
        setupWhisperModeToggle(settings);

        // 빈 슬롯들을 투명한 유리판으로 채우기
        for (int i = 0; i < GUI_SIZE; i++) {
            if (getItem(i) == null) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 친구 요청 토글 설정
     */
    private void setupFriendRequestsToggle(PlayerSettings settings) {
        updateFriendRequestsToggle(settings);
    }

    /**
     * 길드 초대 토글 설정
     */
    private void setupGuildInvitesToggle(PlayerSettings settings) {
        updateGuildInvitesToggle(settings);
    }

    /**
     * 귓말 모드 토글 설정
     */
    private void setupWhisperModeToggle(PlayerSettings settings) {
        updateWhisperModeToggle(settings);
    }

    /**
     * 친구 요청 토글 업데이트
     */
    private void updateFriendRequestsToggle(PlayerSettings settings) {
        boolean enabled = settings.isFriendRequestsEnabled();
        
        GuiItem friendRequestsToggle = GuiItem.clickable(
                ItemBuilder.of(enabled ? Material.EMERALD : Material.REDSTONE)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_SOCIAL_SETTINGS_FRIEND_REQUESTS_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_SOCIAL_SETTINGS_STATUS, viewer,
                                Component.translatable(enabled ? "status.enabled" : "status.disabled")
                                .color(enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR)))
                        .addLore(Component.empty())
                        .addLore(LangManager.list(LangKey.ITEMS_SETTINGS_SOCIAL_SETTINGS_FRIEND_REQUESTS_DESC, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_SOCIAL_SETTINGS_CLICK_TO_TOGGLE, viewer,
                                Component.translatable(enabled ? "action.disable" : "action.enable")))
                        .hideAllFlags()
                        .build(),
                p -> {
                    settings.setFriendRequestsEnabled(!enabled);
                    updateFriendRequestsToggle(settings);
                    playClickSound(p);
                    p.sendMessage(LangManager.text(LangKey.GUI_SOCIAL_SETTINGS_FRIEND_REQUESTS_TOGGLED, p,
                            Component.translatable(settings.isFriendRequestsEnabled() ? "status.enabled" : "status.disabled")));
                }
        );
        setItem(FRIEND_REQUESTS_SLOT, friendRequestsToggle);
    }

    /**
     * 길드 초대 토글 업데이트
     */
    private void updateGuildInvitesToggle(PlayerSettings settings) {
        boolean enabled = settings.isGuildInvitesEnabled();
        
        GuiItem guildInvitesToggle = GuiItem.clickable(
                ItemBuilder.of(enabled ? Material.GOLD_INGOT : Material.IRON_INGOT)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_SOCIAL_SETTINGS_GUILD_INVITES_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_SOCIAL_SETTINGS_STATUS, viewer,
                                Component.translatable(enabled ? "status.enabled" : "status.disabled")
                                .color(enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR)))
                        .addLore(Component.empty())
                        .addLore(LangManager.list(LangKey.ITEMS_SETTINGS_SOCIAL_SETTINGS_GUILD_INVITES_DESC, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_SOCIAL_SETTINGS_GUILD_INVITES_NOTE, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_SOCIAL_SETTINGS_CLICK_TO_TOGGLE, viewer,
                                Component.translatable(enabled ? "action.disable" : "action.enable")))
                        .hideAllFlags()
                        .build(),
                p -> {
                    settings.setGuildInvitesEnabled(!enabled);
                    updateGuildInvitesToggle(settings);
                    playClickSound(p);
                    p.sendMessage(LangManager.text(LangKey.GUI_SOCIAL_SETTINGS_GUILD_INVITES_TOGGLED, p,
                            Component.translatable(settings.isGuildInvitesEnabled() ? "status.enabled" : "status.disabled")));
                }
        );
        setItem(GUILD_INVITES_SLOT, guildInvitesToggle);
    }

    /**
     * 귓말 모드 토글 업데이트
     */
    private void updateWhisperModeToggle(PlayerSettings settings) {
        String mode = settings.getWhisperMode();
        
        Material material = switch (mode) {
            case "ALL" -> Material.LIME_DYE;
            case "FRIENDS_ONLY" -> Material.YELLOW_DYE;
            case "BLOCKED" -> Material.RED_DYE;
            default -> Material.WHITE_DYE;
        };

        Component modeDisplay = switch (mode) {
            case "ALL" -> Component.translatable("whisper.mode.all");
            case "FRIENDS_ONLY" -> Component.translatable("whisper.mode.friends-only");
            case "BLOCKED" -> Component.translatable("whisper.mode.blocked");
            default -> Component.translatable("whisper.mode.unknown");
        };

        Component modeDescription = switch (mode) {
            case "ALL" -> Component.translatable("whisper.mode.all.desc");
            case "FRIENDS_ONLY" -> Component.translatable("whisper.mode.friends-only.desc");
            case "BLOCKED" -> Component.translatable("whisper.mode.blocked.desc");
            default -> Component.translatable("whisper.mode.unknown.desc");
        };
        
        GuiItem whisperModeToggle = GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_SOCIAL_SETTINGS_WHISPER_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_SOCIAL_SETTINGS_CURRENT_MODE, viewer, modeDisplay))
                        .addLore(modeDescription.color(UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_SOCIAL_SETTINGS_WHISPER_CLICK_HINT, viewer))
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_SOCIAL_SETTINGS_WHISPER_MODE_CYCLE, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_SOCIAL_SETTINGS_WHISPER_NOTE, viewer))
                        .hideAllFlags()
                        .build(),
                p -> {
                    String nextMode = switch (mode) {
                        case "ALL" -> "FRIENDS_ONLY";
                        case "FRIENDS_ONLY" -> "BLOCKED";
                        case "BLOCKED" -> "ALL";
                        default -> "ALL";
                    };
                    
                    settings.setWhisperMode(nextMode);
                    updateWhisperModeToggle(settings);
                    playClickSound(p);
                    
                    Component newModeDisplay = switch (nextMode) {
                        case "ALL" -> Component.translatable("whisper.mode.all");
                        case "FRIENDS_ONLY" -> Component.translatable("whisper.mode.friends-only");
                        case "BLOCKED" -> Component.translatable("whisper.mode.blocked");
                        default -> Component.translatable("whisper.mode.unknown");
                    };
                    
                    p.sendMessage(LangManager.text(LangKey.GUI_SOCIAL_SETTINGS_WHISPER_MODE_CHANGED, p, newModeDisplay));
                }
        );
        setItem(WHISPER_MODE_SLOT, whisperModeToggle);
    }
    
}