package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "Error:[username] Dato Mandatorio")
    private String username;

    @NotBlank(message = "Error:[password]Dato Mandatorio")
    @Size(min = 8, message = "Error:[password]Dato Invalido")
    private String password;

    // Getters y setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
