package com.huahuacuna.app.DTO;

import com.huahuacuna.app.model.Apadrinamiento.EstadoApadrinamiento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApadrinamientoDTO {
    
    private Integer idApadrinamiento;
    private Integer idPadrino;
    private Integer idNino;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoApadrinamiento estado;
    
    // Datos adicionales para respuestas
    private String nombrePadrino;
    private String nombreNino;
    private String fotoNino;
    private Integer edadNino;
    private String generoNino;
    private String descripcionNino;
}