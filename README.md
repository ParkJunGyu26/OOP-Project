# KAU OOP Project dd

안드로이드 MVVM 패턴을 활용한 서버리스 채팅 애플리케이션

## 프로젝트 아키텍처

### MVVM 아키텍처 패턴
이 프로젝트는 MVVM(Model-View-ViewModel) 아키텍처 패턴을 사용합니다:
- **View**: 사용자 인터페이스를 담당하며, 사용자의 입력을 받아 ViewModel에 전달
- **ViewModel**: View와 Model 사이의 중재자 역할, UI 관련 비즈니스 로직 처리
- **Model**: 애플리케이션의 데이터와 비즈니스 로직을 포함
- **Repository**: Firebase와의 데이터 통신을 담당하는 계층


### 프로젝트 구조
```
app/src/main/java/com.kau.oop/
├── data/                      # 데이터 계층
│   ├── model/                 # 데이터 모델 클래스
│   │   ├── user/             # 사용자 관련 모델
│   │   ├── post/             # 게시글 관련 모델
│   │   └── chat/             # 채팅 관련 모델
│   └── repository/           # Firebase 데이터 처리
│       ├── user/             # 사용자 데이터 처리
│       ├── post/             # 게시글 데이터 처리
│       └── chat/             # 채팅 데이터 처리
└── ui/                        # UI 계층
│   ├── user/                  # 사용자 관련 UI
│   │   ├── view/             # 로그인/회원가입/프로필 화면
│   │   └── viewmodel/        # 사용자 관련 뷰모델
│   ├── post/                  # 게시글 관련 UI
│   │   ├── view/             # 게시글 목록/상세/작성 화면
│   │   └── viewmodel/        # 게시글 관련 뷰모델
│   └── chat/                  # 채팅 관련 UI
│       ├── view/             # 채팅 목록/대화방 화면
│       └── viewmodel/        # 채팅 관련 뷰모델
```

### 도메인 구조
프로젝트는 3개의 주요 도메인으로 구성됩니다:

1. **User Domain**
   - 회원가입/로그인 기능
   - 마이페이지(활동 목록)
   - 알림 목록

2. **Post Domain**
   - 게시글 작성/조회/수정/삭제
   - 댓글 기능
   - 게시글 검색
   - 스크랩

3. **Chat Domain**
   - 일대일 채팅
   - 다대다 채팅(오픈채팅)
   - 사진 및 파일 첨부
   - 푸시 알림

## Git Flow
이 프로젝트는 Git Flow 전략을 따릅니다:

- `main`: 제품 출시 버전
- `feature/*`: 기능 개발 브랜치
  - 각 개발자는 feature 브랜치에서 작업
  - 코드 리뷰 후 main 브랜치로 병합(ex)
    - feature/user
    - feature/post
    - feature/chat

### 개발 프로세스
1. feature 브랜치 생성
2. 기능 개발
3. Pull Request 생성
4. 코드 리뷰
5. main 브랜치로 병합

### 브랜치 명명 규칙
- 기능 개발: `feature/[domain]/[feature-name]`
  - 예: `feature/user/login`
  - 예: `feature/post/write`
  - 예: `feature/chat/message`

### 레이아웃 명명 규칙
- 파일명: activity_{domain}_{feature_name}

## Team
- User Domain: [박준규]
- Post Domain: [유완규]
- Chat Domain: [최수빈]
