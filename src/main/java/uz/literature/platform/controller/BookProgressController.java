package uz.literature.platform.controller;

/**
 * Created by: Barkamol
 * DateTime: 2/9/2026 10:50 AM
 */

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import uz.literature.platform.payload.request.UpdateProgressRequest;
import uz.literature.platform.payload.response.BookProgressResponse;
import uz.literature.platform.service.impl.BookProgressService;

import java.util.HashMap;
import java.util.Map;

/**
 * Kitob o'qish progressini boshqarish uchun controller
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookProgressController {

    private final BookProgressService progressService;

    /**
     * Kitob o'qishni boshlash
     * POST /api/books/{bookId}/start
     *
     * @param userDetails - authentication principal
     * @param bookId - kitob ID
     * @return BookProgressResponse
     */
    @PostMapping("/{bookId}/start")
    public ResponseEntity<BookProgressResponse> startReading(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long bookId) {

        BookProgressResponse response = progressService.startReading(
                userDetails.getUsername(),
                bookId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Kitob progressini olish
     * GET /api/books/{bookId}/progress
     *
     * @param userDetails - authentication principal
     * @param bookId - kitob ID
     * @return BookProgressResponse
     */
    @GetMapping("/{bookId}/progress")
    public ResponseEntity<BookProgressResponse> getProgress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long bookId) {

        BookProgressResponse response = progressService.getBookProgress(
                userDetails.getUsername(),
                bookId
        );

        return ResponseEntity.ok(response);
    }

    /**
     * O'qish progressini yangilash
     * PUT /api/books/{bookId}/progress
     *
     * Request body:
     * {
     *   "currentPage": 45,
     *   "currentChapter": 3
     * }
     *
     * @param userDetails - authentication principal
     * @param bookId - kitob ID
     * @param request - yangilash ma'lumotlari
     * @return BookProgressResponse
     */
    @PutMapping("/{bookId}/progress")
    public ResponseEntity<BookProgressResponse> updateProgress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long bookId,
            @Valid @RequestBody UpdateProgressRequest request) {

        BookProgressResponse response = progressService.updateProgress(
                userDetails.getUsername(),
                bookId,
                request
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Kitobni sevimli qilish yoki sevimlilardan olib tashlash
     * POST /api/books/{bookId}/favorite
     *
     * @param userDetails - authentication principal
     * @param bookId - kitob ID
     * @return BookProgressResponse
     */
    @PostMapping("/{bookId}/favorite")
    public ResponseEntity<BookProgressResponse> toggleFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long bookId) {

        BookProgressResponse response = progressService.toggleFavorite(
                userDetails.getUsername(),
                bookId
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Kitobga reyting qo'yish
     * POST /api/books/{bookId}/rating
     *
     * Request body:
     * {
     *   "rating": 5,
     *   "review": "Juda zo'r kitob!"
     * }
     *
     * @param userDetails - authentication principal
     * @param bookId - kitob ID
     * @param request - reyting va sharh
     * @return BookProgressResponse
     */
    @PostMapping("/{bookId}/rating")
    public ResponseEntity<BookProgressResponse> addRating(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long bookId,
            @RequestBody Map<String, Object> request) {

        Integer rating = (Integer) request.get("rating");
        String review = (String) request.get("review");

        BookProgressResponse response = progressService.addRating(
                userDetails.getUsername(),
                bookId,
                rating,
                review
        );

        return ResponseEntity.ok(response);
    }

    /**
     * O'qish sessiyasini boshlash
     * POST /api/books/sessions/start
     *
     * Request body:
     * {
     *   "bookId": 1,
     *   "currentPage": 10
     * }
     *
     * @param userDetails - authentication principal
     * @param request - sessiya ma'lumotlari
     * @return sessionId
     */
    @PostMapping("/sessions/start")
    public ResponseEntity<Map<String, Object>> startSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> request) {

        Long bookId = ((Number) request.get("bookId")).longValue();
        Integer currentPage = (Integer) request.get("currentPage");

        Long sessionId = progressService.startReadingSession(
                userDetails.getUsername(),
                bookId,
                currentPage
        );

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("message", "Sessiya muvaffaqiyatli boshlandi");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * O'qish sessiyasini tugatish
     * POST /api/books/sessions/end
     *
     * Request body:
     * {
     *   "sessionId": 123,
     *   "endPage": 50
     * }
     *
     * @param request - sessiya tugash ma'lumotlari
     * @return xabar
     */
    @PostMapping("/sessions/end")
    public ResponseEntity<Map<String, String>> endSession(
            @RequestBody Map<String, Object> request) {

        Long sessionId = ((Number) request.get("sessionId")).longValue();
        Integer endPage = (Integer) request.get("endPage");

        progressService.endReadingSession(sessionId, endPage);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Sessiya muvaffaqiyatli tugatildi");

        return ResponseEntity.ok(response);
    }

    /**
     * Faol sessiyani tugatish (browser yopilganda)
     * POST /api/books/sessions/end-active
     *
     * Request body:
     * {
     *   "endPage": 50
     * }
     *
     * @param userDetails - authentication principal
     * @param request - tugash sahifasi
     * @return xabar
     */
    @PostMapping("/sessions/end-active")
    public ResponseEntity<Map<String, String>> endActiveSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Integer> request) {

        Integer endPage = request.get("endPage");
        progressService.endActiveSession(userDetails.getUsername(), endPage);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Faol sessiya tugatildi");

        return ResponseEntity.ok(response);
    }
}
