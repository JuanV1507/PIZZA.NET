package com.Proyecto.Web.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.Proyecto.Web.Model.Productos;
import com.Proyecto.Web.Model.Usuario;
import com.Proyecto.Web.Service.EmpleadoService;
import com.Proyecto.Web.Service.ProductoService;
import com.Proyecto.Web.Service.UsuarioService;
import com.Proyecto.Web.Service.VentaService;
import com.Proyecto.Web.DTO.ReporteDTO;
import com.Proyecto.Web.DTO.HistorialVentaDTO;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private VentaService ventaService;

    // Página de login
    @GetMapping("/login")
    public String loginForm() {
        return "login"; // login.html
    }

    // Página principal (dashboard)
    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioService.findByUsername(userDetails.getUsername());
        //obtener los productos que son paquetes para mostrar
        List<Productos> paquetes = productoService.filtrar("Paquete", null);
        
        ReporteDTO reporteHoy = ventaService.generarReporte("hoy");
        List<HistorialVentaDTO> todasVentasHoy = ventaService.obtenerHistorialVentas("hoy");
        List<HistorialVentaDTO> ventasRecientes = todasVentasHoy.stream()
            .limit(5).collect(Collectors.toList());
            
        // Datos simulados para el gráfico de los últimos 7 días terminando en los ingresos de hoy
        List<Double> ventasSemanaData = java.util.List.of(3200.0, 7230.0, 5100.0, 4450.0, 8450.0, 5980.0, reporteHoy.getTotalIngresos());

        model.addAttribute("reporteHoy", reporteHoy);
        model.addAttribute("ventasRecientes", ventasRecientes);
        model.addAttribute("ventasSemanaData", ventasSemanaData);
        model.addAttribute("totalEmpleados", empleadoService.listarTodos().size());
        model.addAttribute("totalProductos", productoService.listarTodos().size());
        model.addAttribute("paquetes", paquetes);
        // Agrega el usuario al modelo para mostrar su información en la vista
        model.addAttribute("titulo", "Panel Principal");
        model.addAttribute("usuario", usuario);
        return "home";
    }

    @GetMapping("/acceso")
    public String acceso() {
    return "acceso"; // acceso.html
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado(org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Acceso Restringido: Tu rol de Cajera no tiene permisos para acceder a esta sección.");
        return "redirect:/home";
    }

    @GetMapping("/reportes")
    public String reportes(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "semana") String periodo, Model model) {
        com.Proyecto.Web.DTO.ReporteDTO reporte = ventaService.generarReporte(periodo);
        java.util.List<com.Proyecto.Web.DTO.HistorialVentaDTO> historial = ventaService.obtenerHistorialVentas(periodo);
        java.util.List<com.Proyecto.Web.Model.Empleado> empleados = empleadoService.listarTodos();
        
        double nominaTotal = empleados.stream()
            .mapToDouble(e -> e.getSalario_calculado() != null ? e.getSalario_calculado() : 0)
            .sum();

        model.addAttribute("titulo", "Reportes de sistema");
        model.addAttribute("periodoSeleccionado", periodo);
        model.addAttribute("reporte", reporte);
        model.addAttribute("historial", historial);
        model.addAttribute("empleados", empleados);
        model.addAttribute("nominaTotal", nominaTotal);
        return "reportes"; // reportes.html
    }
}
