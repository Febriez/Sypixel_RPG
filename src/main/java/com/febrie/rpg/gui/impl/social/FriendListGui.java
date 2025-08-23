package com.febrie.rpg.gui.impl.social;

import com.febrie.rpg.dto.social.FriendshipDTO;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.system.MainMenuGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.social.FriendManager;
import com.febrie.rpg.util.UnifiedColorUtil;
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

    private FriendListGui(@NotNull GuiManager guiManager,
                        @NotNull Player player) {
        super(player, guiManager, GUI_SIZE, Component.translatable("gui.friends.title"));
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
    public static FriendListGui create(@NotNull GuiManager guiManager,
                                      @NotNull Player player) {
        FriendListGui gui = new FriendListGui(guiManager, player);
        gui.loadFriends();
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("gui.friends.title");
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
                ItemBuilder.of(Material.PLAYER_HEAD, viewer.locale())
                        .displayNameTranslated("items.social.friends.title.name")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.social.friends.title.lore")
                        .hideAllFlags()
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
                ItemBuilder.of(Material.WRITABLE_BOOK, viewer.locale())
                        .displayNameTranslated("items.social.friends.requests.name")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.social.friends.requests.lore")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.social.friends.requests.click")
                        .hideAllFlags()
                        .build(),
                p -> {
                    FriendRequestGui requestGui = FriendRequestGui.create(guiManager, p);
                    guiManager.openGui(p, requestGui);
                    playClickSound(p);
                }
        );
        setItem(FRIEND_REQUESTS_SLOT, friendRequestsButton);

        // 친구 추가 버튼
        GuiItem addFriendButton = GuiItem.clickable(
                ItemBuilder.of(Material.EMERALD, viewer.locale())
                        .displayNameTranslated("items.social.friends.add.name")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.social.friends.add.lore")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.social.friends.add.click")
                        .hideAllFlags()
                        .build(),
                p -> {
                    p.closeInventory();
                    p.sendMessage(LangManager.get("gui.friends.add-command-hint", p));
                    p.sendMessage(LangManager.get("gui.friends.add-command-example", p));
                    playClickSound(p);
                }
        );
        setItem(ADD_FRIEND_SLOT, addFriendButton);

        // 새로고침 버튼
        GuiItem refreshButton = GuiItem.clickable(
                ItemBuilder.of(Material.CLOCK, viewer.locale())
                        .displayNameTranslated("items.social.friends.refresh.name")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.social.friends.refresh.lore")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.social.friends.refresh.click")
                        .hideAllFlags()
                        .build(),
                p -> {
                    friendManager.clearCache(p.getUniqueId());
                    loadFriends();
                    p.sendMessage(LangManager.get("gui.friends.refreshed", p));
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
                ItemBuilder.of(Material.HOPPER, viewer.locale())
                        .displayNameTranslated("items.loading.name")
                        .hideAllFlags()
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
                    ItemBuilder.of(Material.BARRIER, viewer.locale())
                            .displayNameTranslated("items.social.friends.no-friends.name")
                            .addLoreTranslated("items.social.friends.no-friends.lore")
                            .hideAllFlags()
                            .build()
            ));
            return;
        }

        // 친구 목록을 이름순으로 정렬
        List<FriendshipDTO> sortedFriends = friends.stream()
                .sorted((f1, f2) -> {
                    String name1 = f1.getFriendName(viewer.getUniqueId());
                    String name2 = f2.getFriendName(viewer.getUniqueId());
                    if (name1 == null && name2 == null) return 0;
                    if (name1 == null) return 1;
                    if (name2 == null) return -1;
                    return name1.compareToIgnoreCase(name2);
                })
                .collect(Collectors.toList());

        // 친구 아이템 생성
        int slot = FRIENDS_START_SLOT;
        for (FriendshipDTO friendship : sortedFriends) {
            if (slot > FRIENDS_END_SLOT) break;

            UUID friendUuid = friendship.getFriendUuid(viewer.getUniqueId());
            String friendName = friendship.getFriendName(viewer.getUniqueId());
            if (friendUuid == null || friendName == null) {
                continue; // Skip invalid friend entries
            }
            boolean isOnline = friendManager.isPlayerOnline(friendUuid);

            Material material = isOnline ? Material.LIME_DYE : Material.GRAY_DYE;
            Component statusText = Component.translatable(isOnline ? "status.online" : "status.offline");

            GuiItem friendItem = GuiItem.clickable(
                    ItemBuilder.of(material, viewer.locale())
                            .displayName(Component.text(friendName, 
                                    isOnline ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.GRAY)
                                    .decoration(TextDecoration.BOLD, true))
                            .addLore(Component.empty())
                            .addLore(LangManager.get("gui.friends.status", viewer, statusText))
                            .addLore(LangManager.get("gui.friends.since", viewer, 
                                    Component.text(new java.util.Date(friendship.createdAt()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString())))
                            .addLore(Component.empty())
                            .addLoreTranslated("items.social.friends.friend-item.left-click")
                            .addLoreTranslated("items.social.friends.friend-item.right-click")
                            .hideAllFlags()
                            .build(),
                    p -> {
                        // 귓말 보내기 (추후 구현)
                        p.closeInventory();
                        p.sendMessage(LangManager.get("gui.friends.whisper-hint", p, Component.text(friendName)));
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