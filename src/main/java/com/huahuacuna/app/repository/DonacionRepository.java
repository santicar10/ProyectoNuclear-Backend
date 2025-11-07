package com.huahuacuna.app.repository;

import com.huahuacuna.app.model.Donacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Long> {
    List<Donacion> findByEstado(Donacion.EstadoDonacion estado);
    List<Donacion> findByCorreoElectronico(String correoElectronico);
}