package com.Proyecto.Web.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Proyecto.Web.Model.Productos;
import com.Proyecto.Web.Service.ProductoService;



@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

        
    @GetMapping
        public String listar(Model model) {
            model.addAttribute("productos", productoService.listarTodos());
            return "productos"; // Vista Thymeleaf
        }

    
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
            model.addAttribute("producto", new Productos());
            return "productos_form";
    }
    
    @PostMapping
    public String guardar(@ModelAttribute Productos producto) {
        productoService.guardar(producto);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id).orElse(null));
        return "productos_form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }

    
    
}
