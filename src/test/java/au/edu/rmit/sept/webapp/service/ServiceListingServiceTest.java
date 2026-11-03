package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.dto.ServiceListingResponse;
import au.edu.rmit.sept.webapp.model.ProviderAccount;
import au.edu.rmit.sept.webapp.model.ServiceListing;
import au.edu.rmit.sept.webapp.model.ServiceStatus;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.repository.ServiceListingRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceListingServiceTest {

    private FakeServiceListingRepository repository;
    private ServiceListingService service;

    @BeforeEach
    void setUp() {
        repository = new FakeServiceListingRepository();
        service = new ServiceListingService(repository);
    }

    @Test
    void returnsAllFields() {
        repository.activeListings = List.of(listing(1L));

        List<ServiceListingResponse> result = service
                .getAvailableServices(null, null, null, null);

        assertEquals(1, result.size());
        ServiceListingResponse response = result.get(0);
        assertEquals(1L, response.getId());
        assertEquals("House Cleaning", response.getTitle());
        assertEquals("Bright Home Services", response.getProvider());
        assertEquals("Cleaning", response.getCategory());
        assertEquals("Melbourne CBD", response.getLocation());
        assertEquals("Trusted local home cleaning", response.getDescription());
        assertEquals(new BigDecimal("45.00"), response.getPrice());
        assertEquals(new BigDecimal("4.90"), response.getRating());
        assertEquals(1, repository.queryCount);
    }

    @ParameterizedTest
    @ValueSource(strings = {" HOUSE ", "CLEANING", "trusted local", "BRIGHT HOME"})
    void matchesKeyword(String keyword) {
        repository.activeListings = List.of(listing(1L));

        List<ServiceListingResponse> result = service
                .getAvailableServices(keyword, null, null, null);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void appliesCombinedFilters() {
        ServiceListing match = listing(1L);
        match.setCategory("Gardening");
        match.setLocation("Carlton");
        match.setHourlyRate(new BigDecimal("50.00"));

        ServiceListing tooExpensive = listing(2L);
        tooExpensive.setCategory("Gardening");
        tooExpensive.setLocation("Carlton");
        tooExpensive.setHourlyRate(new BigDecimal("50.01"));

        ServiceListing wrongLocation = listing(3L);
        wrongLocation.setCategory("Gardening");
        wrongLocation.setLocation("Richmond");

        repository.activeListings = List.of(match, tooExpensive, wrongLocation);

        List<ServiceListingResponse> result = service.getAvailableServices(
                null, "GARDENING", "carlton", new BigDecimal("50.00"));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(new BigDecimal("50.00"), result.get(0).getPrice());
    }

    @Test
    void includesFreeService() {
        ServiceListing free = listing(1L);
        free.setHourlyRate(BigDecimal.ZERO);
        ServiceListing paid = listing(2L);
        paid.setHourlyRate(new BigDecimal("0.01"));

        repository.activeListings = List.of(free, paid);

        List<ServiceListingResponse> result = service
                .getAvailableServices(null, null, null, BigDecimal.ZERO);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void mapsNullRatingToZero() {
        ServiceListing unrated = listing(1L);
        unrated.setRating(null);
        repository.activeListings = List.of(unrated);

        List<ServiceListingResponse> result = service
                .getAvailableServices(null, null, null, null);

        assertEquals(BigDecimal.ZERO, result.get(0).getRating());
    }

    @Test
    void returnsEmptyWhenNoMatch() {
        repository.activeListings = List.of(listing(1L));

        List<ServiceListingResponse> result = service
                .getAvailableServices("laptop", null, null, null);

        assertEquals(0, result.size());
    }

    @Test
    void rejectsNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> service
                .getAvailableServices(null, null, null, new BigDecimal("-0.01")));

        assertEquals(0, repository.queryCount);
    }

    private ServiceListing listing(Long id) {
        User providerUser = new User();
        providerUser.setName("Bright Home Services");

        ProviderAccount provider = new ProviderAccount();
        provider.setUser(providerUser);

        ServiceListing listing = new ServiceListing();
        listing.setServiceId(id);
        listing.setTitle("House Cleaning");
        listing.setCategory("Cleaning");
        listing.setLocation("Melbourne CBD");
        listing.setDescription("Trusted local home cleaning");
        listing.setHourlyRate(new BigDecimal("45.00"));
        listing.setRating(new BigDecimal("4.90"));
        listing.setProvider(provider);
        listing.setStatus(ServiceStatus.ACTIVE);
        return listing;
    }

    private static class FakeServiceListingRepository
            extends ServiceListingRepository {

        private List<ServiceListing> activeListings = List.of();
        private int queryCount;

        FakeServiceListingRepository() {
            super(null, null);
        }

        @Override
        public List<ServiceListing> findByStatusOrderByCreatedAtDesc(
                ServiceStatus status) {
            queryCount++;
            return activeListings;
        }
    }
}
