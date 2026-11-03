package au.edu.rmit.sept.webapp.controller;

import au.edu.rmit.sept.webapp.dto.SeekerProfileResponse;
import au.edu.rmit.sept.webapp.dto.UpdateSeekerProfileRequest;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.service.SeekerService;
import au.edu.rmit.sept.webapp.service.ServiceListingService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SeekerPageControllerTest {

    private final StubSeekerService seekerService = new StubSeekerService();
    private final ServiceListingService listingService = new ServiceListingService(null);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new SeekerPageController(seekerService, listingService, null))
            .build();

    @Test
    void profileRequiresLogin() throws Exception {
        mockMvc.perform(get("/api/seekers/7/profile"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(patch("/api/seekers/7/profile")
                        .contentType("application/json")
                        .content("{\"name\":\"New name\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void profileRejectsAnotherUser() throws Exception {
        mockMvc.perform(get("/api/seekers/8/profile").session(seekerSession()))
                .andExpect(status().isForbidden());
    }

    @Test
    void profileRejectsProvider() throws Exception {
        MockHttpSession session = seekerSession();
        session.setAttribute("role", UserRole.PROVIDER);

        mockMvc.perform(get("/api/seekers/7/profile").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void seekerCanReadOwnProfile() throws Exception {
        mockMvc.perform(get("/api/seekers/7/profile").session(seekerSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alex Morgan"));

        assertEquals(7L, seekerService.lastReadId);
    }

    @Test
    void seekerCanUpdateOwnProfile() throws Exception {
        mockMvc.perform(patch("/api/seekers/7/profile")
                        .session(seekerSession())
                        .contentType("application/json")
                        .content("{\"name\":\"Alex Morgan\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(7));

        assertEquals(7L, seekerService.lastUpdatedId);
    }

    private MockHttpSession seekerSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 7L);
        session.setAttribute("role", UserRole.SEEKER);
        return session;
    }

    private SeekerProfileResponse profile() {
        return new SeekerProfileResponse(
                7L, 3L, "Alex Morgan", "alex@example.com", "SEEKER", "ACTIVE",
                "Hello", LocalDateTime.of(2026, 9, 19, 10, 0));
    }

    private class StubSeekerService extends SeekerService {
        private Long lastReadId;
        private Long lastUpdatedId;

        StubSeekerService() {
            super(null, null);
        }

        @Override
        public SeekerProfileResponse getProfile(Long userId) {
            lastReadId = userId;
            return profile();
        }

        @Override
        public SeekerProfileResponse updateProfile(
                Long userId, UpdateSeekerProfileRequest request) {
            lastUpdatedId = userId;
            return profile();
        }
    }
}
