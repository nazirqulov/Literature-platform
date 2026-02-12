package uz.literature.platform.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.literature.platform.entity.base.BaseLongEntity;

import java.time.LocalDateTime;

/**
 * Created by: Barkamol
 * DateTime: 2/9/2026 10:36 AM
 */
@Entity
@Table(name = "user_book_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBookProgress extends BaseLongEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * Kitob o'qish statusi
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status = ReadingStatus.NOT_STARTED;

    /**
     * Hozirgi sahifa
     */
    @Column(name = "current_page")
    private Integer currentPage = 0;

    /**
     * Hozirgi bob
     */
    @Column(name = "current_chapter")
    private Integer currentChapter = 0;

    /**
     * O'qilgan foiz (0-100)
     */
    @Column(name = "progress_percentage")
    private Double progressPercentage = 0.0;

    /**
     * Kitob sevimli kitoblar ro'yxatidami?
     */
    @Column(name = "is_favorite")
    private Boolean isFavorite = false;

    /**
     * Kitob o'qishni boshlagan vaqt
     */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    /**
     * Kitob o'qishni tugatgan vaqt
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Oxirgi o'qilgan vaqt
     */
    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    /**
     * Foydalanuvchi reytingi (1-5)
     */
    @Column(name = "user_rating")
    private Integer userRating;

    /**
     * Foydalanuvchi sharhi
     */
    @Column(length = 1000)
    private String userReview;

    public enum ReadingStatus {
        NOT_STARTED,   // Boshlanmagan
        READING,       // O'qilmoqda
        COMPLETED,     // Tugatilgan
        ON_HOLD        // To'xtatilgan
    }

    public void updateProgress(int currentPage, int totalPages) {
        this.currentPage = currentPage;
        this.progressPercentage = totalPages > 0 ? (currentPage * 100.0) / totalPages : 0.0;
        this.lastReadAt = LocalDateTime.now();

        if (this.status == ReadingStatus.NOT_STARTED) {
            this.status = ReadingStatus.READING;
            this.startedAt = LocalDateTime.now();
        }

        if (this.progressPercentage >= 100.0) {
            this.status = ReadingStatus.COMPLETED;
            this.completedAt = LocalDateTime.now();
            this.progressPercentage = 100.0;
        }
    }

    public void toggleFavorite() {
        this.isFavorite = !this.isFavorite;
    }

    public void setRating(Integer rating, String review) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Reyting 1 dan 5 gacha bo'lishi kerak");
        }
        this.userRating = rating;
        this.userReview = review;
    }
}
