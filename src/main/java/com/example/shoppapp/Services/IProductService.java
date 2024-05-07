package com.example.shoppapp.Services;

import com.example.shoppapp.Models.Product;
import com.example.shoppapp.Models.ProductImage;
import com.example.shoppapp.dto.ProductDTO;
import com.example.shoppapp.dto.ProductImageDTO;
import com.example.shoppapp.exception.DataNotFoundException;
import com.example.shoppapp.exception.InvalidParamException;
import com.example.shoppapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IProductService {
     Product createProduct(ProductDTO productDTO) throws DataNotFoundException;

    Product getProductById(long id) throws Exception;

    Page<ProductResponse> getAllProduct( String key, Long categoryId,PageRequest pageRequest);

    Product updateProduct(long id,ProductDTO productDTO) throws Exception;

    void deleteProduct(long id);

    boolean existsByName(String name);

     ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO) throws DataNotFoundException, InvalidParamException;

    List<Product> findProductsByIds(List<Long> productIds);
    List<Product> findProductsByPrice(Float priceMax,Float priceMin);

}
