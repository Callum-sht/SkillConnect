package au.edu.rmit.sept.webapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import au.edu.rmit.sept.webapp.model.Conversation;
import au.edu.rmit.sept.webapp.model.Message;
import au.edu.rmit.sept.webapp.repository.ConversationRepository;
import au.edu.rmit.sept.webapp.repository.MessageRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

class MessageServiceTest {

    private FakeMessageRepository messageRepository;
    private FakeConversationRepository conversationRepository;
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageRepository = new FakeMessageRepository();
        conversationRepository =
                new FakeConversationRepository();

        messageService = new MessageService(
                messageRepository,
                conversationRepository);

        Conversation conversation = new Conversation();
        conversation.setConversationId(10L);
        conversation.setSeekerId(1L);
        conversation.setProviderId(2L);

        conversationRepository.conversation = conversation;
    }

    @Test
    void seekerCanSendMessage() {
        Message result =
                messageService.sendMessage(
                        10L,
                        1L,
                        "Hello Provider");

        assertEquals(10L, result.getConversationId());
        assertEquals(1L, result.getSenderId());
        assertEquals("Hello Provider", result.getContent());
        assertEquals(1, messageRepository.saveCount);
    }

    @Test
    void providerCanSendMessage() {
        Message result =
                messageService.sendMessage(
                        10L,
                        2L,
                        "Hello Seeker");

        assertEquals(2L, result.getSenderId());
        assertEquals("Hello Seeker", result.getContent());
        assertEquals(1, messageRepository.saveCount);
    }

    @Test
    void messageContentIsTrimmedBeforeSaving() {
        Message result =
                messageService.sendMessage(
                        10L,
                        1L,
                        "   Hello Provider   ");

        assertEquals(
                "Hello Provider",
                result.getContent());
    }

    @Test
    void emptyMessageCannotBeSent() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> messageService.sendMessage(
                                10L,
                                1L,
                                ""));

        assertEquals(
                "Message cannot be empty",
                exception.getMessage());
        assertEquals(0, messageRepository.saveCount);
    }

    @Test
    void whitespaceOnlyMessageCannotBeSent() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> messageService.sendMessage(
                                10L,
                                1L,
                                "   \t\n   "));

        assertEquals(
                "Message cannot be empty",
                exception.getMessage());
        assertEquals(0, messageRepository.saveCount);
    }

    @Test
    void nonParticipantCannotSendMessage() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> messageService.sendMessage(
                                10L,
                                99L,
                                "Unauthorized"));

        assertEquals(
                "You do not have access to this conversation",
                exception.getMessage());
        assertEquals(0, messageRepository.saveCount);
    }

    @Test
    void missingConversationCannotReceiveMessage() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> messageService.sendMessage(
                                999L,
                                1L,
                                "Hello"));

        assertEquals(
                "Conversation not found",
                exception.getMessage());
    }

    @Test
    void participantCanRetrieveSavedConversationHistory() {
        Message first = createMessage(
                1L,
                10L,
                1L,
                "Hello Provider");

        Message second = createMessage(
                2L,
                10L,
                2L,
                "Hello Seeker");

        Message third = createMessage(
                3L,
                10L,
                1L,
                "Is tomorrow available?");

        messageRepository.messages.add(first);
        messageRepository.messages.add(second);
        messageRepository.messages.add(third);

        List<Message> result =
                messageService.getMessages(10L, 1L);

        assertEquals(3, result.size());

        assertSame(first, result.get(0));
        assertSame(second, result.get(1));
        assertSame(third, result.get(2));

        assertEquals(
                "Hello Provider",
                result.get(0).getContent());

        assertEquals(
                "Hello Seeker",
                result.get(1).getContent());

        assertEquals(
                "Is tomorrow available?",
                result.get(2).getContent());
    }

    @Test
    void nonParticipantCannotRetrieveMessages() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> messageService.getMessages(
                                10L,
                                99L));

        assertEquals(
                "You do not have access to this conversation",
                exception.getMessage());
    }

    @Test
    void providerCanRetrieveSavedConversationHistory() {
        Message seekerMessage = createMessage(
                1L,
                10L,
                1L,
                "Hello Provider");

        Message providerMessage = createMessage(
                2L,
                10L,
                2L,
                "Hello Seeker");

        messageRepository.messages.add(seekerMessage);
        messageRepository.messages.add(providerMessage);

        List<Message> result =
                messageService.getMessages(10L, 2L);

        assertEquals(2, result.size());
        assertSame(seekerMessage, result.get(0));
        assertSame(providerMessage, result.get(1));
    }

    @Test
    void participantCanRetrieveEmptyConversationHistory() {
        List<Message> result =
                messageService.getMessages(10L, 1L);

        assertEquals(0, result.size());
    }

    private Message createMessage(
            Long messageId,
            Long conversationId,
            Long senderId,
            String content) {

        Message message = new Message();
        message.setMessageId(messageId);
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setContent(content);

        return message;
    }

    @Test
    void sentMessageRemainsAvailableInConversationHistory() {
        Message sentMessage =
                messageService.sendMessage(
                        10L,
                        1L,
                        "Persistent message");

        List<Message> history =
                messageService.getMessages(
                        10L,
                        1L);

        assertEquals(1, history.size());
        assertSame(sentMessage, history.get(0));
        assertEquals(
                "Persistent message",
                history.get(0).getContent());
    }

    @Test
    void conversationHistoryPreservesMessageDetails() {
        Message first = createMessage(
                1L,
                10L,
                1L,
                "First message");

        first.setSentAt(
                LocalDateTime.of(
                        2026, 10, 10, 9, 0));

        Message second = createMessage(
                2L,
                10L,
                2L,
                "Second message");

        second.setSentAt(
                LocalDateTime.of(
                        2026, 10, 10, 9, 5));

        Message third = createMessage(
                3L,
                10L,
                1L,
                "Third message");

        third.setSentAt(
                LocalDateTime.of(
                        2026, 10, 10, 9, 10));

        messageRepository.messages.add(first);
        messageRepository.messages.add(second);
        messageRepository.messages.add(third);

        List<Message> history =
                messageService.getMessages(
                        10L,
                        1L);

        assertEquals(3, history.size());

        assertEquals(
                1L,
                history.get(0).getSenderId());
        assertEquals(
                LocalDateTime.of(
                        2026, 10, 10, 9, 0),
                history.get(0).getSentAt());

        assertEquals(
                2L,
                history.get(1).getSenderId());
        assertEquals(
                LocalDateTime.of(
                        2026, 10, 10, 9, 5),
                history.get(1).getSentAt());

        assertEquals(
                1L,
                history.get(2).getSenderId());
        assertEquals(
                LocalDateTime.of(
                        2026, 10, 10, 9, 10),
                history.get(2).getSentAt());
    }

    @Test
    void seekerCanMarkReceivedMessagesAsRead() {
        Message ownMessage = new Message();
        ownMessage.setConversationId(10L);
        ownMessage.setSenderId(1L);
        ownMessage.setContent("Hello");
        ownMessage.setRead(false);

        Message receivedMessage = new Message();
        receivedMessage.setConversationId(10L);
        receivedMessage.setSenderId(2L);
        receivedMessage.setContent("Hi");
        receivedMessage.setRead(false);

        messageRepository.messages.add(ownMessage);
        messageRepository.messages.add(receivedMessage);

        int result =
                messageService.markMessagesAsRead(
                        10L,
                        1L);

        assertEquals(1, result);
        assertEquals(false, ownMessage.isRead());
        assertEquals(true, receivedMessage.isRead());
        assertEquals(1, messageRepository.markReadCount);
    }

    @Test
    void providerCanMarkReceivedMessagesAsRead() {
        Message receivedMessage = new Message();
        receivedMessage.setConversationId(10L);
        receivedMessage.setSenderId(1L);
        receivedMessage.setContent("Hello Provider");
        receivedMessage.setRead(false);

        messageRepository.messages.add(
                receivedMessage);

        int result =
                messageService.markMessagesAsRead(
                        10L,
                        2L);

        assertEquals(1, result);
        assertEquals(true, receivedMessage.isRead());
    }

    @Test
    void nonParticipantCannotMarkMessagesAsRead() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                messageService
                                        .markMessagesAsRead(
                                                10L,
                                                99L));

        assertEquals(
                "You do not have access to this conversation",
                exception.getMessage());

        assertEquals(
                0,
                messageRepository.markReadCount);
    }

    @Test
    void missingConversationCannotMarkMessagesAsRead() {
        conversationRepository.conversation = null;

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                messageService
                                        .markMessagesAsRead(
                                                999L,
                                                1L));

        assertEquals(
                "Conversation not found",
                exception.getMessage());

        assertEquals(
                0,
                messageRepository.markReadCount);
    }

    private static class FakeMessageRepository
            extends MessageRepository {

        private final List<Message> messages =
                new ArrayList<>();

        private int saveCount;
        private int markReadCount;

        FakeMessageRepository() {
            super(null);
        }

        @Override
        public Message save(Message message) {
            saveCount++;
            messages.add(message);
            return message;
        }

        @Override
        public List<Message> findByConversationId(
                Long conversationId) {

            return messages.stream()
                    .filter(message ->
                            conversationId.equals(
                                    message.getConversationId()))
                    .toList();
        }

        @Override
        public int markReceivedMessagesAsRead(
                Long conversationId,
                Long userId) {

            markReadCount++;

            int updated = 0;

            for (Message message : messages) {
                if (conversationId.equals(
                            message.getConversationId())
                        && !userId.equals(
                            message.getSenderId())
                        && !message.isRead()) {

                    message.setRead(true);
                    updated++;
                }
            }

            return updated;
        }
    }

    private static class FakeConversationRepository
            extends ConversationRepository {

        private Conversation conversation;

        FakeConversationRepository() {
            super(null);
        }

        @Override
        public Optional<Conversation> findById(
                Long conversationId) {

            if (conversation == null
                    || !conversationId.equals(
                            conversation.getConversationId())) {
                return Optional.empty();
            }

            return Optional.of(conversation);
        }
    }
}