package com.huahuacuna.app.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecuperarDTO {

    @NotBlank(message = "El correo no puede estar vacío")
    private String correo;
}


