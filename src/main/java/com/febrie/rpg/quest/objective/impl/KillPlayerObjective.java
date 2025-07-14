package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
        super(id, amount);
        this.targetPlayerName = targetPlayerName;
        this.checkWorld = checkWorld;
        this.worldName = worldName;
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.KILL_PLAYER;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        StringBuilder sb = new StringBuilder();

        sb.append(Objects.requireNonNullElse(targetPlayerName, "Players")).append(" ").append(getProgressString(progress));

        if (checkWorld && worldName != null) {
            sb.append(" (").append(worldName).append(")");
        }

        return sb.toString();
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

        // 특정 플레이어 대상인 경우
        if (targetPlayerName != null) {
            if (!deathEvent.getEntity().getName().equals(targetPlayerName)) {
                return false;
            }
        }

        // 월드 확인
        if (checkWorld && worldName != null) {
            return deathEvent.getEntity().getWorld().getName().equals(worldName);
        }

        return true;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
    }

    @Override
    protected @NotNull String serializeData() {
        return (targetPlayerName != null ? targetPlayerName : "") + ";" +
                checkWorld + ";" +
                (worldName != null ? worldName : "");
    }

    /**
     * 대상 플레이어 이름 반환
     */
    public @Nullable String getTargetPlayerName() {
        return targetPlayerName;
    }

    /**
     * 월드 체크 여부 반환
     */
    public boolean isCheckWorld() {
        return checkWorld;
    }

    /**
     * 대상 월드 이름 반환
     */
    public @Nullable String getWorldName() {
        return worldName;
    }
}