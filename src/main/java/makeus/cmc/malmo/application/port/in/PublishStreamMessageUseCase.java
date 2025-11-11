package makeus.cmc.malmo.application.port.in;

public interface PublishStreamMessageUseCase {
    void publish(Long outboxId);
}
