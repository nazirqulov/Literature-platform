package uz.literature.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.literature.platform.payload.request.BookCreateRequest;
import uz.literature.platform.payload.response.BookResponse;
import uz.literature.platform.service.interfaces.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BookController {


    private final BookService bookService;


    @GetMapping("/get-all")
    public ResponseEntity<Page<BookResponse>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponse> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        bookService.incrementViewCount(id);
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BookResponse>> searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponse> books = bookService.searchBooks(keyword, pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<BookResponse>> getBooksByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponse> books = bookService.getBooksByAuthor(authorId, pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<BookResponse>> getBooksByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponse> books = bookService.getBooksByCategory(categoryId, pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<BookResponse>> getFeaturedBooks() {
        List<BookResponse> books = bookService.getFeaturedBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<BookResponse>> getTopRatedBooks(
            @RequestParam(defaultValue = "10") int limit) {
        List<BookResponse> books = bookService.getTopRatedBooks(limit);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<BookResponse>> getLatestBooks(
            @RequestParam(defaultValue = "10") int limit) {
        List<BookResponse> books = bookService.getLatestBooks(limit);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<BookResponse>> getPopularBooks(
            @RequestParam(defaultValue = "10") int limit) {
        List<BookResponse> books = bookService.getPopularBooks(limit);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/create")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookCreateRequest request) {
        BookResponse book = bookService.createBook(request);
        return ResponseEntity.ok(book);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookCreateRequest request) {
        BookResponse book = bookService.updateBook(id, request);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cover")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> uploadCoverImage(
            @PathVariable Long id,
            @PathVariable MultipartFile file) {
        BookResponse book = bookService.uploadCoverImage(id, file);
        return ResponseEntity.ok(book);
    }

    @PostMapping("/{id}/pdf")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> uploadPdfFile(
            @PathVariable Long id,
            @PathVariable MultipartFile file) {
        BookResponse book = bookService.uploadPdfFile(id, file);
        return ResponseEntity.ok(book);
    }

    @PostMapping("/{id}/audio")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> uploadAudioFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        BookResponse book = bookService.uploadAudioFile(id, file);
        return ResponseEntity.ok(book);
    }

    @PostMapping("/{id}/download")
    public ResponseEntity<Void> incrementDownloadCount(@PathVariable Long id) {
        bookService.incrementDownloadCount(id);
        return ResponseEntity.ok().build();
    }
}
