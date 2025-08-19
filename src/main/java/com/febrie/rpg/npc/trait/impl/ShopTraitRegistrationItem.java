package com.febrie.rpg.npc.trait.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.npc.trait.base.ImprovedBaseTraitRegistrationItem;
import com.febrie.rpg.npc.trait.RPGShopTrait;
import net.citizensnpcs.api.trait.Trait;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
/**
 * 상점 NPC Trait 등록 아이템
 * ImprovedBaseTraitRegistrationItem을 사용한 구현
 *
 * @author Febrie, CoffeeTory
 */
public class ShopTraitRegistrationItem extends ImprovedBaseTraitRegistrationItem {
    
    private ShopTraitRegistrationItem(@NotNull RPGMain plugin) {
        super(plugin);
    }
    
    public static ShopTraitRegistrationItem create(@NotNull RPGMain plugin) {
        ShopTraitRegistrationItem item = new ShopTraitRegistrationItem(plugin);
        plugin.getServer().getPluginManager().registerEvents(item, plugin);
        return item;
    }
    
    @Override
    @NotNull
    protected String getTraitType() {
        return "shop";
    }
    
    @Override
    @NotNull
    protected String getItemDisplayPrefix() {
        return "상점 NPC";
    }
    
    @Override
    @NotNull
    protected NamedTextColor getItemColor() {
        return NamedTextColor.GREEN;
    }
    
    @Override
    @NotNull
    protected Class<? extends Trait> getTraitClass() {
        return RPGShopTrait.class;
    }
    
    @Override
    @NotNull
    protected Trait createTrait(@NotNull String traitId) {
        RPGShopTrait trait = new RPGShopTrait();
        // Store the traitId in the trait's data if needed
        return trait;
    }
    
    @Override
    @NotNull
    protected String getSuccessMessage(@NotNull String traitId, int npcId) {
        return String.format("&aNPC #%d를 상점 NPC '%s'로 설정했습니다", npcId, traitId);
    }
}