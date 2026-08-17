package com.tienda.repository;

import com.tienda.domain.Factura;
import com.tienda.domain.Venta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findByFactura(Factura factura);
}
