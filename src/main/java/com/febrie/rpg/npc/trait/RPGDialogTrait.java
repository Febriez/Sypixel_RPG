package com.febrie.rpg.npc.trait;

import com.febrie.rpg.util.UnifiedColorUtil;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * NPC 대화 Trait
 * NPC가 랜덤하게 대사를 출력하도록 하는 기능
 *
 * @author Febrie
 */
@TraitName("rpgdialog")
public class RPGDialogTrait extends Trait {

    @Persist("dialogId")
    private String dialogId = null;
    
    @Persist("lastDialogTime")
    private long lastDialogTime = 0;
    
    @Persist("dialogCooldown")
    private long dialogCooldown = 3000; // 3초 기본 쿨다운
    
    private final Random random = new Random();

    public RPGDialogTrait() {
        super("rpgdialog");
    }

    /**
     * 대화 ID 설정
     */
    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }
    
    /**
     * 대화 ID 조회
     */
    public String getDialogId() {
        return dialogId;
    }
    
    /**
     * 대화 ID가 설정되어 있는지 확인
     */
    public boolean hasDialogId() {
        return dialogId != null && !dialogId.isEmpty();
    }
    
    /**
     * 쿨다운 설정 (밀리초)
     */
    public void setDialogCooldown(long cooldown) {
        this.dialogCooldown = cooldown;
    }
    
    /**
     * 쿨다운 조회
     */
    public long getDialogCooldown() {
        return dialogCooldown;
    }

    /**
     * 플레이어가 NPC와 상호작용할 때 호출
     */
    public void onInteract(Player player) {
        // 대화 ID가 없으면 무시
        if (!hasDialogId()) {
            return;
        }
        
        // 쿨다운 체크
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDialogTime < dialogCooldown) {
            return;
        }
        
        // lang 파일에서 대화 리스트 가져오기
        List<String> dialogLines = new ArrayList<>();
        
        // 임시로 dialogId에 따른 기본 대화 제공
        switch (dialogId) {
            case "village_greeting" -> {
                dialogLines.add("안녕하세요! 좋은 날씨네요.");
                dialogLines.add("이 마을에 오신 것을 환영합니다!");
                dialogLines.add("오늘도 평화로운 하루네요.");
            }
            case "merchant_greeting" -> {
                dialogLines.add("어서오세요! 좋은 물건이 많이 있습니다.");
                dialogLines.add("오늘의 특별 상품을 확인해보세요!");
                dialogLines.add("싸게 드릴게요, 한 번 구경해보세요.");
            }
            default -> dialogLines.add("안녕하세요!");
        }
        
        if (dialogLines.isEmpty()) {
            // 대화가 정의되지 않았으면 기본 메시지
            player.sendMessage(Component.text("[" + npc.getName() + "] ", UnifiedColorUtil.GOLD)
                    .append(Component.text("...", UnifiedColorUtil.GRAY)));
            return;
        }
        
        // 랜덤 대사 선택
        String dialogue = dialogLines.get(random.nextInt(dialogLines.size()));
        
        // 대사 출력
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("[" + npc.getName() + "] ", UnifiedColorUtil.GOLD)
                .append(Component.text(dialogue, UnifiedColorUtil.COMMON)));
        
        // 마지막 대화 시간 업데이트
        lastDialogTime = currentTime;
    }

    /**
     * Trait가 NPC에 추가될 때 호출
     */
    @Override
    public void onAttach() {
        super.onAttach();
        // NPC가 생성될 때 기본 설정
        if (npc != null) {
            npc.setProtected(true);
        }
    }

}