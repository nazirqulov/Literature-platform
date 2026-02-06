package uz.literature.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.literature.platform.entity.Category;
import uz.literature.platform.payload.ApiResponse;
import uz.literature.platform.payload.request.CategoryCreateRequestDto;
import uz.literature.platform.payload.request.CategoryParentRequestDto;
import uz.literature.platform.payload.response.CategoryDTO;
import uz.literature.platform.payload.response.CategoryDataDto;
import uz.literature.platform.payload.response.CategoryResponse;
import uz.literature.platform.repository.CategoryRepository;
import uz.literature.platform.service.interfaces.CategoryService;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CategoryController {


    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    private final CategoryService categoryService;

    @GetMapping("/read")
    public ResponseEntity<?> getAllCategories(@RequestParam(required = false, defaultValue = "0") int page,
                                              @RequestParam(required = false, defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<CategoryDataDto> dtos = categoryService.getAll(pageable);

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {


        CategoryDataDto byId = categoryService.getById(id);

        return ResponseEntity.ok(byId);

    }

    @PostMapping("/create")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryCreateRequestDto category) {

        CategoryDataDto response = categoryService.createCategoryWithSubCategories(category);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-parent-category")
    public ResponseEntity<?> createParentCategory(@Valid @RequestBody CategoryParentRequestDto category) {

        ApiResponse<String> parent = categoryService.createParent(category);

        return ResponseEntity.ok(parent);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryCreateRequestDto dto) {

        CategoryDataDto update = categoryService.update(id, dto);

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

//    private CategoryResponse mapToResponse(Category category) {
//        CategoryResponse response = modelMapper.map(category, CategoryResponse.class);
//        response.setBooksCount(category.getBooks() != null ? category.getBooks().size() : 0);
//        return response;
//    }
//    @GetMapping("/{id}/children")
//    public ResponseEntity<?> children(@PathVariable Long id) {
//        return ResponseEntity.ok(categoryService.getChildren(id));
//    }


    @GetMapping("/search")
    public ResponseEntity<?>getSearch(@RequestParam(required = false)String search,
                                      @RequestParam(required = false,defaultValue = "0")int page,
                                      @RequestParam(required = false,defaultValue = "10")int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<CategoryDTO> dtos = categoryService.searchByName(search, pageable);

        return ResponseEntity.ok(dtos);
    }
}
