package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 아이템 전달 퀘스트 목표
 * NPC에게 특정 아이템 전달
 *
 * @author Febrie
 */
public class DeliverItemObjective extends BaseObjective {

    private final Component npcName;
    private final Map<Material, Integer> requiredItems;

    /**
     * 단일 아이템 전달 생성자 (문자열)
     *
     * @param id       목표 ID
     * @param npcName  대상 NPC 이름
     * @param itemType 전달할 아이템
     * @param amount   수량
     */
    public DeliverItemObjective(@NotNull String id, @NotNull String npcName,
                                @NotNull Material itemType, int amount) {
        this(id, Component.text(npcName), Map.of(itemType, amount));
    }

    /**
     * 다중 아이템 전달 생성자 (문자열)
     *
     * @param id            목표 ID
     * @param npcName       대상 NPC 이름
     * @param requiredItems 전달할 아이템 맵
     */
    public DeliverItemObjective(@NotNull String id, @NotNull String npcName,
                                @NotNull Map<Material, Integer> requiredItems) {
        this(id, Component.text(npcName), requiredItems);
    }

    /**
     * Component 기반 생성자
     *
     * @param id            목표 ID
     * @param npcName       대상 NPC 이름 (Component)
     * @param requiredItems 전달할 아이템 맵
     */
    public DeliverItemObjective(@NotNull String id, @NotNull Component npcName,
                                @NotNull Map<Material, Integer> requiredItems) {
        super(id, 1);
        this.npcName = Objects.requireNonNull(npcName);
        this.requiredItems = new HashMap<>(requiredItems);
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.DELIVER_ITEM;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        if (progress.isCompleted()) {
            return PlainTextComponentSerializer.plainText().serialize(npcName) + " ✓";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(PlainTextComponentSerializer.plainText().serialize(npcName)).append(" - ");
        boolean first = true;
        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getKey().translationKey()).append(" x").append(entry.getValue());
            first = false;
        }
        return sb.toString();
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

        // NPC 확인
        if (!(interactEvent.getRightClicked() instanceof Villager villager)) {
            return false;
        }

        // NPC 이름 확인
        Component customName = villager.customName();
        if (!npcName.equals(customName)) {
            return false;
        }

        // 인벤토리에 필요한 아이템이 있는지 확인
        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            if (!player.getInventory().contains(entry.getKey(), entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        if (!canProgress(event, player)) return 0;

        // 아이템 제거
        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            player.getInventory().removeItem(new ItemStack(entry.getKey(), entry.getValue()));
        }

        return 1;
    }

    @Override
    protected @NotNull String serializeData() {
        StringBuilder sb = new StringBuilder();
        sb.append(PlainTextComponentSerializer.plainText().serialize(npcName)).append(";");
        requiredItems.forEach((mat, amt) ->
                sb.append(mat.name()).append(":").append(amt).append(","));
        return sb.toString();
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
     * 필요한 아이템 맵 반환
     */
    public @NotNull Map<Material, Integer> getRequiredItems() {
        return new HashMap<>(requiredItems);
    }
}