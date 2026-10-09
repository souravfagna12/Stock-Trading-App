package com.trading.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email is required.")
    @Email(message = "Email is in incorrect format.")
    private String email;

    @NotBlank(message = "password is required.")
    @Min(value = 8 , message = "Minimum lenght of password is 8 character required")
    private String password;
}
