package au.edu.rmit.sept.webapp.controller;

import au.edu.rmit.sept.webapp.model.Conversation;
import au.edu.rmit.sept.webapp.service.ConversationService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import au.edu.rmit.sept.webapp.dto.ConversationSummaryResponse;
import java.time.LocalDateTime;
import java.util.List;

class ConversationControllerTest {

    private MockMvc createMockMvc(
            ConversationService conversationService) {

        ConversationController controller =
                new ConversationController(conversationService);

        return MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void loggedInSeekerCanStartConversation() throws Exception {
        StubConversationService service =
                new StubConversationService();

        MockMvc mockMvc = createMockMvc(service);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(
                        post("/api/conversations")
                                .session(session)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "providerId": 2
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value(10))
                .andExpect(jsonPath("$.seekerId").value(1))
                .andExpect(jsonPath("$.providerId").value(2))
                .andExpect(jsonPath("$.otherParticipantName").value("Alice Provider"));

        assertEquals(1L, service.lastSeekerId);
        assertEquals(2L, service.lastProviderId);
    }

    @Test
    void unauthenticatedUserCannotStartConversation()
            throws Exception {

        StubConversationService service =
                new StubConversationService();

        MockMvc mockMvc = createMockMvc(service);

        mockMvc.perform(
                        post("/api/conversations")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "providerId": 2
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(
                        "User must be logged in"));
    }

    @Test
    void providerIdIsRequired() throws Exception {
        StubConversationService service =
                new StubConversationService();

        MockMvc mockMvc = createMockMvc(service);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(
                        post("/api/conversations")
                                .session(session)
                                .contentType("application/json")
                                .content("""
                                        {}
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "Provider ID is required"));
    }

    @Test
    void invalidConversationRequestReturnsBadRequest()
            throws Exception {

        StubConversationService service =
                new StubConversationService();
        service.errorMessage = "Provider not found";

        MockMvc mockMvc = createMockMvc(service);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(
                        post("/api/conversations")
                                .session(session)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "providerId": 999
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "Provider not found"));
    }

    @Test
    void loggedInParticipantCanGetConversation()
            throws Exception {

        StubConversationService service =
                new StubConversationService();

        MockMvc mockMvc = createMockMvc(service);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(
                        get("/api/conversations/10")
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value(10))
                .andExpect(jsonPath("$.seekerId").value(1))
                .andExpect(jsonPath("$.providerId").value(2))
                .andExpect(jsonPath("$.otherParticipantName")
                        .value("Alice Provider"));

        assertEquals(10L, service.lastConversationId);
        assertEquals(1L, service.lastUserId);
    }

    @Test
    void unauthenticatedUserCannotGetConversation()
            throws Exception {

        StubConversationService service =
                new StubConversationService();

        MockMvc mockMvc = createMockMvc(service);

        mockMvc.perform(
                        get("/api/conversations/10")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(
                        "User must be logged in"));
    }

    @Test
    void inaccessibleConversationReturnsBadRequest()
            throws Exception {

        StubConversationService service =
                new StubConversationService();

        service.getConversationErrorMessage =
                "You do not have access to this conversation";

        MockMvc mockMvc = createMockMvc(service);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 99L);

        mockMvc.perform(
                        get("/api/conversations/10")
                                .session(session)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "You do not have access to this conversation"));
    }

    @Test
    void loggedInUserCanGetConversationList()
            throws Exception {

        StubConversationService service =
                new StubConversationService();

        MockMvc mockMvc = createMockMvc(service);

        MockHttpSession session =
                new MockHttpSession();

        session.setAttribute("userId", 1L);

        mockMvc.perform(
                        get("/api/conversations")
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].conversationId")
                                .value(10))
                .andExpect(
                        jsonPath("$[0].otherParticipantName")
                                .value("Alice Provider"))
                .andExpect(
                        jsonPath("$[0].latestMessage")
                                .value(
                                        "Is tomorrow available?"))
                .andExpect(
                        jsonPath("$[0].latestMessageTime")
                                .exists())

                .andExpect(
                        jsonPath("$[0].unreadCount")
                                .value(2));

        assertEquals(
                1L,
                service.lastUserId);
    }

    @Test
    void unauthenticatedUserCannotGetConversationList()
            throws Exception {

        StubConversationService service =
                new StubConversationService();

        MockMvc mockMvc = createMockMvc(service);

        mockMvc.perform(
                        get("/api/conversations")
                )
                .andExpect(
                        status().isUnauthorized())
                .andExpect(
                        content().string(
                                "User must be logged in"));
    }

    private static class StubConversationService
            extends ConversationService {

        @Override
        public String getOtherParticipantName(
                Conversation conversation,
                Long currentUserId) {

            if (currentUserId.equals(
                    conversation.getSeekerId())) {
                return "Alice Provider";
            }

            return "Yihan Seeker";
        }

        private Long lastSeekerId;
        private Long lastProviderId;
        private String errorMessage;
        private Long lastConversationId;
        private Long lastUserId;
        private String getConversationErrorMessage;

        StubConversationService() {
            super(null, null, null);
        }

        @Override
        public Conversation startConversation(
                Long seekerId,
                Long providerId) {

            lastSeekerId = seekerId;
            lastProviderId = providerId;

            if (errorMessage != null) {
                throw new IllegalArgumentException(errorMessage);
            }

            Conversation conversation = new Conversation();
            conversation.setConversationId(10L);
            conversation.setSeekerId(seekerId);
            conversation.setProviderId(providerId);

            return conversation;
        }

        @Override
        public Conversation getConversation(
                Long conversationId,
                Long userId) {

            lastConversationId = conversationId;
            lastUserId = userId;

            if (getConversationErrorMessage != null) {
                throw new IllegalArgumentException(
                        getConversationErrorMessage);
            }

            Conversation conversation = new Conversation();
            conversation.setConversationId(conversationId);
            conversation.setSeekerId(1L);
            conversation.setProviderId(2L);

            return conversation;
        }

        @Override
        public List<ConversationSummaryResponse>
                getConversationsForUser(Long userId) {

            lastUserId = userId;

            return List.of(
                    new ConversationSummaryResponse(
                            10L,
                            "Alice Provider",
                            "Is tomorrow available?",
                            LocalDateTime.of(
                                    2026, 10, 9, 11, 30),
                                2));
        }
    }
}