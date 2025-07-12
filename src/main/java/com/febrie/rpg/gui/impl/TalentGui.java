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

import java.util.*;

/**
 * 특성 관리 GUI - 스크롤 가능한 그리드 레이아웃
 * 웹 형태의 특성 트리를 탐색하고 특성을 배울 수 있음
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
    private final Stack<PageInfo> pageHistory = new Stack<>();

    // 페이지 정보를 저장하는 내부 클래스
    private record PageInfo(String pageId, List<Talent> talents) {
    }

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
        String pageTitle = pageId != null ? getPageTitle() : "";

        this.inventory = Bukkit.createInventory(this, guiSize,
                langManager.getComponent(viewer, titleKey, "page", pageTitle));

        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        if (pageId != null && !pageId.equals("main")) {
            return trans("gui.talent.title-with-page", "page", getPageTitle());
        }
        return trans("gui.talent.title");
    }

    @Override
    public int getSize() {
        return guiSize;
    }

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void refresh() {
        inventory.clear();
        items.clear();
        updateScroll();
        setupLayout();
    }

    @Override
    public @NotNull org.bukkit.inventory.Inventory getInventory() {
        return inventory;
    }

    @Override
    protected List<GuiItem> getScrollableItems() {
        return createTalentItems();
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
        setupBackground();
        setupInfoDisplay();
        setupTalentDisplay();
        setupScrollButtons();
        setupNavigationButtons();
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        // 상단 테두리
        for (int i = 0; i < 9; i++) {
            if (i != 0 && i != 4 && i != 8) { // 정보 표시 슬롯 제외
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 하단 테두리 - 네비게이션 버튼 위치 제외
        for (int i = 45; i < 54; i++) {
            if (i != 45 && i != 49 && i != 50 && i != 53) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 좌우 테두리
        setItem(9, GuiFactory.createDecoration());
        setItem(18, GuiFactory.createDecoration());
        setItem(27, GuiFactory.createDecoration());
        setItem(36, GuiFactory.createDecoration());

        // 스크롤 버튼 위치 제외
        if (getMaxScroll() == 0) {
            setItem(17, GuiFactory.createDecoration());
            setItem(26, GuiFactory.createDecoration());
            setItem(35, GuiFactory.createDecoration());
            setItem(44, GuiFactory.createDecoration());
        }
    }

    /**
     * 특성 아이템들 생성
     */
    private List<GuiItem> createTalentItems() {
        List<GuiItem> talentItems = new ArrayList<>();

        for (Talent talent : pageTalents) {
            talentItems.add(createTalentItem(talent));
        }

        return talentItems;
    }

    /**
     * 특성 아이템 생성
     */
    private GuiItem createTalentItem(@NotNull Talent talent) {
        Talent.TalentHolder talentHolder = rpgPlayer.getTalents();
        int currentLevel = talentHolder.getTalentLevel(talent);
        boolean canActivate = talent.canActivate(talentHolder);
        boolean isMaxLevel = currentLevel >= talent.getMaxLevel();

        // 이름 가져오기 - Talent에 언어별 이름이 하드코딩되어 있으므로 그대로 사용
        boolean isKorean = transString("general.language-code").equals("ko_KR");
        String talentName = talent.getName(isKorean);

        ItemBuilder builder = ItemBuilder.of(talent.getIcon())
                .displayName(Component.text(talentName, talent.getColor())
                        .decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty());

        // 레벨 정보
        if (talent.getMaxLevel() > 1) {
            String levelKey = isMaxLevel ? "gui.talent.talent-level-max" : "gui.talent.talent-level";
            builder.addLore(trans(levelKey,
                    "current", String.valueOf(currentLevel),
                    "max", String.valueOf(talent.getMaxLevel())));
        }

        // 필요 포인트
        if (!isMaxLevel) {
            builder.addLore(trans("gui.talent.required-points",
                    "points", String.valueOf(talent.getRequiredPoints())));
        }

        builder.addLore(Component.empty());

        // 설명 - Talent의 getDescription이 언어 지원하므로 그대로 사용
        List<Component> description = talent.getDescription(isKorean, currentLevel);
        for (Component line : description) {
            builder.addLore(line);
        }

        builder.addLore(Component.empty());

        // 상호작용 안내
        if (isMaxLevel) {
            builder.addLore(trans("gui.talent.max-level-reached"));
        } else if (canActivate) {
            builder.addLore(trans("gui.talent.click-learn"));
        } else {
            if (talentHolder.getAvailablePoints() < talent.getRequiredPoints()) {
                builder.addLore(trans("gui.talent.insufficient-points"));
            } else {
                builder.addLore(trans("gui.talent.prerequisite-not-met"));
            }
        }

        if (talent.hasSubPage()) {
            builder.addLore(trans("gui.talent.click-subpage"));
        }

        // 이미 배운 특성은 인챈트 효과
        if (currentLevel > 0) {
            builder.enchant(Enchantment.UNBREAKING, currentLevel);
        }

        builder.flags(ItemFlag.values());

        GuiItem item = GuiItem.of(builder.build());

        // 좌클릭 - 특성 배우기
        item.onClick(ClickType.LEFT, (player, click) -> {
            if (isMaxLevel) {
                playErrorSound(player);
                return;
            }

            if (talent.levelUp(talentHolder)) {
                refresh();
                player.playSound(player.getLocation(),
                        org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                sendMessage(player, "messages.talent-learned", "talent", talentName);

                // 스탯 보너스 업데이트
                updateStatBonuses();
            } else {
                playErrorSound(player);
            }
        });

        // 우클릭 - 하위 페이지 열기
        if (talent.hasSubPage()) {
            item.onClick(ClickType.RIGHT, (player, click) -> {
                openSubPage(talent);
            });
        }

        return item;
    }

    /**
     * 특성 표시 설정
     */
    private void setupTalentDisplay() {
        setupScrollableArea(inventory, items, this::setItem);
    }

    /**
     * 스크롤 버튼 설정
     */
    private void setupScrollButtons() {
        if (getMaxScroll() > 0) {
            setItem(scrollUpSlot, createScrollUpButton());
            setItem(scrollDownSlot, createScrollDownButton());

            // 스크롤바
            setItem(scrollBarStart, createScrollBarItem(scrollBarStart));
            setItem(26, createScrollBarItem(26));
            setItem(scrollBarEnd, createScrollBarItem(scrollBarEnd));
        }
    }

    /**
     * 네비게이션 버튼 설정 - 위치 통일
     */
    private void setupNavigationButtons() {
        // 뒤로가기 버튼 - 45번 슬롯 (좌측 하단)
        setItem(45, GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(trans("gui.buttons.back.name"))
                        .addLore(trans("gui.buttons.back.lore"))
                        .build(),
                player -> goToPreviousPage()
        ));

        // 새로고침 버튼 - 49번 슬롯 (중앙 하단)
        setItem(49, GuiFactory.createRefreshButton(player -> refresh(), langManager, viewer));

        // 스탯 페이지로 가기 버튼 - 50번 슬롯
        setItem(50, GuiItem.clickable(
                ItemBuilder.of(Material.IRON_SWORD)
                        .displayName(trans("gui.stats.title"))
                        .addLore(trans("gui.talent.click-stats"))
                        .build(),
                player -> {
                    if (guiManager != null) {
                        // closeInventory() 제거하여 마우스 위치 유지
                        StatsGui statsGui = new StatsGui(guiManager, langManager, player, rpgPlayer);
                        statsGui.open(player);
                    }
                }
        ));

        // 닫기 버튼 - 53번 슬롯 (우측 하단)
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
                    // closeInventory() 제거
                    CombatPowerGui combatPowerGui = new CombatPowerGui(guiManager, langManager, player, rpgPlayer);
                    combatPowerGui.open(player);
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
     * 하위 페이지 열기
     */
    private void openSubPage(@NotNull Talent talent) {
        // 현재 페이지 정보 저장
        pageHistory.push(new PageInfo(pageId, pageTalents));

        // 새 GUI 생성하여 열기 - closeInventory() 제거
        TalentGui subPageGui = new TalentGui(
                guiManager, langManager, rpgPlayer.getBukkitPlayer(), rpgPlayer,
                talent.getPageId(), talent.getChildren()
        );

        // 페이지 히스토리 복사
        subPageGui.pageHistory.addAll(this.pageHistory);

        subPageGui.open(rpgPlayer.getBukkitPlayer());
    }

    /**
     * 이전 페이지로 돌아가기
     */
    private void goToPreviousPage() {
        if (pageHistory.isEmpty()) {
            // 메인 메뉴로
            if (guiManager != null) {
                guiManager.openMainMenuGui(rpgPlayer.getBukkitPlayer());
            }
            return;
        }

        PageInfo previousPage = pageHistory.pop();

        // 이전 페이지 GUI 생성 - closeInventory() 제거
        TalentGui previousGui = new TalentGui(
                guiManager, langManager, rpgPlayer.getBukkitPlayer(), rpgPlayer,
                previousPage.pageId, previousPage.talents
        );

        // 남은 히스토리 복사
        previousGui.pageHistory.addAll(this.pageHistory);

        previousGui.open(rpgPlayer.getBukkitPlayer());
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