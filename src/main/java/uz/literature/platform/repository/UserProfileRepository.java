package uz.literature.platform.repository;

import lombok.extern.java.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.literature.platform.entity.UserProfile;

/**
 * Created by: Barkamol
 * DateTime: 2/4/2026 10:17 PM
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
