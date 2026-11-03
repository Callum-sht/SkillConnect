package au.edu.rmit.sept.webapp.controller;

import au.edu.rmit.sept.webapp.dto.BookingRequest;
import au.edu.rmit.sept.webapp.dto.BookingResponse;
import au.edu.rmit.sept.webapp.dto.ReviewRequest;
import au.edu.rmit.sept.webapp.dto.SeekerProfileForProviderResponse;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.service.BookingService;
import au.edu.rmit.sept.webapp.service.ReviewService;
import au.edu.rmit.sept.webapp.service.SeekerProfileForProviderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class BookingController {

    private final BookingService bookingService;
    private final SeekerProfileForProviderService seekerProfileForProviderService;
    private final ReviewService reviewService;

    public BookingController(
            BookingService bookingService,
            SeekerProfileForProviderService seekerProfileForProviderService,
            ReviewService reviewService) {
        this.bookingService = bookingService;
        this.seekerProfileForProviderService = seekerProfileForProviderService;
        this.reviewService = reviewService;
    }

    @PostMapping("/seekers/{userId}/bookings")
    public String requestBooking(
            @PathVariable Long userId,
            @RequestBody BookingRequest request) {
        return bookingService.requestBooking(
                userId,
                request.getServiceId(),
                request.getBookingDate(),
                request.getBookingTime(),
                request.getBookingEndDate(),
                request.getBookingEndTime());
    }

    @PatchMapping("/providers/{providerId}/bookings/{bookingId}/decline")
    public String declineBooking(
            @PathVariable Long providerId,
            @PathVariable Long bookingId,
            @RequestBody(required = false) Map<String, String> body,
            HttpServletRequest request) {
        String declineReason = null;

        if (body != null) {
            declineReason = body.get("declineReason");
        }

        requireProviderSession(providerId, request);
        return bookingService.declineBooking(bookingId, providerId, declineReason);
    }

    @PatchMapping("/providers/{providerId}/bookings/{bookingId}/accept")
    public String acceptBooking(
            @PathVariable Long providerId,
            @PathVariable Long bookingId,
            HttpServletRequest request) {
        requireProviderSession(providerId, request);
        return bookingService.acceptBooking(bookingId, providerId);
    }

    @PatchMapping("/providers/{providerId}/bookings/{bookingId}/complete")
    public String completeBooking(
            @PathVariable Long providerId,
            @PathVariable Long bookingId) {
        return bookingService.completeBooking(bookingId, providerId);
    }

    @PostMapping("/seekers/{seekerUserId}/bookings/{bookingId}/review")
    public String submitReview(
            @PathVariable Long seekerUserId,
            @PathVariable Long bookingId,
            @RequestBody ReviewRequest reviewRequest,
            HttpServletRequest httpRequest) {
        requireSeekerSession(seekerUserId, httpRequest);
        return reviewService.submitReview(
                seekerUserId,
                bookingId,
                reviewRequest.getRating(),
                reviewRequest.getComment());
    }

    @GetMapping("/seekers/{userId}/bookings")
    public ResponseEntity<List<BookingResponse>> getSeekerBookingHistory(
            @PathVariable Long userId,
            HttpServletRequest request) {
        requireSeekerSession(userId, request);
        return ResponseEntity.ok(bookingService.getSeekerBookingHistory(userId));
    }

    @GetMapping("/providers/{userId}/bookings")
    public ResponseEntity<List<BookingResponse>> getProviderBookingHistory(
            @PathVariable Long userId,
            HttpServletRequest request) {
        requireProviderSession(userId, request);
        return ResponseEntity.ok(bookingService.getProviderBookingHistory(userId));
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

    @GetMapping("/providers/{providerUserId}/seekers/{seekerUserId}/profile")
    public ResponseEntity<SeekerProfileForProviderResponse> getSeekerProfile(
            @PathVariable Long providerUserId,
            @PathVariable Long seekerUserId,
            HttpServletRequest request) {
        requireProviderSession(providerUserId, request);

        SeekerProfileForProviderResponse profile = seekerProfileForProviderService
                .getSeekerProfile(providerUserId, seekerUserId);

        if (profile == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(profile);
    }
}
