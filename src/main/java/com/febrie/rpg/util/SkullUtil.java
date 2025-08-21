package com.febrie.rpg.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Utility class for creating skull items
 *
 * @author Febrie, CoffeeTory
 */
public class SkullUtil {

    private SkullUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Creates a skull item from a player UUID
     *
     * @param uuid The UUID of the player
     * @return The skull ItemStack
     */
    public static ItemStack getSkullFromUUID(@NotNull String uuid) {
        try {
            UUID playerUuid = UUID.fromString(uuid);
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerUuid);
            return getSkullFromPlayer(player);
        } catch (IllegalArgumentException e) {
            // Invalid UUID, return default skull
            return new ItemStack(Material.PLAYER_HEAD);
        }
    }

    /**
     * Creates a skull item from an OfflinePlayer
     *
     * @param player The player
     * @return The skull ItemStack
     */
    public static ItemStack getSkullFromPlayer(@NotNull OfflinePlayer player) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            skull.setItemMeta(meta);
        }
        return skull;
    }

    /**
     * Creates a skull item from a player name
     *
     * @param playerName The name of the player
     * @return The skull ItemStack
     */
    public static ItemStack getSkullFromName(@NotNull String playerName) {
        // Minecraft usernames are limited to 16 characters
        // If the name is longer, truncate it or return default head
        if (playerName.length() > 16) {
            // Try to get by UUID if it's a UUID string
            try {
                UUID uuid = UUID.fromString(playerName);
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                return getSkullFromPlayer(player);
            } catch (IllegalArgumentException e) {
                // Not a UUID, truncate the name
                playerName = playerName.substring(0, 16);
            }
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        return getSkullFromPlayer(player);
    }

    /**
     * Alias for getSkullFromName - used by island GUIs
     *
     * @param playerName The name of the player
     * @return The skull ItemStack
     */
    public static ItemStack getPlayerHead(@NotNull String playerName) {
        return getSkullFromName(playerName);
    }
}