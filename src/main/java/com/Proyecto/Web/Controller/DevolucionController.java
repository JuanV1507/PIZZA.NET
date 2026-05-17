package com.Proyecto.Web.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Proyecto.Web.Model.DetalleDevolucion;
import com.Proyecto.Web.Model.Devolucion;
import com.Proyecto.Web.Model.Productos;
import com.Proyecto.Web.Service.DevolucionService;
import com.Proyecto.Web.Service.ProductoService;

@Controller
@RequestMapping("/devoluciones")
public class DevolucionController {

    private final DevolucionService devolucionService;
    private final ProductoService productoService;

    public DevolucionController(DevolucionService devolucionService, ProductoService productoService) {
        this.devolucionService = devolucionService;
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("devoluciones", devolucionService.listarTodas());
        model.addAttribute("productos", productoService.listarTodos());
        return "devoluciones";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Devolucion devolucion,
            @RequestParam(value = "productosIds", required = false) List<Long> productosIds,
            @RequestParam(value = "cantidades", required = false) List<Integer> cantidades,
            RedirectAttributes redirectAttrs) {

        try {
            if (productosIds != null && cantidades != null && productosIds.size() == cantidades.size()) {
                List<DetalleDevolucion> detalles = new ArrayList<>();
                for (int i = 0; i < productosIds.size(); i++) {
                    Long idProducto = productosIds.get(i);
                    Integer cantidad = cantidades.get(i);
                    if (idProducto != null && cantidad != null && cantidad > 0) {
                        DetalleDevolucion detalle = new DetalleDevolucion();
                        Productos producto = productoService.buscarPorId(idProducto).orElse(null);
                        if (producto != null) {
                            detalle.setProducto(producto);
                            detalle.setCantidad(cantidad);
                            detalles.add(detalle);
                        }
                    }
                }
                devolucion.setDetalles(detalles);
            }
            devolucionService.guardar(devolucion);
            redirectAttrs.addFlashAttribute("mensaje", "Devolución registrada exitosamente");
            redirectAttrs.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("mensaje", "Error al registrar la devolución: " + e.getMessage());
            redirectAttrs.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/devoluciones";
    }
}
