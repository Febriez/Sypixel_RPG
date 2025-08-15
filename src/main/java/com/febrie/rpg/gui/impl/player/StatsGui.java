package com.febrie.rpg.gui.impl.player;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.framework.ScrollableGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.stat.Stat;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
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
 * 스탯 GUI - 올바르게 수정된 버전
 * 플레이어가 스탯을 관리할 수 있는 GUI
 *
 * @author Febrie, CoffeeTory
 */
public class StatsGui extends ScrollableGui {

    private static final int DEFAULT_SIZE = 54; // 6줄

    // 스탯 표시 위치 (2x3 그리드)
    private static final Map<Stat, Integer> STAT_SLOTS = new HashMap<>();

    static {
        STAT_SLOTS.put(Stat.STRENGTH, 20);
        STAT_SLOTS.put(Stat.DEXTERITY, 21);
        STAT_SLOTS.put(Stat.INTELLIGENCE, 22);
        STAT_SLOTS.put(Stat.VITALITY, 29);
        STAT_SLOTS.put(Stat.WISDOM, 30);
        STAT_SLOTS.put(Stat.LUCK, 31);
    }

    // 정보 표시 슬롯
    private static final int PLAYER_INFO_SLOT = 4;
    private static final int STAT_POINTS_SLOT = 13;

    // NAV_BACK_SLOT, NAV_CLOSE_SLOT 제거 - BaseGui의 동적 메소드 사용

    private final RPGPlayer rpgPlayer;
    private final Map<Integer, GuiItem> items = new HashMap<>();

    private StatsGui(@NotNull GuiManager guiManager,
                    @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer) {
        super(viewer, guiManager, DEFAULT_SIZE, "gui.stats.title");
        this.rpgPlayer = rpgPlayer;
    }

    /**
     * StatsGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param viewer 보는 플레이어
     * @param rpgPlayer RPG 플레이어
     * @return 초기화된 StatsGui 인스턴스
     */
    public static StatsGui create(@NotNull GuiManager guiManager,
                                 @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer) {
        StatsGui gui = new StatsGui(guiManager, viewer, rpgPlayer);
        gui.initialize("gui.stats.title");
        return gui;
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
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT, ClickType.RIGHT, ClickType.SHIFT_LEFT);
    }

    @Override
    protected List<GuiItem> getScrollableItems() {
        // 스탯이 고정 위치에 있으므로 스크롤 없음
        return new ArrayList<>();
    }

    @Override
    protected void handleNonScrollClick(@NotNull InventoryClickEvent event, @NotNull Player player,
                                        int slot, @NotNull ClickType click) {
        GuiItem item = items.get(slot);
        if (item != null && item.hasActions()) {
            item.executeAction(player, click);
        }
    }

    @Override
    protected void setupLayout() {
        setupBackground();
        setupPlayerInfo();
        setupStatsDisplay();
        // 표준 네비게이션 사용 (새로고침 버튼 없음, 닫기 버튼 있음)
        setupStandardNavigation(false, true);
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        // 테두리
        createBorder();

        // 중앙 장식
        for (int i = 19; i < 24; i++) {
            if (!STAT_SLOTS.containsValue(i)) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
        for (int i = 28; i < 33; i++) {
            if (!STAT_SLOTS.containsValue(i)) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 플레이어 정보 표시
     */
    private void setupPlayerInfo() {
        String jobName = rpgPlayer.hasJob() ?
                transString("job." + rpgPlayer.getJob().name().toLowerCase() + ".name") :
                transString("gui.profile.no-job");

        GuiItem playerInfo = GuiItem.display(
                new ItemBuilder(viewer)
                        .displayName(trans("gui.profile.player-info.name", "player", viewer.getName()))
                        .addLore(trans("gui.profile.level", "level", String.valueOf(rpgPlayer.getLevel())))
                        .addLore(trans("gui.profile.job", "job", jobName))
                        .addLore(trans("gui.profile.combat-power", "power", String.valueOf(rpgPlayer.getCombatPower())))
                        .build()
        );
        setItem(PLAYER_INFO_SLOT, playerInfo);

        // 스탯 포인트 정보
        GuiItem statPointsInfo = GuiItem.display(
                new ItemBuilder(Material.NETHER_STAR)
                        .displayName(trans("gui.stats.points-available"))
                        .addLore(trans("gui.stats.points-count", "points", String.valueOf(rpgPlayer.getStatPoints())))
                        .addLore(Component.empty())
                        .addLore(trans("gui.stats.points-info"))
                        .glint(rpgPlayer.getStatPoints() > 0)
                        .build()
        );
        setItem(STAT_POINTS_SLOT, statPointsInfo);
    }

    /**
     * 스탯 표시
     */
    private void setupStatsDisplay() {
        STAT_SLOTS.forEach((stat, slot) -> {
            int currentValue = rpgPlayer.getStats().getBaseStat(stat);
            int bonusValue = rpgPlayer.getStats().getBonusStat(stat);
            int totalValue = currentValue + bonusValue;

            ItemBuilder builder = ItemBuilder.of(stat.getIcon())
                    .displayName(trans("stat." + stat.getId() + ".name"))
                    .addLore(Component.empty());

            // 현재 스탯
            builder.addLore(trans("gui.stats.current-value", "value", String.valueOf(totalValue)));

            if (bonusValue > 0) {
                builder.addLore(trans("gui.stats.base-bonus",
                        "base", String.valueOf(currentValue),
                        "bonus", "+" + bonusValue));
            }

            builder.addLore(Component.empty());

            // 스탯 설명
            List<Component> description = com.febrie.rpg.util.LangManager.getComponentList(viewer,
                    "stat." + stat.getId() + ".description");
            description.forEach(builder::addLore);

            builder.addLore(Component.empty())
                    .addLore(trans("gui.stats.click-to-add"))
                    .flags(ItemFlag.values());

            GuiItem statItem = GuiItem.of(builder.build())
                    .onClick(ClickType.LEFT, (player, clickType) -> handleStatClick(player, stat, 1))
                    .onClick(ClickType.SHIFT_LEFT, (player, clickType) -> handleStatClick(player, stat, 10))
                    .onClick(ClickType.RIGHT, (player, clickType) -> handleStatClick(player, stat, 1))
                    .onClick(ClickType.SHIFT_RIGHT, (player, clickType) -> handleStatClick(player, stat, 10));

            setItem(slot, statItem);
        });
    }

    // setupNavigationButtons 메소드 제거 - BaseGui의 setupStandardNavigation 사용

    /**
     * 스탯 클릭 처리
     */
    private void handleStatClick(@NotNull Player player, @NotNull Stat stat, int pointsToAdd) {
        if (rpgPlayer.getStatPoints() < pointsToAdd) {
            sendMessage(player, "messages.not-enough-stat-points");
            playErrorSound(player);
            return;
        }

        if (rpgPlayer.useStatPoint(stat, pointsToAdd)) {
            sendMessage(player, "messages.stat-increased",
                    "stat", transString("stat." + stat.getId() + ".name"));
            playSuccessSound(player);
            refresh();
        } else {
            playErrorSound(player);
        }
    }

    @Override
    public GuiFramework getBackTarget() {
        // StatsGui는 ProfileGui로 돌아갑니다
        return new ProfileGui(guiManager, viewer);
    }
}