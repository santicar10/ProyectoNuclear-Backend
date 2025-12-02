package com.huahuacuna.app.service;

import com.huahuacuna.app.DTO.BitacoraDTO;
import com.huahuacuna.app.model.Apadrinamiento;
import com.huahuacuna.app.model.Bitacora;
import com.huahuacuna.app.repository.ApadrinamientoRepository;
import com.huahuacuna.app.repository.BitacoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BitacoraService {

    @Autowired
    private BitacoraRepository bitacoraRepository;

    @Autowired
    private ApadrinamientoRepository apadrinamientoRepository;

    public Bitacora crearBitacoraPorAdmin(BitacoraDTO dto) {

        Apadrinamiento a = apadrinamientoRepository.findById(dto.getIdApadrinamiento())
                .orElseThrow(() -> new RuntimeException("Apadrinamiento no encontrado"));

        // ⚠ Validar que el apadrinamiento esté ACTIVO
        if (!"ACTIVO".equalsIgnoreCase(a.getEstado().toString())) {
            throw new RuntimeException("No se pueden crear bitácoras para apadrinamientos inactivos");
        }

        Bitacora b = new Bitacora();
        b.setApadrinamiento(a);
        b.setDescripcion(dto.getDescripcion());
        b.setFotoUrl(dto.getFotoUrl());
        b.setVideoUrl(dto.getVideoUrl());

        // 📌 Asignar fecha automáticamente
        b.setFechaRegistro(LocalDate.now());

        return bitacoraRepository.save(b);
    }

    public Bitacora actualizar(Integer id, BitacoraDTO dto) {

        Bitacora b = bitacoraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bitácora no encontrada"));

        b.setDescripcion(dto.getDescripcion());
        b.setFotoUrl(dto.getFotoUrl());
        b.setVideoUrl(dto.getVideoUrl());

        return bitacoraRepository.save(b);
    }

    public void eliminar(Integer id) {
        bitacoraRepository.deleteById(id);
    }

    public List<Bitacora> listarTodas() {
        return bitacoraRepository.findAll();
    }

    public List<Bitacora> listarPorPadrino(Integer idApadrinamiento, Integer idUsuarioPadrino) {

        Apadrinamiento a = apadrinamientoRepository.findById(idApadrinamiento)
                .orElseThrow(() -> new RuntimeException("Apadrinamiento no encontrado"));

        // Validar padrino correct
        if (!a.getPadrino().getIdUsuario().equals(idUsuarioPadrino)) {
            throw new RuntimeException("No autorizado");
        }

        return bitacoraRepository.findByApadrinamientoIdApadrinamiento(idApadrinamiento);
    }

    public String generarTextoBitacora(Bitacora b) {
        StringBuilder sb = new StringBuilder();
        sb.append("BITÁCORA DEL NIÑO").append("\n\n");
        sb.append("Fecha de registro: ").append(b.getFechaRegistro()).append("\n");
        sb.append("Descripción: ").append(b.getDescripcion()).append("\n\n");
        sb.append("Foto URL: ").append(b.getFotoUrl()).append("\n");
        sb.append("Video URL: ").append(b.getVideoUrl()).append("\n");
        sb.append("\nPadrino: ").append(b.getApadrinamiento().getPadrino().getNombre());

        return sb.toString();
    }
}

