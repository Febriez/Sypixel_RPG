package com.febrie.rpg.quest.impl.guild;

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
 * 길드 설립 - 길드 퀘스트
 * 자신만의 길드를 창설하고 길드 마스터가 되는 퀘스트
 *
 * @author Febrie
 */
public class GuildEstablishmentQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class GuildEstablishmentBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new GuildEstablishmentQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public GuildEstablishmentQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private GuildEstablishmentQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new GuildEstablishmentBuilder()
                .id(QuestID.GUILD_ESTABLISHMENT)
                .objectives(Arrays.asList(
                        // 준비 단계
                        new InteractNPCObjective("guild_registrar", 116), // 길드 등록관
                        new ReachLevelObjective("level_requirement", 25),
                        new PayCurrencyObjective("registration_fee", CurrencyType.GOLD, 5000),
                        
                        // 길드 깃발 제작
                        new CollectItemObjective("gather_wool", Material.WHITE_WOOL, 6),
                        new CollectItemObjective("gather_stick", Material.STICK, 1),
                        new CollectItemObjective("gather_dyes", Material.LAPIS_LAZULI, 3),
                        new CraftItemObjective("craft_banner", Material.WHITE_BANNER, 1),
                        
                        // 길드 홀 준비
                        new VisitLocationObjective("guild_district", "guild_district"),
                        new CollectItemObjective("gather_gold", Material.GOLD_BLOCK, 10),
                        new CollectItemObjective("gather_emerald", Material.EMERALD_BLOCK, 5),
                        new PayCurrencyObjective("hall_rental", CurrencyType.GOLD, 10000),
                        
                        // 길드 멤버 모집
                        new InteractNPCObjective("recruit_npc1", 117), // 첫 번째 지원자
                        new InteractNPCObjective("recruit_npc2", 118), // 두 번째 지원자
                        new InteractNPCObjective("recruit_npc3", 119), // 세 번째 지원자
                        new CollectItemObjective("member_contracts", Material.PAPER, 5),
                        
                        // 길드 창설 문서
                        new CollectItemObjective("guild_seal", Material.GOLD_NUGGET, 1),
                        new CraftItemObjective("guild_charter", Material.WRITTEN_BOOK, 1),
                        new DeliverItemObjective("submit_charter", "guild_registrar", Material.WRITTEN_BOOK, 1),
                        
                        // 길드 홀 설치
                        new VisitLocationObjective("guild_hall", "your_guild_hall"),
                        new PlaceBlockObjective("place_banner", Material.WHITE_BANNER, 1),
                        new PlaceBlockObjective("place_chest", Material.CHEST, 3),
                        new PlaceBlockObjective("place_furnace", Material.FURNACE, 2),
                        new PlaceBlockObjective("place_table", Material.CRAFTING_TABLE, 2),
                        
                        // 첫 길드 미션
                        new KillMobObjective("first_mission", EntityType.PILLAGER, 20),
                        new CollectItemObjective("mission_reward", Material.EMERALD, 30),
                        new DeliverItemObjective("complete_mission", "guild_registrar", Material.EMERALD, 30),
                        
                        // 길드 승인
                        new InteractNPCObjective("final_approval", 116)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 20000)
                        .addCurrency(CurrencyType.DIAMOND, 100)
                        .addItem(new ItemStack(Material.BEACON)) // 길드 신호기
                        .addItem(new ItemStack(Material.ENDER_CHEST, 3))
                        .addItem(new ItemStack(Material.SHULKER_BOX, 2))
                        .addItem(new ItemStack(Material.WRITTEN_BOOK)) // 길드 마스터 가이드
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.GUILD)
                .minLevel(25)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "길드 창설의 길" : "Path of Guild Foundation";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "자신만의 길드를 창설하고 길드 마스터가 되세요!",
                    "동료들과 함께 더 큰 모험을 시작할 수 있습니다.",
                    "",
                    "🏰 길드 혜택:",
                    "• 전용 길드 홀",
                    "• 길드 창고 시스템",
                    "• 길드 전용 퀘스트",
                    "• 길드원 간 거래 수수료 면제",
                    "• 길드 레이드 참여 가능",
                    "",
                    "설립 과정:",
                    "• 1단계: 등록 및 준비",
                    "• 2단계: 길드 깃발 제작",
                    "• 3단계: 길드 홀 임대",
                    "• 4단계: 멤버 모집",
                    "• 5단계: 길드 창설 문서 작성",
                    "• 6단계: 길드 홀 꾸미기",
                    "• 7단계: 첫 길드 미션 완수",
                    "",
                    "목표:",
                    "• 길드 등록관과 대화",
                    "• 레벨 25 달성",
                    "• 등록비 지불",
                    "• 길드 깃발 제작",
                    "• 길드 홀 임대",
                    "• 멤버 5명 모집",
                    "• 길드 창설 문서 제출",
                    "• 길드 홀 설치",
                    "• 첫 미션 완수",
                    "",
                    "보상:",
                    "• 골드 20,000",
                    "• 다이아몬드 100개",
                    "• 길드 신호기",
                    "• 엔더 상자 3개",
                    "• 셜커 상자 2개",
                    "• 길드 마스터 가이드북",
                    "• 경험치 5,000"
            );
        } else {
            return Arrays.asList(
                    "Create your own guild and become a Guild Master!",
                    "Start bigger adventures with your companions.",
                    "",
                    "🏰 Guild Benefits:",
                    "• Exclusive Guild Hall",
                    "• Guild Storage System",
                    "• Guild-exclusive Quests",
                    "• No trade fees between members",
                    "• Access to Guild Raids",
                    "",
                    "Foundation Process:",
                    "• Stage 1: Registration and Preparation",
                    "• Stage 2: Guild Banner Creation",
                    "• Stage 3: Guild Hall Rental",
                    "• Stage 4: Member Recruitment",
                    "• Stage 5: Guild Charter Documentation",
                    "• Stage 6: Guild Hall Setup",
                    "• Stage 7: First Guild Mission",
                    "",
                    "Objectives:",
                    "• Talk to Guild Registrar",
                    "• Reach Level 25",
                    "• Pay registration fee",
                    "• Craft guild banner",
                    "• Rent guild hall",
                    "• Recruit 5 members",
                    "• Submit guild charter",
                    "• Set up guild hall",
                    "• Complete first mission",
                    "",
                    "Rewards:",
                    "• 20,000 Gold",
                    "• 100 Diamonds",
                    "• Guild Beacon",
                    "• 3 Ender Chests",
                    "• 2 Shulker Boxes",
                    "• Guild Master Guidebook",
                    "• 5,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "guild_registrar" -> isKorean ? "길드 등록관과 대화" : "Talk to the Guild Registrar";
            case "level_requirement" -> isKorean ? "레벨 25 달성" : "Reach Level 25";
            case "registration_fee" -> isKorean ? "등록비 5,000골드 지불" : "Pay 5,000 gold registration fee";
            case "gather_wool" -> isKorean ? "하얀 양털 6개 수집" : "Gather 6 White Wool";
            case "gather_stick" -> isKorean ? "막대기 1개 수집" : "Gather 1 Stick";
            case "gather_dyes" -> isKorean ? "청금석 3개 수집" : "Gather 3 Lapis Lazuli";
            case "craft_banner" -> isKorean ? "길드 깃발 제작" : "Craft Guild Banner";
            case "guild_district" -> isKorean ? "길드 구역 방문" : "Visit Guild District";
            case "gather_gold" -> isKorean ? "금 블록 10개 수집" : "Gather 10 Gold Blocks";
            case "gather_emerald" -> isKorean ? "에메랄드 블록 5개 수집" : "Gather 5 Emerald Blocks";
            case "hall_rental" -> isKorean ? "길드 홀 임대료 10,000골드 지불" : "Pay 10,000 gold hall rental";
            case "recruit_npc1" -> isKorean ? "첫 번째 지원자와 면접" : "Interview first applicant";
            case "recruit_npc2" -> isKorean ? "두 번째 지원자와 면접" : "Interview second applicant";
            case "recruit_npc3" -> isKorean ? "세 번째 지원자와 면접" : "Interview third applicant";
            case "member_contracts" -> isKorean ? "멤버 계약서 5장 수집" : "Gather 5 Member Contracts";
            case "guild_seal" -> isKorean ? "길드 인장 획득" : "Obtain Guild Seal";
            case "guild_charter" -> isKorean ? "길드 창설 문서 작성" : "Write Guild Charter";
            case "submit_charter" -> isKorean ? "창설 문서 제출" : "Submit Charter";
            case "guild_hall" -> isKorean ? "길드 홀 방문" : "Visit Your Guild Hall";
            case "place_banner" -> isKorean ? "길드 깃발 설치" : "Place Guild Banner";
            case "place_chest" -> isKorean ? "상자 3개 설치" : "Place 3 Chests";
            case "place_furnace" -> isKorean ? "화로 2개 설치" : "Place 2 Furnaces";
            case "place_table" -> isKorean ? "제작대 2개 설치" : "Place 2 Crafting Tables";
            case "first_mission" -> isKorean ? "약탈자 20마리 처치" : "Kill 20 Pillagers";
            case "mission_reward" -> isKorean ? "에메랄드 30개 수집" : "Collect 30 Emeralds";
            case "complete_mission" -> isKorean ? "미션 보상 제출" : "Submit Mission Rewards";
            case "final_approval" -> isKorean ? "최종 승인 받기" : "Receive Final Approval";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("guild_establishment_dialog");

        // 시작 대화
        dialog.addLine("길드 등록관",
                "길드를 창설하고 싶으신가요? 훌륭한 결정입니다!",
                "Do you wish to establish a guild? Excellent decision!");

        dialog.addLine("길드 등록관",
                "길드 마스터가 되는 것은 큰 책임이 따릅니다. 준비되셨나요?",
                "Being a Guild Master comes with great responsibility. Are you ready?");

        dialog.addLine("플레이어",
                "네, 준비되었습니다. 무엇부터 시작해야 하나요?",
                "Yes, I'm ready. Where do I start?");

        dialog.addLine("길드 등록관",
                "먼저 레벨 25가 되어야 하고, 등록비 5,000골드가 필요합니다.",
                "First, you need to be level 25 and pay a 5,000 gold registration fee.");

        // 길드 깃발 제작
        dialog.addLine("길드 등록관",
                "길드의 상징이 될 깃발을 만들어야 합니다. 재료를 모아오세요.",
                "You need to create a banner that will symbolize your guild. Gather the materials.");

        // 멤버 모집
        dialog.addLine("지원자 1",
                "길드에 가입하고 싶습니다! 저는 훌륭한 전사입니다.",
                "I want to join your guild! I'm an excellent warrior.");

        dialog.addLine("지원자 2",
                "저는 숙련된 마법사입니다. 길드에 도움이 될 거예요.",
                "I'm a skilled mage. I'll be helpful to the guild.");

        dialog.addLine("지원자 3",
                "치유사로서 길드를 지원하고 싶습니다.",
                "I want to support the guild as a healer.");

        // 첫 미션
        dialog.addLine("길드 등록관",
                "첫 길드 미션입니다. 근처 마을을 약탈하는 약탈자들을 처치하세요.",
                "Your first guild mission. Eliminate the pillagers raiding nearby villages.");

        dialog.addLine("길드 등록관",
                "미션을 완수하면 길드로서 인정받을 수 있습니다.",
                "Completing this mission will prove your worth as a guild.");

        // 완료
        dialog.addLine("길드 등록관",
                "축하합니다! 이제 공식적으로 길드 마스터입니다.",
                "Congratulations! You are now officially a Guild Master.");

        dialog.addLine("길드 등록관",
                "이 가이드북을 받으세요. 길드 운영에 도움이 될 겁니다.",
                "Take this guidebook. It will help you manage your guild.");

        return dialog;
    }
}