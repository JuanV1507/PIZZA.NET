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

    // Mostrar tabla + formulario en la misma vista
    @GetMapping
    public String listar(Model model) {
        var empleados = empleadoService.listarTodos();

        double totalSalarios = empleados.stream()
                .mapToDouble(e -> e.getSalario_calculado() != null ? e.getSalario_calculado() : 0)
                .sum();

        model.addAttribute("empleados", empleados);
        model.addAttribute("empleado", new Empleado());
        model.addAttribute("totalSalarios", totalSalarios);
         // Agregar roles disponibles para el SweetAlert
        model.addAttribute("roles", new String[] { "Administrador", "Cajera" });

        return "empleados";
    }

    @PostMapping
    public String guardar(@ModelAttribute Empleado empleado) {

        empleadoService.guardar(empleado);

        return "redirect:/empleados?crearUsuario=" + empleado.getId_empleado();
    }

    // Cuando se hace clic en EDITAR en la tabla
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        var empleados = empleadoService.listarTodos();

        double totalSalarios = empleados.stream()
                .mapToDouble(e -> e.getSalario_calculado() != null ? e.getSalario_calculado() : 0)
                .sum();

        model.addAttribute("empleados", empleados);
        model.addAttribute("empleado", empleadoService.buscarPorId(id).orElse(new Empleado()));
        model.addAttribute("totalSalarios", totalSalarios);

        return "empleados";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        empleadoService.eliminar(id);
        return "redirect:/empleados";
    }
}
