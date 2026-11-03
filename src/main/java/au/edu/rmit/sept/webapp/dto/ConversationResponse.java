package au.edu.rmit.sept.webapp.dto;

import au.edu.rmit.sept.webapp.model.Conversation;
import java.time.LocalDateTime;

public class ConversationResponse {

    private Long conversationId;
    private Long seekerId;
    private Long providerId;
    private String otherParticipantName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ConversationResponse() {
    }

    public ConversationResponse(
            Conversation conversation,
            String otherParticipantName) {

        this.conversationId = conversation.getConversationId();
        this.seekerId = conversation.getSeekerId();
        this.providerId = conversation.getProviderId();
        this.otherParticipantName = otherParticipantName;
        this.createdAt = conversation.getCreatedAt();
        this.updatedAt = conversation.getUpdatedAt();
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getSeekerId() {
        return seekerId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public String getOtherParticipantName() {
        return otherParticipantName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}