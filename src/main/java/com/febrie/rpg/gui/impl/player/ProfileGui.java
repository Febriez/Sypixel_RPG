package com.febrie.rpg.gui.impl.player;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.job.JobSelectionGui;
import com.febrie.rpg.gui.impl.quest.QuestListGui;
import com.febrie.rpg.gui.impl.settings.PlayerSettingsGui;
import com.febrie.rpg.gui.impl.system.MainMenuGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.util.ItemBuilder;

import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.SkullUtil;
import com.febrie.rpg.util.TimeUtil;
import com.febrie.rpg.util.UnifiedColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Player profile GUI implementation with internationalization support
 * Shows player statistics, information, and provides access to various features
 * <p>
 * 개선된 네비게이션 시스템 적용
 *
 * @author Febrie, CoffeeTory
 */
public class ProfileGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows
    private final Player targetPlayer;

    // 레이아웃 상수
    private static final int PLAYER_HEAD_SLOT = 4; // 상단 중앙에 플레이어 머리

    // 스탯 표시 슬롯
    private static final int LEVEL_INFO_SLOT = 20;
    private static final int GAME_INFO_SLOT = 24;

    // 프로필 메뉴 버튼 슬롯 (중앙 배치)
    private static final int JOB_INFO_SLOT = 29;
    private static final int QUEST_INFO_SLOT = 30;
    private static final int STATS_INFO_SLOT = 31;
    private static final int COLLECTION_SLOT = 32;
    private static final int PET_SLOT = 33;

    // 하단 네비게이션 버튼 슬롯
    private static final int USER_SETTINGS_SLOT = 50;  // 중앙에서 오른쪽 한 칸

    /**
     * Creates a new ProfileGui for viewing another player's profile
     *
     * @param guiManager   GUI 관리자
     * @param langManager  언어 관리자
     * @param viewer       GUI를 보는 플레이어
     * @param targetPlayer 프로필 대상 플레이어
     */
    protected ProfileGui(@NotNull GuiManager guiManager, @NotNull Player viewer, @NotNull Player targetPlayer) {
        super(viewer, guiManager, GUI_SIZE, LangManager.text(LangKey.GUI_PROFILE_PLAYER_TITLE, viewer, Component.text(targetPlayer.getName())));
        this.targetPlayer = targetPlayer;
    }

    /**
     * Creates a new ProfileGui for viewing own profile (편의 생성자)
     *
     * @param guiManager  GUI 관리자
     * @param langManager 언어 관리자
     * @param viewer      GUI를 보는 플레이어 (자기 자신의 프로필)
     */
    protected ProfileGui(@NotNull GuiManager guiManager, @NotNull Player viewer) {
        this(guiManager, viewer, viewer);
    }

    /**
     * Factory method to create ProfileGui for viewing another player's profile
     */
    public static ProfileGui create(@NotNull GuiManager guiManager, @NotNull Player viewer, @NotNull Player targetPlayer) {
        return new ProfileGui(guiManager, viewer, targetPlayer);
    }

    /**
     * Factory method to create ProfileGui for viewing own profile
     */
    public static ProfileGui create(@NotNull GuiManager guiManager, @NotNull Player viewer) {
        return create(guiManager, viewer, viewer);
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(LangKey.GUI_PROFILE_PLAYER_TITLE, viewer, Component.text(targetPlayer.getName()));
    }

    @Override
    protected GuiFramework getBackTarget() {
        // 프로필에서는 메인 메뉴로 돌아감
        return MainMenuGui.create(guiManager, viewer);
    }

    /**
     * Gets the target player whose profile this GUI shows
     */
    public Player getTargetPlayer() {
        return targetPlayer;
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupPlayerInfo();
        setupStatsSection();
        setupActionButtons();
    }

    /**
     * Sets up decorative elements and borders
     */
    private void setupDecorations() {
        // 상단 테두리 (플레이어 머리 슬롯 제외)
        for (int i = 0; i < 9; i++) {
            if (i != PLAYER_HEAD_SLOT) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 중간 구분선 제거 - 버튼들만 표시

        // 좌우 테두리
        for (int row = 1; row < 5; row++) {
            setItem(row * 9, GuiFactory.createDecoration());
            setItem(row * 9 + 8, GuiFactory.createDecoration());
        }
    }

    /**
     * Sets up player information display
     */
    private void setupPlayerInfo() {
        com.febrie.rpg.player.RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager()
                .getOrCreatePlayer(targetPlayer);

        // Player head at top center with wallet info
        ItemBuilder headBuilder = ItemBuilder.from(SkullUtil.getPlayerHead(targetPlayer.getUniqueId().toString()))
                .displayName(Component.text(targetPlayer.getName())
                        .color(UnifiedColorUtil.LEGENDARY).decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_PROFILE_ONLINE_STATUS, viewer, targetPlayer.isOnline() ? LangManager.text(LangKey.STATUS_ONLINE, viewer) : LangManager.text(LangKey.STATUS_OFFLINE, viewer)))
                .addLore(Component.empty());

        // Add wallet information - 모든 통화를 일관된 방식으로 표시
        com.febrie.rpg.economy.Wallet wallet = rpgPlayer.getWallet();

        // 모든 통화 타입을 순회하며 표시
        for (com.febrie.rpg.economy.CurrencyType currency : com.febrie.rpg.economy.CurrencyType.values()) {
            long balance = wallet.getBalance(currency);

            // 통화 이름과 금액을 포함한 완전한 Component 생성
            Component currencyLine = LangManager.text(LangKey.fromString("CURRENCY_" + currency.getId().toUpperCase() + "_NAME"), viewer)
                    .color(currency.getColor())  // 통화 이름에 색상 적용
                    .append(Component.text(": ", UnifiedColorUtil.WHITE))  // 콜론은 흰색으로
                    .append(Component.text(String.format("%,d", balance)).color(currency.getColor()));  // 금액도 통화 색상으로

            headBuilder.addLore(currencyLine);
        }

        setItem(PLAYER_HEAD_SLOT, GuiItem.display(headBuilder.build()));

        // Level/XP info - locale 적용 예시
        setItem(LEVEL_INFO_SLOT, GuiItem.display(ItemBuilder.of(Material.EXPERIENCE_BOTTLE)
                .displayName(LangManager.text(LangKey.ITEMS_PROFILE_LEVEL_INFO_NAME, viewer))
                .addLore(LangManager.text(LangKey.GUI_PROFILE_LEVEL, viewer, String.valueOf(rpgPlayer.getLevel())))
                .addLore(LangManager.text(LangKey.GUI_PROFILE_EXPERIENCE, viewer, String.valueOf(rpgPlayer.getExperience())))
                .build()));

        // Game stats info - locale 적용 예시
        setItem(GAME_INFO_SLOT, GuiItem.display(ItemBuilder.of(Material.GOLDEN_SWORD)
                .displayName(LangManager.text(LangKey.ITEMS_PROFILE_GAME_STATS_NAME, viewer))
                .addLore(LangManager.text(LangKey.GUI_PROFILE_PLAYTIME, viewer, TimeUtil.formatTime(rpgPlayer.getTotalPlaytime())))
                .addLore(LangManager.text(LangKey.GUI_PROFILE_MOB_KILLS, viewer, String.valueOf(rpgPlayer.getMobsKilled())))
                .flags(org.bukkit.inventory.ItemFlag.values()).build()));
    }

    /**
     * Sets up player statistics section
     */
    private void setupStatsSection() {
        // TODO: Implement stats section display
        // This section is currently handled in setupActionButtons
    }

    private void setupActionButtons() {
        com.febrie.rpg.player.RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager()
                .getOrCreatePlayer(targetPlayer);

        // Job info button - click to open talents menu
        setupJobInfoButton(rpgPlayer);

        // Quest info button - 새로 추가
        setupQuestInfoButton();

        // Stats info button
        setupStatsInfoButton(rpgPlayer);

        // Collection book button (coming soon)
        setupCollectionButton();

        // Pet button (coming soon)
        setupPetButton();

        // Navigation buttons
        setupNavigationButtons();
    }

    /**
     * Quest info button setup - 퀘스트 목록으로 이동
     */
    private void setupQuestInfoButton() {
        GuiItem questButton = GuiItem.clickable(ItemBuilder.of(Material.WRITTEN_BOOK)
                .displayName(LangManager.text(LangKey.ITEMS_PROFILE_QUEST_INFO_NAME, viewer))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_PROFILE_ACTIVE_QUESTS, viewer, String.valueOf(getActiveQuestCount())))
                .addLore(LangManager.text(LangKey.GUI_PROFILE_COMPLETED_QUESTS, viewer, String.valueOf(getCompletedQuestCount())))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.ITEMS_PROFILE_QUEST_INFO_CLICK_LORE, viewer))
                .flags(ItemFlag.values()).build(), p -> {
            if (p.equals(targetPlayer)) {
                QuestListGui questListGui = QuestListGui.create(guiManager, p);
                guiManager.openGui(p, questListGui);
                playSuccessSound(p);
            } else {
                p.sendMessage(LangManager.text(LangKey.GENERAL_CANNOT_VIEW_OTHERS_QUESTS, p));
                playErrorSound(p);
            }
        });
        setItem(QUEST_INFO_SLOT, questButton);
    }

    /**
     * 활성 퀘스트 개수 가져오기
     */
    private int getActiveQuestCount() {
        return QuestManager.getInstance().getActiveQuests(targetPlayer.getUniqueId()).size();
    }

    /**
     * 완료된 퀘스트 개수 가져오기
     */
    private int getCompletedQuestCount() {
        return QuestManager.getInstance().getCompletedQuests(targetPlayer.getUniqueId()).size();
    }

    /**
     * Job info button setup - shows job and level, opens talent menu on click
     */
    private void setupJobInfoButton(com.febrie.rpg.player.RPGPlayer rpgPlayer) {
        if (!rpgPlayer.hasJob()) {
            // No job - show job selection button
            GuiItem jobButton = GuiItem.clickable(ItemBuilder.of(Material.ENCHANTING_TABLE)
                    .displayName(LangManager.text(LangKey.ITEMS_MAINMENU_JOB_BUTTON_NAME, viewer))
                    .lore(LangManager.list(LangKey.ITEMS_MAINMENU_JOB_BUTTON_LORE, viewer))
                    .glint(true).build(), p -> {
                if (p.equals(targetPlayer)) {
                    JobSelectionGui jobGui = JobSelectionGui.create(guiManager, p, rpgPlayer);
                    guiManager.openGui(p, jobGui);
                    playSuccessSound(p);
                } else {
                    p.sendMessage(LangManager.text(LangKey.GENERAL_CANNOT_SELECT_OTHERS_JOB, p));
                    playErrorSound(p);
                }
            });
            setItem(JOB_INFO_SLOT, jobButton);
        } else {
            // Has job - show current job info with level, click to open talents
            JobType job = rpgPlayer.getJob();
            if (job == null) {
                return; // Safety check
            }
            String jobKey = job.name().toLowerCase();
            ItemBuilder jobBuilder = ItemBuilder.of(job.getMaterial())
                    .displayName(Component.text(job.getIcon() + " ")
                            .append(LangManager.text(LangKey.fromString("JOB_" + jobKey.toUpperCase() + "_NAME"), viewer))
                            .color(job.getColor())
                            .decoration(TextDecoration.BOLD, true))
                    .addLore(Component.empty())
                    .addLore(LangManager.text(LangKey.GUI_PROFILE_JOB_LEVEL, viewer, String.valueOf(rpgPlayer.getLevel())))
                    .addLore(LangManager.text(LangKey.GUI_PROFILE_COMBAT_POWER, viewer, String.valueOf(rpgPlayer.getCombatPower())));

            // 특성 메뉴로 이동 설명 추가
            if (viewer.equals(targetPlayer)) {
                jobBuilder.addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_PROFILE_CLICK_TO_TALENTS, viewer));
            }

            GuiItem jobInfo = GuiItem.clickable(jobBuilder.build(), p -> {
                if (p.equals(targetPlayer)) {
                    // Open talent GUI
                    List<com.febrie.rpg.talent.Talent> talents = RPGMain.getPlugin().getTalentManager()
                            .getJobMainTalents(rpgPlayer.getJob());
                    TalentGui talentGui = TalentGui.create(guiManager, p, rpgPlayer, "main", talents);
                    guiManager.openGui(p, talentGui);
                    playSuccessSound(p);
                } else {
                    p.sendMessage(LangManager.text(LangKey.GENERAL_CANNOT_VIEW_OTHERS_TALENTS, p));
                    playErrorSound(p);
                }
            });
            setItem(JOB_INFO_SLOT, jobInfo);
        }
    }

    /**
     * Stats info button setup
     */
    private void setupStatsInfoButton(com.febrie.rpg.player.RPGPlayer rpgPlayer) {
        GuiItem statsButton = GuiItem.clickable(ItemBuilder.of(Material.IRON_CHESTPLATE)
                .displayName(LangManager.text(LangKey.ITEMS_MAINMENU_STATS_BUTTON_NAME, viewer))
                .lore(LangManager.list(LangKey.ITEMS_MAINMENU_STATS_BUTTON_LORE, viewer))
                .flags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES).build(), p -> {
            if (!rpgPlayer.hasJob()) {
                p.sendMessage(LangManager.text(LangKey.MESSAGES_NO_JOB_FOR_STATS, p));
                playErrorSound(p);
                return;
            }

            if (p.equals(targetPlayer)) {
                StatsGui statsGui = StatsGui.create(guiManager, p, rpgPlayer);
                guiManager.openGui(p, statsGui);
                playSuccessSound(p);
            } else {
                p.sendMessage(LangManager.text(LangKey.GENERAL_CANNOT_VIEW_OTHERS_STATS, p));
                playErrorSound(p);
            }
        });
        setItem(STATS_INFO_SLOT, statsButton);
    }

    /**
     * Collection book button setup (coming soon)
     */
    private void setupCollectionButton() {
        GuiItem collectionButton = GuiItem.clickable(ItemBuilder.of(Material.BOOK)
                .displayName(LangManager.text(LangKey.ITEMS_PROFILE_COLLECTION_BOOK_NAME, viewer))
                .lore(LangManager.list(LangKey.GENERAL_COMING_SOON, viewer)).build(), p -> {
            p.sendMessage(LangManager.text(LangKey.GENERAL_COMING_SOON, p));
            playClickSound(p);
        });
        setItem(COLLECTION_SLOT, collectionButton);
    }

    /**
     * Pet button setup (coming soon)
     */
    private void setupPetButton() {
        GuiItem petButton = GuiItem.clickable(ItemBuilder.of(Material.BONE)
                .displayName(LangManager.text(LangKey.ITEMS_PROFILE_PETS_NAME, viewer))
                .lore(LangManager.list(LangKey.GENERAL_COMING_SOON, viewer)).build(), p -> {
            p.sendMessage(LangManager.text(LangKey.GENERAL_COMING_SOON, p));
            playClickSound(p);
        });
        setItem(PET_SLOT, petButton);
    }

    /**
     * Navigation buttons setup
     */
    private void setupNavigationButtons() {
        // 하단 전체에 장식 배치
        for (int i = 45; i < 54; i++) {
            // 뒤로가기와 닫기 버튼 위치는 제외
            if (i != getBackButtonSlot() && i != getCloseButtonSlot()) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // Back button - 항상 표시 (메인 메뉴로 돌아가기)
        setItem(getBackButtonSlot(), GuiItem.clickable(ItemBuilder.of(Material.ARROW).displayName(LangManager.text(LangKey.GUI_BUTTONS_BACK_NAME, viewer))
                .lore(LangManager.list(LangKey.GUI_BUTTONS_BACK_LORE, viewer)).build(), p -> {
            // 네비게이션 스택이 비어있어도 메인 메뉴로 돌아가기
            GuiFramework backTarget = getBackTarget();
            if (backTarget != null) {
                guiManager.openGui(p, backTarget);
            } else {
                // 메인 메뉴 열기
                MainMenuGui mainMenu = MainMenuGui.create(guiManager, p);
                guiManager.openGui(p, mainMenu);
            }
            playBackSound(p);
        }));

        // Close button - BaseGui 표준 위치 사용
        setItem(getCloseButtonSlot(), GuiFactory.createCloseButton(viewer));

        // User settings button (only for own profile)
        if (viewer.equals(targetPlayer)) {
            GuiItem userSettingsButton = GuiItem.clickable(ItemBuilder.of(Material.COMPARATOR)
                    .displayName(LangManager.text(LangKey.ITEMS_PROFILE_USER_SETTINGS_NAME, viewer))
                    .lore(LangManager.list(LangKey.ITEMS_PROFILE_USER_SETTINGS_LORE, viewer)).build(), p -> {
                PlayerSettingsGui settingsGui = PlayerSettingsGui.create(guiManager, p);
                guiManager.openGui(p, settingsGui);
                playClickSound(p);
            });
            setItem(USER_SETTINGS_SLOT, userSettingsButton);
        }
    }
}