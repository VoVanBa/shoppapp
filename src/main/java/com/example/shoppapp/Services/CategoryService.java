package com.example.shoppapp.Services;

import com.example.shoppapp.Models.Category;
import com.example.shoppapp.Reponsitories.CategoryReponsitory;
import com.example.shoppapp.dto.CategoriDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements ICategoryService{

    private final CategoryReponsitory categoryReponsitory;
    @Autowired
    public CategoryService(CategoryReponsitory categoryReponsitory) {
        this.categoryReponsitory = categoryReponsitory;
    }

    @Override
    public Category createCategory(CategoriDTO categoriDTO) {
        Category newCategory=Category.builder()
                .name(categoriDTO.getName())
                .build();
        return this.categoryReponsitory.save(newCategory);
    }

    @Override
    public Category getCategoryById(long id) {
        return this.categoryReponsitory.findById(id).orElseThrow(()->
                new RuntimeException("category not found"));
    }

    @Override
    public List<Category> getAllCategory() {
        return this.categoryReponsitory.findAll();
    }

    @Override
    public Category updateCategory(long id, CategoriDTO categoriDTO) {
        Category exitsCategory= getCategoryById(id);
        exitsCategory.setName(categoriDTO.getName());
        categoryReponsitory.save(exitsCategory);
        return exitsCategory;
    }

    @Override
    public void deleteCategory(long id) {
        categoryReponsitory.deleteById(id);
    }
}
