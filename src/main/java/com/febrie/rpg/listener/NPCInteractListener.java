package com.febrie.rpg.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.util.LangManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Citizens NPC 상호작용 리스너
 * 퀘스트 NPC 클릭 처리
 *
 * @author Febrie
 */
public class NPCInteractListener implements Listener {

    private final RPGMain plugin;
    private final GuiManager guiManager;
    private final LangManager langManager;
    private final QuestManager questManager;

    public NPCInteractListener(@NotNull RPGMain plugin, @NotNull GuiManager guiManager,
                               @NotNull LangManager langManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.questManager = QuestManager.getInstance();
    }

    /**
     * Citizens NPC 우클릭 이벤트 처리
     */
    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();

        // 퀘스트 NPC인지 확인
        if (!npc.data().has("quest_npc") || !npc.data().get("quest_npc", false)) {
            return;
        }

        // 퀘스트 ID 가져오기 (enum name으로 저장)
        String questIdStr = npc.data().get("quest_id", "");
        if (questIdStr.isEmpty()) {
            return;
        }

        // QuestID enum으로 변환
        QuestID questId;
        try {
            questId = QuestID.valueOf(questIdStr);
        } catch (IllegalArgumentException e) {
            // legacy ID로 시도
            try {
                questId = QuestID.fromLegacyId(questIdStr);
            } catch (IllegalArgumentException ex) {
                langManager.sendMessage(player, "quest.npc.invalid-quest");
                return;
            }
        }

        // 퀘스트 가져오기
        Quest quest = questManager.getQuest(questId);
        if (quest == null) {
            langManager.sendMessage(player, "quest.npc.invalid-quest");
            return;
        }

        // 이미 퀘스트를 진행 중인지 확인
        QuestID finalQuestId = questId;
        boolean hasActiveQuest = questManager.getActiveQuests(player.getUniqueId()).stream()
                .anyMatch(p -> p.getQuestId().equals(finalQuestId));

        if (hasActiveQuest) {
            langManager.sendMessage(player, "quest.npc.already-active");
            return;
        }

        // 이미 완료했고 반복 불가능한지 확인
        boolean hasCompleted = questManager.getCompletedQuests(player.getUniqueId())
                .contains(questId);

        if (hasCompleted && !quest.isRepeatable()) {
            langManager.sendMessage(player, "quest.npc.already-completed");
            return;
        }

        // 퀘스트 수락 GUI 열기
        guiManager.openQuestAcceptGui(player, quest);
    }
}