package uz.literature.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.literature.platform.payload.response.BookResponse;
import uz.literature.platform.service.interfaces.FavoriteService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    @PostMapping("/{bookId}")
    public ResponseEntity<Map<String, String>> addToFavorites(@PathVariable Long bookId) {
        favoriteService.addToFavorites(bookId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Kitob sevimlilarga qo'shildi");
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Map<String, String>> removeFromFavorites(@PathVariable Long bookId) {
        favoriteService.removeFromFavorites(bookId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Kitob sevimlilardan o'chirildi");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<Page<BookResponse>> getFavoriteBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponse> books = favoriteService.getFavoriteBooks(pageable);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/check/{bookId}")
    public ResponseEntity<Map<String, Boolean>> isFavorite(@PathVariable Long bookId) {
        boolean isFavorite = favoriteService.isFavorite(bookId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isFavorite", isFavorite);
        
        return ResponseEntity.ok(response);
    }
}
