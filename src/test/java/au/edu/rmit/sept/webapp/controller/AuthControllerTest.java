package au.edu.rmit.sept.webapp.controller;

import au.edu.rmit.sept.webapp.exception.AuthExceptionHandler;
import au.edu.rmit.sept.webapp.exception.InactiveUserException;
import au.edu.rmit.sept.webapp.exception.InvalidCredentialsException;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthControllerTest {

    private MockMvc createMockMvc(AuthService authService) {
        AuthController controller = new AuthController(authService);

        return MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new AuthExceptionHandler())
                .build();
    }

    @Test
    void loginReturnsRoleForActiveUser() throws Exception {
        AuthService authService =
                new StubAuthService(LoginOutcome.SUCCESS);

        MockMvc mockMvc = createMockMvc(authService);

        MvcResult result = mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "email": "alice@example.com",
                                          "password": "secret-password"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(7))
                .andExpect(jsonPath("$.role").value("SEEKER"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        assertNotNull(session);
        assertEquals(7L, session.getAttribute("userId"));
        assertEquals(UserRole.SEEKER, session.getAttribute("role"));
    }

    @Test
    void logoutInvalidatesSession() throws Exception {
        MockMvc mockMvc = createMockMvc(new StubAuthService(LoginOutcome.SUCCESS));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 7L);
        session.setAttribute("role", UserRole.SEEKER);

        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isNoContent());

        assertTrue(session.isInvalid());
    }

    @Test
    void logoutWithoutSessionSucceeds() throws Exception {
        MockMvc mockMvc = createMockMvc(new StubAuthService(LoginOutcome.SUCCESS));

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    void loginReturnsUnauthorizedForInvalidCredentials() throws Exception {
        AuthService authService =
                new StubAuthService(LoginOutcome.INVALID_CREDENTIALS);

        MockMvc mockMvc = createMockMvc(authService);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "email": "alice@example.com",
                                          "password": "wrong-password"
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginReturnsForbiddenForInactiveUser() throws Exception {
        AuthService authService =
                new StubAuthService(LoginOutcome.INACTIVE_USER);

        MockMvc mockMvc = createMockMvc(authService);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "email": "alice@example.com",
                                          "password": "secret-password"
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());
    }

    private enum LoginOutcome {
        SUCCESS,
        INVALID_CREDENTIALS,
        INACTIVE_USER
    }

    private static class StubAuthService extends AuthService {

        private final LoginOutcome outcome;

        StubAuthService(LoginOutcome outcome) {
            super(null, null, null);
            this.outcome = outcome;
        }

        @Override
        public User login(String email, String password) {
            if (outcome == LoginOutcome.INVALID_CREDENTIALS) {
                throw new InvalidCredentialsException();
            }

            if (outcome == LoginOutcome.INACTIVE_USER) {
                throw new InactiveUserException();
            }

            User user = new User();
            user.setUserId(7L);
            user.setRole(UserRole.SEEKER);
            return user;
        }
    }
}
