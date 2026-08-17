package com.tienda.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class DetalleVenta implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idProducto;
    private String descripcion;
    private BigDecimal precio;
    private Integer cantidad;
    private BigDecimal subtotal;
}
