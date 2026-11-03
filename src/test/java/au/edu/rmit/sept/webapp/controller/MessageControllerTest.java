package au.edu.rmit.sept.webapp.controller;

import au.edu.rmit.sept.webapp.model.Message;
import au.edu.rmit.sept.webapp.service.MessageService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

class MessageControllerTest {

    private MockMvc createMockMvc(MessageService messageService) {
        MessageController controller =
                new MessageController(messageService);

        return MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void participantCanSendMessage() throws Exception {
        StubMessageService service =
                new StubMessageService();

        MockMvc mockMvc = createMockMvc(service);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(
                        post("/api/conversations/10/messages")
                                .session(session)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "content": "Hello Provider"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(100))
                .andExpect(jsonPath("$.conversationId").value(10))
                .andExpect(jsonPath("$.senderId").value(1))
                .andExpect(jsonPath("$.content")
                        .value("Hello Provider"));

        assertEquals(10L, service.lastConversationId);
        assertEquals(1L, service.lastUserId);
        assertEquals("Hello Provider", service.lastContent);
    }

    @Test
    void unauthenticatedUserCannotSendMessage()
            throws Exception {

        MockMvc mockMvc =
                createMockMvc(new StubMessageService());

        mockMvc.perform(
                        post("/api/conversations/10/messages")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "content": "Hello"
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(
                        "User must be logged in"));
    }

    @Test
    void invalidMessageReturnsBadRequest()
            throws Exception {

        StubMessageService service =
                new StubMessageService();

        service.errorMessage =
                "Message cannot be empty";

        MockMvc mockMvc = createMockMvc(service);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(
                        post("/api/conversations/10/messages")
                                .session(session)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "content": "   "
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "Message cannot be empty"));
    }

    @Test
    void participantCanGetMessages() throws Exception {
        StubMessageService service =
                new StubMessageService();

        service.messages = List.of(
                createMessage(
                        100L,
                        10L,
                        1L,
                        "First message"),
                createMessage(
                        101L,
                        10L,
                        2L,
                        "Second message"));

        MockMvc mockMvc = createMockMvc(service);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(
                        get("/api/conversations/10/messages")
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].messageId")
                        .value(100))
                .andExpect(jsonPath("$[0].senderId")
                        .value(1))
                .andExpect(jsonPath("$[0].content")
                        .value("First message"))
                .andExpect(jsonPath("$[0].sentAt")
                        .exists())

                .andExpect(jsonPath("$[1].messageId")
                        .value(101))
                .andExpect(jsonPath("$[1].senderId")
                        .value(2))
                .andExpect(jsonPath("$[1].content")
                        .value("Second message"))
                .andExpect(jsonPath("$[1].sentAt")
                        .exists());

        assertEquals(10L, service.lastConversationId);
        assertEquals(1L, service.lastUserId);
    }

    @Test
    void unauthenticatedUserCannotGetMessages()
            throws Exception {

        MockMvc mockMvc =
                createMockMvc(new StubMessageService());

        mockMvc.perform(
                        get("/api/conversations/10/messages")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(
                        "User must be logged in"));
    }

    @Test
    void nonParticipantCannotGetMessages()
            throws Exception {

        StubMessageService service =
                new StubMessageService();

        service.errorMessage =
                "You do not have access to this conversation";

        MockMvc mockMvc = createMockMvc(service);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 99L);

        mockMvc.perform(
                        get("/api/conversations/10/messages")
                                .session(session)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "You do not have access to this conversation"));
    }

    @Test
    void participantCanMarkMessagesAsRead()
            throws Exception {

        StubMessageService service =
                new StubMessageService();

        MockMvc mockMvc = createMockMvc(service);

        MockHttpSession session =
                new MockHttpSession();

        session.setAttribute("userId", 1L);

        mockMvc.perform(
                        put(
                                "/api/conversations/10/messages/read")
                                .session(session)
                )
                .andExpect(
                        status().isNoContent());

        assertEquals(
                10L,
                service.lastConversationId);

        assertEquals(
                1L,
                service.lastUserId);
    }

    @Test
    void unauthenticatedUserCannotMarkMessagesAsRead()
            throws Exception {

        MockMvc mockMvc =
                createMockMvc(
                        new StubMessageService());

        mockMvc.perform(
                        put(
                                "/api/conversations/10/messages/read")
                )
                .andExpect(
                        status().isUnauthorized())
                .andExpect(
                        content().string(
                                "User must be logged in"));
    }

    @Test
    void nonParticipantCannotMarkMessagesAsRead()
            throws Exception {

        StubMessageService service =
                new StubMessageService();

        service.errorMessage =
                "You do not have access to this conversation";

        MockMvc mockMvc =
                createMockMvc(service);

        MockHttpSession session =
                new MockHttpSession();

        session.setAttribute("userId", 99L);

        mockMvc.perform(
                        put(
                                "/api/conversations/10/messages/read")
                                .session(session)
                )
                .andExpect(
                        status().isBadRequest())
                .andExpect(
                        content().string(
                                "You do not have access to this conversation"));
    }

    private static Message createMessage(
            Long messageId,
            Long conversationId,
            Long senderId,
            String content) {

        Message message = new Message();
        message.setMessageId(messageId);
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setSentAt(
                LocalDateTime.of(
                        2026, 10, 8, 20, 30));

        return message;
    }

    private static class StubMessageService
            extends MessageService {

        private Long lastConversationId;
        private Long lastUserId;
        private String lastContent;
        private String errorMessage;
        private List<Message> messages = List.of();

        StubMessageService() {
            super(null, null);
        }

        @Override
        public Message sendMessage(
                Long conversationId,
                Long senderId,
                String content) {

            lastConversationId = conversationId;
            lastUserId = senderId;
            lastContent = content;

            if (errorMessage != null) {
                throw new IllegalArgumentException(
                        errorMessage);
            }

            return createMessage(
                    100L,
                    conversationId,
                    senderId,
                    content);
        }

        @Override
        public List<Message> getMessages(
                Long conversationId,
                Long userId) {

            lastConversationId = conversationId;
            lastUserId = userId;

            if (errorMessage != null) {
                throw new IllegalArgumentException(
                        errorMessage);
            }

            return messages;
        }

        @Override
        public int markMessagesAsRead(
                Long conversationId,
                Long userId) {

            lastConversationId = conversationId;
            lastUserId = userId;

            if (errorMessage != null) {
                throw new IllegalArgumentException(
                        errorMessage);
            }

            return 1;
        }
    }
}