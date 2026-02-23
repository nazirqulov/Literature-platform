package uz.literature.platform.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.literature.platform.entity.Author;

/**
 * Created by: Barkamol
 * DateTime: 2/21/2026 5:06 PM
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Author findByName(@NotNull(message = "Author name is required") String name);
}
