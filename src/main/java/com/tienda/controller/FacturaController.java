package com.tienda.controller;

import com.tienda.domain.DetalleVenta;
import com.tienda.domain.Factura;
import com.tienda.domain.Producto;
import com.tienda.domain.Usuario;
import com.tienda.service.FacturaService;
import com.tienda.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.tienda.service.UsuarioService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/factura")
public class FacturaController {

    private final FacturaService facturaService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public FacturaController(FacturaService facturaService,
            ProductoService productoService,
            UsuarioService usuarioService) {

        this.facturaService = facturaService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {

        List<Producto> productos = productoService.getProductos(true);

        model.addAttribute("productos", productos);

        return "/factura/listado";
    }

    @PostMapping("/agregar")
    public String agregar(
            @RequestParam Integer idProducto,
            @RequestParam Integer cantidad,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Producto producto = productoService.getProducto(idProducto).orElse(null);

        if (producto == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "El producto no existe."
            );

            return "redirect:/factura/listado";
        }

        if (cantidad == null || cantidad <= 0) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "La cantidad debe ser mayor que cero."
            );

            return "redirect:/factura/listado";
        }

        if (cantidad > producto.getExistencias()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "No hay suficientes existencias."
            );

            return "redirect:/factura/listado";
        }

        List<DetalleVenta> carrito
                = (List<DetalleVenta>) session.getAttribute("carrito");

        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        boolean encontrado = false;

        for (DetalleVenta detalle : carrito) {

            if (detalle.getIdProducto().equals(idProducto)) {

                int nuevaCantidad
                        = detalle.getCantidad() + cantidad;

                if (nuevaCantidad > producto.getExistencias()) {
                    redirectAttributes.addFlashAttribute(
                            "error",
                            "La cantidad supera las existencias disponibles."
                    );

                    return "redirect:/factura/listado";
                }

                detalle.setCantidad(nuevaCantidad);

                detalle.setSubtotal(
                        producto.getPrecio()
                                .multiply(BigDecimal.valueOf(nuevaCantidad))
                );

                encontrado = true;
                break;
            }
        }

        if (!encontrado) {

            DetalleVenta detalle = new DetalleVenta();

            detalle.setIdProducto(producto.getIdProducto());
            detalle.setDescripcion(producto.getDescripcion());
            detalle.setPrecio(producto.getPrecio());
            detalle.setCantidad(cantidad);

            detalle.setSubtotal(
                    producto.getPrecio()
                            .multiply(BigDecimal.valueOf(cantidad))
            );

            carrito.add(detalle);
        }

        session.setAttribute("carrito", carrito);

        redirectAttributes.addFlashAttribute(
                "todoOk",
                "Producto agregado al carrito."
        );

        return "redirect:/factura/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(
            @RequestParam Integer idProducto,
            HttpSession session) {

        List<DetalleVenta> carrito
                = (List<DetalleVenta>) session.getAttribute("carrito");

        if (carrito != null) {

            carrito.removeIf(
                    detalle -> detalle.getIdProducto().equals(idProducto)
            );

            session.setAttribute("carrito", carrito);
        }

        return "redirect:/factura/listado";
    }

    @PostMapping("/pagar")
    public String pagar(
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        if (username == null || username.equals("anonymousUser")) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Debe iniciar sesión para realizar una compra."
            );

            return "redirect:/sesion/listado";
        }

        Usuario usuario = usuarioService.getUsuarioPorUsername(username);

        if (usuario == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se encontró el usuario."
            );

            return "redirect:/sesion/listado";
        }

        List<DetalleVenta> carrito
                = (List<DetalleVenta>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El carrito está vacío."
            );

            return "redirect:/factura/listado";
        }

        try {

            Factura factura
                    = facturaService.guardarFactura(usuario, carrito);

            session.removeAttribute("carrito");

            return "redirect:/factura/ver/" + factura.getIdFactura();

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );

            return "redirect:/factura/listado";
        }
    }

    @GetMapping("/ver/{idFactura}")
    public String verFactura(
            @org.springframework.web.bind.annotation.PathVariable Integer idFactura,
            Model model) {

        Factura factura
                = facturaService.getFactura(idFactura);

        if (factura == null) {
            return "redirect:/factura/listado";
        }

        List<com.tienda.domain.Venta> ventas
                = facturaService.getVentasPorFactura(factura);

        model.addAttribute("factura", factura);
        model.addAttribute("ventas", ventas);

        return "/factura/ver";
    }
}
