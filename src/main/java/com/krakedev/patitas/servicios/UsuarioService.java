package com.krakedev.patitas.servicios;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.krakedev.patitas.entidades.Usuario;
import com.krakedev.patitas.repositorios.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Inyección de dependencias mediante el constructor
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Método para guardar un usuario
    public Usuario guardar(Usuario usuario) {
    	String contrasenaEncriptada = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
    	usuario.setPassword(contrasenaEncriptada);
    	return usuarioRepository.save(usuario);
    }

    // Método para autenticar buscando por username y validando la contraseña
    public boolean autenticar(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (BCrypt.checkpw(password, usuario.getPassword())) {
                return true;
            }
        }
        
        return false; // Retorna falso si el usuario no existe o la contraseña no coincide
    }
}