package uz.literature.platform.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.literature.platform.entity.User;
import uz.literature.platform.entity.UserProfile;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

/**
 * Created by: Barkamol
 * DateTime: 2/21/2026 2:33 PM
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
     importUser();
    }

    @PostConstruct
    private void importUser() {
        String adminUsername = "admin123";
        String adminEmail = "admin@gmail.com";
        Optional<User> existingAdmin = userRepository.findByUsername(adminUsername);

        if (existingAdmin.isPresent()) {
            log.info("Admin user already exists");
            return;
        }

        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.SUPERADMIN);
        admin.setIsActive(true);

        UserProfile profile = new UserProfile();
        profile.setUser(admin);
        profile.setUsername(adminUsername);
        profile.setEmail(adminEmail);
        profile.setFullName("Administrator");

        admin.setUserProfile(profile);
        userRepository.save(admin);
        log.info("Admin user created successfully");

    }
}
