package uz.literature.platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.literature.platform.entity.Category;
import uz.literature.platform.exception.BadRequestException;
import uz.literature.platform.payload.ApiResponse;
import uz.literature.platform.payload.request.CategoryRequestDto;
import uz.literature.platform.payload.response.CategoryDTO;
import uz.literature.platform.projection.CategoryProjection;
import uz.literature.platform.repository.CategoryRepository;
import uz.literature.platform.service.interfaces.CategoryService;

import java.util.List;
import java.util.Optional;

/**
 * Created by: Barkamol
 * DateTime: 2/4/2026 3:55 PM
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDTO> read() {

        List<CategoryProjection> read = categoryRepository.read();

        return read.stream()
                .map(c -> new CategoryDTO(
                        c.getName(),
                        c.getDescription()
                )).toList();
    }

    @Override
    public ApiResponse<?> get(Long id) {

        Optional<Category> optionalCategory = categoryRepository.findById(id);

        if (optionalCategory.isPresent()) {

            Category category = optionalCategory.get();

            CategoryDTO categoryDTO = new CategoryDTO(
                    category.getName(),
                    category.getDescription()
            );

            return ApiResponse.success(categoryDTO);
        }
        
        throw new BadRequestException("Category not found with id: " + id);
    }

    @Override
    public ApiResponse<String> create(CategoryRequestDto dto) {

        if (categoryRepository.existsByNameIgnoreCase(dto.getCategoryName())) {

            throw new BadRequestException("Category already exists");
        }

        Category category = new Category();

        category.setName(dto.getCategoryName());

        category.setDescription(dto.getCategoryDescription());

        categoryRepository.save(category);

        return ApiResponse.success("Category created successfully");

    }

    @Override
    public ApiResponse<String> update(Long id, CategoryDTO dto) {

        Optional<Category> optionalCategory = categoryRepository.findById(id);

        if (optionalCategory.isPresent()) {

            Category category = optionalCategory.get();

            category.setName(dto.getCategoryName());

            category.setDescription(dto.getCategoryDescription());

            return ApiResponse.success("Category updated successfully");
        }
        throw new BadRequestException("Category not found with id:" + id);
    }
}
