package uz.literature.platform.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.*;
import uz.literature.platform.entity.base.BaseLongEntity;

/**
 * Created by: Barkamol
 * DateTime: 2/9/2026 10:38 AM
 */
@Entity
@Table(name = "chapters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chapter extends BaseLongEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private String title;

    @Column(name = "chapter_number", nullable = false)
    private Integer chapterNumber;

    @Column(name = "start_page")
    private Integer startPage;

    @Column(name = "end_page")
    private Integer endPage;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_available")
    private Boolean isAvailable = true;
}
