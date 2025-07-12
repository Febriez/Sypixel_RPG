package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.ScrollableGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.stat.Stat;
import com.febrie.rpg.talent.Talent;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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

    private final RPGPlayer rpgPlayer;
    private final Map<Integer, GuiItem> items = new HashMap<>();

    // 현재 페이지 정보
    private final String pageId;
    private final List<Talent> pageTalents;

    public TalentGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                     @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer,
                     @Nullable String pageId, @NotNull List<Talent> talents) {
        super(viewer, guiManager, langManager, 54,
                pageId != null && !pageId.equals("main") ? "gui.talent.title-with-page" : "gui.talent.title",
                pageId != null ? getPageTitleForId(pageId, langManager, viewer) : "");

        this.rpgPlayer = rpgPlayer;
        this.pageId = pageId != null ? pageId : "main";
        this.pageTalents = talents;

        setupLayout();
    }

    /**
     * 페이지 ID에 따른 제목 가져오기
     */
    private static String getPageTitleForId(String pageId, LangManager langManager, Player viewer) {
        return langManager.getMessage(viewer, "gui.talent.pages." + pageId,
                langManager.getMessage(viewer, "gui.talent.pages.default"));
    }

    @Override
    public @NotNull Component getTitle() {
        return pageId != null && !pageId.equals("main")
                ? trans("gui.talent.title-with-page", "page", getPageTitle())
                : trans("gui.talent.title");
    }

    @Override
    protected void setupLayout() {
        // 배경 설정
        setupBackground();

        // 정보 표시
        setupInfoDisplay();

        // 스크롤 가능한 특성 표시
        setupScrollableArea(inventory, items, this::setItem);

        // 네비게이션 버튼
        setupNavigationButtons();
    }

    @Override
    protected List<GuiItem> getScrollableItems() {
        List<GuiItem> talentItems = new ArrayList<>();
        Talent.TalentHolder talents = rpgPlayer.getTalents();

        for (Talent talent : pageTalents) {
            int currentLevel = talents.getTalentLevel(talent);
            boolean isMaxLevel = currentLevel >= talent.getMaxLevel();
            boolean canLearn = talent.canActivate(talents);

            // 특성 이름 가져오기
            String talentName = transString("talent." + talent.getId() + ".name");

            // 특성 아이템 생성
            ItemBuilder builder = ItemBuilder.of(talent.getIcon())
                    .displayName(Component.text(talentName,
                                    isMaxLevel ? ColorUtil.LEGENDARY : talent.getColor())
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

            // 특성 설명 (lang 파일에서 가져오기)
            List<Component> description = langManager.getComponentList(viewer,
                    "talent." + talent.getId() + ".description");
            for (Component line : description) {
                builder.addLore(line);
            }

            // 선행 조건 표시
            Map<Talent, Integer> prerequisites = talent.getPrerequisites();
            if (!prerequisites.isEmpty()) {
                builder.addLore(Component.empty());
                builder.addLore(trans("gui.talent.prerequisites"));

                for (Map.Entry<Talent, Integer> reqEntry : prerequisites.entrySet()) {
                    Talent reqTalent = reqEntry.getKey();
                    int reqLevel = reqEntry.getValue();
                    int playerLevel = talents.getTalentLevel(reqTalent);
                    boolean met = playerLevel >= reqLevel;

                    String reqTalentName = transString("talent." + reqTalent.getId() + ".name");
                    Component reqText = Component.text("  • " + reqTalentName + " Lv." + reqLevel,
                            met ? ColorUtil.GREEN : ColorUtil.RED);
                    builder.addLore(reqText);
                }
            }

            // 상태 표시
            builder.addLore(Component.empty());
            if (isMaxLevel) {
                builder.addLore(trans("gui.talent.max-level-reached"));
                builder.enchant(Enchantment.UNBREAKING, 1);
            } else if (canLearn) {
                builder.addLore(trans("gui.talent.click-learn"));
            } else if (talents.getAvailablePoints() < talent.getRequiredPoints()) {
                builder.addLore(trans("gui.talent.insufficient-points"));
            } else {
                builder.addLore(trans("gui.talent.prerequisite-not-met"));
            }

            // 하위 페이지가 있는 경우
            if (talent.hasSubPage()) {
                builder.addLore(Component.empty());
                builder.addLore(trans("gui.talent.click-subpage"));
                builder.glint(true);
            }

            builder.flags(ItemFlag.values());

            GuiItem talentItem = new GuiItem(builder.build())
                    .onClick(ClickType.LEFT, (player, click) -> handleTalentClick(player, talent, currentLevel, canLearn, ClickType.LEFT))
                    .onClick(ClickType.RIGHT, (player, click) -> handleTalentClick(player, talent, currentLevel, canLearn, ClickType.RIGHT));

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

        // 스크롤 버튼 설정
        if (getMaxScroll() > 0) {
            setItem(scrollUpSlot, createScrollUpButton());
            setItem(scrollDownSlot, createScrollDownButton());
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
                    guiManager::goBack
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
                transString("gui.talent.no-job");

        GuiItem pageInfo = GuiItem.display(
                ItemBuilder.of(Material.ENCHANTED_BOOK)
                        .displayName(trans("gui.talent.page-info"))
                        .addLore(trans("gui.talent.current-page", "page", getPageTitle()))
                        .addLore(trans("gui.talent.job", "job", jobName))
                        .addLore(Component.empty())
                        .addLore(trans("gui.talent.available-points",
                                "points", String.valueOf(rpgPlayer.getTalents().getAvailablePoints())))
                        .build()
        );
        setItem(4, pageInfo);
    }

    /**
     * 특성 클릭 처리
     */
    private void handleTalentClick(@NotNull Player player, @NotNull Talent talent,
                                   int currentLevel, boolean canLearn, @NotNull ClickType clickType) {
        // 하위 페이지가 있고 우클릭인 경우
        if (talent.hasSubPage() && clickType == ClickType.RIGHT) {
            openSubPage(talent);
            playClickSound(player);
            return;
        }

        // 특성 학습 처리 (좌클릭)
        if (clickType == ClickType.LEFT && canLearn) {
            if (talent.levelUp(rpgPlayer.getTalents())) {
                String talentName = transString("talent." + talent.getId() + ".name");
                sendMessage(player, "gui.talent.talent-learned", "talent", talentName);
                playSuccessSound(player);

                // 스탯 보너스 업데이트
                updateStatBonuses();

                // GUI 새로고침
                refresh();
            } else {
                sendMessage(player, "messages.talent-learn-failed");
                playErrorSound(player);
            }
        } else {
            // 학습 불가능한 경우
            playErrorSound(player);
        }
    }

    /**
     * 하위 페이지 열기 - GuiManager 사용
     */
    private void openSubPage(@NotNull Talent talent) {
        TalentGui subPageGui = new TalentGui(
                guiManager, langManager, rpgPlayer.getPlayer(), rpgPlayer,
                talent.getPageId(), talent.getChildren()
        );

        // GuiManager가 자동으로 히스토리 관리
        guiManager.openGui(rpgPlayer.getPlayer(), subPageGui);
    }

    /**
     * 스탯 보너스 업데이트
     */
    private void updateStatBonuses() {
        Map<Stat, Integer> bonuses = rpgPlayer.getTalents().calculateStatBonuses();

        for (Map.Entry<Stat, Integer> entry : bonuses.entrySet()) {
            rpgPlayer.getStats().setBonusStat(entry.getKey(), entry.getValue());
        }

        // RPGPlayer의 내부 저장은 자동으로 이루어짐 (private saveToPDC 호출)
        // 또는 RPGPlayerManager를 통한 저장
        // savePlayerData는 RPGPlayerManager에서 처리됨
    }

    /**
     * 페이지 제목 가져오기
     */
    private String getPageTitle() {
        return transString("gui.talent.pages." + pageId, transString("gui.talent.pages.default"));
    }
}