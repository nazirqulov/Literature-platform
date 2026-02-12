package uz.literature.platform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by: Barkamol
 * DateTime: 2/9/2026 10:43 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserReadingStatsResponse {
    private Long completedBooks;
    private Long readingBooks;
    private Long favoriteBooks;
    private Long totalBooks;
    private Long totalReadingMinutes;
    private Long totalReadingHours;
    private Long todayReadingMinutes;
    private Long totalPagesRead;
    private Double averageProgress;
}
