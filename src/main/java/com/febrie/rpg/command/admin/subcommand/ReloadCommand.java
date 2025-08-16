package com.febrie.rpg.command.admin.subcommand;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.SubCommand;
import com.febrie.rpg.util.ColorUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 리로드 서브커맨드
 *
 * @author Febrie, CoffeeTory
 */
public class ReloadCommand implements SubCommand {
    
    private final RPGMain plugin;
    
    public ReloadCommand(@NotNull RPGMain plugin) {
        this.plugin = plugin;
    }
    
    @Override
    @NotNull
    public String getName() {
        return "reload";
    }
    
    @Override
    @NotNull
    public String getDescription() {
        return "설정 파일을 다시 로드합니다";
    }
    
    @Override
    @NotNull
    public String getUsage() {
        return "/rpgadmin reload [config|lang|all]";
    }
    
    @Override
    @NotNull
    public String getPermission() {
        return "rpg.admin.reload";
    }
    
    @Override
    @NotNull
    public List<String> getAliases() {
        return List.of("rl");
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
        String type = args.length > 0 ? args[0].toLowerCase() : "all";
        
        sender.sendMessage(ColorUtil.colorize("&e리로드를 시작합니다..."));
        
        long startTime = System.currentTimeMillis();
        boolean success = true;
        
        try {
            switch (type) {
                case "config" -> {
                    plugin.reloadConfig();
                    sender.sendMessage(ColorUtil.colorize("&a✓ 설정 파일 리로드 완료"));
                }
                case "lang" -> {
                    plugin.getLangManager().reload();
                    sender.sendMessage(ColorUtil.colorize("&a✓ 언어 파일 리로드 완료"));
                }
                case "all" -> {
                    plugin.reloadConfig();
                    plugin.getLangManager().reload();
                    sender.sendMessage(ColorUtil.colorize("&a✓ 설정 파일 리로드 완료"));
                    sender.sendMessage(ColorUtil.colorize("&a✓ 언어 파일 리로드 완료"));
                }
                default -> {
                    sender.sendMessage(ColorUtil.colorize("&c알 수 없는 타입: " + type));
                    sender.sendMessage(ColorUtil.colorize("&7사용 가능: config, lang, all"));
                    return false;
                }
            }
        } catch (Exception e) {
            sender.sendMessage(ColorUtil.colorize("&c리로드 중 오류 발생: " + e.getMessage()));
            plugin.getLogger().severe("Reload error: " + e.getMessage());
            e.printStackTrace();
            success = false;
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        if (success) {
            sender.sendMessage(ColorUtil.colorize(String.format(
                "&a리로드 완료! (%dms)",
                elapsed
            )));
        } else {
            sender.sendMessage(ColorUtil.colorize("&c리로드 실패"));
        }
        
        return success;
    }
    
    @Override
    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("config", "lang", "all").stream()
                .filter(type -> type.startsWith(args[0].toLowerCase()))
                .toList();
        }
        return List.of();
    }
}