package uz.literature.platform.service.interfaces;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.literature.platform.payload.request.AuthorRequest;
import uz.literature.platform.payload.response.AuthorResponse;

import java.util.List;

/**
 * Created by: Barkamol
 * DateTime: 2/21/2026 5:07 PM
 */
public interface AuthorService {
    List<AuthorResponse> getAllAuthors();

    AuthorResponse getAuthorById(Long id);

    AuthorResponse createAuthor(@Valid AuthorRequest request);

    String updateAuthor(Long id, @Valid AuthorRequest request);

    void deleteAuthor(Long id);
}
