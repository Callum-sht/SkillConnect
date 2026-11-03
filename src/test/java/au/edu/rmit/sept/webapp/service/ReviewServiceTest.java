package au.edu.rmit.sept.webapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import au.edu.rmit.sept.webapp.model.Booking;
import au.edu.rmit.sept.webapp.model.BookingStatus;
import au.edu.rmit.sept.webapp.model.Review;
import au.edu.rmit.sept.webapp.model.SeekerAccount;
import au.edu.rmit.sept.webapp.model.ServiceListing;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.repository.BookingRepository;
import au.edu.rmit.sept.webapp.repository.ReviewRepository;
import au.edu.rmit.sept.webapp.repository.ServiceListingRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReviewServiceTest {

    private FakeBookingRepository bookingRepository;
    private FakeReviewRepository reviewRepository;
    private FakeServiceListingRepository serviceListingRepository;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        bookingRepository = new FakeBookingRepository();
        reviewRepository = new FakeReviewRepository();
        serviceListingRepository = new FakeServiceListingRepository();
        reviewService = new ReviewService(
                bookingRepository, reviewRepository, serviceListingRepository);
    }

    @Test
    void completedBookingCanBeReviewed() {
        bookingRepository.booking = createBooking(BookingStatus.COMPLETED, 7L);

        String result = reviewService.submitReview(7L, 1L, 5, "Great!");

        assertEquals("Review submitted successfully.", result);
        assertEquals(1, reviewRepository.saveCount);
        assertEquals(1, serviceListingRepository.saveCount);
    }

    @Test
    void nonCompletedBookingCannotBeReviewed() {
        bookingRepository.booking = createBooking(BookingStatus.ACCEPTED, 7L);

        String result = reviewService.submitReview(7L, 1L, 5, "Too early");

        assertEquals("Only completed bookings can be reviewed.", result);
        assertEquals(0, reviewRepository.saveCount);
    }

    @Test
    void alreadyReviewedBookingCannotBeReviewedAgain() {
        bookingRepository.booking = createBooking(BookingStatus.COMPLETED, 7L);
        reviewRepository.alreadyReviewed = true;

        String result = reviewService.submitReview(7L, 1L, 5, "Again");

        assertEquals("This booking has already been reviewed.", result);
        assertEquals(0, reviewRepository.saveCount);
    }

    @Test
    void ratingOutOfRangeIsRejected() {
        bookingRepository.booking = createBooking(BookingStatus.COMPLETED, 7L);

        String tooHigh = reviewService.submitReview(7L, 1L, 10, "Too high");
        String tooLow = reviewService.submitReview(7L, 1L, 0, "Too low");

        assertEquals("Rating must be between 1 and 5.", tooHigh);
        assertEquals("Rating must be between 1 and 5.", tooLow);
        assertEquals(0, reviewRepository.saveCount);
    }

    @Test
    void differentSeekerCannotReviewBooking() {
        bookingRepository.booking = createBooking(BookingStatus.COMPLETED, 7L);

        String result = reviewService.submitReview(99L, 1L, 5, "Not mine");

        assertEquals("You can only review your own bookings.", result);
        assertEquals(0, reviewRepository.saveCount);
    }

    @Test
    void missingBookingCannotBeReviewed() {
        bookingRepository.booking = null;

        String result = reviewService.submitReview(7L, 1L, 5, "Missing");

        assertEquals("Booking not found.", result);
    }

    private Booking createBooking(BookingStatus status, Long seekerUserId) {
        User seekerUser = new User();
        seekerUser.setUserId(seekerUserId);

        SeekerAccount seeker = new SeekerAccount();
        seeker.setUser(seekerUser);

        ServiceListing service = new ServiceListing();
        service.setServiceId(3L);

        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setSeeker(seeker);
        booking.setStatus(status);
        booking.setService(service);

        return booking;
    }

    private static class FakeBookingRepository extends BookingRepository {

        private Booking booking;

        FakeBookingRepository() {
            super(null, null, null);
        }

        @Override
        public Optional<Booking> findById(Long bookingId) {
            return Optional.ofNullable(booking);
        }
    }

    private static class FakeReviewRepository extends ReviewRepository {

        private boolean alreadyReviewed;
        private int saveCount;

        FakeReviewRepository() {
            super(null, null);
        }

        @Override
        public boolean existsByBookingId(Long bookingId) {
            return alreadyReviewed;
        }

        @Override
        public Review save(Review review) {
            saveCount++;
            return review;
        }

        @Override
        public BigDecimal findAverageRatingByServiceId(Long serviceId) {
            return new BigDecimal("5.0");
        }
    }

    private static class FakeServiceListingRepository extends ServiceListingRepository {

        private int saveCount;

        FakeServiceListingRepository() {
            super(null, null);
        }

        @Override
        public ServiceListing save(ServiceListing serviceListing) {
            saveCount++;
            return serviceListing;
        }
    }
}