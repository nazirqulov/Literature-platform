package uz.literature.platform.service.interfaces;

import jakarta.validation.Valid;
import uz.literature.platform.payload.ApiResponse;
import uz.literature.platform.payload.request.CategoryRequestDto;
import uz.literature.platform.payload.response.CategoryDTO;

import java.util.List;

/**
 * Created by: Barkamol
 * DateTime: 2/4/2026 3:55 PM
 */
public interface CategoryService {

    List<CategoryDTO> read();

    ApiResponse<?> get(Long id);

    ApiResponse<String> create(@Valid CategoryRequestDto category);

    ApiResponse<String> update(Long id, CategoryDTO dto);
}
