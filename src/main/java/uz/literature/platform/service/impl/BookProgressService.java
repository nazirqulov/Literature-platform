package uz.literature.platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.literature.platform.entity.Book;
import uz.literature.platform.entity.ReadingSession;
import uz.literature.platform.entity.User;
import uz.literature.platform.entity.UserBookProgress;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.payload.request.UpdateProgressRequest;
import uz.literature.platform.payload.response.BookProgressResponse;
import uz.literature.platform.payload.response.UserReadingStatsResponse;
import uz.literature.platform.repository.BookRepository;
import uz.literature.platform.repository.ReadingSessionRepository;
import uz.literature.platform.repository.UserBookProgressRepository;
import uz.literature.platform.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by: Barkamol
 * DateTime: 2/9/2026 10:43 AM
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookProgressService {

    private final UserBookProgressRepository progressRepository;
    private final ReadingSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    /**
     * Foydalanuvchining kitob progressini olish
     */
    public BookProgressResponse getBookProgress(String username, Long bookId) {
        User user = findUserByUsername(username);
        UserBookProgress progress = progressRepository
                .findByUserIdAndBookId(user.getId(), bookId)
                .orElse(createNewProgress(user, bookId));

        return mapToResponse(progress);
    }

    /**
     * Kitob o'qishni boshlash
     */
    @Transactional
    public BookProgressResponse startReading(String username, Long bookId) {
        User user = findUserByUsername(username);
        Book book = findBookById(bookId);

        UserBookProgress progress = progressRepository
                .findByUserIdAndBookId(user.getId(), bookId)
                .orElse(UserBookProgress.builder()
                        .user(user)
                        .book(book)
                        .status(UserBookProgress.ReadingStatus.NOT_STARTED)
                        .currentPage(0)
                        .currentChapter(0)
                        .progressPercentage(0.0)
                        .isFavorite(false)
                        .build());

        if (progress.getStatus() == UserBookProgress.ReadingStatus.NOT_STARTED) {
            progress.setStatus(UserBookProgress.ReadingStatus.READING);
            progress.setStartedAt(LocalDateTime.now());
        }

        // Kitob ko'rish sonini oshirish
        book.incrementViewCount();
        bookRepository.save(book);

        UserBookProgress saved = progressRepository.save(progress);
        log.info("User {} started reading book {}", username, bookId);

        return mapToResponse(saved);
    }

    /**
     * O'qish progressini yangilash
     */
    @Transactional
    public BookProgressResponse updateProgress(String username, Long bookId, UpdateProgressRequest request) {
        User user = findUserByUsername(username);
        Book book = findBookById(bookId);

        UserBookProgress progress = progressRepository
                .findByUserIdAndBookId(user.getId(), bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress topilmadi"));

        // Progressni yangilash
        progress.updateProgress(request.getCurrentPage(), book.getPageCount());

        if (request.getCurrentChapter() != null) {
            progress.setCurrentChapter(request.getCurrentChapter());
        }

        UserBookProgress saved = progressRepository.save(progress);
        log.info("User {} updated progress for book {}: {}%", username, bookId, saved.getProgressPercentage());

        return mapToResponse(saved);
    }

    /**
     * O'qish sessiyasini boshlash
     */
    @Transactional
    public Long startReadingSession(String username, Long bookId, int currentPage) {
        User user = findUserByUsername(username);
        Book book = findBookById(bookId);

        // Agar faol sessiya bo'lsa, avval uni tugatish
        sessionRepository.findActiveSession(user.getId())
                .ifPresent(activeSession -> activeSession.endSession(currentPage));

        ReadingSession session = ReadingSession.builder()
                .user(user)
                .book(book)
                .sessionStart(LocalDateTime.now())
                .startPage(currentPage)
                .build();

        ReadingSession saved = sessionRepository.save(session);
        log.info("User {} started reading session for book {}", username, bookId);

        return saved.getId();
    }

    /**
     * O'qish sessiyasini tugatish
     */
    @Transactional
    public void endReadingSession(Long sessionId, int endPage) {
        ReadingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessiya topilmadi"));

        session.endSession(endPage);
        sessionRepository.save(session);

        log.info("Reading session {} ended. Duration: {} minutes", sessionId, session.getDurationMinutes());
    }

    /**
     * Faol sessiyani tugatish
     */
    @Transactional
    public void endActiveSession(String username, int endPage) {
        User user = findUserByUsername(username);

        sessionRepository.findActiveSession(user.getId())
                .ifPresent(session -> {
                    session.endSession(endPage);
                    sessionRepository.save(session);
                    log.info("Active session ended for user {}", username);
                });
    }

    /**
     * Kitobni sevimli qilish/olib tashlash
     */
    @Transactional
    public BookProgressResponse toggleFavorite(String username, Long bookId) {
        User user = findUserByUsername(username);

        UserBookProgress progress = progressRepository
                .findByUserIdAndBookId(user.getId(), bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress topilmadi"));

        progress.toggleFavorite();
        UserBookProgress saved = progressRepository.save(progress);

        log.info("User {} toggled favorite for book {}: {}", username, bookId, saved.getIsFavorite());

        return mapToResponse(saved);
    }

    /**
     * Kitobga reyting qo'yish
     */
    @Transactional
    public BookProgressResponse addRating(String username, Long bookId, Integer rating, String review) {
        User user = findUserByUsername(username);
        Book book = findBookById(bookId);

        UserBookProgress progress = progressRepository
                .findByUserIdAndBookId(user.getId(), bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress topilmadi"));

        progress.setRating(rating, review);

        // Kitobning umumiy reytingini yangilash
        book.updateRating(rating.doubleValue());
        bookRepository.save(book);

        UserBookProgress saved = progressRepository.save(progress);

        log.info("User {} rated book {} with {} stars", username, bookId, rating);

        return mapToResponse(saved);
    }

    /**
     * Status bo'yicha kitoblarni olish
     */
    public Page<BookProgressResponse> getBooksByStatus(String username,
                                                       UserBookProgress.ReadingStatus status,
                                                       int page,
                                                       int size) {
        User user = findUserByUsername(username);

        Pageable pageable = PageRequest.of(page, size, Sort.by("lastReadAt").descending());
        Page<UserBookProgress> progressPage = progressRepository
                .findByUserIdAndStatus(user.getId(), status, pageable);

        return progressPage.map(this::mapToResponse);
    }

    /**
     * Sevimli kitoblarni olish
     */
    public Page<BookProgressResponse> getFavoriteBooks(String username, int page, int size) {
        User user = findUserByUsername(username);

        Pageable pageable = PageRequest.of(page, size, Sort.by("lastReadAt").descending());
        Page<UserBookProgress> progressPage = progressRepository
                .findByUserIdAndIsFavoriteTrue(user.getId(), pageable);

        return progressPage.map(this::mapToResponse);
    }

    /**
     * Oxirgi o'qilgan kitoblarni olish
     */
    public List<BookProgressResponse> getRecentBooks(String username, int limit) {
        User user = findUserByUsername(username);

        Pageable pageable = PageRequest.of(0, limit);
        List<UserBookProgress> progressList = progressRepository
                .findRecentlyReadBooks(user.getId(), pageable);

        return progressList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Foydalanuvchi statistikasi
     */
    public UserReadingStatsResponse getUserStats(String username) {
        User user = findUserByUsername(username);

        Long completedBooks = progressRepository.countCompletedBooksByUserId(user.getId());
        Long readingBooks = progressRepository.countReadingBooksByUserId(user.getId());
        Long favoriteBooks = progressRepository.countByUserIdAndIsFavoriteTrue(user.getId());
        Long totalMinutes = sessionRepository.getTotalReadingMinutesByUserId(user.getId());
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        Long todayMinutes = sessionRepository.getTodayReadingMinutesByUserId(user.getId(),startOfDay,endOfDay);
        Long totalPages = sessionRepository.getTotalPagesRead(user.getId());

        // O'rtacha progress hisoblash
        List<UserBookProgress> allProgress = progressRepository.findByUserId(user.getId());
        double averageProgress = allProgress.isEmpty() ? 0.0 :
                allProgress.stream()
                        .mapToDouble(UserBookProgress::getProgressPercentage)
                        .average()
                        .orElse(0.0);

        return UserReadingStatsResponse.builder()
                .completedBooks(completedBooks != null ? completedBooks : 0)
                .readingBooks(readingBooks != null ? readingBooks : 0)
                .favoriteBooks(favoriteBooks != null ? favoriteBooks : 0)
                .totalBooks((long) allProgress.size())
                .totalReadingMinutes(totalMinutes != null ? totalMinutes : 0)
                .totalReadingHours((totalMinutes != null ? totalMinutes : 0) / 60)
                .todayReadingMinutes(todayMinutes != null ? todayMinutes : 0)
                .totalPagesRead(totalPages != null ? totalPages : 0)
                .averageProgress(averageProgress)
                .build();
    }

    // Helper methods
    private User findUserByUsername(String username) {
        return (User) userRepository.findByUsernameAndIsActiveTrueAndDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));
    }

    private Book findBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));
    }

    private UserBookProgress createNewProgress(User user, Long bookId) {
        Book book = findBookById(bookId);
        return UserBookProgress.builder()
                .user(user)
                .book(book)
                .status(UserBookProgress.ReadingStatus.NOT_STARTED)
                .currentPage(0)
                .progressPercentage(0.0)
                .isFavorite(false)
                .build();
    }

    private BookProgressResponse mapToResponse(UserBookProgress progress) {
        Book book = progress.getBook();

        return BookProgressResponse.builder()
                .bookId(book.getId())
                .bookTitle(book.getTitle())
                .bookAuthors(book.getAuthorsAsString())
                .bookCover(book.getCoverImage())
                .category(book.getSubCategories() != null && book.getSubCategories().iterator().hasNext()
                        && book.getSubCategories().iterator().next().getCategory() != null
                        ? book.getSubCategories().iterator().next().getCategory().getName() : null)
                .subCategory(book.getSubCategories() != null && book.getSubCategories().iterator().hasNext()
                        ? book.getSubCategories().iterator().next().getName() : null)
                .status(progress.getStatus().name())
                .currentPage(progress.getCurrentPage())
                .totalPages(book.getPageCount())
                .currentChapter(progress.getCurrentChapter())
                .progressPercentage(progress.getProgressPercentage())
                .isFavorite(progress.getIsFavorite())
                .userRating(progress.getUserRating())
                .userReview(progress.getUserReview())
                .startedAt(progress.getStartedAt())
                .completedAt(progress.getCompletedAt())
                .lastReadAt(progress.getLastReadAt())
                .build();
    }
}
