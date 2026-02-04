package uz.literature.platform.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.literature.platform.payload.response.BookResponse;

public interface FavoriteService {
    
    void addToFavorites(Long bookId);
    
    void removeFromFavorites(Long bookId);
    
    Page<BookResponse> getFavoriteBooks(Pageable pageable);
    
    boolean isFavorite(Long bookId);
}
