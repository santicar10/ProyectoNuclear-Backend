// src/main/java/com/huahuacuna/app/DTO/BitacoraDTO.java
package com.huahuacuna.app.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BitacoraDTO {

    @NotNull(message = "El ID del niño es obligatorio")
    private Integer ninoId;
    
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    
    private String imagen;  // URL de imagen
}