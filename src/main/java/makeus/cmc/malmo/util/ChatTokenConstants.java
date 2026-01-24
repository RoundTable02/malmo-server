package makeus.cmc.malmo.util;

public class ChatTokenConstants {
    /**
     * 4단계 자유 대화에서 최근 메시지 개수 제한
     */
    public static final int FREE_CONVERSATION_RECENT_MESSAGE_LIMIT = 20;

    /**
     * 4단계 자유 대화에서 요약 생성 임계값 (이 개수 초과 시 요약 생성)
     */
    public static final int FREE_CONVERSATION_SUMMARY_THRESHOLD = 30;

    /**
     * 4단계 자유 대화에서 요약 생성 주기 (이 개수 단위로 요약 생성)
     */
    public static final int FREE_CONVERSATION_SUMMARY_INTERVAL = 20;
}
