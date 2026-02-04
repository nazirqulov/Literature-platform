package uz.literature.platform.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {
    
    @NotNull(message = "Kitob ID kiritilishi shart")
    private Long bookId;
    
    @NotNull(message = "Baho kiritilishi shart")
    @Min(value = 1, message = "Baho 1 dan kam bo'lmasligi kerak")
    @Max(value = 5, message = "Baho 5 dan oshmasligi kerak")
    private Integer rating;
    
    private String comment;
}
