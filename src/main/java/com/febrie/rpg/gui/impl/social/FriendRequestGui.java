package com.febrie.rpg.gui.impl.social;
import com.febrie.rpg.util.lang.GeneralLangKey;

import com.febrie.rpg.dto.social.FriendRequestDTO;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.social.FriendManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.lang.GuiLangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * 친구 요청 GUI
 * 받은 친구 요청을 확인하고 수락/거절할 수 있는 GUI
 *
 * @author Febrie
 */
public class FriendRequestGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows

    // 요청 목록 시작 슬롯
    private static final int REQUESTS_START_SLOT = 10;
    private static final int REQUESTS_END_SLOT = 43;

    // 타이틀 슬롯
    private static final int TITLE_SLOT = 4;

    private final FriendManager friendManager;

    private FriendRequestGui(@NotNull GuiManager guiManager, @NotNull Player player) {
        super(player, guiManager, GUI_SIZE, LangManager.text(GuiLangKey.GUI_FRIEND_REQUESTS_TITLE));
        this.friendManager = FriendManager.getInstance();
    }

    /**
     * FriendRequestGui 인스턴스를 생성하고 초기화합니다.
     *
     * @param guiManager  GUI 매니저
     * @param langManager 언어 매니저
     * @param player      플레이어
     * @return 초기화된 FriendRequestGui 인스턴스
     */
    public static FriendRequestGui create(@NotNull GuiManager guiManager, @NotNull Player player) {
        FriendRequestGui gui = new FriendRequestGui(guiManager, player);
        gui.loadRequests();
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(GuiLangKey.GUI_FRIEND_REQUESTS_TITLE);
    }

    @Override
    protected GuiFramework getBackTarget() {
        return FriendListGui.create(guiManager, viewer);
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
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
                ItemBuilder.of(Material.WRITABLE_BOOK)
                        .displayName(LangManager.text(GeneralLangKey.ITEMS_SOCIAL_FRIEND_REQUESTS_TITLE_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(GeneralLangKey.ITEMS_SOCIAL_FRIEND_REQUESTS_TITLE_LORE, viewer))
                        .hideAllFlags()
                        .build()
        );
        setItem(TITLE_SLOT, titleItem);
    }

    /**
     * 친구 요청 목록 로드
     */
    private void loadRequests() {
        // 요청 목록 영역 초기화
        for (int i = REQUESTS_START_SLOT; i <= REQUESTS_END_SLOT; i++) {
            setItem(i, GuiFactory.createDecoration());
        }

        // 로딩 표시
        setItem(22, GuiItem.display(
                ItemBuilder.of(Material.HOPPER)
                        .displayName(LangManager.text(GeneralLangKey.ITEMS_LOADING_NAME, viewer))
                        .hideAllFlags()
                        .build()
        ));

        // 비동기로 친구 요청 목록 로드
        friendManager.getPendingRequests(viewer.getUniqueId()).thenAccept(requests -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                displayRequests(requests);
            });
        });
    }

    /**
     * 친구 요청 목록 표시
     */
    private void displayRequests(@NotNull Set<FriendRequestDTO> requests) {
        // 요청 목록 영역 초기화
        for (int i = REQUESTS_START_SLOT; i <= REQUESTS_END_SLOT; i++) {
            setItem(i, GuiFactory.createDecoration());
        }

        if (requests.isEmpty()) {
            // 요청이 없을 때
            setItem(22, GuiItem.display(
                    ItemBuilder.of(Material.BARRIER)
                            .displayName(LangManager.text(GeneralLangKey.ITEMS_SOCIAL_FRIEND_REQUESTS_NO_REQUESTS_NAME, viewer))
                            .addLore(LangManager.text(GeneralLangKey.ITEMS_SOCIAL_FRIEND_REQUESTS_NO_REQUESTS_LORE, viewer))
                            .hideAllFlags()
                            .build()
            ));
            return;
        }

        // 요청을 시간순으로 정렬 (최신 순)
        List<FriendRequestDTO> sortedRequests = requests.stream().sorted((r1, r2) -> r2.requestTime().compareTo(r1.requestTime())).toList();

        // 요청 아이템 생성
        int slot = REQUESTS_START_SLOT;
        for (FriendRequestDTO request : sortedRequests) {
            if (slot > REQUESTS_END_SLOT) break;

            // 3개씩 배치 (요청자 정보, 수락, 거절)
            if (slot + 2 > REQUESTS_END_SLOT) break;

            // 요청자 정보
            GuiItem requestInfo = GuiItem.display(
                    ItemBuilder.of(Material.PLAYER_HEAD)
                            .displayName(Component.text(request.fromPlayerName(), UnifiedColorUtil.PRIMARY).decoration(TextDecoration.BOLD, true))
                            .addLore(Component.empty())
                            .addLore(LangManager.text(GuiLangKey.GUI_FRIEND_REQUESTS_REQUEST_TIME, viewer, Component.text(request.requestTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))))
                            .addLore(Component.empty())
                            .addLore(request.message() != null ? 
                                    LangManager.text(GuiLangKey.GUI_FRIEND_REQUESTS_MESSAGE, viewer, Component.text(request.message())) : 
                                    LangManager.text(GuiLangKey.GUI_FRIEND_REQUESTS_NO_MESSAGE, viewer))
                            .addLore(Component.empty())
                            .addLore(LangManager.text(GeneralLangKey.ITEMS_SOCIAL_FRIEND_REQUESTS_REQUEST_INFO_HINT, viewer))
                            .hideAllFlags()
                            .build()
            );
            setItem(slot, requestInfo);

            // 수락 버튼
            GuiItem acceptButton = GuiItem.clickable(
                    ItemBuilder.of(Material.LIME_DYE)
                            .displayName(LangManager.text(GeneralLangKey.ITEMS_SOCIAL_FRIEND_REQUESTS_ACCEPT_NAME, viewer))
                            .addLore(Component.empty())
                            .addLore(LangManager.list(GuiLangKey.GUI_FRIEND_REQUESTS_ACCEPT_DESC, viewer, Component.text(request.fromPlayerName())))
                            .addLore(Component.empty())
                            .addLore(LangManager.text(GeneralLangKey.ITEMS_SOCIAL_FRIEND_REQUESTS_ACCEPT_CLICK, viewer))
                            .hideAllFlags()
                            .build(), p -> {
                if (request.id() == null) {
                    p.sendMessage(LangManager.text(GeneralLangKey.ERROR_FRIEND_REQUEST_ID_MISSING, p).color(UnifiedColorUtil.ERROR));
                    return;
                }
                friendManager.acceptFriendRequest(p, request.id()).thenAccept(success -> {
                    if (success) {
                        // 목록 새로고침
                        Bukkit.getScheduler().runTask(plugin, this::loadRequests);
                    }
                });
                playSuccessSound(p);
            });
            setItem(slot + 1, acceptButton);

            // 거절 버튼
            GuiItem rejectButton = GuiItem.clickable(
                    ItemBuilder.of(Material.RED_DYE)
                            .displayName(LangManager.text(GeneralLangKey.ITEMS_SOCIAL_FRIEND_REQUESTS_REJECT_NAME, viewer))
                            .addLore(Component.empty())
                            .addLore(LangManager.list(GuiLangKey.GUI_FRIEND_REQUESTS_REJECT_DESC, viewer, Component.text(request.fromPlayerName())))
                            .addLore(Component.empty())
                            .addLore(LangManager.text(GeneralLangKey.ITEMS_SOCIAL_FRIEND_REQUESTS_REJECT_CLICK, viewer))
                            .hideAllFlags()
                            .build(), p -> {
                if (request.id() == null) {
                    p.sendMessage(LangManager.text(GeneralLangKey.ERROR_FRIEND_REQUEST_ID_MISSING, p).color(UnifiedColorUtil.ERROR));
                    return;
                }
                friendManager.rejectFriendRequest(p, request.id()).thenAccept(success -> {
                    if (success) {
                        // 목록 새로고침
                        Bukkit.getScheduler().runTask(plugin, this::loadRequests);
                    }
                });
                playClickSound(p);
            });
            setItem(slot + 2, rejectButton);

            slot += 9; // 다음 줄로 이동
        }
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return super.getAllowedClickTypes();
    }
    
}