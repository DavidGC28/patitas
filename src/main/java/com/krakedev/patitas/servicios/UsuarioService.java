package com.krakedev.patitas.servicios;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import com.krakedev.patitas.entidades.Usuario;
import com.krakedev.patitas.repositorios.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Método para guardar un usuario con contraseña encriptada (BCrypt)
    public Usuario guardar(Usuario usuario) {
        String contrasenaEncriptada = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
        usuario.setPassword(contrasenaEncriptada);
        return usuarioRepository.save(usuario);
    }

    // Método para autenticar usando BCrypt
    public boolean autenticar(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
           
            if (BCrypt.checkpw(password, usuario.getPassword())) {
                return true;
            }
        }
        
        return false;
    }
}