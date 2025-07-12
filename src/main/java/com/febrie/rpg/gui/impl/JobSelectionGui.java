package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 직업 선택 GUI
 * 플레이어가 직업을 선택할 수 있는 인터페이스
 *
 * @author Febrie, CoffeeTory
 */
public class JobSelectionGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6줄

    private final RPGPlayer rpgPlayer;
    private JobType.JobCategory selectedCategory = JobType.JobCategory.WARRIOR;

    public JobSelectionGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                           @NotNull Player player, @NotNull RPGPlayer rpgPlayer) {
        super(player, guiManager, langManager, GUI_SIZE, "gui.job-selection.title");
        this.rpgPlayer = rpgPlayer;
        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("gui.job-selection.title");
    }

    @Override
    public int getSize() {
        return GUI_SIZE;
    }

    @Override
    protected void setupLayout() {
        setupBackground();
        setupCategoryTabs();
        setupJobDisplay();
        setupNavigationButtons();
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        // 상단 테두리
        for (int i = 0; i < 9; i++) {
            if (i < 3 || i > 5) { // 카테고리 탭 위치 제외
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 중간 구분선
        for (int i = 18; i < 27; i++) {
            setItem(i, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
        }

        // 하단 테두리 - 네비게이션 버튼 위치 제외
        for (int i = 45; i < 54; i++) {
            if (i != 45 && i != 53) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 좌우 테두리
        setItem(9, GuiFactory.createDecoration());
        setItem(17, GuiFactory.createDecoration());
        setItem(36, GuiFactory.createDecoration());
        setItem(44, GuiFactory.createDecoration());
    }

    /**
     * 카테고리 탭 설정
     */
    private void setupCategoryTabs() {
        int tabSlot = 3;

        for (JobType.JobCategory category : JobType.JobCategory.values()) {
            boolean isSelected = category == selectedCategory;

            GuiItem tabItem = GuiItem.clickable(
                    ItemBuilder.of(isSelected ? Material.ENCHANTED_BOOK : Material.BOOK)
                            .displayName(trans("job.categories." + category.name().toLowerCase() + ".name")
                                    .decoration(TextDecoration.BOLD, true))
                            .addLore(trans(isSelected ? "gui.job-selection.selected" : "gui.job-selection.click-to-select"))
                            .flags(ItemFlag.values())
                            .glint(isSelected)
                            .build(),
                    clickPlayer -> {
                        selectedCategory = category;
                        refresh();
                        playClickSound(clickPlayer);
                    }
            );

            setItem(tabSlot++, tabItem);
        }
    }

    /**
     * 직업 표시
     */
    private void setupJobDisplay() {
        // 카테고리 정보 표시
        GuiItem categoryInfo = GuiItem.display(
                ItemBuilder.of(selectedCategory.getIcon())
                        .displayName(trans("job.categories." + selectedCategory.name().toLowerCase() + ".name")
                                .append(trans("gui.job-selection.category-suffix"))
                                .decoration(TextDecoration.BOLD, true))
                        .lore(langManager.getComponentList(viewer, "job.categories." +
                                selectedCategory.name().toLowerCase() + ".description"))
                        .flags(ItemFlag.values())
                        .build()
        );
        setItem(13, categoryInfo);

        // 해당 카테고리의 직업들 표시
        List<JobType> categoryJobs = new ArrayList<>();
        for (JobType job : JobType.values()) {
            if (job.getCategory() == selectedCategory) {
                categoryJobs.add(job);
            }
        }

        // 직업 표시 위치: 29, 31, 33
        int[] jobSlots = {29, 31, 33};
        for (int i = 0; i < categoryJobs.size() && i < jobSlots.length; i++) {
            JobType job = categoryJobs.get(i);
            setItem(jobSlots[i], createJobItem(job));
        }
    }

    /**
     * 직업 아이템 생성
     */
    private GuiItem createJobItem(@NotNull JobType job) {
        String jobKey = job.name().toLowerCase();

        ItemBuilder builder = ItemBuilder.of(job.getMaterial())
                .displayName(Component.text(job.getIcon() + " ")
                        .append(trans("job." + jobKey + ".name"))
                        .decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty())
                .addLore(trans("gui.job-selection.max-level", "level", String.valueOf(job.getMaxLevel())))
                .addLore(Component.empty());

        // 직업 설명 추가
        List<Component> description = langManager.getComponentList(viewer, "job." + jobKey + ".description");
        for (Component line : description) {
            builder.addLore(line);
        }

        builder.addLore(Component.empty())
                .addLore(trans("general.separator"))
                .addLore(trans("gui.job-selection.warning"))
                .addLore(trans("general.separator"))
                .addLore(Component.empty())
                .addLore(trans("gui.job-selection.click-to-choose"))
                .flags(ItemFlag.values())
                .glint(true);

        return GuiItem.clickable(
                builder.build(),
                _ -> openConfirmationGui(job)
        );
    }

    /**
     * 네비게이션 버튼 설정 - 위치 통일
     */
    private void setupNavigationButtons() {
        // 표준 네비게이션 설정 사용
        setupStandardNavigation(false, true); // refresh 버튼 없음, close 버튼 있음
    }

    /**
     * 직업 선택 확인 GUI 열기
     */
    private void openConfirmationGui(@NotNull JobType job) {
        // 확인 GUI 열기
        JobConfirmationGui confirmationGui = new JobConfirmationGui(
                guiManager, langManager, viewer, rpgPlayer, job
        );
        guiManager.openGui(viewer, confirmationGui);
        playClickSound(viewer);
    }
}