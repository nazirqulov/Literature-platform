package uz.literature.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.literature.platform.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByResetToken(String resetToken);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);

    Optional<Object> findByUsernameOrEmail(String name, String name1);

    Page<User> findAllByRoleAndCreatedAtBetween(User.Role role, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    Page<User> findAllByRole(User.Role role, Pageable pageable);

    Page<User> findAllByCreatedAtBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1, Pageable pageable);
}
