package com.febrie.rpg.command.admin.subcommand;

import net.kyori.adventure.text.Component;
import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.BaseSubCommand;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 경험치 관리 명령어
 * 
 * @author Febrie
 */
public class ExpCommand extends BaseSubCommand {
    
    private final RPGPlayerManager playerManager;
    
    private ExpCommand(@NotNull String name, @NotNull String permission, @NotNull String description,
                      @NotNull RPGPlayerManager playerManager) {
        super(name, permission, description);
        this.playerManager = playerManager;
        this.setMinArgs(3);
        this.setUsage("/rpgadmin exp give <player> <amount>");
    }
    
    public static ExpCommand create(@NotNull RPGMain plugin) {
        return new ExpCommand("exp", "rpg.admin.exp", "Manage player experience points", plugin.getRPGPlayerManager());
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 3 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(Component.translatable("commands.admin.exp.usage"));
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.translatable("commands.admin.player-not-found"));
            return true;
        }
        
        try {
            int amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                sender.sendMessage(Component.translatable("commands.admin.exp.must-positive"));
                return true;
            }
            
            RPGPlayer rpgPlayer = playerManager.getPlayer(target);
            if (rpgPlayer == null) {
                sender.sendMessage(Component.translatable("commands.admin.player-data-not-found"));
                return true;
            }
            
            rpgPlayer.addExperience(amount);
            sender.sendMessage(Component.translatable("commands.admin.exp.success", 
                Component.text(target.getName()), Component.text(amount)));
            target.sendMessage(Component.translatable("commands.admin.exp.received", 
                Component.text(amount)));
            
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.translatable("commands.admin.invalid-number"));
        }
        
        return true;
    }
    
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("give");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return getOnlinePlayerNames().stream()
                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return List.of("100", "500", "1000", "5000", "10000");
        }
        return List.of();
    }
}