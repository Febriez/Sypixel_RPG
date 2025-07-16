package com.febrie.rpg.gui.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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
     * @param guiManager GUI 관리자
     * @param langManager 언어 관리자
     * @param viewer GUI를 보는 플레이어
     * @param targetPlayer 프로필 대상 플레이어
     */
    public ProfileGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                      @NotNull Player viewer, @NotNull Player targetPlayer) {
        super(viewer, guiManager, langManager, GUI_SIZE, "gui.profile.player-title",
                "player", targetPlayer.getName());
        this.targetPlayer = targetPlayer;
        setupLayout();
    }
    
    /**
     * Creates a new ProfileGui for viewing own profile (편의 생성자)
     * 
     * @param guiManager GUI 관리자
     * @param langManager 언어 관리자
     * @param viewer GUI를 보는 플레이어 (자기 자신의 프로필)
     */
    public ProfileGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                      @NotNull Player viewer) {
        this(guiManager, langManager, viewer, viewer);
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("gui.profile.player-title", "player", targetPlayer.getName());
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        // 프로필에서는 메인 메뉴로 돌아감
        return new MainMenuGui(guiManager, langManager, viewer);
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
        com.febrie.rpg.player.RPGPlayer rpgPlayer = RPGMain.getPlugin()
                .getRPGPlayerManager().getOrCreatePlayer(targetPlayer);

        // Player head at top center with wallet info
        ItemBuilder headBuilder = new ItemBuilder(targetPlayer)
                .displayName(Component.text(targetPlayer.getName())
                        .color(ColorUtil.LEGENDARY)
                        .decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty())
                .addLore(trans("gui.profile.online-status",
                        "status", targetPlayer.isOnline() ?
                                transString("status.online") :
                                transString("status.offline")))
                .addLore(Component.empty());

        // Add wallet information - 모든 통화를 일관된 방식으로 표시
        com.febrie.rpg.economy.Wallet wallet = rpgPlayer.getWallet();

        // 모든 통화 타입을 순회하며 표시
        for (com.febrie.rpg.economy.CurrencyType currency : com.febrie.rpg.economy.CurrencyType.values()) {
            long balance = wallet.getBalance(currency);

            // 통화 이름과 금액을 포함한 완전한 Component 생성
            Component currencyLine = trans("currency." + currency.getId() + ".name")
                    .color(currency.getColor())  // 통화 이름에 색상 적용
                    .append(Component.text(": ", ColorUtil.WHITE))  // 콜론은 흰색으로
                    .append(Component.text(String.format("%,d", balance))
                            .color(currency.getColor()));  // 금액도 통화 색상으로

            headBuilder.addLore(currencyLine);
        }

        setItem(PLAYER_HEAD_SLOT, GuiItem.display(headBuilder.build()));

        // Level/XP info
        setItem(LEVEL_INFO_SLOT, GuiItem.display(
                ItemBuilder.of(Material.EXPERIENCE_BOTTLE)
                        .displayName(trans("gui.profile.level-info"))
                        .addLore(trans("gui.profile.level", "level", String.valueOf(rpgPlayer.getLevel())))
                        .addLore(trans("gui.profile.experience", "exp", String.valueOf(rpgPlayer.getExperience())))
                        .build()
        ));

        // Game stats info
        setItem(GAME_INFO_SLOT, GuiItem.display(
                ItemBuilder.of(Material.GOLDEN_SWORD)
                        .displayName(trans("gui.profile.game-stats"))
                        .addLore(trans("gui.profile.playtime",
                                "time", TimeUtil.formatTime(rpgPlayer.getTotalPlaytime())))
                        .addLore(trans("gui.profile.mob-kills",
                                "kills", String.valueOf(rpgPlayer.getMobsKilled())))
                        .flags(org.bukkit.inventory.ItemFlag.values())
                        .build()
        ));
    }

    /**
     * Sets up player statistics section
     */
    private void setupStatsSection() {
        // This section is handled in setupActionButtons
    }

    private void setupActionButtons() {
        com.febrie.rpg.player.RPGPlayer rpgPlayer = RPGMain.getPlugin()
                .getRPGPlayerManager().getOrCreatePlayer(targetPlayer);

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
        GuiItem questButton = GuiItem.clickable(
                ItemBuilder.of(Material.WRITTEN_BOOK)
                        .displayName(trans("gui.profile.quest-info")
                                .color(ColorUtil.UNCOMMON)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(trans("gui.profile.active-quests",
                                "count", String.valueOf(getActiveQuestCount())))
                        .addLore(trans("gui.profile.completed-quests",
                                "count", String.valueOf(getCompletedQuestCount())))
                        .addLore(Component.empty())
                        .addLore(trans("gui.profile.click-for-quests")
                                .color(ColorUtil.GRAY))
                        .flags(ItemFlag.values())
                        .build(),
                p -> {
                    if (p.equals(targetPlayer)) {
                        QuestListGui questListGui = new QuestListGui(guiManager, langManager, p);
                        guiManager.openGui(p, questListGui);
                        playSuccessSound(p);
                    } else {
                        langManager.sendMessage(p, "general.cannot-view-others-quests");
                        playErrorSound(p);
                    }
                }
        );
        setItem(QUEST_INFO_SLOT, questButton);
    }

    /**
     * 활성 퀘스트 개수 가져오기
     */
    private int getActiveQuestCount() {
        return QuestManager.getInstance()
                .getActiveQuests(targetPlayer.getUniqueId())
                .size();
    }

    /**
     * 완료된 퀘스트 개수 가져오기
     */
    private int getCompletedQuestCount() {
        return QuestManager.getInstance()
                .getCompletedQuests(targetPlayer.getUniqueId())
                .size();
    }

    /**
     * Job info button setup - shows job and level, opens talent menu on click
     */
    private void setupJobInfoButton(com.febrie.rpg.player.RPGPlayer rpgPlayer) {
        if (!rpgPlayer.hasJob()) {
            // No job - show job selection button
            GuiItem jobButton = GuiItem.clickable(
                    ItemBuilder.of(Material.ENCHANTING_TABLE)
                            .displayName(trans("items.mainmenu.job-button.name"))
                            .addLore(langManager.getComponentList(viewer, "items.mainmenu.job-button.lore"))
                            .glint(true)
                            .build(),
                    p -> {
                        if (p.equals(targetPlayer)) {
                            JobSelectionGui jobGui = new JobSelectionGui(guiManager, langManager, p, rpgPlayer);
                            guiManager.openGui(p, jobGui);
                            playSuccessSound(p);
                        } else {
                            langManager.sendMessage(p, "general.cannot-select-others-job");
                            playErrorSound(p);
                        }
                    }
            );
            setItem(JOB_INFO_SLOT, jobButton);
        } else {
            // Has job - show current job info with level, click to open talents
            String jobKey = rpgPlayer.getJob().name().toLowerCase();
            ItemBuilder jobBuilder = ItemBuilder.of(rpgPlayer.getJob().getMaterial())
                    .displayName(Component.text(rpgPlayer.getJob().getIcon() + " ")
                            .append(trans("job." + jobKey + ".name"))
                            .color(rpgPlayer.getJob().getColor())
                            .decoration(TextDecoration.BOLD, true))
                    .addLore(Component.empty())
                    .addLore(trans("gui.profile.job-level", "level", String.valueOf(rpgPlayer.getLevel())))
                    .addLore(trans("gui.profile.combat-power", "power", String.valueOf(rpgPlayer.getCombatPower())));

            // 특성 메뉴로 이동 설명 추가
            if (viewer.equals(targetPlayer)) {
                jobBuilder.addLore(Component.empty())
                        .addLore(trans("gui.talent.title").color(ColorUtil.YELLOW));
            }

            GuiItem jobInfo = GuiItem.clickable(
                    jobBuilder.build(),
                    p -> {
                        if (p.equals(targetPlayer)) {
                            // Open talent GUI
                            java.util.List<com.febrie.rpg.talent.Talent> talents = RPGMain.getPlugin()
                                    .getTalentManager().getJobMainTalents(rpgPlayer.getJob());
                            TalentGui talentGui = new TalentGui(guiManager, langManager, p, rpgPlayer, "main", talents);
                            guiManager.openGui(p, talentGui);
                            playSuccessSound(p);
                        } else {
                            langManager.sendMessage(p, "general.cannot-view-others-talents");
                            playErrorSound(p);
                        }
                    }
            );
            setItem(JOB_INFO_SLOT, jobInfo);
        }
    }

    /**
     * Stats info button setup
     */
    private void setupStatsInfoButton(com.febrie.rpg.player.RPGPlayer rpgPlayer) {
        GuiItem statsButton = GuiItem.clickable(
                ItemBuilder.of(Material.IRON_CHESTPLATE)
                        .displayName(trans("items.mainmenu.stats-button.name"))
                        .addLore(langManager.getComponentList(viewer, "items.mainmenu.stats-button.lore"))
                        .flags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES)
                        .build(),
                p -> {
                    if (!rpgPlayer.hasJob()) {
                        langManager.sendMessage(p, "messages.no-job-for-stats");
                        playErrorSound(p);
                        return;
                    }

                    if (p.equals(targetPlayer)) {
                        StatsGui statsGui = new StatsGui(guiManager, langManager, p, rpgPlayer);
                        guiManager.openGui(p, statsGui);
                        playSuccessSound(p);
                    } else {
                        langManager.sendMessage(p, "general.cannot-view-others-stats");
                        playErrorSound(p);
                    }
                }
        );
        setItem(STATS_INFO_SLOT, statsButton);
    }

    /**
     * Collection book button setup (coming soon)
     */
    private void setupCollectionButton() {
        GuiItem collectionButton = GuiItem.clickable(
                ItemBuilder.of(Material.BOOK)
                        .displayName(Component.text("수집북", ColorUtil.INFO))
                        .addLore(trans("general.coming-soon"))
                        .build(),
                p -> {
                    langManager.sendMessage(p, "general.coming-soon");
                    playClickSound(p);
                }
        );
        setItem(COLLECTION_SLOT, collectionButton);
    }

    /**
     * Pet button setup (coming soon)
     */
    private void setupPetButton() {
        GuiItem petButton = GuiItem.clickable(
                ItemBuilder.of(Material.BONE)
                        .displayName(Component.text("애완동물", ColorUtil.UNCOMMON))
                        .addLore(trans("general.coming-soon"))
                        .build(),
                p -> {
                    langManager.sendMessage(p, "general.coming-soon");
                    playClickSound(p);
                }
        );
        setItem(PET_SLOT, petButton);
    }

    /**
     * Navigation buttons setup
     */
    private void setupNavigationButtons() {
        // 하단 전체에 장식 배치
        for (int i = 45; i < 54; i++) {
            // 뒤로가기와 닫기 버튼 위치는 제외
            if (i != BACK_BUTTON_SLOT && i != CLOSE_BUTTON_SLOT) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // Back button - 항상 표시 (메인 메뉴로 돌아가기)
        setItem(BACK_BUTTON_SLOT, GuiItem.clickable(
                new ItemBuilder(Material.ARROW)
                        .displayName(trans("gui.buttons.back.name"))
                        .addLore(trans("gui.buttons.back.lore"))
                        .build(),
                p -> {
                    // 네비게이션 스택이 비어있어도 메인 메뉴로 돌아가기
                    GuiFramework backTarget = getBackTarget();
                    if (backTarget != null) {
                        guiManager.openGui(p, backTarget);
                    } else {
                        // 메인 메뉴 열기
                        MainMenuGui mainMenu = new MainMenuGui(guiManager, langManager, p);
                        guiManager.openGui(p, mainMenu);
                    }
                    playBackSound(p);
                }
        ));

        // Close button - BaseGui 표준 위치 사용
        setItem(CLOSE_BUTTON_SLOT, GuiFactory.createCloseButton(langManager, viewer));

        // User settings button (only for own profile)
        if (viewer.equals(targetPlayer)) {
            GuiItem userSettingsButton = GuiItem.clickable(
                    ItemBuilder.of(Material.COMPARATOR)
                            .displayName(Component.text("사용자 설정", ColorUtil.GRAY))
                            .addLore(Component.text("개인 설정을 변경합니다", ColorUtil.GRAY))
                            .build(),
                    p -> {
                        langManager.sendMessage(p, "general.coming-soon");
                        playClickSound(p);
                    }
            );
            setItem(USER_SETTINGS_SLOT, userSettingsButton);
        }
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
}