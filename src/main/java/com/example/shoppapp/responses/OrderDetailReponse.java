package com.example.shoppapp.responses;

import com.example.shoppapp.Models.Order;
import com.example.shoppapp.Models.OrderDetail;
import com.example.shoppapp.Models.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailReponse {
    private Long id;


    @JsonProperty("order_id")
    private Long order;

    @JsonProperty("product_id")
    private Long product;

    private float price;

    @JsonProperty("number_of_products")
    private int numberOfProduct;

    @JsonProperty("total_money")
    private float totalMoney;

    private String color;
    public static OrderDetailReponse fromOrderDetail(OrderDetail orderDetail){
        return OrderDetailReponse.builder()
                .id(orderDetail.getId())
                .order(orderDetail.getOrder().getId())
                .product(orderDetail.getProduct().getId())
                .price(orderDetail.getPrice())
                .numberOfProduct(orderDetail.getNumberOfProduct())
                .totalMoney(orderDetail.getTotalMoney())
                .color(orderDetail.getColor())
                .build();
    }
}
