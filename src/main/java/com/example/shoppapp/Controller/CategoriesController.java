package com.example.shoppapp.Controller;

import com.example.shoppapp.Models.Category;
import com.example.shoppapp.Services.CategoryService;
import com.example.shoppapp.dto.CategoriDTO;
import com.example.shoppapp.responses.UpdateCategoryReponse;
import com.example.shoppapp.Components.LocalLizationUtils;
import com.example.shoppapp.utils.MessageKeys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoriesController {
    private final CategoryService categoryService;
    private final LocalLizationUtils lizationUtils;
    @Autowired
    public CategoriesController(CategoryService categoryService, LocalLizationUtils lizationUtils) {
        this.categoryService = categoryService;
        this.lizationUtils = lizationUtils;

    }



    @PostMapping("")
    public ResponseEntity<?>createCategoris(@Valid @RequestBody CategoriDTO categoriDTO, BindingResult result){
        if(result.hasErrors()){
            List<String> errorr= result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
            return ResponseEntity.badRequest().body(errorr);
        }
        categoryService.createCategory(categoriDTO);
        return ResponseEntity.ok("insert category success"+categoriDTO);
    }
    @GetMapping("")
    public ResponseEntity<List<Category>>getAllCategoris(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit){
        List<Category> categories=categoryService.getAllCategory();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateCategoryReponse>upadteCategoris(
            @PathVariable("id") Long id,
            @RequestBody
            CategoriDTO categoriDTO){
        categoryService.updateCategory(id,categoriDTO);

        return ResponseEntity.ok(UpdateCategoryReponse.builder().message(lizationUtils.getLocalzedMessage(MessageKeys.UPDATE_SUCCESSFULLY)).build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String>deleteCategoris(@PathVariable("id") int id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("delete category"+id);
    }
}
