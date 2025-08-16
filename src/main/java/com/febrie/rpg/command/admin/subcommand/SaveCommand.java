package com.febrie.rpg.command.admin.subcommand;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.SubCommand;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 데이터 저장 서브커맨드
 *
 * @author Febrie, CoffeeTory
 */
public class SaveCommand implements SubCommand {
    
    private final RPGMain plugin;
    private final RPGPlayerManager playerManager;
    
    public SaveCommand(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getRPGPlayerManager();
    }
    
    @Override
    @NotNull
    public String getName() {
        return "save";
    }
    
    @Override
    @NotNull
    public String getDescription() {
        return "플레이어 데이터를 수동으로 저장합니다";
    }
    
    @Override
    @NotNull
    public String getUsage() {
        return "/rpgadmin save [플레이어|all]";
    }
    
    @Override
    @NotNull
    public String getPermission() {
        return "rpg.admin.save";
    }
    
    @Override
    @NotNull
    public List<String> getAliases() {
        return List.of();
    }
    
    @Override
    public int getMinArgs() {
        return 0;
    }
    
    @Override
    public int getMaxArgs() {
        return 1;
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("all")) {
            // 모든 온라인 플레이어 저장
            sender.sendMessage(ColorUtil.colorize("&e모든 온라인 플레이어 데이터를 저장합니다..."));
            
            CompletableFuture<Void> saveFuture = playerManager.saveAllOnlinePlayers();
            
            saveFuture.thenRun(() -> {
                int count = Bukkit.getOnlinePlayers().size();
                sender.sendMessage(ColorUtil.colorize(String.format(
                    "&a%d명의 플레이어 데이터를 저장했습니다",
                    count
                )));
            }).exceptionally(ex -> {
                sender.sendMessage(ColorUtil.colorize("&c데이터 저장 중 오류가 발생했습니다"));
                plugin.getLogger().severe("Save error: " + ex.getMessage());
                return null;
            });
            
            return true;
        }
        
        // 특정 플레이어 저장
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ColorUtil.colorize("&c플레이어를 찾을 수 없습니다: " + args[0]));
            return false;
        }
        
        RPGPlayer rpgPlayer = playerManager.getPlayer(target.getUniqueId());
        if (rpgPlayer == null) {
            sender.sendMessage(ColorUtil.colorize("&c플레이어 데이터를 찾을 수 없습니다"));
            return false;
        }
        
        sender.sendMessage(ColorUtil.colorize("&e" + target.getName() + "의 데이터를 저장합니다..."));
        
        playerManager.savePlayerDataAsync(rpgPlayer, true).thenAccept(success -> {
            if (success) {
                sender.sendMessage(ColorUtil.colorize("&a" + target.getName() + "의 데이터를 저장했습니다"));
            } else {
                sender.sendMessage(ColorUtil.colorize("&c데이터 저장에 실패했습니다"));
            }
        }).exceptionally(ex -> {
            sender.sendMessage(ColorUtil.colorize("&c데이터 저장 중 오류가 발생했습니다"));
            plugin.getLogger().severe("Save error: " + ex.getMessage());
            return null;
        });
        
        return true;
    }
    
    @Override
    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> suggestions = Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
            
            if ("all".startsWith(args[0].toLowerCase())) {
                suggestions.add(0, "all");
            }
            
            return suggestions;
        }
        return List.of();
    }
}