// src/main/java/com/huahuacuna/app/DTO/BitacoraResponseDTO.java
package com.huahuacuna.app.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BitacoraResponseDTO {
    private Integer id;
    private String nombreNino;
    private String fecha;
    private String descripcion;
    private String imagen;
}