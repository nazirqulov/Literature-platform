package uz.literature.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.literature.platform.entity.SubCategory;

import java.util.List;

/**
 * Created by: Barkamol
 * DateTime: 2/6/2026 12:09 PM
 */
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    boolean existsByNameIgnoreCase(String name);
    @Query("""
   select sc from SubCategory sc
   where lower(sc.name) like lower(concat('%', :key, '%'))
      or lower(sc.description) like lower(concat('%', :key, '%'))
""")
    Page<SubCategory> search(@Param("key") String key, Pageable pageable);

    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);

    List<SubCategory> getAllByCategoryDeleted(boolean categoryDeleted);



}
