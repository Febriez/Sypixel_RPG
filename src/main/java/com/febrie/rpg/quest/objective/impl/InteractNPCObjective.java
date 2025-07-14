package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
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

    private final Component npcName;
    private final @Nullable Villager.Profession profession;

    /**
     * 이름 기반 생성자 (문자열)
     *
     * @param id      목표 ID
     * @param npcName NPC 이름
     */
    public InteractNPCObjective(@NotNull String id, @NotNull String npcName) {
        this(id, Component.text(npcName), null);
    }

    /**
     * 직업 포함 생성자 (문자열)
     *
     * @param id         목표 ID
     * @param npcName    NPC 이름
     * @param profession 주민 직업 (선택사항)
     */
    public InteractNPCObjective(@NotNull String id, @NotNull String npcName,
                                @Nullable Villager.Profession profession) {
        this(id, Component.text(npcName), profession);
    }

    /**
     * Component 기반 생성자
     *
     * @param id         목표 ID
     * @param npcName    NPC 이름 (Component)
     * @param profession 주민 직업 (선택사항)
     */
    public InteractNPCObjective(@NotNull String id, @NotNull Component npcName,
                                @Nullable Villager.Profession profession) {
        super(id, 1);
        this.npcName = Objects.requireNonNull(npcName);
        this.profession = profession;
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.INTERACT_NPC;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        String status = PlainTextComponentSerializer.plainText().serialize(npcName);
        if (profession != null) {
            status += " (" + profession.translationKey() + ")";
        }
        if (progress.isCompleted()) {
            status += " ✓";
        }
        return status;
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
        if (!npcName.equals(customName)) {
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
        String npcNameStr = PlainTextComponentSerializer.plainText().serialize(npcName);
        if (profession != null) {
            // Registry를 사용하여 NamespacedKey 가져오기
            NamespacedKey key = Registry.VILLAGER_PROFESSION.getKey(profession);
            return npcNameStr + ":" + (key != null ? key.toString() : "none");
        }
        return npcNameStr;
    }

    /**
     * 직렬화된 데이터에서 객체 생성 (역직렬화)
     */
    public static InteractNPCObjective deserialize(@NotNull String id, @NotNull String data) {
        String[] parts = data.split(":", 2);
        String npcName = parts[0];

        if (parts.length > 1 && !parts[1].equals("none")) {
            try {
                // NamespacedKey로부터 Profession 가져오기
                NamespacedKey key = NamespacedKey.fromString(parts[1]);
                if (key != null) {
                    Villager.Profession profession = Registry.VILLAGER_PROFESSION.get(key);
                    if (profession != null) {
                        return new InteractNPCObjective(id, npcName, profession);
                    }
                }
            } catch (Exception e) {
                // 역직렬화 실패 시 기본값으로
            }
        }

        return new InteractNPCObjective(id, npcName);
    }

    /**
     * NPC 이름 반환 (Component)
     */
    public @NotNull Component getNpcName() {
        return npcName;
    }

    /**
     * NPC 이름을 문자열로 반환
     */
    public @NotNull String getNpcNameAsString() {
        return PlainTextComponentSerializer.plainText().serialize(npcName);
    }

    /**
     * 직업 반환
     */
    public @Nullable Villager.Profession getProfession() {
        return profession;
    }
}