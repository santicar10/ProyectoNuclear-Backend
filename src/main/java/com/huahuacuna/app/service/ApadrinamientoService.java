package com.huahuacuna.app.service;


import com.huahuacuna.app.model.Apadrinamiento;
import com.huahuacuna.app.repository.ApadrinamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApadrinamientoService {

    @Autowired
    private ApadrinamientoRepository repo;

    public Apadrinamiento guardar(Apadrinamiento ap) {
        return repo.save(ap);
    }

    public List<Apadrinamiento> buscarPorPadrino(Integer idPadrino) {
        return repo.findByPadrino_IdUsuario(idPadrino);
    }

    public List<Apadrinamiento> listarTodos() {
        return repo.findAll();
    }
}
