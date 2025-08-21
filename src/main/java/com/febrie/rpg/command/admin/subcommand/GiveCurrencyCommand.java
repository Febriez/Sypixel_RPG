package com.febrie.rpg.command.admin.subcommand;

import net.kyori.adventure.text.Component;
import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.SubCommand;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 재화 지급 서브커맨드
 *
 * @author Febrie, CoffeeTory
 */
public class GiveCurrencyCommand implements SubCommand {
    
    private final RPGPlayerManager playerManager;
    
    public GiveCurrencyCommand(@NotNull RPGMain plugin) {
        this.playerManager = plugin.getRPGPlayerManager();
    }
    
    @Override
    @NotNull
    public String getName() {
        return "givecurrency";
    }
    
    @Override
    @NotNull
    public String getDescription() {
        return "플레이어에게 재화를 지급합니다";
    }
    
    @Override
    @NotNull
    public String getUsage() {
        return "/rpgadmin givecurrency <플레이어> <재화타입> <금액>";
    }
    
    @Override
    @NotNull
    public String getPermission() {
        return "rpg.admin.currency";
    }
    
    @Override
    @NotNull
    public List<String> getAliases() {
        return List.of("givemoney", "addcurrency", "addmoney");
    }
    
    @Override
    public int getMinArgs() {
        return 3;
    }
    
    @Override
    public int getMaxArgs() {
        return 3;
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(UnifiedColorUtil.parse("&c플레이어를 찾을 수 없습니다: " + args[0]));
            return false;
        }
        
        CurrencyType currency;
        try {
            currency = CurrencyType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(UnifiedColorUtil.parse("&c유효하지 않은 재화 타입: " + args[1]));
            sender.sendMessage(UnifiedColorUtil.parse("&7사용 가능: " + 
                Arrays.stream(CurrencyType.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "))));
            return false;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                sender.sendMessage(UnifiedColorUtil.parse("&c금액은 0보다 커야 합니다"));
                return false;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(UnifiedColorUtil.parse("&c유효하지 않은 금액: " + args[2]));
            return false;
        }
        
        // RPGPlayer 가져오기
        RPGPlayer rpgPlayer = playerManager.getPlayer(target.getUniqueId());
        if (rpgPlayer == null) {
            sender.sendMessage(UnifiedColorUtil.parse("&c플레이어 데이터를 찾을 수 없습니다"));
            return false;
        }
        
        // 재화 추가
        boolean success = rpgPlayer.getWallet().add(currency, amount);
        
        if (success) {
            sender.sendMessage(UnifiedColorUtil.parse(String.format(
                "&a%s에게 %s %d개를 지급했습니다",
                target.getName(), currency.name(), amount
            )));
            target.sendMessage(UnifiedColorUtil.parse(String.format(
                "&a%s %d개를 받았습니다!",
                currency.name(), amount
            )));
            
            // 데이터 저장 요청
            playerManager.savePlayerDataAsync(rpgPlayer, true);
        } else {
            sender.sendMessage(UnifiedColorUtil.parse("&c재화 지급에 실패했습니다 (최대치 도달 또는 오류)"));
        }
        
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
            case 2 -> Arrays.stream(CurrencyType.values())
                .map(type -> type.name().toLowerCase())
                .filter(name -> name.startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
            case 3 -> List.of("100", "1000", "10000");
            default -> List.of();
        };
    }
}