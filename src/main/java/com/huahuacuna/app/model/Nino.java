package com.huahuacuna.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

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
    private Integer idNino;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

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

    /**
     * Método getter que calcula la edad dinámicamente
     * No se persiste en la BD
     */
    @Transient 
    public Integer getEdad() {
        if (this.fechaNacimiento == null) {
            return 0;
        }
        return Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
    }

    public enum EstadoApadrinamiento {
        Disponible,
        Apadrinado,
        Inactivo
    }
}