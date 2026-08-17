package com.tienda.repository;

import com.tienda.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    Rol findByRol(String rol);

}