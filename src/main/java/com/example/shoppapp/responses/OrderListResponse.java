package com.example.shoppapp.responses;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class OrderListResponse {
    private List<OrderReponse> orders;
    private int totalPages;
}
