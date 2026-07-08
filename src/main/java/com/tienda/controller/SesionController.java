package com.tienda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.tienda.domain.Usuario;
import com.tienda.service.UsuarioService;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/sesion")
public class SesionController {

    private final UsuarioService usuarioService;

    public SesionController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {

        return "/sesion/listado";
    }

    @GetMapping("/registro")
    public String registro(Model model) {

        model.addAttribute("usuario", new Usuario());

        return "/sesion/registro";

    }

    @PostMapping("/guardar")
    public String guardar(Usuario usuario, Model model) {

        Usuario existe = usuarioService.getUsuarioPorUsername(usuario.getUsername());

        if (existe != null) {

            model.addAttribute("error", "Ese nombre de usuario ya existe.");

            model.addAttribute("usuario", usuario);

            return "/sesion/registro";

        }

        usuario.setActivo(true);

        usuarioService.save(usuario);

        return "redirect:/sesion/listado";
    }

    @PostMapping("/login")
    public String login(String username,
            String password,
            Model model,
            jakarta.servlet.http.HttpSession session) {

        Usuario usuario = usuarioService.getUsuarioPorUsername(username);

        if (usuario == null) {
            model.addAttribute("error", "El usuario no existe.");
            return "/sesion/listado";
        }

        if (!usuario.getPassword().equals(password)) {
            model.addAttribute("error", "La contraseña es incorrecta.");
            return "/sesion/listado";
        }
        session.setAttribute("usuarioLogueado", usuario);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(jakarta.servlet.http.HttpSession session) {

        session.invalidate();

        return "redirect:/";

    }
}
