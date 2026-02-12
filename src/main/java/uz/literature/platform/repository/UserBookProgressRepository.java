package uz.literature.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.literature.platform.entity.UserBookProgress;

import java.util.List;
import java.util.Optional;

/**
 * Created by: Barkamol
 * DateTime: 2/9/2026 10:46 AM
 */
public interface UserBookProgressRepository extends JpaRepository<UserBookProgress, Long> {


    /**
     * Foydalanuvchining kitob progressini topish
     */
    Optional<UserBookProgress> findByUserIdAndBookId(Long userId, Long bookId);

    /**
     * Foydalanuvchining barcha kitoblarini status bo'yicha olish
     */
    List<UserBookProgress> findByUserIdAndStatus(Long userId, UserBookProgress.ReadingStatus status);

    /**
     * Foydalanuvchining barcha kitoblarini status bo'yicha sahifalab olish
     */
    Page<UserBookProgress> findByUserIdAndStatus(Long userId, UserBookProgress.ReadingStatus status, Pageable pageable);

    /**
     * Foydalanuvchining sevimli kitoblarini olish
     */
    List<UserBookProgress> findByUserIdAndIsFavoriteTrue(Long userId);

    /**
     * Foydalanuvchining sevimli kitoblarini sahifalab olish
     */
    Page<UserBookProgress> findByUserIdAndIsFavoriteTrue(Long userId, Pageable pageable);

    /**
     * Foydalanuvchining barcha kitoblarini olish
     */
    List<UserBookProgress> findByUserId(Long userId);

    /**
     * Foydalanuvchining tugallagan kitoblar sonini hisoblash
     */
    @Query("SELECT COUNT(p) FROM UserBookProgress p WHERE p.user.id = :userId AND p.status = 'COMPLETED'")
    Long countCompletedBooksByUserId(@Param("userId") Long userId);

    /**
     * Foydalanuvchining o'qiyotgan kitoblar sonini hisoblash
     */
    @Query("SELECT COUNT(p) FROM UserBookProgress p WHERE p.user.id = :userId AND p.status = 'READING'")
    Long countReadingBooksByUserId(@Param("userId") Long userId);

    /**
     * Foydalanuvchining sevimli kitoblar sonini hisoblash
     */
    Long countByUserIdAndIsFavoriteTrue(Long userId);

    /**
     * Oxirgi o'qilgan kitoblarni olish
     */
    @Query("SELECT p FROM UserBookProgress p WHERE p.user.id = :userId ORDER BY p.lastReadAt DESC")
    List<UserBookProgress> findRecentlyReadBooks(@Param("userId") Long userId, Pageable pageable);

    /**
     * Foydalanuvchi kitobni o'qiyaptimi yoki yo'qligini tekshirish
     */
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    /**
     * Foydalanuvchi kitobni sevimli qilganmi?
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM UserBookProgress p WHERE p.user.id = :userId AND p.book.id = :bookId AND p.isFavorite = true")
    boolean isBookFavorite(@Param("userId") Long userId, @Param("bookId") Long bookId);
}
