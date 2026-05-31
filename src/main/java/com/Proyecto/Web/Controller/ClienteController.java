package com.Proyecto.Web.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Proyecto.Web.Model.Clientes;
import com.Proyecto.Web.Service.ClienteService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @org.springframework.beans.factory.annotation.Autowired
    private org.thymeleaf.TemplateEngine templateEngine;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // ── Vista principal ──
    @GetMapping
    public String listar(
            @RequestParam(required = false) String buscar,
            Model model) {
        var clientes = (buscar != null && !buscar.isBlank())
                ? clienteService.buscarPorNombreOTelefono(buscar)
                : clienteService.listarTodos();

        model.addAttribute("clientes", clientes);
        model.addAttribute("buscar", buscar);
        model.addAttribute("totalClientes", clienteService.listarTodos().size());
        return "clientes";
    }

    // ── Guardar / Actualizar desde formulario HTML ──
    @PostMapping("/guardar")
    public String guardarFormulario(
            Clientes cliente,
            RedirectAttributes redirectAttrs) {
        try {
            clienteService.guardar(cliente);
            redirectAttrs.addFlashAttribute("mensaje", "Cliente guardado correctamente.");
            redirectAttrs.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("mensaje", "Error al guardar: " + e.getMessage());
            redirectAttrs.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/clientes";
    }

    // ── Eliminar ──
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            clienteService.eliminar(id);
            redirectAttrs.addFlashAttribute("mensaje", "Cliente eliminado correctamente.");
            redirectAttrs.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("mensaje", "Error al eliminar: " + e.getMessage());
            redirectAttrs.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/clientes";
    }

    // ── API REST: usada desde ventas.html (buscar por teléfono) ──
    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<?> buscarPorTelefono(@RequestParam String telefono) {
        Clientes cliente = clienteService.findByTelefono(telefono);
        if (cliente == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(cliente);
    }

    // ── API REST: guardar desde ventas.html ──
    @PostMapping("/api/guardar")
    @ResponseBody
    public Clientes guardarApi(@RequestBody Clientes cliente) {
        return clienteService.guardar(cliente);
    }
}
