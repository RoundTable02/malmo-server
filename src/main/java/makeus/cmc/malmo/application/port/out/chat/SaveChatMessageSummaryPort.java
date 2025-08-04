package makeus.cmc.malmo.application.port.out.chat;

import makeus.cmc.malmo.domain.model.chat.ChatMessageSummary;

public interface SaveChatMessageSummaryPort {

    void saveChatMessageSummary(ChatMessageSummary chatMessageSummary);

}
