package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.model.Conversation;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.repository.ConversationRepository;
import au.edu.rmit.sept.webapp.repository.MessageRepository;
import au.edu.rmit.sept.webapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import au.edu.rmit.sept.webapp.dto.ConversationSummaryResponse;
import au.edu.rmit.sept.webapp.model.Message;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public ConversationService(
            ConversationRepository conversationRepository,
            UserRepository userRepository,
            MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    public Conversation startConversation(Long seekerId, Long providerId) {
        User seeker = userRepository.findById(seekerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Seeker not found"));

        if (seeker.getRole() != UserRole.SEEKER) {
            throw new IllegalArgumentException(
                    "Only Seekers can start a conversation");
        }

        User provider = userRepository.findById(providerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Provider not found"));

        if (provider.getRole() != UserRole.PROVIDER) {
            throw new IllegalArgumentException(
                    "Conversation target must be a Provider");
        }

        return conversationRepository
                .findByParticipants(seekerId, providerId)
                .orElseGet(() -> {
                    Conversation conversation = new Conversation();
                    conversation.setSeekerId(seekerId);
                    conversation.setProviderId(providerId);

                    return conversationRepository.save(conversation);
                });
    }

    public Conversation getConversation(
            Long conversationId,
            Long userId) {

        Conversation conversation =
                conversationRepository.findById(conversationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Conversation not found"));

        boolean isParticipant =
                userId.equals(conversation.getSeekerId())
                        || userId.equals(conversation.getProviderId());

        if (!isParticipant) {
            throw new IllegalArgumentException(
                    "You do not have access to this conversation");
        }

        return conversation;
    }

    public String getOtherParticipantName(
            Conversation conversation,
            Long currentUserId) {

        Long otherParticipantId;

        if (currentUserId.equals(
                conversation.getSeekerId())) {

            otherParticipantId =
                    conversation.getProviderId();

        } else if (currentUserId.equals(
                conversation.getProviderId())) {

            otherParticipantId =
                    conversation.getSeekerId();

        } else {
            throw new IllegalArgumentException(
                    "You do not have access to this conversation");
        }

        User otherParticipant =
                userRepository.findById(otherParticipantId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Participant not found"));

        return otherParticipant.getName();
    }

    public List<ConversationSummaryResponse> getConversationsForUser(
            Long userId) {

        return conversationRepository
                .findByParticipantId(userId)
                .stream()
                .map(conversation ->
                        createConversationSummary(
                                conversation,
                                userId))
                .toList();
    }

    private ConversationSummaryResponse createConversationSummary(
            Conversation conversation,
            Long userId) {

        String otherParticipantName =
                getOtherParticipantName(
                        conversation,
                        userId);

        Optional<Message> latestMessage =
                messageRepository.findLatestByConversationId(
                        conversation.getConversationId());

        int unreadCount =
            messageRepository.countUnreadByConversationId(
                    conversation.getConversationId(),
                    userId);

        if (latestMessage.isPresent()) {
            Message message = latestMessage.get();

            return new ConversationSummaryResponse(
                    conversation.getConversationId(),
                    otherParticipantName,
                    message.getContent(),
                    message.getSentAt(),
                    unreadCount);
        }

        return new ConversationSummaryResponse(
                conversation.getConversationId(),
                otherParticipantName,
                null,
                conversation.getCreatedAt(),
                    unreadCount);
    }
    
}