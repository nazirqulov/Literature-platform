package uz.literature.platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.literature.platform.entity.Category;
import uz.literature.platform.entity.SubCategory;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.payload.request.SubCategoryUpdateDTO;
import uz.literature.platform.payload.response.SubCategoryResDTO;
import uz.literature.platform.repository.CategoryRepository;
import uz.literature.platform.repository.SubCategoryRepository;
import uz.literature.platform.service.interfaces.SubCategoryService;

import java.util.List;
import java.util.Optional;

/**
 * Created by: Barkamol
 * DateTime: 2/26/2026 11:06 AM
 */
@Service
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService {
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<SubCategoryResDTO> getAll() {

        List<SubCategory> allByCategoryDeleted = subCategoryRepository.getAllByCategoryDeleted(false);

        return allByCategoryDeleted.stream()
                .map(p -> new SubCategoryResDTO(p.getCategory().getId(), p.getName(), p.getDescription())).toList();
    }

    @Override
    public List<SubCategoryResDTO> create(SubCategoryResDTO request) {

        Optional<Category> byIdAndDeletedFalse = categoryRepository.findByIdAndDeletedFalse(request.getCategoryId());

        if (byIdAndDeletedFalse.isEmpty()) {
            throw new ResourceNotFoundException("Category not found with id: " + request.getCategoryId());
        }

        SubCategory subCategory = new SubCategory();

        subCategory.setName(request.getName());

        subCategory.setDescription(request.getDescription());

        subCategory.setCategory((byIdAndDeletedFalse.get()));

        SubCategory saved = subCategoryRepository.save(subCategory);

        return List.of(new SubCategoryResDTO(saved.getCategory().getId(), saved.getName(), saved.getDescription()));
    }

    @Override
    public SubCategoryUpdateDTO update(Long categoryId, Long subCategoryId, SubCategoryUpdateDTO dto) {

        Optional<Category> byIdAndDeletedFalse = categoryRepository.findByIdAndDeletedFalse(categoryId);

        if (byIdAndDeletedFalse.isEmpty()) {
            throw new ResourceNotFoundException("Category not found with id:" + categoryId);
        }

        Optional<SubCategory> byId = subCategoryRepository.findById(subCategoryId);

        if (byId.isEmpty()) {
            throw new ResourceNotFoundException("Subcategory not found with id:" + subCategoryId);
        }

        SubCategory subCategory = byId.get();

        subCategory.setName(dto.getName());

        subCategory.setDescription(dto.getDescription());

        subCategory.setCategory(byIdAndDeletedFalse.get());

        subCategoryRepository.save(subCategory);

        SubCategoryUpdateDTO dto1 = new SubCategoryUpdateDTO();

        dto1.setName(subCategory.getName());
        dto1.setDescription(subCategory.getDescription());

        return dto1;
    }
}
