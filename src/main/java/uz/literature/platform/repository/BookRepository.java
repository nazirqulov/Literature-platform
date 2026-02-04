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
    
    Page<Book> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title, Pageable pageable);
    
    Page<Book> findByAuthorIdAndIsActiveTrue(Long authorId, Pageable pageable);
    
    Page<Book> findByCategoriesIdAndIsActiveTrue(Long categoryId, Pageable pageable);
    
    List<Book> findByIsFeaturedTrueAndIsActiveTrue();
    
    @Query("SELECT b FROM Book b WHERE b.isActive = true ORDER BY b.viewCount DESC")
    List<Book> findTopByViewCount(Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.isActive = true ORDER BY b.averageRating DESC, b.ratingCount DESC")
    List<Book> findTopRatedBooks(Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.isActive = true ORDER BY b.createdAt DESC")
    List<Book> findLatestBooks(Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.author.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "b.isActive = true")
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);
}
