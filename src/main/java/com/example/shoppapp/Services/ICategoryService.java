package com.example.shoppapp.Services;

import com.example.shoppapp.Models.Category;
import com.example.shoppapp.dto.CategoriDTO;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoriDTO categoriDTO);
    Category getCategoryById(long id);
    List<Category> getAllCategory();
    Category updateCategory(long id,CategoriDTO categoriDTO);
    void deleteCategory(long id);

}
