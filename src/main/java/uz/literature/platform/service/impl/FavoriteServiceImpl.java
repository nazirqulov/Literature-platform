package uz.literature.platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.literature.platform.payload.response.BookResponse;
import uz.literature.platform.entity.Book;
import uz.literature.platform.entity.Favorite;
import uz.literature.platform.entity.User;
import uz.literature.platform.exception.BadRequestException;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.repository.BookRepository;
import uz.literature.platform.repository.FavoriteRepository;
import uz.literature.platform.service.interfaces.FavoriteService;
import uz.literature.platform.service.interfaces.UserService;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final BookRepository bookRepository;

    private final UserService userService;

    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    public void addToFavorites(Long bookId) {
        User currentUser = userService.getCurrentUserEntity();
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));
        
        if (favoriteRepository.existsByUserIdAndBookId(currentUser.getId(), bookId)) {
            throw new BadRequestException("Bu kitob allaqachon sevimlilar ro'yxatida");
        }
        
        Favorite favorite = new Favorite();
        favorite.setUser(currentUser);
        favorite.setBook(book);
        
        favoriteRepository.save(favorite);
    }
    
    @Override
    @Transactional
    public void removeFromFavorites(Long bookId) {
        User currentUser = userService.getCurrentUserEntity();
        
        if (!favoriteRepository.existsByUserIdAndBookId(currentUser.getId(), bookId)) {
            throw new ResourceNotFoundException("Bu kitob sevimlilar ro'yxatida emas");
        }
        
        favoriteRepository.deleteByUserIdAndBookId(currentUser.getId(), bookId);
    }
    
    @Override
    public Page<BookResponse> getFavoriteBooks(Pageable pageable) {
        User currentUser = userService.getCurrentUserEntity();
        
        Page<Favorite> favorites = favoriteRepository.findByUserId(currentUser.getId(), pageable);
        
        return favorites.map(favorite -> {
            BookResponse response = modelMapper.map(favorite.getBook(), BookResponse.class);
            response.setIsFavorite(true);
            return response;
        });
    }
    
    @Override
    public boolean isFavorite(Long bookId) {
        try {
            User currentUser = userService.getCurrentUserEntity();
            return favoriteRepository.existsByUserIdAndBookId(currentUser.getId(), bookId);
        } catch (Exception e) {
            return false;
        }
    }
}
