package uz.literature.platform.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.literature.platform.payload.request.ReviewRequest;
import uz.literature.platform.payload.response.ReviewResponse;

public interface ReviewService {
    
    ReviewResponse createReview(ReviewRequest request);
    
    ReviewResponse updateReview(Long id, ReviewRequest request);
    
    void deleteReview(Long id);
    
    ReviewResponse getReviewById(Long id);
    
    Page<ReviewResponse> getReviewsByBook(Long bookId, Pageable pageable);
    
    Page<ReviewResponse> getReviewsByCurrentUser(Pageable pageable);
}
