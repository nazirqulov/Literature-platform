package uz.literature.platform.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryResponse {
    
    private Long id;
    private String name;
    private String description;
    private Integer booksCount;
    private LocalDateTime createdAt;
}
