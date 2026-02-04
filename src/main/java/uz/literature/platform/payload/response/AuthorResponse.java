package uz.literature.platform.payload.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AuthorResponse {
    
    private Long id;
    private String name;
    private String biography;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private String nationality;
    private String profileImage;
    private Integer booksCount;
    private LocalDateTime createdAt;
}
