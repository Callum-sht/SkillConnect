package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.dto.SeekerProfileResponse;
import au.edu.rmit.sept.webapp.dto.UpdateSeekerProfileRequest;
import au.edu.rmit.sept.webapp.exception.UserNotFoundException;
import au.edu.rmit.sept.webapp.model.SeekerAccount;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.repository.SeekerAccountRepository;
import au.edu.rmit.sept.webapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeekerService {

    private final UserRepository userRepository;
    private final SeekerAccountRepository seekerAccountRepository;

    public SeekerService(
            UserRepository userRepository,
            SeekerAccountRepository seekerAccountRepository) {
        this.userRepository = userRepository;
        this.seekerAccountRepository = seekerAccountRepository;
    }

    //Retrieves the profile of a seeker user by their user ID.
    public SeekerProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getRole() != UserRole.SEEKER) {
            throw new IllegalArgumentException("User is not a seeker.");
        }

        SeekerAccount seekerAccount = seekerAccountRepository
                .findByUserUserId(userId)
                .orElse(null);

        return toResponse(user, seekerAccount);
    }

    //Updates the profile of a seeker user, including their name and bio.
    //Transactional annotation ensures ensures the user and seeker profile updates succeed or roll back together.
    @Transactional
    public SeekerProfileResponse updateProfile(
            Long userId,
            UpdateSeekerProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getRole() != UserRole.SEEKER) {
            throw new IllegalArgumentException("User is not a seeker.");
        }

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

        SeekerAccount seekerAccount = seekerAccountRepository
                .findByUserUserId(userId)
                .orElse(null);

        if (request.getBio() != null) {
            String bio = request.getBio().trim();

            if (bio.length() > 1000) {
                throw new IllegalArgumentException(
                        "Bio cannot exceed 1000 characters.");
            }

            if (seekerAccount == null) {
                seekerAccount = new SeekerAccount();
                seekerAccount.setUser(user);
            }

            seekerAccount.setBio(bio.isEmpty() ? null : bio);

            seekerAccount = seekerAccountRepository.save(seekerAccount);
        }

        return toResponse(user, seekerAccount);
    }


    // Convert User and SeekerAccount entities to SeekerProfileResponse DTO.
    private SeekerProfileResponse toResponse(
            User user,
            SeekerAccount seekerAccount) {
        return new SeekerProfileResponse(
                user.getUserId(),
                seekerAccount == null ? null : seekerAccount.getSeekerAccountId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name(),
                seekerAccount == null ? null : seekerAccount.getBio(),
                user.getCreatedAt()
        );
    }
}
