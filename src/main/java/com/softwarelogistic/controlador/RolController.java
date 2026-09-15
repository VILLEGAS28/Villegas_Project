package com.softwarelogistic.controlador;

import com.softwarelogistic.modelo.Rol;
import com.softwarelogistic.servicio.RolService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ====================================================================
 * CONTROLADOR REST: ROLES Y PRIVILEGIOS
 * ====================================================================
 * Administra los perfiles de usuario del sistema (ADMIN, SUPERVISOR, OPERADOR).
 */
@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    /**
     * GET /api/roles
     * Retorna todos los roles registrados.
     */
    @GetMapping
    public ResponseEntity<List<Rol>> listarRoles() {
        return ResponseEntity.ok(rolService.listarRoles());
    }

    /**
     * GET /api/roles/{id}
     * Busca un rol por su identificador primario.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Rol> buscarPorId(@PathVariable Integer id) {
        Optional<Rol> rol = rolService.buscarPorId(id);
        return rol.map(ResponseEntity::ok)
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST /api/roles
     * Crea un nuevo rol asegurando nombre único.
     */
    @PostMapping
    public ResponseEntity<?> crearRol(@RequestBody Rol rol) {
        if (rol.getNombre() == null || rol.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre del rol es obligatorio.");
        }

        if (rolService.existeNombre(rol.getNombre().trim())) {
            return ResponseEntity.badRequest().body("Ya existe un rol registrado con ese nombre.");
        }

        Rol nuevoRol = rolService.guardarRol(rol);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRol);
    }

    /**
     * PUT /api/roles/{id}
     * Actualiza el nombre o descripción del rol validando que no colisione con otro.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarRol(@PathVariable Integer id, @RequestBody Rol rol) {
        Optional<Rol> rolExistente = rolService.buscarPorId(id);

        if (rolExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado.");
        }

        if (rol.getNombre() != null && !rol.getNombre().isBlank()) {
            Optional<Rol> rolMismoNombre = rolService.listarRoles().stream()
                    .filter(r -> r.getNombre().equalsIgnoreCase(rol.getNombre().trim()))
                    .filter(r -> !r.getIdRol().equals(id))
                    .findFirst();

            if (rolMismoNombre.isPresent()) {
                return ResponseEntity.badRequest().body("Ya existe otro rol con ese nombre.");
            }
        }

        rol.setIdRol(id);
        Rol actualizado = rolService.actualizarRol(rol);

        if (actualizado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No fue posible actualizar el rol.");
        }

        return ResponseEntity.ok(actualizado);
    }

    /**
     * DELETE /api/roles/{id}
     * Elimina un rol si ningún usuario lo tiene asignado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRol(@PathVariable Integer id) {
        Optional<Rol> rol = rolService.buscarPorId(id);

        if (rol.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado.");
        }

        try {
            rolService.eliminarRol(id);
            return ResponseEntity.ok("Rol eliminado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar el rol porque está asignado a uno o más usuarios.");
        }
    }
}