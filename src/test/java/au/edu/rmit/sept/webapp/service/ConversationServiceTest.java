package au.edu.rmit.sept.webapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import au.edu.rmit.sept.webapp.model.Conversation;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.repository.ConversationRepository;
import au.edu.rmit.sept.webapp.repository.MessageRepository;
import au.edu.rmit.sept.webapp.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import au.edu.rmit.sept.webapp.model.Message;
import au.edu.rmit.sept.webapp.dto.ConversationSummaryResponse;
import java.time.LocalDateTime;
import java.util.List;

class ConversationServiceTest {

    private FakeConversationRepository conversationRepository;
    private FakeUserRepository userRepository;
    private ConversationService conversationService;
    private FakeMessageRepository messageRepository;

    @BeforeEach
    void setUp() {
        conversationRepository = new FakeConversationRepository();
        userRepository = new FakeUserRepository();
        messageRepository = new FakeMessageRepository();
        conversationService = new ConversationService(
                conversationRepository,
                userRepository,
                messageRepository);
    }

    @Test
    void seekerCanStartNewConversationWithProvider() {
        userRepository.addUser(createUser(1L, UserRole.SEEKER));
        userRepository.addUser(createUser(2L, UserRole.PROVIDER));

        Conversation result =
                conversationService.startConversation(1L, 2L);

        assertEquals(1L, result.getSeekerId());
        assertEquals(2L, result.getProviderId());
        assertEquals(1, conversationRepository.saveCount);
    }

    @Test
    void existingConversationIsReused() {
        userRepository.addUser(createUser(1L, UserRole.SEEKER));
        userRepository.addUser(createUser(2L, UserRole.PROVIDER));

        Conversation existing = new Conversation();
        existing.setConversationId(10L);
        existing.setSeekerId(1L);
        existing.setProviderId(2L);

        conversationRepository.conversation = existing;

        Conversation result =
                conversationService.startConversation(1L, 2L);

        assertSame(existing, result);
        assertEquals(10L, result.getConversationId());
        assertEquals(0, conversationRepository.saveCount);
    }

    @Test
    void nonSeekerCannotStartConversation() {
        userRepository.addUser(createUser(1L, UserRole.PROVIDER));
        userRepository.addUser(createUser(2L, UserRole.PROVIDER));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> conversationService.startConversation(1L, 2L));

        assertEquals(
                "Only Seekers can start a conversation",
                exception.getMessage());
        assertEquals(0, conversationRepository.saveCount);
    }

    @Test
    void missingSeekerCannotStartConversation() {
        userRepository.addUser(createUser(2L, UserRole.PROVIDER));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> conversationService.startConversation(1L, 2L));

        assertEquals("Seeker not found", exception.getMessage());
        assertEquals(0, conversationRepository.saveCount);
    }

    @Test
    void missingProviderCannotStartConversation() {
        userRepository.addUser(createUser(1L, UserRole.SEEKER));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> conversationService.startConversation(1L, 2L));

        assertEquals("Provider not found", exception.getMessage());
        assertEquals(0, conversationRepository.saveCount);
    }

    @Test
    void conversationTargetMustBeProvider() {
        userRepository.addUser(createUser(1L, UserRole.SEEKER));
        userRepository.addUser(createUser(2L, UserRole.SEEKER));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> conversationService.startConversation(1L, 2L));

        assertEquals(
                "Conversation target must be a Provider",
                exception.getMessage());
        assertEquals(0, conversationRepository.saveCount);
    }

    private User createUser(Long userId, UserRole role) {
        User user = new User();
        user.setUserId(userId);
        user.setRole(role);
        return user;
    }

    private User createUser(
            Long userId,
            String name,
            UserRole role) {

        User user = new User();
        user.setUserId(userId);
        user.setName(name);
        user.setRole(role);

        return user;
    }

    private Conversation createConversation(
        Long conversationId,
        Long seekerId,
        Long providerId) {

        Conversation conversation = new Conversation();
        conversation.setConversationId(conversationId);
        conversation.setSeekerId(seekerId);
        conversation.setProviderId(providerId);

        return conversation;
    }

    @Test
    void seekerSeesProviderAsOtherParticipant() {
        Conversation conversation = new Conversation();
        conversation.setConversationId(10L);
        conversation.setSeekerId(1L);
        conversation.setProviderId(2L);

        userRepository.users.put(
                2L,
                createUser(
                        2L,
                        "Alice Provider",
                        UserRole.PROVIDER));

        String result =
                conversationService
                        .getOtherParticipantName(
                                conversation,
                                1L);

        assertEquals("Alice Provider", result);
    }

    @Test
    void providerSeesSeekerAsOtherParticipant() {
        Conversation conversation = new Conversation();
        conversation.setConversationId(10L);
        conversation.setSeekerId(1L);
        conversation.setProviderId(2L);

        userRepository.users.put(
                1L,
                createUser(
                        1L,
                        "Yihan Seeker",
                        UserRole.SEEKER));

        String result =
                conversationService
                        .getOtherParticipantName(
                                conversation,
                                2L);

        assertEquals("Yihan Seeker", result);
    }

    @Test
    void seekerCanGetTheirConversation() {
        Conversation existing = createConversation(
                10L, 1L, 2L);

        conversationRepository.conversation = existing;

        Conversation result =
                conversationService.getConversation(10L, 1L);

        assertSame(existing, result);
    }

    @Test
    void providerCanGetTheirConversation() {
        Conversation existing = createConversation(
                10L, 1L, 2L);

        conversationRepository.conversation = existing;

        Conversation result =
                conversationService.getConversation(10L, 2L);

        assertSame(existing, result);
    }

    @Test
    void nonParticipantCannotGetConversation() {
        conversationRepository.conversation =
                createConversation(10L, 1L, 2L);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> conversationService.getConversation(10L, 99L));

        assertEquals(
                "You do not have access to this conversation",
                exception.getMessage());
    }

    @Test
    void missingConversationCannotBeRetrieved() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> conversationService.getConversation(999L, 1L));

        assertEquals(
                "Conversation not found",
                exception.getMessage());
    }

    @Test
    void seekerCanViewConversationSummaries() {
        Conversation conversation =
                createConversation(10L, 1L, 2L);

        conversation.setCreatedAt(
                LocalDateTime.of(2026, 10, 9, 10, 0));

        conversationRepository.conversations.add(
                conversation);

        userRepository.addUser(
                createUser(
                        2L,
                        "Alice Provider",
                        UserRole.PROVIDER));

        Message message = new Message();
        message.setMessageId(100L);
        message.setConversationId(10L);
        message.setSenderId(2L);
        message.setContent("Is tomorrow available?");
        message.setSentAt(
                LocalDateTime.of(
                        2026, 10, 9, 11, 30));

        messageRepository.latestMessage = message;
        messageRepository.unreadCount = 2;

        List<ConversationSummaryResponse> result =
                conversationService
                        .getConversationsForUser(1L);

        assertEquals(1, result.size());
        assertEquals(
                10L,
                result.get(0).getConversationId());
        assertEquals(
                "Alice Provider",
                result.get(0).getOtherParticipantName());
        assertEquals(
                "Is tomorrow available?",
                result.get(0).getLatestMessage());
        assertEquals(
                message.getSentAt(),
                result.get(0).getLatestMessageTime());
        assertEquals(
                2,
                result.get(0).getUnreadCount());
    }

    @Test
    void providerSeesSeekerInConversationSummary() {
        Conversation conversation =
                createConversation(10L, 1L, 2L);

        conversation.setCreatedAt(
                LocalDateTime.of(2026, 10, 9, 10, 0));

        conversationRepository.conversations.add(
                conversation);

        userRepository.addUser(
                createUser(
                        1L,
                        "Yihan Seeker",
                        UserRole.SEEKER));

        List<ConversationSummaryResponse> result =
                conversationService
                        .getConversationsForUser(2L);

        assertEquals(1, result.size());
        assertEquals(
                "Yihan Seeker",
                result.get(0).getOtherParticipantName());
    }

    @Test
    void conversationWithoutMessagesHasNoLatestMessage() {
        LocalDateTime createdAt =
                LocalDateTime.of(
                        2026, 10, 9, 10, 0);

        Conversation conversation =
                createConversation(10L, 1L, 2L);

        conversation.setCreatedAt(createdAt);

        conversationRepository.conversations.add(
                conversation);

        userRepository.addUser(
                createUser(
                        2L,
                        "Alice Provider",
                        UserRole.PROVIDER));

        List<ConversationSummaryResponse> result =
                conversationService
                        .getConversationsForUser(1L);

        assertEquals(1, result.size());
        assertEquals(
                "Alice Provider",
                result.get(0).getOtherParticipantName());
        assertEquals(
                null,
                result.get(0).getLatestMessage());
        assertEquals(
                createdAt,
                result.get(0).getLatestMessageTime());
        assertEquals(
                0,
                result.get(0).getUnreadCount());
    }

    private static class FakeConversationRepository
            extends ConversationRepository {

        private Conversation conversation;

        private final List<Conversation> conversations =
                new java.util.ArrayList<>();

        private int saveCount;

        FakeConversationRepository() {
            super(null);
        }

        @Override
        public Optional<Conversation> findByParticipants(
                Long seekerId,
                Long providerId) {

            if (conversation == null) {
                return Optional.empty();
            }

            if (conversation.getSeekerId().equals(seekerId)
                    && conversation.getProviderId().equals(providerId)) {
                return Optional.of(conversation);
            }

            return Optional.empty();
        }

        @Override
        public Conversation save(Conversation conversation) {
            saveCount++;
            this.conversation = conversation;
            return conversation;
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

        @Override
        public List<Conversation> findByParticipantId(
                Long userId) {

            return conversations.stream()
                    .filter(conversation ->
                            userId.equals(
                                    conversation.getSeekerId())
                            || userId.equals(
                                    conversation.getProviderId()))
                    .toList();
        }
    }

    private static class FakeUserRepository extends UserRepository {

        private final Map<Long, User> users = new HashMap<>();

        FakeUserRepository() {
            super(null);
        }

        void addUser(User user) {
            users.put(user.getUserId(), user);
        }

        @Override
        public Optional<User> findById(Long userId) {
            return Optional.ofNullable(users.get(userId));
        }
    }

    private static class FakeMessageRepository
            extends MessageRepository {

        private Message latestMessage;
        private int unreadCount;

        FakeMessageRepository() {
            super(null);
        }

        @Override
        public Optional<Message> findLatestByConversationId(
                Long conversationId) {

            return Optional.ofNullable(latestMessage);
        }

        @Override
        public int countUnreadByConversationId(
                Long conversationId,
                Long userId) {

            return unreadCount;
        }
    }
}