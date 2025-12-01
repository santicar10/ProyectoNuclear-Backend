package com.huahuacuna.app.repository;

import com.huahuacuna.app.model.Bitacora;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BitacoraRepository extends JpaRepository<Bitacora, Integer> {

    List<Bitacora> findByApadrinamientoIdApadrinamiento(Integer idApadrinamiento);
}

