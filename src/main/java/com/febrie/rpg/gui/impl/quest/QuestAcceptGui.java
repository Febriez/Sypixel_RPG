package com.febrie.rpg.gui.impl.quest;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.system.MainMenuGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 퀘스트 수락 GUI
 * 퀘스트 정보를 보여주고 수락/거절할 수 있는 인터페이스
 *
 * @author Febrie
 */
public class QuestAcceptGui extends BaseGui {

    private static final int GUI_SIZE = 45; // 5 rows

    // 레이아웃 상수
    private static final int QUEST_INFO_SLOT = 13;
    private static final int ACCEPT_BUTTON_SLOT = 29;
    private static final int DECLINE_BUTTON_SLOT = 33;

    private final Quest quest;
    private final QuestManager questManager;

    public QuestAcceptGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                          @NotNull Player viewer, @NotNull Quest quest) {
        super(viewer, guiManager, langManager, GUI_SIZE, "gui.quest-accept.title");
        this.quest = quest;
        this.questManager = QuestManager.getInstance();
        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("gui.quest-accept.title");
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupQuestInfo();
        setupActionButtons();
        setupBackButton();
    }

    /**
     * 장식 요소 설정
     */
    private void setupDecorations() {
        // 전체 경계선
        createBorder();

        // 중앙 장식
        for (int i = 10; i < 17; i++) {
            if (i != QUEST_INFO_SLOT) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 퀘스트 정보 표시
     */
    private void setupQuestInfo() {
        ItemBuilder builder = new ItemBuilder(Material.BOOK)
                .displayName(Component.text(quest.getDisplayName(viewer.locale().toString().startsWith("ko")))
                        .color(ColorUtil.LEGENDARY)
                        .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();

        // 퀘스트 설명
        lore.add(Component.empty());
        List<String> descriptions = quest.getDisplayInfo(viewer.locale().toString().startsWith("ko"));
        for (String desc : descriptions) {
            lore.add(Component.text(desc, ColorUtil.GRAY));
        }
        lore.add(Component.empty());

        // 카테고리
        boolean isKorean = viewer.locale().toString().startsWith("ko");
        String categoryName = switch (quest.getCategory()) {
            case MAIN -> isKorean ? "메인 퀘스트" : "Main Quest";
            case SIDE -> isKorean ? "사이드 퀘스트" : "Side Quest";
            case DAILY -> isKorean ? "일일 퀘스트" : "Daily Quest";
            case WEEKLY -> isKorean ? "주간 퀘스트" : "Weekly Quest";
            case EVENT -> isKorean ? "이벤트 퀘스트" : "Event Quest";
            case TUTORIAL -> isKorean ? "튜토리얼" : "Tutorial";
            case NORMAL -> isKorean ? "일반 퀘스트" : "Normal Quest";
        };
        lore.add(trans("gui.quest-accept.category")
                .append(Component.text(": ", ColorUtil.GRAY))
                .append(Component.text(categoryName, ColorUtil.YELLOW)));

        // 레벨 요구사항
        lore.add(trans("gui.quest-accept.level-requirement")
                .append(Component.text(": ", ColorUtil.GRAY))
                .append(Component.text(quest.getMinLevel() + (quest.getMaxLevel() > 0 ? "-" + quest.getMaxLevel() : "+"), ColorUtil.WHITE)));

        lore.add(Component.empty());

        // 목표 목록
        lore.add(trans("gui.quest-accept.objectives").color(ColorUtil.YELLOW));
        int objIndex = 1;
        for (QuestObjective objective : quest.getObjectives()) {
            String objDesc = quest.getObjectiveDescription(objective, viewer.locale().toString().startsWith("ko"));
            lore.add(Component.text("  " + objIndex + ". ", ColorUtil.GRAY)
                    .append(Component.text(objDesc, ColorUtil.WHITE)));
            objIndex++;
        }

        lore.add(Component.empty());

        // 보상 정보
        lore.add(trans("gui.quest-accept.rewards").color(ColorUtil.EMERALD));
        lore.addAll(quest.getReward().getLoreComponents(viewer));

        // 선행 퀘스트 확인
        List<QuestID> completedQuests = questManager.getCompletedQuests(viewer.getUniqueId());
        if (quest.hasPrerequisiteQuests()) {
            lore.add(Component.empty());
            lore.add(Component.text("선행 퀘스트:", ColorUtil.GOLD));
            
            for (QuestID prereqId : quest.getPrerequisiteQuests()) {
                boolean completed = completedQuests.contains(prereqId);
                Component status = completed
                        ? Component.text(" ✓", ColorUtil.SUCCESS)
                        : Component.text(" ✗", ColorUtil.ERROR);
                
                lore.add(Component.text("  - " + prereqId.getDisplayName(), ColorUtil.GRAY)
                        .append(status));
            }
        }

        // 양자택일 퀘스트 경고
        if (quest.hasExclusiveQuests()) {
            lore.add(Component.empty());
            lore.add(Component.text("양자택일 퀘스트:", ColorUtil.RED));
            
            for (QuestID exclusiveId : quest.getExclusiveQuests()) {
                lore.add(Component.text("  - " + exclusiveId.getDisplayName(), ColorUtil.GRAY));
            }
        }

        builder.lore(lore);
        builder.addItemFlags(ItemFlag.values());

        GuiItem questItem = GuiItem.display(builder.build());
        setItem(QUEST_INFO_SLOT, questItem);
    }

    /**
     * 수락/거절 버튼 설정
     */
    private void setupActionButtons() {
        // 수락 버튼
        boolean canStart = checkQuestRequirements();

        ItemBuilder acceptBuilder = new ItemBuilder(Material.LIME_WOOL)
                .displayName(trans("gui.quest-accept.accept")
                        .color(canStart ? ColorUtil.SUCCESS : ColorUtil.GRAY)
                        .decoration(TextDecoration.BOLD, true));

        if (canStart) {
            acceptBuilder.addLore(trans("gui.quest-accept.accept-desc"));
        } else {
            acceptBuilder.addLore(trans("gui.quest-accept.cannot-accept")
                    .color(ColorUtil.ERROR));

            // 수락 불가 이유 표시
            if (!quest.canStart(viewer.getUniqueId())) {
                acceptBuilder.addLore(trans("gui.quest-accept.requirements-not-met")
                        .color(ColorUtil.GRAY));
            }

            List<QuestID> completedQuests = questManager.getCompletedQuests(viewer.getUniqueId());
            if (!quest.arePrerequisitesComplete(completedQuests)) {
                acceptBuilder.addLore(trans("gui.quest-accept.prerequisites-not-complete")
                        .color(ColorUtil.GRAY));
            }

            if (quest.hasCompletedExclusiveQuests(completedQuests)) {
                acceptBuilder.addLore(trans("gui.quest-accept.exclusive-quest-completed")
                        .color(ColorUtil.GRAY));
            }

            if (questManager.getActiveQuests(viewer.getUniqueId()).stream()
                    .anyMatch(p -> p.getQuestId().equals(quest.getId()))) {
                acceptBuilder.addLore(trans("gui.quest-accept.already-active")
                        .color(ColorUtil.GRAY));
            }

            if (completedQuests.contains(quest.getId()) && !quest.isRepeatable()) {
                acceptBuilder.addLore(trans("gui.quest-accept.already-completed")
                        .color(ColorUtil.GRAY));
            }
        }

        GuiItem acceptButton = GuiItem.clickable(acceptBuilder.build(), p -> {
            if (canStart) {
                if (questManager.startQuest(p, quest.getId())) {
                    langManager.sendMessage(p, "quest.started",
                            "quest", quest.getDisplayName(viewer.locale().toString().startsWith("ko")));
                    p.closeInventory();
                    playSuccessSound(p);
                } else {
                    langManager.sendMessage(p, "quest.start-failed");
                    playErrorSound(p);
                }
            } else {
                playErrorSound(p);
            }
        });

        setItem(ACCEPT_BUTTON_SLOT, acceptButton);

        // 거절 버튼
        GuiItem declineButton = GuiItem.clickable(
                new ItemBuilder(Material.RED_WOOL)
                        .displayName(trans("gui.quest-accept.decline")
                                .color(ColorUtil.ERROR)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(trans("gui.quest-accept.decline-desc"))
                        .build(),
                p -> {
                    p.closeInventory();
                    langManager.sendMessage(p, "quest.declined");
                    playClickSound(p);
                }
        );

        setItem(DECLINE_BUTTON_SLOT, declineButton);
    }

    /**
     * 퀘스트 시작 가능 여부 확인
     */
    private boolean checkQuestRequirements() {
        List<QuestID> completedQuests = questManager.getCompletedQuests(viewer.getUniqueId());

        // 이미 진행 중인지 확인
        if (questManager.getActiveQuests(viewer.getUniqueId()).stream()
                .anyMatch(p -> p.getQuestId().equals(quest.getId()))) {
            return false;
        }

        // 이미 완료했고 반복 불가능한지 확인
        if (completedQuests.contains(quest.getId()) && !quest.isRepeatable()) {
            return false;
        }

        // 시작 조건 확인
        if (!quest.canStart(viewer.getUniqueId())) {
            return false;
        }

        // 선행 퀘스트 확인
        if (!quest.arePrerequisitesComplete(completedQuests)) {
            return false;
        }

        // 양자택일 퀘스트 확인
        if (quest.hasCompletedExclusiveQuests(completedQuests)) {
            return false;
        }

        return true;
    }

    /**
     * 뒤로가기 버튼 설정
     */
    private void setupBackButton() {
        // 뒤로가기 버튼은 하단 중앙에 배치
        int backSlot = 31; // 하단 중앙 슬롯
        
        GuiItem backButton = GuiItem.clickable(
                new ItemBuilder(Material.BARRIER)
                        .displayName(trans("gui.buttons.back.name"))
                        .addLore(trans("gui.buttons.back.lore"))
                        .addItemFlags(ItemFlag.values())
                        .build(),
                p -> {
                    // 이전 화면으로 돌아가기
                    GuiFramework backTarget = getBackTarget();
                    if (backTarget != null) {
                        guiManager.openGui(p, backTarget);
                    } else {
                        // 백타겟이 없으면 GUI 닫기
                        p.closeInventory();
                    }
                    playClickSound(p);
                }
        );
        
        setItem(backSlot, backButton);
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }

    @Override
    public GuiFramework getBackTarget() {
        // QuestAcceptGui는 MainMenuGui로 돌아갑니다
        return new MainMenuGui(guiManager, langManager, viewer);
    }
}