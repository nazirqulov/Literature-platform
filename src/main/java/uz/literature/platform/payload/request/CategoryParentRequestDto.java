package uz.literature.platform.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by: Barkamol
 * DateTime: 2/6/2026 11:12 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryParentRequestDto {

    private Long categoryId;

    private String parentName;

    private String description;

}
