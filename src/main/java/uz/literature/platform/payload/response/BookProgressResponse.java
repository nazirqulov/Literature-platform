package uz.literature.platform.payload.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Created by: Barkamol
 * DateTime: 2/9/2026 10:41 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookProgressResponse {
    private Long bookId;
    private String bookTitle;
    private String bookAuthors;
    private String bookCover;
    private String category;
    private String subCategory;
    private String status;
    private Integer currentPage;
    private Integer totalPages;
    private Integer currentChapter;
    private Double progressPercentage;
    private Boolean isFavorite;
    private Integer userRating;
    private String userReview;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastReadAt;
}
