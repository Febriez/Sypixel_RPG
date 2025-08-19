package com.febrie.rpg.gui.impl.social;

import com.febrie.rpg.dto.social.FriendRequestDTO;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.social.FriendManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
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
        super(player, guiManager, GUI_SIZE, "gui.friend-requests.title");
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
        return Component.text("친구 요청", UnifiedColorUtil.UNCOMMON);
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
        GuiItem titleItem = GuiItem.display(new ItemBuilder(Material.WRITABLE_BOOK).displayName(Component.text("📨 친구 요청", UnifiedColorUtil.UNCOMMON).decoration(TextDecoration.BOLD, true)).addLore(Component.empty()).addLore(Component.text("받은 친구 요청을 관리합니다", UnifiedColorUtil.GRAY)).build());
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
        setItem(22, GuiItem.display(new ItemBuilder(Material.HOPPER).displayName(Component.text("로딩 중...", UnifiedColorUtil.GRAY)).build()));

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
            setItem(22, GuiItem.display(new ItemBuilder(Material.BARRIER).displayName(Component.text("받은 친구 요청이 없습니다", UnifiedColorUtil.ERROR)).addLore(Component.text("새로운 요청이 오면 알림을 받을 수 있습니다", UnifiedColorUtil.GRAY)).build()));
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
            GuiItem requestInfo = GuiItem.display(new ItemBuilder(Material.PLAYER_HEAD).displayName(Component.text(request.fromPlayerName(), UnifiedColorUtil.PRIMARY).decoration(TextDecoration.BOLD, true)).addLore(Component.empty()).addLore(Component.text("요청 시간: " + request.requestTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), UnifiedColorUtil.GRAY)).addLore(Component.empty()).addLore(request.message() != null ? Component.text("메시지: " + request.message(), UnifiedColorUtil.WHITE) : Component.text("메시지 없음", UnifiedColorUtil.GRAY)).addLore(Component.empty()).addLore(Component.text("우측 버튼으로 수락/거절", UnifiedColorUtil.YELLOW)).build());
            setItem(slot, requestInfo);

            // 수락 버튼
            GuiItem acceptButton = GuiItem.clickable(new ItemBuilder(Material.LIME_DYE).displayName(Component.text("✓ 수락", UnifiedColorUtil.SUCCESS).decoration(TextDecoration.BOLD, true)).addLore(Component.empty()).addLore(Component.text(request.fromPlayerName() + "님의", UnifiedColorUtil.GRAY)).addLore(Component.text("친구 요청을 수락합니다", UnifiedColorUtil.GRAY)).addLore(Component.empty()).addLore(Component.text("클릭하여 수락", UnifiedColorUtil.YELLOW)).build(), p -> {
                if (request.id() == null) {
                    p.sendMessage(Component.text("친구 요청 ID가 없습니다.", UnifiedColorUtil.ERROR));
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
            GuiItem rejectButton = GuiItem.clickable(new ItemBuilder(Material.RED_DYE).displayName(Component.text("✗ 거절", UnifiedColorUtil.ERROR).decoration(TextDecoration.BOLD, true)).addLore(Component.empty()).addLore(Component.text(request.fromPlayerName() + "님의", UnifiedColorUtil.GRAY)).addLore(Component.text("친구 요청을 거절합니다", UnifiedColorUtil.GRAY)).addLore(Component.empty()).addLore(Component.text("클릭하여 거절", UnifiedColorUtil.YELLOW)).build(), p -> {
                if (request.id() == null) {
                    p.sendMessage(Component.text("친구 요청 ID가 없습니다.", UnifiedColorUtil.ERROR));
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