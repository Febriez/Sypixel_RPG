package com.febrie.rpg.command.admin.subcommand;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.SubCommand;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 레벨 설정 서브커맨드
 *
 * @author Febrie, CoffeeTory
 */
public class SetLevelCommand implements SubCommand {
    
    private final RPGMain plugin;
    private final RPGPlayerManager playerManager;
    
    public SetLevelCommand(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getRPGPlayerManager();
    }
    
    @Override
    @NotNull
    public String getName() {
        return "setlevel";
    }
    
    @Override
    @NotNull
    public String getDescription() {
        return "플레이어의 레벨을 설정합니다";
    }
    
    @Override
    @NotNull
    public String getUsage() {
        return "/rpgadmin setlevel <플레이어> <레벨>";
    }
    
    @Override
    @NotNull
    public String getPermission() {
        return "rpg.admin.setlevel";
    }
    
    @Override
    @NotNull
    public List<String> getAliases() {
        return List.of("level", "lvl");
    }
    
    @Override
    public int getMinArgs() {
        return 2;
    }
    
    @Override
    public int getMaxArgs() {
        return 2;
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(UnifiedColorUtil.parse("&c플레이어를 찾을 수 없습니다: " + args[0]));
            return false;
        }
        
        int level;
        try {
            level = Integer.parseInt(args[1]);
            if (level < 1 || level > 1000) {
                sender.sendMessage(UnifiedColorUtil.parse("&c레벨은 1-1000 사이여야 합니다"));
                return false;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(UnifiedColorUtil.parse("&c유효하지 않은 레벨: " + args[1]));
            return false;
        }
        
        RPGPlayer rpgPlayer = playerManager.getPlayer(target.getUniqueId());
        if (rpgPlayer == null) {
            sender.sendMessage(UnifiedColorUtil.parse("&c플레이어 데이터를 찾을 수 없습니다"));
            return false;
        }
        
        rpgPlayer.setLevel(level);
        playerManager.savePlayerDataAsync(rpgPlayer, true);
        
        sender.sendMessage(UnifiedColorUtil.parse(String.format(
            "&a%s의 레벨을 %d로 설정했습니다",
            target.getName(), level
        )));
        
        target.sendMessage(UnifiedColorUtil.parse(String.format(
            "&a당신의 레벨이 %d로 설정되었습니다!",
            level
        )));
        
        return true;
    }
    
    @Override
    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return switch (args.length) {
            case 1 -> Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
            case 2 -> List.of("1", "10", "50", "100", "200", "500", "1000");
            default -> List.of();
        };
    }
}