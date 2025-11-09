package com.Proyecto.Web.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Proyecto.Web.Model.Empleado;
import com.Proyecto.Web.Service.EmpleadoService;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    //  Carga la vista de empleados dentro del layout
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("titulo", "Gestión de Empleados");
        model.addAttribute("empleados", empleadoService.listarTodos());
        model.addAttribute("contenido", "empleados"); // Fragmento que se insertará
        return "layout"; // Carga el layout principal
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("empleado", new Empleado());
        model.addAttribute("titulo", "Nuevo Empleado");
        model.addAttribute("contenido", "empleados_form");
        return "layout";
    }

    @PostMapping
    public String guardar(@ModelAttribute Empleado empleado) {
        empleadoService.guardar(empleado);
        return "redirect:/empleados";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("empleado", empleadoService.buscarPorId(id).orElse(null));
        model.addAttribute("titulo", "Editar Empleado");
        model.addAttribute("contenido", "empleados_form");
        return "layout";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        empleadoService.eliminar(id);
        return "redirect:/empleados";
    }
}
