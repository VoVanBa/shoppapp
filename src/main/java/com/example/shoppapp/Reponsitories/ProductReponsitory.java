package com.example.shoppapp.Reponsitories;

import com.example.shoppapp.Models.Product;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReponsitory extends JpaRepository<Product,Long> {
  //kiểm tra xem product đó tồn tại chưa
    boolean existsByName(String name);
    
    Page<Product> findAll(Pageable pageable);
//dấu : cho bt giá trị tham số được cấp ở bên ngoài khi truy vấn được thực thi
  @Query("SELECT p FROM Product p WHERE " +
          "(:categoryId IS NULL OR :categoryId = 0 OR p.category.id = :categoryId) " +
          "AND (:keyword IS NULL OR TRIM(:keyword) = '' OR p.name LIKE %:keyword% OR p.description LIKE %:keyword%)"
  )
  Page<Product> searchProducts(
          @Param("categoryId") Long categoryId,
          @Param("keyword") String keyword,Pageable pageable
  );

  @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImages WHERE p.id = :productId")
  Optional<Product> getDetailProduct(@Param("productId") Long productId);

  //JOIN FETCH được sử dụng để thực hiện một truy vấn nối (join)
  // giữa các thực thể và đồng thời tải các mối quan hệ một cách tự động
  @Query("SELECT p FROM Product p WHERE p.id IN :productIds")
  List<Product> findProductsByIds(@Param("productIds") List<Long> productIds);

  @Query("SELECT p FROM Product p WHERE p.price >= :priceMin AND  p.price <= :priceMax")
  List<Product> findByPrice(@Param("priceMax") Float priceMax,
                            @Param("priceMin") Float priceMin);
}
