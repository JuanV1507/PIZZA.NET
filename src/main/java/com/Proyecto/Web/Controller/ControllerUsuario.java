package com.Proyecto.Web.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Proyecto.Web.Model.Usuario;
import com.Proyecto.Web.Repository.UsuarioRepository;

@Controller
@RequestMapping("/usuarios")
public class ControllerUsuario {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ==========================
    //  FORMULARIO PARA CREAR USUARIO
    // ==========================
    @GetMapping("/crear/{idEmpleado}")
    public String mostrarFormularioUsuario(@PathVariable Long idEmpleado, Model model) {

        Usuario usuario = new Usuario();
        usuario.setId_empleado(idEmpleado);

        // Lista de roles visibles para seleccionar
        model.addAttribute("roles", new String[] { "Administrador", "Cajera" });

        model.addAttribute("usuario", usuario);

        // AHORA ya no regresamos layout
        return "usuarios/formulario"; 
    }


    // ==========================
    //   GUARDAR USUARIO EN BD
    // ==========================
  @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttrs) {

    // 1. Validar si el username ya existe
    if (usuarioRepository.existsByUsername(usuario.getUsername())) {
        redirectAttrs.addFlashAttribute("error", "El nombre de usuario ya existe, elige otro.");
        return "redirect:/empleados"; 
    }

    // 2. Encriptar contraseña
    usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

    // 3. Convertir rol elegido a rol interno (necesario para Spring Security)
    switch (usuario.getRol()) {
        case "Administrador":
            usuario.setRol("ROLE_ADMIN");
            break;
        case "Cajera":
            usuario.setRol("ROLE_CAJERA");
            break;
        default:
            usuario.setRol("ROLE_USER");
            break;
    }

    // 4. Guardar usuario
    usuarioRepository.save(usuario);

    // 5. Mensaje para SweetAlert
    redirectAttrs.addFlashAttribute("success", "Usuario creado correctamente.");

    // 6. Redirigir
    return "redirect:/empleados";
}

}
