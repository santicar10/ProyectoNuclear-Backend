package com.huahuacuna.app.repository;

import com.huahuacuna.app.model.Donacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Long> {
    List<Donacion> findByEstado(Donacion.EstadoDonacion estado);
    List<Donacion> findByCorreoElectronico(String correoElectronico);
    /**
     * Agrupa por donante (id_usuario y correo) y devuelve:
     * id_usuario, correo_electronico, totalDonado, totalDonaciones, ultimaDonacion
     * Ahora soporta filtros opcionales: from, to, tipo (MONETARIA|MATERIAL), tipo_dotacion (ej. 'Alimentos').
     */
    @Query(value = "" +
            "SELECT " +
            "  COALESCE(id_usuario, 0) AS idUsuario, " +
            "  COALESCE(correo_electronico, '') AS correo, " +
            "  COALESCE(SUM(monto), 0) AS totalDonado, " +
            "  COUNT(*) AS totalDonaciones, " +
            "  MAX(fecha_donacion) AS ultimaDonacion " +
            "FROM donaciones d " +
            "WHERE (:from IS NULL OR fecha_donacion >= :from) " +
            "  AND (:to IS NULL OR fecha_donacion <= :to) " +
            "  AND (:tipo IS NULL OR tipo = :tipo) " +
            "  AND (:tipoDotacion IS NULL OR tipo_dotacion = :tipoDotacion) " +
            "GROUP BY COALESCE(id_usuario, 0), COALESCE(correo_electronico, '') " +
            "ORDER BY totalDonado DESC",
            nativeQuery = true)
    List<Object[]> findDonorReport(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("tipo") String tipo,
            @Param("tipoDotacion") String tipoDotacion);
}