package com.febrie.rpg.gui.impl.social;

import com.febrie.rpg.dto.social.MailDTO;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.system.MainMenuGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.social.MailManager;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import net.wesjd.anvilgui.AnvilGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 우편함 GUI
 * 받은 우편을 확인하고 관리하는 GUI
 *
 * @author Febrie
 */
public class MailboxGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows

    // 메뉴 버튼 슬롯
    private static final int REFRESH_SLOT = 10;
    private static final int SEND_MAIL_SLOT = 13;
    private static final int DELETE_ALL_READ_SLOT = 16;

    // 우편 목록 시작 슬롯
    private static final int MAILS_START_SLOT = 19;
    private static final int MAILS_END_SLOT = 43;

    // 타이틀 슬롯
    private static final int TITLE_SLOT = 4;

    private final MailManager mailManager;
    private boolean showReadMails = false;
    private List<MailDTO> mails = new ArrayList<>();

    private MailboxGui(@NotNull GuiManager guiManager,
                     @NotNull Player player) {
        super(player, guiManager, GUI_SIZE, "gui.mailbox.title");
        this.mailManager = MailManager.getInstance();
    }

    /**
     * MailboxGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param player 플레이어
     * @return 초기화된 MailboxGui 인스턴스
     */
    public static MailboxGui create(@NotNull GuiManager guiManager,
                                   @NotNull Player player) {
        MailboxGui gui = new MailboxGui(guiManager, player);
        gui.loadMails();
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("social.mailbox.title");
    }

    @Override
    protected GuiFramework getBackTarget() {
        return MainMenuGui.create(guiManager, viewer);
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupMenuButtons();
        setupStandardNavigation(true, true);
    }

    /**
     * 장식 요소 설정
     */
    private void setupDecorations() {
        createBorder();
        setupTitleItem();
    }

    /**
     * 타이틀 아이템 설정
     */
    private void setupTitleItem() {
        GuiItem titleItem = GuiItem.display(
                new ItemBuilder(Material.CHEST)
                        .displayName(trans("social.mailbox.title-icon")
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(trans("social.mailbox.description"))
                        .build()
        );
        setItem(TITLE_SLOT, titleItem);
    }

    /**
     * 메뉴 버튼들 설정
     */
    private void setupMenuButtons() {
        // 새로고침 버튼
        GuiItem refreshButton = GuiItem.clickable(
                new ItemBuilder(Material.CLOCK)
                        .displayName(trans("social.mailbox.refresh")
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(trans("social.mailbox.refresh-desc"))
                        .addLore(Component.empty())
                        .addLore(trans("social.mailbox.click-refresh"))
                        .build(),
                p -> {
                    mailManager.clearCache(p.getUniqueId());
                    loadMails();
                    p.sendMessage(LangManager.getMessage(p, "social.mailbox.refresh-success"));
                    playClickSound(p);
                }
        );
        setItem(REFRESH_SLOT, refreshButton);

        // 우편 보내기 버튼
        GuiItem sendMailButton = GuiItem.clickable(
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .displayName(trans("social.mailbox.send-mail")
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(trans("social.mailbox.send-mail-desc"))
                        .addLore(Component.empty())
                        .addLore(trans("social.mailbox.click-to-write"))
                        .build(),
                p -> {
                    p.closeInventory();
                    p.sendMessage(LangManager.getMessage(p, "social.mailbox.send-mail-guide"));
                    p.sendMessage(LangManager.getMessage(p, "social.mailbox.send-mail-command"));
                    p.sendMessage(LangManager.getMessage(p, "social.mailbox.send-mail-example"));
                    playClickSound(p);
                }
        );
        setItem(SEND_MAIL_SLOT, sendMailButton);

        // 읽은 우편 삭제 버튼
        GuiItem deleteReadButton = GuiItem.clickable(
                new ItemBuilder(Material.LAVA_BUCKET)
                        .displayName(trans("social.mailbox.delete-read")
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(trans("social.mailbox.delete-read-desc"))
                        .addLore(trans("social.mailbox.except-attachments"))
                        .addLore(Component.empty())
                        .addLore(trans("social.mailbox.click-to-delete"))
                        .build(),
                p -> {
                    // 읽은 우편 일괄 삭제 구현
                    deleteAllReadMails(p);
                    playClickSound(p);
                }
        );
        setItem(DELETE_ALL_READ_SLOT, deleteReadButton);

        // 빈 슬롯들을 투명한 유리판으로 채우기
        for (int i = 0; i < MAILS_START_SLOT; i++) {
            if (getItem(i) == null) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
        for (int i = MAILS_END_SLOT + 1; i < GUI_SIZE; i++) {
            if (getItem(i) == null) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 우편 목록 로드
     */
    private void loadMails() {
        // 우편 목록 영역 초기화
        for (int i = MAILS_START_SLOT; i <= MAILS_END_SLOT; i++) {
            setItem(i, GuiFactory.createDecoration());
        }

        // 로딩 표시
        setItem(MAILS_START_SLOT + 12, GuiItem.display(
                new ItemBuilder(Material.HOPPER)
                        .displayName(trans("social.mailbox.loading"))
                        .build()
        ));

        // 비동기로 우편 목록 로드
        mailManager.getMails(viewer.getUniqueId(), showReadMails).thenAccept(loadedMails -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                this.mails = loadedMails;
                displayMails(this.mails);
            });
        });
    }

    /**
     * 우편 목록 표시
     */
    private void displayMails(@NotNull List<MailDTO> mails) {
        // 우편 목록 영역 초기화
        for (int i = MAILS_START_SLOT; i <= MAILS_END_SLOT; i++) {
            setItem(i, GuiFactory.createDecoration());
        }

        if (mails.isEmpty()) {
            // 우편이 없을 때
            setItem(MAILS_START_SLOT + 12, GuiItem.display(
                    new ItemBuilder(Material.BARRIER)
                            .displayName(Component.text(showReadMails ? "우편이 없습니다" : "읽지 않은 우편이 없습니다", UnifiedColorUtil.ERROR))
                            .addLore(Component.text("새로운 우편이 오면 알림을 받을 수 있습니다", UnifiedColorUtil.GRAY))
                            .build()
            ));
            return;
        }

        // 우편 아이템 생성
        int slot = MAILS_START_SLOT;
        for (MailDTO mail : mails) {
            if (slot > MAILS_END_SLOT) break;

            // 우편 상태에 따른 아이콘
            Material material;
            if (mail.isUnread()) {
                material = Material.PAPER; // 읽지 않은 우편
            } else {
                material = Material.MAP; // 읽은 우편
            }

            String status = "§7읽음";
            if (mail.isUnread()) {
                status = "§e새 우편";
            }

            GuiItem mailItem = GuiItem.clickable(
                    new ItemBuilder(material)
                            .displayName(Component.text(mail.subject(), UnifiedColorUtil.PRIMARY)
                                    .decoration(TextDecoration.BOLD, mail.isUnread()))
                            .addLore(Component.empty())
                            .addLore(Component.text("보낸 사람: " + mail.senderName(), UnifiedColorUtil.WHITE))
                            .addLore(Component.text("상태: " + status, UnifiedColorUtil.GRAY))
                            .addLore(Component.text("시간: " + java.time.Instant.ofEpochMilli(mail.sentAt()).atZone(java.time.ZoneId.systemDefault()).format(
                                    DateTimeFormatter.ofPattern("MM-dd HH:mm")), UnifiedColorUtil.GRAY))
                            .addLore(Component.empty())
                            .addLore(Component.text("클릭하여 우편 확인", UnifiedColorUtil.YELLOW))
                            .build(),
                    p -> {
                        MailDetailGui detailGui = MailDetailGui.create(guiManager, p, mail);
                        guiManager.openGui(p, detailGui);
                        playClickSound(p);
                    }
            );

            setItem(slot, mailItem);
            slot++;
        }
    }

    /**
     * 읽은 우편 일괄 삭제
     */
    private void deleteAllReadMails(Player player) {
        List<MailDTO> readMailsToDelete = mails.stream()
                .filter(mail -> !mail.isUnread())
                .toList();
        
        if (readMailsToDelete.isEmpty()) {
            sendMessage(player, "gui.mailbox.message.no_read_mails_to_delete");
            return;
        }
        
        // 삭제 확인 GUI 표시
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return List.of(AnvilGUI.ResponseAction.run(() -> {}));
                    }
                    String text = stateSnapshot.getText();
                    if ("삭제".equals(text)) {
                        int deletedCount = 0;
                        for (MailDTO mail : readMailsToDelete) {
                            // 우편 삭제 처리
                            mails.remove(mail);
                            deletedCount++;
                        }
                        
                        sendMessage(player, "gui.mailbox.message.deleted_read_mails", String.valueOf(deletedCount));
                        
                        // GUI 새로고침
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            loadMails();
                            setupLayout();
                        });
                        return List.of(AnvilGUI.ResponseAction.close());
                    } else {
                        sendMessage(player, "gui.mailbox.message.delete_confirmation_invalid");
                        return List.of(AnvilGUI.ResponseAction.close());
                    }
                })
                .text("삭제하려면 '삭제' 입력")
                .title("읽은 우편 " + readMailsToDelete.size() + "개 삭제 확인")
                .plugin(guiManager.getPlugin())
                .open(player);
    }
    
    /**
     * 플레이어에게 메시지 전송
     */
    private void sendMessage(Player player, String key, String... args) {
        LangManager.sendMessage(player, key, args);
    }
}