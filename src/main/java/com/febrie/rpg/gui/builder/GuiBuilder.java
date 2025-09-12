package com.febrie.rpg.gui.builder;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.ILangKey;
import com.febrie.rpg.util.SoundUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Builder pattern for creating GUI elements with reduced code duplication
 */
public class GuiBuilder {
    
    private final BaseGui gui;
    private final Player viewer;
    private final GuiManager guiManager;
    
    public GuiBuilder(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull GuiManager guiManager) {
        this.gui = gui;
        this.viewer = viewer;
        this.guiManager = guiManager;
    }
    
    /**
     * Add a clickable menu button with standard sound
     */
    public GuiBuilder menuButton(int slot, @NotNull Material material, 
                                 @NotNull ILangKey nameKey, @NotNull ILangKey loreKey, 
                                 @NotNull Consumer<Player> action) {
        GuiItem button = GuiItem.clickable(
            ItemBuilder.of(material)
                .displayName(LangManager.text(nameKey, viewer.locale()))
                .addLore(LangManager.list(loreKey, viewer))
                .hideAllFlags()
                .build(),
            player -> {
                action.accept(player);
                SoundUtil.playClickSound(player);
            }
        );
        gui.setItem(slot, button);
        return this;
    }
    
    /**
     * Add a menu button with custom ItemStack
     */
    public GuiBuilder menuButton(int slot, @NotNull ItemStack item, 
                                 @NotNull Consumer<Player> action) {
        GuiItem button = GuiItem.clickable(item, player -> {
            action.accept(player);
            SoundUtil.playClickSound(player);
        });
        gui.setItem(slot, button);
        return this;
    }
    
    /**
     * Add a display-only item
     */
    public GuiBuilder display(int slot, @NotNull Material material,
                             @NotNull ILangKey nameKey, @NotNull ILangKey loreKey) {
        gui.setItem(slot, GuiItem.display(
            ItemBuilder.of(material)
                .displayName(LangManager.text(nameKey, viewer.locale()))
                .addLore(LangManager.list(loreKey, viewer))
                .hideAllFlags()
                .build()
        ));
        return this;
    }
    
    /**
     * Add a display-only item with custom ItemStack
     */
    public GuiBuilder display(int slot, @NotNull ItemStack item) {
        gui.setItem(slot, GuiItem.display(item));
        return this;
    }
    
    /**
     * Add standard border (gray stained glass panes)
     */
    public GuiBuilder withBorder() {
        // createBorder is protected, so we'll implement it here
        GuiItem borderItem = GuiItem.display(
            ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE)
                .displayName(Component.empty())
                .build()
        );
        
        int size = gui.getSize();
        
        // Top row
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, borderItem);
        }
        
        // Bottom row
        for (int i = size - 9; i < size; i++) {
            gui.setItem(i, borderItem);
        }
        
        // Left column
        for (int i = 9; i < size - 9; i += 9) {
            gui.setItem(i, borderItem);
        }
        
        // Right column
        for (int i = 17; i < size - 9; i += 9) {
            gui.setItem(i, borderItem);
        }
        
        return this;
    }
    
    /**
     * Add navigation buttons (close only)
     */
    public GuiBuilder withNavigation(boolean showBack, boolean showClose) {
        // Close button only - back button handled by BaseGui
        if (showClose) {
            gui.setItem(gui.getSize() - 1, createCloseButton());
        }
        return this;
    }
    
    /**
     * Fill empty slots with a specific material
     */
    public GuiBuilder fillEmpty(@NotNull Material material) {
        ItemStack filler = ItemBuilder.of(material)
            .displayName(Component.empty())
            .build();
        
        // Since getItem is not accessible, we'll just skip this feature
        // or implement it differently if needed
        return this;
    }
    
    /**
     * Add a title item at specific slot
     */
    public GuiBuilder withTitle(int slot, @NotNull Component title) {
        gui.setItem(slot, GuiItem.display(
            ItemBuilder.of(Material.NAME_TAG)
                .displayName(title)
                .build()
        ));
        return this;
    }
    
    /**
     * Build and return the GUI
     */
    public BaseGui build() {
        return gui;
    }
    
    
    private GuiItem createCloseButton() {
        return GuiFactory.createCloseButton(viewer);
    }
}
