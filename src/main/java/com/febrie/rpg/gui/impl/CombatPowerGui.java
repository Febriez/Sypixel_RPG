package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
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
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 전투력 상세 정보 GUI
 * 전투력이 어떻게 계산되는지 자세히 보여줌
 *
 * @author Febrie, CoffeeTory
 */
public class CombatPowerGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6줄 (통일성을 위해 변경)
    private final RPGPlayer rpgPlayer;

    public CombatPowerGui(@Nullable GuiManager guiManager, @NotNull LangManager langManager,
                          @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer) {
        super(viewer, guiManager, langManager, GUI_SIZE, "gui.combat-power.title");
        this.rpgPlayer = rpgPlayer;
        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("gui.combat-power.title");
    }

    @Override
    public int getSize() {
        return GUI_SIZE;
    }

    @Override
    protected void setupLayout() {
        setupBackground();
        setupCombatPowerDisplay();
        setupNavigationButtons();
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        createBorder();

        // 중앙 장식
        setItem(4, GuiItem.display(
                ItemBuilder.of(Material.DIAMOND_SWORD)
                        .displayName(trans("gui.combat-power.total", "power", String.valueOf(rpgPlayer.getCombatPower())))
                        .flags(ItemFlag.values())
                        .glint(true)
                        .build()
        ));

        // 중간 구분선 (선택적)
        for (int i = 27; i < 36; i++) {
            if (i != 31) { // 스탯 표시 위치 제외
                setItem(i, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
            }
        }
    }

    /**
     * 전투력 상세 표시
     */
    private void setupCombatPowerDisplay() {
        List<Component> breakdownLore = new ArrayList<>();
        int totalCombatPower = 0;

        // 레벨 기여도
        int levelContribution = rpgPlayer.getLevel() * 10;
        totalCombatPower += levelContribution;

        GuiItem levelItem = GuiItem.display(
                ItemBuilder.of(Material.EXPERIENCE_BOTTLE)
                        .displayName(trans("gui.combat-power.level-contribution"))
                        .addLore(trans("gui.combat-power.level-detail",
                                "level", String.valueOf(rpgPlayer.getLevel()),
                                "value", String.valueOf(levelContribution)))
                        .build()
        );
        setItem(20, levelItem); // 19 -> 20 (한 줄 아래로)

        // 스탯 기여도
        Stat.StatHolder stats = rpgPlayer.getStats();
        int statContribution = 0;

        // 각 스탯별 계산
        StatInfo[] statInfos = {
                new StatInfo(Stat.STRENGTH, 5, Material.IRON_SWORD),
                new StatInfo(Stat.INTELLIGENCE, 5, Material.BOOK),
                new StatInfo(Stat.DEXTERITY, 3, Material.FEATHER),
                new StatInfo(Stat.VITALITY, 4, Material.GOLDEN_APPLE),
                new StatInfo(Stat.WISDOM, 4, Material.ENCHANTED_BOOK),
                new StatInfo(Stat.LUCK, 2, Material.RABBIT_FOOT)
        };

        int[] slots = {21, 22, 23, 30, 31, 32}; // 한 줄씩 아래로 이동

        for (int i = 0; i < statInfos.length; i++) {
            StatInfo info = statInfos[i];
            int statValue = stats.getTotalStat(info.stat);
            int contribution = statValue * info.multiplier;
            statContribution += contribution;

            boolean isKorean = transString("general.language-code").equals("ko_KR");
            String statName = info.stat.getName(isKorean);

            GuiItem statItem = GuiItem.display(
                    ItemBuilder.of(info.icon)
                            .displayName(Component.text(statName, info.stat.getColor())
                                    .decoration(TextDecoration.BOLD, true))
                            .addLore(trans("gui.combat-power.stat-detail",
                                    "stat", statName,
                                    "value", String.valueOf(statValue),
                                    "multiplier", String.valueOf(info.multiplier),
                                    "total", String.valueOf(contribution)))
                            .flags(ItemFlag.values())
                            .build()
            );
            setItem(slots[i], statItem);
        }

        totalCombatPower += statContribution;

        // 총 스탯 기여도 표시
        GuiItem statTotalItem = GuiItem.display(
                ItemBuilder.of(Material.BOOK)
                        .displayName(trans("gui.combat-power.stat-contribution"))
                        .addLore(trans("gui.combat-power.total-stats", "value", String.valueOf(statContribution)))
                        .addLore(Component.empty())
                        .addLore(trans("gui.combat-power.stat-multipliers"))
                        .build()
        );
        setItem(24, statTotalItem); // 25 -> 24

        // 계산 공식 설명
        GuiItem formulaItem = GuiItem.display(
                ItemBuilder.of(Material.PAPER)
                        .displayName(Component.text("계산 공식", ColorUtil.INFO)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(trans("gui.combat-power.calculation-formula"))
                        .addLore(Component.empty())
                        .addLore(Component.text("레벨 기여도: " + levelContribution, ColorUtil.GREEN))
                        .addLore(Component.text("스탯 기여도: " + statContribution, ColorUtil.INFO))
                        .addLore(Component.text("────────────", ColorUtil.DARK_GRAY))
                        .addLore(Component.text("총 전투력: " + totalCombatPower, ColorUtil.LEGENDARY)
                                .decoration(TextDecoration.BOLD, true))
                        .build()
        );
        setItem(13, formulaItem);
    }

    /**
     * 네비게이션 버튼 설정 - 위치 통일
     */
    private void setupNavigationButtons() {
        // 뒤로가기 (45번 슬롯), 닫기 (53번 슬롯)
        setupNavigationButtons(45, -1, 53);
    }

    /**
     * 스탯 정보 클래스
     */
    private record StatInfo(Stat stat, int multiplier, Material icon) {
    }
}