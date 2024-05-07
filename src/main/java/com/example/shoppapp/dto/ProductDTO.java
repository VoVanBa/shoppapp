package com.example.shoppapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    @NotBlank(message = "title is required")
    @Size(min=3,max=200,message = "title must be betwwen 3 and 20")
    private String name;

    @Min(value = 0,message = "Price must be geater than or equal to 0")
    @Max(value = 1000000,message = "price must be less than or equal to 10,000,000")
    private Float price;
    private String thumbnail;
    private String description;
    @JsonProperty("category_id")
    private long categoryId;


}
