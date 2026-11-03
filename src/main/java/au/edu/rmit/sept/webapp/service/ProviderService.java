package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.dto.AddServiceRequest;
import au.edu.rmit.sept.webapp.dto.ProviderProfileResponse;
import au.edu.rmit.sept.webapp.dto.ServiceListingResponse;
import au.edu.rmit.sept.webapp.dto.UpdateProviderProfileRequest;
import au.edu.rmit.sept.webapp.exception.UserNotFoundException;
import au.edu.rmit.sept.webapp.model.ProviderAccount;
import au.edu.rmit.sept.webapp.model.ServiceListing;
import au.edu.rmit.sept.webapp.model.ServiceStatus;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.repository.ProviderAccountRepository;
import au.edu.rmit.sept.webapp.repository.ServiceListingRepository;
import au.edu.rmit.sept.webapp.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProviderService {

    private final UserRepository userRepository;
    private final ProviderAccountRepository providerAccountRepository;
    private final ServiceListingRepository serviceListingRepository;

    public ProviderService(
            UserRepository userRepository,
            ProviderAccountRepository providerAccountRepository,
            ServiceListingRepository serviceListingRepository) {
        this.userRepository = userRepository;
        this.providerAccountRepository = providerAccountRepository;
        this.serviceListingRepository = serviceListingRepository;
    }

    public ProviderProfileResponse getProfile(Long userId) {
        User user = requireProvider(userId);

        ProviderAccount providerAccount = providerAccountRepository
                .findByUserUserId(userId)
                .orElse(null);

        return toResponse(user, providerAccount);
    }

    @Transactional
    public ProviderProfileResponse updateProfile(
            Long userId,
            UpdateProviderProfileRequest request) {
        User user = requireProvider(userId);

        if (request.getName() != null) {
            String name = request.getName().trim();

            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty.");
            }

            if (name.length() > 100) {
                throw new IllegalArgumentException("Name cannot exceed 100 characters.");
            }

            user.setName(name);
            userRepository.save(user);
        }

        ProviderAccount providerAccount = providerAccountRepository
                .findByUserUserId(userId)
                .orElse(null);

        boolean accountFieldPresent = request.getBio() != null
                || request.getAvailability() != null
                || request.getPortfolio() != null
                || request.getLocation() != null;

        if (accountFieldPresent) {
            if (providerAccount == null) {
                providerAccount = new ProviderAccount();
                providerAccount.setUser(user);
            }

            if (request.getBio() != null) {
                String bio = request.getBio().trim();
                if (bio.length() > 1000) {
                    throw new IllegalArgumentException("Bio cannot exceed 1000 characters.");
                }
                providerAccount.setBio(bio.isEmpty() ? null : bio);
            }

            if (request.getAvailability() != null) {
                String availability = request.getAvailability().trim();
                if (availability.length() > 255) {
                    throw new IllegalArgumentException(
                            "Availability cannot exceed 255 characters.");
                }
                providerAccount.setAvailability(
                        availability.isEmpty() ? null : availability);
            }

            if (request.getPortfolio() != null) {
                String portfolio = request.getPortfolio().trim();
                providerAccount.setPortfolio(portfolio.isEmpty() ? null : portfolio);
            }

            if (request.getLocation() != null) {
                String location = request.getLocation().trim();
                if (location.length() > 255) {
                    throw new IllegalArgumentException(
                            "Location cannot exceed 255 characters.");
                }
                providerAccount.setLocation(location.isEmpty() ? null : location);
            }

            providerAccount = providerAccountRepository.save(providerAccount);
        }

        return toResponse(user, providerAccount);
    }

    public List<ServiceListingResponse> getServices(Long userId) {
        requireProvider(userId);

        return serviceListingRepository.findByProviderUserId(userId)
                .stream()
                .filter(listing -> listing.getStatus() != ServiceStatus.REMOVED)
                .map(this::toServiceResponse)
                .toList();
    }

    @Transactional
    public ServiceListingResponse addService(Long userId, AddServiceRequest request) {
        User user = requireProvider(userId);

        String title = trimmedRequired(request.getTitle(), "Title");
        String category = trimmedRequired(request.getCategory(), "Category");
        String location = trimmedRequired(request.getLocation(), "Location");
        String description = trimmedRequired(request.getDescription(), "Description");

        BigDecimal hourlyRate = request.getHourlyRate();
        if (hourlyRate == null || hourlyRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Hourly rate must be greater than zero.");
        }

        ProviderAccount providerAccount = providerAccountRepository
                .findByUserUserId(userId)
                .orElseGet(() -> {
                    ProviderAccount created = new ProviderAccount();
                    created.setUser(user);
                    return providerAccountRepository.save(created);
                });

        ServiceListing listing = new ServiceListing();
        listing.setProvider(providerAccount);
        listing.setTitle(title);
        listing.setCategory(category);
        listing.setLocation(location);
        listing.setDescription(description);
        listing.setHourlyRate(hourlyRate);

        return toServiceResponse(serviceListingRepository.save(listing));
    }

    @Transactional
    public void deleteService(Long userId, Long serviceId) {
        requireProvider(userId);

        ServiceListing listing = serviceListingRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Service listing not found."));

        Long ownerUserId = listing.getProvider().getUser().getUserId();
        if (!ownerUserId.equals(userId)) {
            throw new IllegalArgumentException(
                    "Providers can only delete their own listings.");
        }

        listing.setStatus(ServiceStatus.REMOVED);
        serviceListingRepository.save(listing);
    }

    private User requireProvider(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getRole() != UserRole.PROVIDER) {
            throw new IllegalArgumentException("User is not a provider.");
        }

        return user;
    }

    private String trimmedRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private ProviderProfileResponse toResponse(
            User user,
            ProviderAccount providerAccount) {
        return new ProviderProfileResponse(
                user.getUserId(),
                providerAccount == null ? null : providerAccount.getProviderAccountId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name(),
                providerAccount == null ? null : providerAccount.getBio(),
                providerAccount == null ? null : providerAccount.getAvailability(),
                providerAccount == null ? null : providerAccount.getPortfolio(),
                providerAccount == null ? null : providerAccount.getLocation(),
                providerAccount == null ? null : providerAccount.getRating(),
                user.getCreatedAt()
        );
    }

    private ServiceListingResponse toServiceResponse(ServiceListing listing) {
        return new ServiceListingResponse(
                listing.getServiceId(),
                listing.getTitle(),
                listing.getProvider().getUser().getName(),
                listing.getProvider().getUser().getUserId(),
                listing.getCategory(),
                listing.getLocation(),
                listing.getDescription(),
                listing.getHourlyRate(),
                listing.getRating()
        );
    }
}