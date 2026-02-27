package uz.literature.platform.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class BookCreateRequest {
    
    @NotBlank(message = "Kitob nomi kiritilishi shart")
    private String title;
    
    private String description;
    
    @NotNull(message = "Muallif ID kiritilishi shart")
    private Long authorId;
    
    private Set<Long> subCategoryIds;
    
    private String isbn;
    
    private Integer publishedYear;
    
    private String publisher;
    
    private String language;
    
    private Integer pageCount;
    
    private Boolean isFeatured = false;
}
