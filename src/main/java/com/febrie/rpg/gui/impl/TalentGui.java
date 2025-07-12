package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.ScrollableGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.gui.util.GuiUtility;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.talent.Talent;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 특성 관리 GUI - 스크롤 가능한 그리드 레이아웃
 * GuiManager 중앙 집중식 네비게이션으로 개선
 *
 * @author Febrie, CoffeeTory
 */
public class TalentGui extends ScrollableGui {

    private final GuiManager guiManager;
    private final LangManager langManager;
    private final RPGPlayer rpgPlayer;
    private final Map<Integer, GuiItem> items = new HashMap<>();

    // 현재 페이지 정보
    private final String pageId;
    private final List<Talent> pageTalents;

    public TalentGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                     @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer,
                     @Nullable String pageId, @NotNull List<Talent> talents) {
        super(viewer);
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.rpgPlayer = rpgPlayer;
        this.pageId = pageId != null ? pageId : "main";
        this.pageTalents = talents;

        String titleKey = pageId != null && !pageId.equals("main") ? "gui.talent.title-with-page" : "gui.talent.title";
        String pageTitle = pageId != null ? transString("gui.talent.pages." + pageId, transString("gui.talent.pages.default")) : "";
        this.inventory = Bukkit.createInventory(this, 54,
                pageId != null && !pageId.equals("main")
                        ? langManager.getComponent(viewer, titleKey, "page", pageTitle)
                        : langManager.getComponent(viewer, titleKey));

        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return pageId != null && !pageId.equals("main")
                ? trans("gui.talent.title-with-page", "page", getPageTitle())
                : trans("gui.talent.title");
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
        List<GuiItem> talentItems = new ArrayList<>();
        Talent.TalentHolder talents = rpgPlayer.getTalents();

        for (Talent talent : pageTalents) {
            int currentLevel = talents.getTalentLevel(talent);
            boolean isMaxLevel = currentLevel >= talent.getMaxLevel();
            boolean canLearn = talent.canActivate(talents);

            // 특성 아이템 생성
            ItemBuilder builder = ItemBuilder.of(talent.getIcon())
                    .displayName(Component.text(talent.getName(),
                                    isMaxLevel ? ColorUtil.LEGENDARY : ColorUtil.UNCOMMON)
                            .decoration(TextDecoration.BOLD, true))
                    .addLore(Component.empty());

            // 레벨 정보
            if (isMaxLevel) {
                builder.addLore(trans("gui.talent.talent-level-max",
                        "current", String.valueOf(currentLevel),
                        "max", String.valueOf(talent.getMaxLevel())));
            } else {
                builder.addLore(trans("gui.talent.talent-level",
                        "current", String.valueOf(currentLevel),
                        "max", String.valueOf(talent.getMaxLevel())));
            }

            // 필요 포인트
            if (!isMaxLevel) {
                builder.addLore(trans("gui.talent.required-points",
                        "points", String.valueOf(talent.getRequiredPoints())));
            }

            builder.addLore(Component.empty());

            // 특성 설명
            for (String line : talent.getDescription()) {
                builder.addLore(Component.text(line, ColorUtil.GRAY));
            }

            // 현재 효과
            if (currentLevel > 0) {
                builder.addLore(Component.empty());
                builder.addLore(trans("gui.talent.current-effect"));
                String effect = talent.getEffectDescription(currentLevel);
                builder.addLore(Component.text("  " + effect, ColorUtil.GREEN));
            }

            // 다음 레벨 효과
            if (!isMaxLevel && currentLevel < talent.getMaxLevel()) {
                builder.addLore(Component.empty());
                builder.addLore(trans("gui.talent.next-effect"));
                String nextEffect = talent.getEffectDescription(currentLevel + 1);
                builder.addLore(Component.text("  " + nextEffect, ColorUtil.YELLOW));
            }

            // 요구사항
            if (!talent.getRequirements().isEmpty()) {
                builder.addLore(Component.empty());
                builder.addLore(trans("gui.talent.requirements"));
                for (Talent.Requirement req : talent.getRequirements()) {
                    boolean met = req.isMet(rpgPlayer);
                    Component reqText = Component.text("  • " + req.getDescription(),
                            met ? ColorUtil.GREEN : ColorUtil.RED);
                    builder.addLore(reqText);
                }
            }

            // 상태 표시
            builder.addLore(Component.empty());
            if (isMaxLevel) {
                builder.addLore(trans("gui.talent.max-level-reached"));
                builder.enchant(Enchantment.BINDING_CURSE, 1);
            } else if (canLearn) {
                builder.addLore(trans("gui.talent.click-to-learn"));
            } else {
                builder.addLore(trans("gui.talent.cannot-learn"));
            }

            // 하위 페이지가 있는 경우
            if (talent.hasSubPage()) {
                builder.addLore(Component.empty());
                builder.addLore(trans("gui.talent.has-sub-page"));
                builder.glint(true);
            }

            builder.flags(ItemFlag.values());

            GuiItem talentItem = GuiItem.clickable(
                    builder.build(),
                    player -> handleTalentClick(player, talent, currentLevel, canLearn)
            );

            talentItems.add(talentItem);
        }

        return talentItems;
    }

    @Override
    protected void handleNonScrollClick(@NotNull InventoryClickEvent event, @NotNull Player player,
                                        int slot, @NotNull ClickType click) {
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

        // 정보 표시
        setupInfoDisplay();

        // 스크롤 가능한 특성 표시
        updateScrollableContent();

        // 네비게이션 버튼
        setupNavigationButtons();
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        // 상단 테두리 (정보 표시 영역 제외)
        for (int i = 1; i < 8; i++) {
            if (i != 4) { // 중앙 정보 슬롯 제외
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 좌우 테두리
        for (int row = 1; row < 5; row++) {
            setItem(row * 9, GuiFactory.createDecoration());
            setItem(row * 9 + 8, GuiFactory.createDecoration());
        }

        // 하단 영역 (네비게이션)
        for (int i = 45; i < 54; i++) {
            if (i != 45 && i != 49 && i != 50 && i != 53) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 스크롤바 영역 설정
        if (getTotalPages() > 1) {
            int scrollBarStart = 9;
            int scrollBarEnd = 36;
            updateScrollBar(scrollBarStart, scrollBarEnd, () -> createScrollBarItem(scrollBarStart),
                    () -> createScrollBarItem(scrollBarEnd));
        }
    }

    /**
     * 네비게이션 버튼 설정 - GuiManager 통합
     */
    private void setupNavigationButtons() {
        // 뒤로가기 버튼 - GuiManager가 처리
        if (guiManager.canGoBack(viewer)) {
            setItem(45, GuiItem.clickable(
                    ItemBuilder.of(Material.ARROW)
                            .displayName(trans("gui.buttons.back.name"))
                            .addLore(trans("gui.buttons.back.lore"))
                            .build(),
                    player -> guiManager.goBack(player)
            ));
        }

        // 새로고침 버튼
        setItem(49, GuiFactory.createRefreshButton(player -> refresh(), langManager, viewer));

        // 스탯 페이지로 가기 버튼
        setItem(50, GuiItem.clickable(
                ItemBuilder.of(Material.IRON_SWORD)
                        .displayName(trans("gui.stats.title"))
                        .addLore(trans("gui.talent.click-stats"))
                        .build(),
                player -> {
                    StatsGui statsGui = new StatsGui(guiManager, langManager, player, rpgPlayer);
                    guiManager.openGui(player, statsGui);
                }
        ));

        // 닫기 버튼
        setItem(53, GuiFactory.createCloseButton(langManager, viewer));
    }

    /**
     * 정보 표시 영역
     */
    private void setupInfoDisplay() {
        // 현재 페이지 정보 (좌측 상단)
        String jobName = rpgPlayer.hasJob() ?
                transString("job." + rpgPlayer.getJob().name().toLowerCase() + ".name") :
                transString("general.unknown");

        GuiItem pageInfo = GuiItem.display(
                ItemBuilder.of(Material.KNOWLEDGE_BOOK)
                        .displayName(Component.text(getPageTitle(), ColorUtil.EPIC)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(trans("gui.talent.level", "level", String.valueOf(rpgPlayer.getLevel())))
                        .addLore(trans(rpgPlayer.hasJob() ? "gui.talent.job" : "gui.talent.no-job",
                                "job", jobName))
                        .build()
        );
        setItem(0, pageInfo);

        // 전투력 정보 (중앙 상단) - 클릭 가능
        GuiItem combatPowerInfo = GuiItem.clickable(
                ItemBuilder.of(Material.DIAMOND_SWORD)
                        .displayName(trans("gui.stats.combat-power", "power", String.valueOf(rpgPlayer.getCombatPower())))
                        .addLore(trans("gui.stats.click-combat-power"))
                        .flags(ItemFlag.values())
                        .build(),
                player -> {
                    // 나중에 CombatPowerGui 구현 시 사용
                    sendMessage(player, "general.coming-soon");
                    playClickSound(player);
                }
        );
        setItem(4, combatPowerInfo);

        // 특성 포인트 정보 (우측 상단)
        Talent.TalentHolder talents = rpgPlayer.getTalents();
        GuiItem talentPointInfo = GuiItem.display(
                ItemBuilder.of(Material.EXPERIENCE_BOTTLE)
                        .displayName(trans("gui.talent.points-info"))
                        .addLore(trans("gui.talent.available-points",
                                "points", String.valueOf(talents.getAvailablePoints())))
                        .addLore(trans("gui.talent.spent-points",
                                "points", String.valueOf(talents.getSpentPoints())))
                        .addLore(Component.empty())
                        .addLore(trans("gui.talent.points-per-level"))
                        .build()
        );
        setItem(8, talentPointInfo);
    }

    /**
     * 특성 클릭 처리
     */
    private void handleTalentClick(@NotNull Player player, @NotNull Talent talent,
                                   int currentLevel, boolean canLearn) {
        // 하위 페이지가 있는 경우
        if (talent.hasSubPage()) {
            openSubPage(talent);
            playClickSound(player);
            return;
        }

        // 최대 레벨인 경우
        if (currentLevel >= talent.getMaxLevel()) {
            sendMessage(player, "messages.talent-max-level");
            playErrorSound(player);
            return;
        }

        // 배울 수 없는 경우
        if (!canLearn) {
            sendMessage(player, "messages.talent-requirements-not-met");
            playErrorSound(player);
            return;
        }

        // 특성 학습
        if (talent.levelUp(rpgPlayer.getTalents())) {
            sendMessage(player, "messages.talent-learned", "talent", talent.getName(langManager.getMessage(viewer, "general.language-code").equals("ko_KR")));
            playSuccessSound(player);

            // 스탯 보너스 업데이트
            updateStatBonuses();

            // GUI 새로고침
            refresh();
        } else {
            sendMessage(player, "messages.talent-learn-failed");
            playErrorSound(player);
        }
    }

    /**
     * 하위 페이지 열기 - GuiManager 사용
     */
    private void openSubPage(@NotNull Talent talent) {
        TalentGui subPageGui = new TalentGui(
                guiManager, langManager, rpgPlayer.getBukkitPlayer(), rpgPlayer,
                talent.getPageId(), talent.getChildren()
        );

        // GuiManager가 자동으로 히스토리 관리
        guiManager.openGui(rpgPlayer.getBukkitPlayer(), subPageGui);
    }

    /**
     * 스탯 보너스 업데이트
     */
    private void updateStatBonuses() {
        Map<com.febrie.rpg.stat.Stat, Integer> bonuses = rpgPlayer.getTalents().calculateStatBonuses();

        for (Map.Entry<com.febrie.rpg.stat.Stat, Integer> entry : bonuses.entrySet()) {
            rpgPlayer.getStats().setBonusStat(entry.getKey(), entry.getValue());
        }

        rpgPlayer.saveToPDC();
    }

    /**
     * 페이지 제목 가져오기
     */
    private String getPageTitle() {
        return transString("gui.talent.pages." + pageId, transString("gui.talent.pages.default"));
    }

    /**
     * 아이템 설정 헬퍼
     */
    private void setItem(int slot, @NotNull GuiItem item) {
        GuiUtility.setItem(slot, item, items, inventory);
    }

    /**
     * Helper methods
     */
    private Component trans(@NotNull String key, @NotNull String... args) {
        return langManager.getComponent(viewer, key, args);
    }

    private String transString(@NotNull String key, @NotNull String... args) {
        return langManager.getMessage(viewer, key, args);
    }

    private void sendMessage(@NotNull Player player, @NotNull String key, @NotNull String... args) {
        langManager.sendMessage(player, key, args);
    }

    private void playErrorSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }

    private void playSuccessSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    private void playClickSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
    }
}