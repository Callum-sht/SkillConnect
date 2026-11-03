package au.edu.rmit.sept.webapp.controller;

import au.edu.rmit.sept.webapp.dto.AdminUserResponse;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminUserControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AdminUserController(new StubUserService()))
                .build();
    }

    @Test
    void getAllUsersReturnsUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users").session(adminSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice Seeker"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void updateUserStatusReturnsUpdatedUser() throws Exception {
        mockMvc.perform(
                        patch("/api/admin/users/1/status")
                                .session(adminSession())
                                .contentType("application/json")
                                .content("""
                                        {
                                          "status": "DEACTIVATED"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.status").value("DEACTIVATED"));
    }

    private MockHttpSession adminSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);
        session.setAttribute("role", UserRole.ADMIN);
        return session;
    }

    private static class StubUserService extends UserService {

        StubUserService() {
            super(null);
        }

        @Override
        public List<AdminUserResponse> getAllUsers() {
            return List.of(createResponse(1L, "ACTIVE"));
        }

        @Override
        public AdminUserResponse updateUserStatus(Long userId, String status) {
            return createResponse(userId, status);
        }

        private AdminUserResponse createResponse(Long userId, String status) {
            return new AdminUserResponse(
                    userId,
                    "Alice Seeker",
                    "alice@example.com",
                    "SEEKER",
                    status,
                    LocalDateTime.of(2026, 9, 15, 16, 0)
            );
        }
    }
}
