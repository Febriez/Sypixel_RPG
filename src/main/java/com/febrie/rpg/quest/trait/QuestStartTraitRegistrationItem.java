package com.febrie.rpg.quest.trait;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
import com.febrie.rpg.npc.trait.base.BaseTraitRegistrationItem;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.util.UnifiedColorUtil;
import net.citizensnpcs.api.trait.Trait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Quest Start Trait Registration Item
 * 특정 퀘스트를 시작할 수 있는 NPC로 설정하는 막대기
 */
public class QuestStartTraitRegistrationItem extends BaseTraitRegistrationItem {
    
    private static final NamespacedKey IS_QUEST_START_ITEM = new NamespacedKey(RPGMain.getPlugin(), "is_quest_start_trait_item");
    private static final NamespacedKey QUEST_ID_KEY = new NamespacedKey(RPGMain.getPlugin(), "quest_id");
    
    public QuestStartTraitRegistrationItem() {
        super(IS_QUEST_START_ITEM, QUEST_ID_KEY);
    }
    
    /**
     * Static factory method to create a quest start trait registration item
     */
    @NotNull
    public static ItemStack create(@NotNull QuestID questId, @NotNull String questName) {
        QuestStartTraitRegistrationItem item = new QuestStartTraitRegistrationItem();
        ItemStack stack = item.createRegistrationItem(questId.name(), questName);
        
        // 추가로 퀘스트 ID를 별도로 저장
        stack.getItemMeta().getPersistentDataContainer().set(QUEST_ID_KEY, PersistentDataType.STRING, questId.name());
        
        return stack;
    }
    
    @Override
    @NotNull
    public ItemStack createRegistrationItem(@NotNull String traitId, @NotNull String displayName) {
        // 퀘스트 ID는 대문자도 허용
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        item.setItemMeta(item.getItemMeta());
        
        var meta = item.getItemMeta();
        
        // Set display name
        meta.displayName(Component.text(getItemDisplayPrefix() + " - " + displayName, getItemColor())
                .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false));
        
        // Set lore
        List<Component> lore = createLore(traitId);
        meta.lore(lore);
        
        // Add enchantment glow
        meta.addEnchant(org.bukkit.enchantments.Enchantment.MENDING, 1, true);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        
        // Set persistent data
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, traitId);
        meta.getPersistentDataContainer().set(itemTypeKey, PersistentDataType.BOOLEAN, true);
        
        item.setItemMeta(meta);
        return item;
    }
    
    @Override
    protected boolean hasTraitId(@NotNull Trait trait) {
        RPGQuestTrait questTrait = (RPGQuestTrait) trait;
        return !questTrait.getQuestIds().isEmpty();
    }
    
    @Override
    @NotNull
    protected String getTraitId(@NotNull Trait trait) {
        RPGQuestTrait questTrait = (RPGQuestTrait) trait;
        return questTrait.getQuestIds().isEmpty() ? "" : questTrait.getQuestIds().get(0).name();
    }
    
    @Override
    protected void setTraitId(@NotNull Trait trait, @NotNull String traitId) {
        RPGQuestTrait questTrait = (RPGQuestTrait) trait;
        try {
            QuestID questId = QuestID.valueOf(traitId);
            questTrait.addQuest(questId);
            
            // 손에 책 아이템 설정
            var npc = trait.getNPC();
            if (npc != null) {
                var equipment = npc.getOrAddTrait(net.citizensnpcs.api.trait.trait.Equipment.class);
                equipment.set(net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot.HAND, 
                        new ItemStack(Material.BOOK));
            }
        } catch (IllegalArgumentException e) {
            // 잘못된 퀘스트 ID
        }
    }
    
    @Override
    @NotNull
    protected Class<? extends Trait> getTraitClass() {
        return RPGQuestTrait.class;
    }
    
    @Override
    @NotNull
    protected String getItemDisplayPrefix() {
        return "퀘스트 시작 NPC 설정기";
    }
    
    @Override
    @NotNull
    protected TextColor getItemColor() {
        return UnifiedColorUtil.LEGENDARY;
    }
    
    @Override
    @NotNull
    protected String getIdType() {
        return "퀘스트";
    }
    
    @Override
    @NotNull
    protected String getIdDisplayName() {
        return "퀘스트 ID";
    }
    
    @Override
    @NotNull
    protected TextColor getIdColor() {
        return UnifiedColorUtil.GOLD;
    }
    
    @Override
    @NotNull
    protected String getTraitName() {
        return "퀘스트 시작 Trait";
    }
    
    @Override
    @NotNull
    protected List<String> getDescription() {
        return List.of(
                "이 Trait가 부착된 NPC와 상호작용 시",
                "해당 퀘스트를 시작할 수 있습니다."
        );
    }
    
    @Override
    @NotNull
    protected String getSuccessMessage() {
        return "이제 이 NPC는 퀘스트를 부여합니다.";
    }
}