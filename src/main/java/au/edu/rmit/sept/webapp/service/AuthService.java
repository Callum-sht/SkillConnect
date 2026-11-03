package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.exception.DuplicateEmailException;
import au.edu.rmit.sept.webapp.exception.InactiveUserException;
import au.edu.rmit.sept.webapp.exception.InvalidCredentialsException;
import au.edu.rmit.sept.webapp.model.ProviderAccount;
import au.edu.rmit.sept.webapp.model.SeekerAccount;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.model.UserStatus;
import au.edu.rmit.sept.webapp.repository.ProviderAccountRepository;
import au.edu.rmit.sept.webapp.repository.SeekerAccountRepository;
import au.edu.rmit.sept.webapp.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SeekerAccountRepository seekerAccountRepository;
    private final ProviderAccountRepository providerAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            SeekerAccountRepository seekerAccountRepository,
            ProviderAccountRepository providerAccountRepository) {

        this.userRepository = userRepository;
        this.seekerAccountRepository = seekerAccountRepository;
        this.providerAccountRepository = providerAccountRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User login(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InactiveUserException();
        }

        return user;
    }

    @Transactional
    public void register(
            String fullName,
            String email,
            String password,
            UserRole role) {

        if (fullName == null || fullName.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()
                || role == null) {
            throw new IllegalArgumentException("Required fields are missing.");
        }

        if (role != UserRole.PROVIDER && role != UserRole.SEEKER) {
            throw new IllegalArgumentException("Invalid user role.");
        }

        String normalizedEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException();
        }

        User user = new User();
        user.setName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        if (role == UserRole.SEEKER) {
            SeekerAccount seekerAccount = new SeekerAccount();
            seekerAccount.setUser(savedUser);
            seekerAccountRepository.save(seekerAccount);
        }

        if (role == UserRole.PROVIDER) {
            ProviderAccount providerAccount = new ProviderAccount();
            providerAccount.setUser(savedUser);
            providerAccountRepository.save(providerAccount);
        }
    }
}
