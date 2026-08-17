package com.tienda.controller;

import com.tienda.domain.Usuario;
import com.tienda.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sesion")
public class SesionController {

    private final UsuarioService usuarioService;

    public SesionController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Página de inicio de sesión
    @GetMapping("/listado")
    public String listado(Model model) {

        return "/sesion/listado";
    }

    // Página de registro
    @GetMapping("/registro")
    public String registro(Model model) {

        model.addAttribute("usuario", new Usuario());

        return "/sesion/registro";
    }

    // Registrar usuario
    @PostMapping("/guardar")
    public String guardar(Usuario usuario, Model model) {

        // Verificar si ya existe el username
        Usuario existe = usuarioService.getUsuarioPorUsername(
                usuario.getUsername()
        );

        if (existe != null) {

            model.addAttribute(
                    "error",
                    "Ese nombre de usuario ya existe."
            );

            model.addAttribute("usuario", usuario);

            return "/sesion/registro";
        }

        // El usuario queda activo
        usuario.setActivo(true);

        // UsuarioService se encarga de encriptar la contraseña
        // y asignar el rol USER
        usuarioService.save(usuario);

        return "redirect:/sesion/listado";
    }

}