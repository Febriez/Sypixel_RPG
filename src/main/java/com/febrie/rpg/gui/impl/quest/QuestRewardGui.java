package com.febrie.rpg.gui.impl.quest;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.economy.CurrencyType;
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
import com.febrie.rpg.quest.reward.QuestReward;
import com.febrie.rpg.dto.quest.CompletedQuestDTO;
import com.febrie.rpg.quest.reward.impl.BasicReward;
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
    private final String instanceId;
    private final QuestManager questManager;
    private final List<ItemStack> rewardItems;
    private final List<ItemStack> claimedItems;
    private boolean hasClaimed = false;
    private long rewardStartTime;
    private CompletedQuestDTO completedData;

    private QuestRewardGui(@NotNull GuiManager guiManager,
                          @NotNull Player viewer, @NotNull Quest quest, @NotNull String instanceId) {
        super(viewer, guiManager, GUI_SIZE, "gui.quest-reward.title");
        this.quest = quest;
        this.instanceId = instanceId;
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
    public static QuestRewardGui create(@NotNull GuiManager guiManager,
                                       @NotNull Player viewer, @NotNull Quest quest, @NotNull String instanceId) {
        QuestRewardGui gui = new QuestRewardGui(guiManager, viewer, quest, instanceId);
        gui.initialize("gui.quest-reward.title");
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        // 일반 타이틀
        return trans("gui.quest-reward.title")
                .append(Component.text(" - ", ColorUtil.GRAY))
                .append(quest.getDisplayName(viewer).color(ColorUtil.LEGENDARY));
    }
    
    /**
     * 시간을 형식화하여 반환
     */
    private String formatTime(long milliseconds, @NotNull Player player) {
        long totalMinutes = milliseconds / 1000 / 60;
        
        if (totalMinutes >= 1440) { // 1일 이상
            long days = totalMinutes / 1440;
            return LangManager.getMessage(player, "time.days", "days", String.valueOf(days))
                    .toString().replaceAll("§.", ""); // 색상 코드 제거
        } else if (totalMinutes >= 60) { // 1시간 이상
            long hours = totalMinutes / 60;
            return LangManager.getMessage(player, "time.hours", "hours", String.valueOf(hours))
                    .toString().replaceAll("§.", ""); // 색상 코드 제거
        } else {
            return LangManager.getMessage(player, "time.minutes", "minutes", String.valueOf(totalMinutes))
                    .toString().replaceAll("§.", ""); // 색상 코드 제거
        }
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
        
        // 완료된 퀘스트 데이터 가져오기
        completedData = questManager.getCompletedQuestData(viewer.getUniqueId(), instanceId);
        
        if (completedData != null) {
            // 미수령 아이템 가져오기
            List<ItemStack> unclaimedItems = questManager.getUnclaimedItems(viewer.getUniqueId(), instanceId);
            if (!unclaimedItems.isEmpty()) {
                rewards = unclaimedItems;
            }
        } else {
            // 이미 보상을 완전히 받았는지 확인
            if (questManager.hasReceivedAllRewards(viewer.getUniqueId(), instanceId)) {
                hasClaimed = true;
                // 보상을 이미 받은 경우 안내 메시지
                ItemStack alreadyClaimedDisplay = new ItemBuilder(Material.BARRIER)
                        .displayName(trans("gui.quest-reward.already-claimed").color(ColorUtil.ERROR))
                        .addLore(trans("gui.quest-reward.no-items-desc").color(ColorUtil.GRAY))
                        .build();
                setItem(22, GuiItem.display(alreadyClaimedDisplay));
                return;
            }
            
            // 보상을 아직 받지 않은 경우 quest에서 직접 가져오기
            if (quest.getReward() instanceof MixedReward mixedReward) {
                rewards.addAll(mixedReward.getItems());
            } else if (quest.getReward() != null) {
                // BasicReward 등 다른 타입의 보상 처리
                QuestReward questReward = quest.getReward();
                if (questReward instanceof BasicReward basicReward) {
                    // BasicReward에서 아이템 가져오기
                    rewards.addAll(basicReward.getItems());
                }
            }
        }
        
        // 완료된 퀘스트 데이터가 없다면 새로 생성해야 하는 상황
        // 하지만 이 GUI가 열리는 시점에는 이미 completedData가 있어야 함
        
        int slot = 0;
        int itemIndex = 0;
        for (ItemStack reward : rewards) {
            if (slot >= 45) break; // 마지막 줄 전까지만
            
            final int currentItemIndex = itemIndex;
            
            // 이미 수령한 아이템인지 확인
            if (completedData != null && completedData.isItemClaimed(currentItemIndex)) {
                // 이미 수령한 아이템 표시
                ItemStack claimedDisplay = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                        .displayName(trans("gui.quest-reward.claimed").color(ColorUtil.GRAY))
                        .build();
                setItem(slot, GuiItem.display(claimedDisplay));
            } else {
                // 수령 가능한 아이템
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
                        
                        // 수령 상태 저장
                        questManager.markItemRewardClaimed(viewer.getUniqueId(), instanceId, currentItemIndex);
                        plugin.getLogger().info("Quest " + quest.getId() + " - 아이템 " + currentItemIndex + " 수령됨");
                        
                        // 빈 슬롯으로 변경
                        ItemStack claimedDisplay = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                                .displayName(trans("gui.quest-reward.claimed").color(ColorUtil.GRAY))
                                .build();
                        setItem(currentSlot, GuiItem.display(claimedDisplay));
                        
                        SoundUtil.playItemPickupSound(p);
                        
                        // 모든 아이템을 수령했는지 확인
                        if (rewardItems.isEmpty()) {
                            // 모든 보상(즉시 보상 + 아이템)이 수령되었는지 확인
                            checkAndCompleteAllRewards();
                            p.closeInventory();
                            p.sendMessage(trans("gui.quest-reward.all-claimed").color(ColorUtil.SUCCESS));
                        }
                    } else {
                        p.sendMessage(trans("gui.quest-reward.inventory-full").color(ColorUtil.ERROR));
                        SoundUtil.playErrorSound(p);
                    }
                });
                
                setItem(slot, rewardItem);
            }
            
            slot++;
            itemIndex++;
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
                .addLore(Component.empty())
                .addLore(trans("gui.quest-reward.destroy-desc").color(ColorUtil.GRAY))
                .addLore(Component.empty())
                .addLore(trans("gui.quest-reward.warning-destroy-line").color(ColorUtil.ERROR))
                .addLore(trans("gui.quest-reward.warning-destroy-line2").color(ColorUtil.ERROR));
        
        GuiItem destroyButton = GuiItem.clickable(destroyBuilder.build(), p -> {
            if (hasClaimed) {
                p.sendMessage(trans("gui.quest-reward.already-claimed").color(ColorUtil.ERROR));
                SoundUtil.playErrorSound(p);
                return;
            }
            
            // 확인 다이얼로그 열기
            QuestRewardConfirmGui confirmGui = QuestRewardConfirmGui.create(guiManager, p, quest, instanceId, this);
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
            
            // 수령하지 않은 아이템만 찾기
            List<ItemStack> unclaimedOnlyItems = new ArrayList<>();
            int totalItemCount = 0;
            
            // 전체 보상 아이템 목록 다시 가져오기
            List<ItemStack> allRewards = new ArrayList<>();
            if (quest.getReward() instanceof MixedReward mixedReward) {
                allRewards.addAll(mixedReward.getItems());
            } else if (quest.getReward() instanceof BasicReward basicReward) {
                allRewards.addAll(basicReward.getItems());
            }
            
            // 수령하지 않은 아이템만 필터링
            for (int i = 0; i < allRewards.size(); i++) {
                if (completedData == null || !completedData.isItemClaimed(i)) {
                    unclaimedOnlyItems.add(allRewards.get(i));
                    totalItemCount++;
                }
            }
            
            // 모든 아이템 지급
            for (int i = 0; i < allRewards.size(); i++) {
                if (completedData == null || !completedData.isItemClaimed(i)) {
                    ItemStack originalItem = allRewards.get(i).clone();
                    p.getInventory().addItem(originalItem);
                    claimedItems.add(originalItem);
                    questManager.markItemRewardClaimed(viewer.getUniqueId(), instanceId, i);
                }
            }
            rewardItems.clear();
            
            p.sendMessage(trans("gui.quest-reward.all-claimed").color(ColorUtil.SUCCESS));
            SoundUtil.playSuccessSound(p);  // 레벨업 소리
            // 모든 보상(즉시 보상 + 아이템)이 수령되었는지 확인
            checkAndCompleteAllRewards();
            p.closeInventory();
        });
        
        setItem(CLAIM_ALL_SLOT, claimAllButton);
    }

    /**
     * 즉시 지급되는 보상 처리 (돈, 경험치)
     */
    private void giveInstantRewards() {
        // 이미 즉시 보상을 받았는지 확인
        if (completedData != null && completedData.instantRewardsClaimed()) {
            return; // 이미 받은 경우 중복 지급 방지
        }
        
        // RPGPlayer 가져오기
        RPGPlayerManager playerManager = RPGMain.getInstance().getRPGPlayerManager();
        RPGPlayer rpgPlayer = playerManager.getPlayer(viewer);
        if (rpgPlayer == null) return;
        
        // 보상에서 경험치와 돈 정보 가져오기
        int expReward = 0;
        long moneyReward = 0;
        
        if (quest.getReward() instanceof com.febrie.rpg.quest.reward.impl.BasicReward basicReward) {
            expReward = basicReward.getExperience();
            moneyReward = basicReward.getCurrencies().getOrDefault(CurrencyType.GOLD, 0L);
        }
        
        // 경험치 지급 (직업이 있을 때만)
        if (expReward > 0 && rpgPlayer.getJob() != null) {
            rpgPlayer.addExperience(expReward);
            viewer.sendMessage(trans("gui.quest-reward.exp-received", 
                "amount", String.valueOf(expReward)).color(ColorUtil.SUCCESS));
        }
        
        // 돈 지급
        if (moneyReward > 0) {
            rpgPlayer.getWallet().add(CurrencyType.GOLD, moneyReward);
            viewer.sendMessage(trans("gui.quest-reward.money-received", 
                "amount", String.valueOf(moneyReward)).color(ColorUtil.SUCCESS));
        }
        
        // 즉시 보상 수령 표시
        if (expReward > 0 || moneyReward > 0) {
            questManager.markInstantRewardsClaimed(viewer.getUniqueId(), instanceId);
            
            // 아이템 보상이 없는 경우 퀘스트를 완전히 보상 수령 상태로 변경
            boolean hasItemRewards = false;
            if (quest.getReward() instanceof com.febrie.rpg.quest.reward.impl.BasicReward basicReward) {
                hasItemRewards = !basicReward.getItems().isEmpty();
            } else if (quest.getReward() instanceof MixedReward mixedReward) {
                hasItemRewards = !mixedReward.getItems().isEmpty();
            }
            
            // 아이템이 없고 즉시 보상만 있는 경우, 모든 보상이 수령된 것이므로 완료 처리
            if (!hasItemRewards) {
                completeRewardClaim();
            }
        }
    }

    /**
     * 모든 보상이 수령되었는지 확인하고 완료 처리
     */
    private void checkAndCompleteAllRewards() {
        // 완료된 퀘스트 데이터를 다시 가져와서 최신 상태 확인
        CompletedQuestDTO latestData = questManager.getCompletedQuestData(viewer.getUniqueId(), instanceId);
        if (latestData == null) {
            plugin.getLogger().info("Quest " + quest.getId() + " - 완료된 퀘스트 데이터가 없음");
            return;
        }
        
        // 모든 보상(즉시 보상 + 아이템)이 수령되었는지 확인
        if (latestData.areAllRewardsClaimed()) {
            plugin.getLogger().info("Quest " + quest.getId() + " - 모든 보상이 수령됨, 완료 처리");
            completeRewardClaim();
        } else {
            plugin.getLogger().info("Quest " + quest.getId() + " - 아직 미수령 보상이 있음: 즉시보상=" + latestData.instantRewardsClaimed() + ", 미수령아이템=" + latestData.unclaimedItemIndices());
        }
    }
    
    /**
     * 보상 수령 완료 처리
     */
    private void completeRewardClaim() {
        hasClaimed = true;
        // 퀘스트를 실제로 완료 처리 (CompletedQuestDTO -> ClaimedQuestDTO로 이동)
        questManager.markQuestAsRewarded(viewer.getUniqueId(), instanceId);
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
            // 부분 수령한 경우 - 새 시스템에서는 자동으로 CompletedQuestDTO에 미수령 상태가 저장됨
            // 별도의 저장 로직이 필요하지 않음
            
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