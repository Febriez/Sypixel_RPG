package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import net.kyori.adventure.text.Component;
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
    private final @Nullable String customName;

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
     * 커스텀 이름 포함 생성자
     *
     * @param id         목표 ID
     * @param targetType 대상 엔티티 타입
     * @param amount     처치 수
     * @param customName 특정 이름을 가진 몹만 대상
     */
    public KillMobObjective(@NotNull String id, @NotNull EntityType targetType, int amount,
                            @Nullable String customName) {
        super(id, amount,
                customName != null ? "quest.objective.kill_mob.custom" : "quest.objective.kill_mob",
                createPlaceholders(targetType, amount, customName));
        this.targetType = Objects.requireNonNull(targetType);
        this.customName = customName;
    }

    private static String[] createPlaceholders(EntityType type, int amount, @Nullable String customName) {
        if (customName != null) {
            return new String[]{
                    "mob", customName,
                    "amount", String.valueOf(amount)
            };
        } else {
            // 엔티티 타입은 마인크래프트 번역 키 사용
            return new String[]{
                    "mob_key", type.translationKey(),
                    "amount", String.valueOf(amount)
            };
        }
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.KILL_MOB;
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof EntityDeathEvent deathEvent)) {
            return false;
        }

        // 플레이어가 킬러인지 확인
        if (deathEvent.getEntity().getKiller() == null ||
                !deathEvent.getEntity().getKiller().equals(player)) {
            return false;
        }

        // 엔티티 타입 확인
        if (deathEvent.getEntityType() != targetType) {
            return false;
        }

        // 커스텀 이름 확인
        if (customName != null) {
            Component displayName = deathEvent.getEntity().customName();
            if (displayName == null) return false;

            String entityName = displayName.toString();
            return entityName.contains(customName);
        }

        return true;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
    }

    @Override
    protected @NotNull String serializeData() {
        return targetType.name() + (customName != null ? ":" + customName : "");
    }

    public EntityType getTargetType() {
        return targetType;
    }

    public @Nullable String getCustomName() {
        return customName;
    }
}