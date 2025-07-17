package com.febrie.rpg.gui.impl.quest;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.SoundUtil;
import com.febrie.rpg.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 퀘스트 대화 GUI
 * 한글자씩 애니메이션으로 대화를 표시하는 시스템
 *
 * @author Febrie
 */
public class QuestDialogGui extends BaseGui {

    private final Quest quest;

    // 대화 상태
    private int currentDialogIndex = 0;
    private int currentCharIndex = 0;
    private String currentDialog = "";
    private boolean isTyping = false;
    private boolean isDialogComplete = false;

    // 애니메이션 관리
    private BukkitTask typingTask;
    private final int typingSpeed; // 틱 단위 (기본값: 2틱 = 100ms)

    // 슬롯 정의
    private static final int DIALOG_SLOT = 4; // 가운데 슬롯
    private static final int EXIT_SLOT = 0; // 나가기 버튼

    public QuestDialogGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                          @NotNull Player player, @NotNull Quest quest) {
        super(player, guiManager, langManager, 9, "gui.quest-dialog.title");
        this.quest = quest;
        this.typingSpeed = getTypingSpeed(player); // 플레이어 설정에서 가져오기
    }

    /**
     * 플레이어의 대화 속도 설정 가져오기
     */
    private int getTypingSpeed(@NotNull Player player) {
        return RPGMain.getPlugin().getRPGPlayerManager().getOrCreatePlayer(player).getPlayerSettings().getDialogSpeed();
    }

    @Override
    protected void setupLayout() {
        setupDialogGui();
        startDialog();
    }

    @Override
    public @NotNull Component getTitle() {
        return langManager.getComponent(viewer, "gui.quest-dialog.title").color(ColorUtil.GOLD);
    }

    @Override
    protected GuiFramework getBackTarget() {
        return null; // 뒤로가기 없음 (대화 중)
    }

    /**
     * 대화 GUI 초기 설정
     */
    private void setupDialogGui() {
        // 나가기 버튼 설정
        GuiItem exitButton = GuiItem.clickable(
                new ItemBuilder(Material.BARRIER)
                        .displayName(langManager.getComponent(viewer, "gui.quest-dialog.close").color(ColorUtil.ERROR))
                        .addLore(langManager.getComponent(viewer, "gui.buttons.close.lore").color(ColorUtil.GRAY))
                        .build(),
                p -> {
                    stopTyping();
                    p.closeInventory();
                    SoundUtil.playCloseSound(p);
                }
        );

        setItem(EXIT_SLOT, exitButton);

        // 빈 슬롯들을 투명한 유리판으로 채우기
        for (int i = 1; i < 9; i++) {
            if (i != DIALOG_SLOT) {
                setItem(i, GuiItem.display(
                        new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                                .displayName(Component.empty())
                                .build()
                ));
            }
        }
    }

    /**
     * 대화 시작
     */
    private void startDialog() {
        if (quest.getDialogCount() == 0) {
            // 대화가 없으면 바로 퀘스트 수락 화면으로
            openQuestAcceptGui();
            return;
        }

        loadCurrentDialog();
        startTypingAnimation();
    }

    /**
     * 현재 대화 로드
     */
    private void loadCurrentDialog() {
        String dialog = quest.getDialog(currentDialogIndex);
        if (dialog != null) {
            currentDialog = dialog;
            currentCharIndex = 0;
            isDialogComplete = false;
            isTyping = true;
        }
    }

    /**
     * 타이핑 애니메이션 시작
     */
    private void startTypingAnimation() {
        if (typingTask != null) {
            typingTask.cancel();
        }

        typingTask = Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::updateDialogDisplay,
                0L,
                typingSpeed
        );
    }

    /**
     * 대화 표시 업데이트
     */
    private void updateDialogDisplay() {
        if (!isTyping || currentCharIndex >= currentDialog.length()) {
            // 타이핑 완료
            isTyping = false;
            isDialogComplete = true;
            if (typingTask != null) {
                typingTask.cancel();
                typingTask = null;
            }
            updateDialogItem(currentDialog, true);
            return;
        }

        // 한글자씩 표시
        currentCharIndex++;
        String displayText = currentDialog.substring(0, currentCharIndex);
        updateDialogItem(displayText, false);
    }

    /**
     * 대화 아이템 업데이트
     */
    private void updateDialogItem(@NotNull String text, boolean isComplete) {
        List<Component> lore = new ArrayList<>();

        // 텍스트를 로어로 분할 (한 줄당 최대 40자)
        String[] lines = TextUtil.wrapText(text, 40);
        for (String line : lines) {
            lore.add(Component.text(line, ColorUtil.WHITE));
        }

        // 상태 표시
        lore.add(Component.empty());
        if (isComplete) {
            if (currentDialogIndex < quest.getDialogCount() - 1) {
                lore.add(langManager.getComponent(viewer, "gui.quest-dialog.next-page").color(ColorUtil.SUCCESS));
            } else {
                lore.add(langManager.getComponent(viewer, "gui.quest-dialog.accept-quest").color(ColorUtil.GOLD));
            }
        } else {
            lore.add(langManager.getComponent(viewer, "gui.quest-dialog.skip").color(ColorUtil.YELLOW));
        }

        ItemBuilder dialogBuilder = new ItemBuilder(Material.ENCHANTED_BOOK)
                .displayName(langManager.getComponent(viewer, "gui.quest-dialog.npc-name", "{npc}", quest.getNPCName()).color(ColorUtil.AQUA));

        for (Component loreLine : lore) {
            dialogBuilder.addLore(loreLine);
        }

        GuiItem dialogItem = GuiItem.clickable(dialogBuilder.build(), this::handleDialogClick);
        setItem(DIALOG_SLOT, dialogItem);
    }


    /**
     * 대화 클릭 처리
     */
    private void handleDialogClick(@NotNull Player player) {
        if (isTyping) {
            // 타이핑 중이면 즉시 완료
            stopTyping();
            isDialogComplete = true;
            updateDialogItem(currentDialog, true);
            SoundUtil.playClickSound(player);
        } else if (isDialogComplete) {
            // 대화가 완료된 상태에서 클릭
            if (currentDialogIndex < quest.getDialogCount() - 1) {
                // 다음 대화로
                currentDialogIndex++;
                loadCurrentDialog();
                startTypingAnimation();
                SoundUtil.playClickSound(player);
            } else {
                // 마지막 대화 완료, 퀘스트 수락 화면으로
                openQuestAcceptGui();
            }
        }
    }

    /**
     * 타이핑 애니메이션 중지
     */
    private void stopTyping() {
        if (typingTask != null) {
            typingTask.cancel();
            typingTask = null;
        }
        isTyping = false;
    }

    /**
     * 퀘스트 수락 화면 열기
     */
    private void openQuestAcceptGui() {
        stopTyping();

        // 현재 GUI를 닫고 퀘스트 수락 GUI 열기
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            guiManager.openQuestAcceptGui(viewer, quest);
        }, 1L);
    }

}