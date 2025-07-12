package com.febrie.rpg.talent;

import com.febrie.rpg.job.JobType;
import com.febrie.rpg.stat.Stat;
import com.febrie.rpg.util.ColorUtil;
import net.kyori.adventure.text.format.TextColor;
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

        // 추가 기본 스탯
        Talent basicDexterity = new Talent.Builder("basic_dexterity")
                .name("기초 민첩", "Basic Dexterity")
                .icon(Material.FEATHER)
                .color(ColorUtil.SUCCESS)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.DEXTERITY, 2)
                .build();
        registerTalent(basicDexterity, "main");

        Talent basicWisdom = new Talent.Builder("basic_wisdom")
                .name("기초 지혜", "Basic Wisdom")
                .icon(Material.ENCHANTED_BOOK)
                .color(ColorUtil.MANA)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.WISDOM, 2)
                .build();
        registerTalent(basicWisdom, "main");

        Talent basicLuck = new Talent.Builder("basic_luck")
                .name("기초 행운", "Basic Luck")
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
                .name("경험의 축복", "Blessing of Experience")
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
                .name("신속함", "Swiftness")
                .icon(Material.SUGAR)
                .color(ColorUtil.WHITE)
                .maxLevel(3)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.DEXTERITY, 1)
                .addEffect("이동속도 +3%")
                .build();
        registerTalent(swiftness, "main");

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

        // 기초 근력에 대량의 하위 특성 추가
        createMassiveStrengthTalents(basicStrength);
    }

    /**
     * 기초 근력 하위 특성 대량 생성 (100개)
     */
    private void createMassiveStrengthTalents(@NotNull Talent parent) {
        // 티어별로 특성 생성
        String[] tiers = {"초급", "중급", "고급", "희귀", "영웅", "전설"};
        String[] tierEnglish = {"Basic", "Intermediate", "Advanced", "Rare", "Epic", "Legendary"};
        TextColor[] tierColors = {ColorUtil.COMMON, ColorUtil.UNCOMMON, ColorUtil.RARE,
                ColorUtil.EPIC, ColorUtil.LEGENDARY, ColorUtil.LEGENDARY};

        String[] types = {"공격력", "치명타", "방어관통", "공격속도", "흡혈"};
        String[] typesEnglish = {"Attack", "Critical", "Penetration", "Speed", "Lifesteal"};
        Material[] typeIcons = {Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD,
                Material.NETHERITE_SWORD, Material.REDSTONE};

        int talentCount = 0;

        // 각 티어별로 특성 생성
        for (int tier = 0; tier < tiers.length; tier++) {
            for (int type = 0; type < types.length; type++) {
                // 각 조합마다 여러 개 생성
                for (int variant = 1; variant <= 4; variant++) {
                    String id = String.format("strength_%s_%s_%d",
                            tierEnglish[tier].toLowerCase(),
                            typesEnglish[type].toLowerCase(),
                            variant);

                    String koreanName = String.format("%s %s %d단계", tiers[tier], types[type], variant);
                    String englishName = String.format("%s %s Stage %d", tierEnglish[tier], typesEnglish[type], variant);

                    // 티어가 높을수록 더 많은 포인트 필요
                    int requiredPoints = tier + 1;
                    int maxLevel = 5 - tier; // 높은 티어일수록 최대 레벨 낮음
                    int statBonus = (tier + 1) * (variant + 1);

                    Talent.Builder builder = new Talent.Builder(id)
                            .name(koreanName, englishName)
                            .icon(typeIcons[type])
                            .color(tierColors[tier])
                            .maxLevel(maxLevel)
                            .requiredPoints(requiredPoints)
                            .category(Talent.TalentCategory.OFFENSE)
                            .addStatBonus(Stat.STRENGTH, statBonus);

                    // 효과 추가
                    switch (type) {
                        case 0 -> builder.addEffect(String.format("물리 공격력 +%d%%", (tier + 1) * variant));
                        case 1 -> builder.addEffect(String.format("치명타 확률 +%d%%", (tier + 1) * variant * 2));
                        case 2 -> builder.addEffect(String.format("방어력 무시 +%d%%", (tier + 1) * variant));
                        case 3 -> builder.addEffect(String.format("공격 속도 +%d%%", (tier + 1) * variant * 3));
                        case 4 -> builder.addEffect(String.format("흡혈 +%d%%", (tier + 1) * variant * 2));
                    }

                    Talent talent = builder.build();

                    // 선행 조건 설정 (이전 티어의 특성 필요)
                    if (tier > 0) {
                        talent.addPrerequisite(parent, tier * 2);
                    }

                    parent.addChild(talent);
                    registerTalent(talent, "basic_strength_page");

                    talentCount++;
                    if (talentCount >= 100) return;
                }
            }
        }

        // 추가 특수 특성들
        String[] specialNames = {
                "근력의 정수", "힘의 폭발", "거인의 힘", "타이탄의 분노", "신들의 축복",
                "전쟁의 화신", "파괴의 권능", "불멸의 힘", "절대 근력", "초월의 경지"
        };

        String[] specialNamesEnglish = {
                "Essence of Strength", "Power Burst", "Giant's Might", "Titan's Wrath", "Blessing of Gods",
                "Avatar of War", "Power of Destruction", "Immortal Strength", "Absolute Power", "Transcendent State"
        };

        for (int i = 0; i < specialNames.length && talentCount < 100; i++) {
            String id = "strength_special_" + (i + 1);

            Talent special = new Talent.Builder(id)
                    .name(specialNames[i], specialNamesEnglish[i])
                    .icon(Material.NETHER_STAR)
                    .color(ColorUtil.LEGENDARY)
                    .maxLevel(1)
                    .requiredPoints(5 + i)
                    .category(Talent.TalentCategory.SPECIAL)
                    .addStatBonus(Stat.STRENGTH, 20 + i * 5)
                    .addStatBonus(Stat.VITALITY, 10 + i * 2)
                    .addEffect(String.format("모든 피해 +%d%%", 10 + i * 5))
                    .build();

            special.addPrerequisite(parent, 10);
            parent.addChild(special);
            registerTalent(special, "basic_strength_page");
            talentCount++;
        }
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

        // 버서커 추가 특성들
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

        // 추가 버서커 특성들
        Talent weaponMastery = new Talent.Builder("berserker_weapon_mastery")
                .name("무기 숙련", "Weapon Mastery")
                .icon(Material.NETHERITE_AXE)
                .color(ColorUtil.COPPER)
                .maxLevel(10)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.STRENGTH, 1)
                .addEffect("무기 피해량 +2%")
                .build();
        registerJobTalent(weaponMastery, JobType.BERSERKER, "main");

        Talent criticalStrike = new Talent.Builder("berserker_critical")
                .name("치명적인 일격", "Critical Strike")
                .icon(Material.GOLDEN_SWORD)
                .color(ColorUtil.GOLD)
                .maxLevel(5)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.LUCK, 2)
                .addEffect("치명타 확률 +4%")
                .build();
        registerJobTalent(criticalStrike, JobType.BERSERKER, "main");

        Talent bloodBath = new Talent.Builder("blood_bath")
                .name("피의 목욕", "Blood Bath")
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

        Talent adaptability = new Talent.Builder("adaptability")
                .name("적응력", "Adaptability")
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
                .name("반격", "Counter Attack")
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

        Talent guardian = new Talent.Builder("guardian")
                .name("수호자", "Guardian")
                .icon(Material.IRON_CHESTPLATE)
                .color(ColorUtil.WHITE)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.DEFENSE)
                .addStatBonus(Stat.VITALITY, 3)
                .addEffect("주변 아군의 방어력 증가")
                .build();
        registerJobTalent(guardian, JobType.TANK, "main");

        Talent lastStand = new Talent.Builder("last_stand")
                .name("최후의 저항", "Last Stand")
                .icon(Material.GOLDEN_APPLE)
                .color(ColorUtil.LEGENDARY)
                .maxLevel(1)
                .requiredPoints(5)
                .category(Talent.TalentCategory.SPECIAL)
                .addEffect("체력 20% 이하에서 방어력 +100%")
                .build();
        registerJobTalent(lastStand, JobType.TANK, "main");
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

        // 사제 추가 특성
        Talent blessing = new Talent.Builder("blessing")
                .name("축복", "Blessing")
                .icon(Material.SUNFLOWER)
                .color(ColorUtil.YELLOW)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.UTILITY)
                .addStatBonus(Stat.WISDOM, 2)
                .addEffect("버프 지속시간 +10%")
                .build();
        registerJobTalent(blessing, JobType.PRIEST, "main");

        Talent sanctuary = new Talent.Builder("sanctuary")
                .name("성역", "Sanctuary")
                .icon(Material.BEACON)
                .color(ColorUtil.WHITE)
                .maxLevel(3)
                .requiredPoints(3)
                .category(Talent.TalentCategory.DEFENSE)
                .addEffect("주변 아군에게 재생 효과")
                .build();
        registerJobTalent(sanctuary, JobType.PRIEST, "main");

        Talent purification = new Talent.Builder("purification")
                .name("정화", "Purification")
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

        // 흑마법사 추가 특성
        Talent shadowBolt = new Talent.Builder("shadow_bolt")
                .name("어둠의 화살", "Shadow Bolt")
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
                .name("생명력 흡수", "Life Drain")
                .icon(Material.SPIDER_EYE)
                .color(ColorUtil.DARK_RED)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("마법 피해의 20% 흡혈")
                .build();
        registerJobTalent(lifeDrain, JobType.DARK_MAGE, "main");

        Talent darkRitual = new Talent.Builder("dark_ritual")
                .name("어둠의 의식", "Dark Ritual")
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

        Talent guardianAngel = new Talent.Builder("guardian_angel")
                .name("수호천사", "Guardian Angel")
                .icon(Material.ELYTRA)
                .color(ColorUtil.WHITE)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.DEFENSE)
                .addEffect("아군 사망 방지 (쿨다운 300초)")
                .build();
        registerJobTalent(guardianAngel, JobType.MERCY, "main");

        Talent healingBoost = new Talent.Builder("healing_boost")
                .name("치유 증폭", "Healing Boost")
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
                .name("부활", "Resurrection")
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

        // 아처 추가 특성
        Talent multishot = new Talent.Builder("multishot")
                .name("멀티샷", "Multishot")
                .icon(Material.SPECTRAL_ARROW)
                .color(ColorUtil.GOLD)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("화살이 3갈래로 분산")
                .build();
        registerJobTalent(multishot, JobType.ARCHER, "main");

        Talent windWalker = new Talent.Builder("wind_walker")
                .name("바람길", "Wind Walker")
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
                .name("독화살", "Poison Arrows")
                .icon(Material.POISONOUS_POTATO)
                .color(ColorUtil.GREEN)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("화살에 독 효과 부여")
                .build();
        registerJobTalent(poisonArrows, JobType.ARCHER, "main");

        Talent bowMastery = new Talent.Builder("bow_mastery")
                .name("활 숙련", "Bow Mastery")
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

        // 스나이퍼 추가 특성
        Talent camouflage = new Talent.Builder("camouflage")
                .name("위장", "Camouflage")
                .icon(Material.TALL_GRASS)
                .color(ColorUtil.DARK_GREEN)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.UTILITY)
                .addEffect("정지 시 투명화")
                .build();
        registerJobTalent(camouflage, JobType.SNIPER, "main");

        Talent longRange = new Talent.Builder("long_range")
                .name("장거리 사격", "Long Range")
                .icon(Material.ENDER_PEARL)
                .color(ColorUtil.CYAN)
                .maxLevel(5)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("거리에 따른 피해 증가")
                .build();
        registerJobTalent(longRange, JobType.SNIPER, "main");

        Talent steadyAim = new Talent.Builder("steady_aim")
                .name("안정된 조준", "Steady Aim")
                .icon(Material.TRIPWIRE_HOOK)
                .color(ColorUtil.GRAY)
                .maxLevel(3)
                .requiredPoints(1)
                .category(Talent.TalentCategory.OFFENSE)
                .addStatBonus(Stat.DEXTERITY, 3)
                .addEffect("조준 시간 감소")
                .build();
        registerJobTalent(steadyAim, JobType.SNIPER, "main");

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

        // 샷건맨 추가 특성
        Talent pointBlank = new Talent.Builder("point_blank")
                .name("영거리 사격", "Point Blank")
                .icon(Material.TNT)
                .color(ColorUtil.ERROR)
                .maxLevel(3)
                .requiredPoints(2)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("초근거리에서 치명타 확률 +50%")
                .build();
        registerJobTalent(pointBlank, JobType.SHOTGUNNER, "main");

        Talent explosiveShot = new Talent.Builder("explosive_shot")
                .name("폭발탄", "Explosive Shot")
                .icon(Material.GUNPOWDER)
                .color(ColorUtil.ORANGE)
                .maxLevel(3)
                .requiredPoints(3)
                .category(Talent.TalentCategory.OFFENSE)
                .addEffect("착탄 시 폭발 피해")
                .build();
        registerJobTalent(explosiveShot, JobType.SHOTGUNNER, "main");

        Talent reload = new Talent.Builder("quick_reload")
                .name("빠른 재장전", "Quick Reload")
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
                .name("더블 배럴", "Double Barrel")
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
        String[] commonTalentIds = {"basic_strength", "basic_intelligence", "basic_vitality",
                "basic_dexterity", "basic_wisdom", "basic_luck",
                "exp_boost", "swiftness"};

        for (String id : commonTalentIds) {
            Talent talent = getTalent(id);
            if (talent != null) {
                mainTalents.add(talent);
            }
        }

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