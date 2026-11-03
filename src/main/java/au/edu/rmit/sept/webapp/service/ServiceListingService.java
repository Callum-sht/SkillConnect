package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.dto.ServiceListingResponse;
import au.edu.rmit.sept.webapp.model.ServiceListing;
import au.edu.rmit.sept.webapp.model.ServiceStatus;
import au.edu.rmit.sept.webapp.repository.ServiceListingRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class ServiceListingService {

    private final ServiceListingRepository serviceListingRepository;

    public ServiceListingService(ServiceListingRepository serviceListingRepository) {
        this.serviceListingRepository = serviceListingRepository;
    }

    public List<ServiceListingResponse> getAvailableServices(
            String keyword,
            String category,
            String location,
            BigDecimal maxPrice) {
        if (maxPrice != null && maxPrice.signum() < 0) {
            throw new IllegalArgumentException("Maximum price cannot be negative.");
        }

        String normalizedKeyword = normalizeFilter(keyword);
        String normalizedCategory = normalizeFilter(category);
        String normalizedLocation = normalizeFilter(location);

        return serviceListingRepository
                .findByStatusOrderByCreatedAtDesc(ServiceStatus.ACTIVE)
                .stream()
                .filter(service -> matchesKeyword(service, normalizedKeyword))
                .filter(service -> matchesValue(service.getCategory(), normalizedCategory))
                .filter(service -> matchesValue(service.getLocation(), normalizedLocation))
                .filter(service -> maxPrice == null
                        || service.getHourlyRate().compareTo(maxPrice) <= 0)
                .map(this::toResponse)
                .toList();
    }

    public ServiceListingResponse getServiceDetail(Long serviceId) {
        ServiceListing service = serviceListingRepository.findById(serviceId)
                .orElse(null);

        if (service == null) {
            return null;
        }

        return toResponse(service);
    }

    public List<String> getCategories() {
        return serviceListingRepository.findDistinctCategories();
    }

    private boolean matchesKeyword(ServiceListing service, String keyword) {
        if (keyword == null) {
            return true;
        }

        return contains(service.getTitle(), keyword)
                || contains(service.getCategory(), keyword)
                || contains(service.getDescription(), keyword)
                || contains(service.getProvider().getUser().getName(), keyword);
    }

    private boolean matchesValue(String value, String filter) {
        return filter == null || value.equalsIgnoreCase(filter);
    }

    private boolean contains(String value, String keyword) {
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private String normalizeFilter(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim().toLowerCase(Locale.ROOT);
    }

    private ServiceListingResponse toResponse(ServiceListing service) {
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