package makeus.cmc.malmo.adaptor.out.persistence.entity;

public enum OutboxState {
    PENDING, // 메시지 발행 대기
    SENT, // 메시지 발행 완료
    FAILED, // 메시지 발행 실패 또는 메시지 처리 실패
    DONE // 메시지 처리 완료
}
