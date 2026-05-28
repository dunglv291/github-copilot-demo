package com.example.github_copilot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {

    private String accessToken;
    private String tokenType;
    private String email;
    private String name;

    public AuthResponseDTO(String accessToken, String email, String name) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.email = email;
        this.name = name;
    }
}
