package uz.literature.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.literature.platform.entity.Book;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.payload.request.BookCreateRequest;
import uz.literature.platform.payload.response.BookResponse;
import uz.literature.platform.repository.BookRepository;
import uz.literature.platform.service.interfaces.BookService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BookController {


    private final BookService bookService;
    private final BookRepository bookRepository;
    @Value("${app.upload.book.image}")
    private String uploadFile;

    @Value("${app.upload.book.pdf}")
    private String uploadPdfFile;

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
            @RequestParam(required = false) String keyword,
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

    @PutMapping("/update/{id}")
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

    @GetMapping("/book-image/{id}")
    public ResponseEntity<?> getBookImage(@PathVariable Long id) throws IOException {


        Optional<Book> optional = bookRepository.findByIdAndDeletedFalse(id);

        Book book = optional.orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));

        String filename = book.getCoverImage();

        if (filename == null || filename.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        String substring = filename.substring(7);

        Path filePath = Paths.get(uploadFile).resolve(substring).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @PostMapping(path = "/file/pdf/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> uploadPdfFile(
            @PathVariable Long id,
            @RequestParam MultipartFile file) {
        BookResponse book = bookService.uploadPdfFile(id, file);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<?> viewPdf(@PathVariable Long id) throws IOException {

        Book book = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getPdfFile() == null) return ResponseEntity.notFound().build();

        String substring = book.getPdfFile().substring(6);

        Path path = Paths.get(uploadPdfFile).resolve(substring).normalize();
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists() || !resource.isReadable()) return ResponseEntity.notFound().build();

        String contentType = Files.probeContentType(path);
        if (contentType == null) contentType = "application/octet-stream";
        MediaType mediaType = MediaType.APPLICATION_PDF;
        mediaType = MediaType.parseMediaType(contentType);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"book.pdf\"")
                .body(resource) ;
    }

    @PostMapping(path = "/file/audio/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> uploadAudioFile(
            @PathVariable Long id,
            @RequestParam MultipartFile file) {
        BookResponse book = bookService.uploadAudioFile(id, file);
        return ResponseEntity.ok(book);
    }

    @PostMapping(path = "/file/download/{id}")
    public ResponseEntity<Void> incrementDownloadCount(@PathVariable Long id) {
        bookService.incrementDownloadCount(id);
        return ResponseEntity.ok().build();
    }
}
