package com.example.shoppapp.Services;

import com.example.shoppapp.Models.Category;
import com.example.shoppapp.Models.Product;
import com.example.shoppapp.Models.ProductImage;
import com.example.shoppapp.Reponsitories.CategoryReponsitory;
import com.example.shoppapp.Reponsitories.ProductImageReponsitory;
import com.example.shoppapp.Reponsitories.ProductReponsitory;
import com.example.shoppapp.Reponsitories.UserReponsitory;
import com.example.shoppapp.dto.ProductDTO;
import com.example.shoppapp.dto.ProductImageDTO;
import com.example.shoppapp.exception.DataNotFoundException;
import com.example.shoppapp.exception.InvalidParamException;
import com.example.shoppapp.responses.ProductResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements IProductService {
    @Autowired
    private ProductReponsitory productReponsitory;

    @Autowired
    private CategoryReponsitory categoryReponsitory;

    @Autowired
    private ProductImageReponsitory productImageReponsitory;


    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory = categoryReponsitory.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException(
                        "can not find category with id" + productDTO.getCategoryId()));
        Product newproduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();
        return productReponsitory.save(newproduct);
    }

    @Override
    public Product getProductById(long id) throws Exception {
        return productReponsitory.findById(id)
                .orElseThrow(() -> new DataNotFoundException("can not found with id" + id));
    }

    @Override
    public Page<ProductResponse> getAllProduct(
            String key,
            Long categoryId,
            PageRequest pageRequest) {
        //lấy danh sách sane phẩm theo page và limit(giới hạn)
        Page<Product> productPage;
       productPage=productReponsitory.searchProducts(categoryId,key,pageRequest);
        return productPage
                .map(ProductResponse::fromProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(long id, ProductDTO productDTO) throws Exception {
        Product existingProduct = getProductById(id);
        if (existingProduct != null) {
            //copy các thuộc tính DTO->Product
            //có thể sử dụng ModelMappper
            Category existingCategory = categoryReponsitory.findById(existingProduct.getCategory().getId())
                    .orElseThrow(() -> new DataNotFoundException(
                            "can not find catefory with id" + productDTO.getCategoryId()));
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            return productReponsitory.save(existingProduct);

        }
        return null;
    }

    @Override
    @Transactional
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productReponsitory.findById(id);
        optionalProduct.ifPresent(product -> productReponsitory.delete(product));
    }

    @Override
    public boolean existsByName(String name) {
        return productReponsitory.existsByName(name);
    }

    @Override
    public ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO) throws DataNotFoundException, InvalidParamException {
        Product existingProduc = productReponsitory.findById(productId)
                .orElseThrow(() -> new DataNotFoundException(
                        "can not find product with id" + productImageDTO.getProductId()));

        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduc)
                .imageUrl(productImageDTO.getImageUrl())
                .build();

        //không cho insert quá 5 ảnh cho một sản phẩm
        int size = productImageReponsitory.findByProductId(productId).size();
        if (size >= ProductImage.MAXIMUM_IMAGE_PER_PRODUCT) {
            throw new InvalidParamException("Number of image must be >="
                    + ProductImage.MAXIMUM_IMAGE_PER_PRODUCT);
        }
        return productImageReponsitory.save(newProductImage);
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productReponsitory.findProductsByIds(productIds);
    }

    @Override
    public List<Product> findProductsByPrice(Float priceMax, Float priceMin) {
        return productReponsitory.findByPrice(priceMax,priceMin);
    }


}
