package au.edu.rmit.sept.webapp.dto;

import au.edu.rmit.sept.webapp.model.Message;
import java.time.LocalDateTime;

public class MessageResponse {

    private Long messageId;
    private Long conversationId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;

    public MessageResponse() {
    }

    public MessageResponse(Message message) {
        this.messageId = message.getMessageId();
        this.conversationId = message.getConversationId();
        this.senderId = message.getSenderId();
        this.content = message.getContent();
        this.sentAt = message.getSentAt();
    }

    public Long getMessageId() {
        return messageId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }
}