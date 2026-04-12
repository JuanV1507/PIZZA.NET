package com.Proyecto.Web.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Proyecto.Web.Model.Empleado;
import com.Proyecto.Web.Service.EmpleadoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    // Mostrar tabla + formulario en la misma vista
    @GetMapping
    public String listar(@RequestParam(required = false) Long crearUsuario, Model model) {
        var empleados = empleadoService.listarTodos();

        double totalSalarios = empleados.stream()
                .mapToDouble(e -> e.getSalario_calculado() != null ? e.getSalario_calculado() : 0)
                .sum();

        model.addAttribute("empleados", empleados);
        model.addAttribute("empleado", new Empleado());
        model.addAttribute("totalSalarios", totalSalarios);
         // Agregar roles disponibles para el SweetAlert
        model.addAttribute("roles", new String[] { "Administrador", "Cajera" });

        model.addAttribute("crearUsuario", crearUsuario);
        return "empleados";
    }

 @PostMapping
public String guardar(
        @Valid @ModelAttribute Empleado empleado,
        BindingResult result,
        RedirectAttributes redirectAttributes,
        Model model) {

    // VALIDACIÓN DE FORMATO (nombre, etc)
    if (result.hasErrors()) {
        model.addAttribute("No se permite números ni caracteres especiales en el nombre");
        model.addAttribute("empleados", empleadoService.listarTodos());
        return "empleados";
    }

    // VALIDACIÓN DE TELÉFONO
    if (empleadoService.telefonoDuplicado(empleado)) {
        model.addAttribute("error", "El teléfono ya está registrado");
        model.addAttribute("empleados", empleadoService.listarTodos());
        model.addAttribute("empleado", empleado);
        return "empleados";
    }
    // GUARDAR
    Empleado empleadoGuardado = empleadoService.guardar(empleado);

    redirectAttributes.addFlashAttribute("success", "Empleado guardado correctamente");
    return "redirect:/empleados?crearUsuario=" + empleadoGuardado.getId_empleado();
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
