package com.Proyecto.Web.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Proyecto.Web.Model.Devolucion;
import com.Proyecto.Web.Model.Venta;
import com.Proyecto.Web.Service.DevolucionService;

@Controller
@RequestMapping("/devoluciones")
public class DevolucionController {

    private final DevolucionService devolucionService;

    public DevolucionController(DevolucionService devolucionService) {
        this.devolucionService = devolucionService;
    }

    /**
     * Página principal del módulo de devoluciones.
     * Lista las devoluciones registradas.
     */
    @GetMapping
    public String listar(Model model) {
        List<Devolucion> devoluciones = devolucionService.listarTodas();
        model.addAttribute("devoluciones", devoluciones);
        model.addAttribute("devolucion", new Devolucion());
        return "devoluciones";
    }

    /**
     * Endpoint AJAX: busca una venta por folio y devuelve sus datos en JSON
     * para pre-cargar el formulario de devolución en el frontend.
     */
    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> buscarPorFolio(@RequestParam("folio") String folio) {
        Map<String, Object> response = new HashMap<>();
        try {
            Venta venta = devolucionService.buscarVentaPorFolio(folio.trim().toUpperCase());
            if (venta == null) {
                response.put("encontrado", false);
                response.put("mensaje", "No se encontró ninguna venta con el folio: " + folio);
                return ResponseEntity.ok(response);
            }
            if (venta.getEstado() == Venta.estado.CANCELADA) {
                response.put("encontrado", false);
                response.put("mensaje", "La venta " + folio + " ya fue cancelada anteriormente y no puede devolverse.");
                return ResponseEntity.ok(response);
            }

            response.put("encontrado", true);
            response.put("folio", venta.getFolioVenta());
            response.put("total", venta.getTotal());
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            response.put("fecha", venta.getFecha() != null ? venta.getFecha().format(formatter) : "");
            response.put("tipoServicio", venta.getTipo_servicio() != null ? venta.getTipo_servicio().name() : "COMEDOR");
            response.put("estado", venta.getEstado() != null ? venta.getEstado().name() : "ACTIVA");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("encontrado", false);
            response.put("mensaje", "Error al buscar el folio: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Registra la devolución y marca la venta original como CANCELADA.
     */
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Devolucion devolucion,
            @RequestParam(value = "folioVenta", required = false) String folioVenta,
            RedirectAttributes redirectAttrs) {

        try {
            if (folioVenta == null || folioVenta.trim().isEmpty()) {
                redirectAttrs.addFlashAttribute("mensaje", "Debes indicar el folio de la venta a devolver.");
                redirectAttrs.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/devoluciones";
            }
            if (devolucion.getMotivo() == null || devolucion.getMotivo().trim().length() < 10) {
                redirectAttrs.addFlashAttribute("mensaje", "El motivo de la devolución debe tener al menos 10 caracteres.");
                redirectAttrs.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/devoluciones";
            }

            devolucionService.procesarDevolucion(devolucion, folioVenta.trim().toUpperCase());

            redirectAttrs.addFlashAttribute("mensaje", "Devolución registrada y venta " + folioVenta.toUpperCase() + " cancelada exitosamente.");
            redirectAttrs.addFlashAttribute("tipoMensaje", "success");
        } catch (IllegalStateException e) {
            redirectAttrs.addFlashAttribute("mensaje", e.getMessage());
            redirectAttrs.addFlashAttribute("tipoMensaje", "warning");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("mensaje", "Error al registrar la devolución: " + e.getMessage());
            redirectAttrs.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/devoluciones";
    }
}
