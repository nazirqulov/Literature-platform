package uz.literature.platform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by: Barkamol
 * DateTime: 2/4/2026 3:57 PM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookDataDTO {

    private String title;

    private String author;
}
