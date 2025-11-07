package com.huahuacuna.app.service;

import com.huahuacuna.app.DTO.DonacionDTO;
import com.huahuacuna.app.model.Donacion;
import com.huahuacuna.app.repository.DonacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DonacionService {

    @Autowired
    private DonacionRepository donacionRepository;

    @Transactional
    public Donacion crearDonacion(DonacionDTO dto) {
        Donacion donacion = new Donacion();

        // ⭐ Asignar usuario solo si viene en el DTO
        donacion.setIdUsuario(dto.getIdUsuario() != null ? dto.getIdUsuario() : 1L);

        // ⭐ Inferir tipo automáticamente si no viene
        if (dto.getTipo() != null) {
            donacion.setTipo(dto.getTipo());
        } else {
            // Si hay monto, es MONETARIA; si hay tipoDotacion, es MATERIAL
            if (dto.getMonto() != null && dto.getMonto().compareTo(java.math.BigDecimal.ZERO) > 0) {
                donacion.setTipo(Donacion.TipoDonacion.MONETARIA);
            } else if (dto.getTipoDotacion() != null && !dto.getTipoDotacion().isEmpty()) {
                donacion.setTipo(Donacion.TipoDonacion.MATERIAL);
            } else {
                throw new IllegalArgumentException("Debe especificar un monto (MONETARIA) o tipo de dotación (MATERIAL)");
            }
        }

        donacion.setMonto(dto.getMonto());
        donacion.setDescripcion(dto.getDescripcion());
        donacion.setBanco(dto.getBanco());
        donacion.setCorreoElectronico(dto.getCorreoElectronico());
        donacion.setNit(dto.getNit());
        donacion.setTipoDotacion(dto.getTipoDotacion());

        return donacionRepository.save(donacion);
    }

    public List<Donacion> listarTodas() {
        return donacionRepository.findAll();
    }

    public List<Donacion> listarPorEstado(Donacion.EstadoDonacion estado) {
        return donacionRepository.findByEstado(estado);
    }

    public List<Donacion> listarPorCorreo(String correo) {
        return donacionRepository.findByCorreoElectronico(correo);
    }

    public Donacion obtenerPorId(Long id) {
        return donacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donación no encontrada"));
    }

    @Transactional
    public Donacion actualizarEstado(Long id, Donacion.EstadoDonacion nuevoEstado) {
        Donacion donacion = obtenerPorId(id);
        donacion.setEstado(nuevoEstado);
        return donacionRepository.save(donacion);
    }

    @Transactional
    public void eliminar(Long id) {
        donacionRepository.deleteById(id);
    }
}