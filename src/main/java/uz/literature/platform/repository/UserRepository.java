package uz.literature.platform.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.literature.platform.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<Object> findByUsernameAndIsActiveTrueAndDeletedFalse(String username);

    Optional<User> findByEmail(String email);
    
    Optional<User> findByResetToken(String resetToken);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);

    Page<User> findAllByCreatedAtBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1, Pageable pageable);

    boolean existsByEmailAndIsActiveTrueAndDeletedFalse(String email);

    boolean existsByUsernameAndIsActiveTrueAndDeletedFalse(String username);

    Optional<User> findByIdAndIsActiveTrueAndDeletedFalse(Long id);

    Page<User> findAllByRoleAndCreatedAtBetweenAndIsActiveTrueAndDeletedFalse(User.Role role, LocalDateTime localDateTime, LocalDateTime localDateTime1, Pageable pageable);

    Page<User> findAllByRoleAndIsActiveTrueAndDeletedFalse(User.Role role, Pageable pageable);

    Optional<Object> findByEmailAndIsActiveTrueAndDeletedFalse(String usernameOrEmail);
}
