package com.huahuacuna.app.service;


import com.huahuacuna.app.DTO.RegistroVoluntariadoDTO;
import com.huahuacuna.app.model.Proyecto;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.model.Voluntariado;
import com.huahuacuna.app.repository.ProyectoRepository;
import com.huahuacuna.app.repository.UsuarioRepository;
import com.huahuacuna.app.repository.VoluntariadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class VoluntariadoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private VoluntariadoRepository voluntariadoRepository;

    public Voluntariado registrarVoluntario(RegistroVoluntariadoDTO dto) {

        // 1️⃣ Verificar si el usuario ya existe
        Usuario usuario = usuarioRepository.findByCorreo(dto.getEmail()).orElse(null);

        if (usuario == null) {
            usuario = new Usuario();
            usuario.setNombre(dto.getNombre());
            usuario.setCorreo(dto.getEmail());
            usuario.setContrasena(dto.getPassword());
            usuario.setRol(Usuario.Rol.voluntario);
            usuarioRepository.save(usuario);
        }

        // 2️⃣ Buscar el proyecto
        Proyecto proyecto = proyectoRepository.findById(dto.getIdProyecto())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // 3️⃣ Crear el registro en voluntariado
        Voluntariado voluntariado = new Voluntariado();
        voluntariado.setUsuario(usuario);
        voluntariado.setProyecto(proyecto);
        voluntariado.setRolVoluntario(dto.getRolVoluntario() != null ? dto.getRolVoluntario() : "Voluntario");
        voluntariado.setFechaInscripcion(LocalDate.now());

        return voluntariadoRepository.save(voluntariado);
    }
}

