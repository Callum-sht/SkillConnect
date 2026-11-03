package au.edu.rmit.sept.webapp.dto;

public class CreateConversationRequest {

    private Long providerId;

    public CreateConversationRequest() {
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }
}