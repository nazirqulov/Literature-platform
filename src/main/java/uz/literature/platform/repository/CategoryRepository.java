package uz.literature.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.literature.platform.entity.Category;
import uz.literature.platform.projection.CategoryProjection;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = """
            select distinct c.name,
                   c.description
            from categories c
            """, nativeQuery = true)
    List<CategoryProjection> read();

    boolean existsByNameIgnoreCase(String attr0);
}
