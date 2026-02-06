package uz.literature.platform.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by: Barkamol
 * DateTime: 2/6/2026 11:46 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryCreateRequestDto {

    private String categoryName;

    private String categoryDescription;

    private List<ParentData>subCategories;
}
