package com.krakedev.patitas.servicios;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.krakedev.patitas.entidades.Usuario;
import com.krakedev.patitas.repositorios.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Guardar usuario (Vulnerable)
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Autenticación directa (Vulnerable)
    public boolean autenticar(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Comparación directa (Vulnerable)
            return usuario.getPassword().equals(password);
        }
        
        return false;
    }
}