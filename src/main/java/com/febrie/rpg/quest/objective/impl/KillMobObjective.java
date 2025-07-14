package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
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
    private final String questGiver;

    /**
     * 기본 생성자
     *
     * @param id         목표 ID
     * @param targetType 대상 엔티티 타입
     * @param amount     처치 수
     */
    public KillMobObjective(@NotNull String id, @NotNull EntityType targetType, int amount) {
        this(id, targetType, amount, null, "마을 경비대장");
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
        this(id, targetType, amount, customName, "마을 경비대장");
    }

    /**
     * 전체 옵션 생성자
     *
     * @param id         목표 ID
     * @param targetType 대상 엔티티 타입
     * @param amount     처치 수
     * @param customName 특정 이름을 가진 몹만 대상
     * @param questGiver 퀘스트 제공자
     */
    public KillMobObjective(@NotNull String id, @NotNull EntityType targetType, int amount,
                            @Nullable String customName, @NotNull String questGiver) {
        super(id, amount,
                customName != null ? "quest.objective.kill_mob.custom" : "quest.objective.kill_mob",
                createPlaceholders(targetType, amount, customName));
        this.targetType = Objects.requireNonNull(targetType);
        this.customName = customName;
        this.questGiver = Objects.requireNonNull(questGiver);
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
    public @NotNull String getDescription(boolean isKorean) {
        if (customName != null) {
            return isKorean ?
                    "퀘스트를 준 사람: " + questGiver + "\n\n" +
                            "최근 " + customName + "(이)라는 이름의 몬스터가 마을 주변을 어지럽히고 있다네. " +
                            "용감한 모험가여, " + customName + " " + requiredAmount + "마리를 처치해 주게나. " +
                            "마을의 평화를 위해 자네의 도움이 필요하다네." :

                    "Quest Giver: " + questGiver + "\n\n" +
                            "Recently, monsters named " + customName + " have been disturbing the area around our village. " +
                            "Brave adventurer, please eliminate " + requiredAmount + " " + customName + ". " +
                            "We need your help to restore peace to our village.";
        } else {
            return isKorean ?
                    "퀘스트를 준 사람: " + questGiver + "\n\n" +
                            "요즘 " + targetType.name().toLowerCase().replace('_', ' ') +
                            "(이)가 마을 주변에서 사람들을 공격하고 있다네. " +
                            "이대로는 안 되겠어. 자네가 " + targetType.name().toLowerCase().replace('_', ' ') +
                            " " + requiredAmount + "마리를 처치해 주게나. " +
                            "마을 사람들이 안심하고 생활할 수 있도록 도와주게." :

                    "Quest Giver: " + questGiver + "\n\n" +
                            "These days, " + targetType.name().toLowerCase().replace('_', ' ') +
                            "s have been attacking people around the village. " +
                            "This cannot continue. Please eliminate " + requiredAmount + " of them. " +
                            "Help us so the villagers can live in peace.";
        }
    }

    @Override
    public @NotNull String getGiverName(boolean isKorean) {
        return questGiver;
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
            String entityName = deathEvent.getEntity().getCustomName();
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
            return targetType.name() + ";" + customName + ";" + questGiver;
        }
        return targetType.name() + ";;" + questGiver;
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
    public @Nullable String getCustomName() {
        return customName;
    }
}