package uz.literature.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.literature.platform.entity.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.subCategory.category.id = :categoryId AND b.isActive = true")
    Page<Book> findByCategoryIdAndIsActiveTrue(@Param("categoryId") Long categoryId, Pageable pageable);


    List<Book> findByIsFeaturedTrueAndIsActiveTrue();

    @Query("SELECT b FROM Book b WHERE b.isActive = true ORDER BY b.viewCount DESC")
    List<Book> findTopByViewCount(Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.isActive = true ORDER BY b.averageRating DESC, b.ratingCount DESC")
    List<Book> findTopRatedBooks(Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.isActive = true ORDER BY b.createdAt DESC")
    List<Book> findLatestBooks(Pageable pageable);

    @Query(
            value = """
                    select b.*
                    from books b
                    where b.is_active = true
                      and (
                        lower(b.title) like lower(concat('%', :keyword, '%'))
                        or lower(b.description) like lower(concat('%', :keyword, '%'))
                      )
                    """,
            countQuery = """
                    select count(*)
                    from books b
                    where b.is_active = true
                      and (
                        lower(b.title) like lower(concat('%', :keyword, '%'))
                        or lower(b.description) like lower(concat('%', :keyword, '%'))
                      )
                    """,
            nativeQuery = true
    )
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);

    Page<Book> findByIdAndIsActiveTrue(Long authorId, Pageable pageable);
}
