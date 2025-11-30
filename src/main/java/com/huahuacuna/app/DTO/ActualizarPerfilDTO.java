package com.huahuacuna.app.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarPerfilDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    private String telefono;
    
    private String direccion;
}