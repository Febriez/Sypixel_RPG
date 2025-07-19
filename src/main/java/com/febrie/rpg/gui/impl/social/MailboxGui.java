package com.febrie.rpg.gui.impl.social;

import com.febrie.rpg.dto.social.MailDTO;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.system.MainMenuGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.social.MailManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
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

    public MailboxGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                     @NotNull Player player) {
        super(player, guiManager, langManager, GUI_SIZE, "gui.mailbox.title");
        this.mailManager = MailManager.getInstance();
        setupLayout();
        loadMails();
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.text("우편함", ColorUtil.PRIMARY);
    }

    @Override
    protected GuiFramework getBackTarget() {
        return MainMenuGui.create(guiManager, langManager, viewer);
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
                        .displayName(Component.text("📬 우편함", ColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("받은 우편을 확인하세요!", ColorUtil.GRAY))
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
                        .displayName(Component.text("🔄 새로고침", ColorUtil.INFO)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("우편 목록을 새로고침합니다", ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 새로고침", ColorUtil.YELLOW))
                        .build(),
                p -> {
                    mailManager.clearCache(p.getUniqueId());
                    loadMails();
                    p.sendMessage("§a우편 목록을 새로고침했습니다.");
                    playClickSound(p);
                }
        );
        setItem(REFRESH_SLOT, refreshButton);

        // 우편 보내기 버튼
        GuiItem sendMailButton = GuiItem.clickable(
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .displayName(Component.text("📝 우편 보내기", ColorUtil.SUCCESS)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("새로운 우편을 보냅니다", ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 작성", ColorUtil.YELLOW))
                        .build(),
                p -> {
                    p.closeInventory();
                    p.sendMessage("§e우편 보내기:");
                    p.sendMessage("§7'/우편보내기 <플레이어명> <제목> [메시지]'를 입력하세요.");
                    p.sendMessage("§7예시: /우편보내기 Steve 선물 안녕하세요!");
                    playClickSound(p);
                }
        );
        setItem(SEND_MAIL_SLOT, sendMailButton);

        // 읽은 우편 삭제 버튼
        GuiItem deleteReadButton = GuiItem.clickable(
                new ItemBuilder(Material.LAVA_BUCKET)
                        .displayName(Component.text("🗑 읽은 우편 삭제", ColorUtil.ERROR)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("읽은 우편을 모두 삭제합니다", ColorUtil.GRAY))
                        .addLore(Component.text("(첨부물이 있는 우편 제외)", ColorUtil.YELLOW))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 삭제", ColorUtil.YELLOW))
                        .build(),
                p -> {
                    // TODO: 읽은 우편 일괄 삭제 구현
                    p.sendMessage("§c기능 준비 중입니다.");
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
                        .displayName(Component.text("로딩 중...", ColorUtil.GRAY))
                        .build()
        ));

        // 비동기로 우편 목록 로드
        mailManager.getMails(viewer.getUniqueId(), showReadMails).thenAccept(mails -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                displayMails(mails);
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
                            .displayName(Component.text(showReadMails ? "우편이 없습니다" : "읽지 않은 우편이 없습니다", ColorUtil.ERROR))
                            .addLore(Component.text("새로운 우편이 오면 알림을 받을 수 있습니다", ColorUtil.GRAY))
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
                            .displayName(Component.text(mail.subject(), ColorUtil.PRIMARY)
                                    .decoration(TextDecoration.BOLD, mail.isUnread()))
                            .addLore(Component.empty())
                            .addLore(Component.text("보낸 사람: " + mail.senderName(), ColorUtil.WHITE))
                            .addLore(Component.text("상태: " + status, ColorUtil.GRAY))
                            .addLore(Component.text("시간: " + java.time.Instant.ofEpochMilli(mail.sentAt()).atZone(java.time.ZoneId.systemDefault()).format(
                                    DateTimeFormatter.ofPattern("MM-dd HH:mm")), ColorUtil.GRAY))
                            .addLore(Component.empty())
                            .addLore(Component.text("클릭하여 우편 확인", ColorUtil.YELLOW))
                            .build(),
                    p -> {
                        MailDetailGui detailGui = new MailDetailGui(guiManager, langManager, p, mail);
                        guiManager.openGui(p, detailGui);
                        playClickSound(p);
                    }
            );

            setItem(slot, mailItem);
            slot++;
        }
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
}