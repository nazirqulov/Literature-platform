package uz.literature.platform.service.interfaces;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.literature.platform.payload.ApiResponse;
import uz.literature.platform.payload.request.CategoryCreateRequestDto;
import uz.literature.platform.payload.request.CategoryParentRequestDto;
import uz.literature.platform.payload.request.CategoryRequestDto;
import uz.literature.platform.payload.response.CategoryDTO;
import uz.literature.platform.payload.response.CategoryDataDto;

import java.util.List;

/**
 * Created by: Barkamol
 * DateTime: 2/4/2026 3:55 PM
 */
public interface CategoryService {

//    List<CategoryDTO> read();
//
//    ApiResponse<?> get(Long id);
//
//    ApiResponse<String> create(@Valid CategoryRequestDto category);
//
//    ApiResponse<String> update(Long id, CategoryDTO dto);

    CategoryDataDto createCategoryWithSubCategories(@Valid CategoryCreateRequestDto categoryDto);

    CategoryDataDto getById(Long id);

    Page<CategoryDataDto> getAll(Pageable pageable);

//    CategoryDataDto update(Long id, CategoryDTO categoryDto);

    CategoryDataDto update(Long id, CategoryCreateRequestDto dto);

    void delete(Long id);

    Page<CategoryDTO> searchByName(String name, Pageable pageable);

//    List<CategoryDataDto> getChildren(Long parentId);

    ApiResponse<String> createParent(@Valid CategoryParentRequestDto category);
}
