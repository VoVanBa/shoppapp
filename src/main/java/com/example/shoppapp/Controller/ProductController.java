package com.example.shoppapp.Controller;


import com.example.shoppapp.Components.LocalLizationUtils;
import com.example.shoppapp.Models.Product;
import com.example.shoppapp.Models.ProductImage;
import com.example.shoppapp.Services.IProductService;
import com.example.shoppapp.dto.ProductDTO;
import com.example.shoppapp.dto.ProductImageDTO;
import com.example.shoppapp.exception.DataNotFoundException;
import com.example.shoppapp.responses.ProductListReponse;
import com.example.shoppapp.responses.ProductResponse;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;
    private final LocalLizationUtils lizationUtils;

    @GetMapping("")
    public ResponseEntity<ProductListReponse> getproducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page-1, limit, Sort.by("id").ascending());
        Page<ProductResponse> productPage = productService.getAllProduct(keyword,categoryId,pageRequest);

        //lấy toonge số trang
        int totalPages = productPage.getTotalPages();
        // List<ProductResponse> products là một biến được khai báo để lưu trữ danh sách các đối tượng ProductResponse.
        // Biến này được gán giá trị bằng kết quả trả về từ phương thức getContent() của đối tượng productPage.
        List<ProductResponse> products = productPage.getContent();

        return ResponseEntity.ok(ProductListReponse.builder()
                        .products(products)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImagẹ(@PathVariable String imageName) {
        try {
            Path imagePath = Paths.get("uploads/" + imageName);

            UrlResource resource = new UrlResource(imagePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/404.jpg").toUri()));
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<?> insertProduct(@Valid @RequestBody ProductDTO productDTO,
//                                             @ModelAttribute("files") List<MultipartFile> files,
                                           BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorr = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errorr);

            }
            Product newproduct = productService.createProduct(productDTO);

            return ResponseEntity.ok(newproduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadeImages(@PathVariable("id") Long productId,
                                           @RequestParam("files") List<MultipartFile> files) {

        try {
            Product existingProduct = productService.getProductById(productId);

            files = files == null ? new ArrayList<MultipartFile>() : files;
            if (files.size() > ProductImage.MAXIMUM_IMAGE_PER_PRODUCT) {
                return ResponseEntity.badRequest().body("only uploads maximun 5 image");
            }
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                //   kiểm tra kích thước file
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("file is large! Max size í 10MB ");
                }
//            lấy ra định dạng file
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("file must be image ");
                }
                // Đây là một phương thức được gọi để lưu trữ tệp (file)
                // . Biến file được truyền vào phương thức này như một đối số.
                String filename = storeFile(file);
                //lưu vào db
                ProductImage productImage = productService.createProductImage(existingProduct.getId(), ProductImageDTO.builder()
                        .imageUrl(filename)
                        .build()
                );
                productImages.add(productImage);
                //Lưu vào bẳng product_images
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    private boolean isImageFile(MultipartFile file) {
        String contenType = file.getContentType();
        return contenType != null && contenType.startsWith("image/");
    }

    //hàm lưu file
    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("invalid image format");
        }
        //đổi tên nếu không sẽ bị trùng
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        //thêm UUID vào trước tên file để đảm bảo file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;

        //đường dẫn đến thư mục chứa file
        Path uplaodDir = Paths.get("uploads");
        //kiểm tra thư mục upload đã tồn tại hay chưa
        if (!Files.exists(uplaodDir)) {
            Files.createDirectories(uplaodDir);
        }

        //đường dãn đầy đủ đến file
        Path destination = Paths.get(uplaodDir.toString(), uniqueFilename);
        //sao chép file vào thư mục đích
//        Files.copy(
//                file.getInputStream(),   // InputStream của tập tin nguồn
//                destination,             // Đường dẫn của tập tin đích
//                StandardCopyOption.REPLACE_EXISTING // Tùy chọn để ghi đè nếu tập tin đích đã tồn tại
//        );
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids) {
        //eg: 1,3,5,7
        try {
            // Tách chuỗi ids thành một mảng các số nguyên
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Product> products = productService.findProductsByIds(productIds);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getproductById(@PathVariable("id") int id) {

        try {
            Product existingProduct = productService.getProductById(id);
            return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> upadteproduct(
            @PathVariable("id") int id,
            @RequestBody ProductDTO productDTO
    ) {
        try {
            Product existtingProduct = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(existtingProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteproduct(@PathVariable("id") int id) throws Exception {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("delete success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/price")
    public ResponseEntity<?> getProducts(@RequestParam(name = "maxprice", required = false) Float maxPrice,
                                         @RequestParam(name = "minprice", required = false) Float minPrice) {
        try{
            List<Product> exitingProduct=productService.findProductsByPrice(maxPrice,minPrice);
            return ResponseEntity.ok(exitingProduct);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //    @PostMapping("/generateFakeProducts")
    private ResponseEntity<String> generateFakeProducts() {
        Faker faker = new Faker();
        for (int i = 0; i < 1000000; i++) {
            String productName = faker.commerce().productName();
            if (productService.existsByName(productName)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float) faker.number().numberBetween(10, 90_000_000))
                    .description(faker.lorem().sentence())
                    .categoryId((long) faker.number().numberBetween(2, 5))
                    .thumbnail("")
                    .build();
            try {
                productService.createProduct(productDTO);
            } catch (DataNotFoundException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("fake product create successfully");
    }
}