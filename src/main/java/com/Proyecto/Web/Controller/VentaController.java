package com.Proyecto.Web.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Proyecto.Web.Model.Productos;
import com.Proyecto.Web.Repository.ProductoRepository;
import com.Proyecto.Web.Service.ProductoService;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private ProductoService ProductoService;

    @Autowired      
    private ProductoRepository ProductoRepository;

    public VentaController(ProductoService productoService,
                            ProductoRepository productoRepository) {
        this.ProductoService = productoService;
        this.ProductoRepository = productoRepository;
    }

   @GetMapping
public String ventas(
        @RequestParam(required = false) Productos.Categoria categoria,
        @RequestParam(required = false) Boolean activo,
        Model model) {

    List<Productos> productos;

    if (categoria != null && activo != null) {
        productos = ProductoRepository.findByCategoriaAndActivo(categoria, activo);
    } 
    else if (categoria != null) {
        productos = ProductoRepository.findByCategoria(categoria);
    } 
    else if (activo != null) {
        productos = ProductoRepository.findByActivo(activo);
    } 
    else {
        productos = ProductoRepository.findByActivo(true);
    }

    model.addAttribute("productos", productos);
    model.addAttribute("categorias", Productos.Categoria.values());
    model.addAttribute("categoriaSeleccionada", categoria);
    model.addAttribute("activoSeleccionado", activo);

    return "ventas";
}

}