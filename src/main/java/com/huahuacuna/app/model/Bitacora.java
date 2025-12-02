package com.huahuacuna.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "bitacora")
@Getter
@Setter
public class Bitacora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bitacora")
    private Integer idBitacora;

    @ManyToOne
    @JoinColumn(name = "id_nino", nullable = false)
    private Nino nino;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro = LocalDate.now();

    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "foto_url")
    private String fotoUrl;
    
    @Column(name = "video_url")
    private String videoUrl;
}