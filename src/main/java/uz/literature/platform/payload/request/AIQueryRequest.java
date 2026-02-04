package uz.literature.platform.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AIQueryRequest {
    
    @NotBlank(message = "Savol kiritilishi shart")
    private String query;
    
    private Long bookId;
    
    private String conversationType;
}
