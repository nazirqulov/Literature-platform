package uz.literature.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.literature.platform.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByResetToken(String resetToken);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);

    Optional<Object> findByUsernameOrEmail(String name, String name1);
}
