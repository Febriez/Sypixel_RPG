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
        // 공통 특성 - 제거 (직업별로만 특성을 제공)
        // initializeCommonTalents();

        // 전사 계열 특성
        initializeWarriorTalents();

        // 마법사 계열 특성
        initializeMageTalents();

        // 궁수 계열 특성
        initializeArcherTalents();

        plugin.getLogger().info("Initialized " + allTalents.size() + " talents");
    }

    /**
     * 전사 계열 특성 초기화
     */
    private void initializeWarriorTalents() {
        // 광전사 특성
        Talent berserkerStrength = new Talent.Builder("basic_strength")
                .icon(Material.IRON_INGOT)
                .color(ColorUtil.COPPER)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 2)
                .build();
        registerJobTalent(berserkerStrength, JobType.BERSERKER, "main");

        Talent berserkRage = new Talent.Builder("berserker_rage")
                .icon(Material.BLAZE_POWDER)
                .color(ColorUtil.DARK_RED)
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
        Talent bruiserStrength = new Talent.Builder("basic_strength")
                .icon(Material.IRON_INGOT)
                .color(ColorUtil.COPPER)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 2)
                .build();
        registerJobTalent(bruiserStrength, JobType.BRUISER, "main");

        Talent bruiserVitality = new Talent.Builder("basic_vitality")
                .icon(Material.APPLE)
                .color(ColorUtil.HEALTH)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.DEFENSE)
                .addStatBonus(Stat.VITALITY, 3)
                .build();
        registerJobTalent(bruiserVitality, JobType.BRUISER, "main");

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
        Talent tankVitality = new Talent.Builder("basic_vitality")
                .icon(Material.APPLE)
                .color(ColorUtil.HEALTH)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.DEFENSE)
                .addStatBonus(Stat.VITALITY, 3)
                .build();
        registerJobTalent(tankVitality, JobType.TANK, "main");

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
        Talent priestIntelligence = new Talent.Builder("basic_intelligence")
                .icon(Material.BOOK)
                .color(ColorUtil.INFO)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.INTELLIGENCE, 2)
                .build();
        registerJobTalent(priestIntelligence, JobType.PRIEST, "main");

        Talent priestWisdom = new Talent.Builder("basic_wisdom")
                .icon(Material.LAPIS_LAZULI)
                .color(ColorUtil.INFO)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.WISDOM, 2)
                .build();
        registerJobTalent(priestWisdom, JobType.PRIEST, "main");

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
                .addEffect("아군 버프 효과")
                .build();
        registerJobTalent(blessing, JobType.PRIEST, "main");

        Talent sanctuary = new Talent.Builder("sanctuary")
                .icon(Material.BEACON)
                .color(ColorUtil.EMERALD)
                .maxLevel(3)
                .requiredPoints(3)
                .category(Talent.TalentCategory.DEFENSE)
                .addEffect("성역 생성으로 범위 치유")
                .build();
        registerJobTalent(sanctuary, JobType.PRIEST, "main");

        Talent purification = new Talent.Builder("purification")
                .icon(Material.MILK_BUCKET)
                .color(ColorUtil.WHITE)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.UTILITY)
                .addEffect("디버프 제거")
                .build();
        registerJobTalent(purification, JobType.PRIEST, "main");

        // 이전에 공통이었던 특성들을 사제 전용으로
        Talent angelicBlessing = new Talent.Builder("angelic_blessing")
                .icon(Material.FEATHER)
                .color(ColorUtil.LEGENDARY)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.STRENGTH, 1)
                .addStatBonus(Stat.INTELLIGENCE, 1)
                .addStatBonus(Stat.VITALITY, 1)
                .addStatBonus(Stat.DEXTERITY, 1)
                .addStatBonus(Stat.WISDOM, 1)
                .addStatBonus(Stat.LUCK, 1)
                .build();
        registerJobTalent(angelicBlessing, JobType.PRIEST, "main");

        Talent guardianAngel = new Talent.Builder("guardian_angel")
                .icon(Material.TOTEM_OF_UNDYING)
                .color(ColorUtil.GOLD)
                .maxLevel(1)
                .requiredPoints(5)
                .category(Talent.TalentCategory.DEFENSE)
                .addEffect("사망 시 50% 확률로 부활")
                .build();
        registerJobTalent(guardianAngel, JobType.PRIEST, "main");

        Talent healingBoost = new Talent.Builder("healing_boost")
                .icon(Material.GOLDEN_CARROT)
                .color(ColorUtil.EMERALD)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addEffect("치유 효과 +20%")
                .build();
        registerJobTalent(healingBoost, JobType.PRIEST, "main");

        Talent resurrection = new Talent.Builder("resurrection")
                .icon(Material.END_CRYSTAL)
                .color(ColorUtil.LEGENDARY)
                .maxLevel(1)
                .requiredPoints(10)
                .category(Talent.TalentCategory.SPECIAL)
                .addEffect("아군 부활 가능")
                .build();
        registerJobTalent(resurrection, JobType.PRIEST, "main");

        // 흑마법사 특성
        Talent warlockIntelligence = new Talent.Builder("basic_intelligence")
                .icon(Material.BOOK)
                .color(ColorUtil.INFO)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.INTELLIGENCE, 2)
                .build();
        registerJobTalent(warlockIntelligence, JobType.DARK_MAGE, "main");

        Talent darkMagic = new Talent.Builder("dark_magic")
                .icon(Material.WITHER_ROSE)
                .color(ColorUtil.DARK_PURPLE)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.INTELLIGENCE, 5)
                .addEffect("암흑 피해 +10%")
                .pageId("warlock_darkness") // 하위 페이지
                .build();
        registerJobTalent(darkMagic, JobType.DARK_MAGE, "main");

        Talent shadowBolt = new Talent.Builder("shadow_bolt")
                .icon(Material.ENDER_PEARL)
                .color(ColorUtil.DARK_PURPLE)
                .maxLevel(5)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("암흑 화살 피해 증가")
                .build();
        darkMagic.addChild(shadowBolt);
        registerJobTalent(shadowBolt, JobType.DARK_MAGE, "warlock_darkness");

        Talent lifeDrain = new Talent.Builder("life_drain")
                .icon(Material.SPIDER_EYE)
                .color(ColorUtil.DARK_RED)
                .maxLevel(5)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("피해의 30% 체력 흡수")
                .build();
        registerJobTalent(lifeDrain, JobType.DARK_MAGE, "main");

        Talent darkRitual = new Talent.Builder("dark_ritual")
                .icon(Material.NETHER_STAR)
                .color(ColorUtil.DARK_PURPLE)
                .maxLevel(1)
                .requiredPoints(5)
                .category(Talent.TalentCategory.SPECIAL)
                .addEffect("모든 능력치 일시 상승")
                .build();
        registerJobTalent(darkRitual, JobType.DARK_MAGE, "main");
    }

    /**
     * 궁수 계열 특성 초기화
     */
    private void initializeArcherTalents() {
        // 궁수 특성
        Talent archerDexterity = new Talent.Builder("basic_dexterity")
                .icon(Material.FEATHER)
                .color(ColorUtil.SUCCESS)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.DEXTERITY, 2)
                .build();
        registerJobTalent(archerDexterity, JobType.ARCHER, "main");

        Talent archerLuck = new Talent.Builder("basic_luck")
                .icon(Material.RABBIT_FOOT)
                .color(ColorUtil.GOLD)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.LUCK, 1)
                .build();
        registerJobTalent(archerLuck, JobType.ARCHER, "main");

        Talent eagleEye = new Talent.Builder("eagle_eye")
                .icon(Material.ENDER_EYE)
                .color(ColorUtil.GREEN)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.DEXTERITY, 3)
                .addEffect("명중률 +10%, 사거리 +2")
                .pageId("archer_precision") // 하위 페이지
                .build();
        registerJobTalent(eagleEye, JobType.ARCHER, "main");

        Talent multishot = new Talent.Builder("multishot")
                .icon(Material.SPECTRAL_ARROW)
                .color(ColorUtil.EMERALD)
                .maxLevel(3)
                .requiredPoints(3)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("3발 동시 발사")
                .build();
        registerJobTalent(multishot, JobType.ARCHER, "main");

        Talent windWalker = new Talent.Builder("wind_walker")
                .icon(Material.SUGAR)
                .color(ColorUtil.AQUA)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addEffect("이동 속도 +10%, 회피율 +5%")
                .build();
        registerJobTalent(windWalker, JobType.ARCHER, "main");

        Talent poisonArrows = new Talent.Builder("poison_arrows")
                .icon(Material.POISONOUS_POTATO)
                .color(ColorUtil.DARK_GREEN)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("화살에 독 피해 추가")
                .build();
        registerJobTalent(poisonArrows, JobType.ARCHER, "main");

        // 궁수 정밀 사격 하위 페이지
        Talent bowMastery = new Talent.Builder("bow_mastery")
                .icon(Material.BOW)
                .color(ColorUtil.COPPER)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("활 공격력 +15%")
                .build();
        eagleEye.addChild(bowMastery);
        registerJobTalent(bowMastery, JobType.ARCHER, "archer_precision");

        Talent precision = new Talent.Builder("precision")
                .icon(Material.TARGET)
                .color(ColorUtil.YELLOW)
                .maxLevel(5)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("치명타 확률 +10%")
                .build();
        registerJobTalent(precision, JobType.ARCHER, "archer_precision");

        Talent camouflage = new Talent.Builder("camouflage")
                .icon(Material.TALL_GRASS)
                .color(ColorUtil.DARK_GREEN)
                .maxLevel(3)
                .requiredPoints(3)
                .category(Talent.TalentCategory.UTILITY)
                .addEffect("5초간 은신")
                .build();
        registerJobTalent(camouflage, JobType.ARCHER, "archer_precision");

        Talent headshot = new Talent.Builder("headshot")
                .icon(Material.SKELETON_SKULL)
                .color(ColorUtil.RED)
                .maxLevel(3)
                .requiredPoints(5)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("헤드샷 피해 +100%")
                .build();
        registerJobTalent(headshot, JobType.ARCHER, "archer_precision");

        Talent steadyAim = new Talent.Builder("steady_aim")
                .icon(Material.SPYGLASS)
                .color(ColorUtil.INFO)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("정지 시 명중률 +30%")
                .build();
        registerJobTalent(steadyAim, JobType.ARCHER, "archer_precision");

        Talent scattershot = new Talent.Builder("scattershot")
                .icon(Material.ARROW)
                .color(ColorUtil.ORANGE)
                .maxLevel(3)
                .requiredPoints(3)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("부채꼴 범위 공격")
                .build();
        registerJobTalent(scattershot, JobType.ARCHER, "archer_precision");

        // 샷거너 특성
        Talent shotgunnerStrength = new Talent.Builder("basic_strength")
                .icon(Material.IRON_INGOT)
                .color(ColorUtil.COPPER)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 2)
                .build();
        registerJobTalent(shotgunnerStrength, JobType.SHOTGUNNER, "main");

        Talent shotgunnerDexterity = new Talent.Builder("basic_dexterity")
                .icon(Material.FEATHER)
                .color(ColorUtil.SUCCESS)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.DEXTERITY, 2)
                .build();
        registerJobTalent(shotgunnerDexterity, JobType.SHOTGUNNER, "main");

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
     * 공통 특성을 제외하고 오직 해당 직업의 특성만 반환
     */
    @NotNull
    public List<Talent> getJobMainTalents(@NotNull JobType job) {
        List<Talent> jobMainTalents = new ArrayList<>();

        // 직업별 특성만 가져오기
        List<Talent> jobSpecificTalents = jobTalents.getOrDefault(job, new ArrayList<>());
        for (Talent talent : jobSpecificTalents) {
            // 메인 페이지의 특성만 추가
            if (talentPages.getOrDefault("main", new ArrayList<>()).contains(talent)) {
                jobMainTalents.add(talent);
            }
        }

        // 공통 특성은 더 이상 추가하지 않음

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