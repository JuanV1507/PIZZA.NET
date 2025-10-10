package com.Proyecto.Web;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.Proyecto.Web.Model.Usuario;
import com.Proyecto.Web.Repository.UsuarioRepository;
import com.Proyecto.Web.Service.UsuarioService;


@SpringBootApplication
public class PizzeriaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PizzeriaApplication.class, args);
	}

@Bean
CommandLineRunner init(UsuarioRepository usuarioRepository, UsuarioService usuarioService, BCryptPasswordEncoder passwordEncoder) {
    return args -> {
        if (usuarioRepository.findByTelefono("+521234567890").isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setTelefono("+521234567890");
            usuario.setContrasena(passwordEncoder.encode("123456")); // encriptar
            usuario.setNombre("Admin");
            usuarioRepository.save(usuario);
            System.out.println("Usuario de prueba creado: +521234567890 / 123456");
        }
    };
}
}
