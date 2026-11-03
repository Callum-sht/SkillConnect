package au.edu.rmit.sept.webapp.controller;
 
import au.edu.rmit.sept.webapp.dto.AddServiceRequest;
import au.edu.rmit.sept.webapp.dto.ProviderProfileResponse;
import au.edu.rmit.sept.webapp.dto.ServiceListingResponse;
import au.edu.rmit.sept.webapp.dto.UpdateProviderProfileRequest;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.service.ProviderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
 
@RestController
@RequestMapping("/api")
public class ProviderController {
 
    private final ProviderService providerService;
 
    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }
 
    @GetMapping("/providers/{userId}/profile")
    public ResponseEntity<ProviderProfileResponse> getProfile(
            @PathVariable Long userId,
            HttpServletRequest request) {
        requireProviderSession(userId, request);
        return ResponseEntity.ok(providerService.getProfile(userId));
    }
 
    @PatchMapping("/providers/{userId}/profile")
    public ResponseEntity<ProviderProfileResponse> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProviderProfileRequest profileRequest,
            HttpServletRequest request) {
        requireProviderSession(userId, request);
        return ResponseEntity.ok(
                providerService.updateProfile(userId, profileRequest));
    }
 
    @GetMapping("/providers/{userId}/services")
    public ResponseEntity<List<ServiceListingResponse>> getServices(
            @PathVariable Long userId,
            HttpServletRequest request) {
        requireProviderSession(userId, request);
        return ResponseEntity.ok(providerService.getServices(userId));
    }
 
    @PostMapping("/providers/{userId}/services")
    public ResponseEntity<ServiceListingResponse> addService(
            @PathVariable Long userId,
            @RequestBody AddServiceRequest serviceRequest,
            HttpServletRequest request) {
        requireProviderSession(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(providerService.addService(userId, serviceRequest));
    }
 
    @DeleteMapping("/providers/{userId}/services/{serviceId}")
    public ResponseEntity<Void> deleteService(
            @PathVariable Long userId,
            @PathVariable Long serviceId,
            HttpServletRequest request) {
        requireProviderSession(userId, request);
        providerService.deleteService(userId, serviceId);
        return ResponseEntity.noContent().build();
    }
 
    private void requireProviderSession(Long userId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
 
        if (!userId.equals(session.getAttribute("userId"))
                || session.getAttribute("role") != UserRole.PROVIDER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}