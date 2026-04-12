package com.Proyecto.Web.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.Proyecto.Web.Model.Productos;
import com.Proyecto.Web.Model.Usuario;
import com.Proyecto.Web.Service.EmpleadoService;
import com.Proyecto.Web.Service.ProductoService;
import com.Proyecto.Web.Service.UsuarioService;

@Controller
public class HomeController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private ProductoService productoService;

    // Página de login
    @GetMapping("/login")
    public String loginForm() {
        return "login"; // login.html
    }

    // Página principal (dashboard)
    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioService.findByUsername(userDetails.getUsername());
        //obtener los productos que son paquetes para mostrar
        List<Productos> paquetes = productoService.filtrar("Paquete", null);
        model.addAttribute("totalEmpleados", 0);
        model.addAttribute("totalProductos",  0);
        model.addAttribute("paquetes", paquetes);
        // Agrega el usuario al modelo para mostrar su información en la vista
        model.addAttribute("titulo", "Panel Principal");
        model.addAttribute("usuario", usuario);
        return "home";
    }

    @GetMapping("/acceso")
    public String acceso() {
    return "acceso"; // acceso.html
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        model.addAttribute("titulo", "Reportes de sistema");
        return "reportes"; // reportes.html
    }
}
