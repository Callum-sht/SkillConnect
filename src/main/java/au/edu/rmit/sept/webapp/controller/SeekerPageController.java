package au.edu.rmit.sept.webapp.controller;

import au.edu.rmit.sept.webapp.dto.ProviderProfileForSeekerResponse;
import au.edu.rmit.sept.webapp.service.ProviderProfileForSeekerService;
import au.edu.rmit.sept.webapp.dto.SeekerProfileResponse;
import au.edu.rmit.sept.webapp.dto.ServiceListingResponse;
import au.edu.rmit.sept.webapp.dto.UpdateSeekerProfileRequest;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.service.SeekerService;
import au.edu.rmit.sept.webapp.service.ServiceListingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class SeekerPageController {

    private final SeekerService seekerService;
    private final ServiceListingService serviceListingService;
    private final ProviderProfileForSeekerService providerProfileForSeekerService;

    public SeekerPageController(
            SeekerService seekerService,
            ServiceListingService serviceListingService,
            ProviderProfileForSeekerService providerProfileForSeekerService) {
        this.seekerService = seekerService;
        this.serviceListingService = serviceListingService;
        this.providerProfileForSeekerService = providerProfileForSeekerService;
    }

    @GetMapping("/seekers/{userId}/profile")
    public ResponseEntity<SeekerProfileResponse> getProfile(
            @PathVariable Long userId,
            HttpServletRequest request) {
        requireSeekerSession(userId, request);
        return ResponseEntity.ok(seekerService.getProfile(userId));
    }

    @PatchMapping("/seekers/{userId}/profile")
    public ResponseEntity<SeekerProfileResponse> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateSeekerProfileRequest profileRequest,
            HttpServletRequest request) {
        requireSeekerSession(userId, request);
        return ResponseEntity.ok(seekerService.updateProfile(userId, profileRequest));
    }

    @GetMapping("/services")
    public ResponseEntity<List<ServiceListingResponse>> getAvailableServices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return ResponseEntity.ok(serviceListingService.getAvailableServices(
                keyword, category, location, maxPrice));
    }

    @GetMapping("/services/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(serviceListingService.getCategories());
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<ServiceListingResponse> getServiceDetail(@PathVariable Long id) {
        ServiceListingResponse service = serviceListingService.getServiceDetail(id);

        if (service == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(service);
    }

    @GetMapping("/providers/{providerUserId}/public-profile")
    public ResponseEntity<ProviderProfileForSeekerResponse> getProviderProfile(
            @PathVariable Long providerUserId) {
        ProviderProfileForSeekerResponse profile = providerProfileForSeekerService
                .getProviderProfile(providerUserId);

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(profile);
    }

    private void requireSeekerSession(Long userId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (!userId.equals(session.getAttribute("userId"))
                || session.getAttribute("role") != UserRole.SEEKER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}