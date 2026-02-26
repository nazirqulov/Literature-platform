package uz.literature.platform.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by: Barkamol
 * DateTime: 2/26/2026 11:37 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubCategoryUpdateDTO {

    private String name;

    private String description;

}
