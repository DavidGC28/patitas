package com.krakedev.patitas.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.patitas.entidades.Usuario;
import com.krakedev.patitas.repositorios.UsuarioRepository;
import com.krakedev.patitas.servicios.UsuarioService;
import com.krakedev.patitas.JwtUtils;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final UsuarioService usuarioService;
	private final UsuarioRepository usuarioRepository;
	
	public AuthController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
		this.usuarioService = usuarioService;
		this.usuarioRepository = usuarioRepository;
	}

	@PostMapping("/register")
	public ResponseEntity<?> registrar(@RequestBody Usuario usuario){
		try {
			Usuario usuarioNuevo = usuarioService.guardar(usuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioNuevo);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al registrar el usuario: " + e.getMessage());
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales){
		String username = credenciales.get("username");
		String password = credenciales.get("password");
		
		boolean autenticado = usuarioService.autenticar(username, password);
		
		if (autenticado) {
			Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
			if (usuarioOpt.isPresent()) {
				Usuario usuario = usuarioOpt.get();
				
				String token = JwtUtils.generarToken(usuario.getUsername(), usuario.getRol());
				return ResponseEntity.ok(Map.of("token", token));
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Usuario autenticado pero no encontrado en BD");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
		}
	}
	
	@GetMapping("/perfil")
	public ResponseEntity<?> perfil(@RequestHeader("Authorization") String authHeader){
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no proporcionado o formato inválido");
		}
		
		
		String token = authHeader.substring(7);
		DecodedJWT tokenDecodificado = JwtUtils.validarToken(token);
		
		if (tokenDecodificado == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
		}
		
		
		String username = tokenDecodificado.getSubject();
		String rol = tokenDecodificado.getClaim("rol").asString();
		
		
		if ("ADMIN".equals(rol)) {
			return ResponseEntity.ok("Bienvenido Voluntario (ADMIN): " + username + ". Gestión de rescates y fichas de salud habilitada.");
		} else {
			return ResponseEntity.ok("Bienvenido Adoptante (USER): " + username + ". Visualización de mascotas y datos de contacto habilitada.");
		}
	}
}