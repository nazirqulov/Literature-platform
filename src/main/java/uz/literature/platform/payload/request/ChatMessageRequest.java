package uz.literature.platform.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatMessageRequest {
    
    @NotBlank(message = "Xabar matni kiritilishi shart")
    private String content;
    
    private Long roomId;
}
