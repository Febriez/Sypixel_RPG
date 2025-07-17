package com.febrie.rpg.gui.impl.social;

import com.febrie.rpg.dto.social.MailDTO;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.social.MailManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.List;

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

    public MailDetailGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                        @NotNull Player player, @NotNull MailDTO mail) {
        super(player, guiManager, langManager, GUI_SIZE, "gui.mail-detail.title");
        this.mail = mail;
        this.mailManager = MailManager.getInstance();
        setupLayout();
        markAsRead();
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.text("우편: " + mail.getSubject(), ColorUtil.PRIMARY);
    }

    @Override
    protected GuiFramework getBackTarget() {
        return new MailboxGui(guiManager, langManager, viewer);
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
                new ItemBuilder(Material.PAPER)
                        .displayName(Component.text(mail.getSubject(), ColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("보낸 사람: " + mail.getFromPlayerName(), ColorUtil.WHITE))
                        .addLore(Component.text("받는 사람: " + mail.getToPlayerName(), ColorUtil.WHITE))
                        .addLore(Component.text("발송 시간: " + mail.getSentTime().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("상태: " + (mail.isRead() ? "읽음" : "새 우편"), 
                                mail.isRead() ? ColorUtil.GRAY : ColorUtil.SUCCESS))
                        .build()
        );
        setItem(MAIL_INFO_SLOT, mailInfoItem);

        // 메시지 내용
        String message = mail.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = "(메시지 없음)";
        }

        // 메시지를 여러 줄로 나누기 (25자씩)
        String[] messageLines = TextUtil.wrapTextOrDefault(message, 25, "(메시지 없음)");

        ItemBuilder messageBuilder = new ItemBuilder(Material.WRITTEN_BOOK)
                .displayName(Component.text("📄 메시지", ColorUtil.INFO)
                        .decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty())
                .addLore(messageLines.length > 0 ? 
                        Component.text(messageLines[0], ColorUtil.WHITE) :
                        Component.text("(메시지 없음)", ColorUtil.GRAY));

        // 추가 메시지 줄들
        for (int i = 1; i < Math.min(messageLines.length, 8); i++) {
            messageBuilder.addLore(Component.text(messageLines[i], ColorUtil.WHITE));
        }

        if (messageLines.length > 8) {
            messageBuilder.addLore(Component.text("...", ColorUtil.GRAY));
        }

        GuiItem messageItem = GuiItem.display(messageBuilder.build());

        setItem(MESSAGE_SLOT, messageItem);
    }

    /**
     * 첨부물 설정
     */
    private void setupAttachments() {
        if (!mail.hasAttachments()) {
            // 첨부물이 없는 경우
            setItem(ATTACHMENT_SLOTS[4], GuiItem.display( // 중앙 슬롯
                    new ItemBuilder(Material.BARRIER)
                            .displayName(Component.text("첨부물 없음", ColorUtil.ERROR))
                            .addLore(Component.text("이 우편에는 첨부물이 없습니다", ColorUtil.GRAY))
                            .build()
            ));
            return;
        }

        // 첨부물 표시
        List<ItemStack> attachments = mail.getAttachmentsAsItemStacks();
        
        for (int i = 0; i < Math.min(attachments.size(), ATTACHMENT_SLOTS.length); i++) {
            ItemStack attachment = attachments.get(i);
            if (attachment != null && !attachment.getType().isAir()) {
                
                // 첨부물 아이템에 설명 추가
                ItemBuilder builder = new ItemBuilder(attachment.clone())
                        .addLore(Component.empty())
                        .addLore(Component.text("📎 첨부물", ColorUtil.GOLD));
                
                if (mail.isCollected()) {
                    builder.addLore(Component.text("✓ 수령 완료", ColorUtil.SUCCESS));
                } else {
                    builder.addLore(Component.text("수령 대기 중", ColorUtil.YELLOW));
                }

                GuiItem attachmentItem = GuiItem.display(builder.build());
                setItem(ATTACHMENT_SLOTS[i], attachmentItem);
            }
        }

        // 나머지 슬롯은 데코레이션으로 채우기
        for (int i = attachments.size(); i < ATTACHMENT_SLOTS.length; i++) {
            setItem(ATTACHMENT_SLOTS[i], GuiFactory.createDecoration());
        }
    }

    /**
     * 액션 버튼들 설정
     */
    private void setupActionButtons() {
        // 첨부물 수령 버튼
        if (mail.hasAttachments() && !mail.isCollected()) {
            GuiItem collectButton = GuiItem.clickable(
                    new ItemBuilder(Material.CHEST)
                            .displayName(Component.text("📦 첨부물 수령", ColorUtil.SUCCESS)
                                    .decoration(TextDecoration.BOLD, true))
                            .addLore(Component.empty())
                            .addLore(Component.text("첨부물을 인벤토리로 가져옵니다", ColorUtil.GRAY))
                            .addLore(Component.text("첨부물 개수: " + mail.getAttachments().size() + "개", ColorUtil.WHITE))
                            .addLore(Component.empty())
                            .addLore(Component.text("클릭하여 수령", ColorUtil.YELLOW))
                            .build(),
                    p -> {
                        mailManager.collectAttachments(p, mail.getId()).thenAccept(success -> {
                            if (success) {
                                Bukkit.getScheduler().runTask(plugin, () -> {
                                    mail.setCollected(true);
                                    setupAttachments(); // 첨부물 표시 업데이트
                                    setupActionButtons(); // 버튼 업데이트
                                });
                            }
                        });
                        playSuccessSound(p);
                    }
            );
            setItem(COLLECT_BUTTON_SLOT, collectButton);
        } else if (mail.hasAttachments() && mail.isCollected()) {
            // 이미 수령한 경우
            setItem(COLLECT_BUTTON_SLOT, GuiItem.display(
                    new ItemBuilder(Material.LIME_DYE)
                            .displayName(Component.text("✓ 수령 완료", ColorUtil.SUCCESS)
                                    .decoration(TextDecoration.BOLD, true))
                            .addLore(Component.text("첨부물을 이미 수령했습니다", ColorUtil.GRAY))
                            .build()
            ));
        } else {
            // 첨부물이 없는 경우
            setItem(COLLECT_BUTTON_SLOT, GuiFactory.createDecoration());
        }

        // 삭제 버튼
        GuiItem deleteButton = GuiItem.clickable(
                new ItemBuilder(Material.LAVA_BUCKET)
                        .displayName(Component.text("🗑 우편 삭제", ColorUtil.ERROR)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("이 우편을 삭제합니다", ColorUtil.GRAY))
                        .addLore(Component.text("(첨부물도 함께 삭제됩니다)", ColorUtil.YELLOW))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 삭제", ColorUtil.YELLOW))
                        .build(),
                p -> {
                    mailManager.deleteMail(mail.getId()).thenAccept(success -> {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            if (success) {
                                p.sendMessage("§a우편을 삭제했습니다.");
                                MailboxGui mailboxGui = new MailboxGui(guiManager, langManager, p);
                                guiManager.openGui(p, mailboxGui);
                            } else {
                                p.sendMessage("§c우편 삭제에 실패했습니다.");
                            }
                        });
                    });
                    playClickSound(p);
                }
        );
        setItem(DELETE_BUTTON_SLOT, deleteButton);

        // 답장 버튼
        GuiItem replyButton = GuiItem.clickable(
                new ItemBuilder(Material.FEATHER)
                        .displayName(Component.text("✉ 답장하기", ColorUtil.INFO)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text(mail.getFromPlayerName() + "님에게 답장을 보냅니다", ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 답장", ColorUtil.YELLOW))
                        .build(),
                p -> {
                    p.closeInventory();
                    p.sendMessage("§e답장 보내기:");
                    p.sendMessage("§7'/우편보내기 " + mail.getFromPlayerName() + " Re:" + mail.getSubject() + " [메시지]'를 입력하세요.");
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
        if (!mail.isRead()) {
            mailManager.markAsRead(mail.getId());
            mail.setRead(true);
        }
    }


    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
}