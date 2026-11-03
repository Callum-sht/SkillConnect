package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.dto.AdminServiceListingResponse;
import au.edu.rmit.sept.webapp.model.ServiceListing;
import au.edu.rmit.sept.webapp.model.ServiceStatus;
import au.edu.rmit.sept.webapp.repository.ServiceListingRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceListingService {

    private final ServiceListingRepository serviceListingRepository;

    public AdminServiceListingService(ServiceListingRepository serviceListingRepository) {
        this.serviceListingRepository = serviceListingRepository;
    }

    public List<AdminServiceListingResponse> getAllServices() {
        return serviceListingRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public String updateServiceStatus(Long serviceId, String status) {
        ServiceListing service = serviceListingRepository.findById(serviceId).orElse(null);

        if (service == null) {
            return "Service listing not found.";
        }

        ServiceStatus newStatus;
        try {
            newStatus = ServiceStatus.valueOf(status);
        } catch (IllegalArgumentException | NullPointerException e) {
            return "Invalid status value.";
        }

        service.setStatus(newStatus);
        serviceListingRepository.save(service);

        return "Service status updated successfully.";
    }

    private AdminServiceListingResponse toResponse(ServiceListing service) {
        return new AdminServiceListingResponse(
                service.getServiceId(),
                service.getTitle(),
                service.getProvider().getUser().getName(),
                service.getCategory(),
                service.getLocation(),
                service.getStatus().name(),
                service.getHourlyRate());
    }
}