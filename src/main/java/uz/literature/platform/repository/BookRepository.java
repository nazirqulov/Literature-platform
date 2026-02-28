package uz.literature.platform.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.literature.platform.entity.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByIsActiveTrue(Pageable pageable);

//    @Query("SELECT b FROM Book b WHERE b.subCategory.category.id = :categoryId AND b.isActive = true")
//    Page<Book> findByCategoryIdAndIsActiveTrue(@Param("categoryId") Long categoryId, Pageable pageable);
//

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
                                join book_authors ba on b.id = ba.book_id
                                join authors a on ba.author_id = a.id
                       where b.is_active = true
                         and (
                           lower(b.title) like lower(concat('%', :keyword, '%'))
                               or lower(b.description) like lower(concat('%', :keyword, '%'))
                               or lower(b.isbn) like lower(concat('%', :keyword, '%'))
                               or lower(a.name) like lower(concat('%', :keyword, '%'))
                           )
                    """,
            countQuery = """
                    select count(*)
                       from books b
                                join book_authors ba on b.id = ba.book_id
                                join authors a on ba.author_id = a.id
                       where b.is_active = true
                         and (
                           lower(b.title) like lower(concat('%', :keyword, '%'))
                               or lower(b.description) like lower(concat('%', :keyword, '%'))
                               or lower(b.isbn) like lower(concat('%', :keyword, '%'))
                               or lower(a.name) like lower(concat('%', :keyword, '%'))
                           )
                    """,
            nativeQuery = true
    )
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);

    Page<Book> findByIdAndIsActiveTrue(Long authorId, Pageable pageable);

    Book findByTitleAndIsActiveTrue(@NotBlank(message = "Kitob nomi kiritilishi shart") String title);

    @Query("""
               select distinct b
               from Book b
               join b.subCategories sc
               where sc.category.id = :categoryId
                 and b.isActive = true
            """)
    Page<Book> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    Optional<Book> findByIdAndDeletedFalse(Long id);}
