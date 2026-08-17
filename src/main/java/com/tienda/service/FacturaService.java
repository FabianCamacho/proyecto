package com.tienda.service;

import com.tienda.domain.DetalleVenta;
import com.tienda.domain.EstadoFactura;
import com.tienda.domain.Factura;
import com.tienda.domain.Producto;
import com.tienda.domain.Usuario;
import com.tienda.domain.Venta;
import com.tienda.repository.FacturaRepository;
import com.tienda.repository.ProductoRepository;
import com.tienda.repository.VentaRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public FacturaService(FacturaRepository facturaRepository,
            VentaRepository ventaRepository,
            ProductoRepository productoRepository) {

        this.facturaRepository = facturaRepository;
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Factura guardarFactura(Usuario usuario, List<DetalleVenta> detalles) {

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La factura no puede estar vacía.");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (DetalleVenta detalle : detalles) {

            Producto producto = productoRepository
                    .findById(detalle.getIdProducto())
                    .orElseThrow(()
                            -> new IllegalArgumentException("El producto no existe."));

            if (!producto.isActivo()) {
                throw new IllegalArgumentException(
                        "El producto " + producto.getDescripcion() + " no está activo.");
            }

            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser mayor que cero.");
            }

            if (producto.getExistencias() < detalle.getCantidad()) {
                throw new IllegalArgumentException(
                        "No hay suficientes existencias de "
                        + producto.getDescripcion() + ".");
            }

            BigDecimal subtotal = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()));

            total = total.add(subtotal);
        }

        Factura factura = new Factura();

        factura.setUsuario(usuario);
        factura.setTotal(total);
        factura.setEstado(EstadoFactura.Pagada);

        factura = facturaRepository.save(factura);

        for (DetalleVenta detalle : detalles) {

            Producto producto = productoRepository
                    .findById(detalle.getIdProducto())
                    .orElseThrow(()
                            -> new IllegalArgumentException("El producto no existe."));

            Venta venta = new Venta();

            venta.setFactura(factura);
            venta.setProducto(producto);
            venta.setPrecioHistorico(producto.getPrecio());
            venta.setCantidad(detalle.getCantidad());

            ventaRepository.save(venta);

            producto.setExistencias(
                    producto.getExistencias() - detalle.getCantidad()
            );

            productoRepository.save(producto);
        }

        return factura;
    }

    @Transactional(readOnly = true)
    public Factura getFactura(Integer idFactura) {
        return facturaRepository.findById(idFactura).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Venta> getVentasPorFactura(Factura factura) {
        return ventaRepository.findByFactura(factura);
    }
}
