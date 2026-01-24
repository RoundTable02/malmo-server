# API 변경 사항 (MM-168)

## 삭제된 API

### 1. GET /chatrooms/current
변경 전: 현재 채팅방 상태 조회
변경 후: 삭제됨
- 더 이상 "현재 채팅방" 개념이 없음. 채팅방 목록에서 상태 확인 가능.

### 2. GET /chatrooms/current/messages
변경 전: 현재 채팅방의 메시지 조회
변경 후: 삭제됨
- `GET /chatrooms/{chatRoomId}/messages` 사용

### 3. POST /chatrooms/current/send
변경 전: 현재 채팅방에 메시지 전송
변경 후: 삭제됨
- `POST /chatrooms/{chatRoomId}/messages` 사용

### 4. POST /chatrooms/current/complete
변경 전: 현재 채팅방 종료
변경 후: 삭제됨
- 채팅방 종료는 서버에서 자동 처리됨

---

## 신규 API

### 5. POST /chatrooms
변경 전: 없음
변경 후: 새로운 채팅방 생성
- 채팅 시작 전 반드시 채팅방을 먼저 생성해야 함
- 응답으로 `chatRoomId` 반환

### 6. POST /chatrooms/{chatRoomId}/messages
변경 전: 없음
변경 후: 특정 채팅방에 메시지 전송
- Request Body: `{ "message": "string" }`
- 기존 `/chatrooms/current/send` 대체

---

## 변경된 API

### 7. GET /chatrooms
변경 전:
```json
{
  "chatRoomId": 1,
  "totalSummary": "전체 요약",
  "situationKeyword": "상황 키워드",
  "solutionKeyword": "해결 키워드",
  "createdAt": "2026-01-12T10:30:00"
}
```
변경 후:
```json
{
  "chatRoomId": 1,
  "title": "채팅방 제목",
  "chatRoomState": "ALIVE",
  "level": 1,
  "lastMessageSentTime": "2026-01-12T10:35:00",
  "createdAt": "2026-01-12T10:30:00"
}
```
- `totalSummary`, `situationKeyword`, `solutionKeyword` 제거
- `title`, `chatRoomState`, `level`, `lastMessageSentTime` 추가

---

## Enum 변경

### ChatRoomState
변경 전: `BEFORE_INIT`, `ALIVE`, `PAUSED`, `NEED_NEXT_QUESTION`, `COMPLETED`, `DELETED`
변경 후: `ALIVE`, `COMPLETED`, `DELETED`
- 상태가 3개로 단순화됨
