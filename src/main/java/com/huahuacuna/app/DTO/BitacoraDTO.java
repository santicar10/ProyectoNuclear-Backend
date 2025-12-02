// src/main/java/com/huahuacuna/app/DTO/BitacoraDTO.java
package com.huahuacuna.app.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BitacoraDTO {
    
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    
    private String imagen;  // URL de imagen (opcional)
}