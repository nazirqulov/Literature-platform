package uz.literature.platform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Created by: Barkamol
 * DateTime: 2/4/2026 3:56 PM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDTO {

    private String name;

    private String description;

}
