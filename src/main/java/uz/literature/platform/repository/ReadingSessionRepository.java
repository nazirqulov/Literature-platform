package uz.literature.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.literature.platform.entity.ReadingSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by: Barkamol
 * DateTime: 2/9/2026 10:45 AM
 */
public interface ReadingSessionRepository extends JpaRepository<ReadingSession, Long> {


    /**
     * Foydalanuvchining barcha o'qish sessiyalarini olish
     */
    List<ReadingSession> findByUserIdOrderBySessionStartDesc(Long userId);

    /**
     * Foydalanuvchining o'qish sessiyalarini sahifalab olish
     */
    Page<ReadingSession> findByUserIdOrderBySessionStartDesc(Long userId, Pageable pageable);

    /**
     * Foydalanuvchining ma'lum kitob uchun sessiyalarini olish
     */
    List<ReadingSession> findByUserIdAndBookId(Long userId, Long bookId);

    /**
     * Foydalanuvchining faol sessiyasini topish
     */
    @Query("SELECT s FROM ReadingSession s WHERE s.user.id = :userId AND s.sessionEnd IS NULL ORDER BY s.sessionStart DESC")
    Optional<ReadingSession> findActiveSession(@Param("userId") Long userId);

    /**
     * Foydalanuvchining faol sessiyasi bormi?
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM ReadingSession s WHERE s.user.id = :userId AND s.sessionEnd IS NULL")
    boolean hasActiveSession(@Param("userId") Long userId);

    /**
     * Foydalanuvchining umumiy o'qish vaqtini hisoblash
     */
    @Query("SELECT COALESCE(SUM(s.durationMinutes), 0) FROM ReadingSession s WHERE s.user.id = :userId")
    Long getTotalReadingMinutesByUserId(@Param("userId") Long userId);

    /**
     * Foydalanuvchining bugungi o'qish vaqtini olish
     */
//    @Query("SELECT COALESCE(SUM(s.durationMinutes), 0) FROM ReadingSession s WHERE s.user.id = :userId AND DATE(s.sessionStart) = CURRENT_DATE")
//    Long getTodayReadingMinutesByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(s.durationMinutes), 0) FROM ReadingSession s WHERE s.user.id = :userId AND s.sessionStart BETWEEN :startOfDay AND :endOfDay")
    Long getTodayReadingMinutesByUserId(@Param("userId") Long userId,
                                        @Param("startOfDay") LocalDateTime startOfDay,
                                        @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * Foydalanuvchining haftalik o'qish vaqtini olish
     */
    @Query("SELECT COALESCE(SUM(s.durationMinutes), 0) FROM ReadingSession s WHERE s.user.id = :userId AND s.sessionStart >= :startDate")
    Long getReadingMinutesSince(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    /**
     * Foydalanuvchining ma'lum kitob uchun umumiy o'qish vaqti
     */
    @Query("SELECT COALESCE(SUM(s.durationMinutes), 0) FROM ReadingSession s WHERE s.user.id = :userId AND s.book.id = :bookId")
    Long getBookReadingTime(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * Foydalanuvchining o'qilgan sahifalar soni
     */
    @Query("SELECT COALESCE(SUM(s.pagesRead), 0) FROM ReadingSession s WHERE s.user.id = :userId")
    Long getTotalPagesRead(@Param("userId") Long userId);
}
