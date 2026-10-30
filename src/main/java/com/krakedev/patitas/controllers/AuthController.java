package com.krakedev.patitas.controllers;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.patitas.entidades.Usuario;
import com.krakedev.patitas.servicios.UsuarioService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final UsuarioService usuarioService;
	
	public AuthController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
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
			return ResponseEntity.ok(Map.of("mensaje", "Login exitoso (Modo vulnerable)"));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
		}
	}
}