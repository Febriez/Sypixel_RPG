package com.febrie.rpg.quest.dialog;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
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
    
    private DialogChoiceGui(@NotNull GuiManager guiManager,
                           @NotNull LangManager langManager,
                           @NotNull Player player,
                           @NotNull QuestDialog dialog,
                           @NotNull QuestDialog.DialogLine dialogLine,
                           @NotNull DialogManager.DialogProgress progress) {
        super(player, guiManager, langManager, GUI_SIZE, "gui.dialog-choice.title");
        this.dialog = dialog;
        this.dialogLine = dialogLine;
        this.progress = progress;
    }

    /**
     * Factory method to create the GUI
     */
    public static DialogChoiceGui create(@NotNull GuiManager guiManager,
                                        @NotNull LangManager langManager,
                                        @NotNull Player player,
                                        @NotNull QuestDialog dialog,
                                        @NotNull QuestDialog.DialogLine dialogLine,
                                        @NotNull DialogManager.DialogProgress progress) {
        DialogChoiceGui gui = new DialogChoiceGui(guiManager, langManager, player, dialog, dialogLine, progress);
        return createAndInitialize(gui, "gui.dialog-choice.title");
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.text("대화 선택", ColorUtil.PRIMARY);
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
                        .displayName(Component.text("대화 선택", ColorUtil.PRIMARY))
                        .addLore(Component.text("원하는 대답을 선택하세요", ColorUtil.GRAY))
                        .build()
        );
        setItem(4, dialogIcon);
    }

    private void displayDialogContent() {
        // NPC 대화 내용 표시
        boolean isKorean = viewer.locale().toString().startsWith("ko");
        
        GuiItem npcDialog = GuiItem.of(
                new ItemBuilder(Material.PLAYER_HEAD)
                        .displayName(Component.text("NPC", ColorUtil.GOLD))
                        .addLore(Component.text("대화 내용", ColorUtil.WHITE))
        );
        setItem(13, npcDialog);
    }

    private void displayChoices() {
        List<QuestDialog.DialogChoice> choices = dialogLine.getChoices();
        if (choices == null || choices.isEmpty()) {
            return;
        }

        boolean isKorean = viewer.locale().toString().startsWith("ko");
        
        // 선택지 위치 계산 (중앙 정렬)
        int[] slots = getChoiceSlots(choices.size());
        
        for (int i = 0; i < choices.size() && i < slots.length; i++) {
            QuestDialog.DialogChoice choice = choices.get(i);
            
            GuiItem choiceItem = GuiItem.clickable(
                    new ItemBuilder(Material.PAPER)
                            .displayName(Component.text("[선택 " + (i + 1) + "]", ColorUtil.YELLOW))
                            .addLore(Component.text(choice.getText(isKorean), ColorUtil.WHITE))
                            .addLore(Component.empty())
                            .addLore(Component.text("클릭하여 선택", ColorUtil.GRAY))
                            .build(),
                    p -> {
                        // 선택 처리
                        DialogManager.getInstance().handleChoice(p, dialog.getId(), choice.getId());
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