package com.febrie.rpg.gui.framework;

import com.febrie.rpg.gui.component.GuiItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * GUI 템플릿 - 정적 아이템들을 캐싱
 * 
 * @author Febrie
 */
public class GuiTemplate {
    
    private final String guiId;
    private final String locale;
    private final Map<Integer, GuiItem> staticItems;
    private final Set<Integer> dynamicSlots;
    private final long createdAt;
    
    /**
     * GUI 템플릿 생성
     * 
     * @param guiId GUI 식별자
     * @param locale 로케일
     */
    public GuiTemplate(@NotNull String guiId, @NotNull String locale) {
        this.guiId = guiId;
        this.locale = locale;
        this.staticItems = new HashMap<>();
        this.dynamicSlots = new HashSet<>();
        this.createdAt = System.currentTimeMillis();
    }
    
    /**
     * 정적 아이템 추가
     * 
     * @param slot 슬롯 번호
     * @param item GUI 아이템
     */
    public void addStaticItem(int slot, @NotNull GuiItem item) {
        staticItems.put(slot, item);
    }
    
    /**
     * 동적 슬롯 등록
     * 
     * @param slot 슬롯 번호
     */
    public void addDynamicSlot(int slot) {
        dynamicSlots.add(slot);
    }
    
    /**
     * 여러 동적 슬롯 등록
     * 
     * @param slots 슬롯 번호들
     */
    public void addDynamicSlots(int... slots) {
        for (int slot : slots) {
            dynamicSlots.add(slot);
        }
    }
    
    /**
     * 정적 아이템 가져오기
     * 
     * @param slot 슬롯 번호
     * @return GUI 아이템 또는 null
     */
    @Nullable
    public GuiItem getStaticItem(int slot) {
        return staticItems.get(slot);
    }
    
    /**
     * 모든 정적 아이템 가져오기
     * 
     * @return 정적 아이템 맵
     */
    @NotNull
    public Map<Integer, GuiItem> getStaticItems() {
        return new HashMap<>(staticItems);
    }
    
    /**
     * 동적 슬롯인지 확인
     * 
     * @param slot 슬롯 번호
     * @return 동적 슬롯 여부
     */
    public boolean isDynamicSlot(int slot) {
        return dynamicSlots.contains(slot);
    }
    
    /**
     * 모든 동적 슬롯 가져오기
     * 
     * @return 동적 슬롯 집합
     */
    @NotNull
    public Set<Integer> getDynamicSlots() {
        return new HashSet<>(dynamicSlots);
    }
    
    /**
     * 템플릿이 만료되었는지 확인
     * 
     * @param maxAge 최대 수명 (밀리초)
     * @return 만료 여부
     */
    public boolean isExpired(long maxAge) {
        return System.currentTimeMillis() - createdAt > maxAge;
    }
    
    /**
     * GUI ID 가져오기
     * 
     * @return GUI ID
     */
    @NotNull
    public String getGuiId() {
        return guiId;
    }
    
    /**
     * 로케일 가져오기
     * 
     * @return 로케일
     */
    @NotNull
    public String getLocale() {
        return locale;
    }
    
    /**
     * 생성 시간 가져오기
     * 
     * @return 생성 시간
     */
    public long getCreatedAt() {
        return createdAt;
    }
    
    /**
     * 템플릿 복사
     * 
     * @return 복사된 템플릿
     */
    @NotNull
    public GuiTemplate copy() {
        GuiTemplate copy = new GuiTemplate(guiId, locale);
        copy.staticItems.putAll(this.staticItems);
        copy.dynamicSlots.addAll(this.dynamicSlots);
        return copy;
    }
}