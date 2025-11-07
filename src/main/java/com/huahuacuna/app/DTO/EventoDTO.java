package com.huahuacuna.app.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoDTO {
    private Long id;

    @NotBlank(message = "El título del evento es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    private String horario;
    private String lugar;
    private String imagenUrl;
    private String descripcionDetallada;
    private LocalDateTime fechaEvento;
    private Boolean activo;
}