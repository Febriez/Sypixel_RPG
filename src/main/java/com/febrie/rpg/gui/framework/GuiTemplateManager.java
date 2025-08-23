package com.febrie.rpg.gui.framework;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * GUI 템플릿 관리자 - 로케일별 GUI 템플릿을 캐싱하고 관리
 * 
 * @author Febrie
 */
public class GuiTemplateManager {
    
    private static GuiTemplateManager instance;
    
    private final JavaPlugin plugin;
    private final Logger logger;
    
    // guiId -> locale -> template
    private final Map<String, Map<String, GuiTemplate>> templateCache;
    
    // 템플릿 최대 수명 (5분)
    private static final long TEMPLATE_MAX_AGE = 5 * 60 * 1000;
    
    // 정리 작업 간격 (1분)
    private static final long CLEANUP_INTERVAL = 60 * 20; // ticks
    
    /**
     * 생성자
     * 
     * @param plugin 플러그인
     */
    private GuiTemplateManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.templateCache = new ConcurrentHashMap<>();
        
        startCleanupTask();
    }
    
    /**
     * 싱글톤 인스턴스 초기화
     * 
     * @param plugin 플러그인
     */
    public static void initialize(@NotNull JavaPlugin plugin) {
        if (instance == null) {
            instance = new GuiTemplateManager(plugin);
        }
    }
    
    /**
     * 싱글톤 인스턴스 가져오기
     * 
     * @return 템플릿 매니저
     */
    @NotNull
    public static GuiTemplateManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GuiTemplateManager not initialized!");
        }
        return instance;
    }
    
    /**
     * 템플릿 가져오기 또는 생성
     * 
     * @param guiId GUI 식별자
     * @param locale 로케일
     * @param creator 템플릿 생성자 (없을 경우 생성)
     * @return GUI 템플릿
     */
    @NotNull
    public GuiTemplate getOrCreateTemplate(@NotNull String guiId, @NotNull String locale, 
                                          @NotNull TemplateCreator creator) {
        Map<String, GuiTemplate> localeTemplates = templateCache.computeIfAbsent(guiId, 
            k -> new ConcurrentHashMap<>());
        
        GuiTemplate template = localeTemplates.get(locale);
        
        // 템플릿이 없거나 만료된 경우
        if (template == null || template.isExpired(TEMPLATE_MAX_AGE)) {
            if (template != null && template.isExpired(TEMPLATE_MAX_AGE)) {
                logger.fine("Template expired for " + guiId + " (" + locale + "), recreating...");
            }
            
            template = creator.create(guiId, locale);
            localeTemplates.put(locale, template);
            logger.fine("Created template for " + guiId + " (" + locale + ")");
        }
        
        return template;
    }
    
    /**
     * 템플릿 가져오기
     * 
     * @param guiId GUI 식별자
     * @param locale 로케일
     * @return GUI 템플릿 또는 null
     */
    @Nullable
    public GuiTemplate getTemplate(@NotNull String guiId, @NotNull String locale) {
        Map<String, GuiTemplate> localeTemplates = templateCache.get(guiId);
        if (localeTemplates == null) {
            return null;
        }
        
        GuiTemplate template = localeTemplates.get(locale);
        if (template != null && template.isExpired(TEMPLATE_MAX_AGE)) {
            localeTemplates.remove(locale);
            return null;
        }
        
        return template;
    }
    
    /**
     * 템플릿 저장
     * 
     * @param template GUI 템플릿
     */
    public void saveTemplate(@NotNull GuiTemplate template) {
        Map<String, GuiTemplate> localeTemplates = templateCache.computeIfAbsent(
            template.getGuiId(), k -> new ConcurrentHashMap<>());
        localeTemplates.put(template.getLocale(), template);
    }
    
    /**
     * 특정 GUI의 모든 템플릿 무효화
     * 
     * @param guiId GUI 식별자
     */
    public void invalidateGui(@NotNull String guiId) {
        templateCache.remove(guiId);
        logger.fine("Invalidated all templates for " + guiId);
    }
    
    /**
     * 특정 템플릿 무효화
     * 
     * @param guiId GUI 식별자
     * @param locale 로케일
     */
    public void invalidateTemplate(@NotNull String guiId, @NotNull String locale) {
        Map<String, GuiTemplate> localeTemplates = templateCache.get(guiId);
        if (localeTemplates != null) {
            localeTemplates.remove(locale);
            logger.fine("Invalidated template for " + guiId + " (" + locale + ")");
        }
    }
    
    /**
     * 모든 템플릿 무효화
     */
    public void invalidateAll() {
        int count = templateCache.size();
        templateCache.clear();
        logger.info("Invalidated " + count + " GUI templates");
    }
    
    /**
     * 만료된 템플릿 정리
     */
    private void cleanupExpiredTemplates() {
        int removed = 0;
        
        for (Map.Entry<String, Map<String, GuiTemplate>> guiEntry : templateCache.entrySet()) {
            Map<String, GuiTemplate> localeTemplates = guiEntry.getValue();
            
            localeTemplates.entrySet().removeIf(entry -> {
                boolean expired = entry.getValue().isExpired(TEMPLATE_MAX_AGE);
                if (expired) {
                    logger.fine("Removing expired template: " + guiEntry.getKey() + 
                               " (" + entry.getKey() + ")");
                }
                return expired;
            });
            
            // 빈 맵 제거
            if (localeTemplates.isEmpty()) {
                templateCache.remove(guiEntry.getKey());
                removed++;
            }
        }
        
        if (removed > 0) {
            logger.fine("Cleaned up " + removed + " expired GUI template groups");
        }
    }
    
    /**
     * 정리 작업 시작
     */
    private void startCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                cleanupExpiredTemplates();
            }
        }.runTaskTimerAsynchronously(plugin, CLEANUP_INTERVAL, CLEANUP_INTERVAL);
    }
    
    /**
     * 캐시 통계 가져오기
     * 
     * @return 통계 정보
     */
    @NotNull
    public CacheStats getStats() {
        int totalTemplates = 0;
        for (Map<String, GuiTemplate> localeTemplates : templateCache.values()) {
            totalTemplates += localeTemplates.size();
        }
        
        return new CacheStats(templateCache.size(), totalTemplates);
    }
    
    /**
     * 템플릿 생성자 인터페이스
     */
    @FunctionalInterface
    public interface TemplateCreator {
        /**
         * 템플릿 생성
         * 
         * @param guiId GUI 식별자
         * @param locale 로케일
         * @return 생성된 템플릿
         */
        @NotNull
        GuiTemplate create(@NotNull String guiId, @NotNull String locale);
    }
    
    /**
     * 캐시 통계
     */
    public record CacheStats(int guiCount, int totalTemplates) {
        @Override
        public String toString() {
            return String.format("GUIs: %d, Templates: %d", guiCount, totalTemplates);
        }
    }
}