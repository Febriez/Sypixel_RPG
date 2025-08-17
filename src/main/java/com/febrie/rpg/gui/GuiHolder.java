package com.febrie.rpg.gui;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Alias for BaseGui - used by some island GUIs
 * This extends Framework BaseGui to provide compatibility
 *
 * @author Febrie, CoffeeTory
 */
public abstract class GuiHolder extends BaseGui {
    
    /**
     * Constructor - protected to prevent direct instantiation
     * @param viewer The viewer of the GUI
     * @param guiManager The GUI manager
     * @param size The size of the inventory (must be multiple of 9)
     */
    protected GuiHolder(@NotNull Player viewer, @NotNull GuiManager guiManager, int size) {
        super(viewer, guiManager, size, "gui.default.title");
    }
    
    /**
     * Legacy constructor for compatibility
     * @param plugin The main plugin instance
     * @param size The size of the inventory
     */
    protected GuiHolder(@NotNull RPGMain plugin, int size) {
        super(null, plugin.getGuiManager(), size, "gui.default.title");
    }
    
    /**
     * Factory method to create and initialize the GUI
     * @param gui The GUI instance to initialize
     * @param title The title of the GUI
     * @return The initialized GUI instance
     */
    protected static <T extends GuiHolder> T create(@NotNull T gui, @NotNull String title) {
        gui.initialize("gui.default.title");
        return gui;
    }
}