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
                .icon(Material.IRON_INGOT)
                .color(ColorUtil.COPPER)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 2)
                .build();
        registerTalent(basicStrength, "main");

        Talent basicIntelligence = new Talent.Builder("basic_intelligence")
                .icon(Material.BOOK)
                .color(ColorUtil.INFO)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.INTELLIGENCE, 2)
                .build();
        registerTalent(basicIntelligence, "main");

        Talent basicVitality = new Talent.Builder("basic_vitality")
                .icon(Material.APPLE)
                .color(ColorUtil.HEALTH)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.DEFENSE)
                .addStatBonus(Stat.VITALITY, 3)
                .build();
        registerTalent(basicVitality, "main");

        // 추가 기본 스탯
        Talent basicDexterity = new Talent.Builder("basic_dexterity")
                .icon(Material.FEATHER)
                .color(ColorUtil.SUCCESS)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.DEXTERITY, 2)
                .build();
        registerTalent(basicDexterity, "main");

        Talent basicWisdom = new Talent.Builder("basic_wisdom")
                .icon(Material.ENCHANTED_BOOK)
                .color(ColorUtil.MANA)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.WISDOM, 2)
                .build();
        registerTalent(basicWisdom, "main");

        Talent basicLuck = new Talent.Builder("basic_luck")
                .icon(Material.RABBIT_FOOT)
                .color(ColorUtil.LEGENDARY)
                .maxLevel(5)
                .requiredPoints(2)
                .category(Talent.TalentCategory.SPECIAL)
                .addStatBonus(Stat.LUCK, 3)
                .build();
        registerTalent(basicLuck, "main");

        // 경험치 증가 특성
        Talent expBoost = new Talent.Builder("exp_boost")
                .icon(Material.EXPERIENCE_BOTTLE)
                .color(ColorUtil.EXPERIENCE)
                .maxLevel(5)
                .requiredPoints(2)
                .category(Talent.TalentCategory.UTILITY)
                .addEffect("경험치 획득량 +5%")
                .build();
        registerTalent(expBoost, "main");

        // 이동속도 증가
        Talent swiftness = new Talent.Builder("swiftness")
                .icon(Material.SUGAR)
                .color(ColorUtil.WHITE)
                .maxLevel(3)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.DEXTERITY, 1)
                .addEffect("이동속도 +3%")
                .build();
        registerTalent(swiftness, "main");

        // 고급 근력 - hasSubPage를 Builder에서 설정
        Talent advancedStrength = new Talent.Builder("advanced_strength")
                .icon(Material.DIAMOND)
                .color(ColorUtil.DIAMOND)
                .maxLevel(5)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 5)
                .addStatBonus(Stat.VITALITY, 2)
                .pageId("basic_strength_page") // 이것이 하위 페이지 ID
                .build();

        // 선행 조건 설정
        advancedStrength.addPrerequisite(basicStrength, 5);
        basicStrength.addChild(advancedStrength);
        registerTalent(advancedStrength, "basic_strength_page");
    }

    /**
     * 전사 계열 특성 초기화
     */
    private void initializeWarriorTalents() {
        // 버서커 특성
        Talent berserkRage = new Talent.Builder("berserker_rage")
                .icon(Material.REDSTONE)
                .color(ColorUtil.ERROR)
                .maxLevel(5)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 6)
                .addStatBonus(Stat.VITALITY, -2)
                .addEffect("체력이 30% 이하일 때 공격력 +50%")
                .build();
        registerJobTalent(berserkRage, JobType.BERSERKER, "main");

        Talent bloodThirst = new Talent.Builder("bloodthirst")
                .icon(Material.REDSTONE_BLOCK)
                .color(ColorUtil.DARK_RED)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("적 처치 시 체력 회복 +5%")
                .build();
        registerJobTalent(bloodThirst, JobType.BERSERKER, "main");

        Talent bloodBath = new Talent.Builder("blood_bath")
                .icon(Material.RED_DYE)
                .color(ColorUtil.DARK_RED)
                .maxLevel(3)
                .requiredPoints(3)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("광역 공격 시 흡혈 효과")
                .build();
        registerJobTalent(bloodBath, JobType.BERSERKER, "main");

        // 브루저 특성
        Talent balanced = new Talent.Builder("balanced_fighter")
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

        Talent adaptability = new Talent.Builder("adaptability")
                .icon(Material.LEATHER_CHESTPLATE)
                .color(ColorUtil.YELLOW)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.DEFENSE)
                .addStatBonus(Stat.VITALITY, 3)
                .addEffect("모든 저항 +2%")
                .build();
        registerJobTalent(adaptability, JobType.BRUISER, "main");

        Talent counterAttack = new Talent.Builder("counter_attack")
                .icon(Material.SHIELD)
                .color(ColorUtil.INFO)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("방어 성공 시 30% 확률로 반격")
                .build();
        registerJobTalent(counterAttack, JobType.BRUISER, "main");

        // 탱커 특성
        Talent fortitude = new Talent.Builder("fortitude")
                .icon(Material.SHIELD)
                .color(ColorUtil.NETHERITE)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.DEFENSE)
                .addStatBonus(Stat.VITALITY, 5)
                .addEffect("받는 피해 -5%")
                .pageId("tank_defense") // 하위 페이지
                .build();
        registerJobTalent(fortitude, JobType.TANK, "main");

        Talent shieldMastery = new Talent.Builder("shield_mastery")
                .icon(Material.SHIELD)
                .color(ColorUtil.DIAMOND)
                .maxLevel(5)
                .requiredPoints(2)
                .category(Talent.TalentCategory.DEFENSE)
                .addEffect("방패 방어 확률 +10%")
                .addEffect("방패 방어 시 반격 가능")
                .build();
        fortitude.addChild(shieldMastery);
        registerJobTalent(shieldMastery, JobType.TANK, "tank_defense");

        Talent taunt = new Talent.Builder("taunt")
                .icon(Material.BELL)
                .color(ColorUtil.WARNING)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.UTILITY)
                .addEffect("도발로 적 어그로 획득")
                .build();
        registerJobTalent(taunt, JobType.TANK, "main");

        Talent ironWill = new Talent.Builder("iron_will")
                .icon(Material.IRON_INGOT)
                .color(ColorUtil.GRAY)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.DEFENSE)
                .addStatBonus(Stat.VITALITY, 2)
                .addStatBonus(Stat.WISDOM, 2)
                .addEffect("디버프 저항 +20%")
                .build();
        registerJobTalent(ironWill, JobType.TANK, "main");
    }

    /**
     * 마법사 계열 특성 초기화
     */
    private void initializeMageTalents() {
        // 사제 특성
        Talent holyLight = new Talent.Builder("holy_light")
                .icon(Material.GLOWSTONE_DUST)
                .color(ColorUtil.LEGENDARY)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.WISDOM, 3)
                .addStatBonus(Stat.INTELLIGENCE, 2)
                .addEffect("치유량 +10%")
                .build();
        registerJobTalent(holyLight, JobType.PRIEST, "main");

        Talent blessing = new Talent.Builder("blessing")
                .icon(Material.GOLDEN_APPLE)
                .color(ColorUtil.GOLD)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.UTILITY)
                .addEffect("축복 효과 지속시간 +30%")
                .build();
        registerJobTalent(blessing, JobType.PRIEST, "main");

        Talent sanctuary = new Talent.Builder("sanctuary")
                .icon(Material.BEACON)
                .color(ColorUtil.WHITE)
                .maxLevel(3)
                .requiredPoints(3)
                .category(Talent.TalentCategory.DEFENSE)
                .addEffect("주변 아군에게 재생 효과")
                .build();
        registerJobTalent(sanctuary, JobType.PRIEST, "main");

        Talent purification = new Talent.Builder("purification")
                .icon(Material.MILK_BUCKET)
                .color(ColorUtil.AQUA)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.UTILITY)
                .addEffect("디버프 해제 능력")
                .build();
        registerJobTalent(purification, JobType.PRIEST, "main");

        // 흑마법사 특성
        Talent darkMagic = new Talent.Builder("dark_magic")
                .icon(Material.COAL)
                .color(ColorUtil.EPIC)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.INTELLIGENCE, 4)
                .addEffect("어둠 마법 피해 +20%")
                .pageId("dark_curses") // 하위 페이지
                .build();
        registerJobTalent(darkMagic, JobType.DARK_MAGE, "main");

        Talent curse = new Talent.Builder("curse_mastery")
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

        // 흑마법사 추가 특성
        Talent shadowBolt = new Talent.Builder("shadow_bolt")
                .icon(Material.ARROW)
                .color(ColorUtil.DARK_PURPLE)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.INTELLIGENCE, 2)
                .addEffect("투사체 속도 +20%")
                .build();
        registerJobTalent(shadowBolt, JobType.DARK_MAGE, "main");

        Talent lifeDrain = new Talent.Builder("life_drain")
                .icon(Material.SPIDER_EYE)
                .color(ColorUtil.DARK_RED)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("마법 피해의 20% 흡혈")
                .build();
        registerJobTalent(lifeDrain, JobType.DARK_MAGE, "main");

        Talent darkRitual = new Talent.Builder("dark_ritual")
                .icon(Material.ENDER_EYE)
                .color(ColorUtil.EPIC)
                .maxLevel(1)
                .requiredPoints(5)
                .category(Talent.TalentCategory.SPECIAL)
                .addEffect("체력을 소모하여 마나 회복")
                .build();
        registerJobTalent(darkRitual, JobType.DARK_MAGE, "main");

        // 메르시 특성
        Talent angelicBlessing = new Talent.Builder("angelic_blessing")
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

        Talent guardianAngel = new Talent.Builder("guardian_angel")
                .icon(Material.ELYTRA)
                .color(ColorUtil.WHITE)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.DEFENSE)
                .addEffect("아군 사망 방지 (쿨다운 300초)")
                .build();
        registerJobTalent(guardianAngel, JobType.MERCY, "main");

        Talent healingBoost = new Talent.Builder("healing_boost")
                .icon(Material.GLISTERING_MELON_SLICE)
                .color(ColorUtil.LIME)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.WISDOM, 3)
                .addEffect("치유량 +10%")
                .build();
        registerJobTalent(healingBoost, JobType.MERCY, "main");

        Talent resurrection = new Talent.Builder("resurrection")
                .icon(Material.TOTEM_OF_UNDYING)
                .color(ColorUtil.LEGENDARY)
                .maxLevel(1)
                .requiredPoints(5)
                .category(Talent.TalentCategory.SPECIAL)
                .addEffect("즉시 부활 능력 (쿨다운 600초)")
                .build();
        registerJobTalent(resurrection, JobType.MERCY, "main");
    }

    /**
     * 궁수 계열 특성 초기화
     */
    private void initializeArcherTalents() {
        // 아처 특성
        Talent eagleEye = new Talent.Builder("eagle_eye")
                .icon(Material.ENDER_EYE)
                .color(ColorUtil.EMERALD)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.DEXTERITY, 3)
                .addEffect("명중률 +15%")
                .pageId("archer_offense") // 하위 페이지
                .build();
        registerJobTalent(eagleEye, JobType.ARCHER, "main");

        Talent rapidShot = new Talent.Builder("rapid_shot")
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

        // 아처 추가 특성
        Talent multishot = new Talent.Builder("multishot")
                .icon(Material.SPECTRAL_ARROW)
                .color(ColorUtil.GOLD)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("화살이 3갈래로 분산")
                .build();
        registerJobTalent(multishot, JobType.ARCHER, "main");

        Talent windWalker = new Talent.Builder("wind_walker")
                .icon(Material.STRING)
                .color(ColorUtil.WHITE)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.DEXTERITY, 2)
                .addEffect("이동 중 명중률 감소 없음")
                .build();
        registerJobTalent(windWalker, JobType.ARCHER, "main");

        Talent poisonArrows = new Talent.Builder("poison_arrows")
                .icon(Material.POISONOUS_POTATO)
                .color(ColorUtil.GREEN)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("화살에 독 효과 부여")
                .build();
        registerJobTalent(poisonArrows, JobType.ARCHER, "main");

        Talent bowMastery = new Talent.Builder("bow_mastery")
                .icon(Material.BOW)
                .color(ColorUtil.BROWN)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.DEXTERITY, 1)
                .addEffect("활 피해량 +3%")
                .build();
        registerJobTalent(bowMastery, JobType.ARCHER, "main");

        // 스나이퍼 특성
        Talent precision = new Talent.Builder("precision")
                .icon(Material.SPYGLASS)
                .color(ColorUtil.INFO)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.DEXTERITY, 2)
                .addStatBonus(Stat.LUCK, 1)
                .addEffect("치명타 확률 +10%")
                .addEffect("치명타 피해 +25%")
                .build();
        registerJobTalent(precision, JobType.SNIPER, "main");

        Talent camouflage = new Talent.Builder("camouflage")
                .icon(Material.GRAY_DYE)
                .color(ColorUtil.GRAY)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.DEFENSE)
                .addEffect("정지 시 은신 효과")
                .build();
        registerJobTalent(camouflage, JobType.SNIPER, "main");

        Talent headshot = new Talent.Builder("headshot")
                .icon(Material.WITHER_SKELETON_SKULL)
                .color(ColorUtil.DARK_RED)
                .maxLevel(3)
                .requiredPoints(3)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("헤드샷 시 즉사 확률 +5%")
                .build();
        registerJobTalent(headshot, JobType.SNIPER, "main");

        Talent steadyAim = new Talent.Builder("steady_aim")
                .icon(Material.IRON_BARS)
                .color(ColorUtil.IRON)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.DEXTERITY, 3)
                .addEffect("조준 중 흔들림 감소")
                .build();
        registerJobTalent(steadyAim, JobType.SNIPER, "main");

        // 샷건맨 특성
        Talent scattershot = new Talent.Builder("scattershot")
                .icon(Material.FIRE_CHARGE)
                .color(ColorUtil.WARNING)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 2)
                .addStatBonus(Stat.DEXTERITY, 1)
                .addEffect("산탄 범위 +20%")
                .build();
        registerJobTalent(scattershot, JobType.SHOTGUNNER, "main");

        // 샷건맨 추가 특성
        Talent pointBlank = new Talent.Builder("point_blank")
                .icon(Material.TNT)
                .color(ColorUtil.ERROR)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("초근거리에서 치명타 확률 +50%")
                .build();
        registerJobTalent(pointBlank, JobType.SHOTGUNNER, "main");

        Talent explosiveShot = new Talent.Builder("explosive_shot")
                .icon(Material.GUNPOWDER)
                .color(ColorUtil.ORANGE)
                .maxLevel(3)
                .requiredPoints(3)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("착탄 시 폭발 피해")
                .build();
        registerJobTalent(explosiveShot, JobType.SHOTGUNNER, "main");

        Talent reload = new Talent.Builder("quick_reload")
                .icon(Material.LEVER)
                .color(ColorUtil.YELLOW)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.DEXTERITY, 2)
                .addEffect("재장전 시간 -20%")
                .build();
        registerJobTalent(reload, JobType.SHOTGUNNER, "main");

        Talent doubleBarrel = new Talent.Builder("double_barrel")
                .icon(Material.CROSSBOW)
                .color(ColorUtil.NETHERITE)
                .maxLevel(1)
                .requiredPoints(5)
                .category(Talent.TalentCategory.SPECIAL)
                .addEffect("2연발 사격")
                .build();
        registerJobTalent(doubleBarrel, JobType.SHOTGUNNER, "main");
    }

    /**
     * 특성 등록
     */
    private void registerTalent(@NotNull Talent talent, @NotNull String pageId) {
        // Talent 클래스의 REGISTRY에도 등록
        Talent.register(talent);

        // TalentManager의 맵에도 등록
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
        List<Talent> jobMainTalents = new ArrayList<>();

        // 직업별 특성
        List<Talent> jobSpecificTalents = jobTalents.getOrDefault(job, new ArrayList<>());
        for (Talent talent : jobSpecificTalents) {
            // 메인 페이지의 특성만 추가
            if (talentPages.getOrDefault("main", new ArrayList<>()).contains(talent)) {
                jobMainTalents.add(talent);
            }
        }

        // 공통 특성도 추가
        List<Talent> commonTalents = talentPages.getOrDefault("main", new ArrayList<>());
        for (Talent talent : commonTalents) {
            // 직업 제한이 없는 특성만 추가
            if (!jobMainTalents.contains(talent) && talent.canLearn(job)) {
                jobMainTalents.add(talent);
            }
        }

        return jobMainTalents;
    }

    /**
     * 모든 특성 가져오기
     */
    @NotNull
    public Collection<Talent> getAllTalents() {
        return allTalents.values();
    }
}