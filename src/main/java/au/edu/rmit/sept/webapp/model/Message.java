package au.edu.rmit.sept.webapp.model;

import java.time.LocalDateTime;

public class Message {

    private Long messageId;
    private Long conversationId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;
    private boolean read;

    public Message() {
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isRead() {
    return read;
}

    public void setRead(boolean read) {
        this.read = read;
    }
}