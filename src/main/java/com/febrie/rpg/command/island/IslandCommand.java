package com.febrie.rpg.command.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import com.febrie.rpg.gui.impl.island.IslandMainGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 섬 명령어 - /섬
 * 플레이어의 섬 상태에 따라 적절한 메뉴를 표시
 *
 * @author CoffeeTory
 */
public class IslandCommand implements CommandExecutor {
    
    private final RPGMain plugin;
    private final GuiManager guiManager;
    private final IslandManager islandManager;
    
    public IslandCommand(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.guiManager = plugin.getGuiManager();
        this.islandManager = plugin.getIslandManager();
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.island.player-only"));
            return true;
        }
        
        // 플레이어의 섬 데이터 가져오기
        String uuid = player.getUniqueId().toString();
        PlayerIslandDataDTO playerIslandData = islandManager.getPlayerIslandDataFromCache(uuid);
        
        if (playerIslandData == null || !playerIslandData.hasIsland()) {
            // 섬이 없는 경우 - 섬 생성 안내
            showNoIslandMessage(player);
        } else {
            // 섬이 있는 경우 - 섬 메뉴 열기
            openIslandMenu(player);
        }
        
        return true;
    }
    
    /**
     * 섬이 없는 플레이어에게 안내 메시지 표시
     */
    private void showNoIslandMessage(@NotNull Player player) {
        player.sendMessage(Component.text(""));
        player.sendMessage(LangManager.getMessage(player, "commands.island.no-island.title"));
        player.sendMessage(Component.text(""));
        player.sendMessage(LangManager.getMessage(player, "commands.island.no-island.message"));
        player.sendMessage(Component.text(""));
        player.sendMessage(LangManager.getMessage(player, "commands.island.no-island.benefits-title"));
        player.sendMessage(LangManager.getMessage(player, "commands.island.no-island.benefit-1"));
        player.sendMessage(LangManager.getMessage(player, "commands.island.no-island.benefit-2"));
        player.sendMessage(LangManager.getMessage(player, "commands.island.no-island.benefit-3"));
        player.sendMessage(LangManager.getMessage(player, "commands.island.no-island.benefit-4"));
        player.sendMessage(Component.text(""));
        player.sendMessage(LangManager.getMessage(player, "commands.island.no-island.how-to-title"));
        player.sendMessage(LangManager.getMessage(player, "commands.island.no-island.how-to-1"));
        player.sendMessage(LangManager.getMessage(player, "commands.island.no-island.how-to-2"));
        player.sendMessage(Component.text(""));
        player.sendMessage(LangManager.getMessage(player, "commands.island.no-island.divider"));
    }
    
    /**
     * 섬 메뉴 열기
     */
    private void openIslandMenu(@NotNull Player player) {
        IslandMainGui islandGui = IslandMainGui.create(guiManager, player);
        guiManager.openGui(player, islandGui);
    }
}