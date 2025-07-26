package com.febrie.rpg.quest.reward.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.quest.reward.QuestReward;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
                keys.add("quest.reward.preview.item");
            }
        }

        // 재화 보상
        if (!currencies.isEmpty()) {
            keys.add("quest.reward.preview.currencies");
            for (Map.Entry<CurrencyType, Long> entry : currencies.entrySet()) {
                keys.add("quest.reward.preview.currency");
            }
        }

        // 경험치 보상
        if (experience > 0) {
            keys.add("quest.reward.preview.experience");
        }

        return keys.toArray(new String[0]);
    }

    @Override
    public @NotNull RewardType getType() {
        int types = 0;
        if (!items.isEmpty()) types++;
        if (!currencies.isEmpty()) types++;
        if (experience > 0) types++;

        return types > 1 ? RewardType.MIXED :
                !items.isEmpty() ? RewardType.ITEM :
                        !currencies.isEmpty() ? RewardType.CURRENCY :
                                experience > 0 ? RewardType.EXPERIENCE :
                                        RewardType.MIXED;
    }

    @Override
    public @NotNull Component getDisplayInfo(@NotNull Player player) {
        LangManager langManager = RPGMain.getPlugin().getLangManager();
        Component result = Component.empty();
        boolean first = true;

        // 아이템 보상 표시
        if (!items.isEmpty()) {
            result = result.append(Component.text(langManager.getMessage(player, "quest.reward.items"), ColorUtil.YELLOW));

            for (ItemStack item : items) {
                result = result.append(Component.newline())
                        .append(Component.text("  • ", ColorUtil.GRAY))
                        .append(Component.translatable(item.getType().translationKey()))
                        .append(Component.text(" x" + item.getAmount(), ColorUtil.WHITE));
            }
            first = false;
        }

        // 재화 보상 표시
        if (!currencies.isEmpty()) {
            if (!first) result = result.append(Component.newline());
            result = result.append(Component.text(langManager.getMessage(player, "quest.reward.currency"), ColorUtil.GOLD));

            for (Map.Entry<CurrencyType, Long> entry : currencies.entrySet()) {
                String currencyName = getCurrencyName(entry.getKey(), player);
                result = result.append(Component.newline())
                        .append(Component.text("  • " + currencyName + " ", ColorUtil.GRAY))
                        .append(Component.text(entry.getValue().toString(), ColorUtil.WHITE));
            }
            first = false;
        }

        // 경험치 보상 표시
        if (experience > 0) {
            if (!first) result = result.append(Component.newline());
            result = result.append(Component.text(langManager.getMessage(player, "quest.reward.experience"), ColorUtil.EMERALD))
                    .append(Component.text(" " + experience, ColorUtil.WHITE));
        }

        return result;
    }

    /**
     * 로어용 보상 정보 목록 생성
     * 각 보상 항목을 개별 Component로 반환
     */
    public @NotNull List<Component> getLoreComponents(@NotNull Player player) {
        LangManager langManager = RPGMain.getPlugin().getLangManager();
        List<Component> loreComponents = new ArrayList<>();

        // 아이템 보상 표시
        if (!items.isEmpty()) {
            loreComponents.add(Component.text("  " + langManager.getMessage(player, "quest.reward.items"), ColorUtil.YELLOW));

            for (ItemStack item : items) {
                loreComponents.add(Component.text("    • ", ColorUtil.GRAY)
                        .append(Component.translatable(item.getType().translationKey()))
                        .append(Component.text(" x" + item.getAmount(), ColorUtil.WHITE)));
            }
        }

        // 재화 보상 표시
        if (!currencies.isEmpty()) {
            loreComponents.add(Component.text("  " + langManager.getMessage(player, "quest.reward.currency"), ColorUtil.GOLD));

            for (Map.Entry<CurrencyType, Long> entry : currencies.entrySet()) {
                String currencyName = getCurrencyName(entry.getKey(), player);
                loreComponents.add(Component.text("    • " + currencyName + " ", ColorUtil.GRAY)
                        .append(Component.text(entry.getValue().toString(), ColorUtil.WHITE)));
            }
        }

        // 경험치 보상 표시
        if (experience > 0) {
            loreComponents.add(Component.text("  " + langManager.getMessage(player, "quest.reward.experience"), ColorUtil.EMERALD)
                    .append(Component.text(" " + experience, ColorUtil.WHITE)));
        }

        return loreComponents;
    }

    /**
     * 재화 이름 가져오기
     */
    private String getCurrencyName(CurrencyType type, Player player) {
        LangManager langManager = RPGMain.getPlugin().getLangManager();
        String currencyKey = type.name().toLowerCase();
        return langManager.getMessage(player, "quest.reward.currencies." + currencyKey);
    }

    /**
     * 빌더 클래스
     */
    public static class Builder {
        private final List<ItemStack> items = new ArrayList<>();
        private final Map<CurrencyType, Long> currencies = new EnumMap<>(CurrencyType.class);
        private int experience = 0;
        private String descriptionKey;

        public Builder addItem(@NotNull ItemStack item) {
            this.items.add(item.clone());
            return this;
        }

        public Builder addCurrency(@NotNull CurrencyType type, long amount) {
            this.currencies.merge(type, amount, Long::sum);
            return this;
        }

        public Builder addExperience(int amount) {
            this.experience += amount;
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

    /**
     * 빌더 생성
     */
    public static Builder builder() {
        return new Builder();
    }
}