package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 몹 처치 퀘스트 목표
 * 특정 타입의 몹을 지정된 수만큼 처치
 *
 * @author Febrie
 */
public class KillMobObjective extends BaseObjective {

    private final EntityType targetType;
    private final @Nullable Component customName;

    /**
     * 기본 생성자
     *
     * @param id         목표 ID
     * @param targetType 대상 엔티티 타입
     * @param amount     처치 수
     */
    public KillMobObjective(@NotNull String id, @NotNull EntityType targetType, int amount) {
        this(id, targetType, amount, null);
    }

    /**
     * 커스텀 이름 포함 생성자 (Component)
     *
     * @param id         목표 ID
     * @param targetType 대상 엔티티 타입
     * @param amount     처치 수
     * @param customName 특정 이름을 가진 몹만 대상 (Component)
     */
    public KillMobObjective(@NotNull String id, @NotNull EntityType targetType, int amount,
                            @Nullable Component customName) {
        super(id, amount);
        this.targetType = Objects.requireNonNull(targetType);
        this.customName = customName;
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.KILL_MOB;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        String target = customName != null ?
                PlainTextComponentSerializer.plainText().serialize(customName) :
                targetType.translationKey();
        return target + " " + getProgressString(progress);
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof EntityDeathEvent deathEvent)) {
            return false;
        }

        // 킬러 확인
        if (deathEvent.getEntity().getKiller() == null ||
                !deathEvent.getEntity().getKiller().equals(player)) {
            return false;
        }

        // 엔티티 타입 확인
        if (!deathEvent.getEntityType().equals(targetType)) {
            return false;
        }

        // 커스텀 이름 확인
        if (customName != null) {
            Component entityName = deathEvent.getEntity().customName();
            return customName.equals(entityName);
        }

        return true;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
    }

    @Override
    protected @NotNull String serializeData() {
        if (customName != null) {
            // Component를 문자열로 직렬화
            String customNameStr = PlainTextComponentSerializer.plainText().serialize(customName);
            return targetType.name() + ";" + customNameStr;
        }
        return targetType.name();
    }

    /**
     * 대상 엔티티 타입 반환
     */
    public @NotNull EntityType getTargetType() {
        return targetType;
    }

    /**
     * 커스텀 이름 반환
     */
    public @Nullable Component getCustomName() {
        return customName;
    }
}