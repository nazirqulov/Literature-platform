package uz.literature.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.literature.platform.entity.Review;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByBookId(Long bookId, Pageable pageable);
    
    Page<Review> findByUserId(Long userId, Pageable pageable);
    
    Optional<Review> findByUserIdAndBookId(Long userId, Long bookId);
    
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
}
