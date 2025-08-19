package com.febrie.rpg.quest.dialog;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.dialog.DialogManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 퀘스트 대화 선택지 GUI
 * 플레이어가 선택할 수 있는 대화 옵션을 표시
 *
 * @author Febrie, CoffeeTory
 */
public class DialogChoiceGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows
    
    private final QuestDialog dialog;
    private final QuestDialog.DialogLine dialogLine;
    private final DialogManager.DialogProgress progress;
    
    private DialogChoiceGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                           @NotNull QuestDialog dialog,
                           @NotNull QuestDialog.DialogLine dialogLine,
                           @NotNull DialogManager.DialogProgress progress) {
        super(viewer, guiManager, GUI_SIZE, "gui.dialog-choice.title");
        this.dialog = dialog;
        this.dialogLine = dialogLine;
        this.progress = progress;
    }

    /**
     * Factory method to create the GUI
     */
    public static DialogChoiceGui create(@NotNull GuiManager guiManager,
                                        @NotNull Player player,
                                        @NotNull QuestDialog dialog,
                                        @NotNull QuestDialog.DialogLine dialogLine,
                                        @NotNull DialogManager.DialogProgress progress) {
        DialogChoiceGui gui = new DialogChoiceGui(player, guiManager, dialog, dialogLine, progress);
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("gui.dialog-choice.title");
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        displayDialogContent();
        displayChoices();
    }

    private void setupDecorations() {
        createBorder();
        
        // 대화 아이콘
        GuiItem dialogIcon = GuiItem.display(
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .displayName(trans("gui.dialog-choice.icon"))
                        .addLore(trans("gui.dialog-choice.description"))
                        .build()
        );
        setItem(4, dialogIcon);
    }

    private void displayDialogContent() {
        // NPC 대화 내용 표시
        GuiItem npcDialog = GuiItem.display(
                new ItemBuilder(Material.PLAYER_HEAD)
                        .displayName(trans("gui.dialog-choice.npc"))
                        .addLore(trans("gui.dialog-choice.npc-content"))
                        .build()
        );
        setItem(13, npcDialog);
    }

    private void displayChoices() {
        List<QuestDialog.DialogChoice> choices = dialogLine.getChoices();
        if (choices == null || choices.isEmpty()) {
            return;
        }

        // 선택지 위치 계산 (중앙 정렬)
        int[] slots = getChoiceSlots(choices.size());
        
        for (int i = 0; i < choices.size() && i < slots.length; i++) {
            QuestDialog.DialogChoice choice = choices.get(i);
            
            GuiItem choiceItem = GuiItem.clickable(
                    new ItemBuilder(Material.PAPER)
                            .displayName(trans("gui.dialog-choice.option", String.valueOf(i + 1)))
                            .addLore(choice.getText().color(UnifiedColorUtil.WHITE))
                            .addLore(Component.empty())
                            .addLore(trans("gui.dialog-choice.click-to-select"))
                            .build(),
                    p -> {
                        // 선택 처리
                        DialogManager.getInstance().handleChoice(p, dialog.getId(), choice.id());
                        p.closeInventory();
                        playClickSound(p);
                    }
            );
            
            setItem(slots[i], choiceItem);
        }
    }

    private int[] getChoiceSlots(int choiceCount) {
        // 선택지 개수에 따른 슬롯 위치 반환
        return switch (choiceCount) {
            case 1 -> new int[]{31};
            case 2 -> new int[]{30, 32};
            case 3 -> new int[]{29, 31, 33};
            case 4 -> new int[]{29, 30, 32, 33};
            case 5 -> new int[]{28, 29, 31, 33, 34};
            case 6 -> new int[]{28, 29, 30, 32, 33, 34};
            default -> new int[]{29, 30, 31, 32, 33}; // 5개 이상은 기본 5개 슬롯 사용
        };
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        // 다이얼로그 선택지는 뒤로가기 없음
        return null;
    }
    
}