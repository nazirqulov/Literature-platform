package uz.literature.platform.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    
    private Long id;
    private UserResponse user;
    private Long bookId;
    private String bookTitle;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
