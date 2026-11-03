package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.dto.SeekerProfileForProviderResponse;
import au.edu.rmit.sept.webapp.model.BookingStatus;
import au.edu.rmit.sept.webapp.model.SeekerAccount;
import au.edu.rmit.sept.webapp.repository.BookingRepository;
import au.edu.rmit.sept.webapp.repository.SeekerAccountRepository;
import org.springframework.stereotype.Service;

@Service
public class SeekerProfileForProviderService {

    private final SeekerAccountRepository seekerAccountRepository;
    private final BookingRepository bookingRepository;

    public SeekerProfileForProviderService(
            SeekerAccountRepository seekerAccountRepository,
            BookingRepository bookingRepository) {
        this.seekerAccountRepository = seekerAccountRepository;
        this.bookingRepository = bookingRepository;
    }

    public SeekerProfileForProviderResponse getSeekerProfile(
            Long providerUserId,
            Long seekerUserId) {

        boolean hasBookingHistory =
                bookingRepository.existsBookingBetween(providerUserId, seekerUserId);

        if (!hasBookingHistory) {
            return null;
        }

        SeekerAccount seeker = seekerAccountRepository
                .findByUserUserId(seekerUserId)
                .orElse(null);

        if (seeker == null) {
            return null;
        }

        int completedBookingsCount = (int) bookingRepository
                .findByProviderUserId(providerUserId)
                .stream()
                .filter(booking -> booking.getSeeker().getUser().getUserId().equals(seekerUserId))
                .filter(booking -> booking.getStatus() == BookingStatus.COMPLETED)
                .count();

        return new SeekerProfileForProviderResponse(
                seeker.getUser().getName(),
                seeker.getBio(),
                completedBookingsCount);
    }
}