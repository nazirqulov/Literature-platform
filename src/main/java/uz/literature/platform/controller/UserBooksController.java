package uz.literature.platform.controller;

/**
 * Created by: Barkamol
 * DateTime: 2/9/2026 10:54 AM
 */

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import uz.literature.platform.entity.UserBookProgress;
import uz.literature.platform.payload.response.BookProgressResponse;
import uz.literature.platform.payload.response.UserReadingStatsResponse;
import uz.literature.platform.service.impl.BookProgressService;

import java.util.List;

/**
 * Foydalanuvchi kitoblarini olish uchun controller
 */
@RestController
@RequestMapping("/api/me/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserBooksController {
    private final BookProgressService progressService;

    /**
     * O'qiyotgan kitoblarni olish (sahifalab)
     * GET /api/me/books/reading?page=0&size=10
     *
     * @param userDetails - authentication principal
     * @param page        - sahifa raqami (default: 0)
     * @param size        - sahifadagi elementlar soni (default: 10)
     * @return Page<BookProgressResponse>
     */
    @GetMapping("/reading")
    public ResponseEntity<Page<BookProgressResponse>> getReadingBooks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<BookProgressResponse> books = progressService.getBooksByStatus(
                userDetails.getUsername(),
                UserBookProgress.ReadingStatus.READING,
                page,
                size
        );

        return ResponseEntity.ok(books);
    }

    /**
     * Tugatilgan kitoblarni olish (sahifalab)
     * GET /api/me/books/completed?page=0&size=10
     *
     * @param userDetails - authentication principal
     * @param page        - sahifa raqami (default: 0)
     * @param size        - sahifadagi elementlar soni (default: 10)
     * @return Page<BookProgressResponse>
     */
    @GetMapping("/completed")
    public ResponseEntity<Page<BookProgressResponse>> getCompletedBooks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<BookProgressResponse> books = progressService.getBooksByStatus(
                userDetails.getUsername(),
                UserBookProgress.ReadingStatus.COMPLETED,
                page,
                size
        );

        return ResponseEntity.ok(books);
    }

    /**
     * To'xtatilgan kitoblarni olish (sahifalab)
     * GET /api/me/books/on-hold?page=0&size=10
     *
     * @param userDetails - authentication principal
     * @param page        - sahifa raqami (default: 0)
     * @param size        - sahifadagi elementlar soni (default: 10)
     * @return Page<BookProgressResponse>
     */
    @GetMapping("/on-hold")
    public ResponseEntity<Page<BookProgressResponse>> getOnHoldBooks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<BookProgressResponse> books = progressService.getBooksByStatus(
                userDetails.getUsername(),
                UserBookProgress.ReadingStatus.ON_HOLD,
                page,
                size
        );

        return ResponseEntity.ok(books);
    }

    /**
     * Sevimli kitoblarni olish (sahifalab)
     * GET /api/me/books/favorites?page=0&size=10
     *
     * @param userDetails - authentication principal
     * @param page        - sahifa raqami (default: 0)
     * @param size        - sahifadagi elementlar soni (default: 10)
     * @return Page<BookProgressResponse>
     */
    @GetMapping("/favorites")
    public ResponseEntity<Page<BookProgressResponse>> getFavoriteBooks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<BookProgressResponse> books = progressService.getFavoriteBooks(
                userDetails.getUsername(),
                page,
                size
        );

        return ResponseEntity.ok(books);
    }

    /**
     * Oxirgi o'qilgan kitoblarni olish
     * GET /api/me/books/recent?limit=5
     *
     * @param userDetails - authentication principal
     * @param limit       - qaytariladigan kitoblar soni (default: 5)
     * @return List<BookProgressResponse>
     */
    @GetMapping("/recent")
    public ResponseEntity<List<BookProgressResponse>> getRecentBooks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "5") int limit) {

        List<BookProgressResponse> books = progressService.getRecentBooks(
                userDetails.getUsername(),
                limit
        );

        return ResponseEntity.ok(books);
    }

    /**
     * Foydalanuvchi statistikasi
     * GET /api/me/books/stats
     * <p>
     * Response:
     * {
     * "completedBooks": 5,
     * "readingBooks": 3,
     * "favoriteBooks": 8,
     * "totalBooks": 15,
     * "totalReadingMinutes": 720,
     * "totalReadingHours": 12,
     * "todayReadingMinutes": 45,
     * "totalPagesRead": 450,
     * "averageProgress": 65.5
     * }
     *
     * @param userDetails - authentication principal
     * @return UserReadingStatsResponse
     */
    @GetMapping("/stats")
    public ResponseEntity<UserReadingStatsResponse> getUserStats(
            @AuthenticationPrincipal UserDetails userDetails) {

        UserReadingStatsResponse stats = progressService.getUserStats(
                userDetails.getUsername()
        );

        return ResponseEntity.ok(stats);
    }
}
