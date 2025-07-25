package com.febrie.rpg.gui.impl.quest;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.reward.MixedReward;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.SoundUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 퀘스트 보상 GUI
 * 플레이어가 퀘스트 보상을 수령하는 GUI
 *
 * @author Febrie
 */
public class QuestRewardGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows
    private static final int DESTROY_REWARD_SLOT = 48; // 마지막 줄 가운데 왼쪽
    private static final int CLAIM_ALL_SLOT = 50; // 마지막 줄 가운데 오른쪽

    private final Quest quest;
    private final QuestManager questManager;
    private final List<ItemStack> rewardItems;
    private final List<ItemStack> claimedItems;
    private boolean hasClaimed = false;
    private long rewardStartTime;

    private QuestRewardGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                          @NotNull Player viewer, @NotNull Quest quest) {
        super(viewer, guiManager, langManager, GUI_SIZE, "gui.quest-reward.title");
        this.quest = quest;
        this.questManager = QuestManager.getInstance();
        this.rewardItems = new ArrayList<>();
        this.claimedItems = new ArrayList<>();
        this.rewardStartTime = System.currentTimeMillis();
    }

    /**
     * QuestRewardGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param viewer 보는 플레이어
     * @param quest 퀘스트
     * @return 초기화된 QuestRewardGui 인스턴스
     */
    public static QuestRewardGui create(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                                       @NotNull Player viewer, @NotNull Quest quest) {
        QuestRewardGui gui = new QuestRewardGui(guiManager, langManager, viewer, quest);
        gui.initialize("gui.quest-reward.title");
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        boolean isKorean = viewer.locale().getLanguage().equals("ko");
        return trans("gui.quest-reward.title")
                .append(Component.text(" - ", ColorUtil.GRAY))
                .append(Component.text(quest.getDisplayName(isKorean), ColorUtil.LEGENDARY));
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupRewardItems();
        setupButtons();
        giveInstantRewards();
    }

    /**
     * 장식 요소 설정
     */
    private void setupDecorations() {
        // 마지막 줄을 유리판으로 채우기
        ItemStack glassPaneItem = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName(Component.empty())
                .asGuiItem(false)
                .build();
        
        GuiItem glassPane = GuiItem.display(glassPaneItem);
        
        for (int i = 45; i < 54; i++) {
            if (i != DESTROY_REWARD_SLOT && i != CLAIM_ALL_SLOT) {
                setItem(i, glassPane);
            }
        }
    }

    /**
     * 보상 아이템 설정
     */
    private void setupRewardItems() {
        // Quest의 reward에서 아이템 가져오기
        List<ItemStack> rewards = new ArrayList<>();
        
        // 미수령 상태 확인
        List<ItemStack> unclaimedItems = questManager.getUnclaimedItems(viewer.getUniqueId(), quest.getId());
        if (!unclaimedItems.isEmpty()) {
            rewards = unclaimedItems;
            // 남은 시간 표시
            long remainingTime = questManager.getUnclaimedReward(viewer.getUniqueId(), quest.getId()).getRemainingTime();
            long minutes = remainingTime / 1000 / 60;
            viewer.sendMessage(trans("gui.quest-reward.unclaimed-timer", "time", String.valueOf(minutes))
                    .color(ColorUtil.WARNING));
        } else if (quest.getReward() instanceof MixedReward mixedReward) {
            rewards.addAll(mixedReward.getItems());
        }
        
        int slot = 0;
        for (ItemStack reward : rewards) {
            if (slot >= 45) break; // 마지막 줄 전까지만
            
            ItemStack displayItem = reward.clone();
            rewardItems.add(displayItem);
            
            // 아이템에 설명 추가
            ItemBuilder itemBuilder = new ItemBuilder(displayItem);
            itemBuilder.addLore(Component.empty());
            itemBuilder.addLore(trans("gui.quest-reward.click-to-claim").color(ColorUtil.GRAY));
            ItemStack finalItem = itemBuilder.build();
            
            final int currentSlot = slot;
            GuiItem rewardItem = GuiItem.clickable(finalItem, p -> {
                if (hasClaimed) {
                    p.sendMessage(trans("gui.quest-reward.already-claimed").color(ColorUtil.ERROR));
                    SoundUtil.playErrorSound(p);
                    return;
                }
                
                // 개별 아이템 수령
                if (p.getInventory().firstEmpty() != -1) {
                    ItemStack originalItem = reward.clone();
                    p.getInventory().addItem(originalItem);
                    claimedItems.add(originalItem);
                    rewardItems.remove(displayItem);
                    
                    // 빈 슬롯으로 변경
                    ItemStack claimedDisplay = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .displayName(trans("gui.quest-reward.claimed").color(ColorUtil.GRAY))
                            .build();
                    setItem(currentSlot, GuiItem.display(claimedDisplay));
                    
                    SoundUtil.playItemPickupSound(p);
                    
                    // 모든 아이템을 수령했는지 확인
                    if (rewardItems.isEmpty()) {
                        completeRewardClaim();
                        p.closeInventory();
                        p.sendMessage(trans("gui.quest-reward.all-claimed").color(ColorUtil.SUCCESS));
                    }
                } else {
                    p.sendMessage(trans("gui.quest-reward.inventory-full").color(ColorUtil.ERROR));
                    SoundUtil.playErrorSound(p);
                }
            });
            
            setItem(slot, rewardItem);
            slot++;
        }
        
        // 보상 아이템이 없는 경우 안내 메시지
        if (rewards.isEmpty()) {
            ItemStack noItemsDisplay = new ItemBuilder(Material.BARRIER)
                    .displayName(trans("gui.quest-reward.no-items").color(ColorUtil.WARNING))
                    .addLore(trans("gui.quest-reward.no-items-desc").color(ColorUtil.GRAY))
                    .build();
            setItem(22, GuiItem.display(noItemsDisplay));
        }
    }

    /**
     * 버튼 설정
     */
    private void setupButtons() {
        // 보상 파괴 버튼
        ItemBuilder destroyBuilder = new ItemBuilder(Material.BARRIER)
                .displayName(trans("gui.quest-reward.destroy-rewards").color(ColorUtil.ERROR))
                .addLore(trans("gui.quest-reward.destroy-desc").color(ColorUtil.GRAY))
                .addLore(Component.empty())
                .addLore(trans("gui.quest-reward.warning-destroy").color(ColorUtil.ERROR));
        
        GuiItem destroyButton = GuiItem.clickable(destroyBuilder.build(), p -> {
            if (hasClaimed) {
                p.sendMessage(trans("gui.quest-reward.already-claimed").color(ColorUtil.ERROR));
                SoundUtil.playErrorSound(p);
                return;
            }
            
            // 확인 다이얼로그 열기
            QuestRewardConfirmGui confirmGui = QuestRewardConfirmGui.create(guiManager, langManager, p, quest, this);
            guiManager.openGui(p, confirmGui);
            SoundUtil.playClickSound(p);
        });
        
        setItem(DESTROY_REWARD_SLOT, destroyButton);
        
        // 모두 받기 버튼
        ItemBuilder claimAllBuilder = new ItemBuilder(Material.EMERALD_BLOCK)
                .displayName(trans("gui.quest-reward.claim-all").color(ColorUtil.SUCCESS))
                .addLore(trans("gui.quest-reward.claim-all-desc").color(ColorUtil.GRAY));
        
        GuiItem claimAllButton = GuiItem.clickable(claimAllBuilder.build(), p -> {
            if (hasClaimed) {
                p.sendMessage(trans("gui.quest-reward.already-claimed").color(ColorUtil.ERROR));
                SoundUtil.playErrorSound(p);
                return;
            }
            
            // 인벤토리 공간 확인
            int emptySlots = 0;
            for (ItemStack item : p.getInventory().getStorageContents()) {
                if (item == null || item.getType() == Material.AIR) {
                    emptySlots++;
                }
            }
            
            if (emptySlots < rewardItems.size()) {
                p.sendMessage(trans("gui.quest-reward.not-enough-space").color(ColorUtil.ERROR));
                SoundUtil.playErrorSound(p);
                return;
            }
            
            // 모든 아이템 지급
            for (ItemStack reward : new ArrayList<>(rewardItems)) {
                ItemStack originalItem = reward.clone();
                // 아이템 설명 제거
                originalItem.lore(null);
                p.getInventory().addItem(originalItem);
                claimedItems.add(originalItem);
            }
            rewardItems.clear();
            
            p.sendMessage(trans("gui.quest-reward.all-claimed").color(ColorUtil.SUCCESS));
            SoundUtil.playRewardSound(p);
            completeRewardClaim();
            p.closeInventory();
        });
        
        setItem(CLAIM_ALL_SLOT, claimAllButton);
    }

    /**
     * 즉시 지급되는 보상 처리 (돈, 경험치)
     */
    private void giveInstantRewards() {
        // 경험치 지급
        if (quest.getExpReward() > 0) {
            viewer.giveExp((int) quest.getExpReward());
            viewer.sendMessage(trans("gui.quest-reward.exp-received", 
                "amount", String.valueOf(quest.getExpReward())).color(ColorUtil.SUCCESS));
        }
        
        // 돈 지급
        if (quest.getMoneyReward() > 0) {
            RPGPlayerManager playerManager = RPGMain.getInstance().getRPGPlayerManager();
            RPGPlayer rpgPlayer = playerManager.getPlayer(viewer);
            if (rpgPlayer != null) {
                rpgPlayer.getWallet().add(CurrencyType.GOLD, quest.getMoneyReward());
                viewer.sendMessage(trans("gui.quest-reward.money-received", 
                    "amount", String.valueOf(quest.getMoneyReward())).color(ColorUtil.SUCCESS));
            }
        }
    }

    /**
     * 보상 수령 완료 처리
     */
    private void completeRewardClaim() {
        hasClaimed = true;
        // 퀘스트를 실제로 완료 처리
        questManager.markQuestAsRewarded(viewer.getUniqueId(), quest.getId());
        // 미수령 보상 제거
        questManager.removeUnclaimedReward(viewer.getUniqueId(), quest.getId());
    }

    /**
     * 아이템의 슬롯 찾기
     */
    private int getSlotForItem(ItemStack item) {
        for (int i = 0; i < 45; i++) {
            GuiItem guiItem = getItem(i);
            if (guiItem != null && guiItem.getItem().isSimilar(item)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }

    @Override
    public GuiFramework getBackTarget() {
        return null; // 보상 GUI는 뒤로가기 불가
    }

    /**
     * GUI 닫힐 때 처리
     */
    public void handleClose() {
        if (!hasClaimed && !rewardItems.isEmpty()) {
            // 부분 수령한 경우 처리
            if (!claimedItems.isEmpty() || questManager.getUnclaimedReward(viewer.getUniqueId(), quest.getId()) != null) {
                // 미수령 아이템 저장
                questManager.saveUnclaimedReward(viewer.getUniqueId(), quest.getId(), rewardItems);
            }
            
            // 경고 메시지
            viewer.sendMessage(Component.empty());
            viewer.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", ColorUtil.ERROR));
            viewer.sendMessage(trans("gui.quest-reward.close-warning").color(ColorUtil.ERROR)
                    .decoration(TextDecoration.BOLD, true));
            viewer.sendMessage(trans("gui.quest-reward.timer-warning").color(ColorUtil.WARNING));
            viewer.sendMessage(trans("gui.quest-reward.timer-info").color(ColorUtil.YELLOW));
            viewer.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", ColorUtil.ERROR));
        }
    }
}