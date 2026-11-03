package au.edu.rmit.sept.webapp.dto;

import java.time.LocalDateTime;

public class ConversationSummaryResponse {

    private Long conversationId;
    private String otherParticipantName;
    private String latestMessage;
    private LocalDateTime latestMessageTime;
    private int unreadCount;

    public ConversationSummaryResponse(
            Long conversationId,
            String otherParticipantName,
            String latestMessage,
            LocalDateTime latestMessageTime,
            int unreadCount) {

        this.conversationId = conversationId;
        this.otherParticipantName = otherParticipantName;
        this.latestMessage = latestMessage;
        this.latestMessageTime = latestMessageTime;
        this.unreadCount = unreadCount;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public String getOtherParticipantName() {
        return otherParticipantName;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public LocalDateTime getLatestMessageTime() {
        return latestMessageTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }
}