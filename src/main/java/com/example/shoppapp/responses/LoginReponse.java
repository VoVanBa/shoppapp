package com.example.shoppapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginReponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("token")
    private String token;

}
