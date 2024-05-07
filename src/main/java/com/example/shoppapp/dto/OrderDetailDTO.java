package com.example.shoppapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {

    @JsonProperty("order_id")
    @Min(value = 1,message = "User id must bt > 0")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1,message = "User id must bt > 0")
    private Long productId;

    @Min(value = 0,message = "price must bt >=0 ")
    private float price;
    
    @JsonProperty("number_of_products")
    @Min(value = 1,message = "number of products must bt >=0 ")
    private int numberOfProduct;

    @JsonProperty("total_money")
    @Min(value = 0,message = "Total money must bt >=0 ")
    private float totalMoney;
    private String color;
}
