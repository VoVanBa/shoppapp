package com.example.shoppapp.responses;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductListReponse {
    private List<ProductResponse> products;
    private int totalPages;
}
