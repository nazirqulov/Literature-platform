package uz.literature.platform.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.literature.platform.payload.response.AuthorResponse;
import uz.literature.platform.entity.Author;
import uz.literature.platform.repository.AuthorRepository;

@RestController
@RequestMapping("/api/authors")
@CrossOrigin(origins = "*")
public class AuthorController {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Page<AuthorResponse>> getAllAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Author> authors = authorRepository.findAll(pageable);
        Page<AuthorResponse> response = authors.map(this::mapToResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new uz.literature.platform.exception.ResourceNotFoundException("Muallif topilmadi"));
        return ResponseEntity.ok(mapToResponse(author));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponse> createAuthor(@RequestBody Author author) {
        Author savedAuthor = authorRepository.save(author);
        return ResponseEntity.ok(mapToResponse(savedAuthor));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponse> updateAuthor(@PathVariable Long id, @RequestBody Author authorDetails) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new uz.literature.platform.exception.ResourceNotFoundException("Muallif topilmadi"));

        author.setName(authorDetails.getName());
        author.setBiography(authorDetails.getBiography());
        author.setBirthDate(authorDetails.getBirthDate());
        author.setDeathDate(authorDetails.getDeathDate());
        author.setNationality(authorDetails.getNationality());
        author.setProfileImage(authorDetails.getProfileImage());

        Author updatedAuthor = authorRepository.save(author);
        return ResponseEntity.ok(mapToResponse(updatedAuthor));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new uz.literature.platform.exception.ResourceNotFoundException("Muallif topilmadi"));
        authorRepository.delete(author);
        return ResponseEntity.noContent().build();
    }

    private AuthorResponse mapToResponse(Author author) {
        AuthorResponse response = modelMapper.map(author, AuthorResponse.class);
        response.setBooksCount(author.getBooks() != null ? author.getBooks().size() : 0);
        return response;
    }
}
