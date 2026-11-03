package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.dto.BookingResponse;
import au.edu.rmit.sept.webapp.model.Booking;
import au.edu.rmit.sept.webapp.model.BookingStatus;
import au.edu.rmit.sept.webapp.model.SeekerAccount;
import au.edu.rmit.sept.webapp.model.ServiceListing;
import au.edu.rmit.sept.webapp.repository.BookingRepository;
import au.edu.rmit.sept.webapp.repository.SeekerAccountRepository;
import au.edu.rmit.sept.webapp.repository.ServiceListingRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceListingRepository serviceListingRepository;
    private final SeekerAccountRepository seekerAccountRepository;

    public BookingService(
            BookingRepository bookingRepository,
            ServiceListingRepository serviceListingRepository,
            SeekerAccountRepository seekerAccountRepository) {
        this.bookingRepository = bookingRepository;
        this.serviceListingRepository = serviceListingRepository;
        this.seekerAccountRepository = seekerAccountRepository;
    }

    public String requestBooking(
            Long seekerUserId,
            Long serviceId,
            LocalDate bookingDate,
            LocalTime bookingTime,
            LocalDate bookingEndDate,
            LocalTime bookingEndTime) {

        if (serviceId == null) {
            return "Service is required.";
        }

        if (bookingDate == null) {
            return "Booking date is required.";
        }

        if (bookingTime == null) {
            return "Booking time is required.";
        }

        if (bookingEndDate == null) {
            return "Booking end date is required.";
        }

        if (bookingEndTime == null) {
            return "Booking end time is required.";
        }

        SeekerAccount seeker = seekerAccountRepository
                .findByUserUserId(seekerUserId)
                .orElse(null);

        if (seeker == null) {
            return "User is not a seeker.";
        }

        ServiceListing service = serviceListingRepository.findById(serviceId).orElse(null);

        if (service == null) {
            return "Service listing not found.";
        }

        LocalDateTime bookingDateTime = LocalDateTime.of(bookingDate, bookingTime);
        LocalDateTime bookingEndDateTime =
                LocalDateTime.of(bookingEndDate, bookingEndTime);

        if (bookingDateTime.isBefore(LocalDateTime.now())) {
            return "Booking date and time cannot be in the past.";
        }

        if (!bookingEndDateTime.isAfter(bookingDateTime)) {
            return "Booking end date and time must be after the start date and time.";
        }

        Booking booking = new Booking();
        booking.setSeeker(seeker);
        booking.setService(service);
        booking.setBookingDate(bookingDateTime);
        booking.setBookingEndTime(bookingEndDateTime);
        booking.setPrice(service.getHourlyRate());
        booking.setStatus(BookingStatus.PENDING);

        bookingRepository.save(booking);

        return "Booking request submitted successfully.";
    }

    public String declineBooking(Long bookingId, Long providerId, String declineReason) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null) {
            return "Booking not found.";
        }

        Long bookingProviderId = booking.getService().getProvider().getUser().getUserId();
        if (!bookingProviderId.equals(providerId)) {
            return "Provider does not own this booking.";
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            return "Only pending bookings can be declined.";
        }

        booking.setStatus(BookingStatus.DECLINED);
        booking.setDeclineReason(declineReason);
        bookingRepository.save(booking);

        return "Booking declined successfully.";
    }

    public String acceptBooking(Long bookingId, Long providerId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null) {
            return "Booking not found.";
        }

        Long bookingProviderId = booking.getService().getProvider().getUser().getUserId();
        if (!bookingProviderId.equals(providerId)) {
            return "Provider does not own this booking.";
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            return "Only pending bookings can be accepted.";
        }

        booking.setStatus(BookingStatus.ACCEPTED);
        bookingRepository.save(booking);

        return "Booking accepted successfully.";
    }

    public String completeBooking(Long bookingId, Long providerId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null) {
            return "Booking not found.";
        }

        Long bookingProviderId = booking.getService().getProvider().getUser().getUserId();
        if (!bookingProviderId.equals(providerId)) {
            return "Provider does not own this booking.";
        }

        if (booking.getStatus() != BookingStatus.ACCEPTED) {
            return "Only accepted bookings can be marked as completed.";
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        return "Booking marked as completed successfully.";
    }

    public List<BookingResponse> getSeekerBookingHistory(Long seekerUserId) {
        return bookingRepository.findBySeekerUserId(seekerUserId)
                .stream()
                .map(this::toSeekerBookingResponse)
                .toList();
    }

    public List<BookingResponse> getProviderBookingHistory(Long providerUserId) {
        return bookingRepository.findByProviderUserId(providerUserId)
                .stream()
                .map(this::toProviderBookingResponse)
                .toList();
    }

    private BookingResponse toSeekerBookingResponse(Booking booking) {
        String providerName = booking.getService().getProvider().getUser().getName();
        Long providerUserId = booking.getService().getProvider().getUser().getUserId();

        return new BookingResponse(
                booking.getBookingId(),
                booking.getService().getTitle(),
                providerName,
                providerUserId,
                booking.getBookingDate(),
                booking.getBookingEndTime(),
                booking.getPrice(),
                booking.getStatus(),
                booking.getDeclineReason());
    }

    private BookingResponse toProviderBookingResponse(Booking booking) {
        String seekerName = booking.getSeeker().getUser().getName();
        Long seekerUserId = booking.getSeeker().getUser().getUserId();

        return new BookingResponse(
                booking.getBookingId(),
                booking.getService().getTitle(),
                seekerName,
                seekerUserId,
                booking.getBookingDate(),
                booking.getBookingEndTime(),
                booking.getPrice(),
                booking.getStatus(),
                booking.getDeclineReason());
    }
}
