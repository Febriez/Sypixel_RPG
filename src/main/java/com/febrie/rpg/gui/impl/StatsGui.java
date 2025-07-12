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

    public StatsGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager, @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer) {
        super(viewer);
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.rpgPlayer = rpgPlayer;
        this.inventory = Bukkit.createInventory(this, GUI_SIZE, langManager.getComponent(viewer, "gui.stats.title"));

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
        // 스탯이 6개뿐이므로 스크롤 없이 고정 배치로 변경
        return new ArrayList<>();
    }

    @Override
    protected void handleNonScrollClick(@NotNull InventoryClickEvent event, @NotNull Player player, int slot, @NotNull ClickType click) {
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

        // 스탯 표시 (고정 위치)
        setupStatsDisplay();

        // 네비게이션 버튼
        setupNavigationButtons();
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        // 테두리 생성 (수동으로)
        // 상단 테두리
        for (int i = 0; i < 9; i++) {
            if (i != 4) { // 플레이어 머리 위치 제외
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 하단 테두리
        for (int i = 45; i < 54; i++) {
            if (i != 45 && i != 49 && i != 50 && i != 53) { // 네비게이션 버튼 위치 제외
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 좌우 테두리
        for (int row = 1; row < 5; row++) {
            setItem(row * 9, GuiFactory.createDecoration());
            setItem(row * 9 + 8, GuiFactory.createDecoration());
        }

        // 중앙 장식용 라인
        for (int i = 18; i < 27; i++) {
            if (i != 22) { // 스탯 포인트 정보 위치 제외
                setItem(i, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
            }
        }
    }

    /**
     * 플레이어 정보 표시
     */
    private void setupPlayerInfo() {
        if (!rpgPlayer.hasJob()) {
            new JobSelectionGui(guiManager, langManager, viewer, rpgPlayer).open(viewer);
            return;
        }

        // 플레이어 머리 (상단 중앙)
        GuiItem playerHead = GuiItem.display(ItemBuilder.of(Material.PLAYER_HEAD).displayName(Component.text(viewer.getName(), ColorUtil.LEGENDARY).decoration(TextDecoration.BOLD, true)).addLore(Component.empty()).addLore(trans("gui.talent.level", "level", String.valueOf(rpgPlayer.getLevel()))).addLore(trans("gui.talent.job", "job", transString("job." + rpgPlayer.getJob().name().toLowerCase() + ".name"))).addLore(trans("gui.stats.combat-power", "power", String.valueOf(rpgPlayer.getCombatPower()))).build());
        setItem(4, playerHead);

        // 스탯 포인트 정보 (중앙)
        GuiItem statPointInfo = GuiItem.display(ItemBuilder.of(Material.EXPERIENCE_BOTTLE).displayName(trans("gui.stats.stat-points")).addLore(trans("gui.stats.available-points", "points", String.valueOf(rpgPlayer.getStatPoints()))).addLore(Component.empty()).addLore(trans("gui.stats.points-per-level")).addLore(trans("gui.stats.click-to-use")).glint(rpgPlayer.getStatPoints() > 0).build());
        setItem(22, statPointInfo);
    }

    /**
     * 스탯 표시 - 더 예쁜 배치
     */
    private void setupStatsDisplay() {
        // 스탯 배치: 2x3 그리드로 변경
        // 상단: STR INT DEX
        // 하단: VIT WIS LUK

        Map<Integer, Stat> statPositions = new HashMap<>();
        statPositions.put(29, Stat.STRENGTH);     // 왼쪽
        statPositions.put(31, Stat.INTELLIGENCE); // 중앙
        statPositions.put(33, Stat.DEXTERITY);    // 오른쪽
        statPositions.put(38, Stat.VITALITY);     // 왼쪽 아래
        statPositions.put(40, Stat.WISDOM);       // 중앙 아래
        statPositions.put(42, Stat.LUCK);         // 오른쪽 아래

        for (Map.Entry<Integer, Stat> entry : statPositions.entrySet()) {
            setItem(entry.getKey(), createStatItem(entry.getValue()));
        }

        // 장식용 아이템들
        // 스탯 설명
        GuiItem statGuide = GuiItem.display(ItemBuilder.of(Material.BOOK).displayName(Component.text("스탯 가이드", ColorUtil.INFO).decoration(TextDecoration.BOLD, true)).addLore(Component.text("각 스탯을 클릭하여 포인트를 사용하세요", ColorUtil.GRAY)).addLore(Component.empty()).addLore(Component.text("조작법:", ColorUtil.YELLOW)).addLore(Component.text("• 좌클릭: +1 포인트", ColorUtil.WHITE)).addLore(Component.text("• Shift+좌클릭: +5 포인트", ColorUtil.WHITE)).addLore(Component.text("• 우클릭: +10 포인트", ColorUtil.WHITE)).build());
        setItem(13, statGuide);
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

        // 진행도 바 추가
        double percentage = (double) baseStat / stat.getMaxValue() * 100;
        Component progressBar = createProgressBar(percentage);
        lore.add(progressBar);
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

        return GuiItem.of(ItemBuilder.of(stat.getIcon()).displayName(stat.getDisplayName(isKorean).decoration(TextDecoration.BOLD, true)).lore(lore).flags(ItemFlag.values()).glint(bonusStat > 0) // 보너스가 있으면 반짝임
                .build()).onClick(ClickType.LEFT, (player, click) -> {
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
     * 진행도 바 생성
     */
    private Component createProgressBar(double percentage) {
        int barLength = 20;
        int filled = (int) Math.round(percentage / 100.0 * barLength);
        String filledBar = "█".repeat(Math.max(0, filled));
        String emptyBar = "░".repeat(Math.max(0, barLength - filled));

        // 색상 결정
        Component coloredBar;
        if (percentage >= 80) {
            coloredBar = Component.text(filledBar, ColorUtil.LEGENDARY).append(Component.text(emptyBar, ColorUtil.GRAY));
        } else if (percentage >= 60) {
            coloredBar = Component.text(filledBar, ColorUtil.EPIC).append(Component.text(emptyBar, ColorUtil.GRAY));
        } else if (percentage >= 40) {
            coloredBar = Component.text(filledBar, ColorUtil.RARE).append(Component.text(emptyBar, ColorUtil.GRAY));
        } else if (percentage >= 20) {
            coloredBar = Component.text(filledBar, ColorUtil.UNCOMMON).append(Component.text(emptyBar, ColorUtil.GRAY));
        } else {
            coloredBar = Component.text(filledBar, ColorUtil.COMMON).append(Component.text(emptyBar, ColorUtil.GRAY));
        }

        return coloredBar;
    }

    /**
     * 네비게이션 버튼 설정 - 위치 통일
     */
    private void setupNavigationButtons() {
        if (guiManager == null) {
            throw new IllegalArgumentException("GuiManager cannot be null");
        }

        if (rpgPlayer == null) {
            throw new IllegalArgumentException("rpgPlayer cannot be null");
        }

        // 뒤로가기 버튼 - 45번 슬롯 (좌측 하단)
        setItem(45, GuiFactory.createBackButton(guiManager, langManager, viewer));

        // 새로고침 버튼 - 49번 슬롯 (중앙 하단)
        setItem(49, GuiFactory.createRefreshButton(_ -> refresh(), langManager, viewer));

        // 특성 페이지로 가기 버튼 - 50번 슬롯
        GuiItem talentButton = GuiItem.clickable(
                ItemBuilder.of(Material.ENCHANTED_BOOK)
                        .displayName(trans("gui.talent.title"))
                        .addLore(trans("gui.stats.click-talents"))
                        .glint(true)
                        .build(),
                player -> {
                    // closeInventory() 제거하여 마우스 위치 유지
                    new TalentGui(guiManager, langManager, player, rpgPlayer, "main",
                            RPGMain.getPlugin().getTalentManager().getJobMainTalents(rpgPlayer.getJob())).open(player);
                }
        );
        setItem(50, talentButton);

        // 닫기 버튼 - 53번 슬롯 (우측 하단)
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

    private String transString(@NotNull String key, @NotNull String... args) {
        return langManager.getMessage(viewer, key, args);
    }

    private void playSuccessSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    private void playErrorSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }
}