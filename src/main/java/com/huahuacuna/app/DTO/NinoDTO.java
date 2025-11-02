package com.huahuacuna.app.DTO;


import com.huahuacuna.app.model.Nino.EstadoApadrinamiento;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO para la transferencia de datos de Nino.
 */
@Getter
@Setter
public class NinoDTO {

    private Integer id_nino;
    private String nombre;
    private Integer edad;
    private String genero;
    private String descripcion;
    private String fotoUrl;
    private EstadoApadrinamiento estadoApadrinamiento;
    private LocalDate fechaRegistro;
}

