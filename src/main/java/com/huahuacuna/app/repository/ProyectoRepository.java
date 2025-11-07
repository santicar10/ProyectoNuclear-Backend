package com.huahuacuna.app.repository;

import com.huahuacuna.app.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {
    List<Proyecto> findByEstado(Proyecto.EstadoProyecto estado);
}

