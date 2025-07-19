package com.febrie.rpg.gui;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Alias for BaseGui - used by some island GUIs
 * This extends BaseGui to provide the same functionality
 *
 * @author Febrie, CoffeeTory
 */
public abstract class GuiHolder extends BaseGui {
    
    /**
     * Constructor - protected to prevent direct instantiation
     * @param plugin The main plugin instance
     * @param size The size of the inventory (must be multiple of 9)
     */
    protected GuiHolder(@NotNull RPGMain plugin, int size) {
        super(plugin, size);
    }
    
    /**
     * Factory method to create and initialize the GUI
     * @param gui The GUI instance to initialize
     * @param title The title of the GUI
     * @return The initialized GUI instance
     */
    protected static <T extends GuiHolder> T create(@NotNull T gui, @NotNull String title) {
        gui.initialize(ColorUtil.parseComponent(title));
        return gui;
    }
}