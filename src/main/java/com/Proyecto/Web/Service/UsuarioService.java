package com.Proyecto.Web.Service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Proyecto.Web.Model.Usuario;
import com.Proyecto.Web.Repository.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Aquí username será el "Número de celular" enviado desde el form
        Usuario usuario = usuarioRepository.findByTelefono(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new org.springframework.security.core.userdetails.User(
            usuario.getTelefono(),
            usuario.getContrasena(), // debe estar encriptada con BCrypt
            List.of(new SimpleGrantedAuthority("USER"))
        );
    }
}
