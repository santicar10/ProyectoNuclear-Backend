// src/main/java/com/huahuacuna/app/service/BitacoraService.java
package com.huahuacuna.app.service;

import com.huahuacuna.app.DTO.BitacoraDTO;
import com.huahuacuna.app.DTO.BitacoraResponseDTO;
import com.huahuacuna.app.model.Bitacora;
import com.huahuacuna.app.model.Nino;
import com.huahuacuna.app.repository.BitacoraRepository;
import com.huahuacuna.app.repository.NinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BitacoraService {

    @Autowired
    private BitacoraRepository bitacoraRepository;

    @Autowired
    private NinoRepository ninoRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional
    public BitacoraResponseDTO crearEntrada(Integer ninoId, BitacoraDTO dto) {
        Nino nino = ninoRepository.findById(ninoId)
                .orElseThrow(() -> new RuntimeException("Niño no encontrado"));

        Bitacora bitacora = new Bitacora();
        bitacora.setNino(nino);
        bitacora.setDescripcion(dto.getDescripcion());
        bitacora.setFotoUrl(dto.getImagen());
        bitacora.setFechaRegistro(LocalDate.now());

        Bitacora guardada = bitacoraRepository.save(bitacora);
        return convertirADTO(guardada);
    }

    @Transactional
    public BitacoraResponseDTO actualizarEntrada(Integer id, BitacoraDTO dto) {
        Bitacora bitacora = bitacoraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrada de bitácora no encontrada"));

        bitacora.setDescripcion(dto.getDescripcion());
        bitacora.setFotoUrl(dto.getImagen());

        Bitacora actualizada = bitacoraRepository.save(bitacora);
        return convertirADTO(actualizada);
    }

    public List<BitacoraResponseDTO> obtenerPorNino(Integer ninoId) {
        List<Bitacora> bitacoras = bitacoraRepository.findByNinoIdOrderByFechaDesc(ninoId);
        return bitacoras.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public BitacoraResponseDTO obtenerPorId(Integer id) {
        Bitacora bitacora = bitacoraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrada de bitácora no encontrada"));
        return convertirADTO(bitacora);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!bitacoraRepository.existsById(id)) {
            throw new RuntimeException("Entrada de bitácora no encontrada");
        }
        bitacoraRepository.deleteById(id);
    }

    private BitacoraResponseDTO convertirADTO(Bitacora bitacora) {
        BitacoraResponseDTO dto = new BitacoraResponseDTO();
        dto.setId(bitacora.getIdBitacora());
        dto.setNombreNino(bitacora.getNino().getNombre());
        dto.setFecha(bitacora.getFechaRegistro().format(FORMATTER));
        dto.setDescripcion(bitacora.getDescripcion());
        dto.setImagen(bitacora.getFotoUrl());
        return dto;
    }
}