package com.huahuacuna.app.DTO;

import com.huahuacuna.app.model.Proyecto.EstadoProyecto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProyectoDTO {

    private String nombreProyecto;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoProyecto estado;
}


