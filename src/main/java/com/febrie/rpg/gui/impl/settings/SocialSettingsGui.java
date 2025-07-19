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

    private SocialSettingsGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                            @NotNull Player player) {
        super(player, guiManager, langManager, GUI_SIZE, "gui.social-settings.title");
    }

    /**
     * SocialSettingsGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param player 플레이어
     * @return 초기화된 SocialSettingsGui 인스턴스
     */
    public static SocialSettingsGui create(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                                          @NotNull Player player) {
        SocialSettingsGui gui = new SocialSettingsGui(guiManager, langManager, player);
        gui.setupLayout();
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.text("소셜 설정", ColorUtil.EPIC);
    }

    @Override
    protected GuiFramework getBackTarget() {
        return PlayerSettingsGui.create(guiManager, langManager, viewer);
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupSettingControls();
        setupStandardNavigation(true, true);
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
                new ItemBuilder(Material.PLAYER_HEAD)
                        .displayName(Component.text("👥 소셜 설정", ColorUtil.EPIC)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("소셜 기능 관련 설정을 변경합니다", ColorUtil.GRAY))
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
                new ItemBuilder(enabled ? Material.EMERALD : Material.REDSTONE)
                        .displayName(Component.text("👤 친구 요청 받기", ColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("상태: " + (enabled ? "활성화" : "비활성화"), 
                                enabled ? ColorUtil.SUCCESS : ColorUtil.ERROR))
                        .addLore(Component.empty())
                        .addLore(Component.text("다른 플레이어로부터", ColorUtil.GRAY))
                        .addLore(Component.text("친구 요청을 받을지 설정합니다", ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 " + (enabled ? "비활성화" : "활성화"), ColorUtil.YELLOW))
                        .build(),
                p -> {
                    settings.setFriendRequestsEnabled(!enabled);
                    updateFriendRequestsToggle(settings);
                    playClickSound(p);
                    langManager.sendMessage(p, "친구 요청 받기가 " + (settings.isFriendRequestsEnabled() ? "활성화" : "비활성화") + "되었습니다");
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
                new ItemBuilder(enabled ? Material.GOLD_INGOT : Material.IRON_INGOT)
                        .displayName(Component.text("🏰 길드 초대 받기", ColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("상태: " + (enabled ? "활성화" : "비활성화"), 
                                enabled ? ColorUtil.SUCCESS : ColorUtil.ERROR))
                        .addLore(Component.empty())
                        .addLore(Component.text("길드로부터 초대를", ColorUtil.GRAY))
                        .addLore(Component.text("받을지 설정합니다", ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("※ 길드 시스템은 준비중입니다", ColorUtil.YELLOW))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 " + (enabled ? "비활성화" : "활성화"), ColorUtil.YELLOW))
                        .build(),
                p -> {
                    settings.setGuildInvitesEnabled(!enabled);
                    updateGuildInvitesToggle(settings);
                    playClickSound(p);
                    langManager.sendMessage(p, "길드 초대 받기가 " + (settings.isGuildInvitesEnabled() ? "활성화" : "비활성화") + "되었습니다");
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

        String modeDisplay = switch (mode) {
            case "ALL" -> "전체";
            case "FRIENDS_ONLY" -> "친구만";
            case "BLOCKED" -> "차단";
            default -> "알 수 없음";
        };

        String modeDescription = switch (mode) {
            case "ALL" -> "모든 플레이어로부터 귓말을 받습니다";
            case "FRIENDS_ONLY" -> "친구로부터만 귓말을 받습니다";
            case "BLOCKED" -> "모든 귓말을 차단합니다";
            default -> "알 수 없는 모드입니다";
        };
        
        GuiItem whisperModeToggle = GuiItem.clickable(
                new ItemBuilder(material)
                        .displayName(Component.text("💬 귓말 모드", ColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("현재 모드: " + modeDisplay, ColorUtil.WHITE))
                        .addLore(Component.text(modeDescription, ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 다음 모드로 변경:", ColorUtil.YELLOW))
                        .addLore(Component.text("전체 → 친구만 → 차단 → 전체", ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("※ 귓말 시스템은 준비중입니다", ColorUtil.YELLOW))
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
                    
                    String newModeDisplay = switch (nextMode) {
                        case "ALL" -> "전체";
                        case "FRIENDS_ONLY" -> "친구만";
                        case "BLOCKED" -> "차단";
                        default -> "알 수 없음";
                    };
                    
                    langManager.sendMessage(p, "귓말 모드가 '" + newModeDisplay + "'로 변경되었습니다");
                }
        );
        setItem(WHISPER_MODE_SLOT, whisperModeToggle);
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
}