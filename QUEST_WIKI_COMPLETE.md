# Sypixel RPG 완전 퀘스트 정보

이 문서는 Sypixel RPG의 모든 퀘스트 정보를 포함합니다. 모든 퀘스트 ID, 카테고리, 목표, 보상, NPC, 대화 등이 상세히 기록되어 있습니다.

## 퀘스트 카테고리
- TUTORIAL: 튜토리얼 퀘스트
- MAIN: 메인 스토리 퀘스트  
- SIDE: 서브 퀘스트
- DAILY: 일일 퀘스트 (매일 오전 6시 리셋)
- WEEKLY: 주간 퀘스트 (매주 월요일 오전 6시 리셋)
- SPECIAL: 특별 퀘스트
- GUILD: 길드 퀘스트
- EVENT: 이벤트 퀘스트
- COMBAT: 전투 퀘스트
- LIFE: 생활 퀘스트
- EXPLORATION: 탐험 퀘스트
- CRAFTING: 제작 퀘스트
- ADVANCEMENT: 발전 퀘스트
- BRANCH: 분기 퀘스트

## 목표 타입
- KILL: 몬스터 처치
- COLLECT: 아이템 수집
- VISIT_LOCATION: 특정 위치 방문
- INTERACT_NPC: NPC와 상호작용
- CRAFT: 아이템 제작
- PLACE: 블록 설치
- SURVIVE: 일정 시간 생존
- DELIVER: 아이템 전달
- PAY: 화폐 지불
- REACH_LEVEL: 레벨 도달
- KILL_PLAYER: 플레이어 처치

## 전체 퀘스트 목록

### 1. 첫 걸음 (First Steps)
퀘스트 ID: TUTORIAL_FIRST_STEPS
카테고리: TUTORIAL
레벨 요구사항: 1
선행 퀘스트: 없음
반복 가능: 아니오
진행 방식: 순차적 (목표를 순서대로 완료해야 함)
한국어 이름: 첫 걸음
영어 이름: First Steps
한국어 설명: 서버의 기본적인 기능을 배우는 첫 번째 튜토리얼 퀘스트입니다.
영어 설명: Learn the basics of the server in this first tutorial quest.
퀘스트 부여 NPC: 튜토리얼 가이드 (스폰 지점)
보상 수령 NPC: 튜토리얼 가이드 (동일 NPC)

목표:
1. 허브(Hub) 지역 방문 - 위치: X:0, Y:64, Z:0, 반경 10블록
2. 마을 상인(village_merchant) NPC와 대화

보상:
- 경험치: 50
- 돈: 100골드
- 아이템: 나무 검(Wooden Sword) 1개, 나무 곡괭이(Wooden Pickaxe) 1개, 나무 도끼(Wooden Axe) 1개, 빵(Bread) 10개

대화:
- 시작: "안녕하세요, {player}님! Sypixel RPG 세계에 오신 것을 환영합니다. 저는 당신의 여정을 도와드릴 가이드입니다."
- 진행중: "아직 목표를 완료하지 못했군요. 허브로 가서 마을 상인을 만나보세요!"
- 완료: "훌륭합니다! 첫 걸음을 성공적으로 마치셨군요. 이제 본격적인 모험을 시작해보세요!"

### 2. 기초 전투 (Basic Combat)
퀘스트 ID: TUTORIAL_BASIC_COMBAT
카테고리: TUTORIAL
레벨 요구사항: 1
선행 퀘스트: TUTORIAL_FIRST_STEPS
반복 가능: 아니오
진행 방식: 비순차적 (목표를 아무 순서로나 완료 가능)
한국어 이름: 기초 전투
영어 이름: Basic Combat
한국어 설명: 전투의 기초를 배우고 첫 장비를 획득하는 퀘스트입니다.
영어 설명: Learn the basics of combat and earn your first equipment.
퀘스트 부여 NPC: 전투 교관 (훈련장)
보상 수령 NPC: 전투 교관 (동일 NPC)

목표:
1. 좀비(ZOMBIE) 5마리 처치
2. 스켈레톤(SKELETON) 3마리 처치

보상:
- 경험치: 100
- 돈: 200골드
- 아이템: 철 검(Iron Sword) 1개, 철 흉갑(Iron Chestplate) 1개, 구운 소고기(Cooked Beef) 20개

대화:
- 시작: "좋습니다, {player}! 이제 실전 훈련을 해볼 시간입니다. 마을 주변의 몬스터들을 처치해보세요."
- 진행중: "계속 싸우세요! 좀비 {killed_zombies}/5, 스켈레톤 {killed_skeletons}/3"
- 완료: "훌륭합니다! 당신은 타고난 전사군요. 이 장비를 가져가세요, 앞으로의 여정에 도움이 될 겁니다."

### 3. 영웅의 여정 (Hero's Journey)
퀘스트 ID: MAIN_HEROES_JOURNEY
카테고리: MAIN
레벨 요구사항: 5
선행 퀘스트: TUTORIAL_BASIC_COMBAT
반복 가능: 아니오
진행 방식: 비순차적
한국어 이름: 영웅의 여정
영어 이름: Hero's Journey
한국어 설명: 진정한 영웅이 되기 위한 첫 걸음. 다양한 도전 과제를 완수하여 실력을 증명하세요.
영어 설명: The first step to becoming a true hero. Complete various challenges to prove your skills.
퀘스트 부여 NPC: 영웅 길드 마스터 (영웅 길드 홀)
보상 수령 NPC: 영웅 길드 마스터 (동일 NPC)

목표:
1. 좀비(ZOMBIE) 10마리 처치
2. 스켈레톤(SKELETON) 10마리 처치
3. 크리퍼(CREEPER) 5마리 처치
4. 철 주괴(IRON_INGOT) 20개 수집
5. 금 주괴(GOLD_INGOT) 10개 수집
6. 다이아몬드 장비 1개 제작 (DIAMOND_SWORD, DIAMOND_PICKAXE, DIAMOND_AXE, DIAMOND_HOE, DIAMOND_SHOVEL 중 아무거나)

보상:
- 경험치: 500
- 돈: 500골드
- 아이템: 다이아몬드(Diamond) 5개, 마법 부여대(Enchanting Table) 1개

대화:
- 시작: "당신에게서 영웅의 자질이 보입니다, {player}. 하지만 진정한 영웅이 되려면 더 많은 시련을 겪어야 합니다."
- 진행중: "계속 노력하세요! 당신의 진척도: 몬스터 처치 {total_kills}/25, 자원 수집 {total_resources}/30"
- 완료: "정말 인상적입니다! 당신은 진정한 영웅의 길을 걷고 있습니다. 이 보상을 받으세요."

### 4. 고대의 예언 (The Ancient Prophecy)
퀘스트 ID: MAIN_ANCIENT_PROPHECY
카테고리: MAIN
레벨 요구사항: 10
선행 퀘스트: MAIN_HEROES_JOURNEY
반복 가능: 아니오
진행 방식: 순차적
한국어 이름: 고대의 예언
영어 이름: The Ancient Prophecy
한국어 설명: 오래된 예언서에 기록된 운명을 따라 고대의 비밀을 밝혀내세요.
영어 설명: Follow the destiny written in ancient prophecies and uncover ancient secrets.
퀘스트 부여 NPC: 장로 (고대 도서관)
보상 수령 NPC: 장로 (동일 NPC)

목표:
1. 고대 사원(Ancient Temple) 방문 - 위치: X:500, Y:80, Z:-300, 반경 20블록
2. 고대 두루마리(PAPER) 3개 수집
3. 장로(elder_sage) NPC에게 고대 두루마리 3개 전달

보상:
- 경험치: 1000
- 돈: 500골드
- 아이템: 다이아몬드(Diamond) 10개, 효율 III 마법이 부여된 책(Enchanted Book - Efficiency III) 1개

대화:
- 시작: "{player}여, 고대의 예언이 당신을 가리키고 있습니다. 사원에서 두루마리를 찾아 진실을 밝혀주세요."
- 진행중: "고대 사원의 비밀은 깊습니다. 계속 탐색하세요. 두루마리 {scrolls_collected}/3"
- 완료: "놀랍습니다! 예언이 실현되었군요. 당신이 바로 선택받은 자입니다."

### 5. 약초 수집 (Collect Herbs)
퀘스트 ID: SIDE_COLLECT_HERBS
카테고리: SIDE
레벨 요구사항: 5
선행 퀘스트: TUTORIAL_BASIC_COMBAT
반복 가능: 아니오
진행 방식: 순차적
한국어 이름: 약초 수집
영어 이름: Collect Herbs
한국어 설명: 마을 연금술사가 포션을 만들기 위한 재료를 구하고 있습니다. 필요한 약초를 모아주세요.
영어 설명: The village alchemist needs ingredients for potions. Collect the required herbs.
퀘스트 부여 NPC: 연금술사 (마을 포션 상점)
보상 수령 NPC: 연금술사 (동일 NPC)

목표:
1. 민들레(DANDELION) 5개 수집
2. 양귀비(POPPY) 5개 수집
3. 푸른 난초(BLUE_ORCHID) 3개 수집
4. 거미 눈(SPIDER_EYE) 5개 수집
5. 연금술사(village_alchemist) NPC에게 수집한 아이템 모두 전달

보상:
- 경험치: 150
- 돈: 250골드
- 아이템: 에메랄드(Emerald) 8개, 치유의 포션 II(Potion of Healing II) 3개, 신속의 포션 II(Potion of Swiftness II) 2개

대화:
- 시작: "아, {player}! 때마침 잘 오셨어요. 포션 재료가 떨어져서 걱정이었는데... 약초를 좀 구해주실 수 있나요?"
- 진행중: "약초 수집은 어떻게 되고 있나요? 신선한 재료가 포션의 효과를 좌우한답니다."
- 완료: "완벽해요! 이 재료들로 훌륭한 포션을 만들 수 있겠네요. 감사의 의미로 이것을 드릴게요."

### 6. 농부의 부탁 (Farmer's Request)
퀘스트 ID: SIDE_FARMERS_REQUEST
카테고리: SIDE
레벨 요구사항: 3
선행 퀘스트: TUTORIAL_BASIC_COMBAT
반복 가능: 아니오
진행 방식: 순차적
한국어 이름: 농부의 부탁
영어 이름: Farmer's Request
한국어 설명: 농부가 수확철을 맞아 도움이 필요합니다. 작물을 수확하고 배달을 도와주세요.
영어 설명: The farmer needs help during harvest season. Help harvest and deliver crops.
퀘스트 부여 NPC: 농부 (마을 농장)
보상 수령 NPC: 농부 (동일 NPC)

목표:
1. 밀(WHEAT) 32개 수집
2. 당근(CARROT) 24개 수집
3. 감자(POTATO) 24개 수집
4. 농부(village_farmer) NPC에게 수집한 작물 모두 전달

보상:
- 경험치: 200
- 돈: 300골드
- 아이템: 에메랄드(Emerald) 10개, 황금 괭이(Golden Hoe) 1개, 뼛가루(Bone Meal) 64개

대화:
- 시작: "이런, {player}님! 수확철인데 일손이 부족해서 큰일이에요. 좀 도와주실 수 있을까요?"
- 진행중: "작물이 잘 자라고 있어요. 조금만 더 힘내주세요!"
- 완료: "정말 감사합니다! 덕분에 올해 수확을 무사히 마칠 수 있었어요. 이건 작은 성의입니다."

### 7. 대장장이의 제자 (The Blacksmith's Apprentice)
퀘스트 ID: SIDE_BLACKSMITH_APPRENTICE
카테고리: SIDE
레벨 요구사항: 10
선행 퀘스트: MAIN_HEROES_JOURNEY
반복 가능: 아니오
진행 방식: 순차적
한국어 이름: 대장장이의 제자
영어 이름: The Blacksmith's Apprentice
한국어 설명: 마을의 유명한 대장장이가 제자를 찾고 있습니다. 여러 시험을 통과하여 대장 기술을 배워보세요.
영어 설명: The famous village blacksmith is looking for an apprentice. Pass various tests to learn smithing skills.
퀘스트 부여 NPC: 대장장이 마스터 (마을 대장간)
보상 수령 NPC: 대장장이 마스터 (동일 NPC)

대화:
- 시작: "흠... 네가 대장 기술을 배우고 싶다고? 먼저 네 실력을 증명해 보거라."
- 진행중: "아직 시험이 끝나지 않았네. 계속 노력하게나."
- 완료: "훌륭하네! 자네는 이제 진정한 대장장이의 길을 걷게 되었네."

목표:
1. 철 주괴(IRON_INGOT) 50개 수집
2. 철 도구 세트 제작 - 철 검(IRON_SWORD) 1개, 철 곡괭이(IRON_PICKAXE) 1개, 철 도끼(IRON_AXE) 1개, 철 괭이(IRON_HOE) 1개, 철 삽(IRON_SHOVEL) 1개
3. 대장장이(master_blacksmith) NPC에게 제작한 철 도구 전달
4. 용광로(FURNACE) 5개 설치
5. 석탄(COAL) 64개 수집
6. 금 주괴(GOLD_INGOT) 20개 수집
7. 금 도구 1개 제작 (GOLDEN_SWORD, GOLDEN_PICKAXE, GOLDEN_AXE, GOLDEN_HOE, GOLDEN_SHOVEL 중 아무거나)
8. 다이아몬드(DIAMOND) 5개 수집
9. 다이아몬드 검(DIAMOND_SWORD) 1개 제작
10. 대장장이에게 최종 보고

보상:
- 경험치: 1500
- 돈: 2500골드
- 아이템: 다이아몬드(Diamond) 20개, 대장 작업대(Smithing Table) 1개, 보호 II 마법이 부여된 철 흉갑(Iron Chestplate - Protection II) 1개

### 8. 잃어버린 보물 (Lost Treasure)
퀘스트 ID: SIDE_LOST_TREASURE
카테고리: SIDE
레벨 요구사항: 10
선행 퀘스트: MAIN_HEROES_JOURNEY
반복 가능: 아니오
진행 방식: 순차적
한국어 이름: 잃어버린 보물
영어 이름: Lost Treasure
한국어 설명: 오래된 보물 지도를 발견했습니다. 지도를 따라가 숨겨진 보물을 찾아보세요.
영어 설명: You've discovered an old treasure map. Follow the map to find hidden treasure.
퀘스트 부여 NPC: 보물 사냥꾼 (모험가 길드)
보상 수령 NPC: 보물 사냥꾼 (동일 NPC)

목표:
1. 첫 번째 단서 위치 방문 - 위치: X:200, Y:65, Z:200, 반경 5블록
2. 두 번째 단서 위치 방문 - 위치: X:-150, Y:70, Z:300, 반경 5블록  
3. 최종 보물 위치 방문 - 위치: X:100, Y:50, Z:-200, 반경 5블록

보상:
- 경험치: 300
- 돈: 500골드
- 아이템: 다이아몬드(Diamond) 5개, 황금 사과(Golden Apple) 2개, 날카로움 III 마법이 부여된 책(Enchanted Book - Sharpness III) 1개

대화:
- 시작: "이 지도를 보게나, {player}. 전설의 보물이 숨겨진 위치를 나타내고 있지. 찾을 수 있겠나?"
- 진행중: "지도의 단서를 잘 따라가고 있나? 보물은 가까이 있을 거야."
- 완료: "놀라워! 정말로 보물을 찾았군! 자네야말로 진정한 보물 사냥꾼이야."

### 9. 고대 암호 (Ancient Cipher)
퀘스트 ID: SIDE_ANCIENT_CIPHER
카테고리: SIDE
레벨 요구사항: 25
선행 퀘스트: MAIN_ANCIENT_PROPHECY
반복 가능: 아니오
진행 방식: 순차적
한국어 이름: 고대 암호
영어 이름: Ancient Cipher
한국어 설명: 전설의 보물고를 여는 고대 암호를 해독하세요. 네 개의 암호 열쇠를 모두 찾아야 합니다.
영어 설명: Decode the ancient cipher that opens the legendary treasury. Find all four cipher keys.
퀘스트 부여 NPC: 고고학자 (박물관)
보상 수령 NPC: 고고학자 (동일 NPC)

대화:
- 시작: "이 고대 문서에는 전설의 보물고 위치가 적혀있지만... 암호화되어 있네. 네 개의 열쇠가 필요해."
- 진행중: "암호 열쇠를 찾고 있나? 고대인들은 그것들을 세계 곳곳에 숨겨두었다네."
- 완료: "놀라워! 모든 암호를 해독했군! 이제 보물고의 위치를 알 수 있겠어!"

목표:
1. 북쪽 신전 방문 - 위치: X:0, Y:70, Z:-1000, 반경 10블록
2. 북쪽 신전에서 첫 번째 암호 열쇠(NETHER_STAR) 획득
3. 동쪽 유적 방문 - 위치: X:1000, Y:65, Z:0, 반경 10블록
4. 동쪽 유적에서 두 번째 암호 열쇠(ENDER_EYE) 획득
5. 남쪽 피라미드 방문 - 위치: X:0, Y:68, Z:1000, 반경 10블록
6. 남쪽 피라미드에서 세 번째 암호 열쇠(ENCHANTED_BOOK) 획득
7. 서쪽 탑 방문 - 위치: X:-1000, Y:100, Z:0, 반경 10블록
8. 서쪽 탑에서 네 번째 암호 열쇠(DRAGON_BREATH) 획득
9. 고고학자에게 네 개의 암호 열쇠 모두 전달
10. 해독된 좌표로 이동 - 위치: X:500, Y:40, Z:500, 반경 15블록
11. 보물 상자에서 고대의 유물(TOTEM_OF_UNDYING) 획득
12. 고고학자에게 고대의 유물 전달

보상:
- 경험치: 4000
- 돈: 8000골드
- 아이템: 다이아몬드(Diamond) 50개, 네더라이트 주괴(Netherite Ingot) 5개, 신속 III 마법이 부여된 책(Enchanted Book - Swiftness III) 1개, 보호 IV 마법이 부여된 책(Enchanted Book - Protection IV) 1개, 마법이 부여된 황금 사과(Enchanted Golden Apple) 3개

### 10. 일일 사냥 (Daily Hunting)
퀘스트 ID: DAILY_HUNTING
카테고리: DAILY
레벨 요구사항: 5
선행 퀘스트: TUTORIAL_BASIC_COMBAT
반복 가능: 예 (매일 오전 6시 리셋)
진행 방식: 비순차적
한국어 이름: 일일 사냥
영어 이름: Daily Hunting
한국어 설명: 마을을 위협하는 몬스터들을 처치하세요. 매일 새로운 사냥 의뢰가 주어집니다.
영어 설명: Defeat monsters threatening the village. New hunting requests are given daily.
퀘스트 부여 NPC: 경비대장 (마을 경비소)
보상 수령 NPC: 경비대장 (동일 NPC)

목표:
1. 좀비(ZOMBIE) 20마리 처치
2. 스켈레톤(SKELETON) 15마리 처치
3. 크리퍼(CREEPER) 10마리 처치

보상:
- 경험치: 150
- 돈: 200골드
- 아이템: 화살(Arrow) 64개, 구운 소고기(Cooked Beef) 32개

대화:
- 시작: "오늘도 마을의 안전을 위해 힘써주시겠습니까? 몬스터들이 날뛰고 있습니다."
- 진행중: "잘 하고 있습니다! 조금만 더 힘내주세요."
- 완료: "훌륭합니다! 덕분에 마을이 안전해졌습니다. 내일도 부탁드립니다."

### 11. 세계의 타이탄 토벌 (World Titan Subjugation)
퀘스트 ID: WEEKLY_WORLD_BOSS
카테고리: WEEKLY
레벨 요구사항: 45
선행 퀘스트: MAIN_HEROES_JOURNEY
반복 가능: 예 (매주 월요일 오전 6시 리셋)
진행 방식: 순차적
한국어 이름: 세계의 타이탄 토벌
영어 이름: World Titan Subjugation
한국어 설명: 세계를 위협하는 거대한 타이탄이 나타났습니다! 모든 영웅들과 힘을 합쳐 타이탄을 토벌하세요.
영어 설명: A giant titan threatening the world has appeared! Join forces with all heroes to defeat the titan.
퀘스트 부여 NPC: 원정대장 (타이탄 토벌 본부)
보상 수령 NPC: 원정대장 (동일 NPC)

대화:
- 시작: "타이탄이 다시 깨어났다! 모든 용사들이여, 힘을 합쳐 이 위협을 막아야 한다!"
- 진행중: "타이탄과의 전투는 3단계로 나뉜다. 각 단계마다 전략을 바꿔야 할 것이다."
- 완료: "대단하다! 타이탄을 물리쳤군! 세계는 다시 한 번 평화를 되찾았다."

목표:
1. 타이탄의 영역 진입 - 위치: X:0, Y:100, Z:-2000, 반경 50블록
2. 타이탄의 부하 100마리 처치 (ZOMBIE, SKELETON, CREEPER 합산)
3. 타이탄의 핵심 3개 파괴 (END_CRYSTAL 3개 파괴)
4. 7200초(2시간) 동안 생존
5. 최종 보스 타이탄(ENDER_DRAGON) 처치
6. 원정대장에게 승리 보고

보상:
- 경험치: 15000
- 돈: 30000골드
- 아이템: 다이아몬드(Diamond) 200개, 네더라이트 흉갑(Netherite Chestplate) 1개, 겉날개(Elytra) 1개

### 12. 전설의 무기 제작 (Forging the Legendary Weapon)
퀘스트 ID: SPECIAL_LEGENDARY_WEAPON
카테고리: SPECIAL
레벨 요구사항: 50
선행 퀘스트: SIDE_BLACKSMITH_APPRENTICE, MAIN_ANCIENT_PROPHECY
반복 가능: 아니오
진행 방식: 순차적
한국어 이름: 전설의 무기 제작
영어 이름: Forging the Legendary Weapon
한국어 설명: 전설에만 전해지던 최강의 무기를 제작하세요. 네 가지 신성한 재료를 모아야 합니다.
영어 설명: Forge the legendary weapon of myths. Gather four divine materials.
퀘스트 부여 NPC: 전설의 대장장이 (숨겨진 대장간)
보상 수령 NPC: 전설의 대장장이 (동일 NPC)

대화:
- 시작: "오호... 전설의 무기를 만들고 싶다고? 그렇다면 네 가지 신성한 재료가 필요하지."
- 진행중: "신성한 재료들은 각각 세계의 끝에 숨겨져 있다네. 위험한 여정이 될 것이야."
- 완료: "믿을 수 없군! 정말로 모든 재료를 모았구나! 이제 전설의 무기를 만들 수 있겠어!"

목표:
1. 용의 심장 획득 - 엔더 드래곤(ENDER_DRAGON) 5마리 처치
2. 불사조의 깃털 획득 - 블레이즈(BLAZE) 50마리 처치 (네더 요새에서)
3. 심해의 진주 획득 - 엘더 가디언(ELDER_GUARDIAN) 3마리 처치 (해저 신전에서)
4. 별빛 광석 획득 - Y좌표 200 이상에서 네더라이트(NETHERITE_INGOT) 10개 수집
5. 전설의 대장장이에게 네 가지 재료 전달
6. 전설의 모루 제작 - 네더라이트 블록(NETHERITE_BLOCK) 4개, 마법이 부여된 책(ENCHANTED_BOOK) 10개 조합
7. 전설의 무기 제작 - 모든 재료를 사용하여 제작
8. 무기에 최종 마법 부여 - 경험치 레벨 100 소모

보상:
- 경험치: 20000
- 돈: 50000골드
- 아이템: 다이아몬드(Diamond) 500개, 전설의 네더라이트 검(Legendary Netherite Sword) - 날카로움 V, 약탈 III, 화염 속성 II, 내구성 III, 수선 마법 부여

### 13. 그림자의 길 (Path of Shadows)
퀘스트 ID: SPECIAL_HIDDEN_CLASS
카테고리: SPECIAL
레벨 요구사항: 35
선행 퀘스트: MAIN_HEROES_JOURNEY
반복 가능: 아니오
진행 방식: 순차적
한국어 이름: 그림자의 길
영어 이름: Path of Shadows
한국어 설명: 비밀스러운 그림자 길드의 일원이 되어 숨겨진 직업을 해금하세요.
영어 설명: Become a member of the secret Shadow Guild and unlock a hidden class.
퀘스트 부여 NPC: 그림자 길드 마스터 (비밀 아지트)
보상 수령 NPC: 그림자 길드 마스터 (동일 NPC)

대화:
- 시작: "그림자의 길을 걷고자 하는가... 쉽지 않은 선택이다. 세 가지 시험을 통과해야 한다."
- 진행중: "그림자는 인내와 기술을 요구한다. 포기하고 싶다면 지금이라도 늦지 않았다."
- 완료: "축하한다... 이제 너는 진정한 그림자 암살자다. 이 힘을 현명하게 사용하길 바란다."

목표:
1. 은신 시험 - 밤 시간대(18000-23000틱)에 1800초(30분) 동안 몬스터에게 피해받지 않고 생존
2. 암살 시험 - 백스탭으로 몬스터 50마리 처치 (뒤에서 공격)
3. 그림자 재료 수집 - 흑요석(OBSIDIAN) 64개, 엔더 진주(ENDER_PEARL) 32개, 석탄 블록(COAL_BLOCK) 32개 수집
4. 그림자 길드 아지트 침투 - 위치: X:-500, Y:30, Z:-500, 반경 10블록
5. 길드원 5명과 PvP 대결에서 승리 (플레이어 5명 처치)
6. 최종 시험 - 그림자 길드 마스터와 1대1 대결 (위더(WITHER) 처치)
7. 그림자의 맹세 - 그림자 길드 마스터에게 충성 서약

보상:
- 경험치: 7500
- 돈: 7500골드
- 아이템: 다이아몬드(Diamond) 50개, 그림자 후드(Shadow Hood) - 투명화 효과, 그림자 망토(Shadow Cloak) - 은신 시 이동속도 증가, 그림자 단검(Shadow Dagger) - 백스탭 데미지 2배
- 특수 보상: 숨겨진 직업 '암살자' 해금

### 14. 길드 창설의 길 (Path of Guild Foundation)
퀘스트 ID: GUILD_ESTABLISHMENT
카테고리: GUILD
레벨 요구사항: 25
선행 퀘스트: MAIN_HEROES_JOURNEY
반복 가능: 아니오
진행 방식: 순차적
한국어 이름: 길드 창설의 길
영어 이름: Path of Guild Foundation
한국어 설명: 자신만의 길드를 창설하고 길드 마스터가 되어보세요.
영어 설명: Create your own guild and become a guild master.
퀘스트 부여 NPC: 길드 등록관 (길드 관리소)
보상 수령 NPC: 길드 등록관 (동일 NPC)

대화:
- 시작: "길드를 창설하고 싶으신가요? 몇 가지 조건을 충족해야 합니다."
- 진행중: "길드 창설은 큰 책임이 따릅니다. 준비가 되셨나요?"
- 완료: "축하합니다! 이제 당신은 정식 길드 마스터입니다!"

목표:
1. 길드 창설 비용 지불 - 10000골드 지불
2. 길드원 모집 - 플레이어 5명과 파티 구성 (파티 크기 5 이상)
3. 길드 홀 위치 선정 - 위치: X:1000, Y:70, Z:1000 방문, 반경 20블록
4. 길드 홀 기초 건설 - 석재(STONE) 500개, 나무 판자(OAK_PLANKS, SPRUCE_PLANKS, BIRCH_PLANKS, JUNGLE_PLANKS, ACACIA_PLANKS, DARK_OAK_PLANKS 중 아무거나) 500개 설치
5. 길드 상징 제작 - 양털(WHITE_WOOL) 64개로 깃발(WHITE_BANNER) 10개 제작
6. 길드 금고 설치 - 상자(CHEST) 10개 설치
7. 길드 등록 완료 - 길드 등록관에게 최종 보고

보상:
- 경험치: 5000
- 돈: 20000골드
- 아이템: 다이아몬드(Diamond) 100개, 길드 비콘(Beacon) 1개, 엔더 상자(Ender Chest) 5개, 셜커 상자(Shulker Box) 3개

## 퀘스트 시스템 메타 정보

### 퀘스트 진행 상태
- NOT_STARTED: 시작하지 않음
- IN_PROGRESS: 진행 중
- COMPLETED: 완료됨 (보상 미수령)
- REWARDED: 보상 수령 완료

### 목표 진행 상태
- 각 목표는 현재값(current)과 요구값(required)을 가짐
- 현재값이 요구값에 도달하면 목표 완료
- 순차적 퀘스트는 이전 목표를 완료해야 다음 목표 진행 가능

### 퀘스트 데이터 저장
- 플레이어별 활성 퀘스트 목록
- 각 퀘스트의 목표별 진행도
- 완료된 퀘스트 목록 및 보상 수령 여부
- 미수령 보상 및 만료 시간

### NPC 상호작용
- NPC ID를 통한 식별 (예: village_merchant, elder_sage)
- 퀘스트 부여 NPC와 보상 수령 NPC가 다를 수 있음
- NPC는 여러 퀘스트를 담당할 수 있음

### 위치 기반 목표
- 3차원 좌표 (X, Y, Z)와 반경으로 정의
- 플레이어가 지정된 범위 내에 들어가면 완료

### 아이템 처리
- 수집: 인벤토리에 아이템 보유 시 카운트
- 전달: NPC에게 전달 시 인벤토리에서 제거
- 제작: 작업대에서 제작 감지

### 보상 시스템
- 즉시 지급: 경험치, 돈
- 아이템 보상: 보상 NPC를 통해 수령
- 특수 보상: 칭호, 직업 해금 등
- 미수령 보상: 1시간 후 자동 소멸

이 문서는 Sypixel RPG의 모든 퀘스트 정보를 완전하게 포함하고 있으며, 위키 사이트 제작에 필요한 모든 데이터를 제공합니다.