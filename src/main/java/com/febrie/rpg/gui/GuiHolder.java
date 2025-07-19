package com.febrie.rpg.gui;

import com.febrie.rpg.RPGMain;
import org.jetbrains.annotations.NotNull;

/**
 * Alias for BaseGui - used by some island GUIs
 * This extends BaseGui to provide the same functionality
 *
 * @author Febrie, CoffeeTory
 */
public abstract class GuiHolder extends BaseGui {
    
    /**
     * Constructor
     * @param plugin The main plugin instance
     * @param size The size of the inventory (must be multiple of 9)
     * @param title The title of the GUI
     */
    public GuiHolder(@NotNull RPGMain plugin, int size, @NotNull String title) {
        super(plugin, size, title);
    }
}