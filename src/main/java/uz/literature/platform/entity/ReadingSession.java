package uz.literature.platform.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.literature.platform.entity.base.BaseLongEntity;

import java.time.LocalDateTime;

/**
 * Created by: Barkamol
 * DateTime: 2/9/2026 10:39 AM
 */
/**
 * O'qish sessiyasi - har bir o'qish sessiyasini kuzatish
 */
@Entity
@Table(name = "reading_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingSession extends BaseLongEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * Sessiya boshlangan vaqt
     */
    @Column(name = "session_start", nullable = false)
    private LocalDateTime sessionStart;

    /**
     * Sessiya tugagan vaqt
     */
    @Column(name = "session_end")
    private LocalDateTime sessionEnd;

    /**
     * O'qish davomiyligi (daqiqalarda)
     */
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    /**
     * Boshlang'ich sahifa
     */
    @Column(name = "start_page")
    private Integer startPage;

    /**
     * Oxirgi sahifa
     */
    @Column(name = "end_page")
    private Integer endPage;

    /**
     * O'qilgan sahifalar soni
     */
    @Column(name = "pages_read")
    private Integer pagesRead;

    /**
     * Sessiyani tugatish
     */
    public void endSession(int endPage) {
        this.sessionEnd = LocalDateTime.now();
        this.endPage = endPage;
        this.pagesRead = endPage - startPage;

        // Daqiqalarda hisoblash
        long seconds = java.time.Duration.between(sessionStart, sessionEnd).getSeconds();
        this.durationMinutes = (int) (seconds / 60);
    }

    /**
     * Sessiya faolmi?
     */
    public boolean isActive() {
        return sessionEnd == null;
    }
}
