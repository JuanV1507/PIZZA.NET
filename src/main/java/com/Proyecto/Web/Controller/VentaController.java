package com.Proyecto.Web.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Proyecto.Web.Model.Venta;
import com.Proyecto.Web.Service.ProductoService;
import com.Proyecto.Web.Service.VentaService;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ProductoService productoService;

    public VentaController(VentaService ventaService, ProductoService productoService) {
        this.ventaService = ventaService;
        this.productoService = productoService;
    }

    @GetMapping
    public String ventas(Model model) {

        // Obtener productos activos
        var productos = productoService.filtrar(null, true);

        model.addAttribute("productos", productos);
        model.addAttribute("venta", new Venta());

        return "ventas"; // Vista donde se muestran las vCards
    }
}

