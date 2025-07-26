package com.febrie.rpg.npc.trait;

import com.febrie.rpg.util.ColorUtil;
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

    @Persist("dialogues")
    private List<String> dialogues = new ArrayList<>();
    
    @Persist("lastDialogTime")
    private long lastDialogTime = 0;
    
    @Persist("dialogCooldown")
    private long dialogCooldown = 3000; // 3초 기본 쿨다운
    
    private final Random random = new Random();

    public RPGDialogTrait() {
        super("rpgdialog");
    }

    /**
     * 대사 추가
     */
    public void addDialogue(String dialogue) {
        if (!dialogues.contains(dialogue)) {
            dialogues.add(dialogue);
        }
    }
    
    /**
     * 대사 제거
     */
    public void removeDialogue(int index) {
        if (index >= 0 && index < dialogues.size()) {
            dialogues.remove(index);
        }
    }
    
    /**
     * 모든 대사 조회
     */
    public List<String> getDialogues() {
        return new ArrayList<>(dialogues);
    }
    
    /**
     * 대사 초기화
     */
    public void clearDialogues() {
        dialogues.clear();
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
        // 대사가 없으면 무시
        if (dialogues.isEmpty()) {
            return;
        }
        
        // 쿨다운 체크
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDialogTime < dialogCooldown) {
            return;
        }
        
        // 랜덤 대사 선택
        String dialogue = dialogues.get(random.nextInt(dialogues.size()));
        
        // 대사 출력
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("[" + npc.getName() + "] ", ColorUtil.GOLD)
                .append(Component.text(dialogue, ColorUtil.COMMON)));
        
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