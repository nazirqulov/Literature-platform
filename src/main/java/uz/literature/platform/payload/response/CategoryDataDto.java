package uz.literature.platform.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Barkamol
 * DateTime: 2/6/2026 9:24 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDataDto {
    private Long id;

    @NotBlank
    private String name;

    private String description;

    private List<CategoryDTO> children = new ArrayList<>();

    private Long booksCount;
    private Boolean hasChildren;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
