package au.edu.rmit.sept.webapp.dto;

import au.edu.rmit.sept.webapp.model.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingResponse {

    private final Long bookingId;
    private final String serviceTitle;
    private final String otherPartyName;
    private final Long otherPartyUserId;
    private final LocalDateTime bookingDate;
    private final LocalDateTime bookingEndTime;
    private final BigDecimal price;
    private final BookingStatus status;
    private final String declineReason;

    public BookingResponse(
            Long bookingId,
            String serviceTitle,
            String otherPartyName,
            Long otherPartyUserId,
            LocalDateTime bookingDate,
            LocalDateTime bookingEndTime,
            BigDecimal price,
            BookingStatus status,
            String declineReason) {
        this.bookingId = bookingId;
        this.serviceTitle = serviceTitle;
        this.otherPartyName = otherPartyName;
        this.otherPartyUserId = otherPartyUserId;
        this.bookingDate = bookingDate;
        this.bookingEndTime = bookingEndTime;
        this.price = price;
        this.status = status;
        this.declineReason = declineReason;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public String getOtherPartyName() {
        return otherPartyName;
    }

    public Long getOtherPartyUserId() {
        return otherPartyUserId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public LocalDateTime getBookingEndTime() {
        return bookingEndTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public String getDeclineReason() {
        return declineReason;
    }
}
