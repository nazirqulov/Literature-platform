package uz.literature.platform.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {
    
    private Long id;
    private Long roomId;
    private String roomName;
    private UserResponse sender;
    private String content;
    private String type;
    private LocalDateTime createdAt;
}
