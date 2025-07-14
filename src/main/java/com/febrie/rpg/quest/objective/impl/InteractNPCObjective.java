package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * NPC 방문 퀘스트 목표
 * 특정 NPC와 상호작용
 *
 * @author Febrie
 */
public class InteractNPCObjective extends BaseObjective {

    private final String npcName;
    private final @Nullable Villager.Profession profession;

    /**
     * 이름 기반 생성자
     *
     * @param id      목표 ID
     * @param npcName NPC 이름
     */
    public InteractNPCObjective(@NotNull String id, @NotNull String npcName) {
        this(id, npcName, null);
    }

    /**
     * 직업 포함 생성자
     *
     * @param id         목표 ID
     * @param npcName    NPC 이름
     * @param profession 주민 직업 (선택사항)
     */
    public InteractNPCObjective(@NotNull String id, @NotNull String npcName,
                                @Nullable Villager.Profession profession) {
        super(id, 1, profession != null ? "quest.objective.interact_npc.profession" : "quest.objective.interact_npc",
                createPlaceholders(npcName, profession));
        this.npcName = Objects.requireNonNull(npcName);
        this.profession = profession;
    }

    private static String[] createPlaceholders(String npcName, @Nullable Villager.Profession profession) {
        if (profession != null) {
            return new String[]{"npc", npcName, "profession", profession.name().toLowerCase()};
        }
        return new String[]{"npc", npcName};
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.INTERACT_NPC;
    }

    @Override
    public @NotNull String getDescription(boolean isKorean) {
        return isKorean ?
                "퀘스트를 준 사람: 모험가 길드\n\n" +
                        npcName + "을(를) 찾아가 이야기를 나누어 보게나. " +
                        "그는 자네에게 중요한 정보를 제공할 걸세. " +
                        (profession != null ? "그는 마을의 " + profession.name().toLowerCase() + "(이)라네." : "") :

                "Quest Giver: Adventurer's Guild\n\n" +
                        "Go find " + npcName + " and talk to them. " +
                        "They will provide you with important information. " +
                        (profession != null ? "They are the village " + profession.name().toLowerCase() + "." : "");
    }

    @Override
    public @NotNull String getGiverName(boolean isKorean) {
        return isKorean ? "모험가 길드" : "Adventurer's Guild";
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof PlayerInteractEntityEvent interactEvent)) {
            return false;
        }

        // 플레이어 확인
        if (!interactEvent.getPlayer().equals(player)) {
            return false;
        }

        // 주민인지 확인
        if (!(interactEvent.getRightClicked() instanceof Villager villager)) {
            return false;
        }

        // 이름 확인
        Component customName = villager.customName();
        if (customName == null) return false;

        String entityName = customName.toString();
        if (!entityName.contains(npcName)) {
            return false;
        }

        // 직업 확인 (지정된 경우)
        if (profession != null && villager.getProfession() != profession) {
            return false;
        }

        return true;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
    }

    @Override
    protected @NotNull String serializeData() {
        return npcName + (profession != null ? ":" + profession.name() : "");
    }

    public String getNpcName() {
        return npcName;
    }

    public @Nullable Villager.Profession getProfession() {
        return profession;
    }
}