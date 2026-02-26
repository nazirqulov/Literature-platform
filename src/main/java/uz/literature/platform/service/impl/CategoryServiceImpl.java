package uz.literature.platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.literature.platform.entity.Category;
import uz.literature.platform.entity.SubCategory;
import uz.literature.platform.exception.BadRequestException;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.payload.ApiResponse;
import uz.literature.platform.payload.request.CategoryCreateRequestDto;
import uz.literature.platform.payload.request.CategoryParentRequestDto;
import uz.literature.platform.payload.request.ParentData;
import uz.literature.platform.payload.response.CategoryDTO;
import uz.literature.platform.payload.response.CategoryDataDto;
import uz.literature.platform.repository.CategoryRepository;
import uz.literature.platform.repository.SubCategoryRepository;
import uz.literature.platform.service.interfaces.CategoryService;

import java.util.Collections;
import java.util.List;

/**
 * Created by: Barkamol
 * DateTime: 2/4/2026 3:55 PM
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    @Override
    @Transactional
    public CategoryDataDto createCategoryWithSubCategories(CategoryCreateRequestDto dto) {

        if (categoryRepository.existsByNameIgnoreCaseAndDeletedFalse(dto.getCategoryName())) {
            throw new BadRequestException("Category already exists");
        }

        Category category = new Category();
        category.setName(dto.getCategoryName());
        category.setDescription(dto.getCategoryDescription());

        if (dto.getSubCategories() != null && !dto.getSubCategories().isEmpty()) {
            for (ParentData subDto : dto.getSubCategories()) {
                if (subDto.getName() == null || subDto.getName().isBlank()) continue;

                if (subCategoryRepository.existsByNameIgnoreCaseAndDeletedFalse(subDto.getName())) {
                    throw new BadRequestException("SubCategory '" + subDto.getName() + "' already exists");
                }

                SubCategory sub = new SubCategory();
                sub.setName(subDto.getName());
                sub.setDescription(subDto.getDescription());
                sub.setCategory(category);

                category.getSubCategories().add(sub);
            }
        }

        Category savedCategory = categoryRepository.save(category);

        return mapToDtoWithChildren(savedCategory);
    }

    @Override
    public CategoryDataDto getById(Long id) {
        return mapToDtoWithChildren(findById(id));
    }

    @Override
    public Page<CategoryDataDto> getAll(Pageable pageable) {
        return categoryRepository.findRootCategories(pageable)
                .map(this::mapToDtoWithChildren);
    }

    @Override
    @Transactional
    public CategoryDataDto update(Long id, CategoryCreateRequestDto dto) {
        Category category = findById(id);

        if (!category.getName().equals(dto.getCategoryName()) &&
                categoryRepository.existsByNameIgnoreCaseAndDeletedFalse(dto.getCategoryName())) {
            throw new BadRequestException("Duplicate category name");
        }

        category.setName(dto.getCategoryName());
        category.setDescription(dto.getCategoryDescription());

        if (dto.getSubCategories() != null) {
            category.getSubCategories().clear();
            for (ParentData subDto : dto.getSubCategories()) {
                if (subDto.getName() == null || subDto.getName().isBlank()) continue;

                SubCategory sub = new SubCategory();
                sub.setName(subDto.getName());
                sub.setDescription(subDto.getDescription());
                sub.setCategory(category);

                category.getSubCategories().add(sub);
            }
        }

        Category saved = categoryRepository.save(category);
        return mapToDtoWithChildren(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = findById(id);

        if (!category.getSubCategories().isEmpty())
            throw new BadRequestException("Category has subcategories");

        categoryRepository.delete(category);
    }

    private Category findById(Long id) {
        return categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private CategoryDataDto mapToDtoWithChildren(Category c) {
        CategoryDataDto dto = new CategoryDataDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setDescription(c.getDescription());

        var subs = c.getSubCategories();
        boolean hasChildren = subs != null && !subs.isEmpty();
        dto.setHasChildren(hasChildren);

        List<CategoryDTO> children = hasChildren
                ? subs.stream().map(child -> {
            CategoryDTO childDto = new CategoryDTO();
            childDto.setId(child.getId());
            childDto.setName(child.getName());
            childDto.setDescription(child.getDescription());
            return childDto;
        }).toList()
                : Collections.emptyList();
        dto.setChildren(children);
        return dto;
    }

    //    @Override
//    public List<CategoryDTO> read() {
//
//        List<CategoryProjection> read = categoryRepository.read();
//
//        return read.stream()
//                .map(c -> new CategoryDTO(
//                        c.getName(),
//                        c.getDescription()
//                )).toList();
//    }
//
//    @Override
//    public ApiResponse<?> get(Long id) {
//
//        Optional<Category> optionalCategory = categoryRepository.findById(id);
//
//        if (optionalCategory.isPresent()) {
//
//            Category category = optionalCategory.get();
//
//            CategoryDTO categoryDTO = new CategoryDTO(
//                    category.getName(),
//                    category.getDescription()
//            );
//
//            return ApiResponse.success(categoryDTO);
//        }
//
//        throw new BadRequestException("Category not found with id: " + id);
//    }
//
//    @Override
//    public ApiResponse<String> create(CategoryRequestDto dto) {
//
//        if (categoryRepository.existsByNameIgnoreCase(dto.getCategoryName())) {
//
//            throw new BadRequestException("Category already exists");
//        }
//
//        Category category = new Category();
//
//        category.setName(dto.getCategoryName());
//
//        category.setDescription(dto.getCategoryDescription());
//
//        categoryRepository.save(category);
//
//        return ApiResponse.success("Category created successfully");
//
//    }
//
//    @Override
//    public ApiResponse<String> update(Long id, CategoryDTO dto) {
//
//        Optional<Category> optionalCategory = categoryRepository.findById(id);
//
//        if (optionalCategory.isPresent()) {
//
//            Category category = optionalCategory.get();
//
//            category.setName(dto.getCategoryName());
//
//            category.setDescription(dto.getCategoryDescription());
//
//            return ApiResponse.success("Category updated successfully");
//        }
//        throw new BadRequestException("Category not found with id:" + id);
////    }
//    @Override
//    public CategoryDataDto create(CategoryCreateRequestDto dto) {
//
//        if (categoryRepository.existsByNameIgnoreCase(dto.getCategoryName()))
//            throw new BadRequestException("Category already exists");
//
//        Category parent = null;
//
//        if (dto.getParentId() != null) {
//            parent = findById(dto.getParentId());
//        } else if (dto.getParentName() != null) {
//            parent = categoryRepository.findByNameIgnoreCase(dto.getParentName())
//                    .orElseGet(() -> {
//                        Category p = new Category();
//                        p.setName(dto.getParentName());
//                        return categoryRepository.save(p);
//                    });
//        }
//
//        Category category = new Category();
//        category.setName(dto.getName());
//        category.setDescription(dto.getDescription());
//        category.setParent(parent);
//
//        return mapToDto(categoryRepository.save(category));

    /// /    }
//@Override
//@Transactional
//public CategoryDataDto create(CategoryCreateRequestDto dto) {
//
//    // 1️⃣ Category nomi tekshiruvi
//    if (categoryRepository.existsByNameIgnoreCase(dto.getCategoryName())) {
//        throw new BadRequestException("Category already exists");
//    }
//
//    // 2️⃣ Parentlar ro'yxati
//    Category lastParent = null; // oxirgi parentni yangi category ga bog‘lash uchun
//
//    if (dto.getParents() != null && !dto.getParents().isEmpty()) {
//        for (ParentData parentDto : dto.getParents()) {
//
//            if (parentDto.getParentName() == null || parentDto.getParentName().isBlank()) {
//                continue; // agar nom bo‘lmasa, o‘tkazib yuboradi
//            }
//
//            // Parent mavjudligini tekshirish
//            Category parent = categoryRepository.findByNameIgnoreCase(parentDto.getParentName())
//                    .orElseGet(() -> {
//                        Category p = new Category();
//                        p.setName(parentDto.getParentName());
//                        p.setDescription(parentDto.getParentDescription());
//                        return categoryRepository.save(p);
//                    });
//
//            lastParent = parent; // keyingi category uchun parent sifatida oxirgisini saqlaymiz
//        }
//    }
//
//    // 3️⃣ Yangi Category yaratish
//    Category category = new Category();
//    category.setName(dto.getCategoryName());
//    category.setDescription(dto.getCategoryDescription());
//    category.setParent(lastParent); // oxirgi parentni bog‘laymiz
//
//    // 4️⃣ Saqlash va DTO ga map qilish
//    return mapToDto(categoryRepository.save(category));
//}
//
//    @Override
//    public CategoryDataDto getById(Long id) {
//        return mapToDtoWithChildren(findById(id));
//    }
//
//    @Override
//    public Page<CategoryDataDto> getAll(Pageable pageable) {
//        return categoryRepository.findByParentIsNull(pageable)
//                .map(this::mapToDto);
//    }
//
//
//    @Override
//    public CategoryDataDto update(Long id, CategoryRequestDto dto) {
//
//        Category category = findById(id);
//
//        if (!category.getName().equals(dto.getName()) &&
//                categoryRepository.existsByNameIgnoreCase(dto.getName()))
//            throw new BadRequestException("Duplicate name");
//
//        if (dto.getParentId() != null) {
//            if (dto.getParentId().equals(id))
//                throw new BadRequestException("Cannot set self as parent");
//
//            Category parent = findById(dto.getParentId());
//            if (isDescendant(category, parent))
//                throw new BadRequestException("Cycle detected");
//
//            category.setParent(parent);
//        } else {
//            category.setParent(null);
//        }
//
//        category.setName(dto.getName());
//        category.setDescription(dto.getDescription());
//
//        return mapToDto(categoryRepository.save(category));
//    }
//
//    @Override
//    public void delete(Long id) {
//        Category category = findById(id);
//
//        if (!category.getChildren().isEmpty())
//            throw new BadRequestException("Has subcategories");
//
//        if (!category.getBooks().isEmpty())
//            throw new BadRequestException("Has books");
//
//        categoryRepository.delete(category);
//    }
    @Override
    public Page<CategoryDTO> searchByName(String name, Pageable pageable) {
        return subCategoryRepository.search(name, pageable)
                .map(this::mapToDto);
    }

    //
//    @Override
//    public List<CategoryDataDto> getChildren(Long parentId) {
//        return categoryRepository.findByParent_Id(parentId)
//                .stream().map(this::mapToDto).toList();
//    }
    @Override
    @Transactional
    public ApiResponse<String> createParent(CategoryParentRequestDto dto) {

        if (dto == null) {
            throw new BadRequestException("Category data is null");
        }

        Category category = categoryRepository.findByIdAndDeletedFalse(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + dto.getCategoryId()));

        boolean exists = subCategoryRepository.existsByNameIgnoreCaseAndDeletedFalse(dto.getParentName());
        if (exists) {
            throw new BadRequestException("SubCategory with this name already exists");
        }

        SubCategory sub = new SubCategory();
        sub.setName(dto.getParentName());
        sub.setDescription(dto.getDescription());
        sub.setCategory(category);

        category.getSubCategories().add(sub);

        subCategoryRepository.save(sub);
        categoryRepository.save(category);

        return ApiResponse.success("Subcategory has been successfully created and linked to parent category");
    }

    private CategoryDTO mapToDto(SubCategory sc) {
        CategoryDTO dto = new CategoryDTO();
        dto.setName(sc.getName());
        dto.setDescription(sc.getDescription());
        return dto;
    }


//    @Override
//    @Transactional
//    public ApiResponse<String> createParent(CategoryParentRequestDto categoryDto) {
//
//        if (categoryDto == null) {
//            throw new BadRequestException("Category data is null");
//        }
//
//        Optional<Category> optionalCategory = categoryRepository.findById(categoryDto.getCategoryId());
//        if (optionalCategory.isEmpty()) {
//            throw new ResourceNotFoundException("Category not found with id " + categoryDto.getCategoryId());
//        }
//
//        Category category = optionalCategory.get();
//
//        if (categoryDto.getParentName() != null && !categoryDto.getParentName().isBlank()) {
//
//            Category parentCategory = categoryRepository
//                    .findByName(categoryDto.getParentName())
//                    .orElseGet(() -> {
//                        Category newParent = new Category();
//                        newParent.setName(categoryDto.getParentName());
//                        newParent.setDescription(categoryDto.getDescription());
//                        return categoryRepository.save(newParent);
//                    });
//
//            category.setParent(parentCategory);
//            categoryRepository.save(category);
//        }
//
//        return ApiResponse.success("Parent category successfully linked");
//    }
//
//    // ===== helpers =====
//
//    private Category findById(Long id) {
//        return categoryRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
//    }
//
//    private boolean isDescendant(Category category, Category parent) {
//        Category current = parent;
//        while (current != null) {
//            if (current.getId().equals(category.getId())) return true;
//            current = current.getParent();
//        }
//        return false;
//    }
//
//    private CategoryDataDto mapToDto(Category c) {
//        CategoryDataDto dto = new CategoryDataDto();
//        dto.setId(c.getId());
//        dto.setName(c.getName());
//        dto.setDescription(c.getDescription());
//        dto.setBooksCount((long) c.getBooks().size());
//        dto.setHasChildren(!c.getChildren().isEmpty());
//
//        if (c.getParent() != null) {
//            dto.setParentId(c.getParent().getId());
//            dto.setParentName(c.getParent().getName());
//        }
//        return dto;
//    }
//
//    private CategoryDataDto mapToDtoWithChildren(Category c) {
//        CategoryDataDto dto = mapToDto(c);
//        dto.setChildren(
//                c.getChildren().stream()
//                        .map(this::mapToDto)
//                        .toList()
//        );
//        return dto;
//    }
}
