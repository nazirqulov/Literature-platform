package uz.literature.platform.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import uz.literature.platform.payload.request.BookCreateRequest;
import uz.literature.platform.payload.response.BookResponse;

import java.util.List;

public interface BookService {
    
    BookResponse createBook(BookCreateRequest request);
    
    BookResponse updateBook(Long id, BookCreateRequest request);
    
    BookResponse getBookById(Long id);
    
    Page<BookResponse> getAllBooks(Pageable pageable);
    
    Page<BookResponse> searchBooks(String keyword, Pageable pageable);
    
    Page<BookResponse> getBooksByAuthor(Long authorId, Pageable pageable);
    
    Page<BookResponse> getBooksByCategory(Long categoryId, Pageable pageable);
    
    List<BookResponse> getFeaturedBooks();
    
    List<BookResponse> getTopRatedBooks(int limit);
    
    List<BookResponse> getLatestBooks(int limit);
    
    List<BookResponse> getPopularBooks(int limit);
    
    BookResponse uploadCoverImage(Long bookId, MultipartFile file);
    
    BookResponse uploadPdfFile(Long bookId, MultipartFile file);
    
    BookResponse uploadAudioFile(Long bookId, MultipartFile file);
    
    void deleteBook(Long id);
    
    void incrementViewCount(Long id);
    
    void incrementDownloadCount(Long id);

}
