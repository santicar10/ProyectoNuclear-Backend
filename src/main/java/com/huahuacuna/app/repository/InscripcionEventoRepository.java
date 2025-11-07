package com.huahuacuna.app.repository;

import com.huahuacuna.app.model.InscripcionEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionEventoRepository extends JpaRepository<InscripcionEvento, Long> {
    List<InscripcionEvento> findByEventoId(Long eventoId);
}