package com.febrie.rpg.npc.trait;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.Player;

/**
 * RPG 가이드 NPC를 위한 커스텀 Trait
 * Citizens의 Trait 시스템을 사용해 NPC 데이터를 영구 저장
 *
 * @author Febrie
 */
@TraitName("rpgguide")
public class RPGGuideTrait extends Trait {

    @Persist("guideType")
    private String guideType = "GENERAL";

    @Persist("npcType")
    private String npcType = "GUIDE";

    @Persist("helpText")
    private String helpText = "안녕하세요! 도움이 필요하시면 언제든 말씀하세요.";

    @Persist("infoText")
    private String infoText = "이곳은 모험가들을 위한 안내소입니다.";

    @Persist("tipText")
    private String tipText = "팁: F 키를 눌러 메인 메뉴를 열 수 있습니다!";

    public RPGGuideTrait() {
        super("rpgguide");
    }

    /**
     * 가이드 타입 설정
     */
    public void setGuideType(String guideType) {
        this.guideType = guideType;
    }

    /**
     * 가이드 타입 조회
     */
    public String getGuideType() {
        return guideType;
    }

    /**
     * NPC 타입 설정
     */
    public void setNpcType(String npcType) {
        this.npcType = npcType;
    }

    /**
     * NPC 타입 조회
     */
    public String getNpcType() {
        return npcType;
    }

    /**
     * 도움말 텍스트 설정
     */
    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    /**
     * 도움말 텍스트 조회
     */
    public String getHelpText() {
        return helpText;
    }

    /**
     * 정보 텍스트 설정
     */
    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }

    /**
     * 정보 텍스트 조회
     */
    public String getInfoText() {
        return infoText;
    }

    /**
     * 팁 텍스트 설정
     */
    public void setTipText(String tipText) {
        this.tipText = tipText;
    }

    /**
     * 팁 텍스트 조회
     */
    public String getTipText() {
        return tipText;
    }


    /**
     * Trait가 NPC에 추가될 때 호출
     */
    @Override
    public void onAttach() {
        super.onAttach();
        if (npc != null) {
            npc.setProtected(true);
        }
    }

}