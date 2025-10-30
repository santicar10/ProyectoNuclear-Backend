package com.huahuacuna.app.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecuperarRequestDTO {
    @NotBlank
    @Email
    private String correo;
}