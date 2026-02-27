package uz.literature.platform.payload.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class BookResponse {
    
    private Long id;
    private String title;
    private String description;
    private AuthorResponse author;
    private List<CategoryResponse> categories;   // ✅ bir nechta
    private List<String> subCategoryName;
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
