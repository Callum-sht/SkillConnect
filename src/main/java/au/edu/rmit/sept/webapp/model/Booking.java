package au.edu.rmit.sept.webapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Booking {

    private Long bookingId;

    private ServiceListing service;

    private SeekerAccount seeker;

    private LocalDateTime bookingDate;

    private LocalDateTime bookingEndTime;

    private BigDecimal price;

    private BookingStatus status = BookingStatus.PENDING;

    private String declineReason;

    private LocalDateTime createdAt;

    private Review review;

    public Booking() {
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public ServiceListing getService() {
        return service;
    }

    public void setService(ServiceListing service) {
        this.service = service;
    }

    public SeekerAccount getSeeker() {
        return seeker;
    }

    public void setSeeker(SeekerAccount seeker) {
        this.seeker = seeker;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDateTime getBookingEndTime() {
        return bookingEndTime;
    }

    public void setBookingEndTime(LocalDateTime bookingEndTime) {
        this.bookingEndTime = bookingEndTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}
