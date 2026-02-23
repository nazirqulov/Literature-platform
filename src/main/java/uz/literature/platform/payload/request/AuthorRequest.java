package uz.literature.platform.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Created by: Barkamol
 * DateTime: 2/21/2026 5:13 PM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthorRequest {

    @NotNull(message = "Author name is required")
    private String name;

    private String biography;

    private LocalDate birthDate;

    private LocalDate deathDate;

    private String nationality;
}
