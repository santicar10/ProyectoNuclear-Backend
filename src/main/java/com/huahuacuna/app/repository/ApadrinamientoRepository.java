package com.huahuacuna.app.repository;


import com.huahuacuna.app.model.Apadrinamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApadrinamientoRepository extends JpaRepository<Apadrinamiento, Integer> {

    List<Apadrinamiento> findByPadrino_IdUsuarioAndEstado(Integer idPadrino, Apadrinamiento.EstadoApadrinamiento estado);

    List<Apadrinamiento> findByNino_IdNino(Integer idNino);

    List<Apadrinamiento> findByPadrino_IdUsuario(Integer idPadrino);
}

