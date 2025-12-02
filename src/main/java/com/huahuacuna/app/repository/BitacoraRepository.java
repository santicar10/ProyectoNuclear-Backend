// src/main/java/com/huahuacuna/app/repository/BitacoraRepository.java
package com.huahuacuna.app.repository;

import com.huahuacuna.app.model.Bitacora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BitacoraRepository extends JpaRepository<Bitacora, Integer> {

    @Query("SELECT b FROM Bitacora b WHERE b.nino.id_nino = :ninoId ORDER BY b.fechaRegistro DESC")
    List<Bitacora> findByNinoIdOrderByFechaDesc(@Param("ninoId") Integer ninoId);
}