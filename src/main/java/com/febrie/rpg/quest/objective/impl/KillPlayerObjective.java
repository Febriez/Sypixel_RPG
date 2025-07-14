package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 플레이어 처치 퀘스트 목표
 * PvP 킬 카운트
 *
 * @author Febrie
 */
public class KillPlayerObjective extends BaseObjective {

    private final @Nullable String targetPlayerName;
    private final boolean checkWorld;
    private final @Nullable String worldName;

    /**
     * 기본 생성자 - 아무 플레이어나 처치
     *
     * @param id     목표 ID
     * @param amount 처치 수
     */
    public KillPlayerObjective(@NotNull String id, int amount) {
        this(id, amount, null, false, null);
    }

    /**
     * 특정 플레이어 처치 생성자
     *
     * @param id               목표 ID
     * @param amount           처치 수
     * @param targetPlayerName 대상 플레이어 이름
     */
    public KillPlayerObjective(@NotNull String id, int amount, @NotNull String targetPlayerName) {
        this(id, amount, targetPlayerName, false, null);
    }

    /**
     * 월드 제한 포함 생성자
     *
     * @param id               목표 ID
     * @param amount           처치 수
     * @param targetPlayerName 대상 플레이어 이름 (null이면 아무나)
     * @param checkWorld       월드 체크 여부
     * @param worldName        대상 월드 이름
     */
    public KillPlayerObjective(@NotNull String id, int amount, @Nullable String targetPlayerName,
                               boolean checkWorld, @Nullable String worldName) {
        super(id, amount, createDescription(amount, targetPlayerName, checkWorld, worldName));
        this.targetPlayerName = targetPlayerName;
        this.checkWorld = checkWorld;
        this.worldName = worldName;
    }

    private static Component createDescription(int amount, @Nullable String target,
                                               boolean checkWorld, @Nullable String world) {
        Component base;

        if (target != null) {
            base = Component.translatable("quest.objective.kill_player.specific",
                    Component.text(target), Component.text(amount));
        } else {
            base = Component.translatable("quest.objective.kill_player",
                    Component.text(amount));
        }

        if (checkWorld && world != null) {
            base = base.append(Component.text(" "))
                    .append(Component.translatable("quest.objective.kill_player.world",
                            Component.text(world)));
        }

        return base.color(NamedTextColor.RED);
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.KILL_PLAYER;
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof PlayerDeathEvent deathEvent)) {
            return false;
        }

        // 킬러 확인
        Player killer = deathEvent.getEntity().getKiller();
        if (killer == null || !killer.equals(player)) {
            return false;
        }

        // 자살 방지
        if (deathEvent.getEntity().equals(killer)) {
            return false;
        }

        // 특정 플레이어 확인
        if (targetPlayerName != null &&
                !deathEvent.getEntity().getName().equals(targetPlayerName)) {
            return false;
        }

        // 월드 확인
        if (checkWorld && worldName != null &&
                !deathEvent.getEntity().getWorld().getName().equals(worldName)) {
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
        StringBuilder data = new StringBuilder();
        if (targetPlayerName != null) data.append(targetPlayerName);
        data.append(":").append(checkWorld);
        if (worldName != null) data.append(":").append(worldName);
        return data.toString();
    }

    public @Nullable String getTargetPlayerName() {
        return targetPlayerName;
    }

    public boolean isCheckWorld() {
        return checkWorld;
    }

    public @Nullable String getWorldName() {
        return worldName;
    }
}