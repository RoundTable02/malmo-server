package makeus.cmc.malmo.application.port.in;

public interface MarkOutboxUseCase {
    void markOutboxDone(Long outboxId);
    void markOutboxFailed(Long outboxId);
}
