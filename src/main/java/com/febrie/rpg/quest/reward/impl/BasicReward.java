package com.febrie.rpg.quest.reward.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.quest.reward.QuestReward;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 기본 퀘스트 보상 구현체
 * 아이템, 경험치, 재화 등을 지급
 *
 * @author Febrie
 */
public class BasicReward implements QuestReward {

    private final List<ItemStack> items;
    private final Map<CurrencyType, Long> currencies;
    private final int experience;
    private final String descriptionKey;

    private BasicReward(Builder builder) {
        this.items = new ArrayList<>(builder.items);
        this.currencies = new EnumMap<>(builder.currencies);
        this.experience = builder.experience;
        this.descriptionKey = builder.descriptionKey;
    }

    @Override
    public void grant(@NotNull Player player) {
        RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager()
                .getOrCreatePlayer(player);

        // 아이템 지급
        for (ItemStack item : items) {
            if (player.getInventory().firstEmpty() == -1) {
                // 인벤토리가 꽉 찬 경우 바닥에 드롭
                player.getWorld().dropItemNaturally(player.getLocation(), item.clone());
            } else {
                player.getInventory().addItem(item.clone());
            }
        }

        // 재화 지급
        for (Map.Entry<CurrencyType, Long> entry : currencies.entrySet()) {
            rpgPlayer.getWallet().add(entry.getKey(), entry.getValue());
        }

        // 경험치 지급
        if (experience > 0) {
            rpgPlayer.addExperience(experience);
        }
    }

    @Override
    public @NotNull String getDescriptionKey() {
        return descriptionKey != null ? descriptionKey : "quest.reward.basic.description";
    }

    @Override
    public @NotNull String[] getPreviewKeys() {
        List<String> keys = new ArrayList<>();

        // 아이템 보상
        if (!items.isEmpty()) {
            keys.add("quest.reward.preview.items");
            for (ItemStack item : items) {
                keys.add("quest.reward.preview.item:" + item.getType().name() + ":" + item.getAmount());
            }
        }

        // 재화 보상
        if (!currencies.isEmpty()) {
            keys.add("quest.reward.preview.currencies");
            for (Map.Entry<CurrencyType, Long> entry : currencies.entrySet()) {
                keys.add("quest.reward.preview.currency:" + entry.getKey().getId() + ":" + entry.getValue());
            }
        }

        // 경험치 보상
        if (experience > 0) {
            keys.add("quest.reward.preview.experience:" + experience);
        }

        return keys.toArray(new String[0]);
    }

    @Override
    public @NotNull RewardType getType() {
        if (items.isEmpty() && currencies.isEmpty() && experience > 0) {
            return RewardType.EXPERIENCE;
        } else if (items.isEmpty() && !currencies.isEmpty() && experience == 0) {
            return RewardType.CURRENCY;
        } else if (!items.isEmpty() && currencies.isEmpty() && experience == 0) {
            return RewardType.ITEM;
        } else {
            return RewardType.MIXED;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 보상 빌더
     */
    public static class Builder {
        private final List<ItemStack> items = new ArrayList<>();
        private final Map<CurrencyType, Long> currencies = new EnumMap<>(CurrencyType.class);
        private int experience = 0;
        private String descriptionKey;

        public Builder addItem(@NotNull ItemStack item) {
            this.items.add(item);
            return this;
        }

        public Builder addItems(@NotNull ItemStack... items) {
            this.items.addAll(Arrays.asList(items));
            return this;
        }

        public Builder addCurrency(@NotNull CurrencyType type, long amount) {
            if (amount > 0) {
                this.currencies.merge(type, amount, Long::sum);
            }
            return this;
        }

        public Builder addExperience(int amount) {
            if (amount > 0) {
                this.experience += amount;
            }
            return this;
        }

        public Builder setDescriptionKey(@NotNull String key) {
            this.descriptionKey = key;
            return this;
        }

        public BasicReward build() {
            return new BasicReward(this);
        }
    }
}