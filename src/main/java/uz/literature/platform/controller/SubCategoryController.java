package uz.literature.platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.literature.platform.payload.request.SubCategoryUpdateDTO;
import uz.literature.platform.payload.response.SubCategoryResDTO;
import uz.literature.platform.service.interfaces.SubCategoryService;

import java.util.List;

/**
 * Created by: Barkamol
 * DateTime: 2/26/2026 11:05 AM
 */
@RestController
@RequestMapping("/api/sub-category")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping("/get-all")
    public ResponseEntity<?>getAll(){

        List<SubCategoryResDTO>dtos=subCategoryService.getAll();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/create")
    public ResponseEntity<?>create(@RequestBody SubCategoryResDTO request){

         List<SubCategoryResDTO>dtos=   subCategoryService.create(request);

         return ResponseEntity.ok(dtos);
    }

    @PutMapping("/update/{categoryId}/{subCategoryId}")
    public ResponseEntity<?>update(@PathVariable Long categoryId,@PathVariable Long subCategoryId,@RequestBody(required = false) SubCategoryUpdateDTO dto){
        SubCategoryUpdateDTO dtos=subCategoryService.update(categoryId,subCategoryId,dto);
        return ResponseEntity.ok(dtos);
    }
}
