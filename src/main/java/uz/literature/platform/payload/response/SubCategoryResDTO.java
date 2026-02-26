package uz.literature.platform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by: Barkamol
 * DateTime: 2/26/2026 11:08 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubCategoryResDTO {

    private Long categoryId;

    private String name;

    private String description;
}
