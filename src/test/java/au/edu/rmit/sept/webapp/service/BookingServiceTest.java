package au.edu.rmit.sept.webapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import au.edu.rmit.sept.webapp.model.Booking;
import au.edu.rmit.sept.webapp.model.BookingStatus;
import au.edu.rmit.sept.webapp.model.ProviderAccount;
import au.edu.rmit.sept.webapp.model.SeekerAccount;
import au.edu.rmit.sept.webapp.model.ServiceListing;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.repository.BookingRepository;
import au.edu.rmit.sept.webapp.repository.SeekerAccountRepository;
import au.edu.rmit.sept.webapp.repository.ServiceListingRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookingServiceTest {

    private FakeBookingRepository bookingRepository;
    private FakeServiceListingRepository serviceListingRepository;
    private FakeSeekerAccountRepository seekerAccountRepository;
    private BookingService bookingService;
    

    @BeforeEach
    void setUp() {
        bookingRepository = new FakeBookingRepository();
        serviceListingRepository = new FakeServiceListingRepository();
        seekerAccountRepository = new FakeSeekerAccountRepository();
        bookingService = new BookingService(
                bookingRepository,
                serviceListingRepository,
                seekerAccountRepository);
    }

    @Test
    void validStartAndEndTimeCreatesPendingBooking() {
        prepareBookingRequestData();

        String result = bookingService.requestBooking(
                1L,
                10L,
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0));

        assertEquals("Booking request submitted successfully.", result);
        assertEquals(1, bookingRepository.saveCount);
        assertEquals(BookingStatus.PENDING, bookingRepository.booking.getStatus());
        assertEquals(
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(10, 0)),
                bookingRepository.booking.getBookingEndTime());
    }

    @Test
    void missingEndDateCannotCreateBooking() {
        prepareBookingRequestData();

        String result = bookingService.requestBooking(
                1L,
                10L,
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                null,
                LocalTime.of(10, 0));

        assertEquals("Booking end date is required.", result);
        assertEquals(0, bookingRepository.saveCount);
    }

    @Test
    void missingEndTimeCannotCreateBooking() {
        prepareBookingRequestData();

        String result = bookingService.requestBooking(
                1L,
                10L,
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalDate.now().plusDays(1),
                null);

        assertEquals("Booking end time is required.", result);
        assertEquals(0, bookingRepository.saveCount);
    }

    @Test
    void endTimeEqualToStartTimeCannotCreateBooking() {
        prepareBookingRequestData();

        String result = bookingService.requestBooking(
                1L,
                10L,
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0));

        assertEquals(
                "Booking end date and time must be after the start date and time.",
                result);
        assertEquals(0, bookingRepository.saveCount);
    }

    @Test
    void endTimeBeforeStartTimeCannotCreateBooking() {
        prepareBookingRequestData();

        String result = bookingService.requestBooking(
                1L,
                10L,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0));

        assertEquals(
                "Booking end date and time must be after the start date and time.",
                result);
        assertEquals(0, bookingRepository.saveCount);
    }

    @Test
    void crossDayBookingCanCreateBookingWhenEndIsAfterStart() {
        prepareBookingRequestData();

        String result = bookingService.requestBooking(
                1L,
                10L,
                LocalDate.now().plusDays(1),
                LocalTime.of(23, 0),
                LocalDate.now().plusDays(2),
                LocalTime.of(1, 0));

        assertEquals("Booking request submitted successfully.", result);
        assertEquals(1, bookingRepository.saveCount);
    }

    @Test
    void pendingBookingCanBeDeclinedWithReason() {
        Booking booking = createBooking(BookingStatus.PENDING, 1L);
        bookingRepository.booking = booking;

        String result = bookingService.declineBooking(1L, 1L, "Unavailable at that time");

        assertEquals("Booking declined successfully.", result);
        assertEquals(BookingStatus.DECLINED, booking.getStatus());
        assertEquals("Unavailable at that time", booking.getDeclineReason());
        assertEquals(1, bookingRepository.saveCount);
    }

    @Test
    void nonPendingBookingCannotBeDeclined() {
        Booking booking = createBooking(BookingStatus.ACCEPTED, 1L);
        bookingRepository.booking = booking;

        String result = bookingService.declineBooking(1L, 1L, "Already accepted");

        assertEquals("Only pending bookings can be declined.", result);
        assertEquals(0, bookingRepository.saveCount);
    }

    @Test
    void differentProviderCannotDeclineBooking() {
        Booking booking = createBooking(BookingStatus.PENDING, 1L);
        bookingRepository.booking = booking;

        String result = bookingService.declineBooking(1L, 2L, "Wrong provider");

        assertEquals("Provider does not own this booking.", result);
        assertEquals(0, bookingRepository.saveCount);
    }

    @Test
    void missingBookingCannotBeDeclined() {
        bookingRepository.booking = null;

        String result = bookingService.declineBooking(1L, 1L, "Missing booking");

        assertEquals("Booking not found.", result);
    }

    @Test
    void seekerBookingHistoryReturnsBookingsForThatSeeker() {
        Booking booking = createBookingWithSeekerAndProviderNames("Alice", "Bob");
        bookingRepository.seekerBookings = java.util.List.of(booking);

        var result = bookingService.getSeekerBookingHistory(5L);

        assertEquals(1, result.size());
        assertEquals("Bob", result.get(0).getOtherPartyName());
        assertEquals(5L, bookingRepository.lastSeekerUserIdQueried);
    }

    @Test
    void seekerBookingHistoryReturnsEmptyListWhenNoBookings() {
        bookingRepository.seekerBookings = java.util.List.of();

        var result = bookingService.getSeekerBookingHistory(5L);

        assertEquals(0, result.size());
    }

    @Test
    void providerBookingHistoryReturnsBookingsForThatProvider() {
        Booking booking = createBookingWithSeekerAndProviderNames("Alice", "Bob");
        bookingRepository.providerBookings = java.util.List.of(booking);

        var result = bookingService.getProviderBookingHistory(9L);

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getOtherPartyName());
        assertEquals(9L, bookingRepository.lastProviderUserIdQueried);
    }

    @Test
    void acceptedBookingCanBeMarkedAsCompleted() {
        Booking booking = createBooking(BookingStatus.ACCEPTED, 1L);
        bookingRepository.booking = booking;

        String result = bookingService.completeBooking(1L, 1L);

        assertEquals("Booking marked as completed successfully.", result);
        assertEquals(BookingStatus.COMPLETED, booking.getStatus());
        assertEquals(1, bookingRepository.saveCount);
    }

    @Test
    void pendingBookingCannotBeMarkedAsCompleted() {
        Booking booking = createBooking(BookingStatus.PENDING, 1L);
        bookingRepository.booking = booking;

        String result = bookingService.completeBooking(1L, 1L);

        assertEquals("Only accepted bookings can be marked as completed.", result);
        assertEquals(0, bookingRepository.saveCount);
    }

    @Test
    void differentProviderCannotCompleteBooking() {
        Booking booking = createBooking(BookingStatus.ACCEPTED, 1L);
        bookingRepository.booking = booking;

        String result = bookingService.completeBooking(1L, 2L);

        assertEquals("Provider does not own this booking.", result);
        assertEquals(0, bookingRepository.saveCount);
    }

    @Test
    void missingBookingCannotBeMarkedAsCompleted() {
        bookingRepository.booking = null;

        String result = bookingService.completeBooking(1L, 1L);

        assertEquals("Booking not found.", result);
    }

    private Booking createBooking(BookingStatus status, Long providerId) {
        User providerUser = new User();
        providerUser.setUserId(providerId);

        ProviderAccount provider = new ProviderAccount();
        provider.setProviderAccountId(providerId);
        provider.setUser(providerUser);

        ServiceListing service = new ServiceListing();
        service.setProvider(provider);

        Booking booking = new Booking();
        booking.setService(service);
        booking.setStatus(status);

        return booking;
    }

    private Booking createBookingWithSeekerAndProviderNames(String seekerName, String providerName) {
        au.edu.rmit.sept.webapp.model.User seekerUser = new au.edu.rmit.sept.webapp.model.User();
        seekerUser.setName(seekerName);
        au.edu.rmit.sept.webapp.model.SeekerAccount seeker =
                new au.edu.rmit.sept.webapp.model.SeekerAccount();
        seeker.setUser(seekerUser);

        au.edu.rmit.sept.webapp.model.User providerUser = new au.edu.rmit.sept.webapp.model.User();
        providerUser.setName(providerName);
        ProviderAccount provider = new ProviderAccount();
        provider.setUser(providerUser);

        ServiceListing service = new ServiceListing();
        service.setTitle("Test Service");
        service.setProvider(provider);

        Booking booking = new Booking();
        booking.setService(service);
        booking.setSeeker(seeker);
        booking.setStatus(BookingStatus.PENDING);

        return booking;
    }

    private void prepareBookingRequestData() {
        User seekerUser = new User();
        seekerUser.setUserId(1L);

        SeekerAccount seeker = new SeekerAccount();
        seeker.setSeekerAccountId(2L);
        seeker.setUser(seekerUser);
        seekerAccountRepository.seeker = seeker;

        ServiceListing service = createServiceForProvider(10L, 3L);
        service.setHourlyRate(new BigDecimal("60.00"));
        serviceListingRepository.service = service;
    }

    private ServiceListing createServiceForProvider(Long serviceId, Long providerId) {
        User providerUser = new User();
        providerUser.setUserId(providerId);

        ProviderAccount provider = new ProviderAccount();
        provider.setProviderAccountId(providerId);
        provider.setUser(providerUser);

        ServiceListing service = new ServiceListing();
        service.setServiceId(serviceId);
        service.setProvider(provider);

        return service;
    }

    private static class FakeBookingRepository extends BookingRepository {

        private Booking booking;
        private int saveCount;
        private java.util.List<Booking> seekerBookings = java.util.List.of();
        private java.util.List<Booking> providerBookings = java.util.List.of();
        private Long lastSeekerUserIdQueried;
        private Long lastProviderUserIdQueried;

        FakeBookingRepository() {
            super(null, null, null);
        }

        @Override
        public Optional<Booking> findById(Long bookingId) {
            return Optional.ofNullable(booking);
        }

        @Override
        public Booking save(Booking booking) {
            saveCount++;
            this.booking = booking;
            return booking;
        }

        @Override
        public java.util.List<Booking> findBySeekerUserId(Long seekerUserId) {
            lastSeekerUserIdQueried = seekerUserId;
            return seekerBookings;
        }

        @Override
        public java.util.List<Booking> findByProviderUserId(Long providerUserId) {
            lastProviderUserIdQueried = providerUserId;
            return providerBookings;
        }
    }

    private static class FakeServiceListingRepository
            extends ServiceListingRepository {

        private ServiceListing service;

        FakeServiceListingRepository() {
            super(null, null);
        }

        @Override
        public Optional<ServiceListing> findById(Long serviceId) {
            return Optional.ofNullable(service);
        }
    }

    private static class FakeSeekerAccountRepository
            extends SeekerAccountRepository {

        private SeekerAccount seeker;

        FakeSeekerAccountRepository() {
            super(null, null);
        }

        @Override
        public Optional<SeekerAccount> findByUserUserId(Long userId) {
            return Optional.ofNullable(seeker);
        }
    }
}
