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
    private final LangManager langManager;
    private final IslandManager islandManager;
    
    public IslandCommand(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.guiManager = plugin.getGuiManager();
        this.langManager = plugin.getLangManager();
        this.islandManager = plugin.getIslandManager();
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("이 명령어는 플레이어만 사용할 수 있습니다.", ColorUtil.ERROR));
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
        player.sendMessage(Component.text("==== 섬 시스템 ====", ColorUtil.YELLOW));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("아직 섬이 없습니다!", ColorUtil.ERROR));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("섬을 생성하면 다음과 같은 기능을 사용할 수 있습니다:", ColorUtil.GRAY));
        player.sendMessage(Component.text("• 자신만의 개인 섬에서 자유로운 건축", ColorUtil.WHITE));
        player.sendMessage(Component.text("• 친구들을 초대하여 함께 플레이", ColorUtil.WHITE));
        player.sendMessage(Component.text("• 섬 업그레이드로 더 넓은 공간 확보", ColorUtil.WHITE));
        player.sendMessage(Component.text("• 섬 워프 포인트 설정", ColorUtil.WHITE));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("섬 생성 방법:", ColorUtil.YELLOW));
        player.sendMessage(Component.text("1. 메인 메뉴(/메뉴)에서 섬 버튼 클릭", ColorUtil.GRAY));
        player.sendMessage(Component.text("2. 섬 생성 버튼을 눌러 새로운 섬 생성", ColorUtil.GRAY));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("==================", ColorUtil.YELLOW));
    }
    
    /**
     * 섬 메뉴 열기
     */
    private void openIslandMenu(@NotNull Player player) {
        IslandMainGui islandGui = IslandMainGui.create(guiManager, langManager, player);
        guiManager.openGui(player, islandGui);
    }
}