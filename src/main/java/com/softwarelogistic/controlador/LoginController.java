package com.softwarelogistic.controlador;

import com.softwarelogistic.dto.LoginRequest;
import com.softwarelogistic.modelo.Usuario;
import com.softwarelogistic.servicio.UsuarioService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * ====================================================================
 * CONTROLADOR REST: AUTENTICACIÓN / INICIO DE SESIÓN
 * ====================================================================
 * Valida correo y contraseña encriptada con BCrypt para permitir el acceso.
 */
@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * POST /api/login
     * Recibe credenciales y devuelve los datos del usuario si son correctas.
     */
    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // 1. Validar que vengan correo y contraseña
        if (request.getCorreo() == null || request.getCorreo().trim().isEmpty() ||
            request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Debe ingresar correo y contraseña.");
        }

        // 2. Buscar si el usuario existe por correo
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorCorreo(request.getCorreo().trim());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo o contraseña incorrectos.");
        }

        Usuario usuario = usuarioOpt.get();

        // 3. Validar si el usuario está activo
        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El usuario se encuentra inactivo.");
        }

        // 4. Comparar contraseña en texto plano contra el hash BCrypt de la BD
        boolean passwordCorrecta = passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash());

        if (!passwordCorrecta) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo o contraseña incorrectos.");
        }

        // 5. Retornar sesión exitosa con el rol del usuario
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Inicio de sesión exitoso.");
        respuesta.put("idUsuario", usuario.getIdUsuario());
        respuesta.put("nombre", usuario.getNombre());
        respuesta.put("apellido", usuario.getApellido());
        respuesta.put("correo", usuario.getCorreo());
        respuesta.put("rol", usuario.getRol());

        return ResponseEntity.ok(respuesta);
    }
}