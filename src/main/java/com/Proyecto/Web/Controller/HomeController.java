package com.Proyecto.Web.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.Proyecto.Web.Service.UsuarioService;

@Controller
public class HomeController {

    @Autowired
    private UsuarioService usuarioService;

    // Página de login
    @GetMapping("/login")
    public String loginForm() {
        return "login"; // login.html
    }

    // Página principal (dashboard)
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("titulo", "Panel Principal");
        return "home"; // ahora carga directamente home.html
    }
}
