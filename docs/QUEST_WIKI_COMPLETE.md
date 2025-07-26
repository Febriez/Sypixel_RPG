# Sypixel RPG 완전 퀘스트 정보

이 문서는 Sypixel RPG의 모든 퀘스트 정보를 포함합니다. 총 263개의 퀘스트가 등록되어 있으며, 카테고리별로 정리되어 있습니다.

## 퀘스트 카테고리
- **TUTORIAL** (2개): 튜토리얼 퀘스트
- **MAIN** (44개): 메인 스토리 퀘스트  
- **SIDE** (52개): 서브 퀘스트
- **DAILY** (15개): 일일 퀘스트 (매일 오전 6시 리셋)
- **WEEKLY** (10개): 주간 퀘스트 (매주 월요일 오전 6시 리셋)
- **REPEATABLE** (10개): 반복 가능 퀘스트
- **SPECIAL** (10개): 특별 퀘스트
- **ADVANCEMENT** (10개): 직업 발전 퀘스트
- **EVENT** (10개): 시즌/이벤트 퀘스트
- **GUILD** (10개): 길드 퀘스트
- **EXPLORATION** (10개): 탐험 퀘스트
- **CRAFTING** (10개): 제작 퀘스트
- **LIFE** (10개): 생활 퀘스트
- **COMBAT** (10개): 전투 퀘스트
- **BRANCH** (10개): 스토리 분기 퀘스트

## 목표 타입
- **KILL**: 몬스터 처치
- **COLLECT**: 아이템 수집
- **VISIT_LOCATION**: 특정 위치 방문
- **INTERACT_NPC**: NPC와 상호작용
- **CRAFT**: 아이템 제작
- **PLACE**: 블록 설치
- **BREAK**: 블록 파괴
- **FISH**: 낚시
- **HARVEST**: 수확
- **SURVIVE**: 일정 시간 생존
- **DELIVER**: 아이템 전달
- **PAY**: 화폐 지불
- **REACH_LEVEL**: 레벨 도달
- **KILL_PLAYER**: 플레이어 처치
- **USE_SKILL**: 스킬 사용
- **ENCHANT**: 인챈트
- **TAME**: 길들이기
- **TRADE**: 거래
- **EQUIP**: 장비 착용
- **CONSUME**: 아이템 소비

---

## 튜토리얼 퀘스트 (TUTORIAL)

### 1. 첫 걸음 (First Steps)
- **퀘스트 ID**: TUTORIAL_FIRST_STEPS
- **카테고리**: TUTORIAL
- **레벨 요구사항**: 1
- **선행 퀘스트**: 없음
- **반복 가능**: 아니오
- **구현 클래스**: FirstStepsQuest
- **파일 위치**: `src/main/java/com/febrie/rpg/quest/impl/tutorial/FirstStepsQuest.java`
- **설명**: 서버의 기본적인 이동과 상호작용을 배우는 첫 번째 튜토리얼 퀘스트

**목표** (순차적 진행 필요):
1. **허브 구역 방문** (VISIT_LOCATION)
   - WorldGuard 영역 이름: `Hub`
   - 목표: 허브 지역으로 이동
2. **마을 상인 NPC 방문** (INTERACT_NPC)
   - NPC Code: `village_merchant`
   - 목표: 마을 상인과 대화하기

**보상**:
- 경험치: 50
- 골드: 100
- 아이템:
  - 나무 검 (WOODEN_SWORD) x1
  - 나무 곡괭이 (WOODEN_PICKAXE) x1
  - 나무 도끼 (WOODEN_AXE) x1
  - 빵 (BREAD) x10

**NPC 대화**:
- **NPC 이름**: 설정된 언어에 따라 다름
- **대화 내용**:
  1. 환영 인사
  2. 가이드 소개
  3. 준비 확인 질문
  4. 수락 시 퀘스트 시작 안내
  5. 거절 시 다시 찾아오라는 안내

**특징**:
- 순차적 진행 (sequential: true)
- 언어 지원: 한국어/영어 (LangManager 사용)
- 대화 시스템 포함 (QuestDialog)
- 총 3개의 대화 라인

### 2. 기초 전투 (Basic Combat)
- **퀘스트 ID**: TUTORIAL_BASIC_COMBAT
- **카테고리**: TUTORIAL
- **레벨 요구사항**: 1
- **최대 레벨**: 제한 없음
- **선행 퀘스트**: TUTORIAL_FIRST_STEPS (필수)
- **반복 가능**: 아니오
- **구현 클래스**: BasicCombatQuest
- **파일 위치**: `src/main/java/com/febrie/rpg/quest/impl/tutorial/BasicCombatQuest.java`
- **설명**: 몬스터와 싸우는 방법을 배우는 전투 기초 튜토리얼

**목표** (순서 상관없이 진행 가능):
1. **좀비 처치** (KILL_MOB)
   - 목표: 좀비 5마리 처치
   - Entity Type: ZOMBIE
   - ID: `kill_zombies`
2. **스켈레톤 처치** (KILL_MOB)
   - 목표: 스켈레톤 3마리 처치
   - Entity Type: SKELETON
   - ID: `kill_skeletons`

**보상**:
- 경험치: 100
- 골드: 200
- 아이템:
  - 철 검 (IRON_SWORD) x1
  - 철 흉갑 (IRON_CHESTPLATE) x1
  - 익힌 소고기 (COOKED_BEEF) x20

**NPC 대화**:
- **NPC 이름**: 전투 교관
- **대화 내용**:
  1. "모험가가 되려면 전투 기술은 필수입니다!"
  2. "밤이 되면 몬스터들이 나타납니다. 준비하세요!"
  3. "좀비와 스켈레톤을 처치하고 돌아오면, 더 나은 장비를 드리겠습니다."

**특징**:
- 비순차적 진행 (sequential: false)
- 전투 중심 튜토리얼
- 기본 장비 제공
- 다국어 지원

---

## 메인 퀘스트 (MAIN)

### Chapter 1: The Awakening (각성)

메인 스토리 퀘스트는 Sypixel RPG의 핵심 스토리를 담고 있는 대서사시입니다.

#### 3. 영웅의 여정 (Hero's Journey)
- **퀘스트 ID**: MAIN_HEROES_JOURNEY
- **카테고리**: MAIN
- **레벨 요구사항**: 5
- **최대 레벨**: 제한 없음
- **선행 퀘스트**: TUTORIAL_BASIC_COMBAT (필수)
- **반복 가능**: 아니오
- **구현 클래스**: HeroesJourneyQuest
- **파일 위치**: `src/main/java/com/febrie/rpg/quest/impl/main/HeroesJourneyQuest.java`
- **설명**: 진정한 영웅이 되기 위한 첫 걸음. 다양한 도전을 통해 실력을 증명하세요!

**특징**:
- 다양한 목표를 동시에 진행 가능
- 자유로운 진행 순서

**목표** (비순차적 진행 - sequential: false):

**[전투 목표]**
1. **좀비 처치** (KILL_MOB)
   - EntityType: ZOMBIE x10
   - ID: `kill_zombies`
2. **스켈레톤 처치** (KILL_MOB)
   - EntityType: SKELETON x10
   - ID: `kill_skeletons`
3. **거미 처치** (KILL_MOB)
   - EntityType: SPIDER x5
   - ID: `kill_spiders`

**[자원 수집 목표]**
4. **철괴 수집** (COLLECT_ITEM)
   - Material: IRON_INGOT x20
   - ID: `collect_iron`
5. **금괴 수집** (COLLECT_ITEM)
   - Material: GOLD_INGOT x10
   - ID: `collect_gold`

**[제작 목표]**
6. **철 검 제작** (CRAFT_ITEM)
   - Material: IRON_SWORD x1
   - ID: `craft_iron_sword`
7. **철 흉갑 제작** (CRAFT_ITEM)
   - Material: IRON_CHESTPLATE x1
   - ID: `craft_iron_armor`

**보상**:
- 경험치: 500
- 골드: 500
- 다이아몬드: 5
- 아이템:
  - 다이아몬드 (DIAMOND) x1
  - 마법부여대 (ENCHANTING_TABLE) x1

**NPC 대화**:
- **NPC 이름**: 모험가 길드장
- **대화 내용**:
  1. "드디어 진정한 모험을 시작할 준비가 되셨군요!"
  2. "영웅이 되는 길은 험난합니다. 하지만 당신이라면 할 수 있을 거예요."
  3. "다양한 몬스터를 처치하고, 자원을 모아 장비를 만드세요."
  4. "모든 목표를 달성하면 특별한 보상이 기다리고 있습니다. 행운을 빕니다!"

#### 4. 빛의 길 (Path of Light)
- **퀘스트 ID**: MAIN_PATH_OF_LIGHT
- **카테고리**: MAIN
- **레벨 요구사항**: 10
- **구현 클래스**: PathOfLightQuest
- **설명**: 빛의 세력을 선택하는 스토리 분기

#### 5. 어둠의 길 (Path of Darkness)
- **퀘스트 ID**: MAIN_PATH_OF_DARKNESS
- **카테고리**: MAIN
- **레벨 요구사항**: 10
- **구현 클래스**: PathOfDarknessQuest
- **설명**: 어둠의 세력을 선택하는 스토리 분기

#### 6. 고대의 예언 (Ancient Prophecy)
- **퀘스트 ID**: MAIN_ANCIENT_PROPHECY
- **카테고리**: MAIN
- **레벨 요구사항**: 15
- **구현 클래스**: AncientProphecyQuest

#### 7. 선택받은 자 (Chosen One)
- **퀘스트 ID**: MAIN_CHOSEN_ONE
- **카테고리**: MAIN
- **레벨 요구사항**: 20
- **구현 클래스**: ChosenOneQuest

#### 8. 첫 번째 시험 (First Trial)
- **퀘스트 ID**: MAIN_FIRST_TRIAL
- **카테고리**: MAIN
- **레벨 요구사항**: 25
- **구현 클래스**: FirstTrialQuest

#### 9. 원소의 돌 (Elemental Stones)
- **퀘스트 ID**: MAIN_ELEMENTAL_STONES
- **카테고리**: MAIN
- **레벨 요구사항**: 30
- **구현 클래스**: ElementalStonesQuest

#### 10. 수호자의 각성 (Guardian Awakening)
- **퀘스트 ID**: MAIN_GUARDIAN_AWAKENING
- **카테고리**: MAIN
- **레벨 요구사항**: 35
- **구현 클래스**: GuardianAwakeningQuest

### Chapter 2: Rise of Darkness (어둠의 부상)

#### 11. 그림자의 침략 (Shadow Invasion)
- **퀘스트 ID**: MAIN_SHADOW_INVASION
- **카테고리**: MAIN
- **레벨 요구사항**: 40
- **구현 클래스**: ShadowInvasionQuest

#### 12. 타락한 땅 (Corrupted Lands)
- **퀘스트 ID**: MAIN_CORRUPTED_LANDS
- **카테고리**: MAIN
- **레벨 요구사항**: 45

#### 13. 잃어버린 왕국 (Lost Kingdom)
- **퀘스트 ID**: MAIN_LOST_KINGDOM
- **카테고리**: MAIN
- **레벨 요구사항**: 50

#### 14. 고대의 악 (Ancient Evil)
- **퀘스트 ID**: MAIN_ANCIENT_EVIL
- **카테고리**: MAIN
- **레벨 요구사항**: 55

#### 15. 영웅들의 동맹 (Heroes Alliance)
- **퀘스트 ID**: MAIN_HEROES_ALLIANCE
- **카테고리**: MAIN
- **레벨 요구사항**: 60

### Chapter 3: The Dragon's Return (용의 귀환)

#### 16. 용의 각성 (Dragon Awakening)
- **퀘스트 ID**: MAIN_DRAGON_AWAKENING
- **카테고리**: MAIN
- **레벨 요구사항**: 65
- **구현 클래스**: DragonAwakeningQuest

#### 17. 용의 시련 (Dragon Trials)
- **퀘스트 ID**: MAIN_DRAGON_TRIALS
- **카테고리**: MAIN
- **레벨 요구사항**: 70

#### 18. 용의 계약 (Dragon Pact)
- **퀘스트 ID**: MAIN_DRAGON_PACT
- **카테고리**: MAIN
- **레벨 요구사항**: 75

#### 19. 하늘의 요새 (Sky Fortress)
- **퀘스트 ID**: MAIN_SKY_FORTRESS
- **카테고리**: MAIN
- **레벨 요구사항**: 80

#### 20. 용의 심장 (Dragon Heart)
- **퀘스트 ID**: MAIN_DRAGON_HEART
- **카테고리**: MAIN
- **레벨 요구사항**: 85

### Chapter 4: War of Realms (차원 전쟁)

#### 21. 차원의 문 (Realm Portal)
- **퀘스트 ID**: MAIN_REALM_PORTAL
- **카테고리**: MAIN
- **레벨 요구사항**: 90

#### 22. 공허의 침공 (Void Invasion)
- **퀘스트 ID**: MAIN_VOID_INVASION
- **카테고리**: MAIN
- **레벨 요구사항**: 95

#### 23. 차원의 수호자 (Realm Defenders)
- **퀘스트 ID**: MAIN_REALM_DEFENDERS
- **카테고리**: MAIN
- **레벨 요구사항**: 100

#### 24. 혼돈의 폭풍 (Chaos Storm)
- **퀘스트 ID**: MAIN_CHAOS_STORM
- **카테고리**: MAIN
- **레벨 요구사항**: 105

#### 25. 차원의 균열 (Dimensional Rift)
- **퀘스트 ID**: MAIN_DIMENSIONAL_RIFT
- **카테고리**: MAIN
- **레벨 요구사항**: 110

### Chapter 5: Final Destiny (최종 운명)

#### 26. 다가오는 폭풍 (Gathering Storm)
- **퀘스트 ID**: MAIN_GATHERING_STORM
- **카테고리**: MAIN
- **레벨 요구사항**: 115

#### 27. 최후의 저항 (Last Stand)
- **퀘스트 ID**: MAIN_LAST_STAND
- **카테고리**: MAIN
- **레벨 요구사항**: 120

#### 28. 최종 전투 (Final Battle)
- **퀘스트 ID**: MAIN_FINAL_BATTLE
- **카테고리**: MAIN
- **레벨 요구사항**: 125

#### 29. 영웅의 희생 (Sacrifice of Heroes)
- **퀘스트 ID**: MAIN_SACRIFICE_OF_HEROES
- **카테고리**: MAIN
- **레벨 요구사항**: 130

#### 30. 새로운 시대 (New Era)
- **퀘스트 ID**: MAIN_NEW_ERA
- **카테고리**: MAIN
- **레벨 요구사항**: 135

### Chapter 6: Epilogue (에필로그)

#### 31. 복구 (Restoration)
- **퀘스트 ID**: MAIN_RESTORATION
- **카테고리**: MAIN
- **레벨 요구사항**: 140

#### 32. 영웅의 유산 (Legacy of Heroes)
- **퀘스트 ID**: MAIN_LEGACY_OF_HEROES
- **카테고리**: MAIN
- **레벨 요구사항**: 145

#### 33. 영원한 수호자 (Eternal Guardian)
- **퀘스트 ID**: MAIN_ETERNAL_GUARDIAN
- **카테고리**: MAIN
- **레벨 요구사항**: 150

---

## 사이드 퀘스트 (SIDE)

### 기본 사이드 퀘스트

#### 34. 농부의 부탁 (Farmers Request)
- **퀘스트 ID**: SIDE_FARMERS_REQUEST
- **카테고리**: SIDE
- **레벨 요구사항**: 5
- **구현 클래스**: FarmersRequestQuest

#### 35. 약초 수집 (Collect Herbs)
- **퀘스트 ID**: SIDE_COLLECT_HERBS
- **카테고리**: SIDE
- **레벨 요구사항**: 8
- **구현 클래스**: CollectHerbsQuest

#### 36. 잃어버린 보물 (Lost Treasure)
- **퀘스트 ID**: SIDE_LOST_TREASURE
- **카테고리**: SIDE
- **레벨 요구사항**: 12
- **구현 클래스**: LostTreasureQuest

### 탐험 & 발견 사이드 퀘스트

#### 37. 고대 유적 탐험 (Ancient Ruins)
- **퀘스트 ID**: SIDE_ANCIENT_RUINS
- **카테고리**: SIDE
- **레벨 요구사항**: 15
- **구현 클래스**: AncientRuinsQuest (exploration 폴더)

#### 38. 숨겨진 계곡 (Hidden Valley)
- **퀘스트 ID**: SIDE_HIDDEN_VALLEY
- **카테고리**: SIDE
- **레벨 요구사항**: 18

#### 39. 신비한 동굴 (Mysterious Cave)
- **퀘스트 ID**: SIDE_MYSTERIOUS_CAVE
- **카테고리**: SIDE
- **레벨 요구사항**: 20

#### 40. 잊혀진 사원 (Forgotten Temple)
- **퀘스트 ID**: SIDE_FORGOTTEN_TEMPLE
- **카테고리**: SIDE
- **레벨 요구사항**: 25

#### 41. 가라앉은 도시 (Sunken City)
- **퀘스트 ID**: SIDE_SUNKEN_CITY
- **카테고리**: SIDE
- **레벨 요구사항**: 30

#### 42. 수정 동굴 (Crystal Cavern)
- **퀘스트 ID**: SIDE_CRYSTAL_CAVERN
- **카테고리**: SIDE
- **레벨 요구사항**: 35

#### 43. 마법의 숲 (Enchanted Forest)
- **퀘스트 ID**: SIDE_ENCHANTED_FOREST
- **카테고리**: SIDE
- **레벨 요구사항**: 40

#### 44. 사막의 오아시스 (Desert Oasis)
- **퀘스트 ID**: SIDE_DESERT_OASIS
- **카테고리**: SIDE
- **레벨 요구사항**: 45

#### 45. 얼어붙은 봉우리 (Frozen Peaks)
- **퀘스트 ID**: SIDE_FROZEN_PEAKS
- **카테고리**: SIDE
- **레벨 요구사항**: 50

#### 46. 화산의 깊이 (Volcanic Depths)
- **퀘스트 ID**: SIDE_VOLCANIC_DEPTHS
- **카테고리**: SIDE
- **레벨 요구사항**: 55

### NPC 스토리 사이드 퀘스트

#### 47. 대장장이의 제자 (Blacksmith Apprentice)
- **퀘스트 ID**: SIDE_BLACKSMITH_APPRENTICE
- **카테고리**: SIDE
- **레벨 요구사항**: 10
- **구현 클래스**: BlacksmithApprenticeQuest

#### 48. 상인의 딜레마 (Merchants Dilemma)
- **퀘스트 ID**: SIDE_MERCHANTS_DILEMMA
- **카테고리**: SIDE
- **레벨 요구사항**: 15

#### 49. 치유사의 부탁 (Healers Request)
- **퀘스트 ID**: SIDE_HEALERS_REQUEST
- **카테고리**: SIDE
- **레벨 요구사항**: 20

#### 50. 도둑 길드 (Thieves Guild)
- **퀘스트 ID**: SIDE_THIEVES_GUILD
- **카테고리**: SIDE
- **레벨 요구사항**: 25

#### 51. 왕실 전령 (Royal Messenger)
- **퀘스트 ID**: SIDE_ROYAL_MESSENGER
- **카테고리**: SIDE
- **레벨 요구사항**: 30

#### 52. 사서의 미스터리 (Librarian Mystery)
- **퀘스트 ID**: SIDE_LIBRARIAN_MYSTERY
- **카테고리**: SIDE
- **레벨 요구사항**: 35

#### 53. 여관주인의 고민 (Innkeeper Trouble)
- **퀘스트 ID**: SIDE_INNKEEPER_TROUBLE
- **카테고리**: SIDE
- **레벨 요구사항**: 40

#### 54. 어부의 이야기 (Fisherman Tale)
- **퀘스트 ID**: SIDE_FISHERMAN_TALE
- **카테고리**: SIDE
- **레벨 요구사항**: 45

#### 55. 광부의 곤경 (Miners Plight)
- **퀘스트 ID**: SIDE_MINERS_PLIGHT
- **카테고리**: SIDE
- **레벨 요구사항**: 50

#### 56. 연금술사의 실험 (Alchemist Experiment)
- **퀘스트 ID**: SIDE_ALCHEMIST_EXPERIMENT
- **카테고리**: SIDE
- **레벨 요구사항**: 55

### 몬스터 사냥 사이드 퀘스트

#### 57. 늑대 무리의 위협 (Wolf Pack Menace)
- **퀘스트 ID**: SIDE_WOLF_PACK_MENACE
- **카테고리**: SIDE
- **레벨 요구사항**: 8

#### 58. 거미 침입 (Spider Infestation)
- **퀘스트 ID**: SIDE_SPIDER_INFESTATION
- **카테고리**: SIDE
- **레벨 요구사항**: 12

#### 59. 언데드 봉기 (Undead Uprising)
- **퀘스트 ID**: SIDE_UNDEAD_UPRISING
- **카테고리**: SIDE
- **레벨 요구사항**: 16

#### 60. 고블린 약탈자 (Goblin Raiders)
- **퀘스트 ID**: SIDE_GOBLIN_RAIDERS
- **카테고리**: SIDE
- **레벨 요구사항**: 20

#### 61. 유령의 저주 (Phantom Haunting)
- **퀘스트 ID**: SIDE_PHANTOM_HAUNTING
- **카테고리**: SIDE
- **레벨 요구사항**: 25

#### 62. 원소의 혼돈 (Elemental Chaos)
- **퀘스트 ID**: SIDE_ELEMENTAL_CHAOS
- **카테고리**: SIDE
- **레벨 요구사항**: 30

#### 63. 야수 길들이기 (Beast Taming)
- **퀘스트 ID**: SIDE_BEAST_TAMING
- **카테고리**: SIDE
- **레벨 요구사항**: 35

#### 64. 악마 사냥꾼 (Demon Hunters)
- **퀘스트 ID**: SIDE_DEMON_HUNTERS
- **카테고리**: SIDE
- **레벨 요구사항**: 40

#### 65. 거인 처치자 (Giant Slayer)
- **퀘스트 ID**: SIDE_GIANT_SLAYER
- **카테고리**: SIDE
- **레벨 요구사항**: 45

#### 66. 용의 정찰병 (Dragon Scout)
- **퀘스트 ID**: SIDE_DRAGON_SCOUT
- **카테고리**: SIDE
- **레벨 요구사항**: 50

### 미스터리 & 퍼즐 사이드 퀘스트

#### 67. 고대 암호 (Ancient Cipher)
- **퀘스트 ID**: SIDE_ANCIENT_CIPHER
- **카테고리**: SIDE
- **레벨 요구사항**: 22
- **구현 클래스**: AncientCipherQuest

#### 68. 시간의 역설 (Time Paradox)
- **퀘스트 ID**: SIDE_TIME_PARADOX
- **카테고리**: SIDE
- **레벨 요구사항**: 28

#### 69. 거울 세계 (Mirror World)
- **퀘스트 ID**: SIDE_MIRROR_WORLD
- **카테고리**: SIDE
- **레벨 요구사항**: 32

#### 70. 꿈의 방랑자 (Dream Walker)
- **퀘스트 ID**: SIDE_DREAM_WALKER
- **카테고리**: SIDE
- **레벨 요구사항**: 38

#### 71. 영혼의 조각 (Soul Fragments)
- **퀘스트 ID**: SIDE_SOUL_FRAGMENTS
- **카테고리**: SIDE
- **레벨 요구사항**: 42

#### 72. 기억 도둑 (Memory Thief)
- **퀘스트 ID**: SIDE_MEMORY_THIEF
- **카테고리**: SIDE
- **레벨 요구사항**: 48

#### 73. 그림자 영역 (Shadow Realm)
- **퀘스트 ID**: SIDE_SHADOW_REALM
- **카테고리**: SIDE
- **레벨 요구사항**: 52

#### 74. 영체 투사 (Astral Projection)
- **퀘스트 ID**: SIDE_ASTRAL_PROJECTION
- **카테고리**: SIDE
- **레벨 요구사항**: 58

#### 75. 저주받은 유물 (Cursed Artifact)
- **퀘스트 ID**: SIDE_CURSED_ARTIFACT
- **카테고리**: SIDE
- **레벨 요구사항**: 62

#### 76. 영원한 불꽃 (Eternal Flame)
- **퀘스트 ID**: SIDE_ETERNAL_FLAME
- **카테고리**: SIDE
- **레벨 요구사항**: 68

---

## 일일 퀘스트 (DAILY)

일일 퀘스트는 매일 오전 6시에 자동으로 리셋되며, 24시간마다 다시 도전할 수 있습니다.

### 77. 일일 사냥 (Daily Hunting)
- **퀘스트 ID**: DAILY_HUNTING
- **카테고리**: DAILY
- **레벨 요구사항**: 5
- **최대 레벨**: 제한 없음
- **선행 퀘스트**: TUTORIAL_BASIC_COMBAT (필수)
- **반복 가능**: 예 (24시간 쿨다운)
- **구현 클래스**: DailyHuntingQuest
- **파일 위치**: `src/main/java/com/febrie/rpg/quest/impl/daily/DailyHuntingQuest.java`
- **설명**: 매일 리셋되는 사냥 퀘스트

**목표** (순서 상관없이 진행 가능):
1. **좀비 처치** (KILL_MOB)
   - Entity Type: ZOMBIE
   - 수량: 20마리
   - ID: `kill_zombies`
2. **스켈레톤 처치** (KILL_MOB)
   - Entity Type: SKELETON
   - 수량: 15마리
   - ID: `kill_skeletons`
3. **크리퍼 처치** (KILL_MOB)
   - Entity Type: CREEPER
   - 수량: 10마리
   - ID: `kill_creepers`

**보상**:
- 경험치: 150
- 골드: 200
- 아이템:
  - 화살 (ARROW) x64
  - 익힌 소고기 (COOKED_BEEF) x32

**특징**:
- 일일 퀘스트 (daily: true)
- 비순차적 진행 (sequential: false)
- 다국어 지원 (LangManager 사용)

### 78. 일일 채광 (Daily Mining)
- **퀘스트 ID**: DAILY_MINING
- **카테고리**: DAILY
- **레벨 요구사항**: 1
- **최대 레벨**: 제한 없음
- **선행 퀘스트**: TUTORIAL_BASIC_COMBAT (필수)
- **반복 가능**: 예 (24시간 쿨다운)
- **구현 클래스**: DailyMiningQuest
- **파일 위치**: `src/main/java/com/febrie/rpg/quest/impl/daily/DailyMiningQuest.java`
- **설명**: 오늘의 채광 목표를 완료하는 일일 퀘스트

**목표** (순서 상관없이 진행 가능):
1. **돌 채광** (BREAK_BLOCK)
   - Material: STONE
   - 수량: 50개
   - ID: `mine_stone`
2. **석탄 광석 채광** (BREAK_BLOCK)
   - Material: COAL_ORE
   - 수량: 20개
   - ID: `mine_coal`
3. **철 광석 채광** (BREAK_BLOCK)
   - Material: IRON_ORE
   - 수량: 10개
   - ID: `mine_iron`

**보상**:
- 경험치: 100
- 골드: 150
- 아이템:
  - 철 곡괭이 (IRON_PICKAXE) x1
  - 훃불 (TORCH) x32

**특징**:
- 일일 퀘스트 (daily: true)
- 비순차적 진행
- 매일 자정에 리셋

### 79. 일일 낚시 (Daily Fishing)
- **퀘스트 ID**: DAILY_FISHING
- **카테고리**: DAILY
- **레벨 요구사항**: 3
- **최대 레벨**: 제한 없음
- **선행 퀘스트**: TUTORIAL_BASIC_COMBAT (필수)
- **반복 가능**: 예 (24시간 쿨다운)
- **구현 클래스**: DailyFishingQuest
- **파일 위치**: `src/main/java/com/febrie/rpg/quest/impl/daily/DailyFishingQuest.java`
- **설명**: 오늘의 낚시 할당량을 채우는 일일 퀘스트

**목표** (순서 상관없이 진행 가능):
1. **물고기 낚기** (FISHING_OBJECTIVE)
   - FishType: ANY (아무 물고기나)
   - 수량: 10마리
   - ID: `catch_any_fish`
2. **연어 낚기** (FISHING_OBJECTIVE)
   - FishType: SPECIFIC
   - Material: SALMON
   - 수량: 5마리
   - ID: `catch_salmon`
3. **복어 낚기** (FISHING_OBJECTIVE)
   - FishType: SPECIFIC
   - Material: PUFFERFISH
   - 수량: 2마리
   - ID: `catch_pufferfish`

**보상**:
- 경험치: 100
- 골드: 200
- 에메랄드: 5
- 아이템:
  - 낚싯대 (FISHING_ROD) x1
  - 구운 연어 (COOKED_SALMON) x16

**NPC 대화**:
- **NPC 이름**: 어부 김씨
- **대화 내용**:
  1. "안녕하세요! 오늘도 낚시하러 오셨군요?"
  2. "매일 물고기를 잡아오면 보상을 드리고 있어요."
  3. "아무 물고기나 10마리, 연어 5마리, 그리고 복어 2마리를 잡아주세요!"
  4. "내일 다시 와도 같은 보상을 드릴게요!"

**특징**:
- FishingObjective 클래스 사용
- 특정 물고기 지정 가능
- 다양한 보상 통화 (Gold + Emerald)

#### 80. 일일 채집 (Daily Gathering)
- **퀘스트 ID**: DAILY_GATHERING
- **카테고리**: DAILY
- **레벨 요구사항**: 5
- **구현 클래스**: DailyGatheringQuest

#### 81. 일일 제작 (Daily Crafting)
- **퀘스트 ID**: DAILY_CRAFTING
- **카테고리**: DAILY
- **레벨 요구사항**: 10
- **구현 클래스**: DailyCraftingQuest

#### 82. 일일 배달 (Daily Delivery)
- **퀘스트 ID**: DAILY_DELIVERY
- **카테고리**: DAILY
- **레벨 요구사항**: 12
- **구현 클래스**: DailyDeliveryQuest

#### 83. 일일 순찰 (Daily Patrol)
- **퀘스트 ID**: DAILY_PATROL
- **카테고리**: DAILY
- **레벨 요구사항**: 15

#### 84. 일일 훈련 (Daily Training)
- **퀘스트 ID**: DAILY_TRAINING
- **카테고리**: DAILY
- **레벨 요구사항**: 5

#### 85. 일일 탐험 (Daily Exploration)
- **퀘스트 ID**: DAILY_EXPLORATION
- **카테고리**: DAILY
- **레벨 요구사항**: 20
- **구현 클래스**: DailyExplorationQuest

#### 86. 일일 연금술 (Daily Alchemy)
- **퀘스트 ID**: DAILY_ALCHEMY
- **카테고리**: DAILY
- **레벨 요구사항**: 25

#### 87. 일일 현상금 사냥 (Daily Bounty Hunter)
- **퀘스트 ID**: DAILY_BOUNTY_HUNTER
- **카테고리**: DAILY
- **레벨 요구사항**: 30
- **구현 클래스**: DailyBountyHunterQuest

#### 88. 일일 투기장 챔피언 (Daily Arena Champion)
- **퀘스트 ID**: DAILY_ARENA_CHAMPION
- **카테고리**: DAILY
- **레벨 요구사항**: 35

#### 89. 일일 보물 사냥 (Daily Treasure Hunter)
- **퀘스트 ID**: DAILY_TREASURE_HUNTER
- **카테고리**: DAILY
- **레벨 요구사항**: 25

#### 90. 일일 상인 호위 (Daily Merchant Escort)
- **퀘스트 ID**: DAILY_MERCHANT_ESCORT
- **카테고리**: DAILY
- **레벨 요구사항**: 20

#### 91. 일일 던전 정리 (Daily Dungeon Clear)
- **퀘스트 ID**: DAILY_DUNGEON_CLEAR
- **카테고리**: DAILY
- **레벨 요구사항**: 40

---

## 주간 퀘스트 (WEEKLY)

주간 퀘스트는 매주 월요일 오전 6시에 자동으로 리셋되며, 주 1회만 도전할 수 있는 고난도 콘텐츠입니다.

### 92. 주간 레이드: 혼돈의 요새 (Weekly Raid: Fortress of Chaos)
- **퀘스트 ID**: WEEKLY_RAID_BOSS
- **카테고리**: WEEKLY
- **레벨 요구사항**: 40
- **최대 레벨**: 제한 없음
- **선행 퀘스트**: TUTORIAL_BASIC_COMBAT (필수)
- **반복 가능**: 예 (주간 리셋)
- **구현 클래스**: WeeklyRaidBossQuest
- **파일 위치**: `src/main/java/com/febrie/rpg/quest/impl/weekly/WeeklyRaidBossQuest.java`
- **설명**: 매주 초기화되는 최고 난이도의 레이드 던전. 혼돈의 군주와 그의 부하들을 물리치고 전설적인 보상을 획듍하세요!

**⚠️ 경고**:
- 매우 어려운 레이드
- 권장 인원: 3-5명의 파티
- PvP 구역 포함

**던전 구성**:
- 1구역: 혼돈의 전당
- 2구역: 어둠의 성소
- 3구역: 왕좌의 방 (최종 보스)

**목표** (비순차적 진행 가능 - sequential: false):

**[준비 단계]**
1. **레이드 사령관과 대화** (INTERACT_NPC)
   - NPC Code: `raid_commander`
   - ID: `raid_commander`
2. **레벨 요구사항 달성** (REACH_LEVEL)
   - 레벨: 40
   - ID: `level_requirement`
3. **레이드 열쇠 수집** (COLLECT_ITEM)
   - Material: TRIPWIRE_HOOK x3
   - ID: `raid_key`

**[레이드 던전 입장]**
4. **혼돈의 요새 입구 도착** (VISIT_LOCATION)
   - 위치: `chaos_fortress_entrance`
   - ID: `raid_entrance`

**[첫 번째 구역 - 혼돈의 전당]**
5. **혼돈의 하수인 처치** (KILL_MOB)
   - EntityType: WITHER_SKELETON x30
   - ID: `chaos_minions`
6. **혼돈의 기사 처치** (KILL_MOB)
   - EntityType: PIGLIN_BRUTE x20
   - ID: `chaos_knights`
7. **첫 번째 미니 보스 처치** (KILL_MOB)
   - EntityType: ELDER_GUARDIAN x1
   - ID: `mini_boss_1`
8. **혼돈의 파편 수집** (COLLECT_ITEM)
   - Material: NETHERITE_SCRAP x5
   - ID: `chaos_fragment`

**[두 번째 구역 - 어둠의 성소]**
9. **어둠의 성소 도착** (VISIT_LOCATION)
   - 위치: `chaos_fortress_sanctuary`
   - ID: `dark_sanctuary`
10. **어둠의 시련 생존** (SURVIVE_OBJECTIVE)
    - 시간: 300초 (5분)
    - ID: `darkness_trial`
11. **그림자 암살자 처치** (KILL_MOB)
    - EntityType: EVOKER x10
    - ID: `shadow_assassins`
12. **공허 방랑자 처치** (KILL_MOB)
    - EntityType: ENDERMAN x25
    - ID: `void_walkers`
13. **두 번째 미니 보스 처치** (KILL_MOB)
    - EntityType: RAVAGER x1
    - ID: `mini_boss_2`
14. **공허의 정수 수집** (COLLECT_ITEM)
    - Material: ENDER_EYE x10
    - ID: `void_essence`

**[최종 보스 구역]**
15. **왕좌의 방 도착** (VISIT_LOCATION)
    - 위치: `chaos_fortress_throne`
    - ID: `throne_room`
16. **PvP 구역 전투** (KILL_PLAYER)
    - 수량: 3명 처치
    - ID: `pvp_zone`
17. **보스 소환 비용 지불** (PAY_CURRENCY)
    - CurrencyType: GOLD x5000
    - ID: `boss_summon`
18. **혼돈의 군주 처치** (KILL_MOB)
    - EntityType: WITHER x1 (최종 보스)
    - ID: `chaos_lord`

**[보상 획듍]**
19. **전설의 전리품 획듍** (COLLECT_ITEM)
    - Material: NETHER_STAR x1
    - ID: `legendary_loot`
20. **레이드 완료 보고** (DELIVER_ITEM)
    - NPC: `raid_commander`
    - Material: NETHER_STAR x1
    - ID: `raid_complete`

**보상**:
- 경험치: 10,000
- 골드: 15,000
- 다이아몬드: 100
- 아이템:
  - 네더라이트 주괴 (NETHERITE_INGOT) x3
  - 마법이 부여된 책 (ENCHANTED_BOOK) x5
  - 신호기 (BEACON) x1

**NPC 대화 하이라이트**:
- 레이드 사령관: "용사여, 혼돈의 요새에 도전할 준비가 되었는가?"
- 레이드 사령관: "이곳은 매주 한 번만 도전할 수 있는 최고 난이도의 던전이네."
- 레이드 사령관: "혼돈의 군주와 그의 정예 부대, 그리고... 다른 모험자들이지."
- 레이드 사령관: "맞아, 왕좌의 방 앞에는 PvP 구역이 있어. 경쟁도 피할 수 없다네."
- 레이드 사령관: "하지만 먼저 레이드 열쇠 3개를 모아와야 해. 그래야 입장할 수 있다네."

**특징**:
- 주간 퀘스트 (weekly: true)
- 대규모 레이드 콘텐츠
- PvP 요소 포함
- 파티 플레이 권장

#### 93. 주간 길드 기여 (Weekly Guild Contribution)
- **퀘스트 ID**: WEEKLY_GUILD_CONTRIBUTION
- **카테고리**: WEEKLY
- **레벨 요구사항**: 30

#### 94. 주간 PVP 토너먼트 (Weekly PVP Tournament)
- **퀘스트 ID**: WEEKLY_PVP_TOURNAMENT
- **카테고리**: WEEKLY
- **레벨 요구사항**: 40

#### 95. 주간 월드 보스 (Weekly World Boss)
- **퀘스트 ID**: WEEKLY_WORLD_BOSS
- **카테고리**: WEEKLY
- **레벨 요구사항**: 60
- **구현 클래스**: WeeklyWorldBossQuest

#### 96. 주간 자원 수집 (Weekly Resource Gathering)
- **퀘스트 ID**: WEEKLY_RESOURCE_GATHERING
- **카테고리**: WEEKLY
- **레벨 요구사항**: 20

#### 97. 주간 엘리트 사냥 (Weekly Elite Hunting)
- **퀘스트 ID**: WEEKLY_ELITE_HUNTING
- **카테고리**: WEEKLY
- **레벨 요구사항**: 45

#### 98. 주간 고대 유적 (Weekly Ancient Ruins)
- **퀘스트 ID**: WEEKLY_ANCIENT_RUINS
- **카테고리**: WEEKLY
- **레벨 요구사항**: 55

#### 99. 주간 진영 전쟁 (Weekly Faction War)
- **퀘스트 ID**: WEEKLY_FACTION_WAR
- **카테고리**: WEEKLY
- **레벨 요구사항**: 50

#### 100. 주간 전설 제작 (Weekly Legendary Craft)
- **퀘스트 ID**: WEEKLY_LEGENDARY_CRAFT
- **카테고리**: WEEKLY
- **레벨 요구사항**: 70

#### 101. 주간 생존 도전 (Weekly Survival Challenge)
- **퀘스트 ID**: WEEKLY_SURVIVAL_CHALLENGE
- **카테고리**: WEEKLY
- **레벨 요구사항**: 65

---

## 반복 퀘스트 (REPEATABLE)

#### 102. 몬스터 토벌 (Monster Extermination)
- **퀘스트 ID**: REPEAT_MONSTER_EXTERMINATION
- **카테고리**: REPEATABLE
- **레벨 요구사항**: 10
- **구현 클래스**: MonsterExterminationQuest

#### 103. 자원 수집 (Resource Collection)
- **퀘스트 ID**: REPEAT_RESOURCE_COLLECTION
- **카테고리**: REPEATABLE
- **레벨 요구사항**: 5

#### 104. 장비 강화 (Equipment Upgrade)
- **퀘스트 ID**: REPEAT_EQUIPMENT_UPGRADE
- **카테고리**: REPEATABLE
- **레벨 요구사항**: 15

#### 105. 무역로 (Trade Route)
- **퀘스트 ID**: REPEAT_TRADE_ROUTE
- **카테고리**: REPEATABLE
- **레벨 요구사항**: 20

#### 106. 경비 임무 (Guard Duty)
- **퀘스트 ID**: REPEAT_GUARD_DUTY
- **카테고리**: REPEATABLE
- **레벨 요구사항**: 25

#### 107. 정찰 임무 (Scout Mission)
- **퀘스트 ID**: REPEAT_SCOUT_MISSION
- **카테고리**: REPEATABLE
- **레벨 요구사항**: 30

#### 108. 보급품 운송 (Supply Run)
- **퀘스트 ID**: REPEAT_SUPPLY_RUN
- **카테고리**: REPEATABLE
- **레벨 요구사항**: 15

#### 109. 연구 지원 (Research Assistance)
- **퀘스트 ID**: REPEAT_RESEARCH_ASSISTANCE
- **카테고리**: REPEATABLE
- **레벨 요구사항**: 35

#### 110. 훈련용 더미 (Training Dummy)
- **퀘스트 ID**: REPEAT_TRAINING_DUMMY
- **카테고리**: REPEATABLE
- **레벨 요구사항**: 5

#### 111. 유물 수집 (Artifact Collection)
- **퀘스트 ID**: REPEAT_ARTIFACT_COLLECTION
- **카테고리**: REPEATABLE
- **레벨 요구사항**: 40

---

## 특수 퀘스트 (SPECIAL)

특수 퀘스트는 특별한 조건을 충족해야만 시작할 수 있는 희귀한 퀘스트입니다.

### 112. 숨겨진 직업 - 그림자의 길 (Hidden Class - Path of Shadows)
- **퀘스트 ID**: SPECIAL_HIDDEN_CLASS
- **카테고리**: SPECIAL
- **레벨 요구사항**: 35
- **최대 레벨**: 제한 없음
- **선행 퀘스트**: TUTORIAL_BASIC_COMBAT (필수)
- **반복 가능**: 아니오 (한 번만 가능)
- **구현 클래스**: HiddenClassQuest
- **파일 위치**: `src/main/java/com/febrie/rpg/quest/impl/special/HiddenClassQuest.java`
- **설명**: 어둠 속에서만 볼 수 있는 길, 그림자 길드의 비밀스러운 시련을 통과하고 숨겨진 직업을 얻는 특별한 퀘스트

**⚠️ 경고**: 이 퀘스트는 매우 위험하며 PvP가 포함됩니다. 한 번 시작하면 돌이킬 수 없습니다.

**목표** (순차적 진행 필수 - sequential: true):

**[비밀 발견 단계]**
1. **신비한 편지 획득** (COLLECT_ITEM)
   - Material: PAPER x1
   - ID: `mysterious_letter`
2. **비밀 장소 방문** (VISIT_LOCATION)
   - 위치: `shadow_guild_entrance`
   - ID: `secret_location`
3. **비밀 노크 사용** (COLLECT_ITEM)
   - Material: STICK x1 (비밀 노크용)
   - ID: `use_secret_knock`

**[그림자 길드 입단 시험]**
4. **그림자 마스터와 대화** (INTERACT_NPC)
   - NPC Code: `shadow_master`
   - ID: `shadow_master`
5. **어둠 속 생존** (SURVIVE_OBJECTIVE)
   - 시간: 600초 (10분)
   - ID: `darkness_test`
6. **PvP 시험** (KILL_PLAYER)
   - 수량: 1명
   - ID: `pvp_test`

**[첫 번째 시련 - 은신술]**
7. **은신 훈련장 도착** (VISIT_LOCATION)
   - 위치: `shadow_training_ground`
   - ID: `stealth_course`
8. **은신 생존** (SURVIVE_OBJECTIVE)
   - 시간: 300초 (5분간 발각되지 않기)
   - ID: `stealth_test`
9. **그림자 정수 수집** (COLLECT_ITEM)
   - Material: BLACK_DYE x10
   - ID: `shadow_essence`
10. **소리없이 처치** (KILL_MOB)
    - EntityType: ZOMBIE x20
    - ID: `silent_kills`

**[두 번째 시련 - 독술]**
11. **독 재료 수집** (COLLECT_ITEM)
    - Material: SPIDER_EYE x15
    - ID: `poison_ingredients`
12. **발효된 거미 눈 수집** (COLLECT_ITEM)
    - Material: FERMENTED_SPIDER_EYE x10
    - ID: `fermented_eyes`
13. **독 포션 제작** (CRAFT_ITEM)
    - Material: POTION x5
    - ID: `craft_poisons`
14. **독 시험** (KILL_MOB)
    - EntityType: CAVE_SPIDER x30
    - ID: `poison_test`

**[세 번째 시련 - 정보 수집]**
15. **정보상과 대화** (INTERACT_NPC)
    - NPC Code: `spy_merchant`
    - ID: `spy_merchant`
16. **정보원 뇌물** (PAY_CURRENCY)
    - CurrencyType: GOLD x2000
    - ID: `bribe_informant`
17. **비밀 문서 수집** (COLLECT_ITEM)
    - Material: WRITTEN_BOOK x3
    - ID: `secret_documents`
18. **정보 전달** (DELIVER_ITEM)
    - NPC: `shadow_master`
    - Material: WRITTEN_BOOK x3
    - ID: `deliver_intel`

**[네 번째 시련 - 암살 임무]**
19. **표적 위치 도달** (VISIT_LOCATION)
    - 위치: `noble_mansion`
    - ID: `target_location`
20. **타락한 귀족 처치** (KILL_MOB)
    - EntityType: VINDICATOR x1
    - ID: `corrupt_noble`
21. **귀족의 인장 획듍** (COLLECT_ITEM)
    - Material: GOLD_NUGGET x1
    - ID: `noble_seal`
22. **경비 회피** (SURVIVE_OBJECTIVE)
    - 시간: 180초 (3분)
    - ID: `escape_guards`

**[최종 시련 - 그림자와의 계약]**
23. **그림자 성소 도달** (VISIT_LOCATION)
    - 위치: `shadow_guild_sanctum`
    - ID: `shadow_sanctum`
24. **그림자 재료 수집** (COLLECT_ITEM)
    - Material: OBSIDIAN x20
    - ID: `shadow_materials`
25. **그림자 제단 설치** (PLACE_BLOCK)
    - Material: OBSIDIAN x9
    - ID: `shadow_altar`
26. **희생물 바치기** (COLLECT_ITEM)
    - Material: ENDER_PEARL x10
    - ID: `shadow_sacrifice`
27. **그림자 수호자 처치** (KILL_MOB)
    - EntityType: ENDERMAN x1
    - ID: `shadow_guardian`
28. **최종 의식** (INTERACT_NPC)
    - NPC Code: `shadow_master`
    - ID: `final_ceremony`
29. **그림자의 표식 받기** (COLLECT_ITEM)
    - Material: PLAYER_HEAD x1
    - ID: `shadow_mark`

**선택 가능한 숨겨진 직업**:
- 그림자 암살자 (Shadow Assassin) - 일격필살의 달인
- 어둠의 마법사 (Dark Sorcerer) - 금지된 마법 사용자
- 밤의 추적자 (Night Stalker) - 추적과 정찰의 전문가

**보상**:
- 경험치: 7,500
- 골드: 7,500
- 다이아몬드: 50
- 아이템:
  - 그림자의 검 (NETHERITE_SWORD) x1
  - 은신 갑옷 (LEATHER_CHESTPLATE) x1
  - 특수 포션 (POTION) x10
  - 엔더 진주 (ENDER_PEARL) x16

**NPC 대화 하이라이트**:
- ???: "이 편지를 읽고 있다면, 당신은 선택받은 자입니다..."
- ???: "자정에 버려진 우물 앞에서 막대기로 세 번 두드리시오."
- 그림자 마스터: "오랜만에 가능성 있는 자가 왔군. 그림자의 길을 걸을 준비가 되었나?"
- 그림자 마스터: "이 길은 돌이킬 수 없네. 한번 발을 들이면 끝까지 가야 해."
- 그림자 마스터: "모든 시련을 통과했다. 이제 너의 길을 선택할 때다."
- 그림자 마스터: "이제 너는 우리 중 하나다. 그림자의 표식을 받아라."

**특징**:
- 총 29개의 복잡한 목표
- 다양한 목표 타입 포함
- 스토리 중심의 몰입도 높은 퀘스트
- 한 번만 수행 가능 (repeatable: false)

#### 113. 전설의 무기 (Legendary Weapon)
- **퀘스트 ID**: SPECIAL_LEGENDARY_WEAPON
- **카테고리**: SPECIAL
- **레벨 요구사항**: 60
- **구현 클래스**: LegendaryWeaponQuest

#### 114. 고대의 힘 (Ancient Power)
- **퀘스트 ID**: SPECIAL_ANCIENT_POWER
- **카테고리**: SPECIAL
- **레벨 요구사항**: 70
- **구현 클래스**: AncientPowerQuest

#### 115. 시간 제한 (Time Limited)
- **퀘스트 ID**: SPECIAL_TIME_LIMITED
- **카테고리**: SPECIAL
- **레벨 요구사항**: 40

#### 116. 비밀 결사 (Secret Society)
- **퀘스트 ID**: SPECIAL_SECRET_SOCIETY
- **카테고리**: SPECIAL
- **레벨 요구사항**: 55

#### 117. 신의 축복 (Divine Blessing)
- **퀘스트 ID**: SPECIAL_DIVINE_BLESSING
- **카테고리**: SPECIAL
- **레벨 요구사항**: 80

#### 118. 저주 해제 (Curse Removal)
- **퀘스트 ID**: SPECIAL_CURSE_REMOVAL
- **카테고리**: SPECIAL
- **레벨 요구사항**: 65

#### 119. 차원 여행자 (Dimension Traveler)
- **퀘스트 ID**: SPECIAL_DIMENSION_TRAVELER
- **카테고리**: SPECIAL
- **레벨 요구사항**: 75
- **구현 클래스**: DimensionTravelerQuest

#### 120. 신화의 야수 (Mythic Beast)
- **퀘스트 ID**: SPECIAL_MYTHIC_BEAST
- **카테고리**: SPECIAL
- **레벨 요구사항**: 90
- **구현 클래스**: MythicBeastQuest

#### 121. 세계수 (World Tree)
- **퀘스트 ID**: SPECIAL_WORLD_TREE
- **카테고리**: SPECIAL
- **레벨 요구사항**: 100
- **구현 클래스**: WorldTreeQuest

---

## 직업 발전 퀘스트 (ADVANCEMENT)

#### 122. 전사 승급 (Warrior Advancement)
- **퀘스트 ID**: CLASS_WARRIOR_ADVANCEMENT
- **카테고리**: ADVANCEMENT
- **레벨 요구사항**: 30
- **구현 클래스**: WarriorAdvancementQuest

#### 123. 마법사 깨달음 (Mage Enlightenment)
- **퀘스트 ID**: CLASS_MAGE_ENLIGHTENMENT
- **카테고리**: ADVANCEMENT
- **레벨 요구사항**: 30
- **구현 클래스**: MageEnlightenmentQuest

#### 124. 궁수 정밀 (Archer Precision)
- **퀘스트 ID**: CLASS_ARCHER_PRECISION
- **카테고리**: ADVANCEMENT
- **레벨 요구사항**: 30

#### 125. 도적 그림자 (Rogue Shadows)
- **퀘스트 ID**: CLASS_ROGUE_SHADOWS
- **카테고리**: ADVANCEMENT
- **레벨 요구사항**: 30

#### 126. 사제 헌신 (Priest Devotion)
- **퀘스트 ID**: CLASS_PRIEST_DEVOTION
- **카테고리**: ADVANCEMENT
- **레벨 요구사항**: 30

#### 127. 성기사 서약 (Paladin Oath)
- **퀘스트 ID**: CLASS_PALADIN_OATH
- **카테고리**: ADVANCEMENT
- **레벨 요구사항**: 40
- **구현 클래스**: PaladinOathQuest

#### 128. 네크로맨서 계약 (Necromancer Pact)
- **퀘스트 ID**: CLASS_NECROMANCER_PACT
- **카테고리**: ADVANCEMENT
- **레벨 요구사항**: 40

#### 129. 드루이드 자연 (Druid Nature)
- **퀘스트 ID**: CLASS_DRUID_NATURE
- **카테고리**: ADVANCEMENT
- **레벨 요구사항**: 40

#### 130. 광전사 분노 (Berserker Rage)
- **퀘스트 ID**: CLASS_BERSERKER_RAGE
- **카테고리**: ADVANCEMENT
- **레벨 요구사항**: 40

#### 131. 소환사 유대 (Summoner Bond)
- **퀘스트 ID**: CLASS_SUMMONER_BOND
- **카테고리**: ADVANCEMENT
- **레벨 요구사항**: 40

---

## 시즌/이벤트 퀘스트 (EVENT)

#### 132. 봄 축제 (Spring Festival)
- **퀘스트 ID**: SEASON_SPRING_FESTIVAL
- **카테고리**: EVENT
- **레벨 요구사항**: 10
- **구현 클래스**: SpringFestivalQuest

#### 133. 여름 지점 (Summer Solstice)
- **퀘스트 ID**: SEASON_SUMMER_SOLSTICE
- **카테고리**: EVENT
- **레벨 요구사항**: 10

#### 134. 가을 수확 (Autumn Harvest)
- **퀘스트 ID**: SEASON_AUTUMN_HARVEST
- **카테고리**: EVENT
- **레벨 요구사항**: 10

#### 135. 겨울 서리 (Winter Frost)
- **퀘스트 ID**: SEASON_WINTER_FROST
- **카테고리**: EVENT
- **레벨 요구사항**: 10

#### 136. 새해 (New Year)
- **퀘스트 ID**: SEASON_NEW_YEAR
- **카테고리**: EVENT
- **레벨 요구사항**: 5

#### 137. 할로윈 밤 (Halloween Night)
- **퀘스트 ID**: SEASON_HALLOWEEN_NIGHT
- **카테고리**: EVENT
- **레벨 요구사항**: 15
- **구현 클래스**: HalloweenNightQuest

#### 138. 발렌타인 사랑 (Valentine Love)
- **퀘스트 ID**: SEASON_VALENTINE_LOVE
- **카테고리**: EVENT
- **레벨 요구사항**: 10

#### 139. 부활절 달걀 (Easter Eggs)
- **퀘스트 ID**: SEASON_EASTER_EGGS
- **카테고리**: EVENT
- **레벨 요구사항**: 10

#### 140. 추수감사절 (Thanksgiving)
- **퀘스트 ID**: SEASON_THANKSGIVING
- **카테고리**: EVENT
- **레벨 요구사항**: 10

#### 141. 크리스마스 정신 (Christmas Spirit)
- **퀘스트 ID**: SEASON_CHRISTMAS_SPIRIT
- **카테고리**: EVENT
- **레벨 요구사항**: 10

---

## 길드 퀘스트 (GUILD)

#### 142. 길드 설립 (Guild Establishment)
- **퀘스트 ID**: GUILD_ESTABLISHMENT
- **카테고리**: GUILD
- **레벨 요구사항**: 20
- **구현 클래스**: GuildEstablishmentQuest

#### 143. 길드 요새 공성 (Guild Fortress Siege)
- **퀘스트 ID**: GUILD_FORTRESS_SIEGE
- **카테고리**: GUILD
- **레벨 요구사항**: 50
- **구현 클래스**: GuildFortressSiegeQuest

#### 144. 길드 자원 전쟁 (Guild Resource War)
- **퀘스트 ID**: GUILD_RESOURCE_WAR
- **카테고리**: GUILD
- **레벨 요구사항**: 40

#### 145. 길드 동맹 결성 (Guild Alliance Formation)
- **퀘스트 ID**: GUILD_ALLIANCE_FORMATION
- **카테고리**: GUILD
- **레벨 요구사항**: 35

#### 146. 길드 명성 쌓기 (Guild Reputation Building)
- **퀘스트 ID**: GUILD_REPUTATION_BUILDING
- **카테고리**: GUILD
- **레벨 요구사항**: 30

#### 147. 길드 상인 계약 (Guild Merchant Contract)
- **퀘스트 ID**: GUILD_MERCHANT_CONTRACT
- **카테고리**: GUILD
- **레벨 요구사항**: 25

#### 148. 길드 영토 확장 (Guild Territory Expansion)
- **퀘스트 ID**: GUILD_TERRITORY_EXPANSION
- **카테고리**: GUILD
- **레벨 요구사항**: 45

#### 149. 길드 외교 임무 (Guild Diplomatic Mission)
- **퀘스트 ID**: GUILD_DIPLOMATIC_MISSION
- **카테고리**: GUILD
- **레벨 요구사항**: 35

#### 150. 길드 금고 강탈 (Guild Treasury Heist)
- **퀘스트 ID**: GUILD_TREASURY_HEIST
- **카테고리**: GUILD
- **레벨 요구사항**: 55

#### 151. 길드 전설 업적 (Guild Legendary Achievement)
- **퀘스트 ID**: GUILD_LEGENDARY_ACHIEVEMENT
- **카테고리**: GUILD
- **레벨 요구사항**: 60

---

## 탐험 퀘스트 (EXPLORATION)

#### 152. 잃어버린 대륙 (Lost Continent)
- **퀘스트 ID**: EXPLORE_LOST_CONTINENT
- **카테고리**: EXPLORATION
- **레벨 요구사항**: 70

#### 153. 하늘 섬 (Sky Islands)
- **퀘스트 ID**: EXPLORE_SKY_ISLANDS
- **카테고리**: EXPLORATION
- **레벨 요구사항**: 60

#### 154. 지하 왕국 (Underground Kingdom)
- **퀘스트 ID**: EXPLORE_UNDERGROUND_KINGDOM
- **카테고리**: EXPLORATION
- **레벨 요구사항**: 50

#### 155. 차원 결절점 (Dimensional Nexus)
- **퀘스트 ID**: EXPLORE_DIMENSIONAL_NEXUS
- **카테고리**: EXPLORATION
- **레벨 요구사항**: 80

#### 156. 고대 도서관 (Ancient Library)
- **퀘스트 ID**: EXPLORE_ANCIENT_LIBRARY
- **카테고리**: EXPLORATION
- **레벨 요구사항**: 45

#### 157. 타이탄 유적 (Titan Remains)
- **퀘스트 ID**: EXPLORE_TITAN_REMAINS
- **카테고리**: EXPLORATION
- **레벨 요구사항**: 90

#### 158. 원소 차원 (Elemental Planes)
- **퀘스트 ID**: EXPLORE_ELEMENTAL_PLANES
- **카테고리**: EXPLORATION
- **레벨 요구사항**: 75

#### 159. 공허 개척지 (Void Frontier)
- **퀘스트 ID**: EXPLORE_VOID_FRONTIER
- **카테고리**: EXPLORATION
- **레벨 요구사항**: 85

#### 160. 천상계 (Celestial Realm)
- **퀘스트 ID**: EXPLORE_CELESTIAL_REALM
- **카테고리**: EXPLORATION
- **레벨 요구사항**: 95

#### 161. 심연의 깊이 (Abyssal Depths)
- **퀘스트 ID**: EXPLORE_ABYSSAL_DEPTHS
- **카테고리**: EXPLORATION
- **레벨 요구사항**: 100

---

## 제작 퀘스트 (CRAFTING)

#### 162. 대장장이 마스터 (Master Blacksmith)
- **퀘스트 ID**: CRAFT_MASTER_BLACKSMITH
- **카테고리**: CRAFTING
- **레벨 요구사항**: 30
- **구현 클래스**: MasterBlacksmithQuest

#### 163. 물약 양조 (Potion Brewing)
- **퀘스트 ID**: CRAFT_POTION_BREWING
- **카테고리**: CRAFTING
- **레벨 요구사항**: 25

#### 164. 인챈트 숙달 (Enchantment Mastery)
- **퀘스트 ID**: CRAFT_ENCHANTMENT_MASTERY
- **카테고리**: CRAFTING
- **레벨 요구사항**: 35

#### 165. 룬 각인 (Rune Inscription)
- **퀘스트 ID**: CRAFT_RUNE_INSCRIPTION
- **카테고리**: CRAFTING
- **레벨 요구사항**: 40

#### 166. 보석 세공 (Jewel Cutting)
- **퀘스트 ID**: CRAFT_JEWEL_CUTTING
- **카테고리**: CRAFTING
- **레벨 요구사항**: 45

#### 167. 갑옷 단조 (Armor Forging)
- **퀘스트 ID**: CRAFT_ARMOR_FORGING
- **카테고리**: CRAFTING
- **레벨 요구사항**: 50

#### 168. 무기 담금질 (Weapon Tempering)
- **퀘스트 ID**: CRAFT_WEAPON_TEMPERING
- **카테고리**: CRAFTING
- **레벨 요구사항**: 55

#### 169. 액세서리 디자인 (Accessory Design)
- **퀘스트 ID**: CRAFT_ACCESSORY_DESIGN
- **카테고리**: CRAFTING
- **레벨 요구사항**: 60

#### 170. 소모품 제작 (Consumable Creation)
- **퀘스트 ID**: CRAFT_CONSUMABLE_CREATION
- **카테고리**: CRAFTING
- **레벨 요구사항**: 20

#### 171. 전설 레시피 (Legendary Recipe)
- **퀘스트 ID**: CRAFT_LEGENDARY_RECIPE
- **카테고리**: CRAFTING
- **레벨 요구사항**: 70

---

## 생활 퀘스트 (LIFE)

#### 172. 요리 달인 (Master Chef)
- **퀘스트 ID**: LIFE_MASTER_CHEF
- **카테고리**: LIFE
- **레벨 요구사항**: 15
- **구현 클래스**: MasterChefQuest

#### 173. 농사 전문가 (Farming Expert)
- **퀘스트 ID**: LIFE_FARMING_EXPERT
- **카테고리**: LIFE
- **레벨 요구사항**: 10

#### 174. 동물 조련사 (Animal Tamer)
- **퀘스트 ID**: LIFE_ANIMAL_TAMER
- **카테고리**: LIFE
- **레벨 요구사항**: 20

#### 175. 상인 거물 (Merchant Tycoon)
- **퀘스트 ID**: LIFE_MERCHANT_TYCOON
- **카테고리**: LIFE
- **레벨 요구사항**: 30

#### 176. 고고학자 (Archaeologist)
- **퀘스트 ID**: LIFE_ARCHAEOLOGIST
- **카테고리**: LIFE
- **레벨 요구사항**: 35

#### 177. 보물 감정사 (Treasure Appraiser)
- **퀘스트 ID**: LIFE_TREASURE_APPRAISER
- **카테고리**: LIFE
- **레벨 요구사항**: 40

#### 178. 몬스터 연구가 (Monster Researcher)
- **퀘스트 ID**: LIFE_MONSTER_RESEARCHER
- **카테고리**: LIFE
- **레벨 요구사항**: 25

#### 179. 지도 제작자 (Cartographer)
- **퀘스트 ID**: LIFE_CARTOGRAPHER
- **카테고리**: LIFE
- **레벨 요구사항**: 45

#### 180. 외교관 (Diplomat)
- **퀘스트 ID**: LIFE_DIPLOMAT
- **카테고리**: LIFE
- **레벨 요구사항**: 50

#### 181. 수집가 (Collector)
- **퀘스트 ID**: LIFE_COLLECTOR
- **카테고리**: LIFE
- **레벨 요구사항**: 55

---

## 전투 퀘스트 (COMBAT)

#### 182. 투기장 검투사 (Arena Gladiator)
- **퀘스트 ID**: COMBAT_ARENA_GLADIATOR
- **카테고리**: COMBAT
- **레벨 요구사항**: 30
- **구현 클래스**: ArenaGladiatorQuest

#### 183. 보스 처치자 (Boss Slayer)
- **퀘스트 ID**: COMBAT_BOSS_SLAYER
- **카테고리**: COMBAT
- **레벨 요구사항**: 50

#### 184. 생존 전문가 (Survival Expert)
- **퀘스트 ID**: COMBAT_SURVIVAL_EXPERT
- **카테고리**: COMBAT
- **레벨 요구사항**: 40

#### 185. 콤보 마스터 (Combo Master)
- **퀘스트 ID**: COMBAT_COMBO_MASTER
- **카테고리**: COMBAT
- **레벨 요구사항**: 35

#### 186. 방어 전문가 (Defense Specialist)
- **퀘스트 ID**: COMBAT_DEFENSE_SPECIALIST
- **카테고리**: COMBAT
- **레벨 요구사항**: 45

#### 187. 원소 전사 (Elemental Warrior)
- **퀘스트 ID**: COMBAT_ELEMENTAL_WARRIOR
- **카테고리**: COMBAT
- **레벨 요구사항**: 55

#### 188. 치명타 공격자 (Critical Striker)
- **퀘스트 ID**: COMBAT_CRITICAL_STRIKER
- **카테고리**: COMBAT
- **레벨 요구사항**: 60

#### 189. 스피드 데몬 (Speed Demon)
- **퀘스트 ID**: COMBAT_SPEED_DEMON
- **카테고리**: COMBAT
- **레벨 요구사항**: 65

#### 190. 탱크 파괴자 (Tank Destroyer)
- **퀘스트 ID**: COMBAT_TANK_DESTROYER
- **카테고리**: COMBAT
- **레벨 요구사항**: 70

#### 191. 암살자의 그림자 (Assassin Shadow)
- **퀘스트 ID**: COMBAT_ASSASSIN_SHADOW
- **카테고리**: COMBAT
- **레벨 요구사항**: 75

---

## 스토리 분기 퀘스트 (BRANCH)

#### 192. 빛의 성기사 (Light Paladin)
- **퀘스트 ID**: BRANCH_LIGHT_PALADIN
- **카테고리**: BRANCH
- **레벨 요구사항**: 50
- **구현 클래스**: LightPaladinQuest

#### 193. 어둠의 기사 (Dark Knight)
- **퀘스트 ID**: BRANCH_DARK_KNIGHT
- **카테고리**: BRANCH
- **레벨 요구사항**: 50

#### 194. 중립 수호자 (Neutral Guardian)
- **퀘스트 ID**: BRANCH_NEUTRAL_GUARDIAN
- **카테고리**: BRANCH
- **레벨 요구사항**: 50

#### 195. 혼돈의 인도자 (Chaos Bringer)
- **퀘스트 ID**: BRANCH_CHAOS_BRINGER
- **카테고리**: BRANCH
- **레벨 요구사항**: 60

#### 196. 질서의 수호자 (Order Keeper)
- **퀘스트 ID**: BRANCH_ORDER_KEEPER
- **카테고리**: BRANCH
- **레벨 요구사항**: 60

#### 197. 자연의 보호자 (Nature Protector)
- **퀘스트 ID**: BRANCH_NATURE_PROTECTOR
- **카테고리**: BRANCH
- **레벨 요구사항**: 55

#### 198. 기술의 개척자 (Technology Pioneer)
- **퀘스트 ID**: BRANCH_TECHNOLOGY_PIONEER
- **카테고리**: BRANCH
- **레벨 요구사항**: 55

#### 199. 마법의 탐구자 (Magic Seeker)
- **퀘스트 ID**: BRANCH_MAGIC_SEEKER
- **카테고리**: BRANCH
- **레벨 요구사항**: 55

#### 200. 균형의 유지자 (Balance Maintainer)
- **퀘스트 ID**: BRANCH_BALANCE_MAINTAINER
- **카테고리**: BRANCH
- **레벨 요구사항**: 65

#### 201. 운명의 도전자 (Fate Defier)
- **퀘스트 ID**: BRANCH_FATE_DEFIER
- **카테고리**: BRANCH
- **레벨 요구사항**: 70

---

## 구현 상태 요약

### 퀘스트 시스템 기능

**퀘스트 관리 기능**:
- 플레이어별 진행 상황 추적
- 자동 리셋 시스템 (일일/주간)
- 보상 지급 시스템
- 다국어 지원 (LangManager)
- NPC 대화 시스템
- 다중 목표 추적

**목표 타입 (Objective Types)**:
- KILL_MOB - 몬스터 처치
- COLLECT_ITEM - 아이템 수집
- VISIT_LOCATION - 특정 위치 방문
- INTERACT_NPC - NPC와 상호작용
- CRAFT_ITEM - 아이템 제작
- PLACE_BLOCK - 블록 설치
- BREAK_BLOCK - 블록 파괴
- FISHING_OBJECTIVE - 낚시
- SURVIVE_OBJECTIVE - 생존 시간
- DELIVER_ITEM - 아이템 전달
- PAY_CURRENCY - 화폐 지불
- REACH_LEVEL - 레벨 도달
- KILL_PLAYER - 플레이어 처치
- USE_SKILL - 스킬 사용
- ENCHANT - 인챈트
- TAME - 길들이기
- TRADE - 거래
- EQUIP - 장비 착용
- CONSUME - 아이템 소비

### 완전히 구현된 퀘스트 (46개)
다음 퀘스트들은 실제 Java 클래스로 구현되어 있습니다:

**튜토리얼 (2개)**
- FirstStepsQuest
- BasicCombatQuest

**메인 퀘스트 (10개)**
- HeroesJourneyQuest
- PathOfLightQuest
- PathOfDarknessQuest
- AncientProphecyQuest
- ChosenOneQuest
- FirstTrialQuest
- ElementalStonesQuest
- GuardianAwakeningQuest
- ShadowInvasionQuest
- DragonAwakeningQuest

**사이드 퀘스트 (5개)**
- FarmersRequestQuest
- CollectHerbsQuest
- LostTreasureQuest
- BlacksmithApprenticeQuest
- AncientCipherQuest

**일일 퀘스트 (7개)**
- DailyHuntingQuest
- DailyMiningQuest
- DailyFishingQuest
- DailyGatheringQuest
- DailyCraftingQuest
- DailyDeliveryQuest
- DailyExplorationQuest
- DailyBountyHunterQuest

**주간 퀘스트 (2개)**
- WeeklyRaidBossQuest
- WeeklyWorldBossQuest

**반복 퀘스트 (1개)**
- MonsterExterminationQuest

**특수 퀘스트 (5개)**
- HiddenClassQuest
- LegendaryWeaponQuest
- AncientPowerQuest
- DimensionTravelerQuest
- MythicBeastQuest
- WorldTreeQuest

**직업 발전 퀘스트 (3개)**
- WarriorAdvancementQuest
- MageEnlightenmentQuest
- PaladinOathQuest

**이벤트 퀘스트 (2개)**
- SpringFestivalQuest
- HalloweenNightQuest

**길드 퀘스트 (2개)**
- GuildEstablishmentQuest
- GuildFortressSiegeQuest

**탐험 퀘스트 (1개)**
- AncientRuinsQuest (exploration 폴더)

**제작 퀘스트 (1개)**
- MasterBlacksmithQuest

**생활 퀘스트 (1개)**
- MasterChefQuest

**전투 퀘스트 (1개)**
- ArenaGladiatorQuest

**분기 퀘스트 (1개)**
- LightPaladinQuest

### 미구현 퀘스트 (217개)
나머지 217개의 퀘스트는 QuestID enum에만 정의되어 있고 실제 구현 클래스가 없습니다.

---

## 개발자 노트

### 퀘스트 추가 방법
1. `QuestID` enum에 새 ID 추가
2. 해당 카테고리의 impl 폴더에 클래스 생성
3. `QuestBuilder` 패턴 사용하여 구현
4. `QuestManager`에 등록
5. 이 문서 업데이트

### 퀘스트 빌더 주요 설정
- `.id()` - 퀘스트 ID (QuestID enum)
- `.objectives()` - 목표 리스트
- `.reward()` - 보상 설정
- `.sequential()` - 순차 진행 여부
- `.repeatable()` - 반복 가능 여부
- `.daily()` / `.weekly()` - 일일/주간 설정
- `.category()` - 퀘스트 카테고리
- `.minLevel()` / `.maxLevel()` - 레벨 제한
- `.addPrerequisite()` - 선행 퀘스트

### 파일 구조
```
src/main/java/com/febrie/rpg/quest/
├── impl/
│   ├── tutorial/       # 튜토리얼 퀘스트
│   ├── main/          # 메인 퀘스트
│   ├── side/          # 사이드 퀘스트
│   ├── daily/         # 일일 퀘스트
│   ├── weekly/        # 주간 퀘스트
│   ├── special/       # 특수 퀘스트
│   ├── event/         # 이벤트 퀘스트
│   ├── guild/         # 길드 퀘스트
│   ├── advancement/   # 직업 발전 퀘스트
│   └── ...
├── objective/          # 목표 클래스
├── reward/            # 보상 클래스
└── dialog/            # 대화 클래스
```

---

이 문서는 Sypixel RPG의 전체 퀘스트 시스템을 상세히 문서화한 것입니다. 

**마지막 업데이트**: 2025년 1월
**총 퀘스트 수**: 263개 (구현 46개 / 미구현 217개)