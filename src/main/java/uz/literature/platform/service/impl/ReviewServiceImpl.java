package uz.literature.platform.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.literature.platform.payload.request.ReviewRequest;
import uz.literature.platform.payload.response.ReviewResponse;
import uz.literature.platform.payload.response.UserResponse;
import uz.literature.platform.entity.Book;
import uz.literature.platform.entity.Review;
import uz.literature.platform.entity.User;
import uz.literature.platform.exception.BadRequestException;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.exception.UnauthorizedException;
import uz.literature.platform.repository.BookRepository;
import uz.literature.platform.repository.ReviewRepository;
import uz.literature.platform.service.interfaces.ReviewService;
import uz.literature.platform.service.interfaces.UserService;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Kitob topilmadi"));
        
        // Check if user already reviewed this book
        if (reviewRepository.existsByUserIdAndBookId(currentUser.getId(), book.getId())) {
            throw new BadRequestException("Siz bu kitobga allaqachon sharh yozgansiz");
        }
        
        Review review = new Review();
        review.setUser(currentUser);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        Review savedReview = reviewRepository.save(review);
        
        // Update book rating
        updateBookRating(book);
        
        return mapToResponse(savedReview);
    }
    
    @Override
    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sharh topilmadi"));
        
        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Siz bu sharhni tahrirlash huquqiga ega emassiz");
        }
        
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        Review updatedReview = reviewRepository.save(review);
        
        // Update book rating
        updateBookRating(review.getBook());
        
        return mapToResponse(updatedReview);
    }
    
    @Override
    @Transactional
    public void deleteReview(Long id) {
        User currentUser = userService.getCurrentUserEntity();
        
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sharh topilmadi"));
        
        if (!review.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new UnauthorizedException("Siz bu sharhni o'chirish huquqiga ega emassiz");
        }
        
        Book book = review.getBook();
        reviewRepository.delete(review);
        
        // Update book rating
        updateBookRating(book);
    }
    
    @Override
    public ReviewResponse getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sharh topilmadi"));
        return mapToResponse(review);
    }
    
    @Override
    public Page<ReviewResponse> getReviewsByBook(Long bookId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByBookId(bookId, pageable);
        return reviews.map(this::mapToResponse);
    }
    
    @Override
    public Page<ReviewResponse> getReviewsByCurrentUser(Pageable pageable) {
        User currentUser = userService.getCurrentUserEntity();
        Page<Review> reviews = reviewRepository.findByUserId(currentUser.getId(), pageable);
        return reviews.map(this::mapToResponse);
    }
    
    private void updateBookRating(Book book) {
        List<Review> reviews = reviewRepository.findByBookId(book.getId(), Pageable.unpaged()).getContent();
        
        if (reviews.isEmpty()) {
            book.setAverageRating(0.0);
            book.setRatingCount(0);
        } else {
            double average = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            
            book.setAverageRating(Math.round(average * 10.0) / 10.0);
            book.setRatingCount(reviews.size());
        }
        
        bookRepository.save(book);
    }
    
    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setBookId(review.getBook().getId());
        response.setBookTitle(review.getBook().getTitle());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        
        UserResponse userResponse = modelMapper.map(review.getUser(), UserResponse.class);
        userResponse.setRole(review.getUser().getRole().name());
        response.setUser(userResponse);
        
        return response;
    }
}
