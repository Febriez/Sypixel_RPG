package com.febrie.rpg.dto;

/**
 * 플레이어 스탯 정보 DTO
 * Firebase 저장용
 *
 * @author Febrie, CoffeeTory
 */
public class StatsDTO {

    private int strength = 10;
    private int intelligence = 10;
    private int dexterity = 10;
    private int vitality = 10;
    private int wisdom = 10;
    private int luck = 1;

    public StatsDTO() {
        // 기본 생성자
    }

    // Getters and Setters
    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getVitality() {
        return vitality;
    }

    public void setVitality(int vitality) {
        this.vitality = vitality;
    }

    public int getWisdom() {
        return wisdom;
    }

    public void setWisdom(int wisdom) {
        this.wisdom = wisdom;
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }
}