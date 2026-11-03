package au.edu.rmit.sept.webapp.controller;

import au.edu.rmit.sept.webapp.dto.AdminServiceListingResponse;
import au.edu.rmit.sept.webapp.dto.UpdateServiceStatusRequest;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.service.AdminServiceListingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/services")
public class AdminServiceListingController {

    private final AdminServiceListingService adminServiceListingService;

    public AdminServiceListingController(
            AdminServiceListingService adminServiceListingService) {
        this.adminServiceListingService = adminServiceListingService;
    }

    @GetMapping
    public ResponseEntity<List<AdminServiceListingResponse>> getAllServices(
            HttpServletRequest request) {
        requireAdminSession(request);
        return ResponseEntity.ok(adminServiceListingService.getAllServices());
    }

    @PatchMapping("/{serviceId}/status")
    public String updateServiceStatus(
            @PathVariable Long serviceId,
            @RequestBody UpdateServiceStatusRequest statusRequest,
            HttpServletRequest request) {
        requireAdminSession(request);
        return adminServiceListingService.updateServiceStatus(
                serviceId, statusRequest.getStatus());
    }

    private void requireAdminSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (session.getAttribute("role") != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}