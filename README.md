# 🎮 Sypixel RPG - 마인크래프트 RPG 서버 플러그인

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.1+-brightgreen)
![Plugin Version](https://img.shields.io/badge/Version-1.0.0-blue)
![Java](https://img.shields.io/badge/Java-17+-orange)
![License](https://img.shields.io/badge/License-Private-red)

> 포괄적인 RPG 경험을 제공하는 마인크래프트 서버 플러그인

## 🌟 주요 기능

### 🗡️ 직업 시스템
14개의 고유한 직업 중 하나를 선택하여 모험을 시작하세요!

**근접 계열**
- **전사** - 균형 잡힌 근접 전투 전문가
- **성기사** - 신성한 힘을 다루는 탱커
- **버서커** - 분노로 싸우는 광전사
- **도적** - 빠르고 치명적인 암살자
- **암살자** - 그림자 속의 처형자
- **수호자** - 철벽같은 방어 전문가

**마법 계열**
- **마법사** - 다양한 마법을 구사하는 마스터
- **원소술사** - 자연의 힘을 다루는 술사
- **흑마법사** - 어둠의 힘을 사용하는 마법사

**지원 계열**
- **성직자** - 아군을 치유하는 힐러
- **메르시** - 자연의 힘으로 보호하는 드루이드

**원거리 계열**
- **궁수** - 정확한 사격의 달인
- **스나이퍼** - 극한의 사거리를 자랑하는 저격수
- **샷거너** - 근거리 화력의 전문가

### 💪 게임 시스템

#### 스탯 시스템
- 근력, 민첩, 지혜, 체력, 행운 5가지 기본 스탯
- 레벨업 시 스탯 포인트 획득
- 전략적인 스탯 분배로 캐릭터 커스터마이징

#### 특성 시스템
- 직업별 고유 특성 트리
- 특성 포인트로 새로운 능력 습득
- 다양한 빌드 구성 가능

#### 퀘스트 시스템
- 메인 스토리 퀘스트
- 일일/주간 퀘스트
- 반복 가능한 퀘스트
- 대화형 퀘스트 진행

### 🎯 GUI 시스템

#### 메인 메뉴
- 프로필, 허브, 상점, 던전, 야생, 섬, 리더보드 접근
- 직관적인 아이콘 기반 인터페이스

#### 프로필 시스템
- 플레이어 정보 확인
- 직업 정보 및 레벨
- 게임 통계 (플레이 시간, 몬스터 처치 수 등)
- 퀘스트 진행 상황

#### 설정 메뉴
- GUI 사운드 볼륨 조절
- 개인 설정 관리
- 알림 설정
- 언어 설정 (한국어/영어)

### 💬 소셜 기능

#### 귓말 시스템
- `/귓말 <플레이어> <메시지>` - 귓말 전송
- `/r <메시지>` - 마지막 귓말에 답장
- 귓말 기록 저장 (Firebase)
- 개인정보 설정 (모두/친구만/차단)

#### 친구 시스템
- 친구 추가/삭제
- 온라인 상태 확인
- 친구 목록 관리

#### 메일 시스템
- 오프라인 플레이어에게 메시지 전송
- 아이템 첨부 가능 (준비중)

### 🌐 웹사이트 연동
- Firebase를 통한 데이터 동기화
- `/사이트계정발급 <이메일>` - 웹사이트 계정 생성
- 자동 비밀번호 생성 및 클릭 복사 기능
- 관리자 권한 동기화

### 🏆 리더보드
- 레벨 순위
- 직업별 순위
- 재산 순위
- 전투력 순위
- 플레이시간 순위

## 🛠️ 기술 스택

- **Java 17+** - 모던 자바 기능 활용
- **Paper/Spigot 1.20.1+** - 최신 마인크래프트 API
- **Firebase** - 실시간 데이터베이스 및 인증
- **Adventure API** - 텍스트 컴포넌트 시스템
- **Maven** - 의존성 관리

## 📁 프로젝트 구조

```
src/main/java/com/febrie/rpg/
├── command/              # 명령어 처리
│   ├── admin/           # 관리자 명령어
│   ├── debug/           # 디버그 명령어  
│   ├── economy/         # 경제 명령어
│   ├── player/          # 플레이어 명령어
│   ├── social/          # 소셜 명령어
│   └── SiteAccountCommand.java  # 사이트 계정 발급
├── database/            # 데이터베이스
│   ├── DatabaseManager.java
│   └── FirestoreRestService.java  # Firebase REST API
├── gui/                 # GUI 시스템
│   ├── framework/       # GUI 프레임워크
│   ├── impl/           # GUI 구현체
│   │   ├── player/     # 프로필, 스탯, 특성 GUI
│   │   ├── settings/   # 설정 GUI
│   │   └── system/     # 메인메뉴, 리더보드 GUI
│   └── manager/        # GUI 관리
├── job/                # 직업 시스템
├── listener/           # 이벤트 리스너
│   └── DamageDisplayListener.java
├── player/             # 플레이어 데이터
├── quest/              # 퀘스트 시스템
├── social/             # 소셜 기능
│   ├── FriendManager.java
│   ├── MailManager.java
│   └── WhisperManager.java
├── stat/               # 스탯 시스템
├── talent/             # 특성 시스템
├── util/               # 유틸리티
│   ├── display/        # 데미지 표시
│   │   └── DamageDisplayManager.java
│   ├── LangManager.java    # 다국어 지원
│   └── SoundUtil.java      # 사운드 유틸
└── RPGMain.java        # 메인 플러그인 클래스
```

## 🚀 최근 업데이트

### 코드 품질 개선
- ✅ Deprecated API 완전 제거
- ✅ 사용하지 않는 import 및 메서드 정리
- ✅ 싱글톤 패턴 스레드 안전성 개선

### GUI 시스템 개선
- ✅ 메인 메뉴 레이아웃 재구성
  - 프로필 버튼을 상단(슬롯 4)으로 이동
  - 스탯/특성 버튼 제거 (프로필에서 접근)
  - 허브, 야생, 섬 버튼 추가
- ✅ 프로필 GUI에서 직업 아이템 클릭 시 특성 페이지로 이동
- ✅ 버튼 설명에 "클릭하여 [페이지]로 이동" 추가
- ✅ GUI 사운드 설정이 실제로 적용되도록 수정

### 번역 시스템 개선
- ✅ GUI 설정 관련 누락된 번역 키 추가
- ✅ 직업명 수정 (머시 → 메르시)
- ✅ 하드코딩된 한국어 메시지를 번역 시스템으로 변경

### 성능 최적화
- ✅ DamageDisplayManager 메모리 누수 방지
- ✅ GUI 사운드 볼륨 계산 최적화

## 📦 설치 방법

1. **요구사항 확인**
   - Java 17 이상
   - Paper/Spigot 1.20.1 이상
   - Firebase 프로젝트 설정

2. **플러그인 빌드**
   ```bash
   mvn clean package
   ```

3. **서버 설치**
   - 생성된 JAR 파일을 서버의 `plugins` 폴더에 복사
   - 서버 시작

4. **환경 변수 설정**
   ```bash
   FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n"
   FIREBASE_WEB_API_KEY="your-web-api-key"
   ```

## ⚙️ 설정

### 권한 노드
- `sypixelrpg.admin` - 관리자 명령어 사용
- `sypixelrpg.siteaccount` - 사이트 계정 발급 (기본값: true)
- `sypixelrpg.debug` - 디버그 명령어 사용

### 주요 명령어
- `/메뉴` - 메인 메뉴 열기
- `/프로필 [플레이어]` - 프로필 확인
- `/귓말 <플레이어> <메시지>` - 귓말 전송
- `/r <메시지>` - 마지막 귓말에 답장
- `/사이트계정발급 <이메일>` - 웹사이트 계정 생성

## 🐛 알려진 이슈

- 귓말 시스템 메시지가 아직 번역 시스템을 사용하지 않음
- 일부 GUI에서 새로고침 버튼이 필요하지 않은데도 표시됨

## 📝 향후 계획

- [ ] 스킬 시스템 구현
- [ ] 던전 시스템 구현
- [ ] 파티 시스템 완성
- [ ] 거래 시스템 추가
- [ ] 펫 시스템 구현
- [ ] 아이템 강화 시스템

## 👥 개발팀

- **Febrie** - 메인 개발자
- **CoffeeTory** - 기여자

## 📞 문의

- 이슈 트래커: GitHub Issues
- 이메일: support@sypixelrpg.com

---

**© 2024 Sypixel RPG. All rights reserved.**