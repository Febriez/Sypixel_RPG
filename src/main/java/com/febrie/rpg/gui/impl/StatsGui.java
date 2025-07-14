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
import net.kyori.adventure.text.format.TextDecoration;
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
 * GuiManager 중앙 집중식 네비게이션 적용
 *
 * @author Febrie, CoffeeTory
 */
public class StatsGui extends ScrollableGui {

    private static final int DEFAULT_SIZE = 54; // 6줄

    // 레이아웃 상수
    private static final int PLAYER_HEAD_SLOT = 4;
    private static final int STAT_POINT_INFO_SLOT = 22;
    private static final int STAT_GUIDE_SLOT = 13;

    // 스탯 표시 위치 (2x3 그리드)
    private static final Map<Integer, Stat> STAT_POSITIONS = new HashMap<>();

    static {
        STAT_POSITIONS.put(29, Stat.STRENGTH);     // 왼쪽
        STAT_POSITIONS.put(31, Stat.INTELLIGENCE); // 중앙
        STAT_POSITIONS.put(33, Stat.DEXTERITY);    // 오른쪽
        STAT_POSITIONS.put(38, Stat.VITALITY);     // 왼쪽 아래
        STAT_POSITIONS.put(40, Stat.WISDOM);       // 중앙 아래
        STAT_POSITIONS.put(42, Stat.LUCK);         // 오른쪽 아래
    }

    // 네비게이션 버튼 위치
    private static final int NAV_BACK_SLOT = 45;
    private static final int NAV_REFRESH_SLOT = 49;
    private static final int NAV_TALENTS_SLOT = 50;
    private static final int NAV_CLOSE_SLOT = 53;

    private final GuiManager guiManager;
    private final LangManager langManager;
    private final RPGPlayer rpgPlayer;

    public StatsGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager, @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer) {
        super(viewer, guiManager, langManager, DEFAULT_SIZE, "gui.stats.title");
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.rpgPlayer = rpgPlayer;

        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("gui.stats.title");
    }

    @Override
    public void refresh() {
        inventory.clear();
        items.clear();
        setupLayout();
    }

    @Override
    protected List<GuiItem> getScrollableItems() {
        // 스탯이 6개뿐이므로 스크롤 없이 고정 배치
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
    @Override
    protected void setupLayout() {
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
        // 테두리 생성
        // 상단 테두리
        for (int i = 0; i < 9; i++) {
            if (i != PLAYER_HEAD_SLOT) { // 플레이어 머리 위치 제외
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 하단 테두리 - 네비게이션 버튼 위치 제외
        for (int i = getLastRowStart(); i < size; i++) {
            if (i != NAV_BACK_SLOT && i != NAV_REFRESH_SLOT && i != NAV_TALENTS_SLOT && i != NAV_CLOSE_SLOT) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 좌우 테두리
        for (int row = 1; row < (size / 9) - 1; row++) {
            setItem(row * 9, GuiFactory.createDecoration());
            setItem(row * 9 + 8, GuiFactory.createDecoration());
        }

        // 중앙 장식용 라인
        for (int i = 18; i < 27; i++) {
            if (i != STAT_POINT_INFO_SLOT) { // 스탯 포인트 정보 위치 제외
                setItem(i, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
            }
        }
    }

    /**
     * 플레이어 정보 표시
     */
    private void setupPlayerInfo() {
        if (!rpgPlayer.hasJob()) {
            // 직업이 없으면 직업 선택 GUI로 이동
            JobSelectionGui jobGui = new JobSelectionGui(guiManager, langManager, viewer, rpgPlayer);
            guiManager.openGui(viewer, jobGui);
            return;
        }

        // 플레이어 머리 (상단 중앙)
        GuiItem playerHead = GuiItem.display(new ItemBuilder(viewer)
                .displayName(Component.text(viewer.getName(), ColorUtil.LEGENDARY)
                        .decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty())
                .addLore(trans("gui.talent.level", "level", String.valueOf(rpgPlayer.getLevel())))
                .addLore(trans("gui.talent.job", "job", transString("job." + rpgPlayer.getJob().name().toLowerCase() + ".name")))
                .addLore(trans("gui.stats.combat-power", "power", String.valueOf(rpgPlayer.getCombatPower())))
                .build());
        setItem(PLAYER_HEAD_SLOT, playerHead);

        // 스탯 포인트 정보 (중앙 라인)
        Stat.StatHolder stats = rpgPlayer.getStats();
        GuiItem statPointInfo = GuiItem.display(ItemBuilder.of(Material.EXPERIENCE_BOTTLE)
                .displayName(trans("gui.stats.stat-points"))
                .addLore(trans("gui.stats.available-points", "points", String.valueOf(rpgPlayer.getStatPoints())))
                .addLore(trans("gui.stats.total-allocated", "points", String.valueOf(getTotalAllocatedPoints(stats))))
                .addLore(Component.empty())
                .addLore(trans("gui.stats.points-per-level"))
                .build());
        setItem(STAT_POINT_INFO_SLOT, statPointInfo);

        // 스탯 가이드
        GuiItem statGuide = GuiItem.display(ItemBuilder.of(Material.BOOK)
                .displayName(trans("gui.stats.guide-title"))
                .addLore(trans("gui.stats.guide-click-add"))
                .addLore(trans("gui.stats.guide-shift-click"))
                .build());
        setItem(STAT_GUIDE_SLOT, statGuide);
    }

    /**
     * 스탯 표시
     */
    private void setupStatsDisplay() {
        Stat.StatHolder stats = rpgPlayer.getStats();

        for (Map.Entry<Integer, Stat> entry : STAT_POSITIONS.entrySet()) {
            int slot = entry.getKey();
            Stat stat = entry.getValue();

            int baseValue = stats.getBaseStat(stat);
            int bonusValue = stats.getBonusStat(stat);
            int totalValue = stats.getTotalStat(stat);

            ItemBuilder builder = ItemBuilder.of(stat.getIcon())
                    .displayName(trans("stat." + stat.getId().toLowerCase() + ".name")
                            .color(ColorUtil.UNCOMMON)
                            .decoration(TextDecoration.BOLD, true))
                    .addLore(Component.empty())
                    .addLore(trans("gui.stats.base-value", "value", String.valueOf(baseValue)))
                    .addLore(trans("gui.stats.bonus-value", "value", String.valueOf(bonusValue)))
                    .addLore(trans("gui.stats.total-value", "value", String.valueOf(totalValue)))
                    .addLore(Component.empty());

            // 스탯 설명
            List<Component> description = langManager.getComponentList(viewer, "stat." + stat.getId().toLowerCase() + ".description");
            for (Component line : description) {
                builder.addLore(line);
            }

            builder.addLore(Component.empty())
                    .addLore(trans("gui.stats.click-to-add"))
                    .flags(ItemFlag.values());

            // GuiItem에 클릭 타입별 액션 설정
            GuiItem statItem = GuiItem.of(builder.build())
                    .onClick(ClickType.LEFT, (player, clickType) -> handleStatClick(player, stat, clickType))
                    .onClick(ClickType.SHIFT_LEFT, (player, clickType) -> handleStatClick(player, stat, clickType))
                    .onClick(ClickType.RIGHT, (player, clickType) -> handleStatClick(player, stat, clickType))
                    .onClick(ClickType.SHIFT_RIGHT, (player, clickType) -> handleStatClick(player, stat, clickType));

            setItem(slot, statItem);
        }
    }

    /**
     * 네비게이션 버튼 설정 - GuiManager 통합
     */
    private void setupNavigationButtons() {
        // 뒤로가기 버튼 - GuiManager가 처리
        if (guiManager.canGoBack(viewer)) {
            setItem(NAV_BACK_SLOT, GuiItem.clickable(
                    ItemBuilder.of(Material.ARROW)
                            .displayName(trans("gui.buttons.back.name"))
                            .addLore(trans("gui.buttons.back.lore"))
                            .build(),
                    guiManager::goBack));
        }

        // 새로고침 버튼
        setItem(NAV_REFRESH_SLOT, GuiFactory.createRefreshButton(_ -> refresh(), langManager, viewer));

        // 특성 페이지로 가기 버튼
        GuiItem talentButton = GuiItem.clickable(
                ItemBuilder.of(Material.ENCHANTED_BOOK)
                        .displayName(trans("gui.talent.title"))
                        .addLore(trans("gui.stats.click-talents"))
                        .glint(true)
                        .build(),
                player -> {
                    TalentGui talentGui = new TalentGui(guiManager, langManager, player, rpgPlayer,
                            "main", RPGMain.getPlugin().getTalentManager().getJobMainTalents(rpgPlayer.getJob()));
                    guiManager.openGui(player, talentGui);
                });
        setItem(NAV_TALENTS_SLOT, talentButton);

        // 닫기 버튼
        setItem(NAV_CLOSE_SLOT, GuiFactory.createCloseButton(langManager, viewer));
    }

    /**
     * 스탯 클릭 처리
     */
    private void handleStatClick(@NotNull Player player, @NotNull Stat stat, @NotNull ClickType click) {
        int pointsToAdd = click.isShiftClick() ? 10 : 1;

        if (rpgPlayer.getStatPoints() < pointsToAdd) {
            sendMessage(player, "messages.not-enough-stat-points");
            playErrorSound(player);
            return;
        }

        if (rpgPlayer.useStatPoint(stat, pointsToAdd)) {
            sendMessage(player, "messages.stat-increased",
                    "stat", transString("stat." + stat.getId().toLowerCase() + ".name"),
                    "amount", String.valueOf(pointsToAdd));
            playSuccessSound(player);

            // GUI 새로고침
            refresh();
        } else {
            sendMessage(player, "messages.stat-increase-failed");
            playErrorSound(player);
        }
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(
                ClickType.LEFT,
                ClickType.SHIFT_LEFT,
                ClickType.RIGHT,
                ClickType.SHIFT_RIGHT
        );
    }

    /**
     * 총 할당된 스탯 포인트 계산
     */
    private int getTotalAllocatedPoints(@NotNull Stat.StatHolder stats) {
        int total = 0;
        for (Stat stat : Stat.getAllStats().values()) {
            total += stats.getBaseStat(stat) - stat.getDefaultValue();
        }
        return total;
    }
}