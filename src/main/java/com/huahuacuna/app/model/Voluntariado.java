package com.huahuacuna.app.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "voluntariado")
@Getter
@Setter
public class Voluntariado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voluntariado")
    private Integer idVoluntariado;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @Column(name = "rol_voluntario")
    private String rolVoluntario;

    @Column(name = "fecha_inscripcion")
    private LocalDate fechaInscripcion = LocalDate.now();
}

