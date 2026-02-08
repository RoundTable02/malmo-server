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

-- 작업 07: 4단계 프롬프트 추가
-- 4단계는 전체 상담 완료 후 사용자가 이전 맥락을 유지하며 자유롭게 대화하는 단계

-- 4단계 가이드라인 프롬프트 추가
INSERT INTO prompt (level, content, is_for_system, is_for_summary, is_for_completed_response,
                    is_for_total_summary, is_for_guideline, is_for_answer_metadata, is_for_title_generation)
VALUES (4, '현재는 상담의 [4단계: 자유 대화] 단계야.

4단계는 1~3단계의 정형화된 상담이 종료된 후, 사용자가 이전 상담 맥락을 바탕으로 추가 질문을 하거나 자유롭게 대화하는 단계야.

[응답 생성 규칙]
1. 이전 1~3단계에서 나눴던 갈등 상황, 분석 내용, 해결책 등의 맥락을 충분히 활용할 것
2. 사용자의 질문이나 고민이 이전 상담 내용과 연결되는 경우, 그 맥락을 자연스럽게 언급하며 응답할 것
3. 새로운 고민이나 질문이 나온 경우에도, 이전 상담에서 파악한 사용자의 애착유형, 연애 가치관, 감정 패턴 등을 고려하여 맞춤형 조언을 제공할 것
4. 친구처럼 편안하게 대화하되, 연애 상담 전문가로서의 전문성을 잃지 말 것
5. 사용자가 이전 상담 내용에 대해 추가 질문을 하면, 구체적이고 깊이 있게 답변할 것
6. 단계가 구분되어 있다는 것을 사용자가 인지하지 못하도록, 자연스럽게 대화를 이어갈 것

[주의사항]
- 형식적인 종료나 새로운 시작을 알리는 멘트는 하지 말 것
- 이전 대화 내용을 요약하거나 반복하는 것보다는, 그 내용을 바탕으로 새로운 인사이트를 제공할 것
- 사용자가 완전히 새로운 주제를 꺼내더라도, 가능하다면 이전 맥락과 연결지어 일관성 있는 상담을 유지할 것',
        false, false, false, false, true, false, false);

-- 4단계 요약 프롬프트 추가
INSERT INTO prompt (level, content, is_for_system, is_for_summary, is_for_completed_response,
                    is_for_total_summary, is_for_guideline, is_for_answer_metadata, is_for_title_generation)
VALUES (4, '대화를 바탕으로 핵심적인 내용을 요약해줘.
지금 전달된 대화는 앞으로 참조하지 않을 것이며, 오로지 너가 요약한 내용만을 참조할거야.
4단계는 자유 대화 단계이므로, 사용자가 새롭게 제기한 질문이나 고민, 그리고 그에 대한 답변의 핵심을 요약할 것.
만약 이전 상담 내용과 연결되는 대화라면, 그 연결점도 명시할 것.
*요약 글자수 제한 150자*

[필수 규칙]
- "사용자"라는 표현은 절대 쓰지 말고 해당 표현이 필요한 상황에서는 그냥 주어를 생략할 것.
- "Assistant", "OK" 등 일상 생활에서 사용하지 않는 표현은 절대 응답에 넣지 말 것.
- 명사형 전성어미 (-ㅁ/음)을 이용해 마무리할 것.',
        false, true, false, false, false, false, false);

-- 4단계 세부 프롬프트 추가 (DetailedPrompt)
-- 4단계는 단일 세부 단계로 구성 (detailedLevel = 1)

-- 4단계 가이드라인 상세 프롬프트
-- 4단계는 충분성 검사를 하지 않으므로 검증 프롬프트는 불필요
INSERT INTO detailed_prompt (level, detailed_level, content, is_for_validation, is_for_summary,
                             metadata_title, is_last_detailed_prompt, is_for_guideline)
VALUES (4, 1, '[응답 생성 규칙]
이전 상담(1~3단계)의 맥락을 바탕으로 사용자의 질문이나 고민에 응답할 것:

1. 사용자 메시지 파악
   - 이전 상담 내용과 관련된 추가 질문인지 확인
   - 완전히 새로운 주제나 고민인지 확인

2. 맥락 연결
   - 관련 질문인 경우: 이전 단계의 갈등 분석, 애착유형 분석, 제안한 해결책 등을 자연스럽게 참조
   - 새로운 주제인 경우: 이전에 파악한 사용자의 애착유형, 연애 가치관, 감정 패턴을 고려하여 조언

3. 응답 생성
   - 구체적이고 실용적인 조언 제공
   - 필요시 이전 대화 내용과 연결하여 일관성 유지
   - 친구처럼 자연스럽게, 하지만 전문가로서 깊이 있게 답변

4. 추가 대화 유도
   - 사용자가 더 궁금한 점이 있는지 자연스럽게 확인
   - 열린 질문으로 대화를 이어갈 수 있도록 유도

[주의사항]
- 충분성 조건 검증이 없으므로, 자유롭게 대화하되 깊이 있는 상담 품질 유지
- 사용자가 만족스러운 답변을 받을 때까지 성의 있게 응답
- 이전 상담 내용을 단순 반복하지 말고, 새로운 관점이나 구체적인 예시 추가', 
        false, false, '자유 대화 응답', true, true);

-- 마이그레이션 확인 쿼리 (실행 후 확인용)
-- SELECT level, content, is_for_guideline, is_for_summary
-- FROM prompt
-- WHERE level = 4
-- ORDER BY prompt_id;
--
-- SELECT level, detailed_level, is_for_guideline, is_for_validation, metadata_title
-- FROM detailed_prompt
-- WHERE level = 4
-- ORDER BY detailed_prompt_id;
