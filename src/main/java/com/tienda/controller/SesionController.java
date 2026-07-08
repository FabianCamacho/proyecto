package com.tienda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sesion")
public class SesionController {
    
    @GetMapping("/listado")
    public String listado(Model model){
        
        return "/sesion/listado";
    }
    
    @GetMapping("/registro")
    public String registro(){
        return "/sesion/registro";
    }
}
