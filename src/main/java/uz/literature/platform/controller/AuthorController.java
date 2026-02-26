package uz.literature.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.literature.platform.payload.request.AuthorRequest;
import uz.literature.platform.payload.response.AuthorResponse;
import uz.literature.platform.service.interfaces.AuthorService;

import java.util.List;

/**
 * Created by: Barkamol
 * DateTime: 2/21/2026 5:06 PM
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllAuthors() {

        List<AuthorResponse> authors = authorService.getAllAuthors();

        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAuthorById(@PathVariable Long id) {

        AuthorResponse author = authorService.getAuthorById(id);

        return ResponseEntity.ok(author);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAuthor(@Valid @RequestBody AuthorRequest request) {

        AuthorResponse author = authorService.createAuthor(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(author);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAuthor(@PathVariable Long id, @RequestBody AuthorRequest request) {

        String response = authorService.updateAuthor(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable Long id) {

        authorService.deleteAuthor(id);

        return ResponseEntity.ok("Author deleted successfully");
    }
}
