package com.febrie.rpg.command.admin.subcommand;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.SubCommand;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 언어 시스템 테스트 커맨드
 * 번역 키를 테스트하고 디버깅하는 기능 제공
 *
 * @author Febrie, CoffeeTory
 */
public class LangTestCommand implements SubCommand {
    
    public LangTestCommand(@NotNull RPGMain plugin) {
        // Plugin not needed - removed unused field
    }
    
    @Override
    @NotNull
    public String getName() {
        return "lang";
    }
    
    @Override
    @NotNull
    public String getDescription() {
        return "언어 시스템을 테스트하고 디버깅합니다";
    }
    
    @Override
    @NotNull
    public String getUsage() {
        return "/rpgadmin lang <test|list|check> [key|section]";
    }
    
    @Override
    @NotNull
    public String getPermission() {
        return "rpg.admin.lang";
    }
    
    @Override
    @NotNull
    public List<String> getAliases() {
        return List.of("language", "translation");
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
            case "test" -> {
                if (args.length < 2) {
                    sender.sendMessage(UnifiedColorUtil.parse("&c사용법: /rpgadmin lang test <key>"));
                    return false;
                }
                return testTranslation(sender, args[1]);
            }
            case "list" -> {
                String section = args.length >= 2 ? args[1] : null;
                return listKeys(sender, section);
            }
            case "check" -> {
                if (args.length < 2) {
                    sender.sendMessage(UnifiedColorUtil.parse("&c사용법: /rpgadmin lang check <key>"));
                    return false;
                }
                return checkKey(sender, args[1]);
            }
            case "debug" -> {
                return toggleDebug(sender);
            }
            default -> {
                sender.sendMessage(UnifiedColorUtil.parse("&c알 수 없는 하위 명령: " + subCommand));
                return false;
            }
        }
    }
    
    /**
     * 번역 키 테스트
     */
    private boolean testTranslation(@NotNull CommandSender sender, @NotNull String key) {
        sender.sendMessage(UnifiedColorUtil.parse("&e=== 번역 테스트: " + key + " ==="));
        
        // Component 생성
        Component component = Component.translatable(key);
        
        // 플레이어인 경우 해당 로케일로 테스트
        if (sender instanceof Player player) {
            Locale playerLocale = player.locale();
            
            // GlobalTranslator를 통한 렌더링
            Component rendered = GlobalTranslator.renderer().render(component, playerLocale);
            String plainText = PlainTextComponentSerializer.plainText().serialize(rendered);
            
            sender.sendMessage(UnifiedColorUtil.parse("&7플레이어 로케일 (&f" + playerLocale + "&7): &a" + plainText));
            
            // 직접 Component 전송 테스트
            sender.sendMessage(UnifiedColorUtil.parse("&7직접 전송 테스트:"));
            sender.sendMessage(component);
        }
        
        // 한국어로 렌더링
        Component koreanRendered = GlobalTranslator.renderer().render(component, Locale.KOREAN);
        String koreanText = PlainTextComponentSerializer.plainText().serialize(koreanRendered);
        sender.sendMessage(UnifiedColorUtil.parse("&7한국어: &a" + koreanText));
        
        // 영어로 렌더링
        Component englishRendered = GlobalTranslator.renderer().render(component, Locale.ENGLISH);
        String englishText = PlainTextComponentSerializer.plainText().serialize(englishRendered);
        sender.sendMessage(UnifiedColorUtil.parse("&7영어: &a" + englishText));
        
        // 원본 Component (번역되지 않은 경우 키가 그대로 나옴)
        String rawText = PlainTextComponentSerializer.plainText().serialize(component);
        if (rawText.equals(key)) {
            sender.sendMessage(UnifiedColorUtil.parse("&c⚠ 번역되지 않음 (키가 그대로 표시됨)"));
        }
        
        // LangManager에서 직접 확인
        boolean existsInKorean = LangManager.hasKey(key, Locale.KOREAN);
        boolean existsInEnglish = LangManager.hasKey(key, Locale.ENGLISH);
        
        sender.sendMessage(UnifiedColorUtil.parse("&7등록 상태: 한국어[" + 
            (existsInKorean ? "&a✓" : "&c✗") + "&7] 영어[" + 
            (existsInEnglish ? "&a✓" : "&c✗") + "&7]"));
        
        // LangManager를 통한 직접 번역 테스트
        sender.sendMessage(UnifiedColorUtil.parse("&7=== LangManager 직접 번역 테스트 ==="));
        Component directKorean = LangManager.getComponent(key, Locale.KOREAN);
        Component directEnglish = LangManager.getComponent(key, Locale.ENGLISH);
        
        sender.sendMessage(UnifiedColorUtil.parse("&7LangManager 한국어: "));
        sender.sendMessage(directKorean);
        sender.sendMessage(UnifiedColorUtil.parse("&7LangManager 영어: "));
        sender.sendMessage(directEnglish);
        
        return true;
    }
    
    /**
     * 등록된 키 목록 표시
     */
    private boolean listKeys(@NotNull CommandSender sender, String section) {
        Set<String> keys = LangManager.getAllKeys(Locale.KOREAN);
        
        if (section != null) {
            // 특정 섹션의 키만 필터링
            keys = keys.stream()
                .filter(key -> key.startsWith(section + "."))
                .collect(Collectors.toSet());
            sender.sendMessage(UnifiedColorUtil.parse("&e=== " + section + " 섹션 키 목록 ==="));
        } else {
            sender.sendMessage(UnifiedColorUtil.parse("&e=== 전체 키 목록 (총 " + keys.size() + "개) ==="));
        }
        
        if (keys.isEmpty()) {
            sender.sendMessage(UnifiedColorUtil.parse("&c등록된 키가 없습니다."));
            return true;
        }
        
        // 키를 정렬하여 표시 (최대 20개만)
        keys.stream()
            .sorted()
            .limit(20)
            .forEach(key -> sender.sendMessage(UnifiedColorUtil.parse("&7- &f" + key)));
        
        if (keys.size() > 20) {
            sender.sendMessage(UnifiedColorUtil.parse("&7... 외 " + (keys.size() - 20) + "개"));
        }
        
        return true;
    }
    
    /**
     * 특정 키 존재 여부 확인
     */
    private boolean checkKey(@NotNull CommandSender sender, @NotNull String key) {
        sender.sendMessage(UnifiedColorUtil.parse("&e=== 키 확인: " + key + " ==="));
        
        boolean koreanExists = LangManager.hasKey(key, Locale.KOREAN);
        boolean englishExists = LangManager.hasKey(key, Locale.ENGLISH);
        
        sender.sendMessage(UnifiedColorUtil.parse("&7한국어: " + (koreanExists ? "&a존재함" : "&c없음")));
        sender.sendMessage(UnifiedColorUtil.parse("&7영어: " + (englishExists ? "&a존재함" : "&c없음")));
        
        if (!koreanExists && !englishExists) {
            // 비슷한 키 제안
            Set<String> allKeys = LangManager.getAllKeys(Locale.ENGLISH);
            String lowerKey = key.toLowerCase();
            
            List<String> suggestions = allKeys.stream()
                .filter(k -> k.toLowerCase().contains(lowerKey.substring(Math.max(0, lowerKey.lastIndexOf('.')))))
                .limit(5)
                .toList();
            
            if (!suggestions.isEmpty()) {
                sender.sendMessage(UnifiedColorUtil.parse("&7비슷한 키:"));
                suggestions.forEach(s -> sender.sendMessage(UnifiedColorUtil.parse("&7- &e" + s)));
            }
        }
        
        return true;
    }
    
    /**
     * 디버그 모드 토글
     */
    private boolean toggleDebug(@NotNull CommandSender sender) {
        boolean debugMode = LangManager.toggleDebugMode();
        sender.sendMessage(UnifiedColorUtil.parse("&7언어 시스템 디버그 모드: " + 
            (debugMode ? "&a활성화" : "&c비활성화")));
        return true;
    }
    
    @Override
    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("test", "list", "check", "debug")
                .filter(s -> s.startsWith(args[0].toLowerCase()))
                .toList();
        }
        
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("list")) {
                // 섹션 목록
                return Stream.of("general", "gui", "items", "job", "talent", "stat", 
                              "messages", "commands", "status", "currency", "quest", "island")
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .toList();
            }
            
            if (subCommand.equals("test") || subCommand.equals("check")) {
                // 일부 공통 키 제안
                String partial = args[1].toLowerCase();
                return LangManager.getAllKeys(Locale.ENGLISH).stream()
                    .filter(key -> key.toLowerCase().contains(partial))
                    .limit(10)
                    .toList();
            }
        }
        
        return List.of();
    }
}