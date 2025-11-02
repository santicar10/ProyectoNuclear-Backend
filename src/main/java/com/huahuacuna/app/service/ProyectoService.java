package com.huahuacuna.app.service;

import com.huahuacuna.app.DTO.ProyectoDTO;
import com.huahuacuna.app.model.Proyecto;
import com.huahuacuna.app.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    // Crear nuevo proyecto
    public Proyecto guardar(ProyectoDTO dto) {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombreProyecto(dto.getNombreProyecto());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setFechaFin(dto.getFechaFin());
        proyecto.setEstado(dto.getEstado() != null ? dto.getEstado() : Proyecto.EstadoProyecto.ACTIVO);
        return proyectoRepository.save(proyecto);
    }

    // Actualizar proyecto existente
    public Proyecto actualizar(Integer id, ProyectoDTO dto) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id));

        if (dto.getNombreProyecto() != null) proyecto.setNombreProyecto(dto.getNombreProyecto());
        if (dto.getDescripcion() != null) proyecto.setDescripcion(dto.getDescripcion());
        if (dto.getFechaInicio() != null) proyecto.setFechaInicio(dto.getFechaInicio());
        if (dto.getFechaFin() != null) proyecto.setFechaFin(dto.getFechaFin());
        if (dto.getEstado() != null) proyecto.setEstado(dto.getEstado());

        return proyectoRepository.save(proyecto);
    }

    // Listar todos
    public List<Proyecto> listarTodos() {
        return proyectoRepository.findAll();
    }

    // Buscar por ID
    public Optional<Proyecto> buscarPorId(Integer id) {
        return proyectoRepository.findById(id);
    }

    // Eliminar
    public void eliminar(Integer id) {
        proyectoRepository.deleteById(id);
    }

    // Listar solo proyectos activos
    public List<Proyecto> listarActivos() {
        return proyectoRepository.findByEstado(Proyecto.EstadoProyecto.ACTIVO);
    }
}
