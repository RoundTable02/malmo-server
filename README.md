# 💌 말모 Malmo - AI 연애 상담, 마음 질문

## 🧐 프로젝트 소개

> **"연인은 왜 저런 반응을 할까?", "이럴 땐 어떻게 대응해야 하지?"**

말모(Malmo)는 연인 사이의 갈등과 고민에서 출발한 **애착 유형 기반 AI 연애 갈등 상담 앱**입니다.

MZ세대는 연인과의 갈등 원인으로 '의사소통 방식'과 '성향 차이'를 가장 많이 꼽았으며, 자신과 연인을 이해하려는 니즈가 높습니다.  
말모(Malmo)는 사용자와 연인의 애착 유형 데이터를 기반으로 갈등 상황을 분석하고, 관계 개선을 위한 맞춤형 조언을 제공하는 서비스입니다.

---

## ✨ 주요 기능

### 1. 애착 유형 진단

- ECR 검사 문항 기반 애착 유형 진단
- 커플 연동으로 서로의 결과 공유 및 AI 상담에 활용

### 2. AI 갈등 상담

- 채팅으로 갈등 상황 입력 → AI가 애착 유형 기반 상담 제공
- 상담 종료 후, 요약 리포트 제공

### 3. 커플 질문

- 매일 새로운 커플 질문 제공
- 누적 답변은 AI 상담 분석에 활용 + 커플 레벨 상승 요소 제공

---

## 🖼️ 스크린샷

<div align="center">
   <img width="800" alt="스크린샷" src="https://github.com/user-attachments/assets/e0960f87-1ba4-453c-ab97-2dd3254727de" />
   <img width="800" alt="Frame 1948756838" src="https://github.com/user-attachments/assets/4d61401f-020c-4da2-8260-465331d66fa4" />
</div>

---

## 🛠️ 기술 스택

| Category          | Tools & Technologies |
|-------------------|-----------------------|
| **Frameworks**    | ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=flat&logo=spring&logoColor=white) ![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat&logo=springsecurity&logoColor=white) |
| **Language**      | ![Java](https://img.shields.io/badge/Java%2017-007396?style=flat&logo=openjdk&logoColor=white) |
| **Persistence**   | ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-007396?style=flat&logo=hibernate&logoColor=white) ![QueryDSL](https://img.shields.io/badge/QueryDSL-59666C?style=flat) |
| **Database**      | ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white) |
| **Cloud (AWS)**   | ![EC2](https://img.shields.io/badge/EC2-FF9900?style=flat&logo=amazon-ec2&logoColor=white) ![S3](https://img.shields.io/badge/S3-569A31?style=flat&logo=amazon-s3&logoColor=white) ![RDS](https://img.shields.io/badge/RDS-527FFF?style=flat&logo=amazon-rds&logoColor=white) |
| **Messaging**     | ![redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white) |
| **Container & DevOps** | ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white) |
| **Monitoring**    | ![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=flat&logo=prometheus&logoColor=white) ![Grafana](https://img.shields.io/badge/Grafana-F46800?style=flat&logo=grafana&logoColor=white) |
| **Documentation** | ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=black) |
| **Testing**       | ![JUnit5](https://img.shields.io/badge/JUnit%205-25A162?style=flat&logo=junit5&logoColor=white) ![Mockito](https://img.shields.io/badge/Mockito-007396?style=flat&logo=java&logoColor=white) |
| **CI/CD**         | ![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=flat&logo=githubactions&logoColor=white) |



---

## 🚎 Architecture

<div align="center">
   <img width="700" height="500" alt="malmo_arch drawio" src="https://github.com/user-attachments/assets/1b9b735a-6441-4c18-83b5-fff7647f6345" />
</div>

---

## 📈 DataBase Schema

<div align="center">
   <img width="3581" height="2403" alt="Image" src="https://github.com/user-attachments/assets/6d3b8996-8916-4e46-aa66-87ed04b0d676" />
</div>

*https://dbdiagram.io/d/malmoerd-669f3b458b4bb5230e11b2d0*

---

# 배포 브랜치 전략 및 CI/CD 가이드

## 브랜치 전략 (Git Flow 기반)

본 레포지토리는 Git Flow 전략을 변형하여 다음과 같이 브랜치를 운영합니다.

* **main** : 운영 환경에 배포된 최종 코드
* **release** : 배포 대상이 되는 안정화 브랜치
* **develop** : 기능 개발 통합 브랜치
* **feature/**\* : 단일 기능 개발 브랜치

## 기능 개발 및 병합 규칙

1. 기능 개발은 `feature/*` 브랜치에서 진행합니다.
2. 기능이 완료되면 `develop` 브랜치에 먼저 병합합니다.
3. 배포 대상 기능이 확정되면 `release` 브랜치로 병합합니다.

## 배포를 위한 Commit 규칙

배포 시점에 `release` 브랜치에 커밋할 때는 **Commit 메시지 규칙**을 반드시 따라야 합니다.
해당 메시지는 GitHub Actions CI/CD 과정에서 **Blue/Green 배포 방식**을 결정하는 기준이 됩니다.

* **형식**

  ```
  <type>: <subject>
  ```

* **type**
  
  이전 커밋의 타입 명의 반대 타입으로 배포해주세요.

  * `release-blue` : Blue 환경으로 배포
  * `release-green` : Green 환경으로 배포

* **subject**

  * 배포할 기능/개발 내용을 간략히 작성

예시:

```
release-blue: 로그인 기능 개선 및 에러 처리 추가
release-green: 결제 모듈 업데이트
```

## CI/CD 및 배포 과정

1. `release` 브랜치에 커밋(Push) → GitHub Actions가 실행됩니다.
2. 커밋 메시지의 `type` 값(`release-blue` 또는 `release-green`)에 따라 Blue/Green 중 하나로 배포됩니다.
3. CI/CD 과정이 완료되면 Nginx 설정 파일을 Blue/Green에 맞게 업데이트하여 최종 배포를 마무리합니다.

---

## 개발 내용 (트러블 슈팅)

- 애착 유형과 연애 질문 등 사용자의 메타 데이터를 바탕으로 한 **AI 연애 상담 챗봇 구현**
  
- 단계별 상담 구조 설계 및 프롬프트 엔지니어링
  
- **SSE 통신** 방식을 이용한 GPT 응답 스트리밍
  
  [[VELOG : SSE로 GPT 응답 스트리밍하기]](https://velog.io/@_roundtable/%EB%A7%90%EB%AA%A8-SSE%EB%A1%9C-GPT-%EC%9D%91%EB%8B%B5-%EC%8A%A4%ED%8A%B8%EB%A6%AC%EB%B0%8D%ED%95%98%EA%B8%B0) 
  
- **Redis Stream**을 도입으로 안정적인 외부 API 호출 관리

  [[VELOG : 좀 더 안정적인 API 호출 with Redis Stream]](https://velog.io/@_roundtable/%EB%A7%90%EB%AA%A8-%EC%A2%80-%EB%8D%94-%EC%95%88%EC%A0%95%EC%A0%81%EC%9D%B8-API-%ED%98%B8%EC%B6%9C-with-Redis-Stream)

- **트랜잭션 아웃박스 (Transactional Outbox)** 패턴 적용으로 메시지 발행 보장
  
   [[트랜잭션 아웃박스 패턴으로 메시지 발행 보장하기]](https://velog.io/@_roundtable/%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EC%95%84%EC%9B%83%EB%B0%95%EC%8A%A4-%ED%8C%A8%ED%84%B4%EC%9C%BC%EB%A1%9C-%EB%A9%94%EC%8B%9C%EC%A7%80-%EB%B0%9C%ED%96%89-%EB%B3%B4%EC%9E%A5%ED%95%98%EA%B8%B0)

- 외부 API의 도메인 영역 침범을 분리하기 위해 **헥사고날 아키텍처**를 적용, 확장 가능성 확보

  [[VELOG : 헥사고날의 선택, 그리고 후기]](https://velog.io/@_roundtable/%EB%A7%90%EB%AA%A8-%ED%97%A5%EC%82%AC%EA%B3%A0%EB%82%A0%EC%9D%98-%EC%84%A0%ED%83%9D-%EA%B7%B8%EB%A6%AC%EA%B3%A0-%ED%9B%84%EA%B8%B0)

- 사용자 프라이버시 보호를 위한 AI채팅 기능 DB 암호화 도입

  [[VELOG : DB 데이터 암호화 전략 (feat. AES-NI 성능 테스트)]](https://velog.io/@_roundtable/%EB%A7%90%EB%AA%A8-DB-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%95%94%ED%98%B8%ED%99%94-%EC%A0%84%EB%9E%B5-feat.-AES-NI-%EC%84%B1%EB%8A%A5-%ED%85%8C%EC%8A%A4%ED%8A%B8)
  
- 도메인 독립성을 검증하고 외부 어댑터 의존성을 제거하기 위한 Mockito 단위 테스트 및 기획 검증을 위한 통합 테스트 코드 작성으로 **코드 커버리지 77% 달성**
  
- 사용자와 애인의 연동을 통한 애착 유형 검사, 연애 질문 기능 개발
  
- Prometheus Grafana **모니터링 대시보드** 구축
  
- 단일 인스턴스에서 nginx와 도커 컨테이너를 이용해 **블루-그린 배포**

  [[VELOG : nginx로 블루-그린 배포 흉내내기]](https://velog.io/@_roundtable/%EB%A7%90%EB%AA%A8-nginx%EB%A1%9C-%EB%B8%94%EB%A3%A8-%EA%B7%B8%EB%A6%B0-%EB%B0%B0%ED%8F%AC-%ED%9D%89%EB%82%B4%EB%82%B4%EA%B8%B0)

- 코드 중복을 줄이고 유지보수성을 확보하기 위해 **Spring AOP** 도입

  [[VELOG : 거의 모든 서비스 로직이 사용자를 검증한다 (AOP 적용기)]](https://velog.io/@_roundtable/%EA%B1%B0%EC%9D%98-%EB%AA%A8%EB%93%A0-%EC%84%9C%EB%B9%84%EC%8A%A4-%EB%A1%9C%EC%A7%81%EC%9D%B4-%EC%82%AC%EC%9A%A9%EC%9E%90%EB%A5%BC-%EA%B2%80%EC%A6%9D%ED%95%9C%EB%8B%A4-AOP-%EC%A0%81%EC%9A%A9%EA%B8%B0)
  
- 도메인 순수성 유지를 위해 생성자 접근을 제한

  [[VELOG : 메인 모델 생성자에 대한 고민]](https://velog.io/@_roundtable/%EB%A7%90%EB%AA%A8-%EB%8F%84%EB%A9%94%EC%9D%B8-%EB%AA%A8%EB%8D%B8%EC%9D%98-%EC%83%9D%EC%84%B1%EC%9E%90%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B3%A0%EB%AF%BC)



---
