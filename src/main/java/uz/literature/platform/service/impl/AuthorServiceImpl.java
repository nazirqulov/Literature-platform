package uz.literature.platform.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.literature.platform.entity.Author;
import uz.literature.platform.payload.request.AuthorRequest;
import uz.literature.platform.payload.response.AuthorResponse;
import uz.literature.platform.repository.AuthorRepository;
import uz.literature.platform.service.interfaces.AuthorService;

import java.util.List;
import java.util.Optional;

/**
 * Created by: Barkamol
 * DateTime: 2/21/2026 5:07 PM
 */
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public List<AuthorResponse> getAllAuthors() {

        List<Author> authorList = authorRepository.findAll();

        return (authorList.stream()
                .map(this::getAuthorResponse)
                .toList());
    }

    @Override
    public AuthorResponse getAuthorById(Long id) {

        Author author = authorRepository.findById(id).orElseThrow();

        return getAuthorResponse(author);
    }

    @Override
    public AuthorResponse createAuthor(@Valid AuthorRequest request) {

        Author existingAuthor = authorRepository.findByName(request.getName());

        if (existingAuthor != null) {
            throw new IllegalArgumentException("Author with the same name already exists");
        }

        Author author = new Author();
        author.setName(request.getName());
        author.setBiography(request.getBiography());
        author.setBirthDate(request.getBirthDate());
        author.setDeathDate(request.getDeathDate());
        author.setNationality(request.getNationality());

        Author savedAuthor = authorRepository.save(author);

        return getAuthorResponse(savedAuthor);
    }

    @Override
    public String updateAuthor(Long id, AuthorRequest request) {

        Optional<Author> byId = authorRepository.findById(id);

        if (byId.isEmpty()){
            throw new IllegalArgumentException("Author not found");
        }

        Author author = byId.get();

        author.setName(request.getName());
        author.setBiography(request.getBiography());
        author.setBirthDate(request.getBirthDate());
        author.setDeathDate(request.getDeathDate());
        author.setNationality(request.getNationality());

        authorRepository.save(author);

        return "Author updated successfully";
    }

    @Override
    public void deleteAuthor(Long id) {
        Optional<Author> byId = authorRepository.findById(id);

        if (byId.isEmpty()){
            throw new IllegalArgumentException("Author not found");
        }

        Author author = byId.get();

        author.setDeleted(true);
    }

    @NotNull
    private AuthorResponse getAuthorResponse(Author author) {

        AuthorResponse response = new AuthorResponse();
        response.setId(author.getId());
        response.setName(author.getName());
        response.setBiography(author.getBiography());
        response.setBirthDate(author.getBirthDate());
        response.setDeathDate(author.getDeathDate());
        response.setNationality(author.getNationality());
        response.setProfileImage(author.getProfileImage());
        response.setBooksCount(author.getBooks().size());
        response.setCreatedAt(author.getCreatedAt());

        return response;
    }
}
