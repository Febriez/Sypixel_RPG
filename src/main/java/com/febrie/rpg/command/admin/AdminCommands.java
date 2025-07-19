package com.febrie.rpg.command.admin;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.npc.trait.RPGGuideTrait;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
import com.febrie.rpg.npc.trait.RPGShopTrait;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.registry.QuestRegistry;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LogUtil;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandLocationDTO;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RPG 관리자 명령어 처리
 * 디버그, 통계, 플레이어 관리 등
 *
 * @author Febrie
 */
public class AdminCommands implements CommandExecutor, TabCompleter {

    private final RPGMain plugin;
    private final RPGPlayerManager playerManager;
    private final GuiManager guiManager;
    private final LangManager langManager;
    private final QuestManager questManager;
    private final IslandManager islandManager;

    public AdminCommands(@NotNull RPGMain plugin, @NotNull RPGPlayerManager playerManager,
                         @NotNull GuiManager guiManager, @NotNull LangManager langManager) {
        this.plugin = plugin;
        this.playerManager = playerManager;
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.questManager = QuestManager.getInstance();
        this.islandManager = plugin.getIslandManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("rpg.admin")) {
            sender.sendMessage(Component.text("권한이 없습니다.", ColorUtil.ERROR));
            return true;
        }

        if (args.length == 0) {
            showUsage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        return switch (subCommand) {
            case "stats" -> handleStatsCommand(sender, args);
            case "reload" -> handleReloadCommand(sender, args);
            case "viewprofile" -> handleViewProfileCommand(sender, args);
            case "exp" -> handleExpCommand(sender, args);
            case "level" -> handleLevelCommand(sender, args);
            case "job" -> handleJobCommand(sender, args);
            case "npc" -> handleNpcCommand(sender, args);
            case "quest" -> handleQuestCommand(sender, args);
            case "island" -> handleIslandCommand(sender, args);
            default -> {
                showUsage(sender);
                yield true;
            }
        };
    }

    /**
     * 사용법 표시
     */
    private void showUsage(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text("=== RPG Admin Commands ===", ColorUtil.GOLD));
        sender.sendMessage(Component.text("/rpgadmin stats - 서버 통계 확인", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin reload - 설정 리로드", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin viewprofile <플레이어> - 프로필 확인", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin exp give <플레이어> <경험치> - 경험치 지급", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin level set <플레이어> <레벨> - 레벨 설정", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin job set <플레이어> <직업> - 직업 설정", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin npc set <퀘스트ID> - NPC에 퀘스트 설정", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin npc setcode <npcID> [이름] - NPC Trait 등록 막대기 지급", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin quest <give|list|reload> - 퀘스트 관리", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin island <info|delete|reset|tp> <플레이어> - 섬 관리", ColorUtil.YELLOW));
    }

    /**
     * 통계 명령어 처리
     */
    private boolean handleStatsCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        sender.sendMessage(Component.text("=== RPG 서버 통계 ===", ColorUtil.GOLD));
        sender.sendMessage(Component.text("온라인 플레이어: " + Bukkit.getOnlinePlayers().size(), ColorUtil.WHITE));
        sender.sendMessage(Component.text("로드된 RPG 플레이어: " + playerManager.getAllPlayers().size(), ColorUtil.WHITE));
        sender.sendMessage(Component.text("등록된 퀘스트: " + QuestID.values().length, ColorUtil.WHITE));
        sender.sendMessage(Component.text("구현된 퀘스트: " + QuestRegistry.getImplementedCount(), ColorUtil.WHITE));

        // 메모리 사용량
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        sender.sendMessage(Component.text("메모리 사용량: " + usedMemory + "MB / " + maxMemory + "MB", ColorUtil.GRAY));

        return true;
    }

    /**
     * 리로드 명령어 처리
     */
    private boolean handleReloadCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        sender.sendMessage(Component.text("설정을 리로드하는 중...", ColorUtil.YELLOW));

        // 언어 파일 리로드
        langManager.reload();

        // TODO: 다른 설정 파일들도 리로드

        sender.sendMessage(Component.text("설정 리로드 완료!", ColorUtil.SUCCESS));
        return true;
    }


    /**
     * 프로필 보기 명령어 처리
     */
    private boolean handleViewProfileCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("사용법: /rpgadmin viewprofile <플레이어>", ColorUtil.ERROR));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다.", ColorUtil.ERROR));
            return true;
        }

        RPGPlayer rpgPlayer = playerManager.getPlayer(target);
        if (rpgPlayer == null) {
            sender.sendMessage(Component.text("RPG 플레이어 데이터를 찾을 수 없습니다.", ColorUtil.ERROR));
            return true;
        }

        // 프로필 정보 표시
        sender.sendMessage(Component.text("=== " + target.getName() + "의 프로필 ===", ColorUtil.GOLD));
        sender.sendMessage(Component.text("레벨: " + rpgPlayer.getLevel(), ColorUtil.WHITE));
        sender.sendMessage(Component.text("경험치: " + rpgPlayer.getExperience() + "/" + rpgPlayer.getExperienceToNextLevel(), ColorUtil.WHITE));
        sender.sendMessage(Component.text("직업: " + (rpgPlayer.getJob() != null ? rpgPlayer.getJob().name() : "없음"), ColorUtil.WHITE));
        sender.sendMessage(Component.text("골드: " + rpgPlayer.getWallet().getBalance(CurrencyType.GOLD), ColorUtil.GOLD));
        sender.sendMessage(Component.text("다이아몬드: " + rpgPlayer.getWallet().getBalance(CurrencyType.DIAMOND), ColorUtil.AQUA));

        // 퀘스트 정보
        List<QuestProgress> activeQuests = questManager.getActiveQuests(target.getUniqueId());
        sender.sendMessage(Component.text("진행중인 퀘스트: " + activeQuests.size() + "개", ColorUtil.YELLOW));

        return true;
    }

    /**
     * 경험치 명령어 처리
     */
    private boolean handleExpCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 4 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage(Component.text("사용법: /rpgadmin exp give <플레이어> <경험치>", ColorUtil.ERROR));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다.", ColorUtil.ERROR));
            return true;
        }

        try {
            int amount = Integer.parseInt(args[3]);
            if (amount <= 0) {
                sender.sendMessage(Component.text("경험치는 양수여야 합니다.", ColorUtil.ERROR));
                return true;
            }

            RPGPlayer rpgPlayer = playerManager.getPlayer(target);
            if (rpgPlayer == null) {
                sender.sendMessage(Component.text("RPG 플레이어 데이터를 찾을 수 없습니다.", ColorUtil.ERROR));
                return true;
            }

            rpgPlayer.addExperience(amount);
            sender.sendMessage(Component.text(target.getName() + "에게 " + amount + " 경험치를 지급했습니다.", ColorUtil.SUCCESS));
            target.sendMessage(Component.text(amount + " 경험치를 받았습니다!", ColorUtil.EMERALD));

        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("올바른 숫자를 입력해주세요.", ColorUtil.ERROR));
        }

        return true;
    }

    /**
     * 레벨 명령어 처리
     */
    private boolean handleLevelCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 4 || !args[1].equalsIgnoreCase("set")) {
            sender.sendMessage(Component.text("사용법: /rpgadmin level set <플레이어> <레벨>", ColorUtil.ERROR));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다.", ColorUtil.ERROR));
            return true;
        }

        try {
            int level = Integer.parseInt(args[3]);
            if (level < 1 || level > 100) {
                sender.sendMessage(Component.text("레벨은 1-100 사이여야 합니다.", ColorUtil.ERROR));
                return true;
            }

            RPGPlayer rpgPlayer = playerManager.getPlayer(target);
            if (rpgPlayer == null) {
                sender.sendMessage(Component.text("RPG 플레이어 데이터를 찾을 수 없습니다.", ColorUtil.ERROR));
                return true;
            }

            // 레벨에 맞는 총 경험치 계산
            long totalExp = 0;
            for (int i = 1; i < level; i++) {
                totalExp += rpgPlayer.getExpForLevel(i);
            }

            rpgPlayer.setExperience(totalExp);
            sender.sendMessage(Component.text(target.getName() + "의 레벨을 " + level + "로 설정했습니다.", ColorUtil.SUCCESS));
            target.sendMessage(Component.text("레벨이 " + level + "로 설정되었습니다!", ColorUtil.GOLD));

        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("올바른 숫자를 입력해주세요.", ColorUtil.ERROR));
        }

        return true;
    }

    /**
     * 직업 명령어 처리
     */
    private boolean handleJobCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // TODO: 직업 변경 구현
        sender.sendMessage(Component.text("직업 변경 기능은 아직 구현되지 않았습니다.", ColorUtil.WARNING));
        return true;
    }

    /**
     * NPC 명령어 처리 (Citizens API 사용)
     */
    private boolean handleNpcCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("플레이어만 사용할 수 있습니다.", ColorUtil.ERROR));
            return true;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            sender.sendMessage(Component.text("Citizens 플러그인이 설치되어 있지 않습니다.", ColorUtil.ERROR));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("사용법:", ColorUtil.ERROR));
            sender.sendMessage(Component.text("  /rpgadmin npc set <퀘스트ID>", ColorUtil.YELLOW));
            sender.sendMessage(Component.text("  /rpgadmin npc setcode <npc코드>", ColorUtil.YELLOW));
            sender.sendMessage(Component.text("  /rpgadmin npc list", ColorUtil.YELLOW));
            return true;
        }

        String subCmd = args[1].toLowerCase();

        switch (subCmd) {
            case "set" -> {
                if (args.length < 3) {
                    sender.sendMessage(Component.text("사용법: /rpgadmin npc set <퀘스트ID>", ColorUtil.ERROR));
                    sender.sendMessage(Component.text("예시: /rpgadmin npc set TUTORIAL_FIRST_STEPS", ColorUtil.GRAY));
                    return true;
                }

                // 퀘스트 ID 파싱
                try {
                    QuestID questId = QuestID.valueOf(args[2].toUpperCase());
                    
                    // NPCTraitSetter를 통해 대기 상태로 설정
                    com.febrie.rpg.npc.NPCTraitSetter.getInstance().prepareQuestTrait(player, questId);
                    
                } catch (IllegalArgumentException e) {
                    player.sendMessage(Component.text("잘못된 퀘스트 ID입니다: " + args[2], ColorUtil.ERROR));
                    player.sendMessage(Component.text("사용 가능한 퀘스트 ID:", ColorUtil.YELLOW));
                    
                    // 모든 퀘스트 ID 나열
                    for (QuestID id : QuestID.values()) {
                        player.sendMessage(Component.text("  - " + id.name(), ColorUtil.GRAY));
                    }
                    return true;
                }
            }
            
            case "setcode" -> {
                // player 변수가 이미 정의되어 있으므로 타입 체크만
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Component.text("이 명령어는 플레이어만 사용할 수 있습니다.", ColorUtil.ERROR));
                    return true;
                }
                
                if (args.length < 3) {
                    sender.sendMessage(Component.text("사용법: /rpgadmin npc setcode <npcID> [이름]", ColorUtil.ERROR));
                    sender.sendMessage(Component.text("예시: /rpgadmin npc setcode village_shopper 마을_상인", ColorUtil.GRAY));
                    return true;
                }
                
                String npcId = args[2];
                
                // NPC ID 유효성 검사 (영문 소문자와 언더스코어만 허용)
                if (!npcId.matches("^[a-z_]+$")) {
                    sender.sendMessage(Component.text("오류: NPC ID는 영문 소문자와 언더스코어(_)만 사용할 수 있습니다.", ColorUtil.ERROR));
                    sender.sendMessage(Component.text("올바른 예시: village_shopper, first_npc, quest_giver", ColorUtil.GRAY));
                    return true;
                }
                
                String displayName = args.length > 3 ? String.join(" ", Arrays.copyOfRange(args, 3, args.length)) : npcId;
                
                // Trait 등록 막대기 생성
                ItemStack traitItem = com.febrie.rpg.quest.trait.QuestTraitRegistrationItem.createRegistrationItem(npcId, displayName);
                
                // 인벤토리가 가득 찬 경우 바닥에 드롭
                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItem(player.getLocation(), traitItem);
                    player.sendMessage(Component.text("인벤토리가 가득 차서 아이템을 바닥에 드롭했습니다.", ColorUtil.WARNING));
                } else {
                    player.getInventory().addItem(traitItem);
                }
                
                player.sendMessage(Component.text("✓ ", ColorUtil.SUCCESS)
                        .append(Component.text("NPC Trait 등록기를 지급했습니다.", ColorUtil.COMMON)));
                player.sendMessage(Component.text("NPC ID: ", ColorUtil.GRAY)
                        .append(Component.text(npcId, ColorUtil.RARE)));
                player.sendMessage(Component.text("표시 이름: ", ColorUtil.GRAY)
                        .append(Component.text(displayName, ColorUtil.YELLOW)));
            }
            
            case "list" -> {
                sender.sendMessage(Component.text("=== Citizens NPC 목록 ===", ColorUtil.GOLD));
                int count = 0;
                for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
                    Component npcInfo = Component.text(String.format("[%d] %s", npc.getId(), npc.getName()), ColorUtil.YELLOW);
                    
                    // RPGQuestTrait 확인
                    if (npc.hasTrait(RPGQuestTrait.class)) {
                        RPGQuestTrait trait = npc.getTraitNullable(RPGQuestTrait.class);
                        if (trait.hasNpcId()) {
                            npcInfo = npcInfo.append(Component.text(" - ID: " + trait.getNpcId(), ColorUtil.AQUA));
                        }
                        if (!trait.getQuestIds().isEmpty()) {
                            npcInfo = npcInfo.append(Component.text(" - 퀘스트: " + trait.getQuestIds().size() + "개", ColorUtil.GREEN));
                        }
                    }
                    
                    sender.sendMessage(npcInfo);
                    count++;
                }
                sender.sendMessage(Component.text("총 " + count + "개의 NPC", ColorUtil.GRAY));
            }
            
            default -> {
                sender.sendMessage(Component.text("사용법:", ColorUtil.ERROR));
                sender.sendMessage(Component.text("  /rpgadmin npc set <퀘스트ID>", ColorUtil.YELLOW));
                sender.sendMessage(Component.text("  /rpgadmin npc setcode <npcID>", ColorUtil.YELLOW));
                sender.sendMessage(Component.text("  /rpgadmin npc list", ColorUtil.YELLOW));
            }
        }

        return true;
    }

    /**
     * 퀘스트 명령어 처리
     */
    private boolean handleQuestCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("사용법: /rpgadmin quest <give|list|reload> [플레이어] [퀘스트ID]", ColorUtil.ERROR));
            return true;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "give" -> {
                if (args.length < 4) {
                    sender.sendMessage(Component.text("사용법: /rpgadmin quest give <플레이어> <퀘스트ID>", ColorUtil.ERROR));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다.", ColorUtil.ERROR));
                    return true;
                }

                // QuestID 파싱
                QuestID questId;
                try {
                    questId = QuestID.valueOf(args[3].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("올바르지 않은 퀘스트 ID입니다: " + args[3], ColorUtil.ERROR));
                    return true;
                }

                // 퀘스트 시작
                if (questManager.startQuest(target, questId)) {
                    sender.sendMessage(Component.text("퀘스트를 지급했습니다: " + questId.getDisplayName(), ColorUtil.SUCCESS));
                    target.sendMessage(Component.text("새로운 퀘스트를 받았습니다: " + questId.getDisplayName(), ColorUtil.GOLD));
                } else {
                    sender.sendMessage(Component.text("퀘스트를 시작할 수 없습니다. 이미 진행중이거나 조건을 충족하지 않습니다.", ColorUtil.ERROR));
                }
            }

            case "list" -> {
                sender.sendMessage(Component.text("=== 사용 가능한 퀘스트 목록 ===", ColorUtil.GOLD));

                for (QuestCategory category : QuestCategory.values()) {
                    sender.sendMessage(Component.text("\n" + category.name() + ":", ColorUtil.YELLOW));

                    QuestID[] questIds = QuestID.getByCategory(category);
                    for (QuestID id : questIds) {
                        boolean implemented = QuestRegistry.isImplemented(id);
                        Component status = implemented
                                ? Component.text(" ✓", ColorUtil.SUCCESS)
                                : Component.text(" ✗", ColorUtil.ERROR);

                        sender.sendMessage(Component.text("  - " + id.name() + " (" + id.getDisplayName() + ")")
                                .append(status));
                    }
                }
            }

            case "reload" -> {
                // 퀘스트 데이터 리로드 (언어 파일 등)
                langManager.reload();
                sender.sendMessage(Component.text("퀘스트 데이터를 리로드했습니다.", ColorUtil.SUCCESS));
            }

            default -> {
                sender.sendMessage(Component.text("사용법: /rpgadmin quest <give|list|reload>", ColorUtil.ERROR));
            }
        }

        return true;
    }

    /**
     * 섬 관리 명령어 처리
     */
    private boolean handleIslandCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("사용법:", ColorUtil.ERROR));
            sender.sendMessage(Component.text("  /rpgadmin island info <플레이어> - 플레이어의 섬 정보 확인", ColorUtil.YELLOW));
            sender.sendMessage(Component.text("  /rpgadmin island delete <플레이어> - 플레이어의 섬 강제 삭제", ColorUtil.YELLOW));
            sender.sendMessage(Component.text("  /rpgadmin island reset <플레이어> - 플레이어의 섬 강제 초기화", ColorUtil.YELLOW));
            sender.sendMessage(Component.text("  /rpgadmin island tp <플레이어> - 플레이어의 섬으로 이동", ColorUtil.YELLOW));
            return true;
        }

        String subCmd = args[1].toLowerCase();
        if (args.length < 3) {
            sender.sendMessage(Component.text("플레이어 이름을 입력해주세요.", ColorUtil.ERROR));
            return true;
        }

        String targetName = args[2];
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다.", ColorUtil.ERROR));
            return true;
        }

        String targetUuid = target.getUniqueId().toString();

        switch (subCmd) {
            case "info" -> {
                islandManager.getPlayerIsland(targetUuid, targetName).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(Component.text(targetName + "은(는) 섬을 소유하고 있지 않습니다.", ColorUtil.ERROR));
                        return;
                    }

                    sender.sendMessage(Component.text("=== " + targetName + "의 섬 정보 ===", ColorUtil.GOLD));
                    sender.sendMessage(Component.text("섬 ID: " + island.getId(), ColorUtil.WHITE));
                    sender.sendMessage(Component.text("섬 이름: " + island.getName(), ColorUtil.WHITE));
                    sender.sendMessage(Component.text("섬장: " + island.getOwnerName(), ColorUtil.WHITE));
                    sender.sendMessage(Component.text("크기: " + island.getSize() + " x " + island.getSize(), ColorUtil.WHITE));
                    sender.sendMessage(Component.text("멤버 수: " + (island.getData().members().size() + 1) + "/" + (island.getData().upgradeData().memberLimit() + 1), ColorUtil.WHITE));
                    sender.sendMessage(Component.text("생성일: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(island.getData().createdAt())), ColorUtil.GRAY));
                    sender.sendMessage(Component.text("총 초기화 횟수: " + island.getData().totalResets(), ColorUtil.GRAY));
                });
            }
            
            case "delete" -> {
                islandManager.getPlayerIsland(targetUuid, targetName).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(Component.text(targetName + "은(는) 섬을 소유하고 있지 않습니다.", ColorUtil.ERROR));
                        return;
                    }

                    sender.sendMessage(Component.text("섬을 삭제하는 중...", ColorUtil.YELLOW));
                    
                    islandManager.deleteIsland(island.getId()).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(Component.text(targetName + "의 섬이 성공적으로 삭제되었습니다.", ColorUtil.SUCCESS));
                            
                            // 섬장이 온라인인 경우 스폰으로 이동
                            if (target.isOnline()) {
                                plugin.getServer().getScheduler().runTask(plugin, () -> {
                                    if (!plugin.getServer().getWorlds().isEmpty()) {
                                        target.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
                                        target.sendMessage(Component.text("관리자에 의해 섬이 삭제되었습니다.", ColorUtil.ERROR));
                                    }
                                });
                            }
                        } else {
                            sender.sendMessage(Component.text("섬 삭제에 실패했습니다.", ColorUtil.ERROR));
                        }
                    });
                });
            }
            
            case "reset" -> {
                islandManager.getPlayerIsland(targetUuid, targetName).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(Component.text(targetName + "은(는) 섬을 소유하고 있지 않습니다.", ColorUtil.ERROR));
                        return;
                    }

                    sender.sendMessage(Component.text("섬을 초기화하는 중...", ColorUtil.YELLOW));
                    
                    islandManager.resetIsland(island.getId()).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(Component.text(targetName + "의 섬이 성공적으로 초기화되었습니다.", ColorUtil.SUCCESS));
                            
                            // 섬장이 온라인인 경우 알림
                            if (target.isOnline()) {
                                target.sendMessage(Component.text("관리자에 의해 섬이 초기화되었습니다.", ColorUtil.WARNING));
                            }
                        } else {
                            sender.sendMessage(Component.text("섬 초기화에 실패했습니다.", ColorUtil.ERROR));
                        }
                    });
                });
            }
            
            case "tp" -> {
                if (!(sender instanceof Player admin)) {
                    sender.sendMessage(Component.text("이 명령어는 플레이어만 사용할 수 있습니다.", ColorUtil.ERROR));
                    return true;
                }
                
                islandManager.getPlayerIsland(targetUuid, targetName).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(Component.text(targetName + "은(는) 섬을 소유하고 있지 않습니다.", ColorUtil.ERROR));
                        return;
                    }

                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        World islandWorld = islandManager.getWorldManager().getIslandWorld();
                        if (islandWorld == null) {
                            sender.sendMessage(Component.text("섬 월드를 찾을 수 없습니다.", ColorUtil.ERROR));
                            return;
                        }
                        
                        Location tpLocation = island.getSpawnLocation();
                        admin.teleport(tpLocation);
                        admin.sendMessage(Component.text(targetName + "의 섬으로 이동했습니다.", ColorUtil.SUCCESS));
                    });
                });
            }
            
            default -> {
                sender.sendMessage(Component.text("알 수 없는 하위 명령어입니다.", ColorUtil.ERROR));
                return true;
            }
        }

        return true;
    }


    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("rpg.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return Arrays.asList("stats", "reload", "viewprofile", "exp", "level", "job", "npc", "quest", "island")
                    .stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "viewprofile" -> {
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
                case "exp" -> {
                    return List.of("give");
                }
                case "level" -> {
                    return List.of("set");
                }
                case "job" -> {
                    return List.of("set");
                }
                case "npc" -> {
                    return Arrays.asList("set", "setcode", "list");
                }
                case "quest" -> {
                    return Arrays.asList("give", "list", "reload");
                }
                case "island" -> {
                    return Arrays.asList("info", "delete", "reset", "tp");
                }
            }
        }

        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "exp", "level", "job" -> {
                    if (args[1].equalsIgnoreCase("give") || args[1].equalsIgnoreCase("set")) {
                        return Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                                .collect(Collectors.toList());
                    }
                }
                case "npc" -> {
                    if (args[1].equalsIgnoreCase("set")) {
                        return getQuestIdSuggestions(args[2]);
                    }
                }
                case "quest" -> {
                    if (args[1].equalsIgnoreCase("give")) {
                        return Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                                .collect(Collectors.toList());
                    }
                }
                case "island" -> {
                    if (args[1].equalsIgnoreCase("info") || args[1].equalsIgnoreCase("delete") || 
                        args[1].equalsIgnoreCase("reset") || args[1].equalsIgnoreCase("tp")) {
                        return Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                                .collect(Collectors.toList());
                    }
                }
            }
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("quest") && args[1].equalsIgnoreCase("give")) {
                return getQuestIdSuggestions(args[3]);
            }
            // NPC create QUEST 뒤에 퀘스트 ID 제안
            if (args[0].equalsIgnoreCase("npc") && args[1].equalsIgnoreCase("create") 
                    && args[2].equalsIgnoreCase("QUEST")) {
                return getQuestIdSuggestions(args[3]);
            }
        }

        return new ArrayList<>();
    }

    /**
     * 탭 완성 - 퀘스트 ID
     */
    private List<String> getQuestIdSuggestions(String partial) {
        List<String> suggestions = new ArrayList<>();
        String upperPartial = partial.toUpperCase();

        // enum name으로만 검색 (대문자만 사용)
        for (QuestID id : QuestID.values()) {
            if (id.name().startsWith(upperPartial)) {
                suggestions.add(id.name());
            }
        }

        return suggestions;
    }
}