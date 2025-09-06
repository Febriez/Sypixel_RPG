package com.febrie.rpg.gui.impl.player;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.system.MainMenuGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.stat.Stat;
import com.febrie.rpg.util.ItemBuilder;

import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

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

    private CombatPowerGui(@NotNull GuiManager guiManager,
                          @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer) {
        super(viewer, guiManager, GUI_SIZE, LangManager.text(LangKey.GUI_COMBAT_POWER_TITLE, viewer));
        this.rpgPlayer = rpgPlayer;
    }

    /**
     * CombatPowerGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param viewer 보는 플레이어
     * @param rpgPlayer RPG 플레이어
     * @return 초기화된 CombatPowerGui 인스턴스
     */
    public static CombatPowerGui create(@NotNull GuiManager guiManager,
                                       @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer) {
        return new CombatPowerGui(guiManager, viewer, rpgPlayer);
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(LangKey.GUI_COMBAT_POWER_TITLE, viewer);
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
                ItemBuilder.of(Material.DIAMOND_SWORD, getViewerLocale())
                        .displayName(LangManager.text(LangKey.GUI_COMBAT_POWER_TOTAL, viewer, String.valueOf(rpgPlayer.getCombatPower())))
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
                ItemBuilder.of(Material.EXPERIENCE_BOTTLE, getViewerLocale())
                        .displayName(LangManager.text(LangKey.GUI_COMBAT_POWER_LEVEL_CONTRIBUTION, viewer))
                        .addLore(LangManager.text(LangKey.GUI_COMBAT_POWER_LEVEL_DETAIL, viewer,
                                String.valueOf(rpgPlayer.getLevel()),
                                String.valueOf(levelContribution)))
                        .build()
        );
        setItem(20, levelItem);

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

        int[] slots = {21, 22, 23, 30, 31, 32};

        for (int i = 0; i < statInfos.length; i++) {
            StatInfo info = statInfos[i];
            int statValue = stats.getTotalStat(info.stat);
            int contribution = statValue * info.multiplier;
            statContribution += contribution;

            Component statName = LangManager.text(LangKey.valueOf("STAT_" + info.stat.getId().toUpperCase() + "_NAME"), viewer);

            GuiItem statItem = GuiItem.display(
                    ItemBuilder.of(info.material, getViewerLocale())
                            .displayName(LangManager.text(LangKey.GUI_COMBAT_POWER_STAT_CONTRIBUTION, viewer, statName))
                            .addLore(LangManager.text(LangKey.GUI_COMBAT_POWER_STAT_DETAIL, viewer,
                                    String.valueOf(statValue),
                                    String.valueOf(info.multiplier),
                                    String.valueOf(contribution)))
                            .build()
            );
            setItem(slots[i], statItem);
        }

        totalCombatPower += statContribution;

        // 총합 표시
        GuiItem totalItem = GuiItem.display(
                ItemBuilder.of(Material.NETHER_STAR, getViewerLocale())
                        .displayName(LangManager.text(LangKey.GUI_COMBAT_POWER_TOTAL, viewer, String.valueOf(totalCombatPower)))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_COMBAT_POWER_BREAKDOWN, viewer))
                        .addLore(LangManager.text(LangKey.GUI_COMBAT_POWER_FROM_LEVEL, viewer, String.valueOf(levelContribution)))
                        .addLore(LangManager.text(LangKey.GUI_COMBAT_POWER_FROM_STATS, viewer, String.valueOf(statContribution)))
                        .glint(true)
                        .build()
        );
        setItem(40, totalItem);
    }

    /**
     * 네비게이션 버튼 설정
     */
    private void setupNavigationButtons() {
        setupStandardNavigation(false, true);
    }

    /**
     * 스탯 정보 저장용 내부 클래스
     */
    private record StatInfo(Stat stat, int multiplier, Material material) {
    }

    @Override
    public GuiFramework getBackTarget() {
        // CombatPowerGui는 MainMenuGui로 돌아갑니다
        return MainMenuGui.create(guiManager, viewer);
    }
}
