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
import java.util.stream.Collectors;

/**
 * 디버그 서브커맨드
 *
 * @author Febrie, CoffeeTory
 */
public class DebugCommand implements SubCommand {
    
    private final RPGMain plugin;
    private final RPGPlayerManager playerManager;
    
    public DebugCommand(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getRPGPlayerManager();
    }
    
    @Override
    @NotNull
    public String getName() {
        return "debug";
    }
    
    @Override
    @NotNull
    public String getDescription() {
        return "디버그 모드를 토글하거나 정보를 확인합니다";
    }
    
    @Override
    @NotNull
    public String getUsage() {
        return "/rpgadmin debug [toggle|info|player <name>]";
    }
    
    @Override
    @NotNull
    public String getPermission() {
        return "rpg.admin.debug";
    }
    
    @Override
    @NotNull
    public List<String> getAliases() {
        return List.of("dbg");
    }
    
    @Override
    public int getMinArgs() {
        return 1;
    }
    
    @Override
    public int getMaxArgs() {
        return 2;
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "toggle" -> {
                boolean debugMode = plugin.getConfig().getBoolean("debug", false);
                debugMode = !debugMode;
                plugin.getConfig().set("debug", debugMode);
                plugin.saveConfig();
                
                sender.sendMessage(ColorUtil.colorize(
                    debugMode ? "&a디버그 모드가 활성화되었습니다" : "&c디버그 모드가 비활성화되었습니다"
                ));
                return true;
            }
            
            case "info" -> {
                sender.sendMessage(ColorUtil.colorize("&6=== 디버그 정보 ==="));
                sender.sendMessage(ColorUtil.colorize("&7서버 버전: &e" + Bukkit.getVersion()));
                sender.sendMessage(ColorUtil.colorize("&7플러그인 버전: &e" + plugin.getDescription().getVersion()));
                sender.sendMessage(ColorUtil.colorize("&7온라인 플레이어: &e" + Bukkit.getOnlinePlayers().size()));
                sender.sendMessage(ColorUtil.colorize("&7로드된 RPG플레이어: &e" + playerManager.getAllPlayers().size()));
                sender.sendMessage(ColorUtil.colorize("&7디버그 모드: &e" + plugin.getConfig().getBoolean("debug", false)));
                
                // 메모리 정보
                Runtime runtime = Runtime.getRuntime();
                long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
                long maxMemory = runtime.maxMemory() / 1024 / 1024;
                sender.sendMessage(ColorUtil.colorize(String.format(
                    "&7메모리 사용량: &e%dMB / %dMB",
                    usedMemory, maxMemory
                )));
                
                return true;
            }
            
            case "player" -> {
                if (args.length < 2) {
                    sender.sendMessage(ColorUtil.colorize("&c플레이어 이름을 입력해주세요"));
                    return false;
                }
                
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtil.colorize("&c플레이어를 찾을 수 없습니다: " + args[1]));
                    return false;
                }
                
                RPGPlayer rpgPlayer = playerManager.getPlayer(target.getUniqueId());
                if (rpgPlayer == null) {
                    sender.sendMessage(ColorUtil.colorize("&cRPG플레이어 데이터를 찾을 수 없습니다"));
                    return false;
                }
                
                sender.sendMessage(ColorUtil.colorize("&6=== " + target.getName() + " 디버그 정보 ==="));
                sender.sendMessage(ColorUtil.colorize("&7UUID: &e" + target.getUniqueId()));
                sender.sendMessage(ColorUtil.colorize("&7레벨: &e" + rpgPlayer.getLevel()));
                // sender.sendMessage(ColorUtil.colorize("&7경험치: &e" + rpgPlayer.getExp() + "/" + rpgPlayer.getRequiredExp())); // TODO: Implement getExp/getRequiredExp
                sender.sendMessage(ColorUtil.colorize("&7전투력: &e" + rpgPlayer.getCombatPower()));
                sender.sendMessage(ColorUtil.colorize("&7골드: &e" + rpgPlayer.getWallet().getBalance(com.febrie.rpg.economy.CurrencyType.GOLD)));
                sender.sendMessage(ColorUtil.colorize("&7위치: &e" + String.format("%.1f, %.1f, %.1f", 
                    target.getLocation().getX(), 
                    target.getLocation().getY(), 
                    target.getLocation().getZ())));
                sender.sendMessage(ColorUtil.colorize("&7월드: &e" + target.getWorld().getName()));
                
                return true;
            }
            
            default -> {
                sender.sendMessage(ColorUtil.colorize("&c알 수 없는 서브커맨드: " + subCommand));
                sender.sendMessage(ColorUtil.colorize("&7사용 가능: toggle, info, player"));
                return false;
            }
        }
    }
    
    @Override
    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("toggle", "info", "player").stream()
                .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                .toList();
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("player")) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        return List.of();
    }
}