package uz.literature.platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.literature.platform.entity.Book;
import uz.literature.platform.entity.Category;
import uz.literature.platform.entity.User;
import uz.literature.platform.exception.BadRequestException;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.payload.request.BookCreateRequest;
import uz.literature.platform.payload.response.BookResponse;
import uz.literature.platform.payload.response.CategoryResponse;
import uz.literature.platform.repository.BookRepository;
import uz.literature.platform.repository.CategoryRepository;
import uz.literature.platform.repository.FavoriteRepository;
import uz.literature.platform.service.interfaces.BookService;
import uz.literature.platform.util.FileUploadUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;


    private final CategoryRepository categoryRepository;

    private final FavoriteRepository favoriteRepository;

    private final ModelMapper modelMapper;

    private FileUploadUtil fileUploadUtil;

    @Override
    @Transactional
    public BookResponse createBook(BookCreateRequest request) {

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setIsbn(request.getIsbn());
        book.setPublishedYear(request.getPublishedYear());
        book.setPublisher(request.getPublisher());
        book.setLanguage(request.getLanguage());
        book.setPageCount(request.getPageCount());
        book.setIsFeatured(request.getIsFeatured());
        book.setIsActive(true);

        // Set categories
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (Long categoryId : request.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Kategoriya topilmadi: " + categoryId));
                categories.add(category);
            }
            book.setCategories(categories);
        }

        Book savedBook = bookRepository.save(book);
        return mapToResponse(savedBook, null);
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, BookCreateRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));


        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setIsbn(request.getIsbn());
        book.setPublishedYear(request.getPublishedYear());
        book.setPublisher(request.getPublisher());
        book.setLanguage(request.getLanguage());
        book.setPageCount(request.getPageCount());
        book.setIsFeatured(request.getIsFeatured());

        // Update categories
        if (request.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>();
            for (Long categoryId : request.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Kategoriya topilmadi: " + categoryId));
                categories.add(category);
            }
            book.setCategories(categories);
        }

        Book updatedBook = bookRepository.save(book);
        return mapToResponse(updatedBook, null);
    }

    @Override
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));
        return mapToResponse(book, null);
    }

    @Override
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findByIsActiveTrue(pageable);
        return books.map(book -> mapToResponse(book, null));
    }

    @Override
    public Page<BookResponse> searchBooks(String keyword, Pageable pageable) {
        Page<Book> books = bookRepository.searchBooks(keyword, pageable);
        return books.map(book -> mapToResponse(book, null));
    }

    @Override
    public Page<BookResponse> getBooksByAuthor(Long authorId, Pageable pageable) {
        Page<Book> books = bookRepository.findByIdAndIsActiveTrue(authorId, pageable);
        return books.map(book -> mapToResponse(book, null));
    }

    @Override
    public Page<BookResponse> getBooksByCategory(Long categoryId, Pageable pageable) {
        Page<Book> books = bookRepository.findByCategoriesIdAndIsActiveTrue(categoryId, pageable);
        return books.map(book -> mapToResponse(book, null));
    }

    @Override
    public List<BookResponse> getFeaturedBooks() {
        List<Book> books = bookRepository.findByIsFeaturedTrueAndIsActiveTrue();
        return books.stream()
                .map(book -> mapToResponse(book, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> getTopRatedBooks(int limit) {
        List<Book> books = bookRepository.findTopRatedBooks(PageRequest.of(0, limit));
        return books.stream()
                .map(book -> mapToResponse(book, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> getLatestBooks(int limit) {
        List<Book> books = bookRepository.findLatestBooks(PageRequest.of(0, limit));
        return books.stream()
                .map(book -> mapToResponse(book, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> getPopularBooks(int limit) {
        List<Book> books = bookRepository.findTopByViewCount(PageRequest.of(0, limit));
        return books.stream()
                .map(book -> mapToResponse(book, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookResponse uploadCoverImage(Long bookId, MultipartFile file) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));

        String fileName = fileUploadUtil.saveFile(file, "covers");
        book.setCoverImage(fileName);

        Book updatedBook = bookRepository.save(book);
        return mapToResponse(updatedBook, null);
    }

    @Override
    @Transactional
    public BookResponse uploadPdfFile(Long bookId, MultipartFile file) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));

        if (!file.getContentType().equals("application/pdf")) {
            throw new BadRequestException("Faqat PDF fayllar qabul qilinadi");
        }

        String fileName = fileUploadUtil.saveFile(file, "books");
        book.setPdfFile(fileName);

        Book updatedBook = bookRepository.save(book);
        return mapToResponse(updatedBook, null);
    }

    @Override
    @Transactional
    public BookResponse uploadAudioFile(Long bookId, MultipartFile file) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));

        String fileName = fileUploadUtil.saveFile(file, "audio");
        book.setAudioFile(fileName);

        Book updatedBook = bookRepository.save(book);
        return mapToResponse(updatedBook, null);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));

        book.setIsActive(false);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));

        book.setViewCount(book.getViewCount() + 1);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void incrementDownloadCount(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));

        book.setDownloadCount(book.getDownloadCount() + 1);
        bookRepository.save(book);
    }

    private BookResponse mapToResponse(Book book, User currentUser) {
        BookResponse response = modelMapper.map(book, BookResponse.class);

        Set<CategoryResponse> categoryResponses = book.getCategories().stream()
                .map(category -> {
                    CategoryResponse catResponse = modelMapper.map(category, CategoryResponse.class);
                    catResponse.setBooksCount(category.getBooks().size());
                    return catResponse;
                })
                .collect(Collectors.toSet());
        response.setCategories(categoryResponses);

        // Check if book is favorite for current user
        if (currentUser != null) {
            boolean isFavorite = favoriteRepository.existsByUserIdAndBookId(currentUser.getId(), book.getId());
            response.setIsFavorite(isFavorite);
        } else {
            response.setIsFavorite(false);
        }

        return response;
    }
}
