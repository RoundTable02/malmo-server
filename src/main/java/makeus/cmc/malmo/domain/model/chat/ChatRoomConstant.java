package makeus.cmc.malmo.domain.model.chat;

public class ChatRoomConstant {
    public static final int INIT_CHATROOM_LEVEL = 1;
    public static final int SYSTEM_PROMPT_LEVEL = -2;
    public static final int SUMMARY_PROMPT_LEVEL = -1;
    public static final int TOTAL_SUMMARY_PROMPT_LEVEL = -3;
    public static final int NOT_COUPLE_MEMBER_LAST_PROMPT_LEVEL = 1;
    public static final int LAST_PROMPT_LEVEL = 4;

    public static final String INIT_CHAT_MESSAGE = "아/야 안녕! 나는 연애 고민 상담사 모모야.\n" +
            "나와의 대화를 마무리하고 싶다면 종료하기 버튼을 눌러줘! 대화 종료 후에는 대화 요약 리포트를 보여줄게.\n" +
            "오늘은 어떤 고민 때문에 나를 찾아왔어? 먼저 연인과 있었던 갈등 상황을 이야기해 주면 내가 같이 고민해볼게!";

    public static final String FINAL_MESSAGE = "이제 대화가 종료되었어! 대화 요약 리포트를 보여줄게.\n" +
            "대화 요약 리포트는 연애 고민 상담사 모모가 함께 대화했던 내용을 바탕으로 고민과 해결책을 분석해본 내용이야.\n" +
            "리포트를 확인하고 싶다면, 종료 버튼을 눌러줘!";
}
