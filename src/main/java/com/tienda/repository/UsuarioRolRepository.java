package com.tienda.repository;

import com.tienda.domain.Usuario;
import com.tienda.domain.UsuarioRol;
import com.tienda.domain.UsuarioRolId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {

    List<UsuarioRol> findByUsuario(Usuario usuario);

}