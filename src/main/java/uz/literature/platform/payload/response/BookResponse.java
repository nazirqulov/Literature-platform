package uz.literature.platform.payload.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookResponse {
    
    private Long id;
    private String title;
    private String description;
    private AuthorResponse author;
    private CategoryResponse category;
    private String subCategoryName;
    private String isbn;
    private Integer publishedYear;
    private String publisher;
    private String language;
    private Integer pageCount;
    private String coverImage;
    private String pdfFile;
    private String audioFile;
    private Integer viewCount;
    private Integer downloadCount;
    private Double averageRating;
    private Integer ratingCount;
    private Boolean isFeatured;
    private Boolean isFavorite;
    private LocalDateTime createdAt;
}
