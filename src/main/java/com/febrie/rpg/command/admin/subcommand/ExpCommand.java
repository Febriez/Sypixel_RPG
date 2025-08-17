package com.febrie.rpg.command.admin.subcommand;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.BaseSubCommand;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.util.LangManager;
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
    
    public ExpCommand(@NotNull RPGMain plugin) {
        super("exp", "rpg.admin.exp", "Manage player experience points");
        this.playerManager = plugin.getRPGPlayerManager();
        this.setMinArgs(3);
        this.setUsage("/rpgadmin exp give <player> <amount>");
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 3 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.exp.usage"));
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
            return true;
        }
        
        try {
            int amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                sender.sendMessage(LangManager.getComponent(sender, "commands.admin.exp.must-positive"));
                return true;
            }
            
            RPGPlayer rpgPlayer = playerManager.getPlayer(target);
            if (rpgPlayer == null) {
                sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-data-not-found"));
                return true;
            }
            
            rpgPlayer.addExperience(amount);
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.exp.success", 
                "player", target.getName(), 
                "amount", String.valueOf(amount)));
            target.sendMessage(LangManager.getMessage(target, "commands.admin.exp.received", 
                "amount", String.valueOf(amount)));
            
        } catch (NumberFormatException e) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.invalid-number"));
        }
        
        return true;
    }
    
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("give");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return getOnlinePlayerNames().stream()
                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return Arrays.asList("100", "500", "1000", "5000", "10000");
        }
        return List.of();
    }
}