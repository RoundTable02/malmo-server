-- 작업 01: ChatRoomState enum 정리 마이그레이션
-- BEFORE_INIT, PAUSED, NEED_NEXT_QUESTION 상태를 ALIVE로 변경
-- COMPLETED와 DELETED 상태는 그대로 유지

-- BEFORE_INIT 상태를 ALIVE로 변경
UPDATE chat_room
SET chat_room_state = 'ALIVE'
WHERE chat_room_state = 'BEFORE_INIT';

-- PAUSED 상태를 ALIVE로 변경
UPDATE chat_room
SET chat_room_state = 'ALIVE'
WHERE chat_room_state = 'PAUSED';

-- NEED_NEXT_QUESTION 상태를 ALIVE로 변경
UPDATE chat_room
SET chat_room_state = 'ALIVE'
WHERE chat_room_state = 'NEED_NEXT_QUESTION';

-- 마이그레이션 확인 쿼리 (실행 후 확인용)
-- SELECT chat_room_state, COUNT(*) as count
-- FROM chat_room
-- GROUP BY chat_room_state;

-- 작업 02: ChatRoom 도메인 모델 리팩토링 마이그레이션
-- title 컬럼 추가 (채팅방 제목)

-- title 컬럼 추가
ALTER TABLE chat_room ADD COLUMN title VARCHAR(255);

-- 기존 데이터는 title이 null이므로 별도 업데이트 불필요
-- 새 채팅방은 1단계 종료 후 title이 생성됨

-- 작업 06: 제목 생성 기능을 위한 마이그레이션
-- prompt 테이블에 is_for_title_generation 컬럼 추가

-- is_for_title_generation 컬럼 추가
ALTER TABLE prompt ADD COLUMN is_for_title_generation BOOLEAN DEFAULT FALSE;

-- 제목 생성 프롬프트 데이터 추가
INSERT INTO prompt (level, content, is_for_system, is_for_summary, is_for_completed_response,
                    is_for_total_summary, is_for_guideline, is_for_answer_metadata, is_for_title_generation)
VALUES (0, '다음 대화 내용을 바탕으로 20자 이내의 간결한 제목을 생성해주세요.
제목은 사용자의 고민을 잘 반영해야 합니다.
제목만 출력하고 따옴표나 부가 설명은 포함하지 마세요.',
        false, false, false, false, false, false, true);

-- 기존 채팅방의 totalSummary를 title로 마이그레이션
-- COMPLETED 상태이고 totalSummary가 있는 채팅방의 경우
-- totalSummary의 앞부분을 title로 복사 (최대 255자)
UPDATE chat_room
SET title = CASE
    WHEN LENGTH(total_summary) > 50 THEN CONCAT(SUBSTRING(total_summary, 1, 47), '...')
    ELSE total_summary
END
WHERE chat_room_state = 'COMPLETED'
  AND total_summary IS NOT NULL
  AND title IS NULL;

-- 마이그레이션 확인 쿼리 (실행 후 확인용)
-- SELECT chat_room_id, chat_room_state, 
--        LENGTH(total_summary) as summary_length, 
--        LENGTH(title) as title_length,
--        title
-- FROM chat_room
-- WHERE chat_room_state = 'COMPLETED'
-- ORDER BY chat_room_id DESC
-- LIMIT 10;
