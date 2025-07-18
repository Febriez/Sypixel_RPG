package com.febrie.rpg.quest.impl.advancement;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 성기사의 서약 - 직업 전직 퀘스트
 * 전사에서 성기사로 승급하는 신성한 서약 퀘스트
 *
 * @author Febrie
 */
public class PaladinOathQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class PaladinOathBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new PaladinOathQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public PaladinOathQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private PaladinOathQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new PaladinOathBuilder()
                .id(QuestID.CLASS_PALADIN_OATH)
                .objectives(Arrays.asList(
                        // 성기사의 길 시작
                        new InteractNPCObjective("paladin_mentor", 140), // 성기사 스승
                        new ReachLevelObjective("warrior_mastery", 30),
                        new InteractNPCObjective("oath_preparation", 140),
                        
                        // 첫 번째 미덕 - 용기
                        new VisitLocationObjective("courage_trial", "trial_of_courage"),
                        new KillMobObjective("face_fears", EntityType.WITHER_SKELETON, 50),
                        new KillMobObjective("defeat_champion", EntityType.IRON_GOLEM, 10),
                        new SurviveObjective("courage_test", 600), // 10분간 시련
                        new CollectItemObjective("courage_emblem", Material.IRON_INGOT, 30),
                        
                        // 두 번째 미덕 - 정의
                        new VisitLocationObjective("justice_court", "hall_of_justice"),
                        new InteractNPCObjective("judge_npc", 141), // 정의의 심판관
                        new KillMobObjective("punish_evil", EntityType.PILLAGER, 30),
                        new KillMobObjective("destroy_undead", EntityType.ZOMBIE, 100),
                        new DeliverItemObjective("return_stolen", "victim_npc", Material.EMERALD, 20),
                        new CollectItemObjective("justice_scale", Material.GOLD_INGOT, 20),
                        
                        // 세 번째 미덕 - 자비
                        new VisitLocationObjective("mercy_temple", "temple_of_mercy"),
                        new CollectItemObjective("healing_herbs", Material.GLISTERING_MELON_SLICE, 10),
                        new CraftItemObjective("brew_potions", Material.POTION, 20),
                        new DeliverItemObjective("heal_wounded", "wounded_soldier", Material.POTION, 10),
                        new PayCurrencyObjective("charity", CurrencyType.GOLD, 5000),
                        new CollectItemObjective("mercy_tears", Material.GHAST_TEAR, 5),
                        
                        // 네 번째 미덕 - 희생
                        new VisitLocationObjective("sacrifice_altar", "altar_of_sacrifice"),
                        new CollectItemObjective("valuable_items", Material.DIAMOND, 30),
                        new PlaceBlockObjective("place_offering", Material.DIAMOND_BLOCK, 3),
                        new SurviveObjective("endure_pain", 300), // 5분간 고통 견디기
                        new PayCurrencyObjective("sacrifice_wealth", CurrencyType.DIAMOND, 50),
                        new CollectItemObjective("sacrifice_token", Material.TOTEM_OF_UNDYING, 1),
                        
                        // 신성한 무기 제작
                        new InteractNPCObjective("holy_weaponsmith", 142), // 신성 대장장이
                        new CollectItemObjective("blessed_metal", Material.GOLD_BLOCK, 5),
                        new CollectItemObjective("holy_water", Material.POTION, 3),
                        new CraftItemObjective("forge_sword", Material.GOLDEN_SWORD, 1),
                        new DeliverItemObjective("bless_weapon", "paladin_mentor", Material.GOLDEN_SWORD, 1),
                        
                        // 최종 서약 의식
                        new VisitLocationObjective("oath_cathedral", "sacred_cathedral"),
                        new PlaceBlockObjective("light_candles", Material.CANDLE, 7),
                        new InteractNPCObjective("begin_ceremony", 140),
                        new KillMobObjective("final_trial", EntityType.WITHER, 1),
                        new SurviveObjective("divine_light", 900), // 15분간 신성한 빛
                        
                        // 성기사 승급
                        new CollectItemObjective("paladin_seal", Material.NETHER_STAR, 1),
                        new InteractNPCObjective("complete_oath", 140)
                ))
                .reward(BasicReward.builder()
                        .addCurrency(CurrencyType.GOLD, 10000)
                        .addCurrency(CurrencyType.DIAMOND, 75)
                        .addItem(new ItemStack(Material.GOLDEN_HELMET)) // 성기사 투구
                        .addItem(new ItemStack(Material.GOLDEN_CHESTPLATE)) // 성기사 갑옷
                        .addItem(new ItemStack(Material.SHIELD)) // 성기사 방패
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 5))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING))
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.NORMAL)
                .minLevel(30)
                .maxLevel(0);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "성기사의 신성한 서약" : "The Sacred Oath of Paladin";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "전사의 길에서 성기사의 길로 나아가는 신성한 여정입니다.",
                    "네 가지 미덕을 증명하고 신성한 서약을 완수하세요.",
                    "",
                    "⚜️ 성기사의 특성:",
                    "• 신성 마법 사용 가능",
                    "• 언데드에게 추가 피해",
                    "• 아군 치유 능력",
                    "• 방어력 대폭 증가",
                    "• 특별한 성기사 스킬 해금",
                    "",
                    "네 가지 미덕의 시련:",
                    "• 용기 - 두려움에 맞서기",
                    "• 정의 - 악을 심판하기",
                    "• 자비 - 약자를 돕기",
                    "• 희생 - 자신을 바치기",
                    "",
                    "퀘스트 진행:",
                    "• 1단계: 성기사 스승 만나기",
                    "• 2단계: 용기의 시련 통과",
                    "• 3단계: 정의의 시련 통과",
                    "• 4단계: 자비의 시련 통과",
                    "• 5단계: 희생의 시련 통과",
                    "• 6단계: 신성한 무기 제작",
                    "• 7단계: 서약 의식 완수",
                    "",
                    "목표:",
                    "• 레벨 30 달성 (전사)",
                    "• 네 가지 미덕 증명",
                    "• 신성한 무기 제작",
                    "• 위더 처치",
                    "• 15분간 신성한 빛 견디기",
                    "• 최종 서약 완수",
                    "",
                    "보상:",
                    "• 성기사 직업 전직",
                    "• 골드 10,000",
                    "• 다이아몬드 75개",
                    "• 성기사 장비 세트",
                    "• 불사의 토템",
                    "• 마법이 부여된 책 5개",
                    "• 경험치 5,000"
            );
        } else {
            return Arrays.asList(
                    "A sacred journey from the path of warrior to the path of paladin.",
                    "Prove four virtues and complete the holy oath.",
                    "",
                    "⚜️ Paladin Features:",
                    "• Can use holy magic",
                    "• Extra damage to undead",
                    "• Ally healing abilities",
                    "• Greatly increased defense",
                    "• Unlock special paladin skills",
                    "",
                    "Four Trials of Virtue:",
                    "• Courage - Face your fears",
                    "• Justice - Judge the evil",
                    "• Mercy - Help the weak",
                    "• Sacrifice - Give yourself",
                    "",
                    "Quest Progress:",
                    "• Stage 1: Meet Paladin Mentor",
                    "• Stage 2: Pass Trial of Courage",
                    "• Stage 3: Pass Trial of Justice",
                    "• Stage 4: Pass Trial of Mercy",
                    "• Stage 5: Pass Trial of Sacrifice",
                    "• Stage 6: Forge Holy Weapon",
                    "• Stage 7: Complete Oath Ceremony",
                    "",
                    "Objectives:",
                    "• Reach Level 30 (Warrior)",
                    "• Prove four virtues",
                    "• Forge holy weapon",
                    "• Defeat Wither",
                    "• Endure divine light for 15 minutes",
                    "• Complete final oath",
                    "",
                    "Rewards:",
                    "• Paladin class advancement",
                    "• 10,000 Gold",
                    "• 75 Diamonds",
                    "• Paladin equipment set",
                    "• Totem of Undying",
                    "• 5 Enchanted Books",
                    "• 5,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "paladin_mentor" -> isKorean ? "성기사 스승과 대화" : "Talk to Paladin Mentor";
            case "warrior_mastery" -> isKorean ? "전사 레벨 30 달성" : "Reach Warrior Level 30";
            case "oath_preparation" -> isKorean ? "서약 준비 상담" : "Oath preparation consultation";
            case "courage_trial" -> isKorean ? "용기의 시련장 도착" : "Arrive at Trial of Courage";
            case "face_fears" -> isKorean ? "두려움 극복 (위더 스켈레톤 50마리)" : "Face fears (50 Wither Skeletons)";
            case "defeat_champion" -> isKorean ? "용기의 챔피언 10명 처치" : "Defeat 10 Champions of Courage";
            case "courage_test" -> isKorean ? "10분간 용기의 시험 견디기" : "Endure courage test for 10 minutes";
            case "courage_emblem" -> isKorean ? "용기의 상징 30개 수집" : "Collect 30 Emblems of Courage";
            case "justice_court" -> isKorean ? "정의의 법정 방문" : "Visit Hall of Justice";
            case "judge_npc" -> isKorean ? "정의의 심판관과 대화" : "Talk to Judge of Justice";
            case "punish_evil" -> isKorean ? "악인 처벌 (약탈자 30마리)" : "Punish evil (30 Pillagers)";
            case "destroy_undead" -> isKorean ? "언데드 정화 (좀비 100마리)" : "Purify undead (100 Zombies)";
            case "return_stolen" -> isKorean ? "도난품 반환" : "Return stolen goods";
            case "justice_scale" -> isKorean ? "정의의 저울 20개 수집" : "Collect 20 Scales of Justice";
            case "mercy_temple" -> isKorean ? "자비의 신전 방문" : "Visit Temple of Mercy";
            case "healing_herbs" -> isKorean ? "치유 약초 10개 수집" : "Gather 10 healing herbs";
            case "brew_potions" -> isKorean ? "치유 물약 20개 제조" : "Brew 20 healing potions";
            case "heal_wounded" -> isKorean ? "부상병에게 물약 전달" : "Deliver potions to wounded";
            case "charity" -> isKorean ? "자선 기부 (5,000골드)" : "Charity donation (5,000 gold)";
            case "mercy_tears" -> isKorean ? "자비의 눈물 5개 수집" : "Collect 5 Tears of Mercy";
            case "sacrifice_altar" -> isKorean ? "희생의 제단 방문" : "Visit Altar of Sacrifice";
            case "valuable_items" -> isKorean ? "귀중품 30개 수집" : "Collect 30 valuable items";
            case "place_offering" -> isKorean ? "다이아몬드 블록 3개 바치기" : "Offer 3 Diamond Blocks";
            case "endure_pain" -> isKorean ? "5분간 고통 견디기" : "Endure pain for 5 minutes";
            case "sacrifice_wealth" -> isKorean ? "재산 희생 (다이아몬드 50개)" : "Sacrifice wealth (50 Diamonds)";
            case "sacrifice_token" -> isKorean ? "희생의 증표 획득" : "Obtain Token of Sacrifice";
            case "holy_weaponsmith" -> isKorean ? "신성 대장장이와 대화" : "Talk to Holy Weaponsmith";
            case "blessed_metal" -> isKorean ? "축복받은 금속 5개 수집" : "Collect 5 Blessed Metals";
            case "holy_water" -> isKorean ? "성수 3병 수집" : "Collect 3 Holy Water";
            case "forge_sword" -> isKorean ? "신성한 검 제작" : "Forge holy sword";
            case "bless_weapon" -> isKorean ? "무기 축복받기" : "Get weapon blessed";
            case "oath_cathedral" -> isKorean ? "서약 대성당 방문" : "Visit Oath Cathedral";
            case "light_candles" -> isKorean ? "촛불 7개 점등" : "Light 7 candles";
            case "begin_ceremony" -> isKorean ? "의식 시작" : "Begin ceremony";
            case "final_trial" -> isKorean ? "최종 시련 - 위더 처치" : "Final trial - Defeat Wither";
            case "divine_light" -> isKorean ? "15분간 신성한 빛 견디기" : "Endure divine light for 15 minutes";
            case "paladin_seal" -> isKorean ? "성기사의 인장 획득" : "Obtain Paladin Seal";
            case "complete_oath" -> isKorean ? "서약 완수" : "Complete oath";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("paladin_oath_dialog");

        // 시작
        dialog.addLine("성기사 스승",
                "전사여, 그대의 명성이 여기까지 들려왔소. 더 높은 길을 걷고자 하는가?",
                "Warrior, your reputation has reached here. Do you seek a higher path?");

        dialog.addLine("성기사 스승",
                "성기사의 길은 단순한 힘이 아닌, 신성한 서약과 미덕의 길이오.",
                "The path of paladin is not just about strength, but sacred oaths and virtues.");

        dialog.addLine("플레이어",
                "저는 준비되었습니다. 무엇을 해야 하나요?",
                "I am ready. What must I do?");

        dialog.addLine("성기사 스승",
                "네 가지 미덕을 증명해야 하오. 용기, 정의, 자비, 그리고 희생.",
                "You must prove four virtues. Courage, Justice, Mercy, and Sacrifice.");

        // 용기의 시련
        dialog.addLine("성기사 스승",
                "첫 번째는 용기다. 두려움에 맞서 싸우고 어둠 속에서도 빛을 지켜라.",
                "First is courage. Fight against fear and keep the light even in darkness.");

        // 정의의 시련
        dialog.addLine("정의의 심판관",
                "정의란 무엇인가? 악을 벌하고 선을 보호하는 것이다.",
                "What is justice? To punish evil and protect good.");

        dialog.addLine("정의의 심판관",
                "도둑들이 훔친 것을 되찾아 주인에게 돌려주라. 그것이 정의다.",
                "Retrieve what thieves stole and return to owners. That is justice.");

        // 자비의 시련
        dialog.addLine("성기사 스승",
                "자비는 약자를 돕고 고통받는 자를 치유하는 것이다.",
                "Mercy is helping the weak and healing those who suffer.");

        // 희생의 시련
        dialog.addLine("성기사 스승",
                "마지막 미덕은 희생이다. 남을 위해 자신을 바칠 수 있는가?",
                "The last virtue is sacrifice. Can you give yourself for others?");

        // 신성한 무기
        dialog.addLine("신성 대장장이",
                "성기사에게는 신성한 무기가 필요하지. 축복받은 재료를 가져오게.",
                "A paladin needs a holy weapon. Bring blessed materials.");

        // 최종 의식
        dialog.addLine("성기사 스승",
                "모든 시련을 통과했군. 이제 최종 서약을 할 시간이다.",
                "You've passed all trials. Now it's time for the final oath.");

        dialog.addLine("성기사 스승",
                "위더와의 전투에서 승리하고 신성한 빛을 견뎌낸다면, 진정한 성기사가 될 것이다.",
                "If you defeat the Wither and endure the divine light, you'll become a true paladin.");

        // 완료
        dialog.addLine("성기사 스승",
                "축하한다, 성기사여! 이제 그대는 빛의 수호자다.",
                "Congratulations, Paladin! You are now a guardian of light.");

        dialog.addLine("성기사 스승",
                "이 장비를 받아라. 정의와 자비로 세상을 지키길 바란다.",
                "Take this equipment. May you protect the world with justice and mercy.");

        return dialog;
    }
}