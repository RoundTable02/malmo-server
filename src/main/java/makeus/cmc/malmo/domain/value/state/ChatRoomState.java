package makeus.cmc.malmo.domain.value.state;

public enum ChatRoomState {
    ALIVE,        // 진행 중 (생성 즉시 ALIVE)
    COMPLETED,    // 기존 완료된 채팅방 (보고서 조회용)
    DELETED       // 삭제됨 (soft delete)
}