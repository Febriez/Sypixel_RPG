package com.febrie.rpg.command;

import net.kyori.adventure.text.Component;
import com.febrie.rpg.RPGMain;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Base class for all commands in the RPG system
 * Provides common functionality and utilities
 *
 * @author Febrie, CoffeeTory
 */
public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    protected final Plugin plugin;

    protected BaseCommand(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        try {
            return executeCommand(sender, command, label, args);
        } catch (Exception e) {
            LogUtil.error("Error executing command " + command.getName(), e);

            if (sender instanceof Player player) {
                player.sendMessage(Component.translatable("general.error"));
            } else {
                sender.sendMessage("An error occurred while executing the command.");
            }
            return true;
        }
    }

    /**
     * Execute the command logic
     * Override this method in subclasses
     */
    protected abstract boolean executeCommand(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String label, @NotNull String[] args);

    /**
     * Checks if the sender is a player and sends error message if not
     */
    protected boolean requirePlayer(@NotNull CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return false;
        }
        return true;
    }

    /**
     * Checks if the player has the required permission
     */
    protected boolean checkPermission(@NotNull Player player, @NotNull String permission) {
        if (!player.hasPermission(permission)) {
            player.sendMessage(Component.translatable("general.no-permission"));
            return false;
        }
        return true;
    }

    /**
     * Registers this command to the plugin
     */
    public void register(@NotNull String commandName) {
        var command = RPGMain.getPlugin().getCommand(commandName);
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        } else {
            LogUtil.error("Failed to register command: " + commandName + " - command not found in plugin.yml");
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        return null; // Override in subclasses if tab completion is needed
    }
}