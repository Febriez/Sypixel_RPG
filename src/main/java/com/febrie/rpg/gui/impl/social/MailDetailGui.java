package com.febrie.rpg.gui.impl.social;

import com.febrie.rpg.dto.social.MailDTO;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.social.MailManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.lang.GuiLangKey;
import com.febrie.rpg.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;

/**
 * 우편 상세 보기 GUI
 * 개별 우편의 내용과 첨부물을 확인하는 GUI
 *
 * @author Febrie
 */
public class MailDetailGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows

    // 우편 정보 슬롯
    private static final int MAIL_INFO_SLOT = 4;
    private static final int MESSAGE_SLOT = 13;

    // 첨부물 시작 슬롯 (3x3 영역)
    private static final int ATTACHMENTS_START_SLOT = 19;
    private static final int[] ATTACHMENT_SLOTS = {19, 20, 21, 28, 29, 30, 37, 38, 39};

    // 액션 버튼 슬롯
    private static final int COLLECT_BUTTON_SLOT = 31;
    private static final int DELETE_BUTTON_SLOT = 40;
    private static final int REPLY_BUTTON_SLOT = 49;

    private final MailDTO mail;
    private final MailManager mailManager;

    private MailDetailGui(@NotNull GuiManager guiManager,
                        @NotNull Player player, @NotNull MailDTO mail) {
        super(player, guiManager, GUI_SIZE, LangManager.text(GuiLangKey.GUI_MAIL_DETAIL_TITLE));
        this.mail = mail;
        this.mailManager = MailManager.getInstance();
    }

    /**
     * MailDetailGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param player 플레이어
     * @param mail 우편 데이터
     * @return 초기화된 MailDetailGui 인스턴스
     */
    public static MailDetailGui create(@NotNull GuiManager guiManager,
                                      @NotNull Player player, @NotNull MailDTO mail) {
        MailDetailGui gui = new MailDetailGui(guiManager, player, mail);
        gui.markAsRead();
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(LangKey.GUI_MAIL_DETAIL_TITLE, viewer, Component.text(mail.subject()));
    }

    @Override
    protected GuiFramework getBackTarget() {
        return MailboxGui.create(guiManager, viewer);
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupMailInfo();
        setupAttachments();
        setupActionButtons();
        setupStandardNavigation(true, true);
    }

    /**
     * 장식 요소 설정
     */
    private void setupDecorations() {
        createBorder();
    }

    /**
     * 우편 정보 설정
     */
    private void setupMailInfo() {
        // 우편 기본 정보
        GuiItem mailInfoItem = GuiItem.display(
                ItemBuilder.of(Material.PAPER)
                        .displayName(Component.text(mail.subject(), UnifiedColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_MAIL_DETAIL_SENDER, viewer, Component.text(mail.senderName())))
                        .addLore(LangManager.text(LangKey.GUI_MAIL_DETAIL_RECEIVER, viewer, Component.text(mail.receiverName())))
                        .addLore(LangManager.text(LangKey.GUI_MAIL_DETAIL_SENT_TIME, viewer, Component.text(java.time.Instant.ofEpochMilli(mail.sentAt()).atZone(java.time.ZoneId.systemDefault()).format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_MAIL_DETAIL_STATUS, viewer, 
                                LangManager.text(mail.isUnread() ? LangKey.STATUS_NEW_MAIL : LangKey.STATUS_READ, viewer)))
                        .hideAllFlags()
                        .build()
        );
        setItem(MAIL_INFO_SLOT, mailInfoItem);

        // 메시지 내용
        String message = mail.content();
        if (message == null || message.trim().isEmpty()) {
            message = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(
                    LangManager.text(LangKey.GUI_MAIL_DETAIL_NO_MESSAGE, viewer));
        }

        // 메시지를 여러 줄로 나누기 (25자씩)
        String[] messageLines = TextUtil.wrapTextOrDefault(message, 25, 
                net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(
                        LangManager.text(LangKey.GUI_MAIL_DETAIL_NO_MESSAGE, viewer)));

        ItemBuilder messageBuilder = ItemBuilder.of(Material.WRITTEN_BOOK)
                .displayName(LangManager.text(LangKey.ITEMS_SOCIAL_MAIL_DETAIL_MESSAGE_NAME, viewer))
                .addLore(Component.empty())
                .addLore(messageLines.length > 0 ? 
                        Component.text(messageLines[0], UnifiedColorUtil.WHITE) :
                        LangManager.text(LangKey.GUI_MAIL_DETAIL_NO_MESSAGE, viewer));

        // 추가 메시지 줄들
        for (int i = 1; i < Math.min(messageLines.length, 8); i++) {
            messageBuilder.addLore(Component.text(messageLines[i], UnifiedColorUtil.WHITE));
        }

        if (messageLines.length > 8) {
            messageBuilder.addLore(Component.text("...", UnifiedColorUtil.GRAY));
        }

        GuiItem messageItem = GuiItem.display(messageBuilder.build());

        setItem(MESSAGE_SLOT, messageItem);
    }

    /**
     * 첨부물 설정
     */
    private void setupAttachments() {
        // 첨부물 기능은 현재 MailDTO에 포함되지 않음
        // 첨부물이 없는 경우로 표시
        setItem(ATTACHMENT_SLOTS[4], GuiItem.display( // 중앙 슬롯
                ItemBuilder.of(Material.BARRIER)
                        .displayName(LangManager.text(LangKey.ITEMS_SOCIAL_MAIL_DETAIL_NO_ATTACHMENTS_NAME, viewer))
                        .addLore(LangManager.text(LangKey.ITEMS_SOCIAL_MAIL_DETAIL_NO_ATTACHMENTS_LORE, viewer))
                        .hideAllFlags()
                        .build()
        ));
        
        // 나머지 슬롯은 데코레이션으로 채우기
        for (int i = 0; i < ATTACHMENT_SLOTS.length; i++) {
            if (i != 4) { // 중앙 슬롯 제외
                setItem(ATTACHMENT_SLOTS[i], GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 액션 버튼들 설정
     */
    private void setupActionButtons() {
        // 첨부물 수령 버튼 - 현재 첨부물 기능 없음
        // 의미 없는 버튼 설정
        setItem(COLLECT_BUTTON_SLOT, GuiFactory.createDecoration());

        // 삭제 버튼
        GuiItem deleteButton = GuiItem.clickable(
                ItemBuilder.of(Material.LAVA_BUCKET)
                        .displayName(LangManager.text(LangKey.ITEMS_SOCIAL_MAIL_DETAIL_DELETE_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.list(LangKey.ITEMS_SOCIAL_MAIL_DETAIL_DELETE_LORE, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SOCIAL_MAIL_DETAIL_DELETE_CLICK, viewer))
                        .hideAllFlags()
                        .build(),
                p -> {
                    mailManager.deleteMail(mail.mailId()).thenAccept(success -> {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            if (success) {
                                p.sendMessage(LangManager.text(LangKey.GUI_MAIL_DETAIL_DELETE_SUCCESS, p));
                                MailboxGui mailboxGui = MailboxGui.create(guiManager, p);
                                guiManager.openGui(p, mailboxGui);
                            } else {
                                p.sendMessage(LangManager.text(LangKey.GUI_MAIL_DETAIL_DELETE_FAILED, p).color(UnifiedColorUtil.ERROR));
                            }
                        });
                    });
                    playClickSound(p);
                }
        );
        setItem(DELETE_BUTTON_SLOT, deleteButton);

        // 답장 버튼
        GuiItem replyButton = GuiItem.clickable(
                ItemBuilder.of(Material.FEATHER)
                        .displayName(LangManager.text(LangKey.ITEMS_SOCIAL_MAIL_DETAIL_REPLY_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_MAIL_DETAIL_REPLY_DESC, viewer, Component.text(mail.senderName())))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SOCIAL_MAIL_DETAIL_REPLY_CLICK, viewer))
                        .hideAllFlags()
                        .build(),
                p -> {
                    p.closeInventory();
                    p.sendMessage(LangManager.text(LangKey.GUI_MAIL_DETAIL_REPLY_GUIDE, p));
                    p.sendMessage(LangManager.text(LangKey.GUI_MAIL_DETAIL_REPLY_COMMAND, p, 
                            Component.text(mail.senderName()), Component.text(mail.subject())));
                    playClickSound(p);
                }
        );
        setItem(REPLY_BUTTON_SLOT, replyButton);

        // 나머지 슬롯은 데코레이션으로 채우기
        for (int i = 0; i < GUI_SIZE; i++) {
            if (getItem(i) == null) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 우편을 읽음 상태로 변경
     */
    private void markAsRead() {
        if (mail.isUnread()) {
            mailManager.markAsRead(mail.mailId());
            // Note: MailDTO is immutable, so we can't update the local copy
            // The GUI will need to be refreshed to see the updated state
        }
    }
    
}