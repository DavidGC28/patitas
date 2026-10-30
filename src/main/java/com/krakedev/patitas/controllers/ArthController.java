package com.krakedev.patitas.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.patitas.JwtUtils;
import com.krakedev.patitas.entidades.Usuario;
import com.krakedev.patitas.repositorios.UsuarioRepository;
import com.krakedev.patitas.servicios.UsuarioService;

@RestController
@RequestMapping("/auth")
public class ArthController {
	
	private final UsuarioService usuarioService;
	private final UsuarioRepository usuarioRepository;
	
	public ArthController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
		super();
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
			
			Usuario usuario = usuarioRepository.findByUsername(username).get();
			String token = JwtUtils.generarToken(usuario.getUsername(), usuario.getRol());
			
			return ResponseEntity.ok(Map.of("token", token));
			
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
		}
	}
	
	@GetMapping("/perfil")
	public ResponseEntity<?> verPerfil(@RequestHeader(value="Authorization", required=false) String authHeader){
		
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acceso Denegado / Ingresa un TOKEN valido");
		}
		
		String token = authHeader.substring(7);
		DecodedJWT datosToken = JwtUtils.validarToken(token);
		
		if (datosToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acceso Denegado: Token Invalido");
		}
		
		String usuario = datosToken.getSubject();
		String rol = datosToken.getClaim("rol").asString();
		
		return ResponseEntity.ok(Map.of(
				"Mensaje", "Bienvenido Al Sistema",
				"Usuario", usuario,
				"Rol", rol,
				"Estatus", "Autenticacion Exitosa"
		));
	}
}