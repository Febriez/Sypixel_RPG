package com.febrie.rpg.gui.impl.player;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.framework.ScrollableGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.talent.Talent;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;

import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 특성 GUI - 올바르게 수정된 버전
 * 플레이어가 특성을 관리할 수 있는 GUI
 *
 * @author Febrie, CoffeeTory
 */
public class TalentGui extends ScrollableGui {

    private static final int GUI_SIZE = 54; // 6줄
    private static final int TALENT_TREE_START = 10;
    private static final int TALENTS_PER_ROW = 7;

    // NAV_BACK_SLOT, NAV_CLOSE_SLOT 제거 - BaseGui의 동적 메소드 사용

    private final RPGPlayer rpgPlayer;
    private final String pageId;
    private final List<Talent> talents;

    private TalentGui(@NotNull GuiManager guiManager,
                     @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer,
                     @NotNull String pageId, @NotNull List<Talent> talents) {
        super(viewer, guiManager, GUI_SIZE, LangManager.text(LangKey.GUI_TALENT_TITLE, viewer));
        this.rpgPlayer = rpgPlayer;
        this.pageId = pageId;
        this.talents = talents;
    }

    /**
     * TalentGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param viewer 보는 플레이어
     * @param rpgPlayer RPG 플레이어
     * @param pageId 페이지 ID
     * @param talents 특성 목록
     * @return 초기화된 TalentGui 인스턴스
     */
    public static TalentGui create(@NotNull GuiManager guiManager,
                                  @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer,
                                  @NotNull String pageId, @NotNull List<Talent> talents) {
        return new TalentGui(guiManager, viewer, rpgPlayer, pageId, talents);
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(LangKey.GUI_TALENT_TITLE, viewer);
    }

    @Override
    public void refresh() {
        inventory.clear();
        items.clear();
        setupLayout();
    }

    @Override
    protected List<GuiItem> getScrollableItems() {
        // 특성은 고정 위치에 표시되므로 스크롤 아이템 없음
        return new ArrayList<>();
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT, ClickType.RIGHT, ClickType.SHIFT_LEFT);
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
        setupInfoDisplay();
        setupTalentTree();
        // 표준 네비게이션 사용 (새로고침 버튼 없음, 닫기 버튼 있음)
        setupStandardNavigation(false, true);
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        createBorder();

        // 특성 영역 배경
        for (int row = 1; row < 4; row++) {
            for (int col = 1; col < 8; col++) {
                int slot = row * 9 + col;
                if (!items.containsKey(slot)) {
                    setItem(slot, GuiFactory.createDecoration());
                }
            }
        }
    }

    /**
     * 정보 표시 영역
     */
    private void setupInfoDisplay() {
        Component jobName;
        if (rpgPlayer.hasJob() && rpgPlayer.getJob() != null) {
            jobName = LangManager.text(LangKey.valueOf("JOB_" + rpgPlayer.getJob().name().toUpperCase() + "_NAME"), viewer);
        } else {
            jobName = LangManager.text(LangKey.GUI_TALENT_NO_JOB, viewer);
        }

        GuiItem pageInfo = GuiItem.display(
                ItemBuilder.of(Material.ENCHANTED_BOOK)
                        .displayName(LangManager.text(LangKey.GUI_TALENT_PAGE_INFO, viewer))
                        .addLore(LangManager.text(LangKey.GUI_TALENT_CURRENT_PAGE, viewer, getPageTitle()))
                        .addLore(LangManager.text(LangKey.GUI_TALENT_JOB, viewer, jobName))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_TALENT_AVAILABLE_POINTS, viewer, String.valueOf(rpgPlayer.getTalents().getAvailablePoints())))
                        .glint(true)
                        .build()
        );
        setItem(4, pageInfo);
    }

    /**
     * 특성 트리 표시
     */
    private void setupTalentTree() {
        int index = 0;
        for (Talent talent : talents) {
            if (index >= TALENTS_PER_ROW * 3) break; // 최대 3줄

            int row = index / TALENTS_PER_ROW;
            int col = index % TALENTS_PER_ROW;
            int slot = TALENT_TREE_START + (row * 9) + col;

            setItem(slot, createTalentItem(talent));
            index++;
        }
    }

    /**
     * 특성 아이템 생성
     */
    private GuiItem createTalentItem(@NotNull Talent talent) {
        int currentLevel = rpgPlayer.getTalents().getTalentLevel(talent);
        boolean canLearn = talent.canActivate(rpgPlayer.getTalents());
        boolean maxed = currentLevel >= talent.getMaxLevel();

        Material material = talent.getIcon();
        if (!canLearn && currentLevel == 0) {
            material = Material.GRAY_DYE;
        }

        ItemBuilder builder = ItemBuilder.of(material)
                .displayName(LangManager.text(LangKey.valueOf("TALENT_" + talent.getId().toUpperCase() + "_NAME"), viewer))
                .amount(Math.max(1, currentLevel));

        // 설명 추가
        List<Component> description = LangManager.list(LangKey.valueOf("TALENT_" + talent.getId().toUpperCase() + "_DESCRIPTION"), viewer);
        description.forEach(builder::addLore);

        // 레벨 정보
        builder.addLore(Component.empty());
        builder.addLore(LangManager.text(LangKey.GUI_TALENT_LEVEL_INFO, viewer,
                String.valueOf(currentLevel),
                String.valueOf(talent.getMaxLevel())));

        // 상태 표시
        if (maxed) {
            builder.addLore(LangManager.text(LangKey.GUI_TALENT_MAXED, viewer))
                    .glint(true);
        } else if (canLearn) {
            builder.addLore(LangManager.text(LangKey.GUI_TALENT_CAN_LEARN, viewer, String.valueOf(talent.getRequiredPoints())));
        } else {
            builder.addLore(LangManager.text(LangKey.GUI_TALENT_CANNOT_LEARN, viewer));

            // 선행 조건 표시
            Map<Talent, Integer> prerequisites = talent.getPrerequisites();
            if (!prerequisites.isEmpty()) {
                builder.addLore(Component.empty());
                builder.addLore(LangManager.text(LangKey.GUI_TALENT_PREREQUISITES, viewer));

                for (Map.Entry<Talent, Integer> entry : prerequisites.entrySet()) {
                    Talent prereq = entry.getKey();
                    int requiredLevel = entry.getValue();
                    int playerLevel = rpgPlayer.getTalents().getTalentLevel(prereq);
                    boolean meets = playerLevel >= requiredLevel;

                    Component prereqName = LangManager.text(LangKey.valueOf("TALENT_" + prereq.getId().toUpperCase() + "_NAME"), viewer);
                    builder.addLore(LangManager.text(meets ? LangKey.GUI_TALENT_PREREQ_MET : LangKey.GUI_TALENT_PREREQ_NOT_MET, viewer,
                            prereqName,
                            String.valueOf(requiredLevel),
                            String.valueOf(playerLevel)));
                }
            }

            // 포인트 부족
            if (rpgPlayer.getTalents().getAvailablePoints() < talent.getRequiredPoints()) {
                builder.addLore(LangManager.text(LangKey.GUI_TALENT_NOT_ENOUGH_POINTS, viewer));
            }
        }

        // 스탯 보너스 표시
        Map<com.febrie.rpg.stat.Stat, Integer> statBonuses = talent.getStatBonuses(1);
        if (!statBonuses.isEmpty()) {
            builder.addLore(Component.empty());
            builder.addLore(LangManager.text(LangKey.GUI_TALENT_STAT_BONUSES, viewer));

            statBonuses.forEach((stat, bonus) -> {
                Component statName = LangManager.text(LangKey.valueOf("STAT_" + stat.getId().toUpperCase() + "_NAME"), viewer);
                int totalBonus = bonus * Math.max(1, currentLevel);
                builder.addLore(LangManager.text(LangKey.GUI_TALENT_STAT_BONUS_LINE, viewer,
                        statName,
                        String.valueOf(totalBonus)));
            });
        }

        // 특수 효과 표시
        List<String> effects = talent.getEffects();
        if (!effects.isEmpty()) {
            builder.addLore(Component.empty());
            builder.addLore(LangManager.text(LangKey.GUI_TALENT_EFFECTS, viewer));
            effects.forEach(effect -> builder.addLore(
                    Component.text("• " + effect, UnifiedColorUtil.GRAY)));
        }

        // 하위 페이지 표시
        if (talent.hasSubPage()) {
            builder.addLore(Component.empty());
            builder.addLore(LangManager.text(LangKey.GUI_TALENT_HAS_SUB_PAGE, viewer));
        }

        builder.flags(ItemFlag.values());

        // 클릭 액션 설정
        return GuiItem.of(builder.build())
                .onClick(ClickType.LEFT, (player, clickType) -> handleTalentClick(player, talent, 1))
                .onClick(ClickType.SHIFT_LEFT, (player, clickType) -> handleTalentClick(player, talent, 5))
                .onClick(ClickType.RIGHT, (player, clickType) -> handleTalentSubPage(player, talent));
    }

    /**
     * 특성 클릭 처리
     */
    private void handleTalentClick(@NotNull Player player, @NotNull Talent talent, int levels) {
        if (!talent.canActivate(rpgPlayer.getTalents())) {
            playErrorSound(player);
            player.sendMessage(LangManager.text(LangKey.MESSAGES_TALENT_CANNOT_LEARN, player));
            return;
        }

        int currentLevel = rpgPlayer.getTalents().getTalentLevel(talent);
        int maxPossibleLevels = Math.min(levels, talent.getMaxLevel() - currentLevel);
        int affordableLevels = rpgPlayer.getTalents().getAvailablePoints() / talent.getRequiredPoints();
        int actualLevels = Math.min(maxPossibleLevels, affordableLevels);

        if (actualLevels <= 0) {
            playErrorSound(player);
            player.sendMessage(LangManager.text(LangKey.MESSAGES_NOT_ENOUGH_TALENT_POINTS, player));
            return;
        }

        // 레벨업 처리
        for (int i = 0; i < actualLevels; i++) {
            if (!talent.levelUp(rpgPlayer.getTalents())) {
                break;
            }
        }

        playSuccessSound(player);
        player.sendMessage(LangManager.text(LangKey.MESSAGES_TALENT_LEARNED, player,
                LangManager.text(LangKey.valueOf("TALENT_" + talent.getId().toUpperCase() + "_NAME"), player),
                String.valueOf(rpgPlayer.getTalents().getTalentLevel(talent))));

        refresh();
    }

    /**
     * 하위 페이지 열기
     */
    private void handleTalentSubPage(@NotNull Player player, @NotNull Talent talent) {
        if (!talent.hasSubPage()) {
            return;
        }

        String subPageId = talent.getPageId();
        if (subPageId == null) {
            return;
        }

        List<Talent> subTalents = RPGMain.getPlugin()
                .getTalentManager().getPageTalents(subPageId);

        if (subTalents.isEmpty()) {
            return;
        }

        TalentGui subGui = TalentGui.create(guiManager, player,
                rpgPlayer, subPageId, subTalents);
        guiManager.openGui(player, subGui);
        playClickSound(player);
    }

    // setupNavigationButtons 메소드 제거 - BaseGui의 setupStandardNavigation 사용

    /**
     * 페이지 타이틀 가져오기
     */
    private String getPageTitle() {
        // Page ID를 직접 반환 (Component API와 호환을 위해)
        return pageId;
    }

    @Override
    public GuiFramework getBackTarget() {
        // TalentGui는 ProfileGui로 돌아갑니다
        return new ProfileGui(guiManager, viewer);
    }
    
}