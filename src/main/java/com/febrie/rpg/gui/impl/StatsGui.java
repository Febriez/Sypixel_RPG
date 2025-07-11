package com.febrie.rpg.gui.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.ScrollableGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.stat.Stat;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
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
    private final Inventory inventory;
    private final Map<Integer, GuiItem> items = new HashMap<>();

    public StatsGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                    @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer) {
        super(viewer);
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.rpgPlayer = rpgPlayer;
        this.inventory = Bukkit.createInventory(this, GUI_SIZE,
                Component.text("스탯 관리", ColorUtil.LEGENDARY));

        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.text("스탯 관리", ColorUtil.LEGENDARY);
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
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    protected List<GuiItem> getScrollableItems() {
        List<GuiItem> statItems = new ArrayList<>();
        boolean isKorean = langManager.getPlayerLanguage(viewer).equals("ko_KR");

        // 모든 스탯을 GuiItem으로 변환
        for (Stat stat : Stat.getAllStats().values()) {
            statItems.add(createStatItem(stat, isKorean));
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
            if (i != 49 && i != SCROLL_DOWN_SLOT) {
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
                        .addLore(Component.text("레벨 " + rpgPlayer.getLevel(), ColorUtil.SUCCESS))
                        .addLore(Component.text("직업: " + (rpgPlayer.hasJob() ?
                                rpgPlayer.getJob().getKoreanName() : "없음"), ColorUtil.INFO))
                        .addLore(Component.text("전투력: " + rpgPlayer.getCombatPower(), ColorUtil.ORANGE))
                        .build()
        );
        setItem(4, playerHead);

        // 스탯 포인트 정보
        GuiItem statPointInfo = GuiItem.display(
                ItemBuilder.of(Material.EXPERIENCE_BOTTLE)
                        .displayName(Component.text("스탯 포인트", ColorUtil.EXPERIENCE))
                        .addLore(Component.text("보유 포인트: " + rpgPlayer.getStatPoints(), NamedTextColor.WHITE))
                        .addLore(Component.empty())
                        .addLore(Component.text("레벨업 시 5포인트를 획득합니다", NamedTextColor.GRAY))
                        .addLore(Component.text("스탯을 클릭하여 포인트를 사용하세요", NamedTextColor.GRAY))
                        .build()
        );
        setItem(49, statPointInfo);
    }

    /**
     * 스탯 아이템 생성
     */
    private GuiItem createStatItem(@NotNull Stat stat, boolean isKorean) {
        Stat.StatHolder stats = rpgPlayer.getStats();
        int baseStat = stats.getBaseStat(stat);
        int bonusStat = stats.getBonusStat(stat);
        int totalStat = stats.getTotalStat(stat);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("현재 능력치", NamedTextColor.YELLOW));
        lore.add(Component.text("  기본: " + baseStat, NamedTextColor.WHITE));

        if (bonusStat > 0) {
            lore.add(Component.text("  보너스: +" + bonusStat, ColorUtil.SUCCESS));
            lore.add(Component.text("  총합: " + totalStat, ColorUtil.LEGENDARY));
        }

        lore.add(Component.empty());
        lore.add(stat.getDescription(isKorean));
        lore.add(Component.empty());

        if (rpgPlayer.getStatPoints() > 0 && baseStat < stat.getMaxValue()) {
            lore.add(Component.text("▶ 좌클릭: +1 포인트", ColorUtil.SUCCESS));
            lore.add(Component.text("▶ Shift+좌클릭: +5 포인트", ColorUtil.SUCCESS));
            lore.add(Component.text("▶ 우클릭: +10 포인트", ColorUtil.SUCCESS));
        } else if (baseStat >= stat.getMaxValue()) {
            lore.add(Component.text("최대치에 도달했습니다", ColorUtil.ERROR));
        } else {
            lore.add(Component.text("스탯 포인트가 부족합니다", ColorUtil.ERROR));
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
                player.playSound(player.getLocation(),
                        org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            } else {
                player.playSound(player.getLocation(),
                        org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }).onClick(ClickType.SHIFT_LEFT, (player, click) -> {
            int amount = Math.min(5, rpgPlayer.getStatPoints());
            if (amount > 0 && rpgPlayer.useStatPoint(stat, amount)) {
                refresh();
                player.playSound(player.getLocation(),
                        org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            } else {
                player.playSound(player.getLocation(),
                        org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }).onClick(ClickType.RIGHT, (player, click) -> {
            int amount = Math.min(10, rpgPlayer.getStatPoints());
            if (amount > 0 && rpgPlayer.useStatPoint(stat, amount)) {
                refresh();
                player.playSound(player.getLocation(),
                        org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            } else {
                player.playSound(player.getLocation(),
                        org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
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
                        .displayName(Component.text("특성 관리", ColorUtil.EPIC))
                        .addLore(Component.text("클릭하여 특성 페이지로 이동", NamedTextColor.GRAY))
                        .build(),
                player -> new TalentGui(guiManager, langManager, player, rpgPlayer, null, RPGMain.getPlugin()
                        .getTalentManager().getJobMainTalents(rpgPlayer.getJob())).open(player)
        );
        setItem(52, talentButton);

        // 닫기 버튼
        setItem(53, GuiFactory.createCloseButton(langManager, viewer));
    }

    /**
     * 아이템 설정
     */
    private void setItem(int slot, @NotNull GuiItem item) {
        items.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }
}