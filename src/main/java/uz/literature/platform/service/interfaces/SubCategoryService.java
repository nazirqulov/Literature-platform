package uz.literature.platform.service.interfaces;

import uz.literature.platform.payload.request.SubCategoryUpdateDTO;
import uz.literature.platform.payload.response.SubCategoryResDTO;

import java.util.List;

/**
 * Created by: Barkamol
 * DateTime: 2/26/2026 11:06 AM
 */
public interface SubCategoryService {
    List<SubCategoryResDTO> getAll();

    List<SubCategoryResDTO> create(SubCategoryResDTO request);

    SubCategoryUpdateDTO update(Long categoryId, Long subCategoryId, SubCategoryUpdateDTO dto);
}
