package uz.literature.platform.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by: Barkamol
 * DateTime: 2/9/2026 10:42 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateProgressRequest {

    @NotNull(message = "Joriy sahifa ko'rsatilishi kerak")
    @Min(value = 0, message = "Sahifa 0 dan kichik bo'lmasligi kerak")
    private Integer currentPage;

    private Integer currentChapter;
}

/**
 * O'qish sessiyasini boshlash uchun DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class StartSessionRequest {
    @NotNull(message = "Kitob ID ko'rsatilishi kerak")
    private Long bookId;

    @NotNull(message = "Joriy sahifa ko'rsatilishi kerak")
    private Integer currentPage;
}

/**
 * O'qish sessiyasini tugatish uchun DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class EndSessionRequest {
    @NotNull(message = "Sessiya ID ko'rsatilishi kerak")
    private Long sessionId;

    @NotNull(message = "Oxirgi sahifa ko'rsatilishi kerak")
    private Integer endPage;
}

/**
 * Reyting qo'yish uchun DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class AddRatingRequest {
    @NotNull(message = "Reyting ko'rsatilishi kerak")
    @Min(value = 1, message = "Reyting kamida 1 bo'lishi kerak")
    @Max(value = 5, message = "Reyting ko'pi bilan 5 bo'lishi kerak")
    private Integer rating;

    private String review;
}
