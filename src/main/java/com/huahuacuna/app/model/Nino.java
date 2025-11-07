package com.huahuacuna.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entidad que representa a un niño registrado en la fundación.
 */
@Entity
@Table(name = "ninos")
@Getter
@Setter
public class Nino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_nino;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Integer edad;

    @Column(nullable = false)
    private String genero;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_apadrinamiento", nullable = false)
    private EstadoApadrinamiento estadoApadrinamiento = EstadoApadrinamiento.Disponible;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro = LocalDate.now();

    public enum EstadoApadrinamiento {
        Disponible,
        Apadrinado,
        Inactivo
    }
}

