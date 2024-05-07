package com.example.shoppapp.Reponsitories;

import com.example.shoppapp.Models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageReponsitory extends JpaRepository<ProductImage,Long> {
    List<ProductImage> findByProductId(Long productId);
}
