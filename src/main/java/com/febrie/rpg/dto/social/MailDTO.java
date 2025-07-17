package com.febrie.rpg.dto.social;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 우편 데이터 전송 객체
 *
 * @author Febrie
 */
public class MailDTO {
    
    private String id; // Firestore 문서 ID
    private UUID fromPlayerId;
    private String fromPlayerName;
    private UUID toPlayerId;
    private String toPlayerName;
    private String subject;
    private String message;
    private LocalDateTime sentTime;
    private boolean isRead;
    private boolean hasAttachments;
    private List<SerializedItemStack> attachments;
    private boolean isCollected; // 첨부물을 수령했는지 여부
    
    public MailDTO() {
        // Firebase 역직렬화를 위한 기본 생성자
        this.attachments = new ArrayList<>();
    }
    
    public MailDTO(@NotNull UUID fromPlayerId, @NotNull String fromPlayerName,
                   @NotNull UUID toPlayerId, @NotNull String toPlayerName,
                   @NotNull String subject, @Nullable String message) {
        this.fromPlayerId = fromPlayerId;
        this.fromPlayerName = fromPlayerName;
        this.toPlayerId = toPlayerId;
        this.toPlayerName = toPlayerName;
        this.subject = subject;
        this.message = message;
        this.sentTime = LocalDateTime.now();
        this.isRead = false;
        this.hasAttachments = false;
        this.attachments = new ArrayList<>();
        this.isCollected = false;
    }
    
    /**
     * 첨부물 추가
     */
    public void addAttachment(@NotNull ItemStack item) {
        if (attachments.size() >= 9) { // 최대 9개까지
            throw new IllegalStateException("첨부물은 최대 9개까지만 가능합니다.");
        }
        
        SerializedItemStack serialized = SerializedItemStack.fromItemStack(item);
        attachments.add(serialized);
        hasAttachments = true;
    }
    
    /**
     * 모든 첨부물을 ItemStack으로 변환
     */
    @NotNull
    public List<ItemStack> getAttachmentsAsItemStacks() {
        List<ItemStack> items = new ArrayList<>();
        for (SerializedItemStack serialized : attachments) {
            ItemStack item = serialized.toItemStack();
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }
    
    /**
     * 직렬화된 ItemStack 내부 클래스
     */
    public static class SerializedItemStack {
        private String material;
        private int amount;
        private Map<String, Object> meta; // ItemMeta 정보
        
        public SerializedItemStack() {}
        
        public SerializedItemStack(@NotNull String material, int amount, @Nullable Map<String, Object> meta) {
            this.material = material;
            this.amount = amount;
            this.meta = meta;
        }
        
        @NotNull
        public static SerializedItemStack fromItemStack(@NotNull ItemStack item) {
            String material = item.getType().name();
            int amount = item.getAmount();
            
            // 간단한 직렬화 (나중에 더 정교하게 구현 가능)
            Map<String, Object> meta = null;
            if (item.hasItemMeta()) {
                // TODO: ItemMeta 직렬화 구현
                // 현재는 기본 아이템만 지원
            }
            
            return new SerializedItemStack(material, amount, meta);
        }
        
        @Nullable
        public ItemStack toItemStack() {
            try {
                Material mat = Material.valueOf(material);
                ItemStack item = new ItemStack(mat, amount);
                
                // TODO: ItemMeta 역직렬화 구현
                
                return item;
            } catch (IllegalArgumentException e) {
                return null; // 잘못된 Material
            }
        }
        
        // Getters and Setters
        public String getMaterial() {
            return material;
        }
        
        public void setMaterial(String material) {
            this.material = material;
        }
        
        public int getAmount() {
            return amount;
        }
        
        public void setAmount(int amount) {
            this.amount = amount;
        }
        
        public Map<String, Object> getMeta() {
            return meta;
        }
        
        public void setMeta(Map<String, Object> meta) {
            this.meta = meta;
        }
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public UUID getFromPlayerId() {
        return fromPlayerId;
    }
    
    public void setFromPlayerId(UUID fromPlayerId) {
        this.fromPlayerId = fromPlayerId;
    }
    
    public String getFromPlayerName() {
        return fromPlayerName;
    }
    
    public void setFromPlayerName(String fromPlayerName) {
        this.fromPlayerName = fromPlayerName;
    }
    
    public UUID getToPlayerId() {
        return toPlayerId;
    }
    
    public void setToPlayerId(UUID toPlayerId) {
        this.toPlayerId = toPlayerId;
    }
    
    public String getToPlayerName() {
        return toPlayerName;
    }
    
    public void setToPlayerName(String toPlayerName) {
        this.toPlayerName = toPlayerName;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getSentTime() {
        return sentTime;
    }
    
    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    public boolean hasAttachments() {
        return hasAttachments;
    }
    
    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }
    
    public List<SerializedItemStack> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<SerializedItemStack> attachments) {
        this.attachments = attachments;
        this.hasAttachments = attachments != null && !attachments.isEmpty();
    }
    
    public boolean isCollected() {
        return isCollected;
    }
    
    public void setCollected(boolean collected) {
        isCollected = collected;
    }
    
    @Override
    public String toString() {
        return String.format("Mail{from=%s, to=%s, subject='%s', time=%s, attachments=%d}", 
                fromPlayerName, toPlayerName, subject, sentTime, attachments.size());
    }
}