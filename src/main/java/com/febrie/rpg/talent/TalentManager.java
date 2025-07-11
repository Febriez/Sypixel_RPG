package com.febrie.rpg.talent;

import com.febrie.rpg.job.JobType;
import com.febrie.rpg.stat.Stat;
import com.febrie.rpg.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 특성 시스템 관리자
 * 모든 특성을 로드하고 관리하며, 직업별 특성 트리를 구성
 *
 * @author Febrie, CoffeeTory
 */
public class TalentManager {

    private final Plugin plugin;
    private final Map<String, Talent> allTalents = new HashMap<>();
    private final Map<JobType, List<Talent>> jobTalents = new HashMap<>();
    private final Map<String, List<Talent>> talentPages = new HashMap<>();

    public TalentManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        initializeTalents();
    }

    /**
     * 모든 특성 초기화
     */
    private void initializeTalents() {
        // 공통 특성
        initializeCommonTalents();

        // 전사 계열 특성
        initializeWarriorTalents();

        // 마법사 계열 특성
        initializeMageTalents();

        // 궁수 계열 특성
        initializeArcherTalents();

        plugin.getLogger().info("Initialized " + allTalents.size() + " talents");
    }

    /**
     * 공통 특성 초기화
     */
    private void initializeCommonTalents() {
        // 기본 스탯 특성들
        Talent basicStrength = new Talent.Builder("basic_strength")
                .name("기초 근력", "Basic Strength")
                .icon(Material.IRON_INGOT)
                .color(ColorUtil.COPPER)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 2)
                .build();
        registerTalent(basicStrength, "main");

        Talent basicIntelligence = new Talent.Builder("basic_intelligence")
                .name("기초 지능", "Basic Intelligence")
                .icon(Material.BOOK)
                .color(ColorUtil.INFO)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.INTELLIGENCE, 2)
                .build();
        registerTalent(basicIntelligence, "main");

        Talent basicVitality = new Talent.Builder("basic_vitality")
                .name("기초 체력", "Basic Vitality")
                .icon(Material.APPLE)
                .color(ColorUtil.HEALTH)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.DEFENSE)
                .addStatBonus(Stat.VITALITY, 3)
                .build();
        registerTalent(basicVitality, "main");

        // 고급 스탯 특성 (하위 페이지)
        Talent advancedStrength = new Talent.Builder("advanced_strength")
                .name("고급 근력", "Advanced Strength")
                .icon(Material.DIAMOND)
                .color(ColorUtil.DIAMOND)
                .maxLevel(5)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 5)
                .addStatBonus(Stat.VITALITY, 2)
                .build();

        // 선행 조건 설정
        advancedStrength.addPrerequisite(basicStrength, 5);
        basicStrength.addChild(advancedStrength);
        registerTalent(advancedStrength, "strength_tree");

        // 근력 특화 특성들
        Talent powerStrike = new Talent.Builder("power_strike")
                .name("파워 스트라이크", "Power Strike")
                .icon(Material.IRON_SWORD)
                .color(ColorUtil.ERROR)
                .maxLevel(3)
                .requiredPoints(3)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 10)
                .addEffect("물리 공격력 +15%")
                .build();
        powerStrike.addPrerequisite(advancedStrength, 3);
        advancedStrength.addChild(powerStrike);
        registerTalent(powerStrike, "strength_mastery");
    }

    /**
     * 전사 계열 특성 초기화
     */
    private void initializeWarriorTalents() {
        // 버서커 특성
        Talent berserkRage = new Talent.Builder("berserk_rage")
                .name("광폭한 분노", "Berserk Rage")
                .icon(Material.REDSTONE)
                .color(ColorUtil.ERROR)
                .maxLevel(5)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 4)
                .addEffect("체력 50% 이하일 때 공격력 +20%")
                .build();
        registerJobTalent(berserkRage, JobType.BERSERKER, "main");

        Talent bloodThirst = new Talent.Builder("blood_thirst")
                .name("피의 갈증", "Blood Thirst")
                .icon(Material.REDSTONE_BLOCK)
                .color(ColorUtil.HEALTH)
                .maxLevel(3)
                .requiredPoints(3)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("공격 시 피해량의 15% 회복")
                .build();
        bloodThirst.addPrerequisite(berserkRage, 3);
        berserkRage.addChild(bloodThirst);
        registerJobTalent(bloodThirst, JobType.BERSERKER, "berserker_offense");

        // 버서커 추가 특성들 (세로 스크롤 테스트용)
        Talent rampage = new Talent.Builder("rampage")
                .name("광란", "Rampage")
                .icon(Material.TNT)
                .color(ColorUtil.ERROR)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 3)
                .addStatBonus(Stat.DEXTERITY, 2)
                .addEffect("연속 처치 시 이동속도 +10%")
                .build();
        registerJobTalent(rampage, JobType.BERSERKER, "main");

        Talent battleFrenzy = new Talent.Builder("battle_frenzy")
                .name("전투 광기", "Battle Frenzy")
                .icon(Material.BLAZE_POWDER)
                .color(ColorUtil.ORANGE)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 2)
                .addEffect("전투 중 공격속도 +5%")
                .build();
        registerJobTalent(battleFrenzy, JobType.BERSERKER, "main");

        Talent undyingRage = new Talent.Builder("undying_rage")
                .name("불사의 분노", "Undying Rage")
                .icon(Material.TOTEM_OF_UNDYING)
                .color(ColorUtil.LEGENDARY)
                .maxLevel(1)
                .requiredPoints(5)
                .category(Talent.TalentCategory.SPECIAL)
                .addEffect("체력 1% 이하에서 3초간 무적")
                .build();
        undyingRage.addPrerequisite(berserkRage, 5);
        registerJobTalent(undyingRage, JobType.BERSERKER, "main");

        // 브루저 특성
        Talent balanced = new Talent.Builder("balanced_fighter")
                .name("균형잡힌 전투", "Balanced Fighting")
                .icon(Material.IRON_SWORD)
                .color(ColorUtil.ORANGE)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.STRENGTH, 2)
                .addStatBonus(Stat.VITALITY, 2)
                .addStatBonus(Stat.DEXTERITY, 2)
                .build();
        registerJobTalent(balanced, JobType.BRUISER, "main");

        // 탱커 특성
        Talent fortitude = new Talent.Builder("fortitude")
                .name("불굴의 의지", "Fortitude")
                .icon(Material.SHIELD)
                .color(ColorUtil.NETHERITE)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.DEFENSE)
                .addStatBonus(Stat.VITALITY, 5)
                .addEffect("받는 피해 -5%")
                .build();
        registerJobTalent(fortitude, JobType.TANK, "main");

        Talent shieldMastery = new Talent.Builder("shield_mastery")
                .name("방패 숙련", "Shield Mastery")
                .icon(Material.SHIELD)
                .color(ColorUtil.DIAMOND)
                .maxLevel(5)
                .requiredPoints(2)
                .category(Talent.TalentCategory.DEFENSE)
                .addEffect("방패 방어 확률 +10%")
                .addEffect("방패 방어 시 반격 가능")
                .build();
        shieldMastery.addPrerequisite(fortitude, 5);
        fortitude.addChild(shieldMastery);
        registerJobTalent(shieldMastery, JobType.TANK, "tank_defense");
    }

    /**
     * 마법사 계열 특성 초기화
     */
    private void initializeMageTalents() {
        // 사제 특성
        Talent holyPower = new Talent.Builder("holy_power")
                .name("신성한 힘", "Holy Power")
                .icon(Material.GLOWSTONE_DUST)
                .color(ColorUtil.LEGENDARY)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.INTELLIGENCE, 3)
                .addStatBonus(Stat.WISDOM, 2)
                .addEffect("언데드에게 추가 피해 +25%")
                .build();
        registerJobTalent(holyPower, JobType.PRIEST, "main");

        Talent divineHealing = new Talent.Builder("divine_healing")
                .name("신성한 치유", "Divine Healing")
                .icon(Material.GOLDEN_APPLE)
                .color(ColorUtil.SUCCESS)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.WISDOM, 5)
                .addEffect("치유 효과 +30%")
                .build();
        divineHealing.addPrerequisite(holyPower, 3);
        holyPower.addChild(divineHealing);
        registerJobTalent(divineHealing, JobType.PRIEST, "priest_healing");

        // 흑마법사 특성
        Talent darkMagic = new Talent.Builder("dark_magic")
                .name("어둠의 마법", "Dark Magic")
                .icon(Material.COAL)
                .color(ColorUtil.EPIC)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.INTELLIGENCE, 4)
                .addEffect("어둠 마법 피해 +20%")
                .build();
        registerJobTalent(darkMagic, JobType.DARK_MAGE, "main");

        Talent curse = new Talent.Builder("curse_mastery")
                .name("저주 숙련", "Curse Mastery")
                .icon(Material.WITHER_SKELETON_SKULL)
                .color(ColorUtil.DARK_PURPLE)
                .maxLevel(3)
                .requiredPoints(3)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("저주 지속시간 +50%")
                .addEffect("저주 효과 +25%")
                .build();
        curse.addPrerequisite(darkMagic, 3);
        darkMagic.addChild(curse);
        registerJobTalent(curse, JobType.DARK_MAGE, "dark_curses");

        // 메르시 특성
        Talent angelicBlessing = new Talent.Builder("angelic_blessing")
                .name("천사의 축복", "Angelic Blessing")
                .icon(Material.FEATHER)
                .color(ColorUtil.SUCCESS)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.WISDOM, 4)
                .addStatBonus(Stat.INTELLIGENCE, 2)
                .addEffect("아군 부활 시 체력 50% 회복")
                .build();
        registerJobTalent(angelicBlessing, JobType.MERCY, "main");
    }

    /**
     * 궁수 계열 특성 초기화
     */
    private void initializeArcherTalents() {
        // 아처 특성
        Talent eagleEye = new Talent.Builder("eagle_eye")
                .name("매의 눈", "Eagle Eye")
                .icon(Material.ENDER_EYE)
                .color(ColorUtil.EMERALD)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.DEXTERITY, 3)
                .addEffect("명중률 +15%")
                .build();
        registerJobTalent(eagleEye, JobType.ARCHER, "main");

        Talent rapidShot = new Talent.Builder("rapid_shot")
                .name("속사", "Rapid Shot")
                .icon(Material.ARROW)
                .color(ColorUtil.SUCCESS)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.DEXTERITY, 5)
                .addEffect("공격 속도 +25%")
                .build();
        rapidShot.addPrerequisite(eagleEye, 3);
        eagleEye.addChild(rapidShot);
        registerJobTalent(rapidShot, JobType.ARCHER, "archer_offense");

        // 스나이퍼 특성
        Talent precision = new Talent.Builder("precision")
                .name("정밀 조준", "Precision")
                .icon(Material.SPYGLASS)
                .color(ColorUtil.INFO)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.DEXTERITY, 2)
                .addStatBonus(Stat.LUCK, 3)
                .addEffect("치명타 확률 +20%")
                .build();
        registerJobTalent(precision, JobType.SNIPER, "main");

        Talent assassination = new Talent.Builder("assassination")
                .name("암살", "Assassination")
                .icon(Material.IRON_SWORD)
                .color(ColorUtil.ERROR)
                .maxLevel(1)
                .requiredPoints(5)
                .category(Talent.TalentCategory.SPECIAL)
                .addEffect("은신 상태에서 첫 공격 시 300% 피해")
                .build();
        assassination.addPrerequisite(precision, 5);
        precision.addChild(assassination);
        registerJobTalent(assassination, JobType.SNIPER, "sniper_special");

        // 샷건맨 특성
        Talent scattershot = new Talent.Builder("scattershot")
                .name("산탄", "Scattershot")
                .icon(Material.FIRE_CHARGE)
                .color(ColorUtil.WARNING)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 2)
                .addStatBonus(Stat.DEXTERITY, 2)
                .addEffect("근거리 피해 +30%")
                .build();
        registerJobTalent(scattershot, JobType.SHOTGUNNER, "main");
    }

    /**
     * 특성 등록
     */
    private void registerTalent(@NotNull Talent talent, @NotNull String pageId) {
        allTalents.put(talent.getId(), talent);
        talentPages.computeIfAbsent(pageId, k -> new ArrayList<>()).add(talent);
    }

    /**
     * 직업별 특성 등록
     */
    private void registerJobTalent(@NotNull Talent talent, @NotNull JobType job, @NotNull String pageId) {
        registerTalent(talent, pageId);
        jobTalents.computeIfAbsent(job, k -> new ArrayList<>()).add(talent);
    }

    /**
     * 특성 ID로 가져오기
     */
    @Nullable
    public Talent getTalent(@NotNull String id) {
        return allTalents.get(id);
    }

    /**
     * 페이지의 특성 목록 가져오기
     */
    @NotNull
    public List<Talent> getPageTalents(@NotNull String pageId) {
        return talentPages.getOrDefault(pageId, new ArrayList<>());
    }

    /**
     * 직업의 메인 특성 목록 가져오기 - 해당 직업 특성만!
     */
    @NotNull
    public List<Talent> getJobMainTalents(@NotNull JobType job) {
        List<Talent> mainTalents = new ArrayList<>();

        // 공통 특성 (기본 스탯 특성만)
        Talent basicStr = getTalent("basic_strength");
        Talent basicInt = getTalent("basic_intelligence");
        Talent basicVit = getTalent("basic_vitality");

        if (basicStr != null) mainTalents.add(basicStr);
        if (basicInt != null) mainTalents.add(basicInt);
        if (basicVit != null) mainTalents.add(basicVit);

        // 직업별 특성 - 해당 직업의 것만!
        List<Talent> jobSpecific = jobTalents.get(job);
        if (jobSpecific != null) {
            for (Talent talent : jobSpecific) {
                // 최상위 특성만 (parent가 없는 것)
                // 그리고 "main" 페이지의 특성만
                if (talent.getParent() == null &&
                        getPageTalents("main").contains(talent)) {
                    mainTalents.add(talent);
                }
            }
        }

        return mainTalents;
    }

    /**
     * 특정 특성의 하위 페이지 특성 목록 가져오기
     */
    @NotNull
    public List<Talent> getSubPageTalents(@NotNull String pageId, @NotNull JobType job) {
        List<Talent> pageTalents = getPageTalents(pageId);

        // 직업에 맞는 특성만 필터링
        if (job != null) {
            pageTalents.removeIf(talent -> {
                List<Talent> jobTalentList = jobTalents.get(job);
                return jobTalentList == null || !jobTalentList.contains(talent);
            });
        }

        return pageTalents;
    }

    /**
     * 모든 특성 가져오기
     */
    @NotNull
    public Collection<Talent> getAllTalents() {
        return allTalents.values();
    }
}