package au.edu.rmit.sept.webapp.controller;

import au.edu.rmit.sept.webapp.dto.ServiceReviewResponse;
import au.edu.rmit.sept.webapp.service.ReviewService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{serviceId}/reviews")
    public List<ServiceReviewResponse> getServiceReviews(
            @PathVariable Long serviceId) {
        return reviewService.getReviewsForService(serviceId);
    }
}