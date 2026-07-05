
package com.tienda.repository;

import com.tienda.domain.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>{
    
    //se crea una consulta derivada
    public List<Producto> findByActivoTrue();

    
    // consulta derivada que recupera los productos de un rango de pecios  ordenada por precio ascendentemente
    public List <Producto> findByPrecioBetweenOrderByPrecioAsc(double precioInf,double precioSup);
    
    // consulta jpql derivada que recupera los productos de un rango de pecios  ordenada por precio ascendentemente
    @Query(value="SELECT p FROM Producto p WHERE p.precio BETWEEN :precionInf  AND :precioSup ORDER BY p.precio ASC")
    public List <Producto> consultaJPQL(double precioInf,double precioSup);

    // consulta SQL derivada que recupera los productos de un rango de pecios  ordenada por precio ascendentemente
    @Query(nativeQuery=true,
            value="SELECT * FROM producto p WHERE p.precio BETWEEN :precionInf  AND :precioSup ORDER BY p.precio ASC")
    public List <Producto> consultaSQL(double precioInf,double precioSup);



}


