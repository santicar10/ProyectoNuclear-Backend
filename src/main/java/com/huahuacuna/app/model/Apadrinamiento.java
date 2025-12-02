package com.huahuacuna.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "apadrinamientos")
@Getter
@Setter
public class Apadrinamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_apadrinamiento")
    private Integer idApadrinamiento;

    @ManyToOne
    @JoinColumn(name = "id_padrino", nullable = false)
    private Usuario padrino;

    @ManyToOne
    @JoinColumn(name = "id_nino", nullable = false)
    private Nino nino;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio = LocalDate.now();

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoApadrinamiento estado = EstadoApadrinamiento.Activo;

    public enum EstadoApadrinamiento {
        Activo,
        Finalizado,
        Pendiente
    }
}