package uz.literature.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.literature.platform.entity.Category;
import uz.literature.platform.payload.ApiResponse;
import uz.literature.platform.payload.request.CategoryRequestDto;
import uz.literature.platform.payload.response.CategoryDTO;
import uz.literature.platform.payload.response.CategoryResponse;
import uz.literature.platform.repository.CategoryRepository;
import uz.literature.platform.service.interfaces.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CategoryController {


    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    private final CategoryService categoryService;

    @GetMapping("/read")
    public ResponseEntity<?> getAllCategories() {

        List<CategoryDTO> dtos = categoryService.read();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {

        ApiResponse<?> dto = categoryService.get(id);

        return ResponseEntity.ok(dto);

    }

    @PostMapping("/create")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequestDto category) {

        ApiResponse<String> response = categoryService.create(category);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDTO dto) {

        ApiResponse<String> update = categoryService.update(id, dto);

        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new uz.literature.platform.exception.ResourceNotFoundException("Kategoriya topilmadi"));
        categoryRepository.delete(category);
        return ResponseEntity.noContent().build();
    }

    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = modelMapper.map(category, CategoryResponse.class);
        response.setBooksCount(category.getBooks() != null ? category.getBooks().size() : 0);
        return response;
    }
}
