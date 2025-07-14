package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 블럭 파괴 퀘스트 목표
 * 특정 블럭을 파괴
 *
 * @author Febrie
 */
public class BreakBlockObjective extends BaseObjective {

    private final Material blockType;
    private final String questGiver;

    /**
     * 기본 생성자
     *
     * @param id        목표 ID
     * @param blockType 파괴할 블럭 타입
     * @param amount    파괴 수량
     */
    public BreakBlockObjective(@NotNull String id, @NotNull Material blockType, int amount) {
        this(id, blockType, amount, "광부");
    }

    /**
     * 퀘스트 제공자 포함 생성자
     *
     * @param id         목표 ID
     * @param blockType  파괴할 블럭 타입
     * @param amount     파괴 수량
     * @param questGiver 퀘스트 제공자
     */
    public BreakBlockObjective(@NotNull String id, @NotNull Material blockType, int amount,
                               @NotNull String questGiver) {
        super(id, amount, "quest.objective.break_block",
                "block_key", blockType.translationKey(),
                "amount", String.valueOf(amount));
        this.blockType = Objects.requireNonNull(blockType);
        this.questGiver = Objects.requireNonNull(questGiver);

        if (!blockType.isBlock()) {
            throw new IllegalArgumentException("Material must be a block: " + blockType);
        }
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.BREAK_BLOCK;
    }

    @Override
    public @NotNull String getDescription(boolean isKorean) {
        return isKorean ?
                "퀘스트를 준 사람: " + questGiver + "\n\n" +
                        "광산에서 일손이 부족하다네. " +
                        blockType.name().toLowerCase().replace('_', ' ') + " " + requiredAmount + "개를 캐서 가져다 주게나. " +
                        "이 자원들은 마을의 건설에 필요한 중요한 재료라네." :

                "Quest Giver: " + questGiver + "\n\n" +
                        "We're short-handed at the mine. " +
                        "Please mine " + requiredAmount + " " + blockType.name().toLowerCase().replace('_', ' ') + " blocks. " +
                        "These resources are important materials needed for village construction.";
    }

    @Override
    public @NotNull String getGiverName(boolean isKorean) {
        return questGiver;
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof BlockBreakEvent breakEvent)) {
            return false;
        }

        // 플레이어 확인
        if (!breakEvent.getPlayer().equals(player)) {
            return false;
        }

        // 블럭 타입 확인
        return breakEvent.getBlock().getType() == blockType;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
    }

    @Override
    protected @NotNull String serializeData() {
        return blockType.name() + ";" + questGiver;
    }

    /**
     * 블럭 타입 반환
     */
    public @NotNull Material getBlockType() {
        return blockType;
    }
}