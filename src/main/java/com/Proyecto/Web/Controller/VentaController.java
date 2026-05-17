package com.Proyecto.Web.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Proyecto.Web.DTO.VentaRequestDTO;
import com.Proyecto.Web.Model.Productos;
import com.Proyecto.Web.Repository.ProductoRepository;
import com.Proyecto.Web.Service.ProductoService;
import com.Proyecto.Web.Service.VentaService;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private ProductoService ProductoService;

    @Autowired      
    private ProductoRepository ProductoRepository;

    @Autowired
    private VentaService ventaService;

    public VentaController(ProductoService productoService,
                            ProductoRepository productoRepository,
                            VentaService ventaService) {
        this.ProductoService = productoService;
        this.ProductoRepository = productoRepository;
        this.ventaService = ventaService;
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
    
    try {
        model.addAttribute("siguienteFolio", ventaService.generarSiguienteFolio());
    } catch (Exception e) {
        model.addAttribute("siguienteFolio", "A0001");
    }

    return "ventas";
}

@GetMapping("/siguiente-folio")
@ResponseBody
public ResponseEntity<String> getSiguienteFolio() {
    try {
        String siguienteFolio = ventaService.generarSiguienteFolio();
        return ResponseEntity.ok().body("{\"folio\":\"" + siguienteFolio + "\"}");
    } catch (Exception e) {
        return ResponseEntity.ok().body("{\"folio\":\"A0001\"}");
    }
}

@PostMapping("/guardar")
@ResponseBody
public ResponseEntity<?> guardarVentaCompleta(@RequestBody VentaRequestDTO request) {
    try {
        ventaService.procesarVentaCompleta(request);
        return ResponseEntity.ok().body("{\"mensaje\":\"Venta guardada correctamente\"}");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body("{\"error\":\"Error al procesar la venta: " + e.getMessage() + "\"}");
    }
}

}