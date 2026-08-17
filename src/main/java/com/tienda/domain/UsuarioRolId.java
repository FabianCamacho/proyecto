package com.tienda.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;

@Data
@Embeddable
public class UsuarioRolId implements Serializable {

    private Long idUsuario;

    private Integer idRol;
}