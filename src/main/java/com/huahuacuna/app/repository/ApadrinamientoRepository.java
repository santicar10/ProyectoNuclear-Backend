package com.huahuacuna.app.repository;

import com.huahuacuna.app.model.Apadrinamiento;
import com.huahuacuna.app.model.Apadrinamiento.EstadoApadrinamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApadrinamientoRepository extends JpaRepository<Apadrinamiento, Integer> {
    
    @Query("SELECT a FROM Apadrinamiento a WHERE a.padrino.id_usuario = :idPadrino")
    List<Apadrinamiento> findByPadrinoId(@Param("idPadrino") Integer idPadrino);
    
    @Query("SELECT a FROM Apadrinamiento a WHERE a.padrino.id_usuario = :idPadrino AND a.estado = :estado")
    List<Apadrinamiento> findByPadrinoIdAndEstado(@Param("idPadrino") Integer idPadrino, @Param("estado") EstadoApadrinamiento estado);
    
    @Query("SELECT a FROM Apadrinamiento a WHERE a.nino.id_nino = :idNino")
    List<Apadrinamiento> findByNinoId(@Param("idNino") Integer idNino);
    
    @Query("SELECT a FROM Apadrinamiento a WHERE a.nino.id_nino = :idNino AND a.estado = :estado")
    Optional<Apadrinamiento> findByNinoIdAndEstado(@Param("idNino") Integer idNino, @Param("estado") EstadoApadrinamiento estado);
    
    @Query("SELECT a FROM Apadrinamiento a WHERE a.padrino.id_usuario = :idPadrino AND a.nino.id_nino = :idNino")
    Optional<Apadrinamiento> findByPadrinoIdAndNinoId(@Param("idPadrino") Integer idPadrino, @Param("idNino") Integer idNino);
    
    @Query("SELECT COUNT(a) FROM Apadrinamiento a WHERE a.padrino.id_usuario = :idPadrino AND a.estado = :estado")
    long countByPadrinoIdAndEstado(@Param("idPadrino") Integer idPadrino, @Param("estado") EstadoApadrinamiento estado);
}