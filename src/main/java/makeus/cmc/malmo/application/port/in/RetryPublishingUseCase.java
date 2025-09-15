package makeus.cmc.malmo.application.port.in;

public interface RetryPublishingUseCase {
    void retryPublishing();
    void retryFailedMessages();
}
