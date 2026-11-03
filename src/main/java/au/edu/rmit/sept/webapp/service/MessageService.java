package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.model.Conversation;
import au.edu.rmit.sept.webapp.model.Message;
import au.edu.rmit.sept.webapp.repository.ConversationRepository;
import au.edu.rmit.sept.webapp.repository.MessageRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    public MessageService(
            MessageRepository messageRepository,
            ConversationRepository conversationRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
    }

    public Message sendMessage(
            Long conversationId,
            Long senderId,
            String content) {

        Conversation conversation =
                getConversationForParticipant(
                        conversationId,
                        senderId);

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(
                    "Message cannot be empty");
        }

        Message message = new Message();
        message.setConversationId(
                conversation.getConversationId());
        message.setSenderId(senderId);
        message.setContent(content.trim());

        return messageRepository.save(message);
    }

    public List<Message> getMessages(
            Long conversationId,
            Long userId) {

        getConversationForParticipant(
                conversationId,
                userId);

        return messageRepository.findByConversationId(
                conversationId);
    }

    public int markMessagesAsRead(
            Long conversationId,
            Long userId) {

        getConversationForParticipant(
                conversationId,
                userId);

        return messageRepository
                .markReceivedMessagesAsRead(
                        conversationId,
                        userId);
    }

    private Conversation getConversationForParticipant(
            Long conversationId,
            Long userId) {

        Conversation conversation =
                conversationRepository.findById(conversationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Conversation not found"));

        boolean isParticipant =
                userId.equals(conversation.getSeekerId())
                        || userId.equals(
                                conversation.getProviderId());

        if (!isParticipant) {
            throw new IllegalArgumentException(
                    "You do not have access to this conversation");
        }

        return conversation;
    }
}