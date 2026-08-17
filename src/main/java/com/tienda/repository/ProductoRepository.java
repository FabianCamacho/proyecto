package com.tienda.repository;

import com.tienda.domain.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Productos activos
    public List<Producto> findByActivoTrue();

    // Consulta derivada
    public List<Producto> findByPrecioBetweenOrderByPrecioAsc(
            double precioInf,
            double precioSup
    );

    // Consulta JPQL
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaJPQL(
            double precioInf,
            double precioSup
    );

    // Consulta SQL
    @Query(value = "SELECT * FROM producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC",
           nativeQuery = true)
    public List<Producto> consultaSQL(
            double precioInf,
            double precioSup
    );
}