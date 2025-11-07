package com.huahuacuna.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título del evento es obligatorio")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String horario; // Ejemplo: "10:00 AM - 2:00 PM"

    private String lugar; // Ubicación del evento

    private String imagenUrl; // URL de la imagen del evento

    @Column(columnDefinition = "TEXT")
    private String descripcionDetallada; // Descripción larga para "Ver más"

    private LocalDateTime fechaEvento; // Fecha y hora del evento

    private Boolean activo = true; // Para activar/desactivar eventos

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}