package uz.literature.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.literature.platform.entity.Category;
import uz.literature.platform.entity.SubCategory;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String attr0);

    Optional<Category> findByIdAndDeletedFalse(Long id);


    @Query("select c from Category c where c.subCategories is not empty")
    Page<Category> findRootCategories(Pageable pageable);

//    Optional<Object> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("""
               select sc from SubCategory sc
               where lower(sc.name) like lower(concat('%', :key, '%'))
                  or lower(sc.description) like lower(concat('%', :key, '%'))
            """)
    Page<SubCategory> search(@Param("key") String key, Pageable pageable);

    boolean existsByNameIgnoreCaseAndDeletedFalse(String categoryName);


}
