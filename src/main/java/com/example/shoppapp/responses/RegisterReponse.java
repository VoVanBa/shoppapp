package com.example.shoppapp.responses;

import com.example.shoppapp.Models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterReponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("user")
    private User user;
}
