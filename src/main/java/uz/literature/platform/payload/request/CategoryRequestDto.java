package uz.literature.platform.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by: Barkamol
 * DateTime: 2/4/2026 4:33 PM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryRequestDto {
    @NotBlank(message = "Category nomi bo‘sh bo‘lishi mumkin emas")
    private String name;

    private String description;

    // Parent orqali bog‘lash
    private Long parentId;     // agar mavjud bo‘lsa

    private String parentName; // agar nom orqali topish kerak bo‘lsa

}
