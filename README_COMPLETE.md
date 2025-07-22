# 🎮 Sypixel RPG - 완전 가이드

<div align="center">

![Java](https://img.shields.io/badge/Java-24-orange.svg)
![Paper](https://img.shields.io/badge/Paper-1.21.7+-blue.svg)
![Firebase](https://img.shields.io/badge/Firebase-Firestore-yellow.svg)
![Citizens](https://img.shields.io/badge/Citizens-Required-green.svg)
![WorldGuard](https://img.shields.io/badge/WorldGuard-Optional-blue.svg)

<h2>⚔️ 마인크래프트 최고의 RPG 서버 플러그인 ⚔️</h2>

[플레이어 가이드](#-플레이어-가이드) • [직업 시스템](#-직업-시스템) • [퀘스트](#-퀘스트-시스템) • [섬 시스템](#-섬-시스템) • [경제](#-경제-시스템) • [개발자 가이드](#-개발자-가이드)

</div>

## 📋 목차

### 플레이어 섹션
- [🚀 빠른 시작 가이드](#-빠른-시작-가이드)
- [💼 직업 시스템 상세](#-직업-시스템-상세)
- [🎯 퀘스트 시스템](#-퀘스트-시스템)
- [🏝️ 섬 시스템](#-섬-시스템)
- [💰 경제 시스템](#-경제-시스템)
- [📊 스탯과 재능](#-스탯과-재능)
- [👥 소셜 기능](#-소셜-기능)
- [🎮 명령어 가이드](#-명령어-가이드)
- [⚙️ 설정](#-설정)

### 개발자 섹션
- [🏗️ 프로젝트 구조](#-프로젝트-구조)
- [📦 패키지별 상세 설명](#-패키지별-상세-설명)
- [🔧 시스템 아키텍처](#-시스템-아키텍처)
- [🌐 Firebase 설정](#-firebase-설정)
- [🔌 API & 확장](#-api--확장)

---

## 🚀 빠른 시작 가이드

### 첫 접속시 할 일

1. **튜토리얼 시작**
   - 서버 접속 시 자동으로 튜토리얼 퀘스트가 시작됩니다
   - `/메뉴` 또는 `Shift + F`로 메인 메뉴를 열 수 있습니다

2. **기본 튜토리얼 퀘스트**
   - **첫 걸음** (TUTORIAL_FIRST_STEPS)
     - 허브 지역 방문
     - 마을 상인 NPC와 대화
     - 보상: 골드 100, 기본 도구 세트, 경험치 50
   
   - **기초 전투** (TUTORIAL_BASIC_COMBAT) 
     - 좀비 5마리, 스켈레톤 3마리 처치
     - 보상: 골드 200, 철 장비, 경험치 100

3. **직업 선택**
   - 레벨 10 도달 시 직업 선택 가능
   - `/메뉴` → 프로필 → 직업 선택
   - ⚠️ **주의**: 직업은 한 번 선택하면 변경 불가능!

4. **웹사이트 계정 연동**
   ```
   /사이트계정발급 your@email.com
   ```
   - 12자리 비밀번호가 자동 생성됩니다
   - 클릭하여 클립보드에 복사 가능

---

## 💼 직업 시스템 상세

### 직업 카테고리와 특성

#### ⚔️ 전사 계열 (WARRIOR)
높은 체력과 방어력, 근접 전투에 특화

| 직업 | 최대 레벨 | 아이콘 | 특징 | 추천 스탯 | 재능 트리 |
|------|-----------|--------|------|-----------|-----------|
| **광전사** (BERSERKER) | 195 | ⚔ | 체력이 낮을수록 공격력 증가<br>흡혈 효과, 폭주 모드 | STR > VIT > DEX | 피의 광란, 불굴의 의지, 광폭화 |
| **브루저** (BRUISER) | 200 | 🛡 | 공격과 방어의 균형<br>반격 시스템 | STR = VIT > DEX | 강인함, 반격, 압도 |
| **탱커** (TANK) | 205 | 🏛 | 최고의 방어력<br>아군 보호, 도발 | VIT > STR > WIS | 철벽 방어, 수호자, 불굴 |

#### 🔮 마법사 계열 (MAGE)
마법 공격과 지원에 특화, 낮은 체력

| 직업 | 최대 레벨 | 아이콘 | 특징 | 추천 스탯 | 재능 트리 |
|------|-----------|--------|------|-----------|-----------|
| **프리스트** (PRIEST) | 115 | ✨ | 치유와 버프 전문<br>부활 능력 | WIS > INT > VIT | 신성 치유, 축복, 부활 |
| **암흑 마법사** (DARK_MAGE) | 120 | 🌑 | 강력한 광역 마법<br>생명력 흡수 | INT > WIS > DEX | 어둠의 힘, 흑마법, 영혼 흡수 |
| **메르시** (MERCY) | 125 | 💚 | 자연의 힘 활용<br>지속 치유, 보호막 | WIS = INT > VIT | 자연의 축복, 재생, 정화 |

#### 🏹 궁수 계열 (ARCHER)
원거리 공격과 높은 민첩성

| 직업 | 최대 레벨 | 아이콘 | 특징 | 추천 스탯 | 재능 트리 |
|------|-----------|--------|------|-----------|-----------|
| **궁수** (ARCHER) | 95 | 🏹 | 기본 원거리 딜러<br>정밀 사격 | DEX > STR > LUK | 정밀 사격, 은신, 다중 발사 |
| **저격수** (SNIPER) | 100 | 🎯 | 극한의 사거리<br>단일 대상 집중 | DEX > INT > LUK | 저격, 관통탄, 헤드샷 |
| **샷거너** (SHOTGUNNER) | 105 | 💥 | 근거리 산탄 공격<br>범위 피해 | STR > DEX > VIT | 산탄, 폭발탄, 제압 사격 |

### 레벨 시스템

각 직업별로 다른 경험치 요구량:
- **전사 계열**: 기본 경험치의 120% 필요
- **마법사 계열**: 기본 경험치의 100% 필요
- **궁수 계열**: 기본 경험치의 90% 필요

만렙 직전 레벨에서는 **2억 경험치** 필요!

---

## 🎯 퀘스트 시스템

### 퀘스트 카테고리

총 **260개**의 퀘스트가 계획되어 있으며, 현재 약 30개가 구현되어 있습니다.

#### 📚 튜토리얼 퀘스트 (2개 구현)
- 첫 걸음 (TUTORIAL_FIRST_STEPS)
- 기초 전투 (TUTORIAL_BASIC_COMBAT)

#### 📖 메인 스토리 퀘스트

**Chapter 1: 운명의 시작** (4개 구현)
1. **고대의 예언** (레벨 10+)
   - 천년전 예언서의 비밀을 파헤치는 퀘스트
   - 고대 두루마리 5개 수집
   - 보상: 골드 500, 다이아몬드 10, 경험치 1000

2. **선택받은 자의 시련** (레벨 15+)
   - 3가지 시련 통과: 용기, 지혜, 희생
   - 최종 보스: 위더 스켈레톤
   - 보상: 골드 2000, 네더라이트 검, 겉날개, 경험치 2000

3. **첫 번째 시험: 생존의 경기장** (레벨 20+)
   - 3차례 몬스터 웨이브 생존
   - 파괴자(Ravager) 보스전
   - 보상: 골드 3000, 다이아몬드 흉갑, 불사의 토템, 경험치 3000

4. **네 원소의 돌** (레벨 25+)
   - 불, 물, 대지, 바람의 사원 방문
   - 각 원소의 수호자 처치
   - 보상: 골드 5000, 신호기, 네더의 별 4개, 경험치 5000

#### 🌅 일일 퀘스트 (8개 구현, 매일 오전 6시 리셋)

| 퀘스트명 | 최소 레벨 | 주요 목표 | 보상 |
|----------|-----------|-----------|------|
| 일일 사냥 | 5 | 몬스터 45마리 처치 | 골드 200, 화살 64개, 경험치 150 |
| 일일 현상금 사냥 | 20 | 특수 몬스터 처치, 증거품 수집 | 골드 2,500, 다이아몬드 15개, 경험치 1,500 |
| 일일 제작 | 10 | 다양한 아이템 제작 | 골드 600, 다이아몬드 8개, 경험치 400 |
| 일일 긴급 배달 | 12 | 6곳에 순차 배달 (시간제한 포함) | 골드 800, 다이아몬드 10개, 경험치 500 |
| 일일 미지의 땅 탐험 | 15 | 5개 지역 탐험 및 특산품 수집 | 골드 3,000, 다이아몬드 20개, 경험치 2,000 |
| 일일 낚시 | 3 | 다양한 물고기 낚기 | 골드 200, 에메랄드 5개, 경험치 100 |
| 일일 자원 수집 | 5 | 광물, 작물, 자원 수집 | 골드 1,500, 다이아몬드 10개, 경험치 500 |
| 일일 채광 | 1 | 기본 광물 채굴 | 골드 150, 철 곡괭이, 경험치 100 |

#### 📅 주간 퀘스트 (2개 구현, 매주 리셋)

1. **주간 레이드: 혼돈의 요새** (레벨 40+)
   - 3개 구역 클리어, PvP 요소 포함
   - 최종 보스: 혼돈의 군주
   - 보상: 골드 15,000, 다이아몬드 100개, 네더라이트 주괴 3개, 경험치 10,000

2. **세계의 타이탄 토벌** (레벨 45+)
   - 서버 전체 협력 이벤트
   - 1,000,000 HP 월드 보스
   - 3단계 전투 (군단 → 4원소 장군 → 타이탄)
   - 보상: 골드 30,000, 다이아몬드 200개, 네더라이트 흉갑, 경험치 15,000

#### ⭐ 특별 퀘스트 (6개 구현, 1회만 완료 가능)

| 퀘스트명 | 레벨 요구 | 특징 | 핵심 보상 |
|----------|-----------|------|-----------|
| **고대의 힘** | 60+ | 5명의 신으로부터 축복 획득<br>20분간 끝없는 전투 | 골드 100,000<br>드래곤 알 |
| **차원 여행자** | 65+ | 6개 차원 여행<br>40분간 차원 융합 생존 | 골드 150,000<br>차원 포탈 프레임 12개 |
| **숨겨진 직업** | 35+ | 그림자 길드 비밀 시련<br>특별 직업 선택 가능 | 그림자의 검<br>은신 갑옷 |
| **전설의 무기** | 50+ | 4가지 신성한 재료 수집<br>최강 무기 제작 | 전설의 네더라이트 검<br>골드 50,000 |
| **신화의 야수** | 55+ | 4대 신수와 계약<br>최종 4신수 동시 전투 | 신수의 날개<br>골드 80,000 |
| **세계수** | 50+ | 이그드라실 부활<br>30분간 각성 의식 | 세계수의 열매 5개<br>골드 100,000 |

### 퀘스트 목표 타입

퀘스트 시스템은 14가지 다양한 목표 타입을 지원합니다:

- **전투**: 몬스터/보스/플레이어 처치
- **수집**: 아이템 수집, 블록 채굴, 농작물 수확, 낚시
- **이동**: 지역 방문, 특정 좌표 도달
- **상호작용**: NPC 대화, 아이템 전달
- **생존**: 시간/지역 생존, 웨이브 방어
- **제작**: 아이템 제작, 블록 설치
- **경제**: 재화 지불
- **성장**: 레벨 달성

---

## 🏝️ 섬 시스템

### 섬 생성 프로세스

1. **섬 생성 시작**
   ```
   /섬
   ```
   - 섬이 없을 경우 생성 GUI가 열립니다

2. **섬 커스터마이징**
   - **이름**: 최대 20자, 한글/영문/숫자
   - **색상**: 8가지 기본 색상 또는 HEX 코드 직접 입력
   - **바이옴**: 12가지 중 선택 (평원, 숲, 사막, 설원, 정글, 늪, 사바나, 버섯 들판 등)
   - **템플릿**: 
     - BASIC: 기본 평지 섬
     - SKYBLOCK: 하늘섬 스타일
     - LARGE: 대형 섬
     - WATER: 수상 기지

3. **섬 크기와 업그레이드**
   
   | 레벨 | 크기 | 멤버 제한 | 알바 제한 | 필요 기여도 |
   |------|------|-----------|-----------|-------------|
   | 0 | 85x85 | 5명 | 2명 | - |
   | 1 | 100x100 | 10명 | 5명 | 1,000 |
   | 2 | 125x125 | 15명 | 10명 | 5,000 |
   | 3 | 150x150 | 20명 | 15명 | 15,000 |
   | 4 | 185x185 | 25명 | 20명 | 30,000 |
   | 5 | 225x225 | 30명 | 25명 | 50,000 |
   | 6 | 265x265 | 35명 | 30명 | 75,000 |
   | 7 | 315x315 | 40명 | 35명 | 100,000 |
   | 8 | 365x365 | 45명 | 40명 | 150,000 |
   | 9 | 425x425 | 50명 | 45명 | 200,000 |
   | 10 | 500x500 | 60명 | 50명 | 300,000 |

### 섬 역할과 권한

#### 역할 계층
1. **섬장** (OWNER) - 모든 권한
2. **부섬장** (CO_OWNER) - 권한 관리 제외 대부분 권한
3. **멤버** (MEMBER) - 기본 활동 권한
4. **알바생** (WORKER) - 제한된 건축 권한
5. **방문자** (VISITOR) - 구경만 가능

#### 권한 종류
- **BUILD**: 블록 설치/파괴
- **USE_ITEMS**: 문, 버튼, 레버 사용
- **OPEN_CONTAINERS**: 상자, 화로 등 열기
- **INVITE_MEMBERS**: 새 멤버 초대
- **KICK_MEMBERS**: 멤버 추방
- **MANAGE_WORKERS**: 알바생 관리
- **MODIFY_SPAWNS**: 스폰 포인트 설정
- **CHANGE_SETTINGS**: 섬 설정 변경

### 섬 관리 GUI 기능

- **멤버 관리**: 초대, 추방, 역할 변경
- **권한 설정**: 역할별 세부 권한 조정
- **업그레이드**: 섬 크기, 멤버 제한 확장
- **기여도**: 멤버별 기여도 순위 확인
- **스폰 설정**: 기본 스폰, 개인 스폰 설정
- **섬 설정**: 이름, 색상, 바이옴 변경
- **방문자 기록**: 방문 통계 확인

---

## 💰 경제 시스템

### 화폐 종류

| 화폐 | 아이콘 | 용도 | 최대 보유량 | 획득 방법 |
|------|--------|------|-------------|-----------|
| **골드** | 🪙 | 기본 거래, 일반 아이템 | 10억 | 퀘스트, 몬스터 사냥, 거래 |
| **다이아몬드** | 💎 | 고급 아이템, 업그레이드 | 10만 | 일일 퀘스트, 채광, 보스 |
| **에메랄드** | 💚 | 특수 거래, 희귀 아이템 | 10만 | 주민 거래, 특별 퀘스트 |
| **가스트 눈물** | ✨ | 마법 아이템, 인챈트 | 1만 | 네더 탐험, 이벤트 |
| **네더의 별** | ⭐ | 전설 아이템, 최종 업그레이드 | 1천 | 보스 레이드, 전설 퀘스트 |
| **경험치** | 🎯 | 레벨업, 인챈트 | 1천만 | 모든 활동 |

### 환율 시스템 (골드 기준)
- 1 다이아몬드 = 100 골드
- 1 에메랄드 = 50 골드  
- 1 가스트 눈물 = 1,000 골드
- 1 네더의 별 = 10,000 골드

### 경제 활동 가이드

#### 초보자 (레벨 1-20)
- 일일 채광/사냥 퀘스트 완료
- 기본 자원 수집 및 판매
- 일일 수입: 약 5,000 골드

#### 중급자 (레벨 20-40)
- 일일 퀘스트 전체 완료
- 보스 사냥 참여
- 섬 농장 운영
- 일일 수입: 약 15,000 골드

#### 고급자 (레벨 40+)
- 주간 레이드 참여
- 희귀 아이템 거래
- 특별 퀘스트 완료
- 일일 수입: 30,000+ 골드

---

## 📊 스탯과 재능

### 기본 스탯 시스템

| 스탯 | 약어 | 효과 | 기본값 | 최대값 |
|------|------|------|--------|--------|
| **근력** | STR | 물리 공격력 증가 | 10 | 999 |
| **지능** | INT | 마법 공격력, 마나 증가 | 10 | 999 |
| **민첩** | DEX | 공격 속도, 회피율 증가 | 10 | 999 |
| **활력** | VIT | 체력, 방어력 증가 | 10 | 999 |
| **지혜** | WIS | 마나 재생, 스킬 쿨타임 감소 | 10 | 999 |
| **행운** | LUK | 크리티컬 확률, 아이템 드롭률 | 1 | 100 |

#### 스탯 포인트 획득
- 레벨업마다 5 포인트
- 특정 퀘스트 보상
- 이벤트 보상

### 재능(Talent) 시스템

재능은 직업별로 고유한 스킬 트리를 제공합니다.

#### 재능 포인트
- 10레벨마다 1포인트 획득
- 특별 퀘스트 보상

#### 재능 카테고리
- **OFFENSE**: 공격력 증가
- **DEFENSE**: 방어력 증가
- **UTILITY**: 유틸리티 스킬
- **SPECIAL**: 직업 특수 능력

#### 직업별 주요 재능 예시

**광전사 (Berserker)**
- 피의 광란: 체력 50% 이하일 때 공격력 +30%
- 흡혈: 공격 시 피해의 15% 회복
- 광폭화: 3분간 모든 능력치 2배 (쿨타임 10분)

**프리스트 (Priest)**
- 신성 치유: 범위 내 아군 체력 회복
- 축복: 아군에게 버프 부여
- 부활: 죽은 플레이어 부활 (하루 1회)

**궁수 (Archer)**
- 정밀 사격: 크리티컬 확률 +20%
- 은신: 5초간 투명화
- 다중 발사: 한 번에 3발 발사

---

## 👥 소셜 기능

### 친구 시스템

#### 친구 추가
```
/친구추가 <플레이어명> [메시지]
```
- 선택적으로 메시지 첨부 가능
- 상대방이 수락해야 친구 등록

#### 친구 관리
- 최대 100명까지 친구 등록 가능
- 온라인/오프라인 상태 실시간 확인
- 친구가 된 날짜 표시

### 메일 시스템 (미구현)

#### 메일 발송
```
/우편보내기 <플레이어명> <제목> [내용]
```
- 제목: 최대 50자
- 내용: 최대 500자
- 손에 든 아이템 자동 첨부

#### 메일함
- 최대 50개 보관
- 30일 후 자동 삭제
- 읽음/안읽음 구분

### 귓속말 시스템

```
/귓속말 <플레이어명> <메시지>
/w <플레이어명> <메시지>
/r <메시지>  # 마지막 귓속말 상대에게 답장
```

#### 귓속말 모드 설정
- **전체**: 모든 플레이어의 귓속말 수신
- **친구만**: 친구의 귓속말만 수신
- **차단**: 모든 귓속말 차단

---

## 🎮 명령어 가이드

### 기본 명령어

| 명령어 | 별칭 | 설명 | 권한 |
|--------|------|------|------|
| `/메뉴` | `/menu`, `/mm` | 메인 메뉴 열기 | 기본 |
| `/섬` | `/island`, `/is` | 섬 메뉴 열기 | 기본 |
| `/친구` | `/friend`, `/f` | 친구 관리 | 기본 |
| `/메일` | `/mail`, `/m` | 메일함 열기 | 기본 |
| `/귓속말` | `/w`, `/tell`, `/msg` | 귓속말 보내기 | 기본 |
| `/사이트계정발급` | `/siteaccount` | 웹사이트 계정 생성 | 기본 |

### 관리자 명령어

모든 관리자 명령어는 `/rpgadmin` 또는 `/rpga`로 시작합니다.

#### 서버 관리
- `stats` - 서버 통계 확인
- `reload` - 설정 파일 리로드
- `debug` - 디버그 모드 토글

#### 플레이어 관리
- `viewprofile <플레이어>` - 프로필 확인
- `exp give <플레이어> <경험치>` - 경험치 지급
- `level set <플레이어> <레벨>` - 레벨 설정
- `stat set <플레이어> <스탯> <값>` - 스탯 설정

#### NPC 관리 (Citizens 연동)
- `npc set <퀘스트ID>` - 선택한 NPC에 퀘스트 설정
- `npc setcode <npcID> [이름]` - NPC 코드 설정 아이템 지급
- `npc list` - 모든 NPC 목록 및 정보

#### 퀘스트 관리
- `quest give <플레이어> <퀘스트ID>` - 퀘스트 강제 시작
- `quest list` - 모든 퀘스트 목록 (구현 상태 포함)
- `quest reload` - 퀘스트 데이터 리로드

#### 섬 관리
- `island info <플레이어>` - 섬 정보 확인
- `island delete <플레이어>` - 섬 강제 삭제
- `island reset <플레이어>` - 섬 초기화
- `island tp <플레이어>` - 플레이어 섬으로 이동

### 단축키

- **Shift + F**: 메인 메뉴 열기
- **F키**: 마지막 귓속말 상대에게 답장

---

## ⚙️ 설정

### GUI 설정
- **사운드 볼륨**: 0-100% (5%씩 조절)
- **사운드 음소거**: GUI 사운드 끄기/켜기

### 인게임 설정
- **퀘스트 대화 속도**: 빠름(2틱), 보통(5틱), 느림(8틱)
- **퀘스트 자동 길안내**: 다음 목표 위치 표시
- **공격 데미지 표시**: 데미지 숫자 표시 여부
- **확인 대화상자**: 스탯/특성 사용 시 확인

### 소셜 설정
- **친구 요청 받기**: 켜기/끄기
- **길드 초대 받기**: 켜기/끄기 (미구현)
- **귓속말 모드**: 전체/친구만/차단

### 알림 설정
- **귓속말 알림**: 귓속말 수신 시 알림
- **초대 알림 모드**: 전체/친구만/길드만/끄기
- **서버 공지 알림**: 서버 공지사항 표시

---

## 🏗️ 프로젝트 구조

### 기술 스택
- **Java 24**: 최신 Java 기능 활용
- **Paper 1.21.7+**: 고성능 서버 API
- **Firebase Firestore**: 클라우드 데이터베이스
- **Citizens**: NPC 시스템
- **WorldGuard**: 지역 보호 (선택)
- **Adventure API**: 현대적 텍스트 처리

### 주요 디렉토리 구조

```
src/main/java/com/febrie/rpg/
├── 📁 command/          # 명령어 처리 시스템
│   ├── BaseCommand      # 기본 명령어 클래스
│   ├── admin/           # 관리자 명령어
│   ├── island/          # 섬 관련 명령어
│   ├── social/          # 소셜 명령어
│   └── system/          # 시스템 명령어
│
├── 📁 database/         # 데이터베이스 레이어
│   ├── FirestoreManager # Firebase 초기화
│   └── service/         # 서비스 구현체
│       ├── BaseFirestoreService
│       └── impl/        # 각 기능별 서비스
│
├── 📁 dto/              # Data Transfer Objects
│   ├── island/          # 섬 관련 DTO (12개)
│   ├── player/          # 플레이어 DTO (7개)
│   ├── quest/           # 퀘스트 DTO (4개)
│   ├── social/          # 소셜 DTO (4개)
│   └── system/          # 시스템 DTO (2개)
│
├── 📁 economy/          # 경제 시스템
│   ├── CurrencyType     # 화폐 타입 정의
│   └── Wallet           # 지갑 관리
│
├── 📁 gui/              # GUI 시스템
│   ├── framework/       # GUI 프레임워크
│   ├── component/       # GUI 컴포넌트
│   ├── impl/            # GUI 구현체 (30+개)
│   └── manager/         # GUI 관리자
│
├── 📁 island/           # 섬 시스템
│   ├── Island           # 섬 엔티티
│   ├── IslandCache      # 캐시 시스템
│   ├── manager/         # 섬 관리
│   ├── permission/      # 권한 시스템
│   └── world/           # 월드 관리
│
├── 📁 job/              # 직업 시스템
│   └── JobType          # 9개 직업 정의
│
├── 📁 level/            # 레벨 시스템
│   └── LevelSystem      # 경험치 계산
│
├── 📁 listener/         # 이벤트 리스너
│   ├── DamageDisplayListener
│   ├── MenuShortcutListener
│   ├── NPCInteractListener
│   └── QuestEventListener
│
├── 📁 npc/              # NPC 시스템
│   ├── manager/         # NPC 관리
│   └── trait/           # Citizens Traits
│
├── 📁 player/           # 플레이어 시스템
│   ├── PlayerSettings   # 개인 설정
│   ├── RPGPlayer        # RPG 데이터
│   └── RPGPlayerManager # 플레이어 관리
│
├── 📁 quest/            # 퀘스트 시스템 (최대 모듈)
│   ├── Quest            # 퀘스트 기본 클래스
│   ├── QuestID          # 260개 퀘스트 ID
│   ├── builder/         # 퀘스트 빌더
│   ├── dialog/          # 대화 시스템
│   ├── impl/            # 퀘스트 구현체
│   │   ├── tutorial/    # 튜토리얼 (2개)
│   │   ├── main/        # 메인 스토리
│   │   ├── daily/       # 일일 (8개)
│   │   ├── weekly/      # 주간 (2개)
│   │   └── special/     # 특별 (6개)
│   ├── objective/       # 목표 시스템 (14종)
│   ├── progress/        # 진행도 추적
│   └── reward/          # 보상 시스템
│
├── 📁 social/           # 소셜 기능
│   ├── FriendManager    # 친구 관리
│   ├── MailManager      # 메일 시스템
│   └── WhisperManager   # 귓속말 관리
│
├── 📁 stat/             # 스탯 시스템
│   └── Stat             # 6개 기본 스탯
│
├── 📁 system/           # 시스템 관리
│   └── ServerStatsManager # 서버 통계
│
├── 📁 talent/           # 재능 시스템
│   ├── Talent           # 재능 정의
│   └── TalentManager    # 재능 관리
│
├── 📁 util/             # 유틸리티
│   ├── ColorUtil        # 색상 처리
│   ├── ItemBuilder      # 아이템 생성
│   ├── LangManager      # 다국어 지원
│   ├── SoundUtil        # 사운드 효과
│   ├── TextUtil         # 텍스트 처리
│   ├── TimeUtil         # 시간 관리
│   ├── display/         # 데미지 표시
│   └── pathfinding/     # 길찾기 알고리즘
│
└── RPGMain.java         # 메인 플러그인 클래스
```

### 리소스 구조

```
src/main/resources/
├── plugin.yml           # 플러그인 설정
└── lang/                # 언어 파일
    ├── ko_KR/           # 한국어
    │   ├── command.json
    │   ├── gui.json
    │   ├── job.json
    │   ├── quest.json
    │   └── ...
    └── en_US/           # 영어
        └── (동일 구조)
```

---

## 📦 패키지별 상세 설명

### Command 패키지
- **BaseCommand**: 모든 명령어의 기본 클래스, 에러 처리 및 권한 체크
- **AdminCommands**: 46개의 관리자 하위 명령어 구현
- **IslandCommand**: 섬 생성/관리 진입점
- **FriendCommand**: 친구 추가/삭제/목록 관리
- **MailCommand**: 우편 발송/수신 (미구현)
- **WhisperCommand**: 귓속말 및 답장 시스템
- **MainMenuCommand**: 메인 GUI 열기
- **SiteAccountCommand**: Firebase Auth 계정 생성

### Database 패키지
- **FirestoreManager**: Firebase 초기화 및 인스턴스 관리
- **BaseFirestoreService**: 모든 서비스의 기본 클래스, 캐싱 구현
- **서비스 구현체**:
  - IslandFirestoreService: 섬 데이터 관리
  - PlayerFirestoreService: 플레이어 프로필 관리
  - QuestFirestoreService: 퀘스트 진행도 저장
  - SocialFirestoreService: 친구/메일 데이터
  - SystemFirestoreService: 서버 통계/리더보드

### GUI 패키지
- **두 가지 GUI 시스템 공존**:
  - Legacy 시스템: 섬 관련 GUI
  - Framework 시스템: 나머지 모든 GUI
- **GuiManager**: 중앙 집중식 GUI 관리
- **GuiFactory**: 표준 컴포넌트 생성
- **30개 이상의 GUI 구현체**

### Quest 패키지
- **Quest 추상 클래스**: 모든 퀘스트의 기본
- **260개의 QuestID**: enum으로 타입 안전성 보장
- **14가지 목표 타입**: 다양한 게임플레이 지원
- **QuestManager**: 싱글톤 패턴으로 중앙 관리
- **빌더 패턴**: 복잡한 퀘스트 생성 단순화

### Island 패키지
- **Island**: 섬 엔티티, 비즈니스 로직
- **IslandCache**: 성능을 위한 메모리 캐싱
- **IslandWorldManager**: FAWE를 이용한 물리적 섬 생성
- **권한 시스템**: 5개 역할, 8개 권한 타입
- **업그레이드 시스템**: 10단계 크기 확장

### Player 패키지
- **RPGPlayer**: 플레이어의 모든 RPG 데이터
- **PlayerSettings**: 개인 설정 관리
- **RPGPlayerManager**: 중앙 집중식 플레이어 관리
- **자동 저장**: 10분마다 자동 저장, 30초 쿨다운

### Util 패키지
- **ColorUtil**: HEX, RGB, Legacy 색상 처리
- **ItemBuilder**: Fluent API 스타일 아이템 생성
- **LangManager**: 메모리 기반 다국어 시스템
- **TextDisplayDamageManager**: 1.19.4+ Display 엔티티 활용
- **PathfindingUtil**: A* 알고리즘 구현

---

## 🔧 시스템 아키텍처

### 데이터 흐름

```
플레이어 액션
    ↓
Command/Listener
    ↓
Manager Layer
    ↓
Service Layer (캐싱)
    ↓
Firebase Firestore
```

### 캐싱 전략
- **Caffeine 캐시**: 5분 TTL, 최대 1000개 항목
- **메모리 캐시**: 자주 접근하는 데이터
- **레이지 로딩**: 필요시에만 데이터 로드

### 비동기 처리
- **CompletableFuture**: 모든 DB 작업
- **Bukkit Scheduler**: 게임 로직 동기화
- **Thread-safe**: ConcurrentHashMap 사용

### 성능 최적화
- **청크 단위 섬 생성**: FAWE 활용
- **배치 처리**: 대량 데이터 한 번에 처리
- **이벤트 최적화**: 필요한 이벤트만 리스닝

---

## 🌐 Firebase 설정

### 환경 변수 설정

1. **Service Account 준비**
   ```bash
   # Service Account JSON을 Base64로 인코딩
   base64 -w 0 firebase-service-account.json > encoded.txt
   ```

2. **환경 변수 설정**
   ```bash
   export FIREBASE_SERVICE_ACCOUNT_BASE64="<encoded.txt 내용>"
   export FIREBASE_WEB_API_KEY="<Firebase Web API Key>"
   ```

### Firestore 컬렉션 구조

```
firestore/
├── players/             # 플레이어 데이터
│   └── {uuid}/
│       ├── profile
│       ├── wallet
│       └── settings
│
├── islands/             # 섬 데이터
│   └── {islandId}/
│       ├── info
│       ├── members
│       └── permissions
│
├── player-islands/      # 플레이어-섬 관계
│   └── {uuid}/
│       └── currentIsland
│
├── player-quests/       # 퀘스트 진행도
│   └── {uuid}/
│       ├── active
│       └── completed
│
├── friendships/         # 친구 관계
│   └── {friendship-id}
│
├── mail/                # 우편
│   └── {mail-id}
│
├── server-stats/        # 서버 통계
│   └── current
│
└── leaderboards/        # 리더보드
    ├── level
    ├── wealth
    └── playtime
```

### 보안 규칙 예시

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // 플레이어는 자신의 데이터만 읽기/쓰기
    match /players/{playerId} {
      allow read, write: if request.auth != null && 
        request.auth.uid == playerId;
    }
    
    // 섬 데이터는 멤버만 접근
    match /islands/{islandId} {
      allow read: if request.auth != null &&
        exists(/databases/$(database)/documents/player-islands/$(request.auth.uid)) &&
        get(/databases/$(database)/documents/player-islands/$(request.auth.uid)).data.currentIsland == islandId;
    }
  }
}
```

---

## 🔌 API & 확장

### 커스텀 퀘스트 추가

```java
public class MyCustomQuest extends Quest {
    public MyCustomQuest() {
        super(QuestID.MY_CUSTOM_QUEST);
        
        // 목표 추가
        addObjective(new KillMobObjective("zombie", 10));
        addObjective(new CollectItemObjective(Material.DIAMOND, 5));
        
        // 보상 설정
        setReward(new Reward() {
            @Override
            public void grant(Player player) {
                // 보상 지급 로직
            }
        });
        
        // 요구사항 설정
        setMinLevel(20);
        addPrerequisite(QuestID.SOME_OTHER_QUEST);
    }
    
    @Override
    public Component getDisplayName(boolean isKorean) {
        return Component.text(isKorean ? "내 커스텀 퀘스트" : "My Custom Quest");
    }
}
```

### 커스텀 GUI 생성

```java
public class MyCustomGui extends BaseGui {
    
    public static MyCustomGui create(GuiManager manager, LangManager lang, Player player) {
        MyCustomGui gui = new MyCustomGui(manager, lang, player);
        gui.initialize();
        return gui;
    }
    
    @Override
    protected void setupLayout() {
        // 테두리 생성
        createBorder();
        
        // 아이템 배치
        setItem(22, GuiItem.clickable(
            ItemBuilder.of(Material.DIAMOND)
                .displayName(trans("my.custom.item"))
                .build(),
            player -> {
                playSuccessSound(player);
                player.sendMessage(trans("my.custom.message"));
            }
        ));
    }
    
    @Override
    public GuiFramework getBackTarget() {
        return MainMenuGui.create(guiManager, langManager, viewer);
    }
}
```

### 커스텀 목표 타입

```java
public class CustomObjective extends BaseObjective {
    private final String customData;
    private final int targetCount;
    
    public CustomObjective(String customData, int targetCount) {
        super("CUSTOM_" + customData);
        this.customData = customData;
        this.targetCount = targetCount;
    }
    
    @Override
    public boolean canProgress(Event event, Player player) {
        // 이벤트가 진행 가능한지 확인
        return event instanceof MyCustomEvent;
    }
    
    @Override
    public int calculateIncrement(Event event, Player player) {
        // 진행도 증가량 계산
        return 1;
    }
    
    @Override
    public boolean isComplete(ObjectiveProgress progress) {
        return progress.getProgress() >= targetCount;
    }
}
```

### 이벤트 후킹

```java
@EventHandler
public void onQuestComplete(QuestCompleteEvent event) {
    Player player = event.getPlayer();
    Quest quest = event.getQuest();
    
    // 커스텀 로직
    if (quest.getId() == QuestID.SPECIAL_QUEST) {
        // 특별 보상 지급
    }
}
```

---

## 🐛 알려진 이슈 및 개선 사항

### 현재 이슈
1. **소셜 시스템 미구현**: 친구/메일/귓속말 기능이 부분적으로만 작동
2. **Firebase 연동 비활성화**: 현재 메모리 캐시만 사용
3. **퀘스트 구현 미완성**: 260개 중 약 30개만 구현
4. **섬 색상/바이옴 미적용**: GUI에서 선택은 가능하나 실제 적용 안됨

### 개선 필요 사항
1. **하드코딩된 관리자 UUID**: 설정 파일로 이동 필요
2. **BaseFirestoreService 타입 안전성**: 제네릭 개선 필요
3. **TalentManager 리팩토링**: 633줄의 거대한 클래스 분할 필요

### 향후 계획
- [ ] 던전 시스템 구현
- [ ] PvP 아레나 추가
- [ ] 길드 시스템 구현
- [ ] 경매장 시스템
- [ ] 펫 시스템
- [ ] 나머지 230개 퀘스트 구현

---

## 📄 라이선스

이 프로젝트는 비공개 라이선스입니다.

---

## 🤝 기여 가이드

### 코드 스타일
- Google Java Style Guide 준수
- 의미 있는 변수명 사용
- 한글 주석 권장
- Component API 사용 (레거시 코드 지양)

### 커밋 메시지
```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅
refactor: 코드 리팩토링
test: 테스트 추가
chore: 빌드 업무 수정
```

### Pull Request
1. feature 브랜치 생성
2. 변경사항 커밋
3. PR 생성 및 리뷰 요청
4. 승인 후 머지

---

<div align="center">

### 🎮 즐거운 게임 되세요! 🎮

**Developed by Febrie & CoffeeTory**

[맨 위로 가기](#-sypixel-rpg---완전-가이드)

</div>