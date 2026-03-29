package com.Proyecto.Web.Service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Proyecto.Web.Model.Usuario;
import com.Proyecto.Web.Repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getContrasena())
                //.roles(usuario.getRol())
                .authorities(usuario.getRol()) 
                .build();
    }

    public boolean existeUsername(String username) {
    return usuarioRepository.existsByUsername(username);
}

public void registrarUsuario(Usuario usuario) {

    if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
        throw new RuntimeException("El correo ya está registrado");
    }

    usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

    usuarioRepository.save(usuario);
}

}
