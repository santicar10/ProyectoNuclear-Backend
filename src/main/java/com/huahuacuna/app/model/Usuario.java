package com.huahuacuna.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_usuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    private String telefono;
    private String direccion;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    private LocalDate fecha_creacion;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    public enum Rol {
        administrador, voluntario, padrino
    }

    public enum Estado {
        activo, inactivo
    }
}

