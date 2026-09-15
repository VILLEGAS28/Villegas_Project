package com.softwarelogistic.controlador;

import com.softwarelogistic.dto.CambiarPasswordRequest;
import com.softwarelogistic.dto.UsuarioRequest;
import com.softwarelogistic.modelo.Rol;
import com.softwarelogistic.modelo.Usuario;
import com.softwarelogistic.repository.RolRepository;
import com.softwarelogistic.servicio.UsuarioService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * ====================================================================
 * CONTROLADOR REST: USUARIOS Y CUENTAS
 * ====================================================================
 * Administra el personal con acceso al sistema y sus contraseñas encriptadas.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * GET /api/usuarios
     * Lista todos los usuarios registrados.
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    /**
     * GET /api/usuarios/{id}
     * Busca un usuario por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        return usuario.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST /api/usuarios
     * Registra un nuevo usuario con contraseña cifrada en BCrypt.
     */
    @PostMapping
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioRequest request) {

        // 1. Validar correo no duplicado
        if (usuarioService.existeCorreo(request.getCorreo().trim())) {
            return ResponseEntity.badRequest().body("Ya existe un usuario con ese correo.");
        }

        // 2. Validar que el rol asignado exista
        Optional<Rol> rol = rolRepository.findById(request.getIdRol());
        if (rol.isEmpty()) {
            return ResponseEntity.badRequest().body("El rol seleccionado no existe.");
        }

        // 3. Crear entidad
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre().trim());
        usuario.setApellido(request.getApellido().trim());
        usuario.setCorreo(request.getCorreo().trim());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol.get());
        usuario.setEstado(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setFechaActualizacion(LocalDateTime.now());

        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    /**
     * PUT /api/usuarios/{id}
     * Actualiza datos de usuario respetando la contraseña existente si no se envía una nueva.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Integer id, @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioService.buscarPorId(id);

        if (usuarioExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        // Validar si el correo pertenece a otro usuario
        if (usuario.getCorreo() != null) {
            Optional<Usuario> userCorreo = usuarioService.buscarPorCorreo(usuario.getCorreo().trim());
            if (userCorreo.isPresent() && !userCorreo.get().getIdUsuario().equals(id)) {
                return ResponseEntity.badRequest().body("El correo ya pertenece a otro usuario.");
            }
        }

        Usuario actual = usuarioExistente.get();
        actual.setNombre(usuario.getNombre());
        actual.setApellido(usuario.getApellido());
        actual.setCorreo(usuario.getCorreo());
        actual.setEstado(usuario.getEstado());

        if (usuario.getRol() != null && usuario.getRol().getIdRol() != null) {
            rolRepository.findById(usuario.getRol().getIdRol()).ifPresent(actual::setRol);
        }

        // Solo actualizar contraseña si el usuario envió una nueva
        if (usuario.getPasswordHash() != null && !usuario.getPasswordHash().trim().isEmpty()) {
            actual.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        }

        actual.setFechaActualizacion(LocalDateTime.now());
        Usuario actualizado = usuarioService.guardarUsuario(actual);

        return ResponseEntity.ok(actualizado);
    }

    /**
     * PUT /api/usuarios/cambiar-password
     * Permite a un usuario cambiar su contraseña validando la anterior.
     */
    @PutMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody CambiarPasswordRequest request) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(request.getIdUsuario());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La contraseña actual es incorrecta.");
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.getNuevaPassword()));
        usuario.setFechaActualizacion(LocalDateTime.now());
        usuarioService.guardarUsuario(usuario);

        return ResponseEntity.ok("Contraseña cambiada exitosamente.");
    }

    /**
     * DELETE /api/usuarios/{id}
     * Elimina un usuario.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok("Usuario eliminado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar el usuario porque tiene movimientos o registros en auditoría.");
        }
    }
}