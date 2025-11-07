package com.huahuacuna.app.repository;

import com.huahuacuna.app.model.Nino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Nino.
 */
@Repository
public interface NinoRepository extends JpaRepository<Nino, Integer> {
}

