package com.febrie.rpg.quest.impl.event;

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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 할로윈의 밤 - 계절 이벤트 퀘스트
 * 매년 할로윈 시즌에만 진행 가능한 특별 퀘스트
 *
 * @author Febrie
 */
public class HalloweenNightQuest extends Quest {
    
    /**
     * 할로윈 퀘스트 NPC
     */
    public enum NPC implements QuestNPC {
        PUMPKIN_KING(180, "호박 왕", "Pumpkin King"),
        WITCH(181, "마녀", "Witch"),
        PARTY_HOST(182, "파티 주최자", "Party Host"),
        GHOST(183, "유령", "Ghost"),
        PLAYER(-1, "플레이어", "Player");
        
        private final int id;
        private final String nameKo;
        private final String nameEn;
        
        NPC(int id, String nameKo, String nameEn) {
            this.id = id;
            this.nameKo = nameKo;
            this.nameEn = nameEn;
        }
        
        @Override
        public int getId() {
            return id;
        }
        
        @Override
        public String getDisplayName(boolean isKorean) {
            return isKorean ? nameKo : nameEn;
        }
    }

    /**
     * 퀘스트 빌더
     */
    private static class HalloweenNightBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new HalloweenNightQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public HalloweenNightQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private HalloweenNightQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new HalloweenNightBuilder()
                .id(QuestID.SEASON_HALLOWEEN_NIGHT)
                .objectives(Arrays.asList(
                        // 할로윈 시작
                        new InteractNPCObjective("pumpkin_king", 180), // 호박 왕
                        new VisitLocationObjective("haunted_village", "spooky_village"),
                        
                        // 호박 수집
                        new HarvestObjective("harvest_pumpkins", Material.PUMPKIN, 50),
                        new CollectItemObjective("collect_pumpkins", Material.PUMPKIN, 30),
                        new CraftItemObjective("carve_lanterns", Material.JACK_O_LANTERN, 20),
                        new PlaceBlockObjective("decorate_village", Material.JACK_O_LANTERN, 20),
                        
                        // 사탕 만들기
                        new CollectItemObjective("sugar_collect", Material.SUGAR, 50),
                        new CollectItemObjective("cocoa_beans", Material.COCOA_BEANS, 30),
                        new CollectItemObjective("honey_collect", Material.HONEY_BOTTLE, 10),
                        new CraftItemObjective("make_cookies", Material.COOKIE, 64),
                        new CraftItemObjective("make_pies", Material.PUMPKIN_PIE, 20),
                        
                        // 유령의 숲 탐험
                        new VisitLocationObjective("ghost_forest", "haunted_forest"),
                        new KillMobObjective("spooky_zombies", EntityType.ZOMBIE, 50),
                        new KillMobObjective("skeleton_army", EntityType.SKELETON, 40),
                        new KillMobObjective("phantom_spirits", EntityType.PHANTOM, 30),
                        new CollectItemObjective("ghost_essence", Material.GHAST_TEAR, 10),
                        
                        // 마녀의 저택
                        new VisitLocationObjective("witch_mansion", "witchs_manor"),
                        new InteractNPCObjective("witch_greeting", 181), // 마녀
                        new KillMobObjective("witch_cats", EntityType.CAT, 15),
                        new KillMobObjective("evil_witches", EntityType.WITCH, 20),
                        new CollectItemObjective("witch_brew", Material.POTION, 20),
                        new CollectItemObjective("spider_eyes", Material.SPIDER_EYE, 30),
                        
                        // 유령의 시험
                        new VisitLocationObjective("ghost_realm", "spectral_dimension"),
                        new SurviveObjective("ghost_maze", 600), // 10분간 유령 미로
                        new CollectItemObjective("soul_fragments", Material.SOUL_SAND, 20),
                        new KillMobObjective("vengeful_spirits", EntityType.VEX, 40),
                        
                        // 불길한 의식
                        new VisitLocationObjective("ritual_site", "dark_altar"),
                        new PlaceBlockObjective("place_candles", Material.CANDLE, 13),
                        new PlaceBlockObjective("place_skulls", Material.SKELETON_SKULL, 6),
                        new PayCurrencyObjective("ritual_offering", CurrencyType.GOLD, 6666),
                        new KillMobObjective("summoned_demon", EntityType.WITHER_SKELETON, 66),
                        
                        // 호박 왕과의 대결
                        new InteractNPCObjective("challenge_king", 180),
                        new KillMobObjective("pumpkin_minions", EntityType.SNOW_GOLEM, 30),
                        new KillMobObjective("headless_horseman", EntityType.SKELETON_HORSE, 10),
                        new KillMobObjective("pumpkin_king_boss", EntityType.IRON_GOLEM, 3),
                        
                        // 할로윈 파티
                        new CollectItemObjective("party_treats", Material.CAKE, 5),
                        new DeliverItemObjective("deliver_treats", "villager", Material.COOKIE, 32),
                        new DeliverItemObjective("deliver_pies", "villager", Material.PUMPKIN_PIE, 10),
                        new InteractNPCObjective("halloween_party", 182), // 파티 주최자
                        
                        // 보상 수령
                        new CollectItemObjective("halloween_mask", Material.CARVED_PUMPKIN, 1),
                        new InteractNPCObjective("event_complete", 180)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 13000)
                        .addCurrency(CurrencyType.DIAMOND, 66)
                        .addItem(new ItemStack(Material.CARVED_PUMPKIN)) // 특별 할로윈 마스크
                        .addItem(new ItemStack(Material.SKELETON_SKULL, 3))
                        .addItem(new ItemStack(Material.ZOMBIE_HEAD, 3))
                        .addItem(new ItemStack(Material.CREEPER_HEAD, 3))
                        .addItem(new ItemStack(Material.BAT_SPAWN_EGG, 5))
                        .addItem(new ItemStack(Material.JACK_O_LANTERN, 20))
                        .addExperience(6666)
                        .build())
                .sequential(true)
                .repeatable(true)  // 매년 반복 가능
                .category(QuestCategory.EVENT)
                .minLevel(15)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "할로윈의 저주받은 밤" : "The Cursed Night of Halloween";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "일년에 한 번, 유령들이 깨어나는 할로윈의 밤!",
                    "호박 왕의 도전을 받아들이고 마을을 구하세요.",
                    "",
                    "🎃 할로윈 이벤트:",
                    "• 기간: 10월 25일 - 11월 1일",
                    "• 특별 몬스터 출현",
                    "• 할로윈 전용 보상",
                    "• 으스스한 분위기 연출",
                    "",
                    "주요 활동:",
                    "• 호박 농사와 잭오랜턴 제작",
                    "• 할로윈 사탕 만들기",
                    "• 유령의 숲 탐험",
                    "• 마녀의 저택 방문",
                    "• 유령 차원 도전",
                    "• 불길한 의식 수행",
                    "• 호박 왕과의 최종 대결",
                    "• 할로윈 파티 참가",
                    "",
                    "특별 도전:",
                    "• 유령 미로 10분 생존",
                    "• 악마 66마리 처치",
                    "• 머리 없는 기수 퇴치",
                    "",
                    "목표:",
                    "• 호박 50개 수확",
                    "• 잭오랜턴 20개 제작",
                    "• 할로윈 과자 제작",
                    "• 유령과 언데드 처치",
                    "• 마녀의 시험 통과",
                    "• 호박 왕 격파",
                    "• 마을 파티 성공",
                    "",
                    "보상:",
                    "• 골드 13,000",
                    "• 다이아몬드 66개",
                    "• 특별 할로윈 마스크",
                    "• 몬스터 머리 9개",
                    "• 박쥐 스폰 에그 5개",
                    "• 잭오랜턴 20개",
                    "• 경험치 6,666"
            );
        } else {
            return Arrays.asList(
                    "Once a year, on Halloween night when ghosts awaken!",
                    "Accept the Pumpkin King's challenge and save the village.",
                    "",
                    "🎃 Halloween Event:",
                    "• Period: October 25 - November 1",
                    "• Special monster spawns",
                    "• Halloween exclusive rewards",
                    "• Spooky atmosphere",
                    "",
                    "Main Activities:",
                    "• Pumpkin farming and Jack o'Lantern crafting",
                    "• Making Halloween candy",
                    "• Exploring the Ghost Forest",
                    "• Visiting the Witch's Mansion",
                    "• Challenging the Ghost Dimension",
                    "• Performing dark rituals",
                    "• Final battle with Pumpkin King",
                    "• Joining Halloween party",
                    "",
                    "Special Challenges:",
                    "• Survive ghost maze for 10 minutes",
                    "• Defeat 66 demons",
                    "• Vanquish the Headless Horseman",
                    "",
                    "Objectives:",
                    "• Harvest 50 pumpkins",
                    "• Craft 20 Jack o'Lanterns",
                    "• Make Halloween treats",
                    "• Defeat ghosts and undead",
                    "• Pass witch's trials",
                    "• Defeat Pumpkin King",
                    "• Successful village party",
                    "",
                    "Rewards:",
                    "• 13,000 Gold",
                    "• 66 Diamonds",
                    "• Special Halloween Mask",
                    "• 9 Monster Heads",
                    "• 5 Bat Spawn Eggs",
                    "• 20 Jack o'Lanterns",
                    "• 6,666 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "pumpkin_king" -> isKorean ? "호박 왕과 대화" : "Talk to the Pumpkin King";
            case "haunted_village" -> isKorean ? "유령 마을 방문" : "Visit the Haunted Village";
            case "harvest_pumpkins" -> isKorean ? "호박 50개 수확" : "Harvest 50 pumpkins";
            case "collect_pumpkins" -> isKorean ? "호박 30개 수집" : "Collect 30 pumpkins";
            case "carve_lanterns" -> isKorean ? "잭오랜턴 20개 조각" : "Carve 20 Jack o'Lanterns";
            case "decorate_village" -> isKorean ? "마을에 잭오랜턴 20개 설치" : "Place 20 Jack o'Lanterns in village";
            case "sugar_collect" -> isKorean ? "설탕 50개 수집" : "Collect 50 sugar";
            case "cocoa_beans" -> isKorean ? "코코아 콩 30개 수집" : "Collect 30 cocoa beans";
            case "honey_collect" -> isKorean ? "꿀병 10개 수집" : "Collect 10 honey bottles";
            case "make_cookies" -> isKorean ? "쿠키 64개 제작" : "Make 64 cookies";
            case "make_pies" -> isKorean ? "호박 파이 20개 제작" : "Make 20 pumpkin pies";
            case "ghost_forest" -> isKorean ? "유령의 숲 진입" : "Enter the Ghost Forest";
            case "spooky_zombies" -> isKorean ? "으스스한 좀비 50마리 처치" : "Kill 50 spooky zombies";
            case "skeleton_army" -> isKorean ? "스켈레톤 군단 40마리 처치" : "Kill 40 skeleton army";
            case "phantom_spirits" -> isKorean ? "팬텀 영혼 30마리 처치" : "Kill 30 phantom spirits";
            case "ghost_essence" -> isKorean ? "유령의 정수 10개 수집" : "Collect 10 ghost essence";
            case "witch_mansion" -> isKorean ? "마녀의 저택 방문" : "Visit Witch's Mansion";
            case "witch_greeting" -> isKorean ? "마녀와 인사" : "Greet the Witch";
            case "witch_cats" -> isKorean ? "마녀의 고양이 15마리 처치" : "Kill 15 witch's cats";
            case "evil_witches" -> isKorean ? "사악한 마녀 20명 처치" : "Kill 20 evil witches";
            case "witch_brew" -> isKorean ? "마녀의 물약 20개 수집" : "Collect 20 witch's brew";
            case "spider_eyes" -> isKorean ? "거미 눈 30개 수집" : "Collect 30 spider eyes";
            case "ghost_realm" -> isKorean ? "유령 차원 진입" : "Enter Ghost Realm";
            case "ghost_maze" -> isKorean ? "유령 미로 10분간 생존" : "Survive ghost maze for 10 minutes";
            case "soul_fragments" -> isKorean ? "영혼 조각 20개 수집" : "Collect 20 soul fragments";
            case "vengeful_spirits" -> isKorean ? "복수의 영혼 40마리 처치" : "Kill 40 vengeful spirits";
            case "ritual_site" -> isKorean ? "의식 장소 도착" : "Arrive at ritual site";
            case "place_candles" -> isKorean ? "양초 13개 배치" : "Place 13 candles";
            case "place_skulls" -> isKorean ? "해골 6개 배치" : "Place 6 skulls";
            case "ritual_offering" -> isKorean ? "의식 제물 6,666골드" : "Ritual offering 6,666 gold";
            case "summoned_demon" -> isKorean ? "소환된 악마 66마리 처치" : "Kill 66 summoned demons";
            case "challenge_king" -> isKorean ? "호박 왕에게 도전" : "Challenge the Pumpkin King";
            case "pumpkin_minions" -> isKorean ? "호박 부하 30마리 처치" : "Kill 30 pumpkin minions";
            case "headless_horseman" -> isKorean ? "머리 없는 기수 10명 처치" : "Kill 10 headless horsemen";
            case "pumpkin_king_boss" -> isKorean ? "호박 왕 3마리 처치" : "Kill 3 Pumpkin Kings";
            case "party_treats" -> isKorean ? "파티 케이크 5개 준비" : "Prepare 5 party cakes";
            case "deliver_treats" -> isKorean ? "마을 주민에게 쿠키 전달" : "Deliver cookies to villagers";
            case "deliver_pies" -> isKorean ? "마을 주민에게 파이 전달" : "Deliver pies to villagers";
            case "halloween_party" -> isKorean ? "할로윈 파티 참가" : "Join Halloween party";
            case "halloween_mask" -> isKorean ? "할로윈 마스크 획득" : "Obtain Halloween mask";
            case "event_complete" -> isKorean ? "이벤트 완료 보고" : "Report event completion";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        // 기존 getDialog() 메소드는 하위 호환성을 위해 유지
        QuestDialog dialog = new QuestDialog("halloween_night_dialog");

        // 시작
        dialog.addLine("호박 왕",
                "후하하하! 또 다시 할로윈의 밤이 찾아왔다! 필멸자여, 나의 도전을 받아들이겠나?",
                "Muahahaha! Halloween night has come again! Mortal, will you accept my challenge?");

        dialog.addLine("호박 왕",
                "이 마을은 저주받았다. 오직 용감한 자만이 저주를 풀 수 있지!",
                "This village is cursed. Only the brave can break the curse!");

        dialog.addLine("플레이어",
                "무엇을 해야 하나요?",
                "What must I do?");

        dialog.addLine("호박 왕",
                "먼저 호박을 수확하고 잭오랜턴으로 마을을 밝혀라. 그것이 시작이다.",
                "First harvest pumpkins and light the village with Jack o'Lanterns. That's the beginning.");

        // 마녀와의 만남
        dialog.addLine("마녀",
                "히히히... 또 다른 방문객이군. 내 시험을 통과할 수 있겠나?",
                "Hehehe... another visitor. Can you pass my test?");

        dialog.addLine("마녀",
                "내 고양이들을 건드리지 마라! 아니면... 저주받을 것이다!",
                "Don't touch my cats! Or else... you'll be cursed!");

        // 유령의 숲
        dialog.addLine("유령",
                "우우우... 살아있는 자여... 왜 우리의 영역에 왔는가...",
                "Ooooh... living one... why have you come to our realm...");

        // 최종 대결
        dialog.addLine("호박 왕",
                "인상적이군! 하지만 이제 진짜 시험이 시작된다!",
                "Impressive! But now the real test begins!");

        dialog.addLine("호박 왕",
                "나와 내 부하들을 물리칠 수 있다면, 이 마을의 저주가 풀릴 것이다!",
                "If you can defeat me and my minions, this village's curse will be lifted!");

        // 파티
        dialog.addLine("파티 주최자",
                "해냈어요! 마을이 구원받았습니다! 이제 축하 파티를 열 시간이에요!",
                "You did it! The village is saved! Now it's time for a celebration party!");

        // 완료
        dialog.addLine("호박 왕",
                "놀랍군... 정말로 해냈구나. 이 할로윈 마스크를 받아라. 용기의 증표다.",
                "Amazing... you really did it. Take this Halloween mask. It's a token of courage.");

        dialog.addLine("호박 왕",
                "내년 할로윈에 다시 만나자! 후하하하!",
                "See you again next Halloween! Muahahaha!");

        return dialog;
    }
    
    @Override
    @Nullable
    public List<QuestDialog.DialogLine> getDialogSequence() {
        List<QuestDialog.DialogLine> sequence = new ArrayList<>();
        
        // 호박 왕 대화
        sequence.add(new QuestDialog.DialogLine(NPC.PUMPKIN_KING.getDisplayName(true), 
                "후하하하! 또 다시 할로윈의 밤이 찾아왔다! 필멸자여, 나의 도전을 받아들이겠나?",
                "Muahahaha! Halloween night has come again! Mortal, will you accept my challenge?", null));
        
        sequence.add(new QuestDialog.DialogLine(NPC.PUMPKIN_KING.getDisplayName(true),
                "이 마을은 저주받았다. 오직 용감한 자만이 저주를 풀 수 있지!",
                "This village is cursed. Only the brave can break the curse!", null));
        
        sequence.add(new QuestDialog.DialogLine(NPC.PLAYER.getDisplayName(true),
                "무엇을 해야 하나요?",
                "What must I do?", null));
        
        sequence.add(new QuestDialog.DialogLine(NPC.PUMPKIN_KING.getDisplayName(true),
                "먼저 호박을 수확하고 잭오랜턴으로 마을을 밝혀라. 그것이 시작이다.",
                "First harvest pumpkins and light the village with Jack o'Lanterns. That's the beginning.", null));
        
        // 마녀 대화
        sequence.add(new QuestDialog.DialogLine(NPC.WITCH.getDisplayName(true),
                "히히히... 또 다른 방문객이군. 내 시험을 통과할 수 있겠나?",
                "Hehehe... another visitor. Can you pass my test?", null));
        
        sequence.add(new QuestDialog.DialogLine(NPC.WITCH.getDisplayName(true),
                "내 고양이들을 건드리지 마라! 아니면... 저주받을 것이다!",
                "Don't touch my cats! Or else... you'll be cursed!", null));
        
        // 유령 대화
        sequence.add(new QuestDialog.DialogLine(NPC.GHOST.getDisplayName(true),
                "우우우... 살아있는 자여... 왜 우리의 영역에 왔는가...",
                "Ooooh... living one... why have you come to our realm...", null));
        
        // 호박 왕 최종 대결
        sequence.add(new QuestDialog.DialogLine(NPC.PUMPKIN_KING.getDisplayName(true),
                "인상적이군! 하지만 이제 진짜 시험이 시작된다!",
                "Impressive! But now the real test begins!", null));
        
        sequence.add(new QuestDialog.DialogLine(NPC.PUMPKIN_KING.getDisplayName(true),
                "나와 내 부하들을 물리칠 수 있다면, 이 마을의 저주가 풀릴 것이다!",
                "If you can defeat me and my minions, this village's curse will be lifted!", null));
        
        // 파티 주최자 대화
        sequence.add(new QuestDialog.DialogLine(NPC.PARTY_HOST.getDisplayName(true),
                "해냈어요! 마을이 구원받았습니다! 이제 축하 파티를 열 시간이에요!",
                "You did it! The village is saved! Now it's time for a celebration party!", null));
        
        // 호박 왕 완료 대화
        sequence.add(new QuestDialog.DialogLine(NPC.PUMPKIN_KING.getDisplayName(true),
                "놀랍군... 정말로 해냈구나. 이 할로윈 마스크를 받아라. 용기의 증표다.",
                "Amazing... you really did it. Take this Halloween mask. It's a token of courage.", null));
        
        sequence.add(new QuestDialog.DialogLine(NPC.PUMPKIN_KING.getDisplayName(true),
                "내년 할로윈에 다시 만나자! 후하하하!",
                "See you again next Halloween! Muahahaha!", null));
        
        return sequence;
    }
    
    @Override
    @Nullable
    public List<QuestDialog.DialogLine> getNPCDialogs(int npcId) {
        List<QuestDialog.DialogLine> dialogs = new ArrayList<>();
        List<QuestDialog.DialogLine> allDialogs = getDialogSequence();
        
        if (allDialogs == null) return null;
        
        // 특정 NPC ID에 해당하는 대화만 필터링
        for (NPC npc : NPC.values()) {
            if (npc.getId() == npcId) {
                String npcName = npc.getDisplayName(true);
                for (QuestDialog.DialogLine line : allDialogs) {
                    if (line.getSpeaker().equals(npcName) || 
                        line.getSpeaker().equals(npc.getDisplayName(false))) {
                        dialogs.add(line);
                    }
                }
                break;
            }
        }
        
        return dialogs.isEmpty() ? null : dialogs;
    }
}