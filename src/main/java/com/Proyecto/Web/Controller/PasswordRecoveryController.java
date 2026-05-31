package com.Proyecto.Web.Controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Proyecto.Web.Model.Usuario;
import com.Proyecto.Web.Repository.UsuarioRepository;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PasswordRecoveryController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/api/recuperar-contrasena")
    @ResponseBody
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String email = payload.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El correo electrónico es obligatorio."));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(email.trim());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El correo electrónico no está registrado."));
        }

        Usuario usuario = usuarioOpt.get();
        String token = UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setResetTokenExpiration(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(usuario);

        // Construir enlace dinámico de recuperación
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String resetLink = baseUrl + "/recuperar?token=" + token;

        try {
            enviarCorreoHtml(usuario.getUsername(), email, resetLink);
            return ResponseEntity.ok(Map.of("mensaje", "Correo enviado exitosamente. Revisa tu bandeja de entrada."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("mensaje", "Error al enviar el correo. Intenta nuevamente."));
        }
    }

    private void enviarCorreoHtml(String username, String destinatario, String enlace) throws Exception {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
        
        helper.setTo(destinatario);
        helper.setSubject("PIZZA.NET | Recuperación de Contraseña");
        
        String cuerpoHtml = String.format(
            "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; padding: 30px; border-radius: 10px;\">" +
            "    <div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05);\">" +
            "        <div style=\"background: linear-gradient(135deg, #0f2027, #2c5364, #4e89b9); padding: 30px; text-align: center; color: white;\">" +
            "            <h1 style=\"margin: 0; font-size: 28px; letter-spacing: 1px;\">PIZZA.NET</h1>" +
            "            <p style=\"margin: 5px 0 0 0; font-size: 14px; opacity: 0.9;\">Recuperación de Contraseña</p>" +
            "        </div>" +
            "        <div style=\"padding: 30px; color: #333333; line-height: 1.6;\">" +
            "            <h2 style=\"color: #2c5364; margin-top: 0;\">¡Hola, %s!</h2>" +
            "            <p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en <strong>PIZZA.NET</strong>.</p>" +
            "            <p>Para continuar con el proceso, haz clic en el siguiente botón. Este enlace es válido por 15 minutos.</p>" +
            "            <div style=\"text-align: center; margin: 30px 0;\">" +
            "                <a href=\"%s\" style=\"background: linear-gradient(90deg, #2B9CF7, #2E79D6); color: white; padding: 12px 30px; text-decoration: none; font-weight: bold; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); display: inline-block;\">Restablecer Contraseña</a>" +
            "            </div>" +
            "            <p style=\"font-size: 13px; color: #666666;\">Si el botón no funciona, copia y pega la siguiente dirección en tu navegador:</p>" +
            "            <p style=\"font-size: 12px; color: #4e89b9; word-break: break-all;\"><a href=\"%s\">%s</a></p>" +
            "            <hr style=\"border: 0; border-top: 1px solid #eeeeee; margin: 30px 0;\">" +
            "            <p style=\"font-size: 12px; color: #999999; margin-bottom: 0;\">Si no solicitaste este cambio, puedes ignorar este correo de forma segura. Tu contraseña seguirá siendo la misma.</p>" +
            "        </div>" +
            "        <div style=\"background-color: #f8f9fa; padding: 15px; text-align: center; font-size: 12px; color: #777777; border-top: 1px solid #eeeeee;\">" +
            "            &copy; 2026 PIZZA.NET. Todos los derechos reservados." +
            "        </div>" +
            "    </div>" +
            "</div>",
            username, enlace, enlace, enlace
        );
        
        helper.setText(cuerpoHtml, true);
        mailSender.send(mensaje);
    }

    @GetMapping("/recuperar")
    public String mostrarPaginaRestablecimiento(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttrs) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByResetToken(token);
        if (usuarioOpt.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "El enlace de recuperación es inválido.");
            return "redirect:/login";
        }

        Usuario usuario = usuarioOpt.get();
        if (usuario.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
            redirectAttrs.addFlashAttribute("error", "El enlace de recuperación ha expirado.");
            return "redirect:/login";
        }

        model.addAttribute("token", token);
        return "recuperar"; // recuperar.html
    }

    @PostMapping("/api/restablecer-contrasena")
    public String restablecerContrasena(@RequestParam("token") String token, 
                                        @RequestParam("contrasena") String contrasena, 
                                        @RequestParam("confirmarContrasena") String confirmarContrasena,
                                        RedirectAttributes redirectAttrs) {
        
        if (!contrasena.equals(confirmarContrasena)) {
            redirectAttrs.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/recuperar?token=" + token;
        }

        if (contrasena.trim().length() < 4) {
            redirectAttrs.addFlashAttribute("error", "La contraseña debe tener al menos 4 caracteres.");
            return "redirect:/recuperar?token=" + token;
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByResetToken(token);
        if (usuarioOpt.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Token de recuperación inválido.");
            return "redirect:/login";
        }

        Usuario usuario = usuarioOpt.get();
        if (usuario.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
            redirectAttrs.addFlashAttribute("error", "El enlace de recuperación ha expirado.");
            return "redirect:/login";
        }

        usuario.setContrasena(passwordEncoder.encode(contrasena));
        usuario.setResetToken(null);
        usuario.setResetTokenExpiration(null);
        usuarioRepository.save(usuario);

        redirectAttrs.addFlashAttribute("success", "Contraseña restablecida correctamente. Ya puedes iniciar sesión.");
        return "redirect:/login";
    }
}
