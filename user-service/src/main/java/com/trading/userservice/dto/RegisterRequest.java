package com.trading.userservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email is required.")
    @Email(message = "Email is in incorrect format.")
    private String email;

    @NotBlank(message = "password is required.")
    @Min(value = 8 , message = "Minimum lenght of password is 8 character required")
    private String password;

    @NotBlank(message = "first name is required.")
    private String firstName;

    @NotBlank(message = "last name is required.")
    private String lastName;

    private BigDecimal initialDeposit = BigDecimal.valueOf(10000);
}
