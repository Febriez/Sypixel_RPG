package com.febrie.rpg.command;

import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.LangManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles profile-related commands
 * Supports both Korean and English commands
 *
 * @author Febrie, CoffeeTory
 */
public class ProfileCommand extends BaseCommand {

    private final GuiManager guiManager;

    public ProfileCommand(@NotNull Plugin plugin, @NotNull LangManager langManager,
                          @NotNull GuiManager guiManager) {
        super(plugin, langManager);
        this.guiManager = guiManager;
    }

    @Override
    protected boolean executeCommand(@NotNull CommandSender sender, @NotNull Command command,
                                     @NotNull String label, @NotNull String[] args) {

        String commandName = command.getName().toLowerCase();

        // Handle profile command (자신의 프로필)
        if (commandName.equals("profile") || commandName.equals("프로필")) {
            return handleProfileCommand(sender, args);
        }

        // Handle viewprofile command (다른 플레이어 프로필)
        if (commandName.equals("viewprofile") || commandName.equals("프로필보기")) {
            return handleViewProfileCommand(sender, args);
        }

        return false;
    }

    /**
     * Handles the profile command (view own profile)
     */
    private boolean handleProfileCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }

        Player player = (Player) sender;

        if (!checkPermission(player, "sypixelrpg.profile.self")) {
            return true;
        }

        // Open player's own profile
        guiManager.openProfileGui(player);
        langManager.sendMessage(player, "commands.profile.success");

        return true;
    }

    /**
     * Handles the viewprofile command (view other player's profile)
     */
    private boolean handleViewProfileCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }

        Player player = (Player) sender;

        if (!checkPermission(player, "sypixelrpg.profile.others")) {
            return true;
        }

        if (args.length != 1) {
            langManager.sendMessage(player, "commands.viewprofile.usage");
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            langManager.sendMessage(player, "general.player-not-found", "player", args[0]);
            return true;
        }

        // Open target player's profile
        guiManager.openProfileGui(player, target);
        langManager.sendMessage(player, "commands.viewprofile.success", "player", target.getName());

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        String commandName = command.getName().toLowerCase();

        // Tab completion for viewprofile command
        if ((commandName.equals("viewprofile") || commandName.equals("프로필보기")) && args.length == 1) {
            List<String> playerNames = new ArrayList<>();
            plugin.getServer().getOnlinePlayers().forEach(player ->
                    playerNames.add(player.getName()));

            // Filter based on partial input
            String partial = args[0].toLowerCase();
            return playerNames.stream()
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .toList();
        }

        return null;
    }
}