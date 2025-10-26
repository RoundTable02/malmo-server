package makeus.cmc.malmo.util;

public class GlobalConstants {
    // 오늘의 질문 상수
    public static final int FIRST_QUESTION_LEVEL = 1;

    // 채팅방 관련 상수
    public static final int INIT_CHATROOM_LEVEL = 1;
    public static final String INIT_CHAT_MESSAGE = " 안녕! 나는 연애 고민 상담사 모모야.\n" +
            "나와의 대화를 마무리하고 싶다면 종료하기 버튼을 눌러줘! 대화 종료 후에는 대화 요약 리포트를 보여줄게.\n" +
            "오늘은 어떤 고민 때문에 나를 찾아왔어? 먼저 연인과 있었던 갈등 상황을 이야기해 주면 내가 같이 고민해볼게!";

    public static final String EXPIRED_ROOM_CREATING_SUMMARY_LINE = "하루가 지나 채팅방이 만료되었습니다. 요약 생성 중...";

    public static final String COMPLETED_ROOM_CREATING_SUMMARY_LINE = "채팅방이 종료되었습니다. 요약 생성 중...";

    public static final String OPENAI_CHAT_URL = "https://api.openai.com/v1";

    public static final String OPENAI_STATUS_URL = "https://status.openai.com/api/v2/status.json";

}
