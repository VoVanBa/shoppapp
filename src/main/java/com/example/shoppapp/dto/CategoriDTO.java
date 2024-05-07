package com.example.shoppapp.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoriDTO {
    @NotEmpty( message ="category not null")
    private String name;
}
