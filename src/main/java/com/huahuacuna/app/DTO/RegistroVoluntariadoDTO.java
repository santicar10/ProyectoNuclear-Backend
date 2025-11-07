package com.huahuacuna.app.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroVoluntariadoDTO {

    private String nombre;
    private String email;
    private String password;
    private Integer idProyecto;
    private String rolVoluntario;
}

