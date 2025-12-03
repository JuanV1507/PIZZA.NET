package com.Proyecto.Web.Controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Proyecto.Web.Service.VentaService;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public String listarVentas(Model model) {
        model.addAttribute("listaVentas", new ArrayList<>());
        return "ventas"; // <-- ventas.html
    }
    
    
    
}
