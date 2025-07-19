package com.febrie.rpg.gui.impl.social;

import com.febrie.rpg.dto.social.FriendRequestDTO;
import com.febrie.rpg.dto.social.FriendshipDTO;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.system.MainMenuGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.social.FriendManager;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 친구 목록 GUI
 * 친구 목록, 친구 요청, 친구 추가 기능 제공
 *
 * @author Febrie
 */
public class FriendListGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows

    // 메뉴 버튼 슬롯
    private static final int FRIEND_REQUESTS_SLOT = 10;
    private static final int ADD_FRIEND_SLOT = 13;
    private static final int REFRESH_SLOT = 16;

    // 친구 목록 시작 슬롯
    private static final int FRIENDS_START_SLOT = 19;
    private static final int FRIENDS_END_SLOT = 43;

    // 타이틀 슬롯
    private static final int TITLE_SLOT = 4;

    private final FriendManager friendManager;

    private FriendListGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                        @NotNull Player player) {
        super(player, guiManager, langManager, GUI_SIZE, "gui.friends.title");
        this.friendManager = FriendManager.getInstance();
    }

    /**
     * FriendListGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매닀저
     * @param langManager 언어 매니저
     * @param player 플레이어
     * @return 초기화된 FriendListGui 인스턴스
     */
    public static FriendListGui create(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                                      @NotNull Player player) {
        FriendListGui gui = new FriendListGui(guiManager, langManager, player);
        gui.setupLayout();
        gui.loadFriends();
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.text("친구 목록", ColorUtil.PRIMARY);
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
                new ItemBuilder(Material.PLAYER_HEAD)
                        .displayName(Component.text("👥 친구 목록", ColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("친구들과 소통하세요!", ColorUtil.GRAY))
                        .build()
        );
        setItem(TITLE_SLOT, titleItem);
    }

    /**
     * 메뉴 버튼들 설정
     */
    private void setupMenuButtons() {
        // 친구 요청 버튼
        GuiItem friendRequestsButton = GuiItem.clickable(
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .displayName(Component.text("📨 친구 요청", ColorUtil.UNCOMMON)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("받은 친구 요청을 확인합니다", ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 열기", ColorUtil.YELLOW))
                        .build(),
                p -> {
                    FriendRequestGui requestGui = FriendRequestGui.create(guiManager, langManager, p);
                    guiManager.openGui(p, requestGui);
                    playClickSound(p);
                }
        );
        setItem(FRIEND_REQUESTS_SLOT, friendRequestsButton);

        // 친구 추가 버튼
        GuiItem addFriendButton = GuiItem.clickable(
                new ItemBuilder(Material.EMERALD)
                        .displayName(Component.text("➕ 친구 추가", ColorUtil.SUCCESS)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("새로운 친구를 추가합니다", ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 추가", ColorUtil.YELLOW))
                        .build(),
                p -> {
                    p.closeInventory();
                    p.sendMessage("§e채팅에 '/친구추가 <플레이어명> [메시지]'를 입력하세요.");
                    p.sendMessage("§7예시: /친구추가 Steve 안녕하세요!");
                    playClickSound(p);
                }
        );
        setItem(ADD_FRIEND_SLOT, addFriendButton);

        // 새로고침 버튼
        GuiItem refreshButton = GuiItem.clickable(
                new ItemBuilder(Material.CLOCK)
                        .displayName(Component.text("🔄 새로고침", ColorUtil.INFO)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("친구 목록을 새로고침합니다", ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 새로고침", ColorUtil.YELLOW))
                        .build(),
                p -> {
                    friendManager.clearCache(p.getUniqueId());
                    loadFriends();
                    p.sendMessage("§a친구 목록을 새로고침했습니다.");
                    playClickSound(p);
                }
        );
        setItem(REFRESH_SLOT, refreshButton);

        // 빈 슬롯들을 투명한 유리판으로 채우기
        for (int i = 0; i < FRIENDS_START_SLOT; i++) {
            if (getItem(i) == null) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
        for (int i = FRIENDS_END_SLOT + 1; i < GUI_SIZE; i++) {
            if (getItem(i) == null) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 친구 목록 로드
     */
    private void loadFriends() {
        // 친구 목록 영역 초기화
        for (int i = FRIENDS_START_SLOT; i <= FRIENDS_END_SLOT; i++) {
            setItem(i, GuiFactory.createDecoration());
        }

        // 로딩 표시
        setItem(FRIENDS_START_SLOT + 12, GuiItem.display(
                new ItemBuilder(Material.HOPPER)
                        .displayName(Component.text("로딩 중...", ColorUtil.GRAY))
                        .build()
        ));

        // 비동기로 친구 목록 로드
        friendManager.getFriends(viewer.getUniqueId()).thenAccept(friends -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                displayFriends(friends);
            });
        });
    }

    /**
     * 친구 목록 표시
     */
    private void displayFriends(@NotNull Set<FriendshipDTO> friends) {
        // 친구 목록 영역 초기화
        for (int i = FRIENDS_START_SLOT; i <= FRIENDS_END_SLOT; i++) {
            setItem(i, GuiFactory.createDecoration());
        }

        if (friends.isEmpty()) {
            // 친구가 없을 때
            setItem(FRIENDS_START_SLOT + 12, GuiItem.display(
                    new ItemBuilder(Material.BARRIER)
                            .displayName(Component.text("친구가 없습니다", ColorUtil.ERROR))
                            .addLore(Component.text("새로운 친구를 추가해보세요!", ColorUtil.GRAY))
                            .build()
            ));
            return;
        }

        // 친구 목록을 이름순으로 정렬
        List<FriendshipDTO> sortedFriends = friends.stream()
                .sorted((f1, f2) -> {
                    String name1 = f1.getFriendName(viewer.getUniqueId());
                    String name2 = f2.getFriendName(viewer.getUniqueId());
                    return name1.compareToIgnoreCase(name2);
                })
                .collect(Collectors.toList());

        // 친구 아이템 생성
        int slot = FRIENDS_START_SLOT;
        for (FriendshipDTO friendship : sortedFriends) {
            if (slot > FRIENDS_END_SLOT) break;

            UUID friendUuid = friendship.getFriendUuid(viewer.getUniqueId());
            String friendName = friendship.getFriendName(viewer.getUniqueId());
            boolean isOnline = friendManager.isPlayerOnline(friendUuid);

            Material material = isOnline ? Material.LIME_DYE : Material.GRAY_DYE;
            String status = isOnline ? "§a온라인" : "§7오프라인";

            GuiItem friendItem = GuiItem.clickable(
                    new ItemBuilder(material)
                            .displayName(Component.text(friendName, 
                                    isOnline ? ColorUtil.SUCCESS : ColorUtil.GRAY)
                                    .decoration(TextDecoration.BOLD, true))
                            .addLore(Component.empty())
                            .addLore(Component.text("상태: " + status, ColorUtil.WHITE))
                            .addLore(Component.text("친구가 된 날: " + 
                                    new java.util.Date(friendship.createdAt()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString(), ColorUtil.GRAY))
                            .addLore(Component.empty())
                            .addLore(Component.text("좌클릭: 귓말 보내기", ColorUtil.YELLOW))
                            .addLore(Component.text("우클릭: 친구 관리", ColorUtil.YELLOW))
                            .build(),
                    p -> {
                        // 귓말 보내기 (추후 구현)
                        p.closeInventory();
                        p.sendMessage("§e채팅에 '/귓말 " + friendName + " <메시지>'를 입력하세요.");
                        playClickSound(p);
                    }
            );

            setItem(slot, friendItem);
            slot++;
        }
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT, ClickType.RIGHT);
    }
}