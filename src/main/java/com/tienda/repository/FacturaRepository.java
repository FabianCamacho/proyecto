package com.tienda.repository;

import com.tienda.domain.Factura;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    List<Factura> findByUsuarioIdUsuarioOrderByFechaDesc(Integer idUsuario);
}
