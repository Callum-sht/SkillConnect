package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.dto.ProviderProfileForSeekerResponse;
import au.edu.rmit.sept.webapp.dto.ServiceListingResponse;
import au.edu.rmit.sept.webapp.model.ProviderAccount;
import au.edu.rmit.sept.webapp.model.ServiceListing;
import au.edu.rmit.sept.webapp.repository.ProviderAccountRepository;
import au.edu.rmit.sept.webapp.repository.ServiceListingRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProviderProfileForSeekerService {

    private final ProviderAccountRepository providerAccountRepository;
    private final ServiceListingRepository serviceListingRepository;

    public ProviderProfileForSeekerService(
            ProviderAccountRepository providerAccountRepository,
            ServiceListingRepository serviceListingRepository) {
        this.providerAccountRepository = providerAccountRepository;
        this.serviceListingRepository = serviceListingRepository;
    }

    public ProviderProfileForSeekerResponse getProviderProfile(Long providerUserId) {
        ProviderAccount provider = providerAccountRepository
                .findByUserUserId(providerUserId)
                .orElse(null);

        if (provider == null) {
            return null;
        }

        List<ServiceListingResponse> services = serviceListingRepository
                .findByProviderUserId(providerUserId)
                .stream()
                .map(this::toServiceResponse)
                .toList();

        return new ProviderProfileForSeekerResponse(
                provider.getUser().getName(),
                provider.getBio(),
                provider.getAvailability(),
                provider.getRating() == null ? BigDecimal.ZERO : provider.getRating(),
                services);
    }

    private ServiceListingResponse toServiceResponse(ServiceListing service) {
        return new ServiceListingResponse(
                service.getServiceId(),
                service.getTitle(),
                service.getProvider().getUser().getName(),
                service.getProvider().getUser().getUserId(),
                service.getCategory(),
                service.getLocation(),
                service.getDescription(),
                service.getHourlyRate(),
                service.getRating() == null ? BigDecimal.ZERO : service.getRating()
        );
    }
}