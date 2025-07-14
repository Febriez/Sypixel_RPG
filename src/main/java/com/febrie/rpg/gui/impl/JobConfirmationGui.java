package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

/**
 * 직업 선택 확인 GUI
 * 직업을 최종적으로 선택하기 전에 한 번 더 확인하는 작은 GUI
 *
 * @author Febrie, CoffeeTory
 */
public class JobConfirmationGui extends BaseGui {

    private static final int GUI_SIZE = 27; // 3줄 (작은 확인창)

    private final RPGPlayer rpgPlayer;
    private final JobType selectedJob;

    public JobConfirmationGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                              @NotNull Player player, @NotNull RPGPlayer rpgPlayer, @NotNull JobType selectedJob) {
        super(player, guiManager, langManager, GUI_SIZE, "gui.job-confirmation.title");
        this.rpgPlayer = rpgPlayer;
        this.selectedJob = selectedJob;
        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("gui.job-confirmation.title");
    }

    @Override
    public int getSize() {
        return GUI_SIZE;
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }

    @Override
    protected void setupLayout() {
        setupBackground();
        setupJobDisplay();
        setupConfirmationButtons();
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        // 전체 배경을 검은색 유리판으로
        for (int i = 0; i < GUI_SIZE; i++) {
            setItem(i, GuiFactory.createDecoration(Material.BLACK_STAINED_GLASS_PANE));
        }

        // 중요 정보 강조를 위한 빨간색 유리판 테두리
        int[] redSlots = {0, 1, 2, 6, 7, 8, 9, 17, 18, 19, 20, 24, 25, 26};
        for (int slot : redSlots) {
            setItem(slot, GuiFactory.createDecoration(Material.RED_STAINED_GLASS_PANE));
        }
    }

    /**
     * 선택한 직업 표시
     */
    private void setupJobDisplay() {
        String jobKey = selectedJob.name().toLowerCase();

        // 중앙에 직업 정보 표시
        ItemBuilder builder = ItemBuilder.of(selectedJob.getMaterial())
                .displayName(Component.text(selectedJob.getIcon() + " ")
                        .append(trans("job." + jobKey + ".name"))
                        .color(selectedJob.getColor())
                        .decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty())
                .addLore(trans("gui.job-confirmation.selected-job"))
                .addLore(Component.empty());

        // 직업 설명
        List<Component> description = langManager.getComponentList(viewer, "job." + jobKey + ".description");
        for (Component line : description) {
            builder.addLore(line);
        }

        builder.addLore(Component.empty())
                .addLore(trans("gui.job-confirmation.max-level", "level", String.valueOf(selectedJob.getMaxLevel())))
                .addLore(Component.empty())
                .addLore(trans("general.separator"))
                .addLore(trans("gui.job-confirmation.warning"))
                .addLore(trans("gui.job-confirmation.cannot-change"))
                .addLore(trans("general.separator"))
                .flags(ItemFlag.values())
                .glint(true);

        setItem(13, GuiItem.display(builder.build()));

        // 추가 경고 아이콘들
        GuiItem warningItem = GuiItem.display(
                ItemBuilder.of(Material.BARRIER)
                        .displayName(trans("gui.job-confirmation.warning-title"))
                        .addLore(trans("gui.job-confirmation.warning-description"))
                        .build()
        );
        setItem(3, warningItem);
        setItem(5, warningItem);
    }

    /**
     * 확인/취소 버튼 설정
     */
    private void setupConfirmationButtons() {
        // 확인 버튼 (좌측)
        GuiItem confirmButton = GuiItem.clickable(
                ItemBuilder.of(Material.LIME_WOOL)
                        .displayName(trans("gui.job-confirmation.confirm")
                                .color(ColorUtil.SUCCESS)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(trans("gui.job-confirmation.confirm-description",
                                "job", transString("job." + selectedJob.name().toLowerCase() + ".name")))
                        .addLore(Component.empty())
                        .addLore(trans("gui.job-confirmation.click-to-confirm"))
                        .glint(true)
                        .build(),
                player -> handleConfirm()
        );
        setItem(11, confirmButton);

        // 취소 버튼 (우측)
        GuiItem cancelButton = GuiItem.clickable(
                ItemBuilder.of(Material.RED_WOOL)
                        .displayName(trans("gui.job-confirmation.cancel")
                                .color(ColorUtil.ERROR)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(trans("gui.job-confirmation.cancel-description"))
                        .addLore(Component.empty())
                        .addLore(trans("gui.job-confirmation.click-to-cancel"))
                        .build(),
                player -> handleCancel()
        );
        setItem(15, cancelButton);

        // 추가 정보 아이템 (하단 중앙)
        GuiItem infoItem = GuiItem.display(
                ItemBuilder.of(Material.BOOK)
                        .displayName(trans("gui.job-confirmation.info-title"))
                        .addLore(trans("gui.job-confirmation.info-line1"))
                        .addLore(trans("gui.job-confirmation.info-line2"))
                        .addLore(trans("gui.job-confirmation.info-line3"))
                        .build()
        );
        setItem(22, infoItem);
    }

    /**
     * 확인 버튼 처리
     */
    private void handleConfirm() {
        if (rpgPlayer.setJob(selectedJob)) {
            String jobName = transString("job." + selectedJob.name().toLowerCase() + ".name");

            // 성공 메시지
            sendMessage(viewer, "gui.job-confirmation.success", "job", jobName);

            // 축하 효과
            playSuccessSound(viewer);

            // Paper API의 showTitle 메서드 사용
            viewer.showTitle(Title.title(
                    trans("gui.job-confirmation.title-success"),
                    trans("gui.job-confirmation.subtitle-success", "job", jobName),
                    Title.Times.times(
                            Duration.ofMillis(500),   // fadeIn
                            Duration.ofMillis(3000),  // stay
                            Duration.ofMillis(1000)   // fadeOut
                    )
            ));

            // GUI 닫고 프로필로 이동
            viewer.closeInventory();
            guiManager.openProfileGui(viewer);

        } else {
            // 이미 직업이 있는 경우 (보통 일어나지 않아야 함)
            sendMessage(viewer, "gui.job-confirmation.already-has-job");
            playErrorSound(viewer);
            viewer.closeInventory();
        }
    }

    /**
     * 취소 버튼 처리
     */
    private void handleCancel() {
        // 직업 선택 GUI로 돌아가기
        playClickSound(viewer);
        guiManager.goBack(viewer);
    }
}