package com.febrie.rpg.gui.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.ScrollableGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.gui.util.GuiUtility;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.stat.Stat;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 스탯 관리 GUI
 * 스탯을 확인하고 포인트를 사용하여 스탯을 올릴 수 있음
 *
 * @author Febrie, CoffeeTory
 */
public class StatsGui extends ScrollableGui {

    private final GuiManager guiManager;
    private final LangManager langManager;
    private final RPGPlayer rpgPlayer;
    private final Map<Integer, GuiItem> items = new HashMap<>();

    public StatsGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                    @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer) {
        super(viewer);
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.rpgPlayer = rpgPlayer;
        this.inventory = Bukkit.createInventory(this, GUI_SIZE,
                langManager.getComponent(viewer, "gui.stats.title"));

        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return langManager.getComponent(viewer, "gui.stats.title");
    }

    @Override
    public int getSize() {
        return GUI_SIZE;
    }

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void refresh() {
        inventory.clear();
        items.clear();
        setupLayout();
    }

    @Override
    public @NotNull org.bukkit.inventory.Inventory getInventory() {
        return inventory;
    }

    @Override
    protected List<GuiItem> getScrollableItems() {
        List<GuiItem> statItems = new ArrayList<>();

        // 모든 스탯을 GuiItem으로 변환
        for (Stat stat : Stat.getAllStats().values()) {
            statItems.add(createStatItem(stat));
        }

        return statItems;
    }

    @Override
    protected void handleNonScrollClick(@NotNull InventoryClickEvent event,
                                        @NotNull Player player, int slot,
                                        @NotNull ClickType click) {
        GuiItem item = items.get(slot);
        if (item != null && item.hasActions()) {
            item.executeAction(player, click);
        }
    }

    /**
     * GUI 레이아웃 설정
     */
    private void setupLayout() {
        // 배경 설정
        setupBackground();

        // 플레이어 정보 표시
        setupPlayerInfo();

        // 스크롤 가능한 스탯 목록
        setupScrollableStats();

        // 스크롤 버튼
        setupScrollButtons();

        // 네비게이션 버튼
        setupNavigationButtons();
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        // 상단 테두리
        for (int i = 0; i < 9; i++) {
            if (i != 4 && i != SCROLL_UP_SLOT) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 하단 테두리
        for (int i = 45; i < 54; i++) {
            if (i != 49) { // 스탯 포인트 정보 슬롯 제외
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 좌우 테두리
        for (int row = 1; row < 5; row++) {
            setItem(row * 9, GuiFactory.createDecoration()); // 좌측
            if (row != 2 && row != 4) { // 스크롤바 위치 제외
                setItem(row * 9 + 8, GuiFactory.createDecoration()); // 우측
            }
        }

        // SCROLL_DOWN_SLOT (44)는 하단 테두리 범위 밖이므로 별도 처리
        // 44번 슬롯은 스크롤 다운 버튼용으로 비워둠
    }

    /**
     * 플레이어 정보 표시
     */
    private void setupPlayerInfo() {
        if (rpgPlayer.getJob() == null) {
            new JobSelectionGui(guiManager, langManager, viewer, rpgPlayer).open(viewer);
            return;
        }

        // 플레이어 머리
        GuiItem playerHead = GuiItem.display(
                ItemBuilder.of(Material.PLAYER_HEAD)
                        .displayName(Component.text(viewer.getName(), ColorUtil.LEGENDARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(trans("gui.talent.level", "level", String.valueOf(rpgPlayer.getLevel())))
                        .addLore(trans("gui.talent.job", "job",
                                trans("job." + rpgPlayer.getJob().name().toLowerCase() + ".name").toString()))
                        .addLore(trans("gui.stats.combat-power", "power", String.valueOf(rpgPlayer.getCombatPower())))
                        .build()
        );
        setItem(4, playerHead);

        // 스탯 포인트 정보
        GuiItem statPointInfo = GuiItem.display(
                ItemBuilder.of(Material.EXPERIENCE_BOTTLE)
                        .displayName(trans("gui.stats.stat-points"))
                        .addLore(trans("gui.stats.available-points",
                                "points", String.valueOf(rpgPlayer.getStatPoints())))
                        .addLore(Component.empty())
                        .addLore(trans("gui.stats.points-per-level"))
                        .addLore(trans("gui.stats.click-to-use"))
                        .build()
        );
        setItem(49, statPointInfo);
    }

    /**
     * 스탯 아이템 생성
     */
    private GuiItem createStatItem(@NotNull Stat stat) {
        Stat.StatHolder stats = rpgPlayer.getStats();
        int baseStat = stats.getBaseStat(stat);
        int bonusStat = stats.getBonusStat(stat);
        int totalStat = stats.getTotalStat(stat);

        // 스탯 이름 - Stat에 언어별 이름이 하드코딩되어 있으므로 그대로 사용
        boolean isKorean = langManager.getMessage(viewer, "general.language-code").equals("ko_KR");

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(trans("gui.stats.current-stats"));
        lore.add(trans("gui.stats.base-stat", "value", String.valueOf(baseStat)));

        if (bonusStat > 0) {
            lore.add(trans("gui.stats.bonus-stat", "value", String.valueOf(bonusStat)));
            lore.add(trans("gui.stats.total-stat", "value", String.valueOf(totalStat)));
        }

        lore.add(Component.empty());
        lore.add(stat.getDescription(isKorean));
        lore.add(Component.empty());

        if (rpgPlayer.getStatPoints() > 0 && baseStat < stat.getMaxValue()) {
            lore.add(trans("gui.stats.click-add-1"));
            lore.add(trans("gui.stats.click-add-5"));
            lore.add(trans("gui.stats.click-add-10"));
        } else if (baseStat >= stat.getMaxValue()) {
            lore.add(trans("gui.stats.stat-maxed"));
        } else {
            lore.add(trans("gui.stats.insufficient-points"));
        }

        return GuiItem.of(
                ItemBuilder.of(stat.getIcon())
                        .displayName(stat.getDisplayName(isKorean)
                                .decoration(TextDecoration.BOLD, true))
                        .lore(lore)
                        .flags(ItemFlag.values())
                        .build()
        ).onClick(ClickType.LEFT, (player, click) -> {
            if (rpgPlayer.useStatPoint(stat, 1)) {
                refresh();
                playSuccessSound(player);
            } else {
                playErrorSound(player);
            }
        }).onClick(ClickType.SHIFT_LEFT, (player, click) -> {
            int amount = Math.min(5, rpgPlayer.getStatPoints());
            if (amount > 0 && rpgPlayer.useStatPoint(stat, amount)) {
                refresh();
                playSuccessSound(player);
            } else {
                playErrorSound(player);
            }
        }).onClick(ClickType.RIGHT, (player, click) -> {
            int amount = Math.min(10, rpgPlayer.getStatPoints());
            if (amount > 0 && rpgPlayer.useStatPoint(stat, amount)) {
                refresh();
                playSuccessSound(player);
            } else {
                playErrorSound(player);
            }
        });
    }

    /**
     * 스크롤 가능한 스탯 목록 설정
     */
    private void setupScrollableStats() {
        // 부모 클래스의 공통 메소드 사용
        setupScrollableArea(inventory, items, this::setItem);
    }

    /**
     * 스크롤 버튼 설정
     */
    private void setupScrollButtons() {
        setItem(SCROLL_UP_SLOT, createScrollUpButton());
        setItem(SCROLL_DOWN_SLOT, createScrollDownButton());

        // 스크롤바
        setItem(SCROLL_BAR_START, createScrollBarItem(SCROLL_BAR_START));
        setItem(26, createScrollBarItem(26));
        setItem(SCROLL_BAR_END, createScrollBarItem(SCROLL_BAR_END));
    }

    /**
     * 네비게이션 버튼 설정
     */
    private void setupNavigationButtons() {
        if (guiManager == null) {
            throw new IllegalArgumentException("GuiManager cannot be null");
        }

        if (rpgPlayer == null) {
            throw new IllegalArgumentException("rpgPlayer cannot be null");
        }

        // 뒤로가기 버튼
        setItem(45, GuiFactory.createBackButton(guiManager, langManager, viewer));

        // 새로고침 버튼
        setItem(46, GuiFactory.createRefreshButton(_ -> refresh(), langManager, viewer));

        // 특성 페이지로 가기 버튼
        GuiItem talentButton = GuiItem.clickable(
                ItemBuilder.of(Material.ENCHANTED_BOOK)
                        .displayName(trans("gui.talent.title"))
                        .addLore(trans("gui.stats.click-talents"))
                        .build(),
                player -> new TalentGui(guiManager, langManager, player, rpgPlayer, null, RPGMain.getPlugin()
                        .getTalentManager().getJobMainTalents(rpgPlayer.getJob())).open(player)
        );
        setItem(52, talentButton);

        // 닫기 버튼
        setItem(53, GuiFactory.createCloseButton(langManager, viewer));
    }

    /**
     * 아이템 설정 - GuiUtility.setItem 사용
     */
    private void setItem(int slot, @NotNull GuiItem item) {
        GuiUtility.setItem(slot, item, items, inventory);
    }

    /**
     * Helper methods from ScrollableGui for translations
     */
    private Component trans(@NotNull String key, @NotNull String... args) {
        return langManager.getComponent(viewer, key, args);
    }

    private void playSuccessSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    private void playErrorSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }
}