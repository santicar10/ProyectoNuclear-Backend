package com.huahuacuna.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inscripcion_evento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombreCompleto;

    @Email(message = "El correo electrónico debe ser válido")
    @NotBlank(message = "El correo electrónico es obligatorio")
    private String email;

    private String telefono;

    @Enumerated(EnumType.STRING)
    private EstadoInscripcion estado = EstadoInscripcion.CONFIRMADO;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaInscripcion;

    public enum EstadoInscripcion {
        CONFIRMADO, CANCELADO
    }
}