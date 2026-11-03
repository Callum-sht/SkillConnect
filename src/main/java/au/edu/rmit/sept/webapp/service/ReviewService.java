package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.model.Booking;
import au.edu.rmit.sept.webapp.model.BookingStatus;
import au.edu.rmit.sept.webapp.model.Review;
import au.edu.rmit.sept.webapp.model.ServiceListing;
import au.edu.rmit.sept.webapp.repository.BookingRepository;
import au.edu.rmit.sept.webapp.repository.ReviewRepository;
import au.edu.rmit.sept.webapp.repository.ServiceListingRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final ServiceListingRepository serviceListingRepository;

    public ReviewService(
            BookingRepository bookingRepository,
            ReviewRepository reviewRepository,
            ServiceListingRepository serviceListingRepository) {
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
        this.serviceListingRepository = serviceListingRepository;
    }

    public String submitReview(
            Long seekerUserId,
            Long bookingId,
            Integer rating,
            String comment) {

        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null) {
            return "Booking not found.";
        }

        Long bookingSeekerUserId = booking.getSeeker().getUser().getUserId();
        if (!bookingSeekerUserId.equals(seekerUserId)) {
            return "You can only review your own bookings.";
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            return "Only completed bookings can be reviewed.";
        }

        if (reviewRepository.existsByBookingId(bookingId)) {
            return "This booking has already been reviewed.";
        }

        if (rating == null || rating < 1 || rating > 5) {
            return "Rating must be between 1 and 5.";
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setRating(rating);
        review.setComment(comment);

        reviewRepository.save(review);

        updateServiceAverageRating(booking.getService());

        return "Review submitted successfully.";
    }

    private void updateServiceAverageRating(ServiceListing service) {
        BigDecimal averageRating = reviewRepository
                .findAverageRatingByServiceId(service.getServiceId());

        if (averageRating != null) {
            service.setRating(averageRating.setScale(1, RoundingMode.HALF_UP));
            serviceListingRepository.save(service);
        }
    }

    public java.util.List<au.edu.rmit.sept.webapp.dto.ServiceReviewResponse> getReviewsForService(
            Long serviceId) {
        return reviewRepository.findByServiceId(serviceId)
                .stream()
                .map(review -> new au.edu.rmit.sept.webapp.dto.ServiceReviewResponse(
                        review.getBooking().getSeeker().getUser().getName(),
                        review.getRating(),
                        review.getComment(),
                        review.getCreatedAt()))
                .toList();
    }
}