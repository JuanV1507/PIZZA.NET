package com.Proyecto.Web.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
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

  
    //  FORMULARIO PARA CREAR USUARIO

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


  
    //   GUARDAR USUARIO EN BD
  
 @PostMapping("/guardar")
public String guardarUsuario(
        @ModelAttribute Usuario usuario,
        @RequestParam("archivoFoto") MultipartFile archivoFoto,
        RedirectAttributes redirectAttrs) {

    if (usuarioRepository.existsByUsername(usuario.getUsername())) {
        redirectAttrs.addFlashAttribute("error", "El nombre de usuario ya existe.");
        return "redirect:/empleados";
    }
    if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
        redirectAttrs.addFlashAttribute("error", "El correo electrónico ya existe.");
        return "redirect:/empleados";
    }
    if (usuarioRepository.existsByUsername(usuario.getUsername())) {
       redirectAttrs.addFlashAttribute("error", "El usuario ya existe");
   return "redirect:/empleados";
}
    // Validar que el correo no esté vacío
    if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) {
        redirectAttrs.addFlashAttribute("error", "El correo electrónico es obligatorio");
        return "redirect:/usuarios/nuevo";
    }
    
    // Validar si el correo ya existe
    if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
        redirectAttrs.addFlashAttribute("error", "El correo " + usuario.getCorreo() + " ya está registrado");
        return "redirect:/usuarios/nuevo";
    }
    usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

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

    // Manejo de imagen
    if (archivoFoto != null && !archivoFoto.isEmpty()) {

        try {
            String nombreArchivo = System.currentTimeMillis() + "_" + archivoFoto.getOriginalFilename();

            Path ruta = Paths.get("C:/imagenes_usuarios/");
            if (!Files.exists(ruta)) {
                Files.createDirectories(ruta);
            }

            Files.copy(archivoFoto.getInputStream(),
                       ruta.resolve(nombreArchivo),
                       StandardCopyOption.REPLACE_EXISTING);

            usuario.setFoto(nombreArchivo);

        } catch (Exception e) {
            e.printStackTrace();
        }

    } else {
        usuario.setFoto("user-default.png");
    }

    usuarioRepository.save(usuario);

    redirectAttrs.addFlashAttribute("success", "Usuario creado correctamente.");

    return "redirect:/empleados";
}

@GetMapping("/verificar-correo")
@ResponseBody
public boolean verificarCorreo(@RequestParam String correo) {
    return usuarioRepository.existsByCorreo(correo);
}

}