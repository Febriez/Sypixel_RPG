package com.febrie.rpg.gui.framework;

/**
 * Interface for read-only GUI implementations
 * Used for information display without user interaction
 *
 * @author Febrie, CoffeeTory
 */
public interface DisplayGui extends GuiFramework {

    /**
     * Whether this GUI should prevent all item movements
     *
     * @return true to prevent item movements (default: true)
     */
    default boolean preventItemMovement() {
        return true;
    }

    /**
     * Whether this GUI should prevent dragging
     *
     * @return true to prevent dragging (default: true)
     */
    default boolean preventDragging() {
        return true;
    }

    /**
     * Auto-close time in ticks
     *
     * @return ticks until auto-close, or -1 to disable
     */
    default long getAutoCloseTime() {
        return -1L;
    }

    /**
     * Whether clicking should close the GUI
     *
     * @return true if clicking anywhere closes the GUI
     */
    default boolean closeOnClick() {
        return false;
    }
}