package uz.literature.platform.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.literature.platform.entity.Category;
import uz.literature.platform.entity.SubCategory;
import uz.literature.platform.entity.User;
import uz.literature.platform.entity.UserProfile;
import uz.literature.platform.repository.CategoryRepository;
import uz.literature.platform.repository.SubCategoryRepository;
import uz.literature.platform.repository.UserRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    @Override
    public void run(String... args) throws Exception {
        importUser();
        seedCategoriesOnce();
    }

    private void seedCategoriesOnce() {
        if (categoryRepository.count() > 0) return;

        Map<String, List<String>> data = new LinkedHashMap<>();

        data.put("Badiiy adabiyot", List.of(
                "Roman", "Qissa", "Hikoya", "Fantastika", "Detektiv", "Drama",
                "Triller", "Sarguzasht", "Tarixiy roman", "Ilmiy fantastika"
        ));

        data.put("Ilmiy adabiyot", List.of(
                "Psixologiya", "Falsafa", "Tarix", "Sotsiologiya", "Siyosat",
                "Iqtisodiyot", "Biografiya", "Autobiografiya",
                "O‘zini rivojlantirish", "Tanqidiy tafakkur"
        ));

        data.put("Texnik adabiyot", List.of(
                "Dasturlash", "Sun’iy intellekt", "Data Science", "Kiberxavfsizlik",
                "Elektronika", "Muhandislik", "Arxitektura", "Robototexnika"
        ));

        data.put("Biznes va Moliyaviy", List.of(
                "Startup", "Marketing", "Menejment", "Liderlik", "Investitsiya",
                "Kriptovalyuta", "Shaxsiy moliya"
        ));

        data.put("Ta’lim", List.of(
                "Maktab darsliklari", "Oliy ta’lim", "IELTS/TOEFL", "Til o‘rganish",
                "Matematika", "Fizika", "Kimyo", "Biologiya"
        ));

        data.put("Bolalar adabiyoti", List.of(
                "Ertaklar", "Multfilm kitoblari", "Rivojlantiruvchi kitoblar", "O‘smirlar uchun"
        ));

        data.put("Diniy adabiyot", List.of(
                "Islom", "Tafsir", "Hadis", "Aqida", "Fiqh"
        ));

        data.put("Badiiy bo‘lmagan ijodiy", List.of(
                "She’riyat", "Dramaturgiya", "Esse", "Publicistika"
        ));

        for (var entry : data.entrySet()) {
            Category category = new Category();
            category.setName(entry.getKey());
            category.setDescription(null);
            categoryRepository.save(category);

            for (String subName : entry.getValue()) {
                SubCategory sc = new SubCategory();
                sc.setName(subName);
                sc.setCategory(category);
                subCategoryRepository.save(sc);
            }
        }
    }

    private void importUser() {
        String adminUsername = "admin123";
        String adminEmail = "admin@gmail.com";
        Optional<Object> existingAdmin = userRepository.findByUsernameAndIsActiveTrueAndDeletedFalse(adminUsername);

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
        profile.setFullName("Administrator");

        admin.setUserProfile(profile);
        userRepository.save(admin);
        log.info("Admin user created successfully");

    }
}
